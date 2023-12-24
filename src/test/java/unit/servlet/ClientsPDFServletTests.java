package unit.servlet;

import by.sakujj.context.Context;
import by.sakujj.dto.ClientResponse;
import by.sakujj.pdf.ReportConfig;
import by.sakujj.pdf.ReportWriter;
import by.sakujj.pdf.TableBuilder;
import by.sakujj.services.ClientService;
import by.sakujj.servlet.ClientsPDFServlet;
import by.sakujj.servlet.util.ClientsServletUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ClientTestBuilder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static by.sakujj.util.HttpStatusCode.*;

@ExtendWith(MockitoExtension.class)
public class ClientsPDFServletTests {
    @Mock
    private Context context;
    @Mock
    private ClientService clientService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletOutputStream servletOutputStream;

    private ReportConfig reportConfig;
    @Spy
    private ClientsPDFServlet clientsPDFServlet;

    @BeforeEach
    void beforeEach() throws ServletException, NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<Context> mockedStatic = Mockito.mockStatic(Context.class)) {

            mockedStatic.when(Context::getInstance)
                    .thenReturn(context);

            Mockito.when(context.getByClass(ClientService.class))
                    .thenReturn(clientService);

            clientsPDFServlet.init();

            Field configField = ClientsPDFServlet.class.getDeclaredField("config");
            configField.setAccessible(true);
            reportConfig = (ReportConfig) configField.get(clientsPDFServlet);
        }
    }

    @Test
    void shouldSetStatusAndWritePDFToGETResponse() throws IOException, ServletException {
        // given
        final String tableTitle = "GET";
        final String reqURI = "request uri";
        final List<Map.Entry<String, String>> expectedTable = List.of(
                Map.entry("key", "val"),
                Map.entry("key2", "val2"),
                Map.entry("key2xxxxx", "val2xxx")
        );
        final ClientResponse expectedResponse = ClientTestBuilder.aClient().buildResponse();
        final UUID expectedId = expectedResponse.getId();

        try (MockedStatic<ClientsServletUtil> mockedStaticClientsServletUtil = Mockito.mockStatic(ClientsServletUtil.class);
             MockedStatic<ReportWriter> mockedStaticReportWriter = Mockito.mockStatic(ReportWriter.class);
             MockedStatic<TableBuilder> mockedStaticTableBuilder = Mockito.mockStatic(TableBuilder.class)
        ) {
            Mockito.when(request.getRequestURI())
                    .thenReturn(reqURI);
            mockedStaticClientsServletUtil.when(() -> ClientsServletUtil.parseUUIDFromRequestURI(
                            reqURI,
                            ClientsPDFServlet.BASE_URI))
                    .thenReturn(Optional.of(expectedId));
            Mockito.when(clientService.findById(expectedId))
                    .thenReturn(Optional.of(expectedResponse));
            mockedStaticTableBuilder.when(() -> TableBuilder.fromClientResponse(expectedResponse))
                    .thenReturn(expectedTable);
            Mockito.when(response.getOutputStream())
                    .thenReturn(servletOutputStream);

            // when
            clientsPDFServlet.doGet(request, response);

            // then
            mockedStaticReportWriter.verify(() -> ReportWriter.writePDF(
                    Mockito.eq(tableTitle),
                    Mockito.eq(List.of(expectedTable)),
                    Mockito.eq(reportConfig),
                    Mockito.anyString(),
                    Mockito.same(servletOutputStream)
            ));
            Mockito.verify(response).setStatus(OK);
        }
    }

    @Test
    void shouldWritePDFErrorToGETResponseWhenClientIsNotFound() throws IOException, ServletException {
        // given
        final String tableTitle = "GET";
        final String reqURI = "request uri";
        final List<Map.Entry<String, String>> expectedTable = List.of(
                Map.entry("key", "val"),
                Map.entry("key2", "val2"),
                Map.entry("key2xxxxx", "val2xxx")
        );
        final ClientResponse expectedResponse = ClientTestBuilder.aClient().buildResponse();
        final UUID expectedId = expectedResponse.getId();

        try (MockedStatic<ClientsServletUtil> mockedStaticClientsServletUtil = Mockito.mockStatic(ClientsServletUtil.class);
             MockedStatic<ReportWriter> mockedStaticReportWriter = Mockito.mockStatic(ReportWriter.class);
             MockedStatic<TableBuilder> mockedStaticTableBuilder = Mockito.mockStatic(TableBuilder.class)
        ) {
            Mockito.when(request.getRequestURI())
                    .thenReturn(reqURI);
            mockedStaticClientsServletUtil.when(() -> ClientsServletUtil.parseUUIDFromRequestURI(
                            reqURI,
                            ClientsPDFServlet.BASE_URI))
                    .thenReturn(Optional.of(expectedId));
            Mockito.when(clientService.findById(expectedId))
                    .thenReturn(Optional.empty());
            Mockito.when(response.getOutputStream())
                    .thenReturn(servletOutputStream);

            // when
            clientsPDFServlet.doGet(request, response);

            // then
            mockedStaticReportWriter.verify(() -> ReportWriter.writePDF(
                    Mockito.eq(tableTitle),
                    Mockito.eq(List.of(
                            new TableBuilder()
                                    .addRow("Статус", "Клиент не найден")
                                    .build())
                    ),
                    Mockito.eq(reportConfig),
                    Mockito.anyString(),
                    Mockito.same(servletOutputStream)
            ));
            Mockito.verify(response).setStatus(NOT_FOUND);
        }
    }

    @Test
    void shouldWritePDFErrorToGETResponseWithMalformedId() throws IOException, ServletException {
        // given
        final String tableTitle = "GET";
        final String reqURI = "request uri";

        try (MockedStatic<ClientsServletUtil> mockedStaticClientsServletUtil = Mockito.mockStatic(ClientsServletUtil.class);
             MockedStatic<ReportWriter> mockedStaticReportWriter = Mockito.mockStatic(ReportWriter.class);
        ) {
            Mockito.when(request.getRequestURI())
                    .thenReturn(reqURI);
            mockedStaticClientsServletUtil.when(() -> ClientsServletUtil.parseUUIDFromRequestURI(
                            reqURI,
                            ClientsPDFServlet.BASE_URI))
                    .thenReturn(Optional.empty());
            Mockito.when(response.getOutputStream())
                    .thenReturn(servletOutputStream);

            // when
            clientsPDFServlet.doGet(request, response);

            // then
            mockedStaticReportWriter.verify(() -> ReportWriter.writePDF(
                    Mockito.eq(tableTitle),
                    Mockito.eq(List.of(new TableBuilder()
                            .addRow("Статус", "Некорректный ID")
                            .build())),
                    Mockito.eq(reportConfig),
                    Mockito.anyString(),
                    Mockito.same(servletOutputStream)
            ));
            Mockito.verify(response).setStatus(BAD_REQUEST);
        }
    }
}
