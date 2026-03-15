package com.scm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 Authentication Success Handler
 * Handles successful login and returns user info or redirects
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        log.info("OAuth2 authentication successful for user: {}", authentication.getName());

        // Check if this is an API request
        String acceptHeader = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");

        if ((acceptHeader != null && acceptHeader.contains("application/json"))
                || "XMLHttpRequest".equals(requestedWith)) {
            // Return JSON response for API requests
            handleApiRequest(response, authentication);
        } else {
            // Redirect to frontend dashboard for browser requests
            response.sendRedirect("/dashboard");
        }
    }

    private void handleApiRequest(HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Authentication successful");

        if (authentication.getPrincipal() instanceof CustomOidcUser oidcUser) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", oidcUser.getLocalUser().getId());
            userInfo.put("username", oidcUser.getLocalUser().getUsername());
            userInfo.put("email", oidcUser.getLocalUser().getEmail());
            userInfo.put("nickname", oidcUser.getLocalUser().getNickname());
            userInfo.put("avatar", oidcUser.getLocalUser().getAvatar());
            result.put("user", userInfo);
        }

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
