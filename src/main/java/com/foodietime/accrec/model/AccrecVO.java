package com.foodietime.accrec.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.foodietime.store.model.StoreVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
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

    @ManyToOne
    @JoinColumn(name = "STOR_ID", insertable = false, updatable = false)
    private StoreVO store;


    @ManyToOne
    @JoinColumn(name = "MEM_ID", insertable = false, updatable = false)
    private MemberVO member;


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

   
}
