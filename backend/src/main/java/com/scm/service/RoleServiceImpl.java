package com.scm.service;

import com.scm.common.AppException;
import com.scm.entity.Role;
import com.scm.entity.RolePermission;
import com.scm.repository.RoleRepository;
import com.scm.repository.RolePermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, String roleName) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<Role> roles = roleRepository.findAll();
        if (roleName != null && !roleName.isEmpty()) {
            roles = roles.stream()
                    .filter(r -> r.getRoleName().contains(roleName))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, roles.size());
        List<Role> pageList = start < roles.size() ? roles.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", roles.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException("角色不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", role.getId());
        result.put("roleCode", role.getRoleCode());
        result.put("roleName", role.getRoleName());
        result.put("description", role.getDescription());
        result.put("status", role.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String roleCode = (String) params.get("roleCode");
        if (roleRepository.existsByRoleCode(roleCode)) {
            throw new AppException("角色编码已存在");
        }

        Role role = new Role();
        role.setRoleCode(roleCode);
        role.setRoleName((String) params.get("roleName"));
        role.setDescription((String) params.get("description"));
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());

        role = roleRepository.save(role);

        Map<String, Object> result = new HashMap<>();
        result.put("id", role.getId());
        result.put("roleCode", role.getRoleCode());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException("角色不存在"));

        if (params.containsKey("roleName")) {
            role.setRoleName((String) params.get("roleName"));
        }
        if (params.containsKey("description")) {
            role.setDescription((String) params.get("description"));
        }
        if (params.containsKey("status")) {
            role.setStatus((Integer) params.get("status"));
        }

        roleRepository.save(role);

        return Map.of("id", role.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new AppException("角色不存在");
        }
        roleRepository.deleteById(id);
        return Map.of("id", id);
    }

    @Override
    public List<Long> getPermissionIds(Long roleId) {
        List<RolePermission> permissions = rolePermissionRepository.findByRoleId(roleId);
        return permissions.stream()
                .map(RolePermission::getPermissionId)
                .toList();
    }

    @Override
    @Transactional
    public Map<String, Object> assignPermissions(Long roleId, List<Long> permissionIds) {
        if (!roleRepository.existsById(roleId)) {
            throw new AppException("角色不存在");
        }

        rolePermissionRepository.deleteByRoleId(roleId);

        for (Long permissionId : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rolePermissionRepository.save(rp);
        }

        return Map.of("roleId", roleId);
    }
}
