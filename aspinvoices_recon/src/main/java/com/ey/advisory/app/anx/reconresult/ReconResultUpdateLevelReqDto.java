package com.ey.advisory.app.anx.reconresult;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReconResultUpdateLevelReqDto {

	@Expose
	private String reportName;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private List<ReconResultUpdateComDetailsDto> commonDetails;
	
}
