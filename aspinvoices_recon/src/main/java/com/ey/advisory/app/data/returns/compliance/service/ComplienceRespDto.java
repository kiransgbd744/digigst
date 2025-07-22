package com.ey.advisory.app.data.returns.compliance.service;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class ComplienceRespDto {

	@Expose
	@SerializedName("initiateTime")
	private String initiateTime;

	@Expose
	@SerializedName("initiatestatus")
	private String initiatestatus;

	@Expose
	@SerializedName("ComplienceSummeryRespDto")
	private List<ComplienceSummeryRespDto> ComplienceSummeryRespDto;

}
