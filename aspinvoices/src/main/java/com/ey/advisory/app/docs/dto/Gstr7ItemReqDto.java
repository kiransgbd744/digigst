package com.ey.advisory.app.docs.dto;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr7ItemReqDto {

	@Expose
	@SerializedName("retPrd")
	private String returnPeriod;

	@Expose
	@SerializedName("actType")
	private String actType;

	@Expose
	@SerializedName("deductrGstin")
	private String tdsGstin;

	@Expose
	@SerializedName("ogDedcteGstin")
	private String orgTdsGstin;

	@Expose
	@SerializedName("orgRetPrd")
	private String orgRetPeriod;

	@Expose
	@SerializedName("orgGrossAmt")
	private String orgGrossAmt;

	@Expose
	@SerializedName("dedcteGstin")
	private String newGstin;

	@Expose
	@SerializedName("grossAmt")
	private String newGrossAmt;

	@Expose
	@SerializedName("igst")
	private String igstAmt;

	@Expose
	@SerializedName("cgst")
	private String cgstAmt;

	@Expose
	@SerializedName("sgst")
	private String sgstAmt;

	@Expose
	@SerializedName("conNumber")
	private String conNumber;

	@Expose
	@SerializedName("contractDt")
	private String conDate;

	@Expose
	@SerializedName("contractVal")
	private String conValue;

	@Expose
	@SerializedName("pmtAdvNum")
	private String payNum;

	@Expose
	@SerializedName("pmtAdvDt")
	private String payDate;

	@Expose
	@SerializedName("docNo")
	private String docNum;

	@Expose
	@SerializedName("docDt")
	private String docDate;
	
	@Expose
	@SerializedName("invVal")
	private String invValue;

	@Expose
	@SerializedName("plantCode")
	private String plantCode;

	@Expose
	@SerializedName("division")
	private String division;

	@Expose
	@SerializedName("purchOrg")
	private String purOrg;

	@Expose
	@SerializedName("prftCenter1")
	private String proCen1;

	@Expose
	@SerializedName("prftCenter2")
	private String proCen2;

	@Expose
	@SerializedName("udf1")
	private String usrDefField1;

	@Expose
	@SerializedName("udf2")
	private String usrDefField2;

	@Expose
	@SerializedName("udf3")
	private String usrDefField3;

	@Expose
	@SerializedName("createdBy")
	private String createdBy;

	@Expose
	@SerializedName("createdOn")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	private String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	private LocalDateTime modifiedOn;
}
