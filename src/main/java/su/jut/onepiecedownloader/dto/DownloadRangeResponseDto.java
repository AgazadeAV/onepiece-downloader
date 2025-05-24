package su.jut.onepiecedownloader.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import su.jut.onepiecedownloader.swagger.schema.DownloadRangeResponseDtoSchema;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(implementation = DownloadRangeResponseDtoSchema.class)
public class DownloadRangeResponseDto {

    private String message;
    private int totalRequested;
    private int totalSuccess;
    private int totalFailed;
    private List<String> failedEpisodes;
}
