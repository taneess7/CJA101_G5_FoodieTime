package com.foodietime.favoritepost.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

import jakarta.persistence.*;

@Entity
@Table(name = "FAVORITE_POST")
public class FavoritePostVO implements Serializable {

	@EmbeddedId
	private FavoritePostId Id;

	@ManyToOne
	@JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
	private PostVO postId;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "POST_ID")
	private MemberVO memId;

}
