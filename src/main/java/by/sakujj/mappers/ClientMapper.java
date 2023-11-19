package by.sakujj.mappers;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.hashing.Hasher;
import by.sakujj.model.Client;
import lombok.Setter;
import org.mapstruct.Mapper;

@Setter
@Mapper
public abstract class ClientMapper {
    private Hasher hasher;

    public Client fromRequest(ClientRequest request) {
        String email = request.getEmail();
        String username = request.getUsername();
        String password = hasher.hash( request.getNotHashedPassword());
        Integer age = request.getAge();

        return Client.builder()
                .email(email)
                .username(username)
                .password(password)
                .age(age)
                .build();
    }

    public abstract ClientResponse toResponse(Client client);
}
