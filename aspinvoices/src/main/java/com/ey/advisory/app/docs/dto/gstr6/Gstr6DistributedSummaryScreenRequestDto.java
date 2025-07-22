package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */

@Data
public class Gstr6DistributedSummaryScreenRequestDto {

	@Expose
	@SerializedName("entity")
	private List<Long> entityId;

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("isdGstin")
	private String isdGstin;

	@Expose
	@SerializedName("recipientGSTIN")
	private String recipientGSTIN;

	@Expose
	@SerializedName("stateCode")
	private String stateCode;

	@Expose
	@SerializedName("originalRecipeintGstin")
	private String originalRecipeintGstin;

	@Expose
	@SerializedName("originalStatecode")
	private String originalStatecode;

	@Expose
	@SerializedName("documentType")
	private String documentType;

	@Expose
	@SerializedName("supplyType")
	private String supplyType;

	@Expose
	@SerializedName("docNum")
	private String docNum;

	@Expose
	@SerializedName("docDate")
	private LocalDate docDate;

	@Expose
	@SerializedName("origDocNumber")
	private String origDocNumber;

	@Expose
	@SerializedName("origDocDate")
	private LocalDate origDocDate;

	@Expose
	@SerializedName("origCrNoteNumber")
	private String origCrNoteNumber;

	@Expose
	@SerializedName("origCrNoteDate")
	private LocalDate origCrNoteDate;

	@Expose
	@SerializedName("eligibleIndicator")
	private String eligibleIndicator;

	@Expose
	@SerializedName("docKey")
	private String docKey;

	@Expose
	@SerializedName("fileId")
	private Long fileId;
	@Expose
	@SerializedName("igstAsIgst")
	private BigDecimal igstAsIgst;

	@Expose
	@SerializedName("igstAsSgst")
	private BigDecimal igstAsSgst;

	@Expose
	@SerializedName("igstAsCgst")
	private BigDecimal igstAsCgst;

	@Expose
	@SerializedName("sgstAsSgst")
	private BigDecimal sgstAsSgst;

	@Expose
	@SerializedName("sgstAsIgst")
	private BigDecimal sgstAsIgst;

	@Expose
	@SerializedName("cgstAsCgst")
	private BigDecimal cgstAsCgst;

	@Expose
	@SerializedName("cgstAsIgst")
	private BigDecimal cgstAsIgst;

	@Expose
	@SerializedName("cessAmount")
	private BigDecimal cessAmount;

	@Expose
	@SerializedName("isProcessed")
	private boolean isProcessed;

}
