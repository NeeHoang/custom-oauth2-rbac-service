package com.authentication.service;

import com.authentication.dto.RoleDTO;

public interface IRoleService {

    RoleDTO createNewRole(RoleDTO request);
    RoleDTO updateRole(Long id, RoleDTO request);
    void deleteRole(Long id);
}
