package com.ey.advisory.app.docs.dto.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr1SummarySaveStatusRespDto {

	@Expose
	@SerializedName("sNo")
	private Integer sNo;

	@Expose
	@SerializedName("date")
	private String date;

	@Expose
	@SerializedName("time")
	private String time;

	@Expose
	@SerializedName("action")
	private String action;

	@Expose
	@SerializedName("refId")
	private String refId;

	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("errorCount")
	private String errorCount;

	@Expose
	@SerializedName("section")
	private String section;

	@Expose
	@SerializedName("errorMsg")
	private String errorMsg;

	@Expose
	@SerializedName("createdOn")
	private String createdOn;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("retPeriod")
	private String retPeriod;
}
