package com.ey.advisory.app.docs.dto.ret;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */

@Data
public class LateFee {
	
	@Expose
	@SerializedName("cgst")
	private BigDecimal cgstAmount;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgstAmount;

}
