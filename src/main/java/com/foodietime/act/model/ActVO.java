package com.foodietime.act.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.foodietime.orddet.model.OrdDetVO;
import com.foodietime.orders.model.OrdersVO;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.foodietime.orders.model.OrdersVO;
import com.foodietime.store.model.StoreVO;
import com.foodietime.storeCate.model.StoreCateVO;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
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
	@ManyToOne
    @JoinColumn(name = "STOR_ID", referencedColumnName = "STOR_ID") // 外鍵名稱
	private StoreVO store;
//	private Integer storId;
	
	public StoreVO getStore() { 
		return store;
	}

	public void setStore(StoreVO store) {
		this.store = store;
	}
	
	
	// 3.活動類型
	@NotEmpty(message="活動類型: 請勿空白")
	@Column(name = "ACT_CATE", length = 50)
	private String actCate; 
	

	// 4.活動名稱
	@NotEmpty(message="活動名稱: 請勿空白")
	@Column(name = "ACT_NAME")
	private String actName; 
	
	// 5.活動內容
	@NotEmpty(message="活動內容: 請勿空白")
	@Column(name = "ACT_CONTENT", length = 255)
	private String actContent; 
	
	// 6.活動建立時間
	@NotEmpty
	@CreationTimestamp
	@Column(name = "ACT_LAUNCHTIME" , updatable = false)
	private Timestamp actSetTime; 
	
	 
	//使用活動折扣需同時符合:活動狀態上架、活動開始時間
	// 7.活動開始時間
	@NotEmpty(message="活動開始時間: 請勿空白")
	@Column(name = "ACT_STARTTIME")
	private Timestamp actStartTime; 
	
	// 8.活動結束時間 
	@NotEmpty(message="活動結束時間: 請勿空白")
	@Column(name = "ACT_ENDTIME")
	private Timestamp actEndTime; 
	
	// 9.活動狀態 (0:未上架,1:上架) 
	@Column(name = "ACT_STATUS")
	private Byte actStatus; 
	
	// 10.折扣類型 (0:百分比,1:金額)
	@Column(name = "ACT_DISCOUNT")
	private Byte actDiscount; 
	
	// 11.折扣值  
	@NotEmpty(message="折扣值: 請勿空白")
	@Column(name = "ACT_DISCOUNT_VALUE")
	private Double actDiscValue; 
	
	// 12.活動圖片
	@Column(name = "ACT_PHOTO")
	private byte[] actPhoto; 
	
	// 13.最後更新時間
	@Column(name = "ACT_LAST_UPDATE")
	@UpdateTimestamp 
	private Timestamp actLastUpdate; 

	
	// OneToMany
	
	@OneToMany(mappedBy = "actId", cascade = CascadeType.ALL)
    private List<OrdersVO> orders;
	
	// 取得or設置
	public ActVO() {
		super();
	}

}