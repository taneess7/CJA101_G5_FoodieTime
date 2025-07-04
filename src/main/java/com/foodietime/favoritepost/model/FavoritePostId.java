package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.util.Objects;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;
import com.foodietime.product.model.ProductVO;
import com.foodietime.smgauth.model.SmgauthId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class FavoritePostId implements Serializable {

	
	@Column(name = "POST_ID")
	private Integer postId;

	@Column(name = "MEM_ID")
	private Integer memId;

	public FavoritePostId() {
	}
	
	public FavoritePostId(Integer postId, Integer memId) {
	this.postId = postId;
	this.memId = memId;
}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
        if (!(o instanceof FavoritePostId)) return false;
        FavoritePostId that = (FavoritePostId) o;
        return Objects.equals(postId, that.postId) &&
               Objects.equals(memId, that.memId);
	}
	
	@Override
    public int hashCode() {
        // 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
        // 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
        // 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
        return Objects.hash(postId, memId);
    }



}
