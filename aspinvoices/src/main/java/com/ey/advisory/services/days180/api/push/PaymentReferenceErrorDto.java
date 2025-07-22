/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 *
 * @author vishal.verma
 */

@Data
public class PaymentReferenceErrorDto {

	@Expose
	@SerializedName("errorCode")
	private String errorCode;

	@Expose
	@SerializedName("errorDesc")
	private String errorDesc;

}
