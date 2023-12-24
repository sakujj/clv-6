package by.sakujj.services;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientService {
    UUID save(ClientRequest request);

    boolean updateById(UUID id, ClientRequest request);

    List<ClientResponse> findAll();

    List<ClientResponse> findByPageWithSize(int page, int size);

    Optional<ClientResponse> findById(UUID id);

    Optional<ClientResponse> findByEmail(String email);

    boolean deleteById(UUID id);
}
