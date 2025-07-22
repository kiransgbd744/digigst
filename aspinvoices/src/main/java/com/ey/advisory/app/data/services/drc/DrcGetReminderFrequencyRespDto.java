package com.ey.advisory.app.data.services.drc;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class DrcGetReminderFrequencyRespDto {

	@Expose
	private Long entityId;

	@Expose
	private List<String> drc01bFreq = new ArrayList<>();
	
	@Expose
	private List<String> drc01cFreq = new ArrayList<>();
	
	@Expose
	private String commType;
	
	@Expose
	private String fromTaxPeriod;
	
	@Expose
	private String toTaxPeriod;
	
	@Expose
	private List<String> gstins;
	
	@Expose
	private List<String> emailType;
	
	@Expose
	private String errMsg;
	
}
