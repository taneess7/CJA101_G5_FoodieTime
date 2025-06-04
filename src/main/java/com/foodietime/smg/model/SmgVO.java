package com.foodietime.smg.model;

import java.io.Serializable;
import java.util.Set;

import com.foodietime.smgauth.model.SmgauthVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "servermanager")
public class SmgVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGR_ID")
    private Integer smgrId;

    @Column(name = "SMGR_EMAIL", length = 45)
    private String smgrEmail;

    @Column(name = "SMGR_ACCOUNT", length = 45)
    private String smgrAccount;

    @Column(name = "SMGR_PASSWORD", length = 128)
    private String smgrPassword;

    @Column(name = "SMGR_NAME", length = 45)
    private String smgrName;

    @Column(name = "SMGR_PHONE", length = 10)
    private String smgrPhone;

    @Column(name = "SMGR_STATUS")
    private Byte smgrStatus;

    // 與 SmgauthVO (權限關聯) 一對多
    @OneToMany(mappedBy = "smg", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SmgauthVO> smgauths;

	public Integer getSmgrId() {
		return smgrId;
	}

	public void setSmgrId(Integer smgrId) {
		this.smgrId = smgrId;
	}

	public String getSmgrEmail() {
		return smgrEmail;
	}

	public void setSmgrEmail(String smgrEmail) {
		this.smgrEmail = smgrEmail;
	}

	public String getSmgrAccount() {
		return smgrAccount;
	}

	public void setSmgrAccount(String smgrAccount) {
		this.smgrAccount = smgrAccount;
	}

	public String getSmgrPassword() {
		return smgrPassword;
	}

	public void setSmgrPassword(String smgrPassword) {
		this.smgrPassword = smgrPassword;
	}

	public String getSmgrName() {
		return smgrName;
	}

	public void setSmgrName(String smgrName) {
		this.smgrName = smgrName;
	}

	public String getSmgrPhone() {
		return smgrPhone;
	}

	public void setSmgrPhone(String smgrPhone) {
		this.smgrPhone = smgrPhone;
	}

	public Byte getSmgrStatus() {
		return smgrStatus;
	}

	public void setSmgrStatus(Byte smgrStatus) {
		this.smgrStatus = smgrStatus;
	}

	public Set<SmgauthVO> getSmgauths() {
		return smgauths;
	}

	public void setSmgauths(Set<SmgauthVO> smgauths) {
		this.smgauths = smgauths;
	}
    
}
