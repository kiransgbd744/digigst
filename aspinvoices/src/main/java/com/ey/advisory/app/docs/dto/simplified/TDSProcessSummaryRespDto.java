package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class TDSProcessSummaryRespDto {

	private String gstin;
	private String state;
	private String docKey;
	private String docKeyPs;
	private String retPeriod;
	private String regType;
	private String authToken;
	private String getTdsStatus;
	private String getTdstimeStamp;
	private String fillingStatus;
	private String fillingtimeStamp;
	private BigDecimal taxableValue;
	private BigDecimal intigratedTax;
	private BigDecimal centralTax;
	private BigDecimal stateUtTax;

	private int notSentCount;

	private int savedCount;
	private int notSavedCount;
	private int errorCount;
	private int totalCount;
	private int totalResult;
//	private String timeStamp;

}
