/**
 * 
 */
package com.ey.advisory.app.services.gstinvalidator.apipush;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Sakshi.jain
 *
 */

@Data
public class GstinValidatorReqDto {

	@Expose
	@SerializedName("customerCode")
	private String customerCode;

	@Expose
	@SerializedName("gstin")
	private String gstin;

}

