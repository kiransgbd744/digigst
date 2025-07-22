package com.ey.advisory.app.docs.dto.gstr3B;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Gstr3BSecDetailsDTO {
	
	@Expose
	@SerializedName("txval")
	private BigDecimal txval;
	
	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt;
	
	@Expose
	@SerializedName("camt")
	private BigDecimal camt;
	
	@Expose
	@SerializedName("samt")
	private BigDecimal samt;
	
	@Expose
	@SerializedName("csamt")
	private BigDecimal csamt;
	
	@Expose
	@SerializedName("pos")
	private String pos;
	
	@Expose
	@SerializedName("ty")
	private String ty;
	
	@Expose
	@SerializedName("inter")
	private BigDecimal inter;
	
	@Expose
	@SerializedName("intra")
	private BigDecimal intra;
	

}
