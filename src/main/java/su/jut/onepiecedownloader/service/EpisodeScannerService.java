package su.jut.onepiecedownloader.service;

import su.jut.onepiecedownloader.dto.ScanResultDto;

public interface EpisodeScannerService {

    ScanResultDto scanAndSaveMissingEpisodes();

    int getTotalEpisodes();

    ScanResultDto checkForNewEpisodeAndSave();
}
