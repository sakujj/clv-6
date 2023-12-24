package by.sakujj.servlet.util;

import by.sakujj.servlet.error.ApiError;
import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@UtilityClass
public class ServletUtil {
    public <T> void writeJsonToResponse(int statusCode, T object, HttpServletResponse resp, Gson gson) throws IOException {
        resp.setStatus(statusCode);
        String responseBody = gson.toJson(object);
        resp.getWriter().write(responseBody);
    }

    public void writeApiErrorToResponse(ApiError apiError, HttpServletResponse response, Gson gson) throws IOException {
        response.setStatus(apiError.getStatusCode());
        String responseBody = gson.toJson(apiError);
        response.getWriter().write(responseBody);
    }
}
