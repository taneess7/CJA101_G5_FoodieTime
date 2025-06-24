package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "FAVORITE_POST")
public class FavoritePostVO implements Serializable {

	@EmbeddedId
	private FavoritePostId Id;

	@ManyToOne
	@JoinColumn(name = "POST_ID")
	@MapsId("postId")
	private PostVO post;

	@ManyToOne
	@JoinColumn(name = "MEM_ID")
	@MapsId("memId")
	private MemberVO member;

	@Override
    public int hashCode() {
        // 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
        // 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
        // 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
        return getClass().hashCode();
    }
}
