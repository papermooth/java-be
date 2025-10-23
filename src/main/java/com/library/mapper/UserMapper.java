package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.User;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {
    Page<User> findUsersByPage(Page<User> page, Map<String, Object> params);
    User findByUsername(String username);
    int updateUser(User user);
    int deleteUser(Long id);
}