package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FavoritePostPK implements Serializable{
	
	@Column(name = "POST_ID")
	private Integer postId;
	
	@Column(name = "MEM_ID")
	private Integer memId;
	
	public FavoritePostPK() {}
		public FavoritePostPK(Integer postId, Integer memId) {
			this.postId = postId;
			this.memId = memId;
		}
	
		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(obj == null || getClass() != obj.getClass()) return false;
			FavoritePostPK that = (FavoritePostPK) obj;
			return Objects.equals(postId, that.postId) &&
					Objects.equals(memId, that.memId);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(postId, memId);
		}

}
