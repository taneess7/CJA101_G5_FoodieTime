package com.foodietime.message.model;

import java.util.*;

public interface MessageDAO_interface {
	
	public void insert(MessageVO messagevo);
	public void update(MessageVO messageVO);
	public void delete(Integer mesId);
	
	public MessageVO findByPrimaryKey(Integer mesId);
	public List<MessageVO> getALL();
	public List<MessageVO> findByMemId(Integer memId);
	public List<MessageVO> findByPostId(Integer postId);
	
}
