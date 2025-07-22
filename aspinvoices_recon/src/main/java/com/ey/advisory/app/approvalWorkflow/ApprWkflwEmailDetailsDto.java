package com.ey.advisory.app.approvalWorkflow;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ApprWkflwEmailDetailsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("action")
	@Expose
	private String action;

	@SerializedName("actionEmail")
	@Expose
	private String actionEmail;

	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("taxPeriod")
	@Expose
	private String taxPeriod;

	@SerializedName("retType")
	@Expose
	private String retType;

	@SerializedName("refId")
	@Expose
	private String refId;

	@SerializedName("checkerEmails")
	@Expose
	private String checkerEmails;

	@SerializedName("makerEmails")
	@Expose
	private String makerEmails;

	@SerializedName("dueDate")
	@Expose
	private String dueDate;

	@SerializedName("appUrl")
	@Expose
	private String appUrl;

	@SerializedName("identifier")
	@Expose
	private String identifier;

}