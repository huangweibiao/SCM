package com.scm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scm.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
