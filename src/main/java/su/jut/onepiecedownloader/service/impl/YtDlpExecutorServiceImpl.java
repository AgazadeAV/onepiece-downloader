package su.jut.onepiecedownloader.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import su.jut.onepiecedownloader.exception.YtDlpException;
import su.jut.onepiecedownloader.service.YtDlpExecutorService;
import su.jut.onepiecedownloader.util.AppConstants;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class YtDlpExecutorServiceImpl implements YtDlpExecutorService {

    @Override
    public void download(int episodeNumber, String quality) {
        ProcessBuilder processBuilder = getProcessBuilder(episodeNumber, quality);

        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.inheritIO();

        try {
            log.info("🎬 Downloading episode {} in {}p...", episodeNumber, quality);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new YtDlpException("yt-dlp exited with code: " + exitCode);
            }
            log.info("✅ Episode {} downloaded!", episodeNumber);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new YtDlpException("Ошибка при запуске yt-dlp: " + e.getMessage());
        }
    }

    private static ProcessBuilder getProcessBuilder(int episodeNumber, String quality) {
        String episodeUrl = String.format(AppConstants.BASE_EPISODE_URL, episodeNumber);
        String outputFileName = String.format(AppConstants.OUTPUT_FILE_PATTERN, episodeNumber, quality);
        String outputPath = AppConstants.getDownloadPathFor(outputFileName);

        return new ProcessBuilder(
                "yt-dlp",
                "--referer", AppConstants.REFERER,
                "-f", "bestvideo[height<=" + quality + "]+bestaudio/best[height<=" + quality + "]",
                "-o", outputPath,
                episodeUrl
        );
    }
}
