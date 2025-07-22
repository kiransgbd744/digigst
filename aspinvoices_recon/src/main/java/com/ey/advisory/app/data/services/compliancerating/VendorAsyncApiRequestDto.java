/**
 * 
 */
package com.ey.advisory.app.data.services.compliancerating;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class VendorAsyncApiRequestDto {

	@Expose
	private List<VendorAsyncApiGstinDto> gstins;

	@Expose
	private String fy;
	
	@Expose
	private String pan;
}
