package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.PageRequest;
import com.library.common.Result;
import com.library.entity.Permission;
import com.library.service.PermissionService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    // 分页查询权限列表
    @PostMapping("/page")
    public Result<Page<Permission>> page(@RequestBody PageRequest pageRequest) {
        Page<Permission> page = permissionService.findPermissionsByPage(pageRequest);
        return Result.success(page);
    }

    // 获取所有权限列表
    @GetMapping("/list")
    public Result<List<Permission>> list() {
        List<Permission> permissions = permissionService.findAllPermissions();
        return Result.success(permissions);
    }

    // 根据ID获取权限
    @GetMapping("/get/{id}")
    public Result<Permission> get(@PathVariable Long id) {
        Permission permission = permissionService.findPermissionById(id);
        if (permission != null) {
            return Result.success(permission);
        } else {
            return Result.error("权限不存在");
        }
    }

    // 新增权限
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody Permission permission) {
        boolean result = permissionService.savePermission(permission);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("保存失败");
        }
    }

    // 更新权限
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Permission permission) {
        boolean result = permissionService.updatePermission(permission);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新失败");
        }
    }

    // 删除权限
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = permissionService.deletePermission(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("删除失败");
        }
    }

    // 批量删除权限
    @DeleteMapping("/batchDelete")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        boolean result = true;
        for (Long id : ids) {
            if (!permissionService.deletePermission(id)) {
                result = false;
                break;
            }
        }
        return result ? Result.success(true) : Result.error("批量删除失败");
    }

    // 根据用户ID获取权限
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getUserPermissions(@PathVariable Long userId) {
        List<Permission> permissions = permissionService.findPermissionsByUserId(userId);
        return Result.success(permissions);
    }
}