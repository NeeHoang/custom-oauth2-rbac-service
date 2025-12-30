package com.authentication.controller;

import com.authentication.dto.RoleDTO;
import com.authentication.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/role")
@Slf4j
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService iRoleService;

    @PostMapping("/")
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO request) {
        log.info("Role creat successfully {}", request.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iRoleService.createNewRole(request));
    }
}
