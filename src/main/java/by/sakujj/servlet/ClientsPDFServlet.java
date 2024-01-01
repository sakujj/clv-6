package by.sakujj.servlet;

import by.sakujj.context.AppContext;
import by.sakujj.dto.ClientResponse;
import by.sakujj.pdf.ReportConfig;
import by.sakujj.pdf.ReportWriter;
import by.sakujj.pdf.TableBuilder;
import by.sakujj.services.ClientService;
import by.sakujj.servlet.util.ClientsServletUtil;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static by.sakujj.util.HttpStatusCode.*;

@WebServlet("/pdf/clients/*")
public class ClientsPDFServlet extends HttpServlet {
    public static final String BASE_URI = "/pdf/clients/";
    private static final String BACKGROUND_PDF_PATH = "pdf-resources/Clevertec_Template.pdf";

    private ClientService clientService;
    private ReportConfig config;

    @Override
    public void init() throws ServletException {
        ApplicationContext context = AppContext.getInstance();
        clientService = context.getBean(ClientService.class);
        config = context.getBean(ReportConfig.class);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqURI = req.getRequestURI();

        String tableTitle = "GET";

        Optional<UUID> optionalUUID = ClientsServletUtil.parseUUIDFromRequestURI(reqURI, BASE_URI);
        if (optionalUUID.isEmpty()) {
            resp.setStatus(BAD_REQUEST);
            ReportWriter.writePDF(
                    tableTitle,
                    List.of(new TableBuilder()
                            .addRow("Статус", "Некорректный ID")
                            .build()),
                    config,
                    BACKGROUND_PDF_PATH,
                    resp.getOutputStream()
            );
            return;
        }

        UUID requestedUUID = optionalUUID.get();
        Optional<ClientResponse> optionalResponse = clientService.findById(requestedUUID);
        if (optionalResponse.isEmpty()) {
            resp.setStatus(NOT_FOUND);
            ReportWriter.writePDF(
                    tableTitle,
                    List.of(new TableBuilder()
                            .addRow("Статус", "Клиент не найден")
                            .build()),
                    config,
                    BACKGROUND_PDF_PATH,
                    resp.getOutputStream()
            );
            return;
        }

        ClientResponse clientResponse = optionalResponse.get();
        resp.setStatus(OK);
        ReportWriter.writePDF(
                tableTitle,
                List.of(TableBuilder.fromClientResponse(clientResponse)),
                config,
                BACKGROUND_PDF_PATH,
                resp.getOutputStream()
        );
    }

}
