package com.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String userName;

    @NotBlank
    private String fullName;

    @NotBlank
    private String passWord;
}

