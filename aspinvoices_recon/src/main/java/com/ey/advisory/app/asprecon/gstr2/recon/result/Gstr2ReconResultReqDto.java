package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2ReconResultReqDto {

	@Expose
	@SerializedName("entityId")
	private String entityId;

	@Expose
	@SerializedName("reconType")
	private String reconType;

	@Expose
	@SerializedName("gstins")
	private List<String> gstins;

	@Expose
	@SerializedName("toTaxPeriod")
	private String toTaxPeriod;

	@Expose
	@SerializedName("fromTaxPeriod")
	private String fromTaxPeriod;

	@Expose
	@SerializedName("toDocDate")
	private String toDocDate;

	@Expose
	@SerializedName("fromDocDate")
	private String fromDocDate;

	@Expose
	@SerializedName("docType")
	private List<String> docType;

	@Expose
	@SerializedName("docNumber")
	private List<String> docNumber;

	@Expose
	@SerializedName("reportCatagory")
	private List<String> reportCatogary;

	@Expose
	@SerializedName("reportType")
	private List<String> reportType;

	@Expose
	@SerializedName("mismatchReason")
	private List<String> mismatchReason;
	
	@Expose
	@SerializedName("vendorGstins")
	private List<String> vendorGstins;
	
	@Expose
	@SerializedName("vendorPans")
	private List<String> vendorPans;
	
	@Expose
	@SerializedName("type")
	private String type;
	
	@Expose
	@SerializedName("pos")
	private List<String> pos;
	
	@Expose
	private String timeStamp;
	
	@Expose
	private String identifier;
	
	@Expose
	@SerializedName("docNumberList")
	private List<String> docNumberList = new ArrayList();
	
	@Expose
	@SerializedName("accVoucherNums")
	private List<String> accVoucherNums = new ArrayList();
	
	@Expose
	@SerializedName("vndrPans")
	private List<String> vndrPans = new ArrayList();
	
	@Expose
	@SerializedName("vndrGstins")
	private List<String> vndrGstins = new ArrayList();
	
	@Expose
	@SerializedName("frmTaxPrd3b")
	private String frmTaxPrd3b;
	
	@Expose
	@SerializedName("toTaxPrd3b")
	private String toTaxPrd3b;
	
	@Expose
	@SerializedName("reconCriteria")
	private String reconCriteria;
	
	@Expose
	@SerializedName("taxPeriodBase")
	private String taxPeriodBase;
	
	
	}
