package com.scm.service;

import com.scm.common.AppException;
import com.scm.entity.Permission;
import com.scm.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, String permissionName) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<Permission> permissions = permissionRepository.findAll();
        if (permissionName != null && !permissionName.isEmpty()) {
            permissions = permissions.stream()
                    .filter(p -> p.getPermissionName().contains(permissionName))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, permissions.size());
        List<Permission> pageList = start < permissions.size() ? permissions.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", permissions.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public List<Map<String, Object>> tree() {
        List<Permission> allPermissions = permissionRepository.findByStatus(1);
        return buildTree(allPermissions, 0L);
    }

    private List<Map<String, Object>> buildTree(List<Permission> permissions, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Permission permission : permissions) {
            if (Objects.equals(permission.getParentId(), parentId)) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", permission.getId());
                node.put("permissionCode", permission.getPermissionCode());
                node.put("permissionName", permission.getPermissionName());
                node.put("parentId", permission.getParentId());
                node.put("permissionType", permission.getPermissionType());
                node.put("path", permission.getPath());
                node.put("component", permission.getComponent());
                node.put("icon", permission.getIcon());
                node.put("sort", permission.getSort());

                List<Map<String, Object>> children = buildTree(permissions, permission.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }
                tree.add(node);
            }
        }
        return tree;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new AppException("权限不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", permission.getId());
        result.put("permissionCode", permission.getPermissionCode());
        result.put("permissionName", permission.getPermissionName());
        result.put("parentId", permission.getParentId());
        result.put("permissionType", permission.getPermissionType());
        result.put("path", permission.getPath());
        result.put("component", permission.getComponent());
        result.put("icon", permission.getIcon());
        result.put("sort", permission.getSort());
        result.put("status", permission.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        Permission permission = new Permission();
        permission.setPermissionCode((String) params.get("permissionCode"));
        permission.setPermissionName((String) params.get("permissionName"));
        permission.setParentId(params.get("parentId") != null ? (Long) params.get("parentId") : 0L);
        permission.setPermissionType((String) params.get("permissionType"));
        permission.setPath((String) params.get("path"));
        permission.setComponent((String) params.get("component"));
        permission.setIcon((String) params.get("icon"));
        permission.setSort(params.get("sort") != null ? (Integer) params.get("sort") : 0);
        permission.setStatus(1);
        permission.setCreateTime(LocalDateTime.now());

        permission = permissionRepository.save(permission);

        Map<String, Object> result = new HashMap<>();
        result.put("id", permission.getId());
        result.put("permissionCode", permission.getPermissionCode());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new AppException("权限不存在"));

        if (params.containsKey("permissionName")) {
            permission.setPermissionName((String) params.get("permissionName"));
        }
        if (params.containsKey("parentId")) {
            permission.setParentId((Long) params.get("parentId"));
        }
        if (params.containsKey("permissionType")) {
            permission.setPermissionType((String) params.get("permissionType"));
        }
        if (params.containsKey("path")) {
            permission.setPath((String) params.get("path"));
        }
        if (params.containsKey("component")) {
            permission.setComponent((String) params.get("component"));
        }
        if (params.containsKey("icon")) {
            permission.setIcon((String) params.get("icon"));
        }
        if (params.containsKey("sort")) {
            permission.setSort((Integer) params.get("sort"));
        }
        if (params.containsKey("status")) {
            permission.setStatus((Integer) params.get("status"));
        }

        permissionRepository.save(permission);

        return Map.of("id", permission.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new AppException("权限不存在");
        }
        permissionRepository.deleteById(id);
        return Map.of("id", id);
    }
}
