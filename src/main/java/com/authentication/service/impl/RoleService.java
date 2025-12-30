package com.authentication.service.impl;

import com.authentication.dto.RoleDTO;
import com.authentication.entity.Permission;
import com.authentication.entity.Role;
import com.authentication.repository.PermissionRepository;
import com.authentication.repository.RoleRepository;
import com.authentication.service.IRoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public RoleDTO createNewRole(RoleDTO request) {

        Set<Permission> permissions = request.getPermissions().stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Permission name not found: " + name)))
                .collect(Collectors.toSet());

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .permissions(permissions)
                .build();

        Role saved = roleRepository.save(role);
        return mapToDTO(saved);
    }

    @Override
    public RoleDTO updateRole(Long id, RoleDTO request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissions() != null) {
            permissions = request.getPermissions().stream()
                    .map(name -> permissionRepository.findByName(name)
                            .orElseThrow(() -> new EntityNotFoundException("Permission not found with name: " + name)))
                    .collect(Collectors.toSet());
        }
        role.setPermissions(permissions);

        Role updated = roleRepository.save(role);
        return mapToDTO(updated);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        roleRepository.delete(role);
    }

    private RoleDTO mapToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
