
package com.ey.advisory.app.docs.dto.gstr8;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr8GetSummaryDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fp")
	private String fp;

	@SerializedName("dflt_amt")
	@Expose
	private BigDecimal dfltAmt;
	
	@Expose
	@SerializedName("tcs")
	Gstr8GetSectionSummaryDto tcs;

	@Expose
	@SerializedName("tcsa")
	Gstr8GetSectionSummaryDto tcsa;

	@Expose
	@SerializedName("urd")
	Gstr8GetSectionSummaryDto urd;

	@Expose
	@SerializedName("urda")
	Gstr8GetSectionSummaryDto urda;

}