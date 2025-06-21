package com.foodietime.smg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.foodietime.accrec.model.AccrecVO;
import com.foodietime.directmessage.model.DirectMessageVO;
import com.foodietime.smgauth.model.SmgauthVO;
import com.foodietime.validation.PasswordPatternIfPresent;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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


    @Size(min = 4, max = 20, message = "密碼長度需介於 4 到 20 字")
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
    @OneToMany(mappedBy = "smg", fetch = FetchType.LAZY)
    private List<SmgauthVO> smgauths= new ArrayList<>();
    
    @OneToMany(mappedBy = "smgr", cascade = CascadeType.ALL)
    private List<DirectMessageVO> directMessages = new ArrayList<>();
    
    @PasswordPatternIfPresent
    @Transient // 不存入資料庫
    private String newPassword;
    
    @Transient  // JPA 不會將它映射進資料表
    //對新增的確認
    private String confirmPassword;
    
    @Transient // 用於接收表單的權限字符串列表
    private List<String> permissions;
    
    // 獲取權限字符串列表（用於表單顯示）
    public List<String> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<>();
            if (smgauths != null) {
                for (SmgauthVO smgauth : smgauths) {
                    if (smgauth.getSmgfc() != null) {
                        permissions.add(smgauth.getSmgfc().getSmgFunc());
                    }
                }
            }
        }
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    
    // 輔助方法：根據權限字符串創建 SmgauthVO 列表
    public void setPermissionsFromStrings(List<String> permissionStrings, com.foodietime.smgfc.model.SmgfcService smgfcService) {
        // 創建新的權限列表，避免操作已刪除的關聯
        List<SmgauthVO> newSmgauths = new ArrayList<>();
        if (permissionStrings != null) {
            for (String permission : permissionStrings) {
                com.foodietime.smgfc.model.SmgfcVO smgfc = smgfcService.findByFunctionName(permission);
                if (smgfc != null) {
                    SmgauthVO smgauth = new SmgauthVO();
                    com.foodietime.smgauth.model.SmgauthId id = new com.foodietime.smgauth.model.SmgauthId(smgfc.getSmgFuncId(), this.smgrId);
                    smgauth.setId(id);
                    // 不設置反向關聯，避免級聯衝突
                    // smgauth.setSmg(this);
                    smgauth.setSmgfc(smgfc);
                    newSmgauths.add(smgauth);
                }
            }
        }
        // 直接替換整個列表，避免clear()操作
        this.smgauths = newSmgauths;
    }

    
}
