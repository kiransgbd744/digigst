package com.ey.advisory.app.gstr2b;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr2BReqStatusDetailsDto {
	
	@Expose
	private String gstin;
	
	@Expose
	private String taxPeriod;

}
