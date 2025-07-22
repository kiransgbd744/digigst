/**
 * 
 */
package com.ey.advisory.core.dto;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class ProcessedVsSubmittedRequestDto {

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;

	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;

	@Expose
	@SerializedName("tableType")
	private List<String> tableType;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

}
