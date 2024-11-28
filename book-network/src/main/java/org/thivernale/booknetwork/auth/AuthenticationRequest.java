package org.thivernale.booknetwork.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "Email should not be empty")
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Password should not be empty")
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, message = "Password should be at least {min} characters")
    private String password;
}
