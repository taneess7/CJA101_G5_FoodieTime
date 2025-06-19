package com.foodietime.postcategory.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "POST_CATEGORY")
public class PostCategoryVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "POST_CATE_ID")
	private Integer postCateId;

	@Column(name = "POST_CATE")
	@NotNull(message = "分類名稱請勿空白")
	private String postCate;

	

// ========== 對應多方 POSTVO==========
	@OneToMany(mappedBy = "postCate", cascade = CascadeType.ALL)
	private Set<PostVO> post;


	}

