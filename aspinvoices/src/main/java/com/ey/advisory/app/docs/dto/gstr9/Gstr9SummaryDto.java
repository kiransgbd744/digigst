package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author KR632DR
 *
 */
@Data
public class Gstr9SummaryDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("stateName")
	private String stateName;

	@Expose
	@SerializedName("auth")
	private String auth;
	
	@Expose
	@SerializedName("regType")
	private String regType;
	
	@Expose
	@SerializedName("digiGstPrData")
	private String digiGstPrData;
	
	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;
	
	@Expose
	@SerializedName("statusGstr1And3BGetCalls")
	private String statusGstr1And3BGetCalls;
	
	@Expose
	@SerializedName("computeStatusAsPerGstn")
	private String computeStatusAsPerGstn;
	
	@Expose
	@SerializedName("computeStatusAsPerGstnUpdateDate")
	private String computeStatusAsPerGstnCreatedDate;
	
	@Expose
	@SerializedName("gstr9SaveStatus")
	private String gstr9SaveStatus;
	
	@Expose
	@SerializedName("gstr9SaveStatusUpdateDate")
	private String gstr9SaveStatusCreatedDate;
	
	@Expose
	@SerializedName("digiCompStatus")
	private String digiCompStatus;
	
	@Expose
	@SerializedName("digiCompStatusCreatedDate")
	private String digiCompStatusCreatedDate;
	
	
	@Expose
	@SerializedName("digiProcessCompStatus")
	private String digiProcessCompStatus;
	
	@Expose
	@SerializedName("digiProcessCompStatusCreatedDate")
	private String digiProcessCompStatusCreatedDate;
	
	@Expose
	@SerializedName("totalTurnOver")
	private BigDecimal totalTurnOver = BigDecimal.ZERO;;
	
	@Expose
	@SerializedName("netItcAvl")
	private BigDecimal netItcAvl= BigDecimal.ZERO;;
	
	@Expose
	@SerializedName("lapsedItc")
	private BigDecimal lapsedItc= BigDecimal.ZERO;;
	
	@Expose
	@SerializedName("taxPayable")
	private BigDecimal taxPayable= BigDecimal.ZERO;;

}
