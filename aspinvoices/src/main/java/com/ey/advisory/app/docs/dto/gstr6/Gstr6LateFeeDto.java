package com.ey.advisory.app.docs.dto.gstr6;

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
public class Gstr6LateFeeDto {

	@Expose
	@SerializedName("cLamt")
	private BigDecimal cLamt;

	@Expose
	@SerializedName("sLamt")
	private BigDecimal sLamt;

	@Expose
	@SerializedName("debitId")
	private String debitId;

	@Expose
	@SerializedName("date")
	private String date;

	@Expose
	@SerializedName("foroffset")
	private Gstr6LateOffSetDetails foroffset;

	
}
