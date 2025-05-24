package su.jut.onepiecedownloader.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import su.jut.onepiecedownloader.swagger.schema.DownloadOneResponseDtoSchema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(implementation = DownloadOneResponseDtoSchema.class)
public class DownloadOneResponseDto {

    private String message;
    private int total;
}
