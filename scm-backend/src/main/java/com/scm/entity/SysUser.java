package com.scm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User Entity for OAuth2 SSO integration
 */
@Data
@Entity
@Table(name = "sys_user")
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    @Column(name = "username", length = 64, unique = true, nullable = false)
    private String username;

    @Column(name = "password", length = 128)
    private String password;

    @Column(name = "nickname", length = 64)
    private String nickname;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "avatar", length = 512)
    private String avatar;

    @Column(name = "status")
    private Integer status = 1;

    @Column(name = "oauth2_id", length = 128)
    private String oauth2Id;

    @Column(name = "oauth2_provider", length = 32)
    private String oauth2Provider;
}
