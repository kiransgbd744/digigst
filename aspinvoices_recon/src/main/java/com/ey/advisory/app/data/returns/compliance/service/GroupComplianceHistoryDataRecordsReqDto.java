package com.ey.advisory.app.data.returns.compliance.service;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Data
public class GroupComplianceHistoryDataRecordsReqDto {

	
	@Expose
	@SerializedName("financialYear")
	private String financialYear;
	
	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<Long>> dataSecAttrs;

}
