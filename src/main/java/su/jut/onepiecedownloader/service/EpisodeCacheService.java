package su.jut.onepiecedownloader.service;

public interface EpisodeCacheService {
    void scanAllEpisodes();

    int getTotalEpisodes();

    void checkForNewEpisode();
}
