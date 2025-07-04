package com.foodietime.post.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

import com.foodietime.cart.model.CartVO;
import com.foodietime.favoritepost.model.FavoritePostVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageVO;
import com.foodietime.postcategory.model.PostCategoryVO;
import com.foodietime.product.model.ProductVO;
import com.foodietime.reportpost.model.ReportPostVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//==================== 1. 移除 @Data，使用更精確的 Lombok 註解 ====================
@Getter
@Setter
@Entity
@Table(name = "POST")
public class PostVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "POST_ID")
	private Integer postId;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO member;

	@ManyToOne
	@JoinColumn(name = "POST_CATE_ID", referencedColumnName = "POST_CATE_ID")
	private PostCategoryVO postCate;

	@Column(name = "POST_DATE")
	private Timestamp postDate;

	@Column(name = "POST_STATUS")
	private byte postStatus;

	@Column(name = "EDITDATE")
	private Timestamp editDate;

	@Column(name = "POST_TITLE")
	@NotNull(message = "標題請勿空白")
	@Size(min = 1, max = 100, message = "標題長度需介於1到100字")
	private String postTitle;

	@Lob
	@Column(name = "POST_CONTENT", columnDefinition = "LONGTEXT")
	@NotNull(message = "內容請勿空白")
	private String postContent;

	@Column(name = "LIKE_COUNT")
	private Integer likeCount;

	@Column(name = "VIEWS")
	private Integer views;

	

// ========== 對應多方 ==========
	@OneToMany(mappedBy = "post")
	private Set<ReportPostVO> reportPost; // 這個分類底下的所有貼文

	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
	private Set<MessageVO> message;

	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
	private Set<FavoritePostVO> favoritePost;

    // ==================== 2. 手動實作 equals 和 hashCode ====================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostVO postVO = (PostVO) o;
        // 關鍵：只比較主鍵 (ID)，並且只有當 ID 不是 null 時才比較
        return postId != null && Objects.equals(postId, postVO.postId);
    }

    @Override
    public int hashCode() {
        // 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
        // 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
        // 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
        return getClass().hashCode();
    }

}
