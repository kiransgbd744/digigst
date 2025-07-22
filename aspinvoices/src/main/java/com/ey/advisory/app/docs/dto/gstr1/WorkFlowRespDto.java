package com.ey.advisory.app.docs.dto.gstr1;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class WorkFlowRespDto {

	@Expose
	private Long entityId;
	
	@Expose
	private String gstin;

	@Expose
	private String returnPeriod;
	
	@Expose
	private Long requestId;
	
	@Expose
	private LocalDateTime requestedOn;
	
	@Expose
	private String checkerId;
	
	@Expose
	private String status;
	
	@Expose
	private LocalDateTime respondedOn;
	
	@Expose
	private String responseComments;
	
	
}
