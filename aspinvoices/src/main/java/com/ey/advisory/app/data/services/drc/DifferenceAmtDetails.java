package com.ey.advisory.app.data.services.drc;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DifferenceAmtDetails {

	@SerializedName("rtnprd")
	@Expose
	private String rtnprd;

	@SerializedName("rtntyp")
	@Expose
	private String rtntyp;

	@SerializedName("igst")
	@Expose
	private BigDecimal igst=BigDecimal.ZERO;

	@SerializedName("cgst")
	@Expose
	private BigDecimal cgst=BigDecimal.ZERO;

	@SerializedName("sgst")
	@Expose
	private BigDecimal sgst=BigDecimal.ZERO;

	@SerializedName("cess")
	@Expose
	private BigDecimal cess=BigDecimal.ZERO;

	@SerializedName("ttl")
	@Expose
	private BigDecimal ttl=BigDecimal.ZERO;
}
