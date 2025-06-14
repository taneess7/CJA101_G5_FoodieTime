package com.foodietime.smg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.foodietime.accrec.model.AccrecVO;
import com.foodietime.directmessage.model.DirectMessageVO;
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"accrec", "smgauths", "directMessages"})
@Table(name = "servermanager")
public class SmgVO implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGR_ID")
	@EqualsAndHashCode.Include
    private Integer smgrId;
	
	@NotNull(message = "records 不可為空")
	@OneToMany(mappedBy = "serverManager")
    private List<AccrecVO> accrec= new ArrayList<>();
	
    @Email(message = "Email 格式錯誤")
    @NotBlank(message = "Email 不可為空")
    @Column(name = "SMGR_EMAIL", length = 45)
    private String smgrEmail;

    @NotBlank(message = "帳號不可為空")
    @Size(min = 4, max = 45, message = "帳號長度需介於 4 到 45 字")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,45}$", message = "帳號只能包含英數字，長度需為 4~45 字")
    @Column(name = "SMGR_ACCOUNT", length = 45)
    private String smgrAccount;


    @Size(min = 6, max = 128, message = "密碼長度需介於 6 到 128 字")
    @Column(name = "SMGR_PASSWORD", length = 128)
    private String smgrPassword;

    @NotBlank(message = "名稱不可為空")
    @Size(max = 45, message = "名稱長度不可超過 45 字")
    @Column(name = "SMGR_NAME", length = 45)
    private String smgrName;

    @Pattern(regexp = "\\d{10}", message = "電話格式錯誤，需為10位數字")
    @Column(name = "SMGR_PHONE", length = 10)
    private String smgrPhone;

    @NotNull(message = "狀態不可為空")
    @Column(name = "SMGR_STATUS")
    private Byte smgrStatus;

    // 與 SmgauthVO (權限關聯) 一對多  
    @OneToMany(mappedBy = "smg", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SmgauthVO> smgauths= new ArrayList<>();
    
    @OneToMany(mappedBy = "smgr", cascade = CascadeType.ALL)
    private List<DirectMessageVO> directMessages = new ArrayList<>();
    
    
    
    

    
}
