package com.scm.service;

import com.scm.common.AppException;
import com.scm.common.Constants;
import com.scm.entity.User;
import com.scm.entity.UserRole;
import com.scm.repository.UserRepository;
import com.scm.repository.UserRoleRepository;
import com.scm.security.JwtTokenUtil;
import com.scm.util.ParamUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("用户名或密码错误"));

        if (!password.equals(user.getPassword())) {
            throw new AppException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new AppException("用户已被禁用");
        }

        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());

        return result;
    }

    @Override
    public Map<String, Object> getCurrentUser() {
        // 从SecurityContext中获取当前用户ID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("用户不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());

        return result;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, String username) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<User> users = userRepository.findAll();
        if (username != null && !username.isEmpty()) {
            users = users.stream()
                    .filter(u -> u.getUsername().contains(username))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, users.size());
        List<User> pageList = start < users.size() ? users.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", users.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("用户不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        result.put("status", user.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String username = (String) params.get("username");
        if (userRepository.existsByUsername(username)) {
            throw new AppException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword((String) params.get("password"));
        user.setRealName((String) params.get("realName"));
        user.setPhone((String) params.get("phone"));
        user.setEmail((String) params.get("email"));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        user = userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("用户不存在"));

        if (params.containsKey("realName")) {
            user.setRealName((String) params.get("realName"));
        }
        if (params.containsKey("phone")) {
            user.setPhone((String) params.get("phone"));
        }
        if (params.containsKey("email")) {
            user.setEmail((String) params.get("email"));
        }
        if (params.containsKey("status")) {
            user.setStatus(ParamUtils.getInteger(params, "status"));
        }
        if (params.containsKey("password")) {
            user.setPassword((String) params.get("password"));
        }

        userRepository.save(user);

        return Map.of("id", user.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new AppException("用户不存在");
        }
        userRepository.deleteById(id);
        return Map.of("id", id);
    }

    @Override
    @Transactional
    public Map<String, Object> assignRoles(Long userId, List<Long> roleIds) {
        if (!userRepository.existsById(userId)) {
            throw new AppException("用户不存在");
        }

        userRoleRepository.deleteByUserId(userId);

        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleRepository.save(userRole);
        }

        return Map.of("userId", userId);
    }
}
