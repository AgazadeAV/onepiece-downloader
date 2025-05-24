package su.jut.onepiecedownloader.client;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import su.jut.onepiecedownloader.util.AppConstants;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EpisodePageClient {

    private static final int TIMEOUT_MS = 2500;
    private static final int RETRY_COUNT = 3;
    private static final int RETRY_DELAY_MS = 300;

    public Document fetchEpisodePage(int episodeNumber) {
        String url = String.format(AppConstants.BASE_EPISODE_URL, episodeNumber);
        try {
            return Jsoup.connect(url).timeout(TIMEOUT_MS).get();
        } catch (IOException e) {
            log.warn("❗ Ошибка при загрузке страницы эпизода {}: {}", episodeNumber, e.getMessage());
            return null;
        }
    }

    public boolean pageExists(int episodeNumber) {
        String url = String.format(AppConstants.BASE_EPISODE_URL, episodeNumber);

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
                    log.info("🔎 Эпизод {} не существует (404)", episodeNumber);
                    return false;
                } else {
                    log.warn("⚠️ Эпизод {} вернул статус {}. Попытка {}/{}", episodeNumber, statusCode, i + 1, RETRY_COUNT);
                }
            } catch (IOException e) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("timed out")) {
                    log.warn("⏱️ Timeout при проверке эпизода {}: {} (попытка {}/{})", episodeNumber, msg, i + 1, RETRY_COUNT);
                } else {
                    log.warn("❗️ IO ошибка при проверке эпизода {}: {} (попытка {}/{})", episodeNumber, msg, i + 1, RETRY_COUNT);
                }
            }

            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException ignored) {
            }
        }

        log.warn("⛔ Эпизод {} не подтвердился после {} попыток — считаем, что не существует", episodeNumber, RETRY_COUNT);
        return false;
    }

    public String extractTitle(Document doc) {
        try {
            var titleEl = doc.selectFirst("div.video_plate_title > span > h2");
            if (titleEl == null) {
                log.warn("⚠️ Заголовок не найден в документе");
                return null;
            }
            return titleEl.text();
        } catch (Exception e) {
            log.warn("⚠️ Ошибка при извлечении заголовка: {}", e.getMessage());
            return null;
        }
    }

    public String extractQualities(Document doc) {
        try {
            String qualities = doc.select("video source").stream()
                    .map(e -> e.attr("res").trim())
                    .filter(res -> !res.isBlank())
                    .distinct()
                    .sorted(Comparator.comparingInt(this::sortWeight))
                    .map(res -> isNumeric(res) ? res + "p" : res)
                    .collect(Collectors.joining(","));

            return qualities.isBlank() ? null : qualities;
        } catch (Exception e) {
            log.warn("⚠️ Не удалось извлечь качества: {}", e.getMessage());
            return null;
        }
    }

    private int sortWeight(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private boolean isNumeric(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
