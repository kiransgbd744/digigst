package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Data
public class ImsSaveQueueStatusResponseDto {

	@Expose
	private String message;

	@Expose
	private String section;

	@Expose
	private String status;
	
	@Expose
	private String requestType;

	@Expose
	private String dateTime;

}
