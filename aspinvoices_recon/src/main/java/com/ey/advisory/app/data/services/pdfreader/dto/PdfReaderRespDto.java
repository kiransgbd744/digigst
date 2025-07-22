package com.ey.advisory.app.data.services.pdfreader.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class PdfReaderRespDto {

	@SerializedName("File Name")
	@Expose
	private String fileName;

	@SerializedName(value = "CustomerName", alternate = {"Customer Name"})
	@Expose
	private String customerName;

	@SerializedName(value = "CustomerTaxId", alternate = { "Customer GSTIN" })
	@Expose
	private String customerTaxId;

	@SerializedName("Customer Address")
	@Expose
	private String customerAddress;

	@SerializedName("Customer State")
	@Expose
	private String customerAddressRecipient;
	
	@SerializedName("Customer State Code")
	@Expose
	private String customerAddressRecipientCode;

	@SerializedName("Customer Pincode")
	@Expose
	private String customerPincode;

	@SerializedName("Billing Address")
	@Expose
	private String billingAddress;

	@SerializedName("Shipping Address")
	@Expose
	private String shippingAddress;

	@SerializedName(value = "InvoiceId", alternate = { "Document Number" })
	@Expose
	private String invoiceId;

	@SerializedName(value = "InvoiceDate", alternate = { "Document Date" })
	@Expose
	private String invoiceDate;

	@SerializedName("Supplier Name")
	@Expose
	private String vendorName;

	@SerializedName(value = "VendorTaxId", alternate = { "Supplier GSTIN" })
	@Expose
	private String vendorTaxId;

	@SerializedName("Supplier Address")
	@Expose
	private String vendorAddress;

	@SerializedName("VendorAddressRecipient")
	@Expose
	private String vendorAddressRecipient;

	@SerializedName("Supplier State")
	@Expose
	private String vendorState;

	@SerializedName("Supplier State Code")
	@Expose
	private String vendorStateCode;

	@SerializedName("Supplier Pincode")
	@Expose
	private String vendorPincode;

	@SerializedName("Place of Supply")
	@Expose
	private String placeOfSupply;

	@SerializedName("Sub Total")
	@Expose
	private BigDecimal subTotal;

	@SerializedName("Total Tax")
	@Expose
	private BigDecimal totalTax;

	@SerializedName(value = "InvoiceTotal", alternate = { "Invoice Total" })
	@Expose
	private BigDecimal invoiceTotal;

	@SerializedName("CGST Amount")
	@Expose
	private String cgstAmount;

	@SerializedName("CGST Rate")
	@Expose
	private String cgstRate;

	@SerializedName("SGST Amount")
	@Expose
	private String sgstAmount;

	@SerializedName("SGST Rate")
	@Expose
	private String sgstRate;

	@SerializedName("IGST Rate")
	@Expose
	private String igstRate;

	@SerializedName("IGST Amount")
	@Expose
	private String igstAmount;

	@SerializedName("UTGST Rate")
	@Expose
	private String utgstRate;

	@SerializedName("UTGST Amount")
	@Expose
	private String utgstAmount;

	@SerializedName("CESS Amount")
	@Expose
	private String cessAmount;

	@SerializedName("CESS Rate")
	@Expose
	private String cessRate;

	@SerializedName("Document Type")
	@Expose
	private String documentType;

	@SerializedName("IRN Number")
	@Expose
	private String irnNumber;

	@SerializedName("IRN Date")
	@Expose
	private String irnDate;
	
	@SerializedName("Quantity")
	@Expose
	private BigDecimal quantity;
	
	@SerializedName("Reverse Charge / RCM Flag")
	@Expose
	private String rCm;
	
	@SerializedName("Purchase Order Number")
	@Expose
	private String purchaseOrder;
	
	@SerializedName(value = "Line-items")
	@Expose
	private List<PdfReaderLineItemRespDto> lineItems = new ArrayList<>();
	
	@SerializedName(value = "message")
	@Expose
	private String message;	
}
