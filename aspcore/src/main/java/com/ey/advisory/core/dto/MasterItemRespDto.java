package com.ey.advisory.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MasterItemRespDto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("gstinPan")
	private String gstinPan;
	
	@Expose
	@SerializedName("itmCode")
	private String itmCode;
	
	@Expose
	@SerializedName("itemDesc")
	private String itemDesc;
	
	@Expose
	@SerializedName("itmCategory")
	private String itmCategory;
	
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
	@SerializedName("ifYCirularNotificationNum")
	private String ifYCirularNotificationNum;
	
	@Expose
	@SerializedName("ifYCirularNotificationDate")
	private LocalDate ifYCirularNotificationDate;
	
	@Expose
	@SerializedName("efCircularDate")
	private LocalDate efCircularDate;
	
	@Expose
	@SerializedName("rate")
	private BigDecimal rate;

	@Expose
	@SerializedName("elgblIndicator")
	private String elgblIndicator;
	
	@Expose
	@SerializedName("perOfElgbl")
	private BigDecimal perOfElgbl;
	
	@Expose
	@SerializedName("commonSuppIndicator")
	private String commonSuppIndicator;
	
	@Expose
	@SerializedName("itcReversalIdentifier")
	private String  itcReversalIdentifier;
	
	@Expose
	@SerializedName("itcsEntitlement")
	private String itcsEntitlement;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
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

	public String getItmCode() {
	    return itmCode;
	}

	public void setItmCode(String itmCode) {
	    this.itmCode = itmCode;
	}

	public String getItemDesc() {
	    return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
	    this.itemDesc = itemDesc;
	}

	public String getItmCategory() {
	    return itmCategory;
	}

	public void setItmCategory(String itmCategory) {
	    this.itmCategory = itmCategory;
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

	public String getIfYCirularNotificationNum() {
	    return ifYCirularNotificationNum;
	}

	public void setIfYCirularNotificationNum(String ifYCirularNotificationNum) {
	    this.ifYCirularNotificationNum = ifYCirularNotificationNum;
	}

	public LocalDate getIfYCirularNotificationDate() {
	    return ifYCirularNotificationDate;
	}

	public void setIfYCirularNotificationDate(
		LocalDate ifYCirularNotificationDate) {
	    this.ifYCirularNotificationDate = ifYCirularNotificationDate;
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

	public String getElgblIndicator() {
	    return elgblIndicator;
	}

	public void setElgblIndicator(String elgblIndicator) {
	    this.elgblIndicator = elgblIndicator;
	}

	public BigDecimal getPerOfElgbl() {
	    return perOfElgbl;
	}

	public void setPerOfElgbl(BigDecimal perOfElgbl) {
	    this.perOfElgbl = perOfElgbl;
	}

	public String getCommonSuppIndicator() {
	    return commonSuppIndicator;
	}

	public void setCommonSuppIndicator(String commonSuppIndicator) {
	    this.commonSuppIndicator = commonSuppIndicator;
	}

	public String getItcReversalIdentifier() {
	    return itcReversalIdentifier;
	}

	public void setItcReversalIdentifier(String itcReversalIdentifier) {
	    this.itcReversalIdentifier = itcReversalIdentifier;
	}

	public String getItcsEntitlement() {
	    return itcsEntitlement;
	}

	public void setItcsEntitlement(String itcsEntitlement) {
	    this.itcsEntitlement = itcsEntitlement;
	}

	@Override
	public String toString() {
	    return "MasterItemRespDto [id=" + id + ", gstinPan=" + gstinPan
		    + ", itmCode=" + itmCode + ", itemDesc=" + itemDesc
		    + ", itmCategory=" + itmCategory + ", hsnOrSac=" + hsnOrSac
		    + ", uom=" + uom + ", reverseChargeFlag="
		    + reverseChargeFlag + ", tdsFlag=" + tdsFlag
		    + ", diffPercent=" + diffPercent + ", nilOrNonOrExmt="
		    + nilOrNonOrExmt + ", ifYCirularNotificationNum="
		    + ifYCirularNotificationNum
		    + ", ifYCirularNotificationDate="
		    + ifYCirularNotificationDate + ", efCircularDate="
		    + efCircularDate + ", rate=" + rate + ", elgblIndicator="
		    + elgblIndicator + ", perOfElgbl=" + perOfElgbl
		    + ", commonSuppIndicator=" + commonSuppIndicator
		    + ", itcReversalIdentifier=" + itcReversalIdentifier
		    + ", itcsEntitlement=" + itcsEntitlement + "]";
	}
}