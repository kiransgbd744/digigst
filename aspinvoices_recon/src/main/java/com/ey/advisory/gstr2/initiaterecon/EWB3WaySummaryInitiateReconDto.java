package com.ey.advisory.gstr2.initiaterecon;

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
 * @author vishal.verma
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EWB3WaySummaryInitiateReconDto {
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("gstin")
	private List<String> sgstins = new ArrayList<>();
	
	@Expose
	@SerializedName("toTaxPeriod")
	private  String toTaxPeriod;
	
	@Expose
	@SerializedName("fromTaxPeriod")
	private  String fromTaxPeriod;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;
	
	@Expose
	@SerializedName("criteria")
	private  String criteria;
	

}
