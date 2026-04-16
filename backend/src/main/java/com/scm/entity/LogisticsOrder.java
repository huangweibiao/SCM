package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物流订单实体类
 * 对应数据库表：scm_logistics_order
 */
@Entity
@Table(name = "scm_logistics_order")
public class LogisticsOrder extends BaseEntity {

    @Column(name = "logistics_no", unique = true, nullable = false, length = 50)
    private String logisticsNo;

    @Column(name = "business_type", nullable = false)
    private Integer businessType;

    @Column(name = "business_id")
    private Long businessId;

    @Column(name = "courier_name", length = 100)
    private String courierName;

    @Column(name = "courier_no", length = 50)
    private String courierNo;

    @Column(name = "send_address", length = 500)
    private String sendAddress;

    @Column(name = "receive_address", nullable = false, length = 500)
    private String receiveAddress;

    @Column(name = "receive_person", nullable = false, length = 50)
    private String receivePerson;

    @Column(name = "receive_phone", nullable = false, length = 20)
    private String receivePhone;

    @Column(name = "status", nullable = false)
    private Integer status = 10;

    @Column(name = "logistics_fee", precision = 18, scale = 2)
    private BigDecimal logisticsFee = BigDecimal.ZERO;

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierNo() {
        return courierNo;
    }

    public void setCourierNo(String courierNo) {
        this.courierNo = courierNo;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getReceivePerson() {
        return receivePerson;
    }

    public void setReceivePerson(String receivePerson) {
        this.receivePerson = receivePerson;
    }

    public String getReceivePhone() {
        return receivePhone;
    }

    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getLogisticsFee() {
        return logisticsFee;
    }

    public void setLogisticsFee(BigDecimal logisticsFee) {
        this.logisticsFee = logisticsFee;
    }
}
