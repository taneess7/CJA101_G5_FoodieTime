package com.foodietime.postcategory.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

import com.foodietime.cart.model.CartVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;
import com.foodietime.product.model.ProductVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "POST_CATEGORY")
public class PostCategoryVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "POST_CATE_ID")
	private Integer postCateId;

	@Column(name = "POST_CATE")
	@NotNull(message = "分類名稱不可為空")
	@Size(min = 2, max = 20, message = "分類名稱長度必須介於 2 到 20 個字之間")
	private String postCate;

// ========== 對應多方 POSTVO==========
	@OneToMany(mappedBy = "postCate", cascade = CascadeType.ALL)
	private Set<PostVO> post;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PostCategoryVO postCategoryVO = (PostCategoryVO) o;
		// 關鍵：只比較主鍵 (ID)，並且只有當 ID 不是 null 時才比較
		return postCateId != null && Objects.equals(postCateId, postCategoryVO.postCateId);
	}

	@Override
	public int hashCode() {
		// 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
		// 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
		// 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
		return getClass().hashCode();
	}
}
