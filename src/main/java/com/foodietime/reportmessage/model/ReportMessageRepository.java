package com.foodietime.reportmessage.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodietime.favoritepost.model.FavoritePostVO;

@Repository
public interface ReportMessageRepository extends JpaRepository<ReportMessageVO, Integer>{

}
