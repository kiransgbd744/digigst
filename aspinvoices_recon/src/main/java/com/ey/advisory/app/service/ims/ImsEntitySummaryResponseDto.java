package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class ImsEntitySummaryResponseDto {

	@Expose
	private String gstin;

	@Expose
	private String state;

	@Expose
	private String regType;

	@Expose
	private String authToken;

	@Expose
	private String saveStatus;

	@Expose
	private String timeStamp;

	@Expose
	private Integer gstnTotal = 0;

	@Expose
	private Integer gstnNoAction = 0;

	@Expose
	private Integer gstnAccepted = 0;

	@Expose
	private Integer gstnRejected = 0;

	@Expose
	private Integer gstnPendingTotal = 0;

	@Expose
	private Integer aspTotal = 0;

	@Expose
	private Integer aspNoAction = 0;

	@Expose
	private Integer aspAccepted = 0;

	@Expose
	private Integer aspRejected = 0;

	@Expose
	private Integer aspPendingTotal = 0;
}
