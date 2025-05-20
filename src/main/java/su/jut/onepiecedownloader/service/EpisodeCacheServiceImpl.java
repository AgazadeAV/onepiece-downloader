package su.jut.onepiecedownloader.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import su.jut.onepiecedownloader.exception.ScanningInProgressException;
import su.jut.onepiecedownloader.util.AppConstants;

import java.io.IOException;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class EpisodeCacheServiceImpl implements EpisodeCacheService {

    private static final int INITIAL_EPISODE = 1;
    private static final int GROWTH_MULTIPLIER = 3;
    private static final int TIMEOUT_MS = 2500;
    private static final int RETRY_COUNT = 3;
    private static final int RETRY_DELAY_MS = 300;

    private volatile int totalEpisodes = -1;

    @PostConstruct
    public void init() {
        Executors.newSingleThreadExecutor().submit(this::countAvailableEpisodes);
    }

    private void countAvailableEpisodes() {
        long start = System.currentTimeMillis(); // ⏱️ старт

        int episode = INITIAL_EPISODE;

        // 🔹 Шаг 1: Экспоненциальный рост
        while (episodeExists(episode)) {
            episode *= GROWTH_MULTIPLIER;
        }

        // 🔹 Шаг 2: Бинарный поиск между предыдущим успешным и текущим неудачным
        int low = episode / 2;
        int high = episode;
        int lastSuccessful = low;

        while (low <= high) {
            int mid = (low + high) / 2;
            if (episodeExists(mid)) {
                lastSuccessful = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        totalEpisodes = lastSuccessful;
        log.info("🔍 Всего доступно эпизодов: {}", totalEpisodes);

        double durationSec = (System.currentTimeMillis() - start) / 1000.0;
        log.info("⏱️ Сканирование завершено за {} секунд", String.format("%.2f", durationSec));
    }

    private boolean episodeExists(int episode) {
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
            } catch (InterruptedException ignored) {}
        }

        log.warn("⛔ Эпизод {} не подтвердился после {} попыток — считаем, что не существует", episode, RETRY_COUNT);
        return false;
    }

    @Override
    public int getTotalEpisodes() {
        if (totalEpisodes == -1) {
            throw new ScanningInProgressException("⏳ Эпизоды ещё сканируются, подожди немного...");
        }
        return totalEpisodes;
    }
}
