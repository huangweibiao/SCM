package com.scm.config;

import com.scm.entity.SysUser;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collections;
import java.util.List;

/**
 * Custom OIDC User principal that wraps OidcUser and includes local user info
 */
@Getter
public class CustomOidcUser implements OidcUser {

    private final OidcUser delegate;
    private final SysUser localUser;

    public CustomOidcUser(OidcUser delegate, SysUser localUser) {
        this.delegate = delegate;
        this.localUser = localUser;
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return localUser.getUsername();
    }

    // Delegate all other OidcUser methods
    @Override
    public java.util.Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    @Override
    public org.springframework.security.oauth2.core.oidc.IdToken getIdToken() {
        return delegate.getIdToken();
    }

    @Override
    public org.springframework.security.oauth2.core.oidc.UserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public String getSubject() {
        return delegate.getSubject();
    }

    @Override
    public String getPreferredUsername() {
        return delegate.getPreferredUsername();
    }

    @Override
    public String getGivenName() {
        return delegate.getGivenName();
    }

    @Override
    public String getFamilyName() {
        return delegate.getFamilyName();
    }

    @Override
    public String getFullName() {
        return delegate.getFullName();
    }

    @Override
    public String getMiddleName() {
        return delegate.getMiddleName();
    }

    @Override
    public String getNickname() {
        return localUser.getNickname() != null ? localUser.getNickname() : delegate.getNickname();
    }

    @Override
    public String getProfile() {
        return delegate.getProfile();
    }

    @Override
    public String getPicture() {
        return localUser.getAvatar() != null ? localUser.getAvatar() : delegate.getPicture();
    }

    @Override
    public String getWebsite() {
        return delegate.getWebsite();
    }

    @Override
    public String getEmail() {
        return localUser.getEmail() != null ? localUser.getEmail() : delegate.getEmail();
    }

    @Override
    public Boolean getEmailVerified() {
        return delegate.getEmailVerified();
    }

    @Override
    public String getGender() {
        return delegate.getGender();
    }

    @Override
    public String getBirthdate() {
        return delegate.getBirthdate();
    }

    @Override
    public String getZoneInfo() {
        return delegate.getZoneInfo();
    }

    @Override
    public String getLocale() {
        return delegate.getLocale();
    }

    @Override
    public String getPhoneNumber() {
        return localUser.getPhone() != null ? localUser.getPhone() : delegate.getPhoneNumber();
    }

    @Override
    public Boolean getPhoneNumberVerified() {
        return delegate.getPhoneNumberVerified();
    }

    @Override
    public String getUpdatedAt() {
        return delegate.getUpdatedAt();
    }

    @Override
    public String getAddress() {
        return delegate.getAddress();
    }

    @Override
    public java.util.Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }
}
