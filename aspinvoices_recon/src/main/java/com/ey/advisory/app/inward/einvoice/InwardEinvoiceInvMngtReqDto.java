package com.ey.advisory.app.inward.einvoice;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InwardEinvoiceInvMngtReqDto {

	@Expose
	@SerializedName("criteria")
	public String criteria;

	@Expose
	@SerializedName("entityId")
	public List<Long> entityId;

	@Expose
	@SerializedName("fromTaxPeriod")
	public String frmTaxPrd;
	
	@Expose
	@SerializedName("toTaxPeriod")
	public String toTaxPrd;
	
	@Expose
	@SerializedName("fromDate")
	public String fromDate;
	
	@Expose
	@SerializedName("toDate")
	public String toDate;

	@Expose
	@SerializedName("supplyType")
	public List<String> supplyType = new ArrayList();
	
	@Expose
	@SerializedName("irnStatus")
	public List<String> irnStatus = new ArrayList();
	
	@Expose
	@SerializedName("gstins")
	public List<String> gstins = new ArrayList();
	
	@Expose
	@SerializedName("irn")
	public String irn;
	
	@Expose
	@SerializedName("vendrGstin")
	public String vendrGstin;
	
	@Expose
	@SerializedName("docNum")
	public String docNum;
	
}
