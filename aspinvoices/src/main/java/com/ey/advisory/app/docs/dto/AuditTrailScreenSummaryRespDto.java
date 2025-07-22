/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class AuditTrailScreenSummaryRespDto {

	@Expose
	@SerializedName("processingFrequency")
	private int processingFrequency;

	@Expose
	@SerializedName("latestStatus")
	private String latestStatus;

	@Expose
	@SerializedName("latestStatusDate")
	private String latestStatusDate;

	@Expose
	@SerializedName("latestStatusTime")
	private String latestStatusTime;

	@Expose
	@SerializedName("tableType")
	private String tableType;

	@Expose
	@SerializedName("items")
	private List<AuditTrailScreenSummaryItemRespDto> items;

}
