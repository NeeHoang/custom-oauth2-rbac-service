package com.authentication.service.impl;

import com.authentication.dto.PermissionDTO;
import com.authentication.entity.Permission;
import com.authentication.repository.PermissionRepository;
import com.authentication.service.IPermissionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionDTO createPermission(PermissionDTO request) {
        Permission permission = Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
        Permission saved = permissionRepository.save(permission);
        return mapToDTO(saved);
    }

    @Override
    public PermissionDTO updatePermission(Long id, PermissionDTO request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        Permission updated = permissionRepository.save(permission);
        return mapToDTO(updated);
    }

    @Override
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));
        permissionRepository.delete(permission);
    }

    private PermissionDTO mapToDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
