package com.foodietime.directmessage.model;

import java.util.List;

public interface DirectMessageDAO_interface {
	public void insert(DirectMessageVO dmVO);
	public void update(DirectMessageVO dmVO);
	public DirectMessageVO findByPrimaryKey(Integer dmId);
	public List<DirectMessageVO> getAll();
	public void delete(Integer dmId);
}
