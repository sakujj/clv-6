package by.sakujj.servlet;

import by.sakujj.context.Context;
import by.sakujj.dto.ClientResponse;
import by.sakujj.pdf.ReportConfig;
import by.sakujj.pdf.ReportWriter;
import by.sakujj.pdf.TableBuilder;
import by.sakujj.services.ClientService;
import by.sakujj.servlet.util.ClientsServletUtil;

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
    private static final String BOLD_FONT_PATH = "pdf-resources/fonts/Hack-Bold.ttf";
    private static final String REGULAR_FONT_PATH = "pdf-resources/fonts/Hack-Regular.ttf";
    private static final float RIGHT_MARGIN_TO_WIDTH_RATIO = 1 / 8f;
    private static final float LEFT_MARGIN_TO_WIDTH_RATIO = 1 / 8f;
    private static final float TOP_MARGIN_TO_WIDTH_RATIO = 1 / 6f;
    private static final float BOTTOM_MARGIN_TO_WIDTH_RATIO = 1 / 8f;
    private static final String BACKGROUND_PDF_PATH = "pdf-resources/Clevertec_Template.pdf";

    private ClientService clientService;
    private ReportConfig config;

    @Override
    public void init() {
        config = new ReportConfig(
                BOLD_FONT_PATH,
                REGULAR_FONT_PATH
        ).setTopMarginToHeightRatio(TOP_MARGIN_TO_WIDTH_RATIO)
                .setRightMarginToWidthRatio(RIGHT_MARGIN_TO_WIDTH_RATIO)
                .setLeftMarginToWidthRatio(LEFT_MARGIN_TO_WIDTH_RATIO)
                .setBottomMarginToHeightRatio(BOTTOM_MARGIN_TO_WIDTH_RATIO);
        Context context = Context.getInstance();

        clientService = context.getByClass(ClientService.class);
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
