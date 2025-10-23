package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_user")
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status; // 0:禁用, 1:启用
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;
}