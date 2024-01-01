package by.sakujj.servlet;

import by.sakujj.context.AppContext;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.services.ClientService;
import by.sakujj.servlet.error.ApiError;
import by.sakujj.servlet.util.ClientsServletUtil;
import by.sakujj.servlet.util.ServletUtil;
import com.google.gson.Gson;
import jakarta.validation.Validator;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static by.sakujj.util.HttpStatusCode.*;

@WebServlet("/api/1/clients/*")
public class ClientsByIdServlet extends HttpServlet {
    public static final String BASE_URI = "/api/1/clients/";

    private ClientService clientService;
    private Validator validator;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        ApplicationContext context = AppContext.getInstance();
        clientService = context.getBean(ClientService.class);
        validator = context.getBean(Validator.class);
        gson = context.getBean(Gson.class);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqURI = req.getRequestURI();

        Optional<UUID> optionalUUID = ClientsServletUtil.parseUUIDFromRequestURI(reqURI, BASE_URI);

        if (optionalUUID.isEmpty()) {
            ApiError apiError = new ApiError(
                    BAD_REQUEST,
                    "Bad request",
                    Instant.now(),
                    List.of("Malformed id specified in the uri"));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return;
        }

        UUID requestedUUID = optionalUUID.get();

        Optional<ClientResponse> optionalResponse = clientService.findById(requestedUUID);

        if (optionalResponse.isEmpty()) {
            ApiError apiError = new ApiError(
                    NOT_FOUND,
                    "Not found",
                    Instant.now(),
                    List.of("Requested client with id = %s is not found".formatted(requestedUUID)));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return;
        }

        ClientResponse clientResponse = optionalResponse.get();
        ServletUtil.writeJsonToResponse(OK, clientResponse, resp, gson);
    }


    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqURI = req.getRequestURI();
        Optional<UUID> optionalUUID = ClientsServletUtil.parseUUIDFromRequestURI(reqURI, BASE_URI);
        if (optionalUUID.isEmpty()) {
            ApiError apiError = new ApiError(
                    BAD_REQUEST,
                    "Bad request",
                    Instant.now(),
                    List.of("Malformed id specified in the uri"));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return;
        }

        Optional<String> optionalJsonDto = ClientsServletUtil.getPostRequestBodyOrWriteApiErrorToResponse(req, resp, gson);
        if (optionalJsonDto.isEmpty()) {
            return;
        }

        Optional<ClientRequest> optionalClientRequest = ClientsServletUtil.getValidatedClientRequestOrWriteApiErrorToResponse(
                optionalJsonDto.get(),
                validator,
                resp,
                gson);
        if (optionalClientRequest.isEmpty()) {
            return;
        }

        ClientRequest clientRequest = optionalClientRequest.get();
        UUID id = optionalUUID.get();
        boolean isUpdated = clientService.updateById(id, clientRequest);
        if (!isUpdated) {
            ApiError apiError = new ApiError(
                    NOT_FOUND,
                    "Bad request",
                    Instant.now(),
                    List.of("Client with requested id is not present"));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return;
        }

        resp.setStatus(NO_CONTENT);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqURI = req.getRequestURI();
        Optional<UUID> optionalUUID = ClientsServletUtil.parseUUIDFromRequestURI(reqURI, BASE_URI);
        if (optionalUUID.isEmpty()) {
            ApiError apiError = new ApiError(
                    BAD_REQUEST,
                    "Bad request",
                    Instant.now(),
                    List.of("Malformed id specified in the uri"));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return;
        }

        UUID id = optionalUUID.get();
        boolean isDeleted = clientService.deleteById(id);
        if (!isDeleted) {
            ApiError apiError = new ApiError(
                    NOT_FOUND,
                    "Bad request",
                    Instant.now(),
                    List.of("Client with requested id is not present"));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return;
        }

        resp.setStatus(NO_CONTENT);
    }
}
