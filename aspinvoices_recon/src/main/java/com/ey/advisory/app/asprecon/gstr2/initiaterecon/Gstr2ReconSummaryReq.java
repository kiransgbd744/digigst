package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rajesh N K
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2ReconSummaryReq {

	@Expose
	@SerializedName("configId")
	private String configId;

	@Expose
	@SerializedName("gstin")
	private List<String> sgstins;

	@Expose
	@SerializedName("returnPeriod")
	private List<String> returnPeriod;
	
	@Expose
	@SerializedName("reconType")
	private String reconType;
	
	

}
