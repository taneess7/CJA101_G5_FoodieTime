package com.foodietime.smgauth.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SmgauthId implements Serializable {

    private Integer smgFuncId; // 對應 SMGEFUNC_ID
    private Integer smgId;     // 對應 SMGR_ID

    public SmgauthId() {}

    public SmgauthId(Integer smgFuncId, Integer smgId) {
        this.smgFuncId = smgFuncId;
        this.smgId = smgId;
    }

    public Integer getSmgFuncId() {
        return smgFuncId;
    }

    public void setSmgFuncId(Integer smgFuncId) {
        this.smgFuncId = smgFuncId;
    }

    public Integer getSmgId() {
        return smgId;
    }

    public void setSmgId(Integer smgId) {
        this.smgId = smgId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SmgauthId)) return false;
        SmgauthId that = (SmgauthId) o;
        return Objects.equals(smgFuncId, that.smgFuncId) &&
               Objects.equals(smgId, that.smgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(smgFuncId, smgId);
    }
}
