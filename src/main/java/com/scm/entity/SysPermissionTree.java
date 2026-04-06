package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * 权限实体类（添加children字段）
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
public class SysPermission extends com.scm.entity.SysPermission {

    /**
     * 子权限列表
     */
    @TableField(exist = false)
    private List<SysPermission> children;
}
