package com.authentication.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PermissionDTO {
    private Long id;
    private String name;
    private String description;
}

