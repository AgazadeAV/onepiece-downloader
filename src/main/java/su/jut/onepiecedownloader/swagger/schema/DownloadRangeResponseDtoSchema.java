package su.jut.onepiecedownloader.swagger.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class DownloadRangeResponseDtoSchema {

    @Schema(description = "Описание результата загрузки", example = "Скачано 50 из 100 эпизодов")
    private String message;

    @Schema(description = "Сколько эпизодов было запрошено на скачивание", example = "1000")
    private int totalRequested;

    @Schema(description = "Сколько эпизодов успешно скачано", example = "998")
    private int totalSuccess;

    @Schema(description = "Сколько эпизодов не удалось скачать", example = "2")
    private int totalFailed;

    @Schema(description = "Список номеров эпизодов, которые не удалось скачать", example = "[45, 56-78, 90-100]")
    private List<String> failedEpisodes;
}
