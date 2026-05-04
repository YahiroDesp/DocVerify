package kz.docverify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String name,
        @NotBlank @Size(min = 6) String password,
        @Pattern(regexp = "AUTHOR|CONTROLLER|ADMIN") String role
) {}
