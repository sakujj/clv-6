package by.sakujj.pdf;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.services.ClientService;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class ClientServiceDecoratedWithReportPDFPrinter implements ClientService {
    private final ClientService clientService;
    private final ReportPDFConfig reportPDFConfig;
    private final String backgroundPdfPath;
    private final String documentSaveDirectory;

    public ClientServiceDecoratedWithReportPDFPrinter(ClientService clientService,
                                                      ReportPDFConfig reportPDFConfig,
                                                      String backgroundPdfPath,
                                                      String documentSaveDirectory) {
        this.clientService = clientService;
        this.reportPDFConfig = reportPDFConfig;
        this.backgroundPdfPath = backgroundPdfPath;

        File theDir = new File(documentSaveDirectory);
        System.out.println(theDir.toPath());
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        String normalizedSaveDirectory = documentSaveDirectory;
        if (documentSaveDirectory.endsWith("/")) {
            normalizedSaveDirectory = documentSaveDirectory.substring(0, documentSaveDirectory.length() - 1);
        }
        this.documentSaveDirectory = normalizedSaveDirectory;
    }

    @Override
    public UUID save(ClientRequest request) {
        UUID generatedId = clientService.save(request);

        String reportInfo =
                """
                        ClientRequest:
                        |||   username: %s,
                        |||   email: %s,
                        |||   not hashed password: %s,
                        |||   age: %s.
                        Сгенерированный DAO id: %s.
                        """.formatted(
                        request.getUsername(),
                        request.getEmail(),
                        request.getNotHashedPassword(),
                        request.getAge(),
                        generatedId);
        ReportPDF reportPDF = new ReportPDF(
                "SAVE",
                List.of(reportInfo),
                reportPDFConfig);

        ReportPDFPrinter.printToPDF(reportPDF, backgroundPdfPath, getDocumentSavePath(reportPDF));

        return generatedId;
    }

    private String getDocumentSavePath(ReportPDF reportPDF) {
        return documentSaveDirectory + "/"
                + reportPDF.getMethodName().replace(' ', '-')
                + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                + ".pdf";
    }

    @Override
    public boolean updateById(UUID id, ClientRequest request) {
        boolean isUpdated = clientService.updateById(id, request);

        String reportInfo =
                """
                        Id: %s,
                        ClientRequest:
                        |||   username: %s,
                        |||   email: %s,
                        |||   not hashed password: %s,
                        |||   age: %s.
                        Обновление произведено успешно: %s.
                        """.formatted(
                        id.toString(),
                        request.getUsername(),
                        request.getEmail(),
                        request.getNotHashedPassword(),
                        request.getAge(),
                        String.valueOf(isUpdated));
        ReportPDF reportPDF = new ReportPDF(
                "UPDATE BY ID",
                List.of(reportInfo),
                reportPDFConfig);

        ReportPDFPrinter.printToPDF(reportPDF, backgroundPdfPath, getDocumentSavePath(reportPDF));

        return isUpdated;
    }

    @Override
    public List<ClientResponse> findAll() {
        List<ClientResponse> foundClients = clientService.findAll();

        List<String> reportInfoList = new ArrayList<>(foundClients.size() + 1);
        reportInfoList.add("Всего клиентов найдено: " + foundClients.size() + ".");
        for (int i = 0; i < foundClients.size(); i++) {
            ClientResponse c = foundClients.get(i);
            reportInfoList.add(
                    """
                            ClientResponse  #%s:
                            |||   id: %s,
                            |||   username: %s,
                            |||   email: %s,
                            |||   age: %s.
                            """.formatted(
                            String.valueOf(i),
                            c.getId().toString(),
                            c.getUsername(),
                            c.getEmail(),
                            c.getAge())
            );
        }

        ReportPDF reportPDF = new ReportPDF(
                "FIND ALL",
                reportInfoList,
                reportPDFConfig);

        ReportPDFPrinter.printToPDF(reportPDF, backgroundPdfPath, getDocumentSavePath(reportPDF));

        return foundClients;
    }

    @Override
    public Optional<ClientResponse> findById(UUID id) {
        Optional<ClientResponse> found = clientService.findById(id);

        String reportInfo = "Клиент с id \"%s\" не был найден.".formatted(id.toString());

        if (found.isPresent()) {
            ClientResponse c = found.get();
            reportInfo =
                    """
                            ClientResponse:
                            |||   id: %s,
                            |||   username: %s,
                            |||   email: %s,
                            |||   age: %s.
                            """.formatted(
                            c.getId().toString(),
                            c.getUsername(),
                            c.getEmail(),
                            c.getAge());
        }

        ReportPDF reportPDF = new ReportPDF(
                "FIND BY ID",
                List.of(reportInfo),
                reportPDFConfig);

        ReportPDFPrinter.printToPDF(reportPDF, backgroundPdfPath, getDocumentSavePath(reportPDF));

        return found;
    }


    @Override
    public Optional<ClientResponse> findByEmail(String email) {
        Optional<ClientResponse> found = clientService.findByEmail(email);

        String reportInfo = "Клиент с email \"%s\" не был найден.".formatted(email);

        if (found.isPresent()) {
            ClientResponse c = found.get();
            reportInfo =
                    """
                            ClientResponse:
                            |||   id: %s,
                            |||   username: %s,
                            |||   email: %s,
                            |||   age: %s.
                            """.formatted(
                            c.getId().toString(),
                            c.getUsername(),
                            c.getEmail(),
                            c.getAge());
        }

        ReportPDF reportPDF = new ReportPDF(
                "FIND BY EMAIL",
                List.of(reportInfo),
                reportPDFConfig);

        ReportPDFPrinter.printToPDF(reportPDF, backgroundPdfPath, getDocumentSavePath(reportPDF));

        return found;
    }

    @Override
    public boolean deleteById(UUID id) {
        boolean isDeleted = clientService.deleteById(id);

        String reportInfo =
                """
                        Id: %s.
                        Удаление произведено успешно: %s.
                        """.formatted(
                        id.toString(),
                        String.valueOf(isDeleted));
        ReportPDF reportPDF = new ReportPDF(
                "DELETE BY ID",
                List.of(reportInfo),
                reportPDFConfig);

        ReportPDFPrinter.printToPDF(reportPDF, backgroundPdfPath, getDocumentSavePath(reportPDF));


        return isDeleted;
    }
}
