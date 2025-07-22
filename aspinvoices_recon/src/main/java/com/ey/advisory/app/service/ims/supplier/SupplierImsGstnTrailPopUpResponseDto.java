package com.ey.advisory.app.service.ims.supplier;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class SupplierImsGstnTrailPopUpResponseDto {

	@Expose
	private String actionGST;

	@Expose
	private String actionGSTTimeStamp;

}
