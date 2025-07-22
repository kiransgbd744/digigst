package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Gstr3BAutoCalcReportDownloadReq {
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("gstin")
	private List<String> gstin;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

}
