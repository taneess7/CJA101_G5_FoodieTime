package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.util.Objects;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class FavoritePostId implements Serializable {

	
	@ManyToOne
	@JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
	private PostVO postId;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO memId;

	public FavoritePostId() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		FavoritePostId that = (FavoritePostId) obj;
		return Objects.equals(postId, that.postId) && Objects.equals(memId, that.memId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(postId, memId);
	}

	public PostVO getPostId() {
		return postId;
	}

	public void setPostId(PostVO postId) {
		this.postId = postId;
	}

	public MemberVO getMemId() {
		return memId;
	}

	public void setMemId(MemberVO memId) {
		this.memId = memId;
	}

}
