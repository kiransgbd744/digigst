package com.ey.advisory.app.docs.dto.erp;

import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class EInvoiceDataStatusRequestDataSummaryItemDto {

	@XmlElement(name = "CompanyCode")
	private String companyCode;

	@XmlElement(name = "Entity")
	private String entity;

	@XmlElement(name = "RetPer")
	private String retPer;

	@XmlElement(name = "GstinNum")
	private String gstinNum;

	@XmlElement(name = "DataType")
	private String dataType;

	@XmlElement(name = "EntityName")
	private String entityName;

	@XmlElement(name = "Location")
	private String location;

	@XmlElement(name = "ProfitCenter1")
	private String profitCenter1;

	@XmlElement(name = "ProfitCenter2")
	private String profitCenter2;

	@XmlElement(name = "SalesOrg")
	private String salesOrg;

	@XmlElement(name = "PurchaseOrg")
	private String purchaseOrg;

	@XmlElement(name = "Division")
	private String division;

	@XmlElement(name = "DistChanel")
	private String distChanel;

	@XmlElement(name = "PlantCode")
	private String plantCode;

	@XmlElement(name = "PushedDt")
	private String pushedDt;

	@XmlElement(name = "ReceivedDate")
	private String receivedDate;

	@XmlElement(name = "TotalRecords")
	private BigInteger totalRecords = BigInteger.ZERO;

	@XmlElement(name = "ProcessedActive")
	private BigInteger processedActive = BigInteger.ZERO;

	@XmlElement(name = "ProcessedInactive")
	private BigInteger processedInactive = BigInteger.ZERO;

	@XmlElement(name = "ErrorActive")
	private BigInteger errorActive = BigInteger.ZERO;

	@XmlElement(name = "ErrorInactive")
	private BigInteger errorInactive = BigInteger.ZERO;

	@XmlElement(name = "EwbNotApplicable")
	private BigInteger ewbNotApplicable = BigInteger.ZERO;

	@XmlElement(name = "EwbErrorDigigst")
	private BigInteger ewbErrorDigigst = BigInteger.ZERO;

	@XmlElement(name = "EwbAspProcess")
	private BigInteger ewbAspProcess = BigInteger.ZERO;

	@XmlElement(name = "EwbInitated")
	private BigInteger ewbInitated = BigInteger.ZERO;

	@XmlElement(name = "EwbGenerated")
	private BigInteger ewbGenerated = BigInteger.ZERO;

	@XmlElement(name = "EwbErrorNic")
	private BigInteger ewbErrorNic = BigInteger.ZERO;

	@XmlElement(name = "EwbCancelled")
	private BigInteger ewbCancelled = BigInteger.ZERO;

	@XmlElement(name = "EwbGeneratedOnErp")
	private BigInteger ewbGeneratedOnErp = BigInteger.ZERO;

	@XmlElement(name = "EwbNotGeneratedOnErp")
	private BigInteger ewbNotGeneratedOnErp = BigInteger.ZERO;

	@XmlElement(name = "GstRtnNotApplicable")
	private BigInteger gstRtnNotApplicable = BigInteger.ZERO;

	@XmlElement(name = "GstRtnErrorsDigigst")
	private BigInteger gstRtnErrorsDigigst = BigInteger.ZERO;

	@XmlElement(name = "GstRtnAspProcess")
	private BigInteger gstRtnAspProcess = BigInteger.ZERO;

	@XmlElement(name = "GstRtnSaveInitated")
	private BigInteger gstRtnSaveInitated = BigInteger.ZERO;

	@XmlElement(name = "GstRtnSaveToGstin")
	private BigInteger gstRtnSaveToGstin = BigInteger.ZERO;

	@XmlElement(name = "GstRtnErrorsGstin")
	private BigInteger gstRtnErrorsGstin = BigInteger.ZERO;

	@XmlElement(name = "EinvNotApplicable")
	private BigInteger einvNotApplicable = BigInteger.ZERO;

	@XmlElement(name = "EinvErrorsDigigst")
	private BigInteger einvErrorsDigigst = BigInteger.ZERO;

	@XmlElement(name = "EinvAspProcess")
	private BigInteger einvAspProcess = BigInteger.ZERO;

	@XmlElement(name = "EinvInrInitated")
	private BigInteger einvInrInitated = BigInteger.ZERO;

	@XmlElement(name = "EinvInrGenerated")
	private BigInteger einvInrGenerated = BigInteger.ZERO;

	@XmlElement(name = "EinvErrorsIrp")
	private BigInteger einvErrorsIrp = BigInteger.ZERO;

	@XmlElement(name = "EinvCancelled")
	private BigInteger einvCancelled = BigInteger.ZERO;

	@XmlElement(name = "EinvInfoError")
	private BigInteger einvInfoError = BigInteger.ZERO;

	@XmlElement(name = "EinvNotOpted")
	private BigInteger einvNotOpted = BigInteger.ZERO;
	
	@XmlElement(name = "AspNA")
	private BigInteger aspNA = BigInteger.ZERO;

	@XmlElement(name = "AspError")
	private BigInteger aspError = BigInteger.ZERO;

	@XmlElement(name = "AspProcess")
	private BigInteger aspProcess = BigInteger.ZERO;

	@XmlElement(name = "AspSaveInitiated")
	private BigInteger aspSaveInitiated = BigInteger.ZERO;

	@XmlElement(name = "AspSavedGstin")
	private BigInteger aspSavedGstin = BigInteger.ZERO;

	@XmlElement(name = "AspErrorsGstin")
	private BigInteger aspErrorsGstin = BigInteger.ZERO;
}
