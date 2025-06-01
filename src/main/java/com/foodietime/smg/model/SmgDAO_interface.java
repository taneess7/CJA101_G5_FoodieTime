package com.foodietime.smg.model;



import java.util.*;

public  interface SmgDAO_interface {
	public void insert(SmgVO smgVO);    
    public void update(SmgVO smgVO);    
    public SmgVO findByPrimaryKey(Integer smgId);
    public boolean isAccountExist(String smgrAccount);
    public List<SmgVO> getAll();
}
