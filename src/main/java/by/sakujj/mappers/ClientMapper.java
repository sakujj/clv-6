package by.sakujj.mappers;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.hashing.Hasher;
import by.sakujj.model.Client;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ClientMapper {
    @Autowired
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
