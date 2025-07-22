package com.ey.advisory.app.data.services.pdfreader.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class PdfReaderLineItemRespDto {

	@SerializedName("Total Amount")
	@Expose
	private BigDecimal amount;

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
	private BigDecimal unitPrice;
	
	@SerializedName("Taxable Amount")
	@Expose
	private BigDecimal taxableAmount;

	@SerializedName("Unit")
	@Expose
	private String unit;

	@SerializedName("Tax Rate")
	@Expose
	//private BigDecimal taxRate;
	private String taxRate;
	
	@SerializedName("Tax Amount")
	@Expose
	//private BigDecimal tax;
	private String tax;
	
	@SerializedName("Line Item Number")
	@Expose
	private Integer lineItemNo;
}
