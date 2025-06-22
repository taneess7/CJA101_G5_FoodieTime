package com.foodietime.message.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageVO, Integer>{

	List<MessageVO> findByPost_PostId(Integer postId);
}
