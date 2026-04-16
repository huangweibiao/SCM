package com.scm.repository;

import com.scm.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 权限Repository
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionCode(String permissionCode);
    List<Permission> findByParentId(Long parentId);
    List<Permission> findByStatus(Integer status);
}
