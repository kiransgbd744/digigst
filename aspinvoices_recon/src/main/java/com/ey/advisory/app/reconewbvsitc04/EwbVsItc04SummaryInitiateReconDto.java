package com.ey.advisory.app.reconewbvsitc04;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EwbVsItc04SummaryInitiateReconDto {
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("gstin")
	private List<String> sgstins = new ArrayList<>();
	
	@Expose
	@SerializedName("fy")
	private  String fy;
	
	@Expose
	@SerializedName("fromTaxPeriod")
	private  String fromTaxPeriod;
	
	@Expose
	@SerializedName("toTaxPeriod")
	private  String toTaxPeriod;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;
	
	@Expose
	@SerializedName("criteria")
	private  String criteria;
	

}
