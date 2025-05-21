package su.jut.onepiecedownloader.service;

import su.jut.onepiecedownloader.dto.DownloadResponseDto;

public interface EpisodeDownloadService {
    DownloadResponseDto downloadOne(int episodeNumber, String quality);

    DownloadResponseDto downloadRange(int start, int end, String quality);

    DownloadResponseDto downloadAll(String quality);

    DownloadResponseDto getAvailableEpisodeCount();

    void scanAllEpisodes();
}
