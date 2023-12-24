package by.sakujj.servlet.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Getter
public class ApiError {
    private int statusCode;
    private String message;
    private Instant timestamp;
    private List<String> errors;
}
