package com.ey.advisory.app.anx2.vendorsummary;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Mohit Basak
 *
 */
@Data
@AllArgsConstructor
public class VendorReconSummaryReqDto {

	@Expose
	private Long entityId;
	
	@Expose
	private List<String> gstins;
	
	@Expose
	private String taxPeriod;

	@Expose
	private List<String> vendorPan;
	
	@Expose
	private List<String> vendorGstin;
	
	@Expose
	private List<String> data;
	
}
