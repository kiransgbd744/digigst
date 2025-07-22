package com.ey.advisory.core.api;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PollingMessage {

	@Expose
	private String gstin;

	@Expose
	private String taxPeriod;

	@Expose
	private String refId;

	@Expose
	private Long batchId;

	@Expose
	private String section;

	@Expose
	private String returnType;

	@Expose
	private Long userRequestId;

	@Expose
	private Long retryCount;

	@Expose
	private String operationType;

	@Expose
	private String apiAction;

	public PollingMessage(String gstin, String taxPeriod, String refId) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.refId = refId;
	}

	public PollingMessage(String gstin, String taxPeriod, String refId,
			Long batchId, String returnType, String section, Long userRequestId,
			Long retryCount, String operationType) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.refId = refId;
		this.batchId = batchId;
		this.section = section;
		this.returnType = returnType;
		this.userRequestId = userRequestId;
		this.retryCount = retryCount;
		this.operationType = operationType;
	}

	public PollingMessage(String gstin, String taxPeriod, String refId,
			Long batchId, String returnType) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.refId = refId;
		this.batchId = batchId;
		this.returnType = returnType;
	}

}
