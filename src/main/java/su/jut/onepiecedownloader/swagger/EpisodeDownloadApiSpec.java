package su.jut.onepiecedownloader.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import su.jut.onepiecedownloader.dto.DownloadResponseDto;

@Tag(name = "One Piece Downloader API", description = "Скачивание эпизодов и управление доступными сериями")
public interface EpisodeDownloadApiSpec {

    @Operation(summary = "Скачать один эпизод", description = "Скачивает указанный эпизод в заданном качестве")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Эпизод успешно скачан",
                    content = @Content(schema = @Schema(implementation = DownloadResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный номер эпизода",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<DownloadResponseDto> downloadOne(int episodeNumber, String quality);

    @Operation(summary = "Скачать диапазон эпизодов", description = "Скачивает все эпизоды в указанном диапазоне")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Эпизоды успешно скачаны",
                    content = @Content(schema = @Schema(implementation = DownloadResponseDto.class)))
    })
    ResponseEntity<DownloadResponseDto> downloadRange(int start, int end, String quality);

    @Operation(summary = "Скачать все доступные эпизоды", description = "Скачивает все эпизоды, определённые автоматически")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Все эпизоды успешно скачаны",
                    content = @Content(schema = @Schema(implementation = DownloadResponseDto.class))),
            @ApiResponse(responseCode = "503", description = "Количество серий ещё определяется",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<DownloadResponseDto> downloadAll(String quality);

    @Operation(summary = "Получить количество доступных эпизодов", description = "Возвращает общее количество доступных эпизодов One Piece")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Количество получено",
                    content = @Content(schema = @Schema(implementation = DownloadResponseDto.class)))
    })
    ResponseEntity<DownloadResponseDto> getAvailableEpisodes();
}
