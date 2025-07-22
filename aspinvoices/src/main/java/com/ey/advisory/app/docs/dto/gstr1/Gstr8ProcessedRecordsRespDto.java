package com.ey.advisory.app.docs.dto.gstr1;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr8ProcessedRecordsRespDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("totalCount")
	private Integer totalCount;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("totalSupplies")
	private BigDecimal totalSupplies;

	@Expose
	@SerializedName("igst")
	private BigDecimal igst;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst;

	@Expose
	@SerializedName("getGstr8Status")
	private String getGstr8Status;
	
	@Expose
	@SerializedName("getGstr8Time")
	private String getGstr8Time;
	
	@Expose
	@SerializedName("returnStatus")
	private String returnStatus;
	
	@Expose
	@SerializedName("returnStatusTime")
	private String returnStatusTime;
	
	@Expose
	@SerializedName("state")
	private String state;

	@Expose
	@SerializedName("regType")
	private String regType;

}
