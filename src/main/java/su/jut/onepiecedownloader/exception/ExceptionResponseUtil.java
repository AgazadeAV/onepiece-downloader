package su.jut.onepiecedownloader.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@UtilityClass
public class ExceptionResponseUtil {

    public static ResponseEntity<ExceptionResponse> buildErrorResponse(HttpStatus status, String message) {
        ExceptionResponse response = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
