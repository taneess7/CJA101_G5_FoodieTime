package com.foodietime.message.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public void deleteMessage(Integer mesId) {
		// 實作軟刪除，將留言內容設為已刪除標記
		MessageVO message = repository.findById(mesId).orElse(null);
		if (message != null) {
			message.setMesContent("[此留言已被管理員刪除]");
			repository.save(message);
		}
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
	
	//供後台管理員使用的查詢方法，支援依貼文ID和關鍵字篩選
    public List<MessageVO> findMessagesForAdmin(Integer postId, String keyword) {
        // 如果 keyword 是空字串，將其視為 null，以便查詢忽略此條件
        String searchKeyword = (keyword != null && keyword.trim().isEmpty()) ? null : keyword;
        return repository.findForAdmin(postId, searchKeyword);
    }

    //批次刪除留言
    @Transactional
    public int batchDelete(List<Integer> mesIds) {
        if (mesIds == null || mesIds.isEmpty()) {
            return 0;
        }
        return repository.deleteByMesIdIn(mesIds);
    }
}
