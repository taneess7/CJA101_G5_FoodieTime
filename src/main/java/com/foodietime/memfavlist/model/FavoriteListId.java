package com.foodietime.memfavlist.model;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteListId implements Serializable{
	
	private Integer memId;
    private Integer prodId;

    public FavoriteListId() {
    }

    public FavoriteListId(Integer memId, Integer prodId) {
        this.memId = memId;
        this.prodId = prodId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteListId)) return false;
        FavoriteListId that = (FavoriteListId) o;
        return Objects.equals(memId, that.memId) &&
               Objects.equals(prodId, that.prodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memId, prodId);
    }
}
