package com.foodietime.act.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodietime.orders.model.OrdersVO;
import com.foodietime.store.model.StoreVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "activity")
public class ActVO implements Serializable {

	// 寫上所有欄位
	// 1.活動編號
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ACT_ID", updatable = false)
	private Integer actId; 
	
	//2.店家物件（外鍵：STORE_ID）
	@JsonIgnore //Jackson 無法自動處理懶加載的 JPA 關聯對象，會噴 500
	@ManyToOne
    @JoinColumn(name = "STOR_ID", referencedColumnName = "STOR_ID",nullable = true) // 外鍵名稱
	private StoreVO store;

	
	//2-1.店家id欄位 
	@Transient // ➜ 不映射到資料庫，只拿來接值用
	private Integer storId;
	
	public Integer getStorId() { //用傳進來的 storId 查出 StoreVO，儲存時更新 STOR_ID 外鍵欄位
		 return (store != null) ? store.getStorId() : null;
	}
	public void setStorId(Integer storId) {
	    this.storId = storId;
	}

	
	// 3.活動類型
	@NotBlank(message="活動類型: 請勿空白")
	@Column(name = "ACT_CATE", length = 50)
	private String actCate; 
	

	// 4.活動名稱
	@NotBlank(message="活動名稱: 請勿空白")
	@Column(name = "ACT_NAME")
	private String actName; 
	
	// 5.活動內容
	@NotBlank(message="活動內容: 請勿空白")
	@Column(name = "ACT_CONTENT", length = 255)
	private String actContent; 
	
	// 6.活動建立時間

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@CreationTimestamp
	@Column(name = "ACT_LAUNCHTIME" , updatable = false)
	private Timestamp actSetTime; 
	
	 
	//使用活動折扣需同時符合:活動狀態上架、活動開始時間
	// 7.活動開始時間
	@NotNull(message="活動開始時間: 請勿空白")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@Column(name = "ACT_STARTTIME")
	private Timestamp actStartTime; 
	
	// 8.活動結束時間 
	@NotNull(message="活動結束時間: 請勿空白")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@Column(name = "ACT_ENDTIME")
	private Timestamp actEndTime; 
	
	// 9.活動狀態 (0:未上架,1:上架) 
	@Column(name = "ACT_STATUS")
	private Byte actStatus; 
	
	// 10.折扣類型 (0:百分比,1:金額)
	@Column(name = "ACT_DISCOUNT")
	private Byte actDiscount; 
	
	// 11.折扣值  
	@NotNull(message = "折扣值不可為空")
	@DecimalMin(value = "0.01", message = "折扣值必須大於 0")
	@Column(name = "ACT_DISCOUNT_VALUE")
	private Double actDiscValue; 
	
	// 12.活動圖片
	@Lob 
	@JsonIgnore//Jackson 就不會試圖把圖片轉成 JSON
	@Column(name = "ACT_PHOTO")
	private byte[] actPhoto; 
	
	// 13.最後更新時間
	@Column(name = "ACT_LAST_UPDATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@UpdateTimestamp 
	private Timestamp actLastUpdate; 
	
	// 14.預設true全店家適用
	@Column(name = "ISGLOBAL")
	private Boolean isGlobal = true;  

	
	// OneToMany
	
	@OneToMany(mappedBy = "act", cascade = CascadeType.ALL)
    private List<OrdersVO> orders;
	
	// 取得or設置
	public ActVO() {
		super();
	}
	
	// ==================== 2. 手動實作 equals 和 hashCode ====================
			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;
				ActVO couponVO = (ActVO) o;
				// 關鍵：只比較主鍵 (ID)，並且只有當 ID 不是 null 時才比較
				return actId != null && Objects.equals(actId, couponVO.actId);
			}

			@Override
			public int hashCode() {
				// 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
				// 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
				// 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
				return getClass().hashCode();
			}

}