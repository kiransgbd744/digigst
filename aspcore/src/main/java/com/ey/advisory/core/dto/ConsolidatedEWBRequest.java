/**
 * 
 */
package com.ey.advisory.core.dto;

import java.time.LocalDate;
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
public class ConsolidatedEWBRequest {

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("CEWBFrom")
	private LocalDate cewbFrom;

	@Expose
	@SerializedName("CEWBTo")
	private LocalDate cewbTo;

	@Expose
	@SerializedName("CEWBNo")
	private String cewbNum;

	@Expose
	@SerializedName("PreviousCEWBNo")
	private String previousCewbNo;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

}
