package com.scm.config;

import com.scm.entity.SysUser;
import com.scm.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Custom OAuth2 User Service for SSO integration
 * Maps OAuth2 user to local database user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends OidcUserService {

    private final SysUserRepository sysUserRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Load user from OAuth2 provider
        OidcUser oidcUser = super.loadUser(userRequest);

        // Extract user info
        String username = oidcUser.getPreferredUsername();
        String email = oidcUser.getEmail();
        String oauth2Id = oidcUser.getSubject();
        String provider = userRequest.getClientRegistration().getRegistrationId();

        log.info("OAuth2 user login attempt - username: {}, email: {}, provider: {}", username, email, provider);

        // Find or create local user
        SysUser user = sysUserRepository.findByOauth2IdAndOauth2Provider(oauth2Id, provider)
            .orElseGet(() -> createNewUser(username, email, oauth2Id, provider));

        // Update user info if needed
        updateUserInfoIfNeeded(user, oidcUser);

        // Return custom principal with user details
        return new CustomOidcUser(oidcUser, user);
    }

    private SysUser createNewUser(String username, String email, String oauth2Id, String provider) {
        log.info("Creating new user from OAuth2 - username: {}, provider: {}", username, provider);

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setOauth2Id(oauth2Id);
        user.setOauth2Provider(provider);
        user.setNickname(username);
        user.setStatus(1);
        user.setDeleted(false);

        return sysUserRepository.save(user);
    }

    private void updateUserInfoIfNeeded(SysUser user, OidcUser oidcUser) {
        boolean needsUpdate = false;

        if (oidcUser.getEmail() != null && !oidcUser.getEmail().equals(user.getEmail())) {
            user.setEmail(oidcUser.getEmail());
            needsUpdate = true;
        }

        if (needsUpdate) {
            sysUserRepository.save(user);
            log.info("Updated user info for: {}", user.getUsername());
        }
    }
}
