package su.jut.onepiecedownloader.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import su.jut.onepiecedownloader.swagger.schema.DownloadResponseDtoSchema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(implementation = DownloadResponseDtoSchema.class)
public class DownloadResponseDto {
    private String message;
    private int total;
}
