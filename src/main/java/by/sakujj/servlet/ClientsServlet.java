package by.sakujj.servlet;

import by.sakujj.context.Context;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.services.ClientService;
import by.sakujj.servlet.error.ApiError;
import by.sakujj.servlet.util.ClientsServletUtil;
import by.sakujj.servlet.util.InstantAdapter;
import by.sakujj.servlet.util.ServletUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.validation.Validator;

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
import java.util.regex.Pattern;

import static by.sakujj.util.HttpStatusCode.*;

@WebServlet("/api/1/clients")
public class ClientsServlet extends HttpServlet {
    private ClientService clientService;
    private Validator validator;
    private Gson gson;

    private static final String EMAIL_REGEX = "[a-zA-Z0-9]{3,}@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+";
    public static final String EMAIL_PARAMETER_NAME = "email";
    public static final String PAGE_PARAMETER_NAME = "page";
    public static final String PAGE_SIZE_PARAMETER_NAME = "pagesize";

    public static final int DEFAULT_PAGE_SIZE = 20;


    @Override
    public void init() throws ServletException {
        Context context = Context.getInstance();

        clientService = context.getByClass(ClientService.class);
        validator = context.getByClass(Validator.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter(EMAIL_PARAMETER_NAME) != null) {
            processGetRequestWithEmailParameter(req, resp, clientService, gson);
            return;
        }

        if (req.getParameterMap().isEmpty()) {
            processGetRequestWithoutParameters(resp, clientService, gson);
            return;
        }

        processGetRequestWithPageAndSizeParameters(req, resp, clientService, gson);
    }

    private static void processGetRequestWithEmailParameter(HttpServletRequest req,
                                                            HttpServletResponse resp,
                                                            ClientService clientService,
                                                            Gson gson) throws IOException {
        String email = req.getParameter(EMAIL_PARAMETER_NAME);

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            ApiError apiError = new ApiError(
                    BAD_REQUEST,
                    "Bad request",
                    Instant.now(),
                    List.of("Malformed %s parameter".formatted(EMAIL_PARAMETER_NAME))
            );
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
        }

        Optional<ClientResponse> clientResponse = clientService.findByEmail(email);
        if (clientResponse.isEmpty()) {
            ApiError apiError = new ApiError(
                    NOT_FOUND,
                    "Not found",
                    Instant.now(),
                    List.of("Requested client with %s = %s is not found".formatted(EMAIL_PARAMETER_NAME, email)));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return;
        }

        ServletUtil.writeJsonToResponse(OK, clientResponse.get(), resp, gson);
    }

    private static void processGetRequestWithoutParameters(HttpServletResponse resp, ClientService clientService, Gson gson) throws IOException {
        List<ClientResponse> responseList = clientService.findByPageWithSize(1, DEFAULT_PAGE_SIZE);
        ServletUtil.writeJsonToResponse(OK, responseList, resp, gson);
    }

    private static void processGetRequestWithPageAndSizeParameters(HttpServletRequest req,
                                                                   HttpServletResponse resp,
                                                                   ClientService clientService,
                                                                   Gson gson) throws IOException {
        int page;
        try {
            page = validateAndGetPage(req);
        } catch (IllegalArgumentException ex) {
            ApiError apiError = new ApiError(BAD_REQUEST, "Bad request", Instant.now(),
                    List.of("An error with '%s' parameter: ".formatted(PAGE_PARAMETER_NAME) + ex.getMessage()));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return;
        }

        int size;
        try {
            size = validateAndGetSize(req);
        } catch (IllegalArgumentException ex) {
            ApiError apiError = new ApiError(BAD_REQUEST, "Bad request", Instant.now(),
                    List.of("An error with '%s' parameter: ".formatted(PAGE_SIZE_PARAMETER_NAME) + ex.getMessage()));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return;
        }

        List<ClientResponse> responseList = clientService.findByPageWithSize(page, size);
        ServletUtil.writeJsonToResponse(OK, responseList, resp, gson);
    }

    private static int validateAndGetSize(HttpServletRequest req) {
        String pageSize = req.getParameter(PAGE_SIZE_PARAMETER_NAME);
        int size = DEFAULT_PAGE_SIZE;
        if (pageSize != null) {
            size = Integer.parseInt(pageSize);
        }

        if (size < 1) {
            throw new IllegalArgumentException("'%s' parameter should be >= 1".formatted(PAGE_SIZE_PARAMETER_NAME));
        }
        return size;
    }

    private static int validateAndGetPage(HttpServletRequest req) {
        int page = Integer.parseInt(req.getParameter(PAGE_PARAMETER_NAME));

        if (page < 1) {
            throw new IllegalArgumentException("'%s' parameter should be >= 1".formatted(PAGE_PARAMETER_NAME));
        }
        return page;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        Optional<UUID> optionalUUID = saveClientRequestOrWriteApiErrorToResponse(
                optionalClientRequest.get(),
                resp,
                clientService,
                gson);
        if (optionalUUID.isEmpty()) {
            return;
        }

        ServletUtil.writeJsonToResponse(CREATED, optionalUUID.get(), resp, gson);
    }

    private static Optional<UUID> saveClientRequestOrWriteApiErrorToResponse(ClientRequest clientRequest,
                                                                             HttpServletResponse resp,
                                                                             ClientService clientService,
                                                                             Gson gson) throws IOException {
        UUID uuid;
        try {
            uuid = clientService.save(clientRequest);
        } catch (IllegalArgumentException ex) {
            ApiError apiError = new ApiError(
                    BAD_REQUEST,
                    "Bad request",
                    Instant.now(),
                    List.of(ex.getMessage()));
            ServletUtil.writeApiErrorToResponse(apiError, resp, gson);
            return Optional.empty();
        }

        return Optional.of(uuid);
    }
}
