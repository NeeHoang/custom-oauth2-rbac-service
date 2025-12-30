package com.authentication.service;

import com.authentication.dto.PermissionDTO;

public interface IPermissionService {
    PermissionDTO createPermission(PermissionDTO request);
    PermissionDTO updatePermission(Long id, PermissionDTO request);
    void deletePermission(Long id);
}
