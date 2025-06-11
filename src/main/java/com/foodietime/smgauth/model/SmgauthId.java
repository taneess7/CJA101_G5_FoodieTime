package com.foodietime.smgauth.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SmgauthId implements Serializable {

    @Column(name = "SMGR_ID")
    private Integer smgId;

    @Column(name = "SMGEFUNC_ID")
    private Integer smgFuncId;

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
