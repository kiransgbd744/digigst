package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Ravindra V S
 *
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EwbTotalRecordsDto {

	@Expose
	@SerializedName("errorCode")
	private String errorCode;

	@Expose
	@SerializedName("errorDescription")
	private String errorDescription;

	@Expose
	@SerializedName("eWBNo")
	private String eWBNo;

	@Expose
	@SerializedName("eWBDate")
	private String eWBDate;

	@Expose
	@SerializedName("supplyType")
	private String supplyType;

	@Expose
	@SerializedName("docNo")
	private String docNo;

	@Expose
	@SerializedName("docDate")
	private String docDate;

	@Expose
	@SerializedName("otherPartyGSTIN")
	private String otherPartyGSTIN;

	@Expose
	@SerializedName("transporterDetails")
	private String transporterDetails;

	@Expose
	@SerializedName("fromGSTINInfo")
	private String fromGSTINInfo;

	@Expose
	@SerializedName("toGSTINInfo")
	private String toGSTINInfo;

	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("noofItems")
	private String noofItems;

	@Expose
	@SerializedName("mainHSNCode")
	private String mainHSNCode;

	@Expose
	@SerializedName("mainHSNDesc")
	private String mainHSNDesc;

	@Expose
	@SerializedName("assessableValue")
	private String assessableValue;

	@Expose
	@SerializedName("sGSTValue")
	private String sGSTValue;

	@Expose
	@SerializedName("cGSTValue")
	private String cGSTValue;

	@Expose
	@SerializedName("iGSTValue")
	private String iGSTValue;

	@Expose
	@SerializedName("cESSValue")
	private String cESSValue;

	@Expose
	@SerializedName("cESSNonAdvolValue")
	private String cESSNonAdvolValue;

	@Expose
	@SerializedName("otherValue")
	private String otherValue;

	@Expose
	@SerializedName("totalInvoiceValue")
	private String totalInvoiceValue;

	@Expose
	@SerializedName("validTillDate")
	private String validTillDate;

	@Expose
	@SerializedName("modeofGeneration")
	private String modeofGeneration;

	@Expose
	@SerializedName("cancelledBy")
	private String cancelledBy;

	@Expose
	@SerializedName("cancelledDate")
	private String cancelledDate;

}
