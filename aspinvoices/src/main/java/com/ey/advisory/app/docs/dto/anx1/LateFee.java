package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Data
public class LateFee {
	@Expose
	@SerializedName("debitId")
	private String debitId;
	@Expose
	@SerializedName("cLamt")
	private BigDecimal cLamt = BigDecimal.ZERO;
	@Expose
	@SerializedName("sLamt")
	private	BigDecimal sLamt = BigDecimal.ZERO;
}
