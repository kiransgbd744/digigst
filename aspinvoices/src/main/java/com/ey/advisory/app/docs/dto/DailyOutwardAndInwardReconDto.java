/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class DailyOutwardAndInwardReconDto {

	@Expose
	@SerializedName("extId")
	private String extractionId;

	@Expose
	@SerializedName("accVchrDate")
	private LocalDate accountingVoucherDate;

	@Expose
	@SerializedName("extType")
	private String extractionType;

	@Expose
	@SerializedName("extDocCount")
	private Integer extractedDocCount = 0;

	@Expose
	@SerializedName("strErrCount")
	private Integer structuralErrorCount = 0;
	@Expose
	@SerializedName("onHold")
	private Integer onHold = 0;

	@Expose
	@SerializedName("availForPush")
	private Integer availableForPush = 0;

	@Expose
	@SerializedName("pushToCloud")
	private Integer pushedToCloud = 0;

	@Expose
	@SerializedName("errorInPush")
	private Integer erroredInPush = 0;

	@Expose
	@SerializedName("extractedOn")
	private LocalDate extractedOn;

	@Expose
	@SerializedName("availInCloud")
	private Integer availInCloud = 0;

	@Expose
	@SerializedName("difference")
	private Integer difference = 0;

	@Expose
	@SerializedName("sourceId")
	private String sourceId;

}
