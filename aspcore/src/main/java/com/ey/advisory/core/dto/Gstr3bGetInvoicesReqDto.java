/**
 * 
 */
package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class Gstr3bGetInvoicesReqDto {

	@Expose
	private String gstin;

	@Expose
	private String taxPeriod;
	
	@Expose
	private String fromPeriod;

	@Expose
	private String toPeriod;

	@Expose
	@SerializedName("isDigigst")
	private Boolean isDigigst;
	
	@Expose
	private String isVerified;
}
