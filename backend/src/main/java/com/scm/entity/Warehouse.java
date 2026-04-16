package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;

/**
 * 仓库实体类
 * 对应数据库表：scm_warehouse
 */
@Entity
@Table(name = "scm_warehouse")
public class Warehouse extends BaseEntity {

    @Column(name = "warehouse_code", unique = true, nullable = false, length = 50)
    private String warehouseCode;

    @Column(name = "warehouse_name", nullable = false, length = 200)
    private String warehouseName;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "manager", length = 50)
    private String manager;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
