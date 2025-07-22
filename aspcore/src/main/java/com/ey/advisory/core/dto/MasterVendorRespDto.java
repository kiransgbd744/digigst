package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MasterVendorRespDto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("custGstinPan")
	private String custGstinPan;
	
	@Expose
	@SerializedName("supplierGstinPan")
	private String supplierGstinPan;
	
	@Expose
	@SerializedName("supplierCode")
	private String supplierCode;
	
	@Expose
	@SerializedName("supplierName")
	private String supplierName;
	
	@Expose
	@SerializedName("supplierType")
	private String supplierType;
	
	@Expose
	@SerializedName("outSideIndia")
	private String outSideIndia;
	
	@Expose
	@SerializedName("emailId")
	private String emailId;
	
	@Expose
	@SerializedName("mobileNum")
	private String mobileNum;

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

	public String getCustGstinPan() {
	    return custGstinPan;
	}

	public void setCustGstinPan(String custGstinPan) {
	    this.custGstinPan = custGstinPan;
	}

	public String getSupplierCode() {
	    return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
	    this.supplierCode = supplierCode;
	}

	public String getSupplierGstinPan() {
	    return supplierGstinPan;
	}

	public void setSupplierGstinPan(String supplierGstinPan) {
	    this.supplierGstinPan = supplierGstinPan;
	}

	public String getSupplierName() {
	    return supplierName;
	}

	public void setSupplierName(String supplierName) {
	    this.supplierName = supplierName;
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

	public String getSupplierType() {
	    return supplierType;
	}

	public void setSupplierType(String supplierType) {
	    this.supplierType = supplierType;
	}

	@Override
	public String toString() {
	    return "MasterVendorRespDto [id=" + id + ", custGstinPan="
		    + custGstinPan + ", supplierGstinPan=" + supplierGstinPan
		    + ", supplierCode=" + supplierCode + ", supplierName="
		    + supplierName + ", supplierType=" + supplierType
		    + ", outSideIndia=" + outSideIndia + ", emailId=" + emailId
		    + ", mobileNum=" + mobileNum + "]";
	}
}
