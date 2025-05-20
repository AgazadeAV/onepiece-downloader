package su.jut.onepiecedownloader.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EpisodeNotAvailableException.class)
    public ResponseEntity<String> handleNotFound(EpisodeNotAvailableException ex) {
        log.warn("❌ {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ScanningInProgressException.class)
    public ResponseEntity<String> handleScanning(ScanningInProgressException ex) {
        log.info("🔄 {}", ex.getMessage());
        return ResponseEntity.status(503).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleGeneric(RuntimeException ex) {
        log.error("❗ Runtime error: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body("Unexpected error occurred");
    }

    @ExceptionHandler(YtDlpException.class)
    public ResponseEntity<String> handleYtDlpException(YtDlpException ex) {
        log.info("yt-dlp error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
