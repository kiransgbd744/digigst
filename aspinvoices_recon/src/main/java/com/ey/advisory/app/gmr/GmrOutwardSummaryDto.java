package com.ey.advisory.app.gmr;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GmrOutwardSummaryDto {
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("gstin")
	private List<String> sgstins;
	
	@Expose
	@SerializedName("toTaxPeriod")
	private String toTaxPeriod;
	
	@Expose
	@SerializedName("fromTaxPeriod")
	private  String fromTaxPeriod;
	
}
