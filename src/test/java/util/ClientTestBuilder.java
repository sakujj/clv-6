package util;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.model.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.UUID;

@With
@Getter
@NoArgsConstructor(staticName = "aClient")
@AllArgsConstructor
public class ClientTestBuilder implements TestBuilder<Client> {
    private UUID id = UUID.fromString("af9a69ff-6c99-4cef-a7d8-1436a775f042");
    private String username = "test client username";
    private String email = "AHMET_ZOGU@mail.ru";
    private String notHashedPassword = "ahmet_zogu";
    private String password = "$2a$10$eb.ea2iUVJhXIwPscv3as.5KKtfrGJEXME4ZGyzUbubnqLr7D3kP.";
    private int age = 54;

    public ClientRequest buildRequest() {
        return ClientRequest.builder()
                .notHashedPassword(notHashedPassword)
                .email(email)
                .age(age)
                .username(username)
                .build();
    }

    public ClientResponse buildResponse() {
        return ClientResponse.builder()
                .id(id)
                .username(username)
                .email(email)
                .age(age)
                .build();
    }

    @Override
    public Client build() {
        return Client.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(notHashedPassword)
                .age(age)
                .build();
    }
}
