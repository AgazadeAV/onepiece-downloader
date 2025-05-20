package su.jut.onepiecedownloader.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.jut.onepiecedownloader.dto.DownloadResponseDto;
import su.jut.onepiecedownloader.exception.EpisodeNotAvailableException;
import su.jut.onepiecedownloader.util.YtDlpExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
@RequiredArgsConstructor
public class EpisodeDownloadServiceImpl implements EpisodeDownloadService {

    private final static int THREAD_COUNT = 10;

    private final YtDlpExecutor ytDlpExecutor;
    private final EpisodeCacheService episodeCacheService;

    @Override
    public DownloadResponseDto downloadOne(int episodeNumber, String quality) {
        checkEpisodeLimit(episodeNumber);
        ytDlpExecutor.download(episodeNumber, quality);
        return new DownloadResponseDto("Episode " + episodeNumber + " downloaded in " + quality + "p", 1);
    }

    @Override
    public DownloadResponseDto downloadRange(int start, int end, String quality) {
        checkEpisodeLimit(end);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = start; i <= end; i++) {
            final int episodeNumber = i;
            futures.add(executor.submit(() -> ytDlpExecutor.download(episodeNumber, quality)));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Error downloading episode in range", e);
            }
        }

        executor.shutdown();

        int count = end - start + 1;
        return new DownloadResponseDto("Episodes from " + start + " to " + end + " downloaded in " + quality + "p", count);
    }

    @Override
    public DownloadResponseDto downloadAll(String quality) {
        int total = episodeCacheService.getTotalEpisodes();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 1; i <= total; i++) {
            final int episodeNumber = i;
            futures.add(executor.submit(() -> ytDlpExecutor.download(episodeNumber, quality)));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Error downloading episode", e);
            }
        }

        executor.shutdown();

        return new DownloadResponseDto("All available episodes downloaded in " + quality + "p", total);
    }


    @Override
    public DownloadResponseDto getAvailableEpisodeCount() {
        int total = episodeCacheService.getTotalEpisodes();
        return new DownloadResponseDto("Total available episodes: " + total, total);
    }

    private void checkEpisodeLimit(int episodeNumber) {
        int total = episodeCacheService.getTotalEpisodes();
        if (episodeNumber > total) {
            throw new EpisodeNotAvailableException("Episode " + episodeNumber + " does not exist (max available: " + total + ")");
        }
    }
}
