package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EInvoiceDocumentDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("docNo")
	private String docNo;

	@Expose
	@SerializedName("docDate")
	private LocalDate docDate;

	@Expose
	@SerializedName("counterPartyGstin")
	private String counterPartyGstin;

	@Expose
	@SerializedName("EWBPartAStatus")
	private Integer eWBPartAStatus;

	@Expose
	@SerializedName("EWBPartADateTime")
	private LocalDateTime eWBPartADateTime;

	@Expose
	@SerializedName("EWBPartANo")
	private String eWBPartANo;

	@Expose
	@SerializedName("EWBPartADate")
	private LocalDateTime eWBPartADate;

	@Expose
	@SerializedName("EWBPartATransId")
	private String eWBPartATransId;

	@Expose
	@SerializedName("GSTStatus")
	private String gstStatus;

	@Expose
	@SerializedName("GSTRetType")
	private String gstRetType;

	@Expose
	@SerializedName("GSTTaxableValue")
	private BigDecimal gstTaxableValue;

	@Expose
	@SerializedName("GSTTableNo")
	private String gstTableNo;
	@Expose
	@SerializedName("EWBValidUpto")
	private LocalDateTime ewbValidUpto;

	@Expose
	@SerializedName("EWBSubSupplyType")
	private String ewbSubSupplyType;

	@Expose
	@SerializedName("EWBErrorPoint")
	private Integer ewbErrorPoint;

	@Expose
	@SerializedName("EWBErrorCode")
	private String ewbErrorCode;

	@Expose
	@SerializedName("EWBErrorDesc")
	private String ewbErrorDesc;

	@Expose
	@SerializedName("GSTErrorCode")
	private String gstErrorCode;

	@Expose
	@SerializedName("GSTErrorDesc")
	private String gstErrorDesc;

	@Expose
	@SerializedName("aspErrorCode")
	private String aspErrorCode;

	@Expose
	@SerializedName("aspErrorDesc")
	private String aspErrorDesc;

	@Expose
	@SerializedName("irnNum")
	private String irnNum;

	@Expose
	@SerializedName("einvStatus")
	private Integer einvStatus;

	@Expose
	@SerializedName("irnDate")
	private LocalDateTime irnDate;

	@Expose
	@SerializedName("infoErrCode")
	private String infoErrCode;

	@Expose
	@SerializedName("infoErrDesc")
	private String infoErrDesc;

	@Expose
	@SerializedName("irnStatus")
	private Integer irnStatus;

	@Expose
	@SerializedName("irnResponse")
	private String irnResponse;

	@Expose
	@SerializedName("ewbNoresp")
	private String ewbNoresp;

	@Expose
	@SerializedName("ewbDateResp")
	private LocalDateTime ewbDateResp;

	@Expose
	@SerializedName("ackDate")
	private LocalDateTime ackDate;

	@Expose
	@SerializedName("ackNum")
	private String ackNum;

	@Expose
	@SerializedName("einvErrorCode")
	private String einvErrorCode;

	@Expose
	@SerializedName("einvErrorDesc")
	private String einvErrorDesc;

	@Expose
	@SerializedName("taxDocType")
	private String taxDocType;

	@Expose
	@SerializedName("supplyType")
	private String supplyType;

	@Expose
	@SerializedName("docKey")
	private String docKey;

	@Expose
	@SerializedName("authStatus")
	private String authStatus;

	@Expose
	@SerializedName("refId")
	private String refId;
	
	@Expose
	@SerializedName("retPeriod")
	private String retPeriod;
	
	@Expose
	@SerializedName("multiSupplyApplicable")
	private boolean multiSupplyApplicable;
}
