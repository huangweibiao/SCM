package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.dto.UserDTO;
import com.scm.entity.SysUser;

/**
 * 用户Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface UserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     */
    PageResult<SysUser> pageQuery(String username, String realName, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 创建用户
     */
    Long createUser(UserDTO userDTO);

    /**
     * 更新用户
     */
    Boolean updateUser(UserDTO userDTO);

    /**
     * 删除用户
     */
    Boolean deleteUser(Long id);

    /**
     * 重置密码
     */
    Boolean resetPassword(Long id, String newPassword);

    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);

    /**
     * 分配角色
     */
    Boolean assignRoles(Long userId, List<Long> roleIds);
}
