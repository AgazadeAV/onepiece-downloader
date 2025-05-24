package su.jut.onepiecedownloader.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import su.jut.onepiecedownloader.swagger.schema.ScanResultDtoSchema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(implementation = ScanResultDtoSchema.class)
public class ScanResultDto {

    private String message;
    private int saved;
    private int failed;
    private int lastEpisodeOnSite;
}
