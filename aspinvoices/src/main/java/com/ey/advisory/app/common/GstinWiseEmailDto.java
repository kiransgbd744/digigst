package com.ey.advisory.app.common;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Saif.S
 *
 */
@Getter
@Setter
@ToString
public class GstinWiseEmailDto {

	@SerializedName("cgstin")
	@Expose
	private String cgstin;

	@SerializedName("getStatus")
	@Expose
	private String getStatus;

	@SerializedName("sectionWiseDetails")
	@Expose
	private List<SectionWiseDetailsDto> sectionWiseDetails;

	@SerializedName("scheduleDate")
	@Expose
	private String scheduleDate;

	@SerializedName("generationDate")
	@Expose
	private String generationDate;

	
}
