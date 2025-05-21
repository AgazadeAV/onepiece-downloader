package su.jut.onepiecedownloader.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.jut.onepiecedownloader.dto.EpisodeLinkDto;
import su.jut.onepiecedownloader.mapper.EpisodeLinkMapper;
import su.jut.onepiecedownloader.model.EpisodeLink;
import su.jut.onepiecedownloader.repository.EpisodeLinkRepository;
import su.jut.onepiecedownloader.service.EpisodeCacheService;
import su.jut.onepiecedownloader.util.AppConstants;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EpisodeCacheServiceImpl implements EpisodeCacheService {

    private final EpisodeLinkRepository repository;
    private final EpisodeLinkMapper episodeLinkMapper;

    private static final int TIMEOUT_MS = 2500;
    private static final int RETRY_COUNT = 3;
    private static final int RETRY_DELAY_MS = 300;

    @Override
    @Transactional
    public void scanAllEpisodes() {
        log.info("📡 Старт сканирования эпизодов...");

        int episode = 1;

        while (episodeExists(episode)) {
            saveIfNotExists(episode);
            episode++;
        }

        log.info("✅ Сканирование завершено. Последний найденный эпизод: {}", episode - 1);
    }


    @Override
    public int getTotalEpisodes() {
        return (int) repository.count();
    }


    @Override
    @Transactional
    @Scheduled(cron = "0 59 23 * * *")
    public void checkForNewEpisode() {
        repository.findTopByOrderByEpisodeNumberDesc().ifPresentOrElse(last -> {
            int next = last.getEpisodeNumber() + 1;
            if (episodeExists(next)) {
                saveIfNotExists(next);
                log.info("🆕 Новый эпизод {} найден и сохранён", next);
            } else {
                log.info("🔁 Новый эпизод {} пока не найден", next);
            }
        }, () -> {
            log.warn("📛 В базе нет ни одного эпизода, запустим полное сканирование");
            scanAllEpisodes();
        });
    }

    private boolean episodeExists(int episode) {
        if (repository.existsByEpisodeNumber(episode)) {
            log.info("📦 Эпизод {} уже есть в базе — пропускаем HTTP-запрос", episode);
            return true;
        }

        String url = String.format(AppConstants.BASE_EPISODE_URL, episode);

        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                var response = Jsoup.connect(url)
                        .timeout(TIMEOUT_MS)
                        .ignoreHttpErrors(true)
                        .execute();

                int statusCode = response.statusCode();
                if (statusCode == 200) {
                    return true;
                } else if (statusCode == 404) {
                    log.info("🔎 Эпизод {} не существует (404)", episode);
                    return false;
                } else {
                    log.warn("⚠️ Эпизод {} вернул статус {}. Попытка {}/{}", episode, statusCode, i + 1, RETRY_COUNT);
                }
            } catch (IOException e) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("timed out")) {
                    log.warn("⏱️ Timeout при проверке эпизода {}: {} (попытка {}/{})", episode, msg, i + 1, RETRY_COUNT);
                } else {
                    log.warn("❗️ IO ошибка при проверке эпизода {}: {} (попытка {}/{})", episode, msg, i + 1, RETRY_COUNT);
                }
            }

            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException ignored) {
            }
        }

        log.warn("⛔ Эпизод {} не подтвердился после {} попыток — считаем, что не существует", episode, RETRY_COUNT);
        return false;
    }

    private void saveIfNotExists(int episode) {
        if (!repository.existsByEpisodeNumber(episode)) {
            String url = String.format(AppConstants.BASE_EPISODE_URL, episode);

            try {
                var doc = Jsoup.connect(url).timeout(TIMEOUT_MS).get();

                String title = extractTitle(doc);
                String qualities = extractAvailableQualities(doc);

                EpisodeLinkDto dto = EpisodeLinkDto.builder()
                        .episodeNumber(episode)
                        .episodeTitle(title)
                        .episodeUrl(url)
                        .availableQualities(qualities)
                        .build();

                EpisodeLink entity = episodeLinkMapper.toEntity(dto);
                repository.save(entity);
                log.info("💾 Сохранён эпизод {}", episode);

            } catch (Exception e) {
                log.warn("❗ Не удалось обработать эпизод {}: {}", episode, e.getMessage());
            }
        }
    }

    private String extractTitle(Document doc) {
        try {
            return Objects.requireNonNull(doc.selectFirst("div.video_plate_title > span > h2")).text();
        } catch (Exception e) {
            log.warn("⚠️ Не удалось извлечь заголовок: {}", e.getMessage());
            return null;
        }
    }

    private String extractAvailableQualities(Document doc) {
        try {
            return doc.select("video source").stream()
                    .map(e -> e.attr("res"))
                    .filter(res -> !res.isBlank())
                    .map(Integer::parseInt)
                    .sorted()
                    .map(res -> res + "p")
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            log.warn("⚠️ Не удалось извлечь качества: {}", e.getMessage());
            return "";
        }
    }
}
