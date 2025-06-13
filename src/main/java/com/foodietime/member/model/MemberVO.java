package com.foodietime.member.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.foodietime.accrec.model.AccrecVO;
import com.foodietime.cart.model.CartVO;
import com.foodietime.directmessage.model.DirectMessageVO;
import com.foodietime.favoritepost.model.FavoritePostVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListVO;
import com.foodietime.grouppurchasereport.model.GroupPurchaseReportVO;
import com.foodietime.memcoupon.model.MemCouponVO;
import com.foodietime.memfavlist.model.FavoriteListVO;
import com.foodietime.message.model.MessageVO;
import com.foodietime.orders.model.OrdersVO;
import com.foodietime.participants.model.ParticipantsVO;
import com.foodietime.post.model.PostVO;
import com.foodietime.reportmessage.model.ReportMessageVO;
import com.foodietime.reportpost.model.ReportPostVO;
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
	private Gender memGender;
	
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
	private MemberStatus memStatus;
	
	@Column(name = "mem_no_speak", nullable = false)
	@NotNull(message="不可為空")
	@Enumerated(EnumType.ORDINAL)
	private NoSpeakStatus memNoSpeak;
	
	@Column(name = "mem_no_post", nullable = false)
	@NotNull(message="不可為空")
	@Enumerated(EnumType.ORDINAL)
	private NoPostStatus memNoPost;
	
	@Column(name = "mem_no_group", nullable = false)
	@NotNull(message="不可為空")
	@Enumerated(EnumType.ORDINAL)
	private NoGroupStatus memNoGroup;
	
	@Column(name = "mem_no_joingroup", nullable = false)
	@NotNull(message="不可為空")
	@Enumerated(EnumType.ORDINAL)
	private NoJoingroupStatus memNoJoingroup;
	
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
	public Gender getMemGender() {
		return memGender;
	}
	public void setMemGender(Gender memGender) {
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
	public MemberStatus getMemStatus() {
		return memStatus;
	}
	public void setMemStatus(MemberStatus memStatus) {
		this.memStatus = memStatus;
	}
	public NoSpeakStatus getMemNoSpeak() {
		return memNoSpeak;
	}
	public void setMemNoSpeak(NoSpeakStatus memNoSpeak) {
		this.memNoSpeak = memNoSpeak;
	}
	public NoPostStatus getMemNoPost() {
		return memNoPost;
	}
	public void setMemNoPost(NoPostStatus memNoPost) {
		this.memNoPost = memNoPost;
	}
	public NoGroupStatus getMemNoGroup() {
		return memNoGroup;
	}
	public void setMemNoGroup(NoGroupStatus memNoGroup) {
		this.memNoGroup = memNoGroup;
	}
	public NoJoingroupStatus getMemNoJoingroup() {
		return memNoJoingroup;
	}
	public void setMemNoJoingroup(NoJoingroupStatus memNoJoingroup) {
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
	        MALE, FEMALE, NOT_REVEAL
	    }

	    public enum MemberStatus {
	        INACTIVE, ACTIVE
	    }

	    public enum NoSpeakStatus {
	        INACTIVE, ACTIVE
	    }

	    public enum NoPostStatus {
	        INACTIVE, ACTIVE
	    }

	    public enum NoGroupStatus {
	        INACTIVE, ACTIVE
	    }

	    public enum NoJoingroupStatus {
	        INACTIVE, ACTIVE
	    }
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<DirectMessageVO> directMessages;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<FavoriteListVO> favoriteList;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<CartVO> cart;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<MemCouponVO> memCoupon;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<ParticipantsVO> participants;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<GroupBuyingCollectionListVO> groupBuyingCollectionList;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<GroupPurchaseReportVO> groupPurchaseReport;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<GroupBuyingCasesVO> groupBuyingCases;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<OrdersVO> orders;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<PostVO> post;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<FavoritePostVO> favoritePost;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<ReportPostVO> reportPost;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<MessageVO> message;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<ReportMessageVO> reportMessage;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<AccrecVO> appropriationCommRecord;
	    

}
