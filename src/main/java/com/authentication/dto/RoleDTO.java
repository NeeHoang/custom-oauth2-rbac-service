package com.authentication.dto;

import lombok.*;
import java.util.Set;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private Set<String> permissions;
}

