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
    @NotBlank
    private String username;

    @Length(max = 40)
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{3,}@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+")
    private String email;

    @NotBlank
    @Pattern(regexp = "\\S{4,60}")
    private String notHashedPassword;

    @Min(14)
    @Max(120)
    @NotNull
    private Integer age;
}
