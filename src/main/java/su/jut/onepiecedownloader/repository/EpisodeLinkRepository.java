package su.jut.onepiecedownloader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.jut.onepiecedownloader.model.EpisodeLink;

import java.util.Optional;

@Repository
public interface EpisodeLinkRepository extends JpaRepository<EpisodeLink, Integer> {

    boolean existsByEpisodeNumber(int episode);

    Optional<EpisodeLink> findTopByOrderByEpisodeNumberDesc();

    Optional<EpisodeLink> findByEpisodeNumber(Integer episodeNumber);
}
