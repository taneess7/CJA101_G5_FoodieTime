package com.foodietime.directmessage.model;

import java.sql.Timestamp;
import java.util.List;



public class DirectMessageService {
	private DirectMessageDAO_interface dao;

	public DirectMessageService() {
		dao = new DirectMessageDAOJDBC();
	}

	public DirectMessageVO addDm( Integer memId, Integer smgrId, String messContent,java.sql.Timestamp messTime,Integer messDirection) {

		DirectMessageVO dmVO = new DirectMessageVO();

		dmVO.setMemId(memId);
		dmVO.setSmgrId(smgrId);
		dmVO.setMessContent(messContent);
		dmVO.setMessTime(messTime);
		dmVO.setMessDirection(messDirection);
		dao.insert(dmVO);

		return dmVO;
	}

	public DirectMessageVO updateDm(Integer dmId, Integer memId,Integer smgrId, String messContent, Integer messDirection)
{

		DirectMessageVO dmVO = new DirectMessageVO();
		
		dmVO.setDmId(dmId);
		dmVO.setMemId(memId);
		dmVO.setSmgrId(smgrId);
		dmVO.setMessContent(messContent);
//		dmVO.setMessTime(messTime);
		dmVO.setMessDirection(messDirection);
		dao.update(dmVO);

		return dmVO;
	}
	
	public DirectMessageVO getOneDm(Integer dmId) {
		return dao.findByPrimaryKey(dmId);
	}

	public List<DirectMessageVO> getAll() {
		return dao.getAll();
	}
	public void delete(Integer dmId) {
		dao.delete(dmId);
	}
}
