package su.jut.onepiecedownloader.service;

public interface EpisodeCacheService {
    void scanAndSaveAllEpisodes();

    int getTotalEpisodes();

    void checkForNewEpisodeAndSave();
}
