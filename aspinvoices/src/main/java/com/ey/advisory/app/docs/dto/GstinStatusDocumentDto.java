package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GstinStatusDocumentDto {

	@Expose
	@SerializedName("supplierGstin")
	private String supplierGstin;

	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("tableType")
	private String tableType;

	@Expose
	@SerializedName("supplyType")
	private String supplyType;
	
	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("docNo")
	private String docNo;

	@Expose
	@SerializedName("docDate")
	private String docDate;

	@Expose
	@SerializedName("recipientGstin")
	private String recipientGstin;

	@Expose
	@SerializedName("recipientName")
	private String recipientName;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("taxableValue")
	private String taxableValue;

	@Expose
	@SerializedName("igstAmount")
	private String igstAmount;

	@Expose
	@SerializedName("cgstAmount")
	private String cgstAmount;

	@Expose
	@SerializedName("sgstAmount")
	private String sgstAmount;

	@Expose
	@SerializedName("cessAmount")
	private String cessAmount;

	@Expose
	@SerializedName("invoiceValue")
	private String invoiceValue;

}
