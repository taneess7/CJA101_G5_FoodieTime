package com.foodietime.store.model;

import java.sql.Time;

import java.util.List;



public class StoreService {

	private StoreDAO_interface dao;

	public StoreService() {
		dao = new StoreDAO();
	}

	public StoreVO addStore(Integer storeCateId, String storName, String storDesc, String storAddr, Double storLon, Double storLat, String storPhone, String storWeb, Time storOnTime,  Time storOffTime,String storOffDay,Byte storDeliver,Byte storStatus,byte[] storPhoto, Byte storReportCount,Integer starNum,Integer reviews) {

		StoreVO storeVO = new StoreVO();

		storeVO.setStoreCateId(storeCateId);
		storeVO.setStorName(storName);
		storeVO.setStorDesc(storDesc);
		storeVO.setStorAddr(storAddr);
		storeVO.setStorLon(storLon);
		storeVO.setStorLat(storLat);
		storeVO.setStorPhone(storPhone);
		storeVO.setStorWeb(storWeb);
		storeVO.setStorOnTime(storOnTime);
		storeVO.setStorOffTime(storOffTime);
		storeVO.setStorOffDay(storOffDay);
		storeVO.setStorDeliver(storDeliver);
		storeVO.setStorStatus(storStatus);
		storeVO.setStorPhoto(storPhoto);
		storeVO.setStorReportCount(storReportCount);
		storeVO.setStarNum(starNum);
		storeVO.setReviews(reviews);
		dao.insert(storeVO);
		
		return storeVO;
	}

	public StoreVO updateStore(Integer storId, Integer storeCateId, String storName, String storDesc, String storAddr, Double storLon, Double storLat, String storPhone, String storWeb, Time storOnTime,  Time storOffTime,String storOffDay,Byte storDeliver,Byte storStatus,byte[] storPhoto, Byte storReportCount,Integer starNum,Integer reviews) {

		StoreVO storeVO = new StoreVO();

		storeVO.setStorId(storId);
		storeVO.setStoreCateId(storeCateId);
		storeVO.setStorName(storName);
		storeVO.setStorDesc(storDesc);
		storeVO.setStorAddr(storAddr);
		storeVO.setStorLon(storLon);
		storeVO.setStorLat(storLat);
		storeVO.setStorPhone(storPhone);
		storeVO.setStorWeb(storWeb);
		storeVO.setStorOnTime(storOnTime);
		storeVO.setStorOffTime(storOffTime);
		storeVO.setStorOffDay(storOffDay);
		storeVO.setStorDeliver(storDeliver);
		storeVO.setStorStatus(storStatus);
		storeVO.setStorPhoto(storPhoto);
		storeVO.setStorReportCount(storReportCount);
		storeVO.setStarNum(starNum);
		storeVO.setReviews(reviews);
		
		dao.update(storeVO);

		return storeVO;
	}

	public void deleteStore(Integer storId) {
		dao.delete(storId);
	}

	public StoreVO getOneStore(Integer storId) {
		return dao.findByPrimaryKey(storId);
	}

	public List<StoreVO> getAll() {
		return dao.getAll();
	}
}