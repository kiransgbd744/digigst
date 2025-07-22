package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class ImsGstnTrailPopUpResponseDto {
	
	@Expose
	private String actionGST;

	@Expose
	private String actionGSTTimeStamp;

}
