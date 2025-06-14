package com.foodietime.smgauth.model;

import java.io.Serializable;

import com.foodietime.smg.model.SmgVO;
import com.foodietime.smgfc.model.SmgfcVO;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "servermanagerauth")
public class SmgauthVO implements Serializable {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private SmgauthId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("smgId") // 這裡對應 SmgauthId 的 smgId
    @JoinColumn(name = "SMGR_ID")
    private SmgVO smg;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("smgFuncId") // 這裡對應 SmgauthId 的 smgFuncId
    @JoinColumn(name = "SMGEFUNC_ID")
    private SmgfcVO smgfc;

}
