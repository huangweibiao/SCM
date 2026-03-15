package com.scm.repository;

import com.scm.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {

    /**
     * Find user by username
     */
    Optional<SysUser> findByUsername(String username);

    /**
     * Find user by OAuth2 ID and provider
     */
    Optional<SysUser> findByOauth2IdAndOauth2Provider(String oauth2Id, String provider);

    /**
     * Find user by email
     */
    Optional<SysUser> findByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
}
