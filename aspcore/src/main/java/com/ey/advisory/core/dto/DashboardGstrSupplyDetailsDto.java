package com.ey.advisory.core.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DashboardGstrSupplyDetailsDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("type")
	private String type;
	
	@Expose
	@SerializedName("stateName")
	private String stateName;
	
	@Expose
	@SerializedName("authStatus")
	private String authStatus;
	
	@Expose
	@SerializedName("outSup")
	private BigDecimal outSup;
	
	@Expose
	@SerializedName("outTax")
	private BigDecimal outTax;
	
	@Expose
	@SerializedName("inwSup")
	private BigDecimal inwSup;
	
	@Expose
	@SerializedName("taxAmt")
	private BigDecimal taxAmt;
	
	@Expose
	@SerializedName("avbCred")
	private BigDecimal avbCred;
	
	@Expose
	@SerializedName("cashLed")
	private BigDecimal cashLed;
	
	@Expose
	@SerializedName("crdLed")
	private BigDecimal crdLed;
	
	@Expose
	@SerializedName("libLed")
	private BigDecimal libLed;
	
	@Expose
	@SerializedName("filStat")
	private String filStat;

}
