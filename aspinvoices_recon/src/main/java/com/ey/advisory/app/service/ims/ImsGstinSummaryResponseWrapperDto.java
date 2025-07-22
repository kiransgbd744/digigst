package com.ey.advisory.app.service.ims;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class ImsGstinSummaryResponseWrapperDto {
	
	@Expose
	private String getCallTimeStamp;
	
	@Expose
	private List<ImsGstinSummaryResponseDto> tables;

}
