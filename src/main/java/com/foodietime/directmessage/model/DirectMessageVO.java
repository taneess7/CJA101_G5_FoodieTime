package com.foodietime.directmessage.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;
import com.foodietime.smg.model.SmgVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Getter
@Setter
@Entity
@ToString(exclude = {"member","smgr"})
@EqualsAndHashCode(exclude = {"member","smgr"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "dmId")


@Table(name = "direct_message")
public class DirectMessageVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dm_id")
	private Integer dmId;

	@ManyToOne
	@JoinColumn(name = "mem_id", nullable = false)
	private MemberVO member;

	@ManyToOne
	@JoinColumn(name = "smgr_id")
	private SmgVO smgr;

	@Column(name = "mess_content", nullable = false)
	private String messContent;

	@Column(name = "mess_time", nullable = false)
	private LocalDateTime messTime;

	@Column(name = "mess_direction", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private MessageDirection messDirection;




	public enum MessageDirection {
	    MEMBER_TO_ADMIN, //0
	    ADMIN_TO_MEMBER  //1
	}
}
