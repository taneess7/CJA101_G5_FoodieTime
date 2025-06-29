package com.foodietime.act.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodietime.store.model.StoreVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name= "activity_participation")
public class ActParticipationVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@JsonIgnore //Jackson 無法自動處理懶加載的 JPA 關聯對象，會噴 500
	@ManyToOne
    @JoinColumn(name = "STOR_ID", referencedColumnName = "STOR_ID",nullable = true) // 外鍵名稱
	private StoreVO store;

	
	//2-1.店家id欄位 
	@Transient // ➜ 不映射到資料庫，只拿來接值用
	private Integer storId;
	
	
	@ManyToOne
    @JoinColumn(name = "ACT_ID", referencedColumnName = "ACT_ID" ,nullable = true)
    private ActVO act;

	
	@JsonIgnore
    private Timestamp joinedTime;

}
