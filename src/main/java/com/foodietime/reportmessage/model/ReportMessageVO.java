package com.foodietime.reportmessage.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageVO;
import com.foodietime.post.model.PostVO;
import com.foodietime.postcategory.model.PostCategoryVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "REPORT_MESSAGE")
public class ReportMessageVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REP_MES_ID")
	private Integer repMesId;

	@ManyToOne
	@JoinColumn(name = "MES_ID", referencedColumnName = "MES_ID")
	private MessageVO mes;

	@ManyToOne
	@JoinColumn(name = "MEM_ID", referencedColumnName = "MEM_ID")
	private MemberVO member;

	@Column(name = "REP_MES_DATE")
	private Timestamp repMesDate;

	@Column(name = "REP_MES_REASON")
	@NotNull(message = "檢舉原因請勿空白")
	@Size(max=255)
	private String repMesReason;

	@Column(name = "REP_MES_STATUS")
	private byte repMesStatus;

	
}
