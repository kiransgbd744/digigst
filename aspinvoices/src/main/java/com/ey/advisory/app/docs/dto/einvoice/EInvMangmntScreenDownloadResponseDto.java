/**
 * 
 */
package com.ey.advisory.app.docs.dto.einvoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class EInvMangmntScreenDownloadResponseDto {

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
	private String eWBPartAStatus;

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
	private String ewbErrorPoint;

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
}
