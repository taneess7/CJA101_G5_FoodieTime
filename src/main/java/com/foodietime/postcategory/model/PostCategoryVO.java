package com.foodietime.postcategory.model;

import java.io.Serializable;

//import jakarta.persistence.*;

//@Entity
//@Table(name = "POST_CATEGORY")
public class PostCategoryVO implements Serializable {

//  @Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)	
//	@Column(name = "POST_CATE_ID")
	private Integer postCateId;
//	@Column(name = "POST_CATE")
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

}
