/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class ProcessedVsSubmittedResponseDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("regType")
	private String regType;

	@Expose
	@SerializedName("state")
	private String state;

	@Expose
	@SerializedName("filingStatus")
	private String filingStatus;

	@Expose
	@SerializedName("filingDateTime")
	private String filingDateTime;

	@Expose
	@SerializedName("getCallStatus")
	private String getCallStatus;

	@Expose
	@SerializedName("getCallDateTime")
	private String getCallDateTime;

	@Expose
	@SerializedName("aprCount")
	private BigInteger aprCount;

	@Expose
	@SerializedName("aprTaxableValue")
	private BigDecimal aprTaxableValue;

	@Expose
	@SerializedName("aprTotalTax")
	private BigDecimal aprTotalTax;

	@Expose
	@SerializedName("asrCount")
	private BigInteger asrCount;

	@Expose
	@SerializedName("asrTaxableValue")
	private BigDecimal asrTaxableValue;

	@Expose
	@SerializedName("asrTotalTax")
	private BigDecimal asrTotalTax;
	
	@Expose
	@SerializedName("notSentCount")
	private BigInteger notSentCount;
	
	@Expose
	@SerializedName("savedCount")
	private BigInteger savedCount;
	
	@Expose
	@SerializedName("notSavedCount")
	private BigInteger notSavedCount;
	
	@Expose
	@SerializedName("errorCount")
	private BigInteger errorCount;
	
	@Expose
	@SerializedName("totalCount")
	private BigInteger totalCount;

}
