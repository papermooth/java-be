package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_role")
public class Role {
    private Long id;
    private String name;
    private String description;
    private Integer status;
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;
}