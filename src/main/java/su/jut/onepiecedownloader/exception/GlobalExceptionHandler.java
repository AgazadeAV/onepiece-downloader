package su.jut.onepiecedownloader.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EpisodeNotAvailableException.class)
    public ResponseEntity<ExceptionResponse> handleNotAvailable(EpisodeNotAvailableException ex) {
        log.warn("🚫 Эпизод недоступен: {}", ex.getMessage());
        return ExceptionResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(YtDlpException.class)
    public ResponseEntity<ExceptionResponse> handleYtDlp(YtDlpException ex) {
        log.warn("⚠️ Ошибка yt-dlp: {}", ex.getMessage());
        return ExceptionResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ExceptionMessageUtil.getConstraintViolationMessage(ex);
        log.warn("🚫 Нарушение ограничений параметров запроса: {}", msg);
        return ExceptionResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ExceptionMessageUtil.getMethodArgumentNotValidMessage(ex);
        log.warn("📥 Ошибка в теле запроса: {}", msg);
        return ExceptionResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String msg = ExceptionMessageUtil.getMissingRequestParamMessage(ex);
        log.warn("⚠️ Отсутствует обязательный параметр запроса: {}", msg);
        return ExceptionResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleGeneric(RuntimeException ex) {
        log.error("❌ Непредвиденная ошибка: {}", ex.getMessage(), ex);
        return ExceptionResponseUtil.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла непредвиденная ошибка");
    }
}
