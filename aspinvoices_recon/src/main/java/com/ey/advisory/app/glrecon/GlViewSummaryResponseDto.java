package com.ey.advisory.app.glrecon;


import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GlViewSummaryResponseDto {

	@Expose
	private int invoiceCountSR = 20;
	
	@Expose
	private String taxValueSR = "20.00";
		
	@Expose
	private String igstSR = "40.00";

	@Expose
	private String cgstSR = "30.00";

	@Expose
	private String sgstSR = "40.00";
	
	@Expose
	private String cessSR = "60.00";
	
	@Expose
	private String totalTaxSR = "29.00";
	
	@Expose
	private int invoiceCountGL = 20;
	
	@Expose
	private String taxValueGL = "20.00";
		
	@Expose
	private String igstGL = "40.00";

	@Expose
	private String cgstGL = "30.00";

	@Expose
	private String sgstGL = "40.00";
	
	@Expose
	private String cessGL = "60.00";
	
	@Expose
	private String totalTaxGL = "29.00";
	
}
