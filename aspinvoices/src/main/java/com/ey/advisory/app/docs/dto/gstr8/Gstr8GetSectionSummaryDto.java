
package com.ey.advisory.app.docs.dto.gstr8;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr8GetSectionSummaryDto {

	@SerializedName("gSuppMade")
	@Expose
	private BigDecimal gSuppMade;

	@SerializedName("gSuppRtn")
	@Expose
	private BigDecimal gSuppRtn;

	@SerializedName("ttl_camt")
	@Expose
	private BigDecimal ttlCamt;
	
	@SerializedName("ttl_iamt")
	@Expose
	private BigDecimal ttlIamt;

	@SerializedName("ttl_samt")
	@Expose
	private BigDecimal ttlSamt;

	@SerializedName("ttl_amtcol")
	@Expose
	private BigDecimal ttlAmtDed;

	@SerializedName("no_rec")
	@Expose
	private int ttlRec;

	@SerializedName("chksum")
	@Expose
	private String chksum;

}