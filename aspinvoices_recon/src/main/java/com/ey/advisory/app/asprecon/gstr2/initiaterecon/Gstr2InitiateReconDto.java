package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

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
public class Gstr2InitiateReconDto {
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("gstin")
	private List<String> sgstins = new ArrayList<>();
	
	@Expose
	@SerializedName("toTaxPeriod")
	private  String toTaxPeriodPR;
	
	@Expose
	@SerializedName("fromTaxPeriod")
	private  String fromTaxPeriodPR;
	
	@Expose
	@SerializedName("toTaxPeriod2A")
	private  String toTaxPeriod2A;
	
	@Expose
	@SerializedName("fromTaxPeriod2A")
	private  String fromTaxPeriod2A;
	
	@Expose
	@SerializedName("toDocDate")
	private  String toDocDate;
	
	@Expose
	@SerializedName("fromDocDate")
	private  String fromDocDate;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;
	
	@Expose
	@SerializedName("reconType")
	private  String reconType;
	
	

}
