package com.foodietime.reportpost.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import com.foodietime.cart.model.CartVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;
import com.foodietime.postcategory.model.PostCategoryVO;
import com.foodietime.product.model.ProductVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "REPORT_POST")
public class ReportPostVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REP_POST_ID")
	private Integer repPostId;

	@ManyToOne
	@JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
	private PostVO post;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO member;

	@Column(name = "REP_POST_DATE")
	private Timestamp repPostDate;

	@Column(name = "REP_POST_REASON")
	@NotNull(message = "檢舉原因請勿空白")
	@Size(max=255)
	private String repPostReason;

	@Column(name = "REP_POST_STATUS")
	private byte repPostStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportPostVO reportPostVO = (ReportPostVO) o;
        // 關鍵：只比較主鍵 (ID)，並且只有當 ID 不是 null 時才比較
        return repPostId != null && Objects.equals(repPostId, reportPostVO.repPostId);
    }

    @Override
    public int hashCode() {
        // 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
        // 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
        // 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
        return getClass().hashCode();
    }
	

}
