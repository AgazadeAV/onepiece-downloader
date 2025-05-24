package su.jut.onepiecedownloader.service;

import su.jut.onepiecedownloader.dto.AvailableEpisodesDto;
import su.jut.onepiecedownloader.dto.DownloadOneResponseDto;
import su.jut.onepiecedownloader.dto.DownloadRangeResponseDto;
import su.jut.onepiecedownloader.dto.ScanResultDto;

public interface EpisodeManagerService {

    DownloadOneResponseDto downloadOne(int episodeNumber, String quality);

    DownloadRangeResponseDto downloadRange(int start, int end, String quality);

    DownloadRangeResponseDto downloadAll(String quality);

    AvailableEpisodesDto getAvailableEpisodeCount();

    ScanResultDto scanAndSaveMissingEpisodes();

    ScanResultDto checkForNewEpisodeAndSave();
}
