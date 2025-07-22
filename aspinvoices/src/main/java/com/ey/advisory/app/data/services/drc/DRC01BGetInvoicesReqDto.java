/**
 * 
 */
package com.ey.advisory.app.data.services.drc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class DRC01BGetInvoicesReqDto {

	@Expose
	private String gstin;

	@Expose
	private String taxPeriod;
	
	@Expose
	private String refId;
	
	@Expose
	@SerializedName("isDigigst")
	private Boolean isDigigst;
	
	@Expose
	private String isVerified;
}
