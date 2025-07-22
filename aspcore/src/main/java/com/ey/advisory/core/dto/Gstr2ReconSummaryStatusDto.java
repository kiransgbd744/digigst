/**
 * 
 */
package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2ReconSummaryStatusDto {

	@Expose
	private String gstin;
		  
	@Expose
	private String state;
	
	@Expose
	private String status;
	
	@Expose
	private String statusdate;
	
	@Expose
	private String gstinIdentifier;
	
		  
}
