package com.ey.advisory.service.gstr3bSummary;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class Gstr3bSummaryUploadDto {

	private String returnPeriod;
	private String gstin;
	private String tableNumber;
	private String totalTaxableValue;
	private String igstAmount;
	private String cgstAmount;
	private String sgstAmount;
	private String cessAmount;
	private String pos;
	private String docKey;
	private Long fileId;
	private String fileName;
	private boolean isPsd;
	private String errorCode;
	private String errorDesc;
	private boolean isActive;
	private LocalDateTime createdDate;

}
