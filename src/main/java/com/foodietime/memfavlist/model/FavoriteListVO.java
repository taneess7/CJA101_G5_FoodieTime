package com.foodietime.memfavlist.model;

import java.io.Serializable;

import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(FavoriteListId.class) //複合主鍵
@Table(name = "favorite_list")
public class FavoriteListVO implements Serializable{
	
	@Id
	@Column(name="mem_id",nullable = false)
	private Integer memId;
	
	@Id
	@Column(name="prod_id",nullable = false)
	private Integer prodId;
	
	// 連到 MEMBER 表
    @ManyToOne
    @JoinColumn(name = "mem_id", referencedColumnName = "mem_id", insertable = true, updatable = false)
    private MemberVO member;

    // 連到 PRODUCT 表
    @ManyToOne
    @JoinColumn(name = "prod_id", referencedColumnName = "prod_id", insertable = true, updatable = false)
    private ProductVO product;
}
