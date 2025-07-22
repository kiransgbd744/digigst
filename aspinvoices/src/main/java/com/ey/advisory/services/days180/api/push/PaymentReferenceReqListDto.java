/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class PaymentReferenceReqListDto {
	
	@Expose
	@SerializedName("invoices")
	private List<PaymentReferenceReqDto> invoices;

}

