package su.jut.onepiecedownloader.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import su.jut.onepiecedownloader.dto.AvailableEpisodesDto;
import su.jut.onepiecedownloader.dto.DownloadOneResponseDto;
import su.jut.onepiecedownloader.dto.DownloadRangeResponseDto;
import su.jut.onepiecedownloader.dto.ScanResultDto;

@Tag(name = "One Piece Downloader API", description = "Скачивание эпизодов и управление доступными сериями")
public interface EpisodeDownloadApiSpec {

    @Operation(
            summary = "Скачать один эпизод",
            description = "Скачивает указанный эпизод в заданном качестве"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Эпизод успешно скачан",
                    content = @Content(schema = @Schema(implementation = DownloadOneResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный номер эпизода",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<DownloadOneResponseDto> downloadOne(int episodeNumber, String quality);

    @Operation(
            summary = "Скачать диапазон эпизодов",
            description = "Скачивает все эпизоды в указанном диапазоне"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Эпизоды успешно скачаны",
                    content = @Content(schema = @Schema(implementation = DownloadRangeResponseDto.class)))
    })
    ResponseEntity<DownloadRangeResponseDto> downloadRange(int start, int end, String quality);

    @Operation(
            summary = "Скачать все доступные эпизоды",
            description = "Скачивает все эпизоды, определённые автоматически"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Все эпизоды успешно скачаны",
                    content = @Content(schema = @Schema(implementation = DownloadRangeResponseDto.class))),
            @ApiResponse(responseCode = "503", description = "Количество серий ещё определяется",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<DownloadRangeResponseDto> downloadAll(String quality);

    @Operation(
            summary = "Получить количество доступных эпизодов",
            description = "Возвращает общее количество доступных эпизодов One Piece"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Количество получено",
                    content = @Content(schema = @Schema(implementation = AvailableEpisodesDto.class)))
    })
    ResponseEntity<AvailableEpisodesDto> getAvailableEpisodes();

    @Operation(
            summary = "Сканировать недостающие эпизоды",
            description = "Начинает сканирование с первого пропущенного и сохраняет все доступные последующие"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сканирование завершено",
                    content = @Content(schema = @Schema(implementation = ScanResultDto.class)))
    })
    ResponseEntity<ScanResultDto> scanAndSaveMissingEpisodes();

    @Operation(
            summary = "Проверить наличие нового эпизода",
            description = "Проверяет наличие следующего эпизода после последнего сохранённого. Если найден — он сохраняется"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Проверка завершена",
                    content = @Content(schema = @Schema(implementation = ScanResultDto.class)))
    })
    ResponseEntity<ScanResultDto> checkNewEpisode();

}
