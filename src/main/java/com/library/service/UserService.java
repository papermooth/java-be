package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.User;
import com.library.common.PageRequest;

public interface UserService extends IService<User> {
    Page<User> findUsersByPage(PageRequest pageRequest);
    User findUserById(Long id);
    User findByUsername(String username);
    boolean saveUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(Long id);
    boolean resetPassword(Long id, String newPassword);
}