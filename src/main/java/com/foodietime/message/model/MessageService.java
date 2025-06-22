package com.foodietime.message.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.reportpost.model.ReportPostVO;

@Service("Message")
public class MessageService {

	@Autowired
	private MessageRepository repository;

	public MessageVO save(MessageVO vo) {
		return repository.save(vo);
	}

	public MessageVO updateMessage(Integer mesId, Integer postId, Integer memId, java.sql.Timestamp mesDate,
			String mesContent) {
		MessageVO messageVO = new MessageVO();

		MemberVO member = new MemberVO();
		member.setMemId(memId);
		PostVO post = new PostVO();
		post.setPostId(postId);

		messageVO.setMesId(mesId);
		messageVO.setPost(post);
		messageVO.setMember(member);
		messageVO.setMesDate(mesDate);
		messageVO.setMesContent(mesContent);
		return repository.save(messageVO);
	}

	public void deleteMessage(Integer mesId) {
		repository.deleteById(mesId);
	}

	public MessageVO getOneMessage(Integer mesId) {
		return repository.findById(mesId).orElse(null);
	}

	public List<MessageVO> getAll() {
		return repository.findAll();
	}

//	public List<MessageVO> getByMemId(Integer memId) {
//		return repository.findByMemId(memId);
//	}

	public List<MessageVO> getByPostId(Integer postId) {
	    return repository.findByPost_PostId(postId);
	}
}
