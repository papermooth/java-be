package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.PageRequest;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    // 分页查询用户列表
    @PostMapping("/page")
    public Result<Page<User>> page(@RequestBody PageRequest pageRequest) {
        Page<User> page = userService.findUsersByPage(pageRequest);
        return Result.success(page);
    }

    // 根据ID获取用户
    @GetMapping("/get/{id}")
    public Result<User> get(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            // 隐藏密码
            user.setPassword(null);
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    // 新增用户
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody User user) {
        // 检查用户名是否已存在
        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }
        boolean result = userService.saveUser(user);
        return result ? Result.success(true) : Result.error("保存失败");
    }

    // 更新用户
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody User user) {
        boolean result = userService.updateUser(user);
        return result ? Result.success(true) : Result.error("更新失败");
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = userService.deleteUser(id);
        return result ? Result.success(true) : Result.error("删除失败");
    }

    // 批量删除用户
    @DeleteMapping("/batchDelete")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        boolean result = true;
        for (Long id : ids) {
            if (!userService.deleteUser(id)) {
                result = false;
                break;
            }
        }
        return result ? Result.success(true) : Result.error("批量删除失败");
    }

    // 重置密码
    @PutMapping("/resetPassword/{id}")
    public Result<Boolean> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        boolean result = userService.resetPassword(id, newPassword);
        return result ? Result.success(true) : Result.error("重置密码失败");
    }

    // 修改用户状态
    @PutMapping("/updateStatus/{id}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        User user = new User();
        user.setId(id);
        user.setStatus(request.get("status"));
        boolean result = userService.updateUser(user);
        return result ? Result.success(true) : Result.error("更新状态失败");
    }
}