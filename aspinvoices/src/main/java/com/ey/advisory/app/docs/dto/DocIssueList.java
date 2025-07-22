package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DocIssueList {

	@Expose
	@SerializedName("num")
	private Integer serialNum;
	
	@Expose
	@SerializedName("from")
	private String fromSerialNum;
	
	@Expose
	@SerializedName("to")
	private String toSerialNum;
	
	@Expose
	@SerializedName("totnum")
	private Integer totalNum;
	
	@Expose
	@SerializedName("cancel")
	private Integer cancelled;
	
	@Expose
	@SerializedName("net_issue")
	private Integer netIssued;

}
