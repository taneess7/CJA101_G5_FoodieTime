package com.foodietime.postcategory.model;

import java.io.Serializable;
import java.util.Set;

import com.foodietime.post.model.PostVO;
import jakarta.persistence.*;

@Entity
@Table(name = "POST_CATEGORY")
public class PostCategoryVO implements Serializable {

	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "POST_CATE_ID")
	private Integer postCateId;
	
	@OneToMany(mappedBy = "postcategory", cascade = CascadeType.ALL)
	private Set<PostVO> posts;

	@Column(name = "POST_CATE")
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

	public Set<PostVO> getPosts() {
		return posts;
	}

	public void setPosts(Set<PostVO> posts) {
		this.posts = posts;
	}
}
