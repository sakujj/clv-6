package unit.servlet;

import by.sakujj.context.Context;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.services.ClientService;
import by.sakujj.servlet.ClientsServlet;
import by.sakujj.servlet.util.ClientsServletUtil;
import by.sakujj.servlet.util.ServletUtil;
import com.google.gson.Gson;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ClientTestBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static by.sakujj.util.HttpStatusCode.CREATED;
import static by.sakujj.util.HttpStatusCode.OK;

@ExtendWith(MockitoExtension.class)
public class ClientsServletTests {
    @Mock
    private Context context;
    @Mock
    private ClientService clientService;
    @Mock
    private Validator validator;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private Gson gson;
    @Spy
    private ClientsServlet clientsServlet;

    @BeforeEach
    void beforeEach() throws ServletException {
        try (MockedStatic<Context> mockedStatic = Mockito.mockStatic(Context.class);
             MockedConstruction<Gson> mockedConstruction = Mockito.mockConstruction(Gson.class)) {

            mockedStatic.when(Context::getInstance)
                    .thenReturn(context);

            Mockito.when(context.getByClass(Validator.class))
                    .thenReturn(validator);
            Mockito.when(context.getByClass(ClientService.class))
                    .thenReturn(clientService);

            clientsServlet.init();
            gson = mockedConstruction.constructed().get(0);
        }
    }

    @Test
    void shouldWriteClientToGETResponseByEmail() throws ServletException, IOException {
        // given
        final String email = "email@gmail.com";
        final ClientResponse expected = ClientTestBuilder.aClient().buildResponse();

        Mockito.when(request.getParameter(ClientsServlet.EMAIL_PARAMETER_NAME))
                .thenReturn(email);
        Mockito.when(clientService.findByEmail(email))
                .thenReturn(Optional.of(expected));
        try (MockedStatic<ServletUtil> mockedStatic = Mockito.mockStatic(ServletUtil.class)) {

            // when
            clientsServlet.doGet(request, response);

            // then
            mockedStatic.verify(() -> ServletUtil.writeJsonToResponse(OK, expected, response, gson));
        }
    }

    @Test
    void shouldWriteClientsToGETResponse() throws ServletException, IOException {
        // given
        final List<ClientResponse> expected = List.of(
                ClientTestBuilder.aClient().buildResponse(),
                ClientTestBuilder.aClient()
                        .withId(UUID.fromString("3efe9b38-77c5-45fa-971f-43c02af55724"))
                        .withAge(26)
                        .withUsername("username")
                        .withEmail("email@gmail.com")
                        .buildResponse(),
                ClientTestBuilder.aClient()
                        .withId(UUID.fromString("730bfb26-3fb7-410f-a09c-2eddfa74f1ac"))
                        .withAge(36)
                        .withUsername("usernamexxx")
                        .withEmail("emaixxxl@gmail.com")
                        .buildResponse()
        );

        Mockito.when(request.getParameter(ClientsServlet.EMAIL_PARAMETER_NAME))
                .thenReturn(null);
        Mockito.when(clientService.findByPageWithSize(1, ClientsServlet.DEFAULT_PAGE_SIZE))
                .thenReturn(expected);
        try (MockedStatic<ServletUtil> mockedStatic = Mockito.mockStatic(ServletUtil.class)) {

            // when
            clientsServlet.doGet(request, response);

            // then
            mockedStatic.verify(() -> ServletUtil.writeJsonToResponse(OK, expected, response, gson));
        }
    }

    @Test
    void shouldWriteClientsToGETResponseWithPageAndPageSize() throws ServletException, IOException {
        // given
        final List<ClientResponse> expected = List.of(
                ClientTestBuilder.aClient().buildResponse(),
                ClientTestBuilder.aClient()
                        .withId(UUID.fromString("3efe9b38-77c5-45fa-971f-43c02af55724"))
                        .withAge(26)
                        .withUsername("username")
                        .withEmail("email@gmail.com")
                        .buildResponse(),
                ClientTestBuilder.aClient()
                        .withId(UUID.fromString("730bfb26-3fb7-410f-a09c-2eddfa74f1ac"))
                        .withAge(36)
                        .withUsername("usernamexxx")
                        .withEmail("emaixxxl@gmail.com")
                        .buildResponse()
        );

        final int page = 1;
        final int pageSize = expected.size();

        Map<String, String[]> paramMap = Map.of(
                ClientsServlet.PAGE_PARAMETER_NAME, new String[]{"" + page},
                ClientsServlet.PAGE_SIZE_PARAMETER_NAME, new String[]{"" + pageSize}
        );
        Mockito.when(request.getParameterMap())
                .thenReturn(paramMap);
        Mockito.when(request.getParameter(ClientsServlet.EMAIL_PARAMETER_NAME))
                .thenReturn(null);
        Mockito.when(request.getParameter(ClientsServlet.PAGE_PARAMETER_NAME))
                .thenReturn(paramMap.get(ClientsServlet.PAGE_PARAMETER_NAME)[0]);
        Mockito.when(request.getParameter(ClientsServlet.PAGE_SIZE_PARAMETER_NAME))
                .thenReturn(paramMap.get(ClientsServlet.PAGE_SIZE_PARAMETER_NAME)[0]);
        Mockito.when(clientService.findByPageWithSize(page, pageSize))
                .thenReturn(expected);
        try (MockedStatic<ServletUtil> mockedStatic = Mockito.mockStatic(ServletUtil.class)) {

            // when
            clientsServlet.doGet(request, response);

            // then
            mockedStatic.verify(() -> ServletUtil.writeJsonToResponse(OK, expected, response, gson));
        }
    }

    @Test
    void shouldSaveClientWriteIdToPOSTResponse() throws ServletException, IOException {
        // given
        final String expectedJsonDto = "expected json dto";
        final ClientRequest expectedClientRequest = ClientTestBuilder.aClient().buildRequest();
        final UUID expected = UUID.fromString("167934e2-3406-4fda-b88e-f28c02083179");

        try (MockedStatic<ClientsServletUtil> mockedStatic = Mockito.mockStatic(ClientsServletUtil.class);
             MockedStatic<ServletUtil> mockedStaticServletUtil = Mockito.mockStatic(ServletUtil.class)) {

            mockedStatic.when(() -> ClientsServletUtil.getPostRequestBodyOrWriteApiErrorToResponse(request, response, gson))
                    .thenReturn(Optional.of(expectedJsonDto));
            mockedStatic.when(() -> ClientsServletUtil.getValidatedClientRequestOrWriteApiErrorToResponse(
                            expectedJsonDto,
                            validator,
                            response,
                            gson))
                    .thenReturn(Optional.of(expectedClientRequest));
            Mockito.when(clientService.save(expectedClientRequest))
                    .thenReturn(expected);

            // when
            clientsServlet.doPost(request, response);

            // then
            mockedStaticServletUtil.verify(() -> ServletUtil.writeJsonToResponse(CREATED, expected, response, gson));
        }
    }

}
