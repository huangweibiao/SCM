package com.scm.repository;

import com.scm.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 角色权限关联Repository
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Long roleId);
    List<RolePermission> findByPermissionId(Long permissionId);
    void deleteByRoleId(Long roleId);
}
