package by.sakujj;

import by.sakujj.context.Context;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.pdf.ClientServiceDecoratedWithReportPDFPrinter;
import by.sakujj.pdf.ReportPDFConfig;
import by.sakujj.services.ClientService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Context ctx = new Context()) {

            ClientService clientService = ctx.getByClass(ClientService.class);
            ClientServiceDecoratedWithReportPDFPrinter clientServiceDecoratedWithReportPdfPrinter = new ClientServiceDecoratedWithReportPDFPrinter(clientService,
                    new ReportPDFConfig(
                            "pdf-resources/fonts/Hack-Bold.ttf",
                            "pdf-resources/fonts/Hack-Regular.ttf"
                    ).setLeftMarginToWidthRatio(1 / 8f)
                            .setRightMarginToWidthRatio(1 / 8f)
                            .setTopMarginToHeightRatio(1 / 6f)
                            .setBottomMarginToHeightRatio(1 / 8f),
                    "pdf-resources/Clevertec_Template.pdf",
                    "pdf-output");


            System.out.println(
                    findById(UUID.fromString("99c0ae88-7acd-4cda-9d59-50e09eede81f"),
                            clientServiceDecoratedWithReportPdfPrinter)
            );

//            System.out.println(
//                    validateThenSave("""
//                            {
//                                "username": "user123",
//                                "email": "q3234x@lgma32il.c3om",
//                                "notHashedPassword": "1111",
//                                "age": 23
//                            }
//                            """, clientServiceDecoratedWithReportPdfPrinter, ctx)
//            );
//
//            System.out.println(
//                    validateThenUpdate(
//                            UUID.fromString("0673979e-3456-4516-a4c9-f9187c471b1b"),
//                            """
//                                    {
//                                         "username": "UPDATED-username",
//                                         "email": "UPDATED-email@mail.com",
//                                         "notHashedPassword": "UPDATED-password",
//                                         "age": 45
//                                    }
//                                    """
//                            , clientServiceDecoratedWithReportPdfPrinter, ctx)
//            );
//            System.out.println("\t\t\t(!)(!)(!)");
//
//            System.out.println(
//                    deleteById(
//                            UUID.fromString("0673979e-3456-4516-a4c9-f9187c471b1b"),
//                            clientServiceDecoratedWithReportPdfPrinter)
//            );
//            System.out.println("\t\t\t(!)(!)(!)");
//
            System.out.println(
                    findAll(clientServiceDecoratedWithReportPdfPrinter)
            );

        }
    }

    public static String findById(UUID id, ClientService service) {

        var found = service.findById(id);
        if (found.isEmpty()) {
            return "";
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(found.get(), ClientResponse.class);
    }

    public static String findAll(ClientService service) {
        var foundList = service.findAll();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(
                foundList,
                new TypeToken<List<ClientResponse>>() {
                }.getType()
        );
    }

    public static String validateThenSave(String jsonRequest, ClientService service, Context ctx) {
        Validator validator = ctx.getByClass(Validator.class);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        ClientRequest clientRequest = gson.fromJson(jsonRequest, ClientRequest.class);
        Set<ConstraintViolation<ClientRequest>> violations = validator.validate(clientRequest);
        if (!violations.isEmpty()) {
            return gson.toJson(violations
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toSet()),
                    new TypeToken<Set<String>>() {
                    }.getType()

            );
        }

        UUID id = service.save(clientRequest);

        return gson.toJson(id, UUID.class);
    }

    public static String validateThenUpdate(UUID id, String jsonRequest, ClientService service, Context ctx) {
        Validator validator = ctx.getByClass(Validator.class);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        ClientRequest clientRequest = gson.fromJson(jsonRequest, ClientRequest.class);
        var violations = validator.validate(clientRequest);
        if (!violations.isEmpty()) {
            return gson.toJson(violations
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toSet()),
                    new TypeToken<Set<String>>() {
                    }.getType()

            );
        }

        boolean isUpdated = service.updateById(id, clientRequest);

        return gson.toJson(isUpdated, Boolean.class);
    }

    public static String deleteById(UUID id, ClientService service) {
        boolean isDeleted = service.deleteById(id);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(isDeleted, Boolean.class);

    }
}