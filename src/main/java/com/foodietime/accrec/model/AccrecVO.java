package com.foodietime.accrec.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.foodietime.member.model.MemberVO;
import com.foodietime.smg.model.SmgVO;
import com.foodietime.store.model.StoreVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "appropriation_comm_record")
public class AccrecVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMM_PAYOUT_ID")
    @EqualsAndHashCode.Include
    private Integer commPayoutID; 

    @NotNull(message = "訂單類型不能是空的")
    @Column(name = "ORDER_TYPE")
    private Byte orderType;

    @NotNull(message = "訂單參考 ID 不能為空")
    @Column(name = "ORDER_REF_ID")
    private Integer orderRefId;

    @ManyToOne
    @JoinColumn(name = "STOR_ID")
    private StoreVO store;

    @ManyToOne
    @JoinColumn(name = "MEM_ID")
    private MemberVO member;

    @NotNull(message = "角色類型不能為空")
    @Column(name = "PAYOUT_ROLE")
    private Byte payoutRole;

    
    @DecimalMin(value = "0.00", message = "撥款金額不能小於 0")
    @Column(name = "PAYOUT_AMOUNT", precision = 10, scale = 2)
    private BigDecimal payoutAmount;

    
    @DecimalMin(value = "0.00", message = "抽成金額不能小於 0")
    @Column(name = "COMMISSION_AMOUNT", precision = 10, scale = 2)
    private BigDecimal commissionAmount;

    
    @Column(name = "PAYOUT_STATUS")
    private Byte payoutStatus;

    
    @Column(name = "COMMISSION_STATUS")
    private Byte commissionStatus;

    @PastOrPresent(message = "建立時間不能在未來")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @PastOrPresent(message = "更新時間不能在未來")
    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;

    @PastOrPresent(message = "撥款時間不能在未來")
    @Column(name = "PAYOUT_TIME")
    private LocalDateTime payoutTime;

    @Size(max = 6, message = "月份格式長度不能超過 6 位")
    @Column(name = "PAYOUT_MONTH", length = 6)
    private String payoutMonth;
   
    
    @ManyToOne
    @NotNull(message = "serverManager 不可為空")  
    @JoinColumn(name = "SMGR_ID") 
    private SmgVO serverManager;
    
    
}
