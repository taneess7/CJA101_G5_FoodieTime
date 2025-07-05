package com.foodietime.act.model;

import lombok.Data;

@Data // constructor 用來配合 JPQL new 語法 	，顯示參與活動的店家 (dto) 沒有用到
public class ActStoreDTO {

	
	   private Integer actId;
	    private String actName;
	    private Integer storId;
	    private String storName;

	    
	    public ActStoreDTO(Integer actId, String actName, Integer storId, String storName) {
	        this.actId = actId;
	        this.actName = actName;
	        this.storId = storId;
	        this.storName = storName;
	    }
}
