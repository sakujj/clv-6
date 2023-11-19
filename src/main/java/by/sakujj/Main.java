package by.sakujj;

import by.sakujj.context.Context;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
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

            System.out.println(
                    findById(UUID.fromString("99c0ae88-7acd-4cda-9d59-50e09eede81f"),
                            ctx)
            );

            System.out.println(
                    findById(UUID.fromString("99c0ae88-7acd-4cda-9d59-50e09eede81f"),
                            ctx)
            );

//            System.out.println(
//                    validateThenSave("""
//                            {
//                                "username": "user123",
//                                "email": "emailgmail.com",
//                                "notHashedPassword": "1111",
//                                "age": 23
//                            }
//                            """, ctx)
//            );

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
//                            , ctx)
//            );
//            System.out.println("\t\t\t(!)(!)(!)");

//            System.out.println(
//                    deleteById(
//                            UUID.fromString("0673979e-3456-4516-a4c9-f9187c471b1b"),
//                            ctx)
//            );
//            System.out.println("\t\t\t(!)(!)(!)");

            System.out.println(
                    findAll(ctx)
            );

        }
    }

    public static String findById(UUID id, Context ctx) {
        ClientService service = ctx.getByClass(ClientService.class);

        var found = service.findById(id);
        if (found.isEmpty()) {
            return "";
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(found.get(), ClientResponse.class);
    }

    public static String findAll(Context ctx) {
        ClientService service = ctx.getByClass(ClientService.class);

        var foundList = service.findAll();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(
                foundList,
                new TypeToken<List<ClientResponse>>() {}.getType()
        );
    }

    public static String validateThenSave(String jsonRequest, Context ctx) {
        Validator validator = ctx.getByClass(Validator.class);
        ClientService service = ctx.getByClass(ClientService.class);
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
                    new TypeToken<Set<String>>() {}.getType()

            );
        }

        UUID id = service.save(clientRequest);

        return gson.toJson(id, UUID.class);
    }

    public static String validateThenUpdate(UUID id, String jsonRequest, Context ctx) {
        Validator validator = ctx.getByClass(Validator.class);
        ClientService service = ctx.getByClass(ClientService.class);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        ClientRequest clientRequest = gson.fromJson(jsonRequest, ClientRequest.class);
        var violations =  validator.validate(clientRequest);
        if (!violations.isEmpty()) {
            return gson.toJson(violations
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toSet()),
                    new TypeToken<Set<String>>() {}.getType()

            );
        }

        boolean isUpdated = service.updateById(id, clientRequest);

        return gson.toJson(isUpdated, Boolean.class);
    }

    public static String deleteById(UUID id, Context ctx) {
        ClientService service = ctx.getByClass(ClientService.class);

        boolean isDeleted = service.deleteById(id);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(isDeleted, Boolean.class);

    }
}