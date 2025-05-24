package su.jut.onepiecedownloader.swagger.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class DownloadOneResponseDtoSchema {

    @Schema(description = "Описание результата загрузки", example = "Эпизод 1 успешно загружен в качестве 360p")
    private String message;

    @Schema(description = "Количество скачанных эпизодов", example = "1")
    private int total;
}
