package su.jut.onepiecedownloader.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.experimental.UtilityClass;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.stream.Collectors;

@UtilityClass
public class ExceptionMessageUtil {

    public static String getConstraintViolationMessage(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(violation -> String.format(
                        "Поле `%s`: %s (некорректное значение: `%s`)",
                        violation.getPropertyPath(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .collect(Collectors.joining("; "));
    }

    public static String getMethodArgumentNotValidMessage(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Поле `%s`: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
    }

    public static String getMissingRequestParamMessage(MissingServletRequestParameterException ex) {
        return String.format(
                "Отсутствует обязательный параметр запроса: `%s` (ожидаемый тип: `%s`)",
                ex.getParameterName(),
                ex.getParameterType());
    }

    public static <T extends Exception> String getExceptionMessage(T ex, String prefix) {
        String message = prefix + ": " + ex.getMessage();
        if (ex.getCause() != null) {
            message += " | Причина: " + ex.getCause().getMessage();
        }
        return message;
    }
}
