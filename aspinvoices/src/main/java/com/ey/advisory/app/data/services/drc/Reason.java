package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Reason {

	@SerializedName("reasonCode")
	@Expose
	private List<String> reasonCode;

	@SerializedName(value = "reasonDesc", alternate = { "rsn" })
	@Expose
	private String reasonDesc;

	@SerializedName("rsncd")
	@Expose
	private String rsnco;

	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("taxPeriod")
	@Expose
	private String taxPeriod;
	
	@SerializedName("reasonUsr")
	@Expose
	private List<UsrReasonList> reasonUsr;
	
	
}



