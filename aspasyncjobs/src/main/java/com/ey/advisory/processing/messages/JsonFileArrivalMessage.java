package com.ey.advisory.processing.messages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class contains all the information required to convert to csv.
 * 
 * @author Sai.Pakanati
 *
 */
@Getter
@Setter
@ToString
public class JsonFileArrivalMessage {

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("startTaxPeriod")
	private String startPeriod;

	@Expose
	@SerializedName("endTaxPeriod")
	private String endPeriod;

	@Expose
	@SerializedName("invoiceType")
	private String invoiceType;
	
	@Expose
	@SerializedName("isFYBased")
	private Boolean isFYBased;

}
