package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EInvoiceDataStatusDto {

	@Expose
	@SerializedName(value = "date")
	private String receivedDate;
	@Expose
	@SerializedName(value = "aspTotal")
	private BigInteger totalRecords = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "processeActive")
	private BigInteger processeActive = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "processeInactive")
	private BigInteger processeInactive = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "errorActive")
	private BigInteger errorActive = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "errorInactive")
	private BigInteger errorInactive = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EinvNA")
	private BigInteger eInvNA = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EinvErrorDigiGST")
	private BigInteger eInvErrorDigiGST = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EinvProcessed")
	private BigInteger eInvProcessed = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EinvINRInitiated")
	private BigInteger einvINRInitiated;

	@Expose
	@SerializedName(value = "EinvGenerated")
	private BigInteger eInvGenerated = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EinvError")
	private BigInteger eInvError = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EinvCancelled")
	private BigInteger einvCancelled = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EinvInfoError")
	private BigInteger einvInfoError = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EinvNotOpted")
	private BigInteger einvNotOpted = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EinvId")
	private BigInteger einvId = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBNA")
	private BigInteger eWbna = BigInteger.ZERO;
	@Expose
	@SerializedName(value = "EWBErrorDigiGST")
	private BigInteger ewbErrorDigiGST = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBProcessd")
	private BigInteger ewbProcessd = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBInitiated")
	private BigInteger ewbInitiated = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBGenerated")
	private BigInteger ewbGenerated = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBError")
	private BigInteger ewbError = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBCancelled")
	private BigInteger ewbCancelled = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBGeneratedOnErp")
	private BigInteger ewbGeneratedOnErp = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBNotGeneratedOnErp")
	private BigInteger ewbNotGeneratedOnErp = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBNotOpted")
	private BigInteger ewbNotOpted = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "EWBId")
	private BigInteger ewbId = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "AspNA")
	BigInteger aspNA = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "AspError")
	BigInteger aspError = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "AspProcess")
	BigInteger aspProcess = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "AspSaveInitiated")
	BigInteger aspSaveInitiated = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "AspSavedGstin")
	BigInteger aspSavedGstin = BigInteger.ZERO;

	@Expose
	@SerializedName(value = "AspErrorsGstin")
	BigInteger aspErrorsGstin = BigInteger.ZERO;

}
