package com.authentication.controller;

import com.authentication.dto.PermissionDTO;
import com.authentication.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/permission")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {

    private final IPermissionService iPermissionService;

    @PostMapping("/")
    public ResponseEntity<PermissionDTO> createPermission(@RequestBody PermissionDTO permission) {
        log.info("Create Permission successfully with name: {}", permission.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iPermissionService.createPermission(permission));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionDTO> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO permission) {
        log.info("Update Permission successfully with name: {}", permission.getName());
        return ResponseEntity.status(HttpStatus.OK)
                .body(iPermissionService.updatePermission(id, permission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable Long id) {
        log.info("Delete Permission successfully with id: {}", id);
        iPermissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Delete Permission successfully");
    }
}
