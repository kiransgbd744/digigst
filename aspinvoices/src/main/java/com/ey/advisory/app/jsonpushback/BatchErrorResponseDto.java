/**
 * 
 */
package com.ey.advisory.app.jsonpushback;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
public class BatchErrorResponseDto {
	
	@Expose
	private String suppGstin;
	
	@Expose
	private String custGstin;
	
	@Expose
	private String retPeriod;
	
	@Expose
	private String docNo;
	
	@Expose	
	private String docType;
	
	@Expose	
	private String docDate;
	
	@Expose	
	private String docKey;
	
	@Expose
	private String errorCode;
	
	@Expose
	private String errorDesc;
}
