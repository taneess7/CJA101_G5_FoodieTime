package com.foodietime.member.model;
import jakarta.persistence.*;
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
	private String memEmail;
	@Column(name = "mem_account", nullable = false, unique = true)
	private String memAccount;
	@Column(name = "mem_password", nullable = false)
	private String memPassword;
	@Column(name = "mem_nickname", nullable = false)
	private String memNickname;
	@Column(name = "mem_name", nullable = false)
	private String memName;
	@Column(name = "mem_phone", nullable = false)
	private String memPhone;
	@Column(name = "mem_gender", nullable = false)
	private Integer memGender;
	@Column(name = "mem_id")
	private String memCity;
	@Column(name = "mem_cityarea", nullable = false)
	private String memCityarea;
	@Column(name = "mem_address", nullable = false)
	private String memAddress;
	@Column(name = "mem_code", nullable = false)
	private String memCode;
	@Column(name = "mem_avatar")
	private byte[] memAvatar;
	@Column(name = "mem_time", nullable = false)
	private Timestamp memTime;
	@Column(name = "mem_status", nullable = false)
	private Integer memStatus;
	@Column(name = "mem_no_speak", nullable = false)
	private Integer memNoSpeak;
	@Column(name = "mem_no_post", nullable = false)
	private Integer memNoPost;
	@Column(name = "mem_no_group", nullable = false)
	private Integer memNoGroup;
	@Column(name = "mem_no_joingroup", nullable = false)
	private Integer memNoJoingroup;
	@Column(name = "total_star_num", nullable = false)
	private Integer totalStarNum;
	@Column(name = "total_reviews", nullable = false)
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
	

}
