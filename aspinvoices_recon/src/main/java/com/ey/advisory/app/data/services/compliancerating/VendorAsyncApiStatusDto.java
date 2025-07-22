/**
 * 
 */
package com.ey.advisory.app.data.services.compliancerating;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class VendorAsyncApiStatusDto {

	@Expose
	private String requestId;

	@Expose
	private String uploadedBy;
	
	@Expose
	private String noOfGstin;
	
	@Expose
	private String fy;
	
	@Expose
	private String createdOn;
	
	@Expose
	private String completionOn;
	
	@Expose
	private String status;
	
	@Expose
	private String uploadMode;
}
