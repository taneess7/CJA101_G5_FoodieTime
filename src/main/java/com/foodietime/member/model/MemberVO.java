package com.foodietime.member.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
@Entity
@Data
@Table(name = "member")
public class MemberVO implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mem_id")
	private Integer memId;
	
	@Column(name = "mem_email", nullable = false)
	@NotNull(message="不可為空")
	@Pattern(regexp = "^(?!\\\\.)[\\\\w!#$%&'*+/=?^`{|}~.-]+(?<!\\\\.)@([A-Za-z0-9-]+\\\\.)+[A-Za-z]{2,}$", message = "信箱格式不符合")
	private String memEmail;
	
	@Column(name = "mem_account", nullable = false, unique = true)
	@NotNull(message="帳號請勿空白")
	private String memAccount;
	
	@Column(name = "mem_password", nullable = false)
	@NotNull(message="密碼請勿空白")
	private String memPassword;
	
	@Column(name = "mem_nickname", nullable = false)
	@NotNull(message="暱稱請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "會員暱稱: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String memNickname;
	
	@Column(name = "mem_name", nullable = false)
	@NotNull(message="姓名請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "會員姓名: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String memName;
	
	@Column(name = "mem_phone", nullable = false)
	@NotNull(message="手機請勿空白")
	@Pattern(regexp = "^(?:\\\\(?0\\\\d{1,2}\\\\)?[-\\\\s]?)?\\\\d{6,8}$|^09\\\\d{2}[-\\\\s]?\\\\d{3}[-\\\\s]?\\\\d{3}$", message = "手機格式錯誤")
	private String memPhone;
	
	@Column(name = "mem_gender", nullable = false)
	@NotNull(message="性別請勿空白")
	@Enumerated(EnumType.ORDINAL)
	private Integer memGender;
	
	@Column(name = "mem_city", nullable = false)
	@NotNull(message="縣市請勿空白")
	private String memCity;
	
	@Column(name = "mem_cityarea", nullable = false)
	@NotNull(message="鄉鎮市區請勿空白")
	private String memCityarea;
	
	@Column(name = "mem_address", nullable = false)
	@NotNull(message="居住街道請勿空白")
	private String memAddress;
	
	@Column(name = "mem_code", nullable = false)
	private String memCode;
	
	@Column(name = "mem_avatar")
	private byte[] memAvatar;
	
	@Column(name = "mem_time", nullable = false)
	@NotNull(message="不可為空")
	private Timestamp memTime;
	
	@Column(name = "mem_status", nullable = false)
	@NotNull(message="不可為空")
	@Enumerated(EnumType.ORDINAL)
	private Integer memStatus;
	
	@Column(name = "mem_no_speak", nullable = false)
	@NotNull(message="不可為空")
	@Enumerated(EnumType.ORDINAL)
	private Integer memNoSpeak;
	
	@Column(name = "mem_no_post", nullable = false)
	@NotNull(message="不可為空")
	@Enumerated(EnumType.ORDINAL)
	private Integer memNoPost;
	
	@Column(name = "mem_no_group", nullable = false)
	@NotNull(message="不可為空")
	@Enumerated(EnumType.ORDINAL)
	private Integer memNoGroup;
	
	@Column(name = "mem_no_joingroup", nullable = false)
	@NotNull(message="不可為空")
	@Enumerated(EnumType.ORDINAL)
	private Integer memNoJoingroup;
	
	@Column(name = "total_star_num", nullable = false)
	@NotNull(message="不可為空")
	private Integer totalStarNum;
	
	@Column(name = "total_reviews", nullable = false)
	@NotNull(message="不可為空")
	private Integer totalReviews;
	
	public Integer getMemId() {
		return memId;
	}
	public void setMemId(Integer memId) {
		this.memId = memId;
	}
	public String getMemEmail() {
		return memEmail;
	}
	public void setMemEmail(String memEmail) {
		this.memEmail = memEmail;
	}
	public String getMemAccount() {
		return memAccount;
	}
	public void setMemAccount(String memAccount) {
		this.memAccount = memAccount;
	}
	public String getMemPassword() {
		return memPassword;
	}
	public void setMemPassword(String memPassword) {
		this.memPassword = memPassword;
	}
	public String getMemNickname() {
		return memNickname;
	}
	public void setMemNickname(String memNickname) {
		this.memNickname = memNickname;
	}
	public String getMemName() {
		return memName;
	}
	public void setMemName(String memName) {
		this.memName = memName;
	}
	public String getMemPhone() {
		return memPhone;
	}
	public void setMemPhone(String memPhone) {
		this.memPhone = memPhone;
	}
	public Integer getMemGender() {
		return memGender;
	}
	public void setMemGender(Integer memGender) {
		this.memGender = memGender;
	}
	public String getMemCity() {
		return memCity;
	}
	public void setMemCity(String memCity) {
		this.memCity = memCity;
	}
	public String getMemCityarea() {
		return memCityarea;
	}
	public void setMemCityarea(String memCityarea) {
		this.memCityarea = memCityarea;
	}
	public String getMemAddress() {
		return memAddress;
	}
	public void setMemAddress(String memAddress) {
		this.memAddress = memAddress;
	}
	public String getMemCode() {
		return memCode;
	}
	public void setMemCode(String memCode) {
		this.memCode = memCode;
	}
	public byte[] getMemAvatar() {
		return memAvatar;
	}
	public void setMemAvatar(byte[] memAvatar) {
		this.memAvatar = memAvatar;
	}
	public Timestamp getMemTime() {
		return memTime;
	}
	public void setMemTime(Timestamp memTime) {
		this.memTime = memTime;
	}
	public Integer getMemStatus() {
		return memStatus;
	}
	public void setMemStatus(Integer memStatus) {
		this.memStatus = memStatus;
	}
	public Integer getMemNoSpeak() {
		return memNoSpeak;
	}
	public void setMemNoSpeak(Integer memNoSpeak) {
		this.memNoSpeak = memNoSpeak;
	}
	public Integer getMemNoPost() {
		return memNoPost;
	}
	public void setMemNoPost(Integer memNoPost) {
		this.memNoPost = memNoPost;
	}
	public Integer getMemNoGroup() {
		return memNoGroup;
	}
	public void setMemNoGroup(Integer memNoGroup) {
		this.memNoGroup = memNoGroup;
	}
	public Integer getMemNoJoingroup() {
		return memNoJoingroup;
	}
	public void setMemNoJoingroup(Integer memNoJoingroup) {
		this.memNoJoingroup = memNoJoingroup;
	}
	public Integer getTotalStarNum() {
		return totalStarNum;
	}
	public void setTotalStarNum(Integer totalStarNum) {
		this.totalStarNum = totalStarNum;
	}
	public Integer getTotalReviews() {
		return totalReviews;
	}
	public void setTotalReviews(Integer totalReviews) {
		this.totalReviews = totalReviews;
	}
	
	public enum Gender {
	    MALE,    // 0
	    FEMALE,   // 1
	    NOT_REVEAL // 2
	}
	
	public enum memStatus {
		INACTIVE,     // 0 
		ACTIVE        // 1
	}
	
	public enum memNoSpeak {
		INACTIVE,     // 0 
		ACTIVE        // 1
	}
	
	public enum memNoPost {
		INACTIVE,     // 0 
		ACTIVE        // 1
	}
	
	public enum memNoGroup {
		INACTIVE,     // 0 
		ACTIVE        // 1
	}
	
	public enum memNoJoingroup {
		INACTIVE,     // 0 
		ACTIVE        // 1
	}

}
