package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_permission")
public class Permission {
    private Long id;
    private String name; // 资源名称
    private String key; // 资源KEY
    private Integer type; // 资源类型（1:菜单, 2:按钮）
    private String path; // 资源URI
    private Integer sort; // 排序
    private Integer status; // 状态（0:禁用, 1:启用）
    private Integer isDeleted; // 是否删除
    private String icon; // 图标
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
}