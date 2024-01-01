package unit.servlet;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.services.ClientService;
import by.sakujj.servlet.ClientsByIdServlet;
import by.sakujj.servlet.util.ClientsServletUtil;
import by.sakujj.servlet.util.ServletUtil;
import com.google.gson.Gson;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;
import util.ClientTestBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static by.sakujj.servlet.ClientsByIdServlet.BASE_URI;
import static by.sakujj.util.HttpStatusCode.NO_CONTENT;
import static by.sakujj.util.HttpStatusCode.OK;

@ExtendWith(MockitoExtension.class)
public class ClientsByIdServletTests {
    @Mock
    private ClientService clientService;
    @Mock
    private Validator validator;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Gson gson;

    private ClientsByIdServlet clientsByIdServlet;

    @BeforeEach
    void beforeEach() {
        clientsByIdServlet = new ClientsByIdServlet();

        Field validatorField = ReflectionUtils.findField(ClientsByIdServlet.class, "validator");
        Field clientServiceField = ReflectionUtils.findField(ClientsByIdServlet.class, "clientService");
        Field gsonField = ReflectionUtils.findField(ClientsByIdServlet.class, "gson");

        ReflectionUtils.setField(validatorField, clientsByIdServlet, validator);
        ReflectionUtils.setField(clientServiceField, clientsByIdServlet, clientService);
        ReflectionUtils.setField(gsonField, clientsByIdServlet, gsonField);
    }


    @Test
    void shouldWriteClientToGETResponse() throws ServletException, IOException {
        // given
        final String reqURI = "req uri";
        final UUID expectedUUID = ClientTestBuilder.aClient().getId();
        final ClientResponse expected = ClientTestBuilder.aClient().buildResponse();

        Mockito.when(request.getRequestURI())
                .thenReturn(reqURI);
        Mockito.when(clientService.findById(expectedUUID))
                .thenReturn(Optional.of(expected));
        try (MockedStatic<ServletUtil> mockedStaticServletUtil = Mockito.mockStatic(ServletUtil.class);
             MockedStatic<ClientsServletUtil> mockedStaticClientServletUtil = Mockito.mockStatic(ClientsServletUtil.class);
        ) {
            mockedStaticClientServletUtil.when(() -> ClientsServletUtil.parseUUIDFromRequestURI(reqURI, BASE_URI))
                    .thenReturn(Optional.of(expectedUUID));

            // when
            clientsByIdServlet.doGet(request, response);

            // then
            mockedStaticServletUtil.verify(() -> ServletUtil.writeJsonToResponse(OK, expected, response, gson));
        }
    }

    @Test
    void shouldSetStatusToPUTResponse() throws ServletException, IOException {
        // given
        final String reqURI = "req uri";
        final UUID expectedUUID = ClientTestBuilder.aClient().getId();
        final String expectedJsonDto = "expected json dto";
        final ClientRequest expectedRequest = ClientTestBuilder.aClient().buildRequest();

        Mockito.when(request.getRequestURI())
                .thenReturn(reqURI);
        try (MockedStatic<ClientsServletUtil> mockedStaticClientServletUtil = Mockito.mockStatic(ClientsServletUtil.class);) {

            mockedStaticClientServletUtil.when(() -> ClientsServletUtil.parseUUIDFromRequestURI(reqURI, BASE_URI))
                    .thenReturn(Optional.of(expectedUUID));
            mockedStaticClientServletUtil.when(() -> ClientsServletUtil.getPostRequestBodyOrWriteApiErrorToResponse(
                            request,
                            response,
                            gson))
                    .thenReturn(Optional.of(expectedJsonDto));
            mockedStaticClientServletUtil.when(() -> ClientsServletUtil.getValidatedClientRequestOrWriteApiErrorToResponse(
                            expectedJsonDto,
                            validator,
                            response,
                            gson))
                    .thenReturn(Optional.of(expectedRequest));
            Mockito.when(clientService.updateById(expectedUUID, expectedRequest))
                    .thenReturn(true);

            // when
            clientsByIdServlet.doPut(request, response);

            // then
            Mockito.verify(response).setStatus(NO_CONTENT);
        }
    }

    @Test
    void shouldSetStatusToDELETEResponse() throws ServletException, IOException {
        // given
        final String reqURI = "req uri";
        final UUID expectedUUID = ClientTestBuilder.aClient().getId();

        Mockito.when(request.getRequestURI())
                .thenReturn(reqURI);
        try (MockedStatic<ClientsServletUtil> mockedStaticClientServletUtil = Mockito.mockStatic(ClientsServletUtil.class);) {

            mockedStaticClientServletUtil.when(() -> ClientsServletUtil.parseUUIDFromRequestURI(reqURI, BASE_URI))
                    .thenReturn(Optional.of(expectedUUID));
            Mockito.when(clientService.deleteById(expectedUUID))
                    .thenReturn(true);

            // when
            clientsByIdServlet.doDelete(request, response);

            // then
            Mockito.verify(response).setStatus(NO_CONTENT);
        }
    }
}
