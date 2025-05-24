package su.jut.onepiecedownloader.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeLinkDto {

    private int episodeNumber;
    private String episodeTitle;
    private String episodeUrl;
    private String availableQualities;
}
