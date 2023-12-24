package by.sakujj.servlet.util;

import by.sakujj.dto.ClientRequest;
import by.sakujj.servlet.error.ApiError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static by.sakujj.util.HttpStatusCode.BAD_REQUEST;

@UtilityClass
public class ClientsServletUtil {
    public static Optional<UUID> parseUUIDFromRequestURI(String reqURI, String baseURI) {
        String reqInfo = reqURI.substring(reqURI.indexOf(baseURI) + baseURI.length());
        return parseStringToUUID(reqInfo);
    }

    private static Optional<UUID> parseStringToUUID(String string) {
        try {
            return Optional.of(UUID.fromString(string));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static Optional<String> getPostRequestBodyOrWriteApiErrorToResponse(HttpServletRequest req, HttpServletResponse resp, Gson gson) throws IOException {
        int contentLength = req.getContentLength();
        if (contentLength == -1) {
            ApiError apiError = new ApiError(
                    BAD_REQUEST,
                    "Bad request",
                    Instant.now(),
                    List.of("Content-Length header should be present"));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return Optional.empty();
        }
        BufferedReader reader = req.getReader();
        char[] jsonBody = new char[contentLength + 1];
        if (reader.read(jsonBody) != contentLength) {
            ApiError apiError = new ApiError(
                    BAD_REQUEST,
                    "Bad request",
                    Instant.now(),
                    List.of("Content-Length is not correct"));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return Optional.empty();
        }

        return Optional.of(new String(jsonBody, 0, contentLength));
    }

    public static Optional<ClientRequest> getValidatedClientRequestOrWriteApiErrorToResponse(String jsonDto,
                                                                                             Validator validator,
                                                                                             HttpServletResponse resp,
                                                                                             Gson gson) throws IOException {
        ClientRequest clientRequest;
        try {
            clientRequest = gson.fromJson(jsonDto, ClientRequest.class);
        } catch (JsonSyntaxException ex) {
            ApiError apiError = new ApiError(
                    BAD_REQUEST,
                    "Bad request",
                    Instant.now(),
                    List.of(ex.getMessage()));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return Optional.empty();
        }

        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(clientRequest);
        if (!violations.isEmpty()) {
            ApiError apiError = new ApiError(
                    BAD_REQUEST,
                    "Bad request",
                    Instant.now(),
                    violations
                            .stream()
                            .map(v -> "'" + v.getPropertyPath().toString() + "' " + v.getMessage())
                            .toList());
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return Optional.empty();
        }

        return Optional.of(clientRequest);
    }
}
