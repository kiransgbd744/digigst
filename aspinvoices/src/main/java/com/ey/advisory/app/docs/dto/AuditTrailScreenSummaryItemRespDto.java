/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class AuditTrailScreenSummaryItemRespDto {

	@Expose
	@SerializedName("processingFrequency")
	private String processingFrequency;

	@Expose
	@SerializedName("processingDateTime")
	private String processingDateTime;

	@Expose
	@SerializedName("userID")
	private String userID;

	@Expose
	@SerializedName("processingSource")
	private String processingSource;

	@Expose
	@SerializedName("processingStatus")
	private String processingStatus;

	@Expose
	@SerializedName("whetherCancel")
	private String whetherCancel;

	@Expose
	@SerializedName("customerGSTIN")
	private String customerGSTIN;

	@Expose
	@SerializedName("noLineItems")
	private Long noLineItems;

	@Expose
	@SerializedName("totalTaxableValue")
	private BigDecimal totalTaxableValue;

	@Expose
	@SerializedName("totalTax")
	private BigDecimal totalTax;

	@Expose
	@SerializedName("invoiceValue")
	private BigDecimal invoiceValue;
}
