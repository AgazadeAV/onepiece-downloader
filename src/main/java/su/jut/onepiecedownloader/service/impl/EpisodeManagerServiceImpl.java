package su.jut.onepiecedownloader.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.jut.onepiecedownloader.dto.AvailableEpisodesDto;
import su.jut.onepiecedownloader.dto.DownloadOneResponseDto;
import su.jut.onepiecedownloader.dto.DownloadRangeResponseDto;
import su.jut.onepiecedownloader.dto.ScanResultDto;
import su.jut.onepiecedownloader.exception.EpisodeNotAvailableException;
import su.jut.onepiecedownloader.model.EpisodeLink;
import su.jut.onepiecedownloader.repository.EpisodeLinkRepository;
import su.jut.onepiecedownloader.service.EpisodeDownloadService;
import su.jut.onepiecedownloader.service.EpisodeManagerService;
import su.jut.onepiecedownloader.service.EpisodeScannerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EpisodeManagerServiceImpl implements EpisodeManagerService {

    private static final int THREAD_COUNT = 10;
    private static final int FIRST_EPISODE = 1;

    private final EpisodeDownloadService ytDlpEpisodeDownloadService;
    private final EpisodeScannerService episodeScannerService;
    private final EpisodeLinkRepository episodeLinkRepository;

    @Override
    public DownloadOneResponseDto downloadOne(int episodeNumber, String quality) {
        checkQuality(episodeNumber, quality);
        checkEpisodeExists(episodeNumber);
        ytDlpEpisodeDownloadService.download(episodeNumber, quality);
        return DownloadOneResponseDto.builder()
                .message("Эпизод " + episodeNumber + " успешно загружен в качестве " + quality + "p")
                .total(episodeNumber)
                .build();
    }

    @Override
    public DownloadRangeResponseDto downloadRange(int start, int end, String quality) {
        checkEpisodeRange(start, end);
        return downloadEpisodes(start, end, quality);
    }

    @Override
    public DownloadRangeResponseDto downloadAll(String quality) {
        int total = episodeScannerService.getTotalEpisodes();
        if (total == 0) {
            throw new EpisodeNotAvailableException("Нет доступных эпизодов для загрузки.");
        }
        return downloadEpisodes(FIRST_EPISODE, total, quality);
    }

    @Override
    public AvailableEpisodesDto getAvailableEpisodeCount() {
        int total = episodeScannerService.getTotalEpisodes();
        return AvailableEpisodesDto.builder()
                .total(total)
                .message("Всего доступных эпизодов: " + total)
                .build();
    }

    @Override
    public ScanResultDto scanAndSaveMissingEpisodes() {
        return episodeScannerService.scanAndSaveMissingEpisodes();
    }

    @Override
    public ScanResultDto checkForNewEpisodeAndSave() {
        return episodeScannerService.checkForNewEpisodeAndSave();
    }

    private DownloadRangeResponseDto downloadEpisodes(int start, int end, String quality) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Integer> failedEpisodes = new ArrayList<>();

        for (int i = start; i <= end; i++) {
            final int episodeNumber = i;
            executor.submit(() -> {
                try {
                    if (!isQualityAvailable(episodeNumber, quality)) {
                        log.warn("⚠️ Эпизод {} не содержит качество {}", episodeNumber, quality);
                        synchronized (failedEpisodes) {
                            failedEpisodes.add(episodeNumber);
                        }
                        return;
                    }

                    ytDlpEpisodeDownloadService.download(episodeNumber, quality);
                } catch (Exception e) {
                    log.error("❌ Ошибка загрузки эпизода {}: {}", episodeNumber, e.getMessage());
                    synchronized (failedEpisodes) {
                        failedEpisodes.add(episodeNumber);
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.yield();
        }

        int total = end - start + 1;
        int failed = failedEpisodes.size();
        int success = total - failed;

        return DownloadRangeResponseDto.builder()
                .message("Скачано " + success + " из " + total + " эпизодов")
                .totalRequested(total)
                .totalSuccess(success)
                .totalFailed(failed)
                .failedEpisodes(formatFailedEpisodes(failedEpisodes))
                .build();
    }

    private void checkEpisodeExists(int episodeNumber) {
        int total = episodeScannerService.getTotalEpisodes();
        if (episodeNumber < FIRST_EPISODE || episodeNumber > total) {
            throw new EpisodeNotAvailableException("Эпизод " + episodeNumber + " не существует (доступный диапазон: " + FIRST_EPISODE + " - " + total + ")");
        }
    }

    private void checkEpisodeRange(int start, int end) {
        int total = episodeScannerService.getTotalEpisodes();

        if (start < FIRST_EPISODE) {
            throw new EpisodeNotAvailableException("Начальный номер эпизода должен быть не меньше " + FIRST_EPISODE);
        }
        if (start > end) {
            throw new EpisodeNotAvailableException("Начальный эпизод не может быть больше конечного");
        }
        if (end > total) {
            throw new EpisodeNotAvailableException("Эпизод " + end + " не существует (максимально доступный: " + total + ")");
        }
    }

    private void checkQuality(int episodeNumber, String quality) {
        EpisodeLink episode = episodeLinkRepository.findByEpisodeNumber(episodeNumber)
                .orElseThrow(() -> new EpisodeNotAvailableException("Эпизод " + episodeNumber + " не найден в базе данных"));

        String availableQualities = episode.getAvailableQualities();

        if (availableQualities == null || availableQualities.isBlank()) {
            throw new EpisodeNotAvailableException("Для эпизода " + episodeNumber + " не указаны доступные качества");
        }

        List<String> qualities = List.of(availableQualities.split(","));

        if (!qualities.contains(quality)) {
            throw new EpisodeNotAvailableException("Качество " + quality + " недоступно для эпизода " + episodeNumber +
                    ". Доступные: " + qualities);
        }
    }

    private boolean isQualityAvailable(int episodeNumber, String quality) {
        return episodeLinkRepository.findByEpisodeNumber(episodeNumber)
                .map(link -> {
                    String available = link.getAvailableQualities();
                    if (available == null || available.isBlank()) return false;
                    return List.of(available.split(",")).contains(quality);
                })
                .orElse(false);
    }

    private List<String> formatFailedEpisodes(List<Integer> episodes) {
        if (episodes == null || episodes.isEmpty()) return List.of();

        List<String> result = new ArrayList<>();
        episodes.sort(Integer::compareTo);

        int start = episodes.get(0);
        int prev = start;

        for (int i = 1; i < episodes.size(); i++) {
            int current = episodes.get(i);
            if (current == prev + 1) {
                prev = current;
            } else {
                result.add(rangeToString(start, prev));
                start = current;
                prev = current;
            }
        }

        result.add(rangeToString(start, prev));
        return result;
    }

    private String rangeToString(int start, int end) {
        return (start == end) ? String.valueOf(start) : start + "-" + end;
    }
}
