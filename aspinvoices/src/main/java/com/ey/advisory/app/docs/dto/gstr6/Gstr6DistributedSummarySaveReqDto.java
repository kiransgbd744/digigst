package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class Gstr6DistributedSummarySaveReqDto {

	private Long id;

	private String returnPeriod;

	private String isdGstin;

	private String recipientGSTIN;

	private String stateCode;

	private String originalRecipeintGstin;

	private String originalStatecode;

	private String documentType;

	private String supplyType;

	private String docNum;

	private LocalDate docDate;

	private String origDocNumber;

	private LocalDate origDocDate;

	private String origCrNoteNumber;

	private LocalDate origCrNoteDate;

	private String eligibleIndicator;

	private BigDecimal igstAsIgst;

	private BigDecimal igstAsSgst;

	private BigDecimal igstAsCgst;

	private BigDecimal sgstAsSgst;

	private BigDecimal sgstAsIgst;

	private BigDecimal cgstAsCgst;

	private BigDecimal cgstAsIgst;

	private BigDecimal cessAmount;

}
