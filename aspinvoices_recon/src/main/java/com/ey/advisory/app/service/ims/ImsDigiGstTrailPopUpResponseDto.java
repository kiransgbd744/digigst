package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class ImsDigiGstTrailPopUpResponseDto {

	@Expose
	private String actionDigiGST;

	@Expose
	private String actionDigiGSTTimeStamp;

	@Expose
	private String actionDigiGSTActionTakenBy;
	
	@Expose
	private String isSavedToGstn;
	
}
