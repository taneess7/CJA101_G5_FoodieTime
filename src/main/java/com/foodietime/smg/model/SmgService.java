package com.foodietime.smg.model;

import java.util.List;

public class SmgService {
	private SmgDAO_interface dao;

	public SmgService() {
		dao = new SmgDAO();
	}

	public SmgVO addSmg(String smgrEmail, String smgrAccount, String smgrPassword, String smgrName, String smgrPhone) {

		SmgVO smgVO = new SmgVO();

		smgVO.setSmgrEmail(smgrEmail);
		smgVO.setSmgrAccount(smgrAccount);
		smgVO.setSmgrPassword(smgrPassword);
		smgVO.setSmgrName(smgrName);
		smgVO.setSmgrPhone(smgrPhone);
		dao.insert(smgVO);

		return smgVO;
	}

	public SmgVO updateSmg(Integer smgId, String smgrEmail, String smgrAccount, String smgrPassword, String smgrName,
			String smgrPhone,Integer smgrStatus) {

		SmgVO smgVO = new SmgVO();

		smgVO.setSmgId(smgId);
		smgVO.setSmgrEmail(smgrEmail);
		smgVO.setSmgrAccount(smgrAccount);
		smgVO.setSmgrPassword(smgrPassword);
		smgVO.setSmgrName(smgrName);
		smgVO.setSmgrPhone(smgrPhone);
		smgVO.setSmgrStatus(smgrStatus);
		dao.update(smgVO);

		return smgVO;
	}
	
	public SmgVO getOneSmg(Integer smgId) {
		return dao.findByPrimaryKey(smgId);
	}
	public boolean isAccountExists(String smgrAccount) {
        return dao.isAccountExist(smgrAccount);
    }
	public List<SmgVO> getAll() {
		return dao.getAll();
	}
}
