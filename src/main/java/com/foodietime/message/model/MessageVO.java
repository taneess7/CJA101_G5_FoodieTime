package com.foodietime.message.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;
import com.foodietime.postcategory.model.PostCategoryVO;
import com.foodietime.reportmessage.model.ReportMessageVO;
import com.foodietime.reportpost.model.ReportPostVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "MESSAGE")
public class MessageVO implements Serializable {

	@Id
	@Column(name = "MES_ID")
	private Integer mesId;

	@ManyToOne
	@JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
	private PostVO post;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO member;

	@Column(name = "MES_DATE")
	private Timestamp mesDate;

	@Column(name = "MES_CONTENT")
	@NotNull(message = "留言內容請勿空白")
	@Min(value=1, message="最小1")
    @Max(value=255, message="最大255")
	private String mesContent;



	// ========== 對應多方 ==========
	@OneToMany(mappedBy = "mes", cascade = CascadeType.ALL)
	private Set<ReportMessageVO> reportMessage; // 這個分類底下的所有留言


}
