/**
 * 
 */
package com.ey.advisory.app.services.gstinvalidator.apipush;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Sakshi.jain
 *
 */

@Data
public class GstinValidatorReqDtoList {

	@Expose
	@SerializedName("gstinValidation")
	private List<GstinValidatorReqDto> gstinList;


}

