package com.foodietime.participants.model;

import java.util.*;

public interface ParticipantsDAO_interface {

	public void insert(ParticipantsVO participantsVO);
	public void update(ParticipantsVO participantsVO);
	
	public ParticipantsVO findByPrimaryKey(Integer parId);
	public List<ParticipantsVO> getAll();
	//查詢某團購案的所有參與者
	public List<ParticipantsVO> findByGBId(Integer gbId);

}
