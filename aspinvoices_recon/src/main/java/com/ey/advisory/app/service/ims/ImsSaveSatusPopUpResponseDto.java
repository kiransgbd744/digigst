package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class ImsSaveSatusPopUpResponseDto {

	@Expose
	private String dateTime;

	@Expose
	private String requestType;

	@Expose
	private String refId;

	@Expose
	private String section;

	@Expose
	private String status;

	@Expose
	private Integer errorCount;
	
	@Expose
	private Integer count;

}
