package com.ey.advisory.app.data.services.drc;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class DrcCommRespDto {

	@Expose
	private Long entityId;
	
	@Expose
	private Integer requestId;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private String emailTime;
	
	@Expose
	private String gstin;
	
	@Expose
	private String reportStatus;
	
	@Expose
	private boolean isDownload;
	
	@Expose
	private String emailStatus;
	
	@Expose
	private String emailType;
	
	@Expose
	private String errMsg;
	
}
