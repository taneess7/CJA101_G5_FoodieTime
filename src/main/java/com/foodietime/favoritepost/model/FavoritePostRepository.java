package com.foodietime.favoritepost.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritePostRepository extends JpaRepository<FavoritePostVO, Integer>{

}
