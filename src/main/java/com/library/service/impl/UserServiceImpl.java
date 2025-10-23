package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import com.library.common.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.InitializingBean;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, InitializingBean {

    @Resource
    private UserMapper userMapper;
    
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Page<User> findUsersByPage(PageRequest pageRequest) {
        Page<User> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", pageRequest.getKeyword());
        params.put("sortBy", pageRequest.getSortBy());
        params.put("orderBy", pageRequest.getOrderBy());
        return userMapper.findUsersByPage(page, params);
    }

    @Override
    public User findUserById(Long id) {
        return getById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public boolean saveUser(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDeleted(0);
        return save(user);
    }

    @Override
    public boolean updateUser(User user) {
        user.setUpdateTime(new Date());
        return updateById(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        // 逻辑删除
        User user = new User();
        user.setId(id);
        user.setIsDeleted(1);
        user.setUpdateTime(new Date());
        return updateById(user);
    }

    @Override
    public boolean resetPassword(Long id, String newPassword) {
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(new Date());
        return updateById(user);
    }
    
    @Override
    public void afterPropertiesSet() {
        // 检查是否存在admin用户，不存在则创建
        User adminUser = findByUsername("admin");
        if (adminUser == null) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123")); // 使用BCrypt加密
            user.setNickname("系统管理员");
            user.setEmail("admin@example.com");
            user.setPhone("13800138000");
            user.setStatus(1); // 启用状态
            user.setIsDeleted(0);
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            save(user);
            System.out.println("默认管理员用户创建成功，用户名：admin，密码：admin123");
        } else {
            // 如果用户存在但密码不是BCrypt格式，则更新为BCrypt格式
            if (!adminUser.getPassword().startsWith("$2a$")) {
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setUpdateTime(new Date());
                updateById(adminUser);
                System.out.println("管理员密码格式已更新为BCrypt格式，用户名：admin，密码：admin123");
            }
        }
    }
}