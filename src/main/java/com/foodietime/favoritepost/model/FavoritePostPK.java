package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.util.Objects;

public class FavoritePostPK implements Serializable{
	private Integer postId;
	private Integer memId;
	
	public FavoritePostPK() {}
		public FavoritePostPK(Integer postId, Integer memId) {
			this.postId = postId;
			this.memId = memId;
		}
	
		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(!(o instanceof FavoritePostPK)) return false;
			FavoritePostPK that = (FavoritePostPK) o;
			return Objects.equals(postId, that.postId) &&
					Objects.equals(memId, that.memId);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(postId, memId);
		}

}
