package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr2xAcceptAndrejectResDto {

	@Expose
	@SerializedName("tot_amt")
	private BigDecimal totAmt;

	@Expose
	@SerializedName("tot_iamt")
	private BigDecimal toIamt;

	@Expose
	@SerializedName("tot_camt")
	private BigDecimal totCamt;

	@Expose
	@SerializedName("tot_samt")
	private BigDecimal totSamt;

	@Expose
	@SerializedName("tot_count")
	private Integer totCount;

}
