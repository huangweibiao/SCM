package com.scm.repository;

import com.scm.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 用户角色关联Repository
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
    List<UserRole> findByRoleId(Long roleId);
    void deleteByUserId(Long userId);
}
