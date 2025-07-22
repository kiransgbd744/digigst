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
public class DashboardGstrLedgerStatusDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
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
