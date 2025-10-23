package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Permission;
import com.library.common.PageRequest;
import java.util.List;

public interface PermissionService extends IService<Permission> {
    List<Permission> findAllPermissions();
    Page<Permission> findPermissionsByPage(PageRequest pageRequest);
    Permission findPermissionById(Long id);
    boolean savePermission(Permission permission);
    boolean updatePermission(Permission permission);
    boolean deletePermission(Long id);
    List<Permission> findPermissionsByUserId(Long userId);
}