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
public class PaymentreferenceDocSaveRespDto {

	@Expose
	@SerializedName("payReferenceDate")
	private String payReferenceDate;

	@Expose
	@SerializedName("documentType")
	private String documentType;

	@Expose
	@SerializedName("documentNumber")
	private String documentNumber;

	@Expose
	@SerializedName("customerGSTIN")
	private String customerGSTIN;
	
	@Expose
	@SerializedName("supplierGSTIN")
	private String supplierGSTIN;

	@Expose
	@SerializedName("payReferenceNo")
	private String payReferenceNo;

	@Expose
	@SerializedName("documentDate")
	private String documentDate;

	@Expose
	@SerializedName("fiscalYear")
	private String fiscalYear;

	@Expose
	@SerializedName("errorDetails")
	private List<PaymentReferenceErrorDto> errors;

}

