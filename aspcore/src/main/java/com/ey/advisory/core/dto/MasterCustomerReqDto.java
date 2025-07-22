package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MasterCustomerReqDto {

    @Expose
    @SerializedName("id")
    private Long id;

    @Expose
    @SerializedName("supplierGstnOrPan")
    private String supplierGstnOrPan;

    @Expose
    @SerializedName("recipientGstnOrPan")
    private String recipientGstnOrPan;

    @Expose
    @SerializedName("legalName")
    private String legalName;

    @Expose
    @SerializedName("tradeName")
    private String tradeName;

    @Expose
    @SerializedName("recipientType")
    private String recipientType;

    @Expose
    @SerializedName("recipientCode")
    private String recipientCode;

    @Expose
    @SerializedName("outSideIndia")
    private String outSideIndia;

    @Expose
    @SerializedName("emailId")
    private String emailId;

    @Expose
    @SerializedName("mobileNum")
    private String mobileNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierGstnOrPan() {
        return supplierGstnOrPan;
    }

    public void setSupplierGstnOrPan(String supplierGstnOrPan) {
        this.supplierGstnOrPan = supplierGstnOrPan;
    }

    public String getRecipientGstnOrPan() {
        return recipientGstnOrPan;
    }

    public void setRecipientGstnOrPan(String recipientGstnOrPan) {
        this.recipientGstnOrPan = recipientGstnOrPan;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public String getRecipientCode() {
        return recipientCode;
    }

    public void setRecipientCode(String recipientCode) {
        this.recipientCode = recipientCode;
    }

    public String getOutSideIndia() {
        return outSideIndia;
    }

    public void setOutSideIndia(String outSideIndia) {
        this.outSideIndia = outSideIndia;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    @Override
    public String toString() {
	return "MasterCustomerReqDto [id=" + id + ", supplierGstnOrPan="
		+ supplierGstnOrPan + ", recipientGstnOrPan="
		+ recipientGstnOrPan + ", legalName=" + legalName
		+ ", tradeName=" + tradeName + ", recipientType="
		+ recipientType + ", recipientCode=" + recipientCode
		+ ", outSideIndia=" + outSideIndia + ", emailId=" + emailId
		+ ", mobileNum=" + mobileNum + "]";
    }
}
