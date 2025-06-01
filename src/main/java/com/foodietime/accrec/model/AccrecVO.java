package com.foodietime.accrec.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "appropriation_comm_record")
public class AccrecVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMM_PAYOUT_ID")
    private Integer commPayoutID;

    @Column(name = "ORDER_TYPE")
    private Byte orderType;

    @Column(name = "ORDER_REF_ID")
    private Integer orderRefId;

    @Column(name = "STOR_ID")
    private Integer storId;

    @Column(name = "MEM_ID")
    private Integer memId;

    @Column(name = "PAYOUT_ROLE")
    private Byte payoutRole;

    @Column(name = "PAYOUT_AMOUNT", precision = 10, scale = 2)
    private BigDecimal payoutAmount;

    @Column(name = "COMMISSION_AMOUNT", precision = 10, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "PAYOUT_STATUS")
    private Byte payoutStatus;

    @Column(name = "COMMISSION_STATUS")
    private Byte commissionStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_AT")
    private Date createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_AT")
    private Date updateAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PAYOUT_TIME")
    private Date payoutTime;

    @Column(name = "PAYOUT_MONTH", length = 6)
    private String payoutMonth;

    // --- Getters and Setters ---

    public Integer getCommPayoutID() {
        return commPayoutID;
    }

    public void setCommPayoutID(Integer commPayoutID) {
        this.commPayoutID = commPayoutID;
    }

    public Byte getOrderType() {
        return orderType;
    }

    public void setOrderType(Byte orderType) {
        this.orderType = orderType;
    }

    public Integer getOrderRefId() {
        return orderRefId;
    }

    public void setOrderRefId(Integer orderRefId) {
        this.orderRefId = orderRefId;
    }

    public Integer getStorId() {
        return storId;
    }

    public void setStorId(Integer storId) {
        this.storId = storId;
    }

    public Integer getMemId() {
        return memId;
    }

    public void setMemId(Integer memId) {
        this.memId = memId;
    }

    public Byte getPayoutRole() {
        return payoutRole;
    }

    public void setPayoutRole(Byte payoutRole) {
        this.payoutRole = payoutRole;
    }

    public BigDecimal getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(BigDecimal payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public Byte getPayoutStatus() {
        return payoutStatus;
    }

    public void setPayoutStatus(Byte payoutStatus) {
        this.payoutStatus = payoutStatus;
    }

    public Byte getCommissionStatus() {
        return commissionStatus;
    }

    public void setCommissionStatus(Byte commissionStatus) {
        this.commissionStatus = commissionStatus;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Date getPayoutTime() {
        return payoutTime;
    }

    public void setPayoutTime(Date payoutTime) {
        this.payoutTime = payoutTime;
    }

    public String getPayoutMonth() {
        return payoutMonth;
    }

    public void setPayoutMonth(String payoutMonth) {
        this.payoutMonth = payoutMonth;
    }
}
