package by.sakujj.model;


import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Client implements Entity<Client, UUID> {
    private UUID id;

    private String username;
    private String email;
    private String password;
    private Integer age;
}