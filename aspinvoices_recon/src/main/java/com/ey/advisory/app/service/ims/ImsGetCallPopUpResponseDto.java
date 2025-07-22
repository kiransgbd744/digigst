package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class ImsGetCallPopUpResponseDto {

	@Expose
	private String type;

	@Expose
	private String status;

	@Expose
	private String timeStamp;

}
