package com.foodietime.memfavlist.model;

import java.io.Serializable;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(FavoriteListId.class) //複合主鍵
@Table(name = "favorite_list")
public class FavoriteListVO implements Serializable{
	
	@Id
	@Column(name="mem_id",nullable = false)
	private Integer memId;
	
	@Id
	@Column(name="prod_id",nullable = false)
	private Integer prodId;
	
}
