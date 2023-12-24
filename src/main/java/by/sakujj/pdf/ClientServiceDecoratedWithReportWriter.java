package by.sakujj.pdf;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.services.ClientService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class ClientServiceDecoratedWithReportWriter implements ClientService {
    private final ClientService clientService;
    private final ReportConfig config;
    private final String backgroundPdfPath;
    private final String documentSaveDirectory;

    public ClientServiceDecoratedWithReportWriter(ClientService clientService,
                                                  ReportConfig config,
                                                  String backgroundPdfPath,
                                                  String documentSaveDirectory) {
        this.clientService = clientService;
        this.config = config;
        this.backgroundPdfPath = backgroundPdfPath;

        File theDir = new File(documentSaveDirectory);
        if (!theDir.exists()) {
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

        var table = TableBuilder.fromClientRequest(request);
        table.add(Map.entry("Сгенерированный ID", generatedId.toString()));

        String methodName = "SAVE";
        String documentSavePath = getDocumentSavePath(methodName, documentSaveDirectory);

        try (OutputStream outputStream = new FileOutputStream(documentSavePath)) {
            ReportWriter.writePDF(methodName, List.of(table), config, backgroundPdfPath, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return generatedId;
    }

    private static String getDocumentSavePath(String methodName, String documentSaveDirectory) {
        return (documentSaveDirectory + "/"
                + methodName
                + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                + ".pdf").replaceAll("\\s", "-");
    }

    @Override
    public boolean updateById(UUID id, ClientRequest request) {
        boolean isUpdated = clientService.updateById(id, request);

        var table = TableBuilder.fromClientRequest(request);
        table.add(Map.entry("Информация обновлена", String.valueOf(isUpdated)));

        String methodName = "UPDATE";
        String documentSavePath = getDocumentSavePath(methodName, documentSaveDirectory);

        try (OutputStream outputStream = new FileOutputStream(documentSavePath)) {
            ReportWriter.writePDF(methodName, List.of(table), config, backgroundPdfPath, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return isUpdated;
    }

    @Override
    public List<ClientResponse> findAll() {
        List<ClientResponse> foundClients = clientService.findAll();

        List<List<Map.Entry<String, String>>> tables = new ArrayList<>();
        for (ClientResponse response : foundClients) {
            tables.add(TableBuilder.fromClientResponse(response));
        }

        if (tables.isEmpty()) {
            tables.add(List.of(Map.entry("Статус", "Клиенты не найдены")));
        }

        String methodName = "FIND ALL";
        String documentSavePath = getDocumentSavePath(methodName, documentSaveDirectory);

        try (OutputStream outputStream = new FileOutputStream(documentSavePath)) {
            ReportWriter.writePDF(methodName, tables, config, backgroundPdfPath, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return foundClients;
    }

    @Override
    public List<ClientResponse> findByPageWithSize(int page, int size) {
        List<ClientResponse> foundClients = clientService.findByPageWithSize(page, size);

        List<List<Map.Entry<String, String>>> tables = new ArrayList<>();
        for (ClientResponse response : foundClients) {
            tables.add(TableBuilder.fromClientResponse(response));
        }

        if (tables.isEmpty()) {
            tables.add(List.of(Map.entry("Статус", "Клиенты не найдены")));
        }

        String methodName = "FIND ALL BY PAGE=" + page + " AND PAGE_SIZE=" + size;
        String documentSavePath = getDocumentSavePath(methodName, documentSaveDirectory);

        try (OutputStream outputStream = new FileOutputStream(documentSavePath)) {
            ReportWriter.writePDF(methodName, tables, config, backgroundPdfPath, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return foundClients;
    }

    @Override
    public Optional<ClientResponse> findById(UUID id) {
        Optional<ClientResponse> found = clientService.findById(id);

        var table = new TableBuilder()
                .addRow("ID", id)
                .build();

        if (found.isEmpty()) {
            table.add(Map.entry("Статус", "Клиент не был найден"));
        } else {
            table = TableBuilder.fromClientResponse(found.get());
        }

        String methodName = "FIND BY ID";
        String documentSavePath = getDocumentSavePath(methodName, documentSaveDirectory);

        try (OutputStream outputStream = new FileOutputStream(documentSavePath)) {
            ReportWriter.writePDF(methodName, List.of(table), config, backgroundPdfPath, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return found;
    }


    @Override
    public Optional<ClientResponse> findByEmail(String email) {
        Optional<ClientResponse> found = clientService.findByEmail(email);

        var table = new TableBuilder()
                .addRow("Email", email)
                .build();

        if (found.isEmpty()) {
            table.add(Map.entry("Статус", "Клиент не был найден"));
        } else {
            table = TableBuilder.fromClientResponse(found.get());
        }

        String methodName = "FIND BY EMAIL";
        String documentSavePath = getDocumentSavePath(methodName, documentSaveDirectory);

        try (OutputStream outputStream = new FileOutputStream(documentSavePath)) {
            ReportWriter.writePDF(methodName, List.of(table), config, backgroundPdfPath, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return found;
    }

    @Override
    public boolean deleteById(UUID id) {
        boolean isDeleted = clientService.deleteById(id);

        var table = new TableBuilder()
                .addRow("ID", id.toString())
                .addRow("Информация удалена", String.valueOf(isDeleted))
                .build();

        String methodName = "DELETE";
        String documentSavePath = getDocumentSavePath(methodName, documentSaveDirectory);

        try (OutputStream outputStream = new FileOutputStream(documentSavePath)) {
            ReportWriter.writePDF(methodName, List.of(table), config, backgroundPdfPath, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return isDeleted;
    }
}
