package com.foodietime.gbprod.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.gbprod.dto.ReviewDTO;
import com.foodietime.grouporders.model.GroupOrdersVO;
import com.foodietime.grouporders.model.GroupOrdersService;


@Service
public class ReviewService {
	
	@Autowired
	public GroupOrdersService groupOrdersService;
	
	
    public List<ReviewDTO> getReview(Integer gbId) {
    	
        List<GroupOrdersVO> gbOrderList = groupOrdersService.getOrdersByGroupBuyingCaseId(gbId);
        
        if (gbOrderList == null || gbOrderList.isEmpty()) {
            return null;
        }
        List<ReviewDTO> reviewList = new ArrayList<>();
        for (GroupOrdersVO gbOrder : gbOrderList) {
            ReviewDTO dto = convertToReviewDTO(gbOrder);
            reviewList.add(dto);
        }
        
        return reviewList;
    }
    public ReviewDTO convertToReviewDTO(GroupOrdersVO gbOrder) {
    	ReviewDTO dto = new ReviewDTO();
    	dto.setComment(gbOrder.getComment());
    	dto.setRating(gbOrder.getRating());
    	dto.setReviewerName(gbOrder.getParName());
    	
    	return dto;
    }
}
