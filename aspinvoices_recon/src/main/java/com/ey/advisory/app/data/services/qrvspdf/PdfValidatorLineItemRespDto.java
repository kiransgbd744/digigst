package com.ey.advisory.app.data.services.qrvspdf;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class PdfValidatorLineItemRespDto {

	@SerializedName("Total Amount")
	@Expose
	private String amount;

	@SerializedName("Description")
	@Expose
	private String description;

	@SerializedName("HSN number")
	@Expose
	private String hsnNumber;

	@SerializedName("Quantity")
	@Expose
	private BigDecimal quantity;

	@SerializedName("Unit Price")
	@Expose
	private String unitPrice;
	
	@SerializedName("Taxable Amount")
	@Expose
	private String taxableAmount;

	@SerializedName("Unit")
	@Expose
	private String unit;

	@SerializedName("Tax Rate")
	@Expose
	private String taxRate;
	
	@SerializedName("Tax Amount")
	@Expose
	private String tax;
}
