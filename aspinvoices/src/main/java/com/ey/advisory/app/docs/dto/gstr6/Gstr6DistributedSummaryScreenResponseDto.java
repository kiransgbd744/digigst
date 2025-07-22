package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */

@Data
public class Gstr6DistributedSummaryScreenResponseDto {

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

	private BigDecimal igstAsIgst = BigDecimal.ZERO;

	private BigDecimal igstAsSgst = BigDecimal.ZERO;

	private BigDecimal igstAsCgst = BigDecimal.ZERO;

	private BigDecimal sgstAsSgst = BigDecimal.ZERO;

	private BigDecimal sgstAsIgst = BigDecimal.ZERO;

	private BigDecimal cgstAsCgst = BigDecimal.ZERO;

	private BigDecimal cgstAsIgst = BigDecimal.ZERO;

	private BigDecimal cessAmount = BigDecimal.ZERO;

	private String docKey;

	private Long fileId;

	private Long asEnterTableId;

	private boolean isProcessed;

	/*
	 * private int derived_Ret_period; private String createdBy; private int
	 * createdOn;
	 */

	private Boolean isDelete;
}
