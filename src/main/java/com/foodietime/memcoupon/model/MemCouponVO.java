package com.foodietime.memcoupon.model;

import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberVO;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table(name = "member_coupon")
public class MemCouponVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="MEM_COU_ID")
	private Integer memCouId;   // 會員優惠券編號（主鍵）

	@ManyToOne
	@JoinColumn(name = "COU_ID",referencedColumnName = "COU_ID")
    private CouponVO couId;      // 優惠券編號（外鍵，對應COUPON表）

	@ManyToOne
	@JoinColumn(name = "mem_id",referencedColumnName = "mem_id")
    private MemberVO memId;      // 會員編號（外鍵，對應MEMBER表）

	@Column(name = "USE_STATUS")
    private Integer useStatus;  // 使用狀態（0:未使用, 1:已使用）

}
