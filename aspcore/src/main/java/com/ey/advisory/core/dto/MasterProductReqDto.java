package com.ey.advisory.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MasterProductReqDto {

    @Expose
    @SerializedName("id")
    private Long id;

    @Expose
    @SerializedName("gstinPan")
    private String gstinPan;

    @Expose
    @SerializedName("productCode")
    private String productCode;

    @Expose
    @SerializedName("productDesc")
    private String productDesc;

    @Expose
    @SerializedName("productCategory")
    private String productCategory;

    @Expose
    @SerializedName("hsnOrSac")
    private Integer hsnOrSac;

    @Expose
    @SerializedName("uom")
    private String uom;

    @Expose
    @SerializedName("reverseChargeFlag")
    private String reverseChargeFlag;

    @Expose
    @SerializedName("tdsFlag")
    private String tdsFlag;

    @Expose
    @SerializedName("diffPercent")
    private String diffPercent;

    @Expose
    @SerializedName("nilOrNonOrExmt")
    private String nilOrNonOrExmt;

    @Expose
    @SerializedName("ifYCircularNotificationNum")
    private String ifYCircularNotificationNum;

    @Expose
    @SerializedName("notificationDate")
    private LocalDate notificationDate;

    @Expose
    @SerializedName("efCircularDate")
    private LocalDate efCircularDate;

    @Expose
    @SerializedName("rate")
    private BigDecimal rate;

    @Expose
    @SerializedName("itcFlag")
    private String itcFlag;

    /**
     * @return the id
     */
    public Long getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
	this.id = id;
    }

    public String getGstinPan() {
	return gstinPan;
    }

    public void setGstinPan(String gstinPan) {
	this.gstinPan = gstinPan;
    }

    public String getProductCode() {
	return productCode;
    }

    public void setProductCode(String productCode) {
	this.productCode = productCode;
    }

    public String getProductDesc() {
	return productDesc;
    }

    public void setProductDesc(String productDesc) {
	this.productDesc = productDesc;
    }

    public String getProductCategory() {
	return productCategory;
    }

    public void setProductCategory(String productCategory) {
	this.productCategory = productCategory;
    }

    public Integer getHsnOrSac() {
	return hsnOrSac;
    }

    public void setHsnOrSac(Integer hsnOrSac) {
	this.hsnOrSac = hsnOrSac;
    }

    public String getUom() {
	return uom;
    }

    public void setUom(String uom) {
	this.uom = uom;
    }

    public String getReverseChargeFlag() {
	return reverseChargeFlag;
    }

    public void setReverseChargeFlag(String reverseChargeFlag) {
	this.reverseChargeFlag = reverseChargeFlag;
    }

    public String getTdsFlag() {
	return tdsFlag;
    }

    public void setTdsFlag(String tdsFlag) {
	this.tdsFlag = tdsFlag;
    }

    public String getDiffPercent() {
	return diffPercent;
    }

    public void setDiffPercent(String diffPercent) {
	this.diffPercent = diffPercent;
    }

    public String getNilOrNonOrExmt() {
	return nilOrNonOrExmt;
    }

    public void setNilOrNonOrExmt(String nilOrNonOrExmt) {
	this.nilOrNonOrExmt = nilOrNonOrExmt;
    }

    public String getIfYCircularNotificationNum() {
	return ifYCircularNotificationNum;
    }

    public void setIfYCircularNotificationNum(
	    String ifYCircularNotificationNum) {
	this.ifYCircularNotificationNum = ifYCircularNotificationNum;
    }

    public LocalDate getNotificationDate() {
	return notificationDate;
    }

    public void setNotificationDate(LocalDate notificationDate) {
	this.notificationDate = notificationDate;
    }

    public LocalDate getEfCircularDate() {
	return efCircularDate;
    }

    public void setEfCircularDate(LocalDate efCircularDate) {
	this.efCircularDate = efCircularDate;
    }

    public BigDecimal getRate() {
	return rate;
    }

    public void setRate(BigDecimal rate) {
	this.rate = rate;
    }

    public String getItcFlag() {
	return itcFlag;
    }

    public void setItcFlag(String itcFlag) {
	this.itcFlag = itcFlag;
    }

    @Override
    public String toString() {
	return "MasterProductReqDto [id=" + id + ", gstinPan=" + gstinPan
		+ ", productCode=" + productCode + ", productDesc="
		+ productDesc + ", productCategory=" + productCategory
		+ ", hsnOrSac=" + hsnOrSac + ", uom=" + uom
		+ ", reverseChargeFlag=" + reverseChargeFlag + ", tdsFlag="
		+ tdsFlag + ", diffPercent=" + diffPercent + ", nilOrNonOrExmt="
		+ nilOrNonOrExmt + ", ifYCircularNotificationNum="
		+ ifYCircularNotificationNum + ", notificationDate="
		+ notificationDate + ", efCircularDate=" + efCircularDate
		+ ", rate=" + rate + ", itcFlag=" + itcFlag + "]";
    }
}