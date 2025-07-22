package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommonCreditReversalProcessedDto {
	private String errorCodes;
	private String errorDesc;
	private Boolean isError;
	private Boolean isProcessed;
	private String commonCreditDocKey;
	private Long prId;
	private String prDocKey;
	private String prTaxPeriod;
	private String dataOriginInTypeCode;
	private String custGstin;
	private String docType;
	private String docNum;
	private LocalDate docDate;
	private String supplierGstin;
	private int itemSerialNum;
	private String commonSupplierIndicator;
	private String revisedTaxPeriod;
	private Long fileId;
	private String fileName;
	private Boolean isDelete;
	private String createdBy;
	protected LocalDateTime createdOn;
	private String updatedBy;
	protected LocalDateTime updatedOn;
	protected String duplicateKeyCheck;
}
