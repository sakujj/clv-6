package by.sakujj.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientRequest {

    @Pattern(regexp = "\\S{4,30}")
    private String username;

    @Length(max = 40)
    @Pattern(regexp = "[a-zA-Z]{3,}@[a-zA-Z]+\\.[a-zA-Z]+")
    private String email;

    @Pattern(regexp = "\\S{4,60}")
    private String notHashedPassword;

    @Min(14)
    @Max(120)
    private Integer age;
}
