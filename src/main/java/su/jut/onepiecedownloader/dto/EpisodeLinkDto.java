package su.jut.onepiecedownloader.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EpisodeLinkDto {
    private int episodeNumber;
    private String episodeTitle;
    private String episodeUrl;
    private String availableQualities;
}
