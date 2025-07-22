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
public class VendorApiRequestDto {

	@Expose
	private String refId;
}
