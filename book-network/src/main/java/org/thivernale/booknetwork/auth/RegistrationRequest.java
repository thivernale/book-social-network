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
public class RegistrationRequest {
    @NotBlank(message = "Firstname should not be empty")
    @NotEmpty(message = "Firstname should not be empty")
    private String firstname;
    @NotBlank(message = "Lastname should not be empty")
    @NotEmpty(message = "Lastname should not be empty")
    private String lastname;
    @NotBlank(message = "Email should not be empty")
    @NotEmpty(message = "Email( should not be empty")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Password should not be empty")
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, message = "Password should be at least {min} characters")
    private String password;
}
