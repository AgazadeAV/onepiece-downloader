package su.jut.onepiecedownloader.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.jut.onepiecedownloader.client.EpisodePageClient;
import su.jut.onepiecedownloader.dto.EpisodeLinkDto;
import su.jut.onepiecedownloader.dto.ScanResultDto;
import su.jut.onepiecedownloader.mapper.EpisodeLinkMapper;
import su.jut.onepiecedownloader.model.EpisodeLink;
import su.jut.onepiecedownloader.repository.EpisodeLinkRepository;
import su.jut.onepiecedownloader.service.EpisodeScannerService;
import su.jut.onepiecedownloader.util.AppConstants;

@Slf4j
@Service
@RequiredArgsConstructor
public class EpisodeScannerServiceImpl implements EpisodeScannerService {

    private final EpisodeLinkRepository episodeLinkRepository;
    private final EpisodeLinkMapper episodeLinkMapper;
    private final EpisodePageClient episodePageClient;

    private static final int FIRST_EPISODE = 1;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        checkForNewEpisodeAndSave();
    }

    @Override
    @Transactional
    public ScanResultDto scanAndSaveMissingEpisodes() {
        log.info("📡 Начинаем полное сканирование эпизодов...");

        int episode = FIRST_EPISODE;
        int saved = 0;
        int failed = 0;

        while (true) {
            if (episodeLinkRepository.existsByEpisodeNumber(episode)) {
                episode++;
                continue;
            }

            if (!episodePageClient.pageExists(episode)) {
                log.info("🔚 Эпизод {} не найден на сайте — завершение первичного прохода", episode);
                break;
            }

            if (saveEpisode(episode)) saved++;
            else failed++;

            episode++;
        }

        int lastOnSite = findLastEpisodeOnSite(episode);

        log.info("🔍 Повторная проверка пропущенных эпизодов (1-{})", lastOnSite);

        for (int i = FIRST_EPISODE; i <= lastOnSite; i++) {
            if (!episodeLinkRepository.existsByEpisodeNumber(i)) {
                log.warn("⚠️ Эпизод {} отсутствует в базе — повторная попытка", i);
                if (episodePageClient.pageExists(i)) {
                    if (saveEpisode(i)) saved++;
                    else failed++;
                } else {
                    log.warn("🚫 Эпизод {} отсутствует на сайте — пропускаем", i);
                }
            }
        }

        log.info("✅ Сканирование завершено. Последний найденный эпизод на сайте: {}", lastOnSite);

        return ScanResultDto.builder()
                .message("Сканирование завершено")
                .saved(saved)
                .failed(failed)
                .lastEpisodeOnSite(lastOnSite)
                .build();
    }

    @Override
    public int getTotalEpisodes() {
        return (int) episodeLinkRepository.count();
    }

    @Override
    @Transactional
    public ScanResultDto checkForNewEpisodeAndSave() {
        return episodeLinkRepository.findTopByOrderByEpisodeNumberDesc().map(last -> {
            int next = last.getEpisodeNumber() + 1;
            if (episodePageClient.pageExists(next)) {
                boolean success = saveEpisode(next);
                return ScanResultDto.builder()
                        .message(success ? "Новый эпизод найден и сохранён" : "Ошибка при сохранении нового эпизода")
                        .saved(success ? 1 : 0)
                        .failed(success ? 0 : 1)
                        .lastEpisodeOnSite(next - 1)
                        .build();
            } else {
                return ScanResultDto.builder()
                        .message("Новый эпизод пока не найден")
                        .saved(0)
                        .failed(0)
                        .lastEpisodeOnSite(next)
                        .build();
            }
        }).orElseGet(() -> {
            log.warn("📛 В базе данных нет ни одного эпизода — запускаем полное сканирование");
            return scanAndSaveMissingEpisodes();
        });
    }

    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void checkForNewEpisodeAndSaveScheduled() {
        checkForNewEpisodeAndSave();
    }

    private boolean saveEpisode(int episode) {
        String url = String.format(AppConstants.BASE_EPISODE_URL, episode);

        try {
            var doc = episodePageClient.fetchEpisodePage(episode);
            if (doc == null) {
                log.warn("📄 Не удалось загрузить HTML-документ для эпизода {}", episode);
                return false;
            }

            String title = episodePageClient.extractTitle(doc);
            String qualities = episodePageClient.extractQualities(doc);

            if (title == null || title.isBlank()) {
                log.warn("⚠️ Пустой заголовок для эпизода {} — сохранение отменено", episode);
                return false;
            }

            if (qualities == null) {
                log.warn("⚠️ Не удалось извлечь качество для эпизода {} — сохранение отменено", episode);
                return false;
            }

            EpisodeLinkDto dto = EpisodeLinkDto.builder()
                    .episodeNumber(episode)
                    .episodeTitle(title)
                    .episodeUrl(url)
                    .availableQualities(qualities)
                    .build();

            EpisodeLink entity = episodeLinkMapper.toEntity(dto);
            episodeLinkRepository.save(entity);
            log.info("💾 Эпизод {} успешно сохранён", episode);
            return true;

        } catch (Exception e) {
            log.warn("❗ Ошибка при обработке эпизода {}: {}", episode, e.getMessage());
            return false;
        }
    }

    private int findLastEpisodeOnSite(int episodeNumber) {
        while (episodePageClient.pageExists(episodeNumber)) {
            episodeNumber++;
        }
        return episodeNumber - 1;
    }
}
