
package com.ey.advisory.app.docs.dto.anx1;


import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr7TaxPayIgstSummaryDto {

	@Expose
	@SerializedName("tx")
	private BigDecimal tx;
	
	@Expose
	@SerializedName("intr")
	private BigDecimal intr;
	
	@Expose
	@SerializedName("pen")
	private BigDecimal pen;
	
	@Expose
	@SerializedName("fee")
	private BigDecimal fee;
	
	@Expose
	@SerializedName("oth")
	private BigDecimal oth;
	
	@Expose
	@SerializedName("tot")
	private BigDecimal tot;
	

}