package com.foodietime.reportpost.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;
import com.foodietime.postcategory.model.PostCategoryVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "REPORT_POST")
public class ReportPostVO implements Serializable {

	@Id
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
	@Min(value=1, message="最小1")
    @Max(value=255, message="最大255")
	private char repPostReason;

	@Column(name = "REP_POST_STATUS")
	private byte repPostStatus;

	

}
