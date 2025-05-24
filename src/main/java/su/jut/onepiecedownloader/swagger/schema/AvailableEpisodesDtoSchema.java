package su.jut.onepiecedownloader.swagger.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AvailableEpisodesDtoSchema {

    @Schema(description = "Общее количество эпизодов в базе", example = "1050")
    private int total;

    @Schema(description = "Сообщение с кратким пояснением", example = "Всего доступных эпизодов: 1050")
    private String message;
}
