package su.jut.onepiecedownloader.swagger.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ScanResultDtoSchema {

    @Schema(description = "Описание операции", example = "Сканирование завершено")
    private String message;

    @Schema(description = "Количество успешно сохранённых эпизодов", example = "15")
    private int saved;

    @Schema(description = "Количество эпизодов, которые не удалось сохранить", example = "2")
    private int failed;

    @Schema(description = "Последний эпизод, найденный на сайте", example = "1110")
    private int lastEpisodeOnSite;
}
