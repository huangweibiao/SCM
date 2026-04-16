package com.scm.config;

import com.scm.entity.*;
import com.scm.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化器
 * 系统启动时初始化必要的初始数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final WarehouseRepository warehouseRepository;
    private final DictTypeRepository dictTypeRepository;
    private final DictDataRepository dictDataRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           UserRoleRepository userRoleRepository,
                           RolePermissionRepository rolePermissionRepository,
                           WarehouseRepository warehouseRepository,
                           DictTypeRepository dictTypeRepository,
                           DictDataRepository dictDataRepository,
                           ItemCategoryRepository itemCategoryRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.warehouseRepository = warehouseRepository;
        this.dictTypeRepository = dictTypeRepository;
        this.dictDataRepository = dictDataRepository;
        this.itemCategoryRepository = itemCategoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("开始初始化数据...");

        initRoles();
        initPermissions();
        initUsers();
        initWarehouses();
        initItemCategories();
        initDictTypes();

        logger.info("数据初始化完成!");
    }

    private void initRoles() {
        if (roleRepository.count() > 0) {
            logger.info("角色数据已存在，跳过初始化");
            return;
        }

        // 超级管理员角色
        Role adminRole = new Role();
        adminRole.setRoleCode("ADMIN");
        adminRole.setRoleName("超级管理员");
        adminRole.setDescription("拥有系统所有权限");
        adminRole.setStatus(1);
        adminRole.setCreateTime(LocalDateTime.now());
        roleRepository.save(adminRole);

        // 普通用户角色
        Role userRole = new Role();
        userRole.setRoleCode("USER");
        userRole.setRoleName("普通用户");
        userRole.setDescription("普通用户角色");
        userRole.setStatus(1);
        userRole.setCreateTime(LocalDateTime.now());
        roleRepository.save(userRole);

        logger.info("角色数据初始化完成");
    }

    private void initPermissions() {
        if (permissionRepository.count() > 0) {
            logger.info("权限数据已存在，跳过初始化");
            return;
        }

        // 系统管理模块
        Permission systemMenu = createPermission("system", "系统管理", 0L, "directory", "system", 1);
        Permission userManage = createPermission("system:user", "用户管理", systemMenu.getId(), "User", "/system/user", 1);
        Permission userList = createPermission("system:user:list", "用户列表", userManage.getId(), "null", "system/user", 2);
        Permission userAdd = createPermission("system:user:add", "新增用户", userManage.getId(), "null", "system/user", 2);
        Permission userEdit = createPermission("system:user:edit", "编辑用户", userManage.getId(), "null", "system/user", 2);
        Permission userDelete = createPermission("system:user:delete", "删除用户", userManage.getId(), "null", "system/user", 2);

        Permission roleManage = createPermission("system:role", "角色管理", systemMenu.getId(), "Lock", "/system/role", 1);
        Permission roleList = createPermission("system:role:list", "角色列表", roleManage.getId(), "null", "system/role", 2);
        Permission roleAdd = createPermission("system:role:add", "新增角色", roleManage.getId(), "null", "system/role", 2);
        Permission roleEdit = createPermission("system:role:edit", "编辑角色", roleManage.getId(), "null", "system/role", 2);
        Permission roleDelete = createPermission("system:role:delete", "删除角色", roleManage.getId(), "null", "system/role", 2);

        Permission permissionManage = createPermission("system:permission", "权限管理", systemMenu.getId(), "Key", "/system/permission", 1);

        // 基础数据模块
        Permission basicMenu = createPermission("basic", "基础数据", 0L, "directory", "basic", 1);
        Permission itemCategoryManage = createPermission("basic:itemCategory", "物料分类", basicMenu.getId(), "Box", "/basic/itemCategory", 1);
        Permission itemManage = createPermission("basic:item", "物料管理", basicMenu.getId(), "Goods", "/basic/item", 1);
        Permission warehouseManage = createPermission("basic:warehouse", "仓库管理", basicMenu.getId(), "OfficeBuilding", "/basic/warehouse", 1);

        // 供应商管理模块
        Permission supplierMenu = createPermission("supplier", "供应商管理", 0L, "directory", "supplier", 1);
        Permission supplierManage = createPermission("supplier:list", "供应商列表", supplierMenu.getId(), "Supplier", "/supplier", 1);

        // 采购管理模块
        Permission purchaseMenu = createPermission("purchase", "采购管理", 0L, "directory", "purchase", 1);
        Permission purchaseOrder = createPermission("purchase:order", "采购订单", purchaseMenu.getId(), "ShoppingCart", "/purchase", 1);
        Permission inboundOrder = createPermission("purchase:inbound", "入库单", purchaseMenu.getId(), "Box", "/inbound", 1);

        // 库存管理模块
        Permission inventoryMenu = createPermission("inventory", "库存管理", 0L, "directory", "inventory", 1);
        Permission inventoryManage = createPermission("inventory:list", "库存查询", inventoryMenu.getId(), "Stock", "/inventory", 1);
        Permission inventoryWarning = createPermission("inventory:warning", "库存预警", inventoryMenu.getId(), "Warning", "/system/inventory-warning", 1);

        // 销售管理模块
        Permission salesMenu = createPermission("sales", "销售管理", 0L, "directory", "sales", 1);
        Permission salesOrder = createPermission("sales:order", "销售订单", salesMenu.getId(), "Sell", "/sales", 1);
        Permission outboundOrder = createPermission("sales:outbound", "出库单", salesMenu.getId(), "Box", "/outbound", 1);

        // 生产管理模块
        Permission productionMenu = createPermission("production", "生产管理", 0L, "directory", "production", 1);
        Permission productionOrder = createPermission("production:order", "生产工单", productionMenu.getId(), "Tools", "/production", 1);

        // 物流管理模块
        Permission logisticsMenu = createPermission("logistics", "物流管理", 0L, "directory", "logistics", 1);
        Permission logisticsOrder = createPermission("logistics:order", "物流订单", logisticsMenu.getId(), "Van", "/logistics", 1);

        // 报表模块
        Permission reportMenu = createPermission("report", "报表分析", 0L, "directory", "report", 1);
        Permission reportManage = createPermission("report:analysis", "报表分析", reportMenu.getId(), "DataAnalysis", "/report", 1);

        // 给超级管理员角色分配所有权限
        Role adminRole = roleRepository.findByRoleCode("ADMIN").orElse(null);
        if (adminRole != null) {
            List<Permission> allPermissions = permissionRepository.findAll();
            for (Permission permission : allPermissions) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(adminRole.getId());
                rp.setPermissionId(permission.getId());
                rolePermissionRepository.save(rp);
            }
        }

        logger.info("权限数据初始化完成");
    }

    private Permission createPermission(String code, String name, Long parentId, String icon, String path, Integer type) {
        Permission permission = new Permission();
        permission.setPermissionCode(code);
        permission.setPermissionName(name);
        permission.setParentId(parentId);
        permission.setPermissionType(type == 1 ? "directory" : "button");
        permission.setPath(path);
        permission.setIcon(icon);
        permission.setSort(0);
        permission.setStatus(1);
        permission.setCreateTime(LocalDateTime.now());
        return permissionRepository.save(permission);
    }

    private void initUsers() {
        if (userRepository.count() > 0) {
            logger.info("用户数据已存在，跳过初始化");
            return;
        }

        // 管理员用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setRealName("系统管理员");
        admin.setPhone("13800138000");
        admin.setEmail("admin@scm.com");
        admin.setStatus(1);
        admin.setCreateTime(LocalDateTime.now());
        admin = userRepository.save(admin);

        // 普通用户
        User user = new User();
        user.setUsername("user");
        user.setPassword("123456");
        user.setRealName("普通用户");
        user.setPhone("13800138001");
        user.setEmail("user@scm.com");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user = userRepository.save(user);

        // 分配角色
        Role adminRole = roleRepository.findByRoleCode("ADMIN").orElse(null);
        Role userRole = roleRepository.findByRoleCode("USER").orElse(null);

        if (adminRole != null) {
            UserRole ur = new UserRole();
            ur.setUserId(admin.getId());
            ur.setRoleId(adminRole.getId());
            userRoleRepository.save(ur);
        }

        if (userRole != null) {
            UserRole ur = new UserRole();
            ur.setUserId(user.getId());
            ur.setRoleId(userRole.getId());
            userRoleRepository.save(ur);
        }

        logger.info("用户数据初始化完成");
    }

    private void initWarehouses() {
        if (warehouseRepository.count() > 0) {
            logger.info("仓库数据已存在，跳过初始化");
            return;
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode("WH001");
        warehouse.setWarehouseName("主仓库");
        warehouse.setAddress("上海市浦东新区张江路100号");
        warehouse.setManager("管理员");
        warehouse.setPhone("021-12345678");
        warehouse.setStatus(1);
        warehouse.setCreateTime(LocalDateTime.now());
        warehouseRepository.save(warehouse);

        Warehouse warehouse2 = new Warehouse();
        warehouse2.setWarehouseCode("WH002");
        warehouse2.setWarehouseName("成品仓库");
        warehouse2.setAddress("上海市浦东新区张江路200号");
        warehouse2.setManager("管理员");
        warehouse2.setPhone("021-12345679");
        warehouse2.setStatus(1);
        warehouse2.setCreateTime(LocalDateTime.now());
        warehouseRepository.save(warehouse2);

        logger.info("仓库数据初始化完成");
    }

    private void initItemCategories() {
        if (itemCategoryRepository.count() > 0) {
            logger.info("物料分类数据已存在，跳过初始化");
            return;
        }

        ItemCategory category1 = new ItemCategory();
        category1.setCategoryCode("CAT001");
        category1.setCategoryName("原材料");
        category1.setParentId(0L);
        category1.setSort(1);
        category1.setStatus(1);
        category1.setCreateTime(LocalDateTime.now());
        itemCategoryRepository.save(category1);

        ItemCategory category2 = new ItemCategory();
        category2.setCategoryCode("CAT002");
        category2.setCategoryName("半成品");
        category2.setParentId(0L);
        category2.setSort(2);
        category2.setStatus(1);
        category2.setCreateTime(LocalDateTime.now());
        itemCategoryRepository.save(category2);

        ItemCategory category3 = new ItemCategory();
        category3.setCategoryCode("CAT003");
        category3.setCategoryName("成品");
        category3.setParentId(0L);
        category3.setSort(3);
        category3.setStatus(1);
        category3.setCreateTime(LocalDateTime.now());
        itemCategoryRepository.save(category3);

        logger.info("物料分类数据初始化完成");
    }

    private void initDictTypes() {
        if (dictTypeRepository.count() > 0) {
            logger.info("数据字典已存在，跳过初始化");
            return;
        }

        // 订单状态
        DictType orderStatusType = new DictType();
        orderStatusType.setDictCode("order_status");
        orderStatusType.setDictName("订单状态");
        orderStatusType.setSort(1);
        orderStatusType.setStatus(1);
        orderStatusType.setCreateTime(LocalDateTime.now());
        orderStatusType = dictTypeRepository.save(orderStatusType);

        createDictData(orderStatusType.getId(), "待审核", "10", 1);
        createDictData(orderStatusType.getId(), "已审核", "20", 2);
        createDictData(orderStatusType.getId(), "部分处理", "30", 3);
        createDictData(orderStatusType.getId(), "已完成", "40", 4);
        createDictData(orderStatusType.getId(), "已关闭", "50", 5);

        // 入库类型
        DictType inboundType = new DictType();
        inboundType.setDictCode("inbound_type");
        inboundType.setDictName("入库类型");
        inboundType.setSort(2);
        inboundType.setStatus(1);
        inboundType.setCreateTime(LocalDateTime.now());
        inboundType = dictTypeRepository.save(inboundType);

        createDictData(inboundType.getId(), "采购入库", "10", 1);
        createDictData(inboundType.getId(), "生产入库", "20", 2);
        createDictData(inboundType.getId(), "退货入库", "30", 3);
        createDictData(inboundType.getId(), "调拨入库", "40", 4);

        // 出库类型
        DictType outboundType = new DictType();
        outboundType.setDictCode("outbound_type");
        outboundType.setDictName("出库类型");
        outboundType.setSort(3);
        outboundType.setStatus(1);
        outboundType.setCreateTime(LocalDateTime.now());
        outboundType = dictTypeRepository.save(outboundType);

        createDictData(outboundType.getId(), "销售出库", "10", 1);
        createDictData(outboundType.getId(), "生产领料", "20", 2);
        createDictData(outboundType.getId(), "退货出库", "30", 3);
        createDictData(outboundType.getId(), "调拨出库", "40", 4);

        // 物流状态
        DictType logisticsStatus = new DictType();
        logisticsStatus.setDictCode("logistics_status");
        logisticsStatus.setDictName("物流状态");
        logisticsStatus.setSort(4);
        logisticsStatus.setStatus(1);
        logisticsStatus.setCreateTime(LocalDateTime.now());
        logisticsStatus = dictTypeRepository.save(logisticsStatus);

        createDictData(logisticsStatus.getId(), "待发货", "10", 1);
        createDictData(logisticsStatus.getId(), "运输中", "20", 2);
        createDictData(logisticsStatus.getId(), "已签收", "30", 3);
        createDictData(logisticsStatus.getId(), "拒收", "40", 4);

        logger.info("数据字典初始化完成");
    }

    private void createDictData(Long typeId, String label, String value, Integer sort) {
        DictData data = new DictData();
        data.setDictTypeId(typeId);
        data.setDictLabel(label);
        data.setDictValue(value);
        data.setSort(sort);
        data.setStatus(1);
        data.setCreateTime(LocalDateTime.now());
        dictDataRepository.save(data);
    }
}
