package com.foodietime.message.model;
import java.util.List;

//import org.eclipse.tags.shaded.org.apache.bcel.generic.RETURN;



public class MessageService {
	
	private MessageDAO_interface dao;
		
	public MessageService() {
			dao = new MessageDAO();
		}
	public MessageVO addMessage(Integer postId, Integer memId, java.sql.Timestamp mesDate, String mesContent) {
		MessageVO messageVO = new MessageVO();
//		messageVO.setPostId(postId);
//		messageVO.setMemId(memId);
		messageVO.setMesDate(mesDate);
		messageVO.setMesContent(mesContent);
		dao.insert(messageVO);
		return messageVO;
	}
	
	public MessageVO updateMessage(Integer mesId, Integer postId, Integer memId, java.sql.Timestamp mesDate, String mesContent) {
		MessageVO messageVO = new MessageVO();
		messageVO.setMesId(mesId);
//		messageVO.setPostId(postId);
//		messageVO.setMemId(memId);		
		messageVO.setMesDate(mesDate);
		messageVO.setMesContent(mesContent);
		dao.update(messageVO);
		return messageVO;
	}
	
	public void deleteMessage(Integer mesId) {
		dao.delete(mesId);
	}
	
	public MessageVO getOneMessage(Integer mesId) {
		return dao.findByPrimaryKey(mesId);
	}
	
	public List<MessageVO> getAll() {
		return dao.getALL();
	}
	
	public List<MessageVO> getByMemId(Integer memId) {
		return dao.findByMemId(memId);
	}
	
	public List<MessageVO> getByPostId(Integer postId) {
		return dao.findByPostId(postId);
	}
	
}

