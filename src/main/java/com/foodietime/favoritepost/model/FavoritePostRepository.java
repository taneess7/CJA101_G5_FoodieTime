package com.foodietime.favoritepost.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritePostRepository extends JpaRepository<FavoritePostVO, FavoritePostId> {
    void deleteById(FavoritePostId id);
    Optional<FavoritePostVO> findById(FavoritePostId id);
}
