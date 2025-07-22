package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class ImsGetCallResponseDto {

	@Expose
	public String state;
	
	@Expose
	public String gstin;
	
	@Expose
	public String regType;

	@Expose
	public String authToken;
	
	@Expose
	public String invCountStatus;
	
	@Expose
	public String invCountTimeStamp;
	
	@Expose
	public String invDetailsStatus;
	
	@Expose
	public String invDetailsTimeStamp;
	
}
