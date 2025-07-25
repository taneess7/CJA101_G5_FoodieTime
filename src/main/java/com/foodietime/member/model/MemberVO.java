package com.foodietime.member.model;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = {"directMessages","favoriteList","cart","memCoupon","participants","groupBuyingCollectionList","groupPurchaseReport","groupBuyingCases","orders","post","favoritePost","reportPost","message","reportMessage","appropriationCommRecord"})
@EqualsAndHashCode(exclude = {"directMessages","favoriteList","cart","memCoupon","participants","groupBuyingCollectionList","groupPurchaseReport","groupBuyingCases","orders","post","favoritePost","reportPost","message","reportMessage","appropriationCommRecord"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "memId")


@Table(name = "member")
public class MemberVO implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mem_id")
	private Integer memId;
	
	@Column(name = "mem_email", nullable = false)
	@NotBlank(message="不可為空")
	@Pattern(
		    regexp = "^(?!\\.)[\\w!#$%&'*+/=?^`{|}~.-]+(?<!\\.)@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$",
		    message = "信箱格式不符合"
		)
	private String memEmail;
	
	@Column(name = "mem_account", nullable = false, unique = true)
	@NotBlank(message="帳號請勿空白")
	@Pattern(regexp = "^[a-zA-Z0-9_]{6,20}$", message = "帳號格式錯誤：僅限英數字與底線，長度6~20字元")
	private String memAccount;
	
	@Column(name = "mem_password", nullable = false)
	@NotBlank(message="密碼請勿空白")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "密碼格式錯誤：需為6~20位英數混合")
	private String memPassword;
	
	@Column(name = "mem_nickname", nullable = false)
	@NotBlank(message="暱稱請勿空白")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,10}$", message = "會員暱稱: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String memNickname;
	
	@Column(name = "mem_name", nullable = false)
	@NotBlank(message="姓名請勿空白")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,10}$", message = "會員姓名: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String memName;
	
	@Column(name = "mem_phone", nullable = false)
	@NotBlank(message="手機請勿空白")
	@Pattern(
		    regexp = "^09\\d{8}$",
		    message = "手機格式錯誤，請輸入 09 開頭的 10 碼手機號碼"
		)
	private String memPhone;
	
	@Column(name = "mem_gender", nullable = false)
	@NotNull(message="性別請勿空白")
    @Enumerated(EnumType.ORDINAL)
	private Gender memGender;
	
	@Column(name = "mem_city", nullable = false)
	@NotBlank(message="縣市請勿空白")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]{2,20}$", message = "格式錯誤：僅允許中英數字與空白")
	private String memCity;
	
	@Column(name = "mem_cityarea", nullable = false)
	@NotBlank(message="鄉鎮市區請勿空白")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]{2,20}$", message = "格式錯誤：僅允許中英數字與空白")
	private String memCityarea;
	
	@Column(name = "mem_address", nullable = false)
	@NotBlank(message="居住街道請勿空白")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]{2,20}$", message = "格式錯誤：僅允許中英數字與空白")
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
	

	
	 public enum Gender {
	        MALE, FEMALE, NOT_REVEAL
	    }

	    public enum MemberStatus {
	        INACTIVE, ACTIVE, DISABLED
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
	    
	    @OneToMany(mappedBy = "member")
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
