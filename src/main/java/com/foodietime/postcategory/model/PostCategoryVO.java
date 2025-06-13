package com.foodietime.postcategory.model;

import java.io.Serializable;
import java.util.Set;

import com.foodietime.post.model.PostVO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "POST_CATEGORY")
public class PostCategoryVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "POST_CATE_ID")
	private Integer postCateId;

	@Column(name = "POST_CATE")
	@NotNull(message = "檢舉原因請勿空白")
	@Min(value=1, message="最小1")
    @Max(value=45, message="最大45")
	private Integer postCate;

	public Integer getPostCateId() {
		return postCateId;
	}

	public void setPostCateId(Integer postCateId) {
		this.postCateId = postCateId;
	}

	public Integer getPostCate() {
		return postCate;
	}

	public void setPostCate(Integer postCate) {
		this.postCate = postCate;
	}

// ========== 對應多方 POSTVO==========
	@OneToMany(mappedBy = "postCateId", cascade = CascadeType.ALL)
	private Set<PostVO> post;

	public Set<PostVO> getPost() {
		return post;
	}

	public void setPost(Set<PostVO> post) {
		this.post = post;
	}
}
