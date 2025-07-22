package com.ey.advisory.app.docs.dto.gstr6a;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Ravindra
 *
 */
@Data
public class Gstr6aDashboardScreenReqDto {

	@Expose
	@SerializedName("entity")
	private Long entity;

	@Expose
	@SerializedName("financialYear")
	private String financialYear;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

}
