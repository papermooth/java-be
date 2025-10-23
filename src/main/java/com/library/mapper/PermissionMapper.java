package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Permission;
import java.util.List;
import java.util.Map;

public interface PermissionMapper extends BaseMapper<Permission> {
    // 分页查询权限列表
    Page<Permission> findPermissionsByPage(Page<Permission> page, Map<String, Object> params);
    List<Permission> findPermissionsByUserId(Long userId);
    int updatePermission(Permission permission);
    int deletePermission(Long id);
}