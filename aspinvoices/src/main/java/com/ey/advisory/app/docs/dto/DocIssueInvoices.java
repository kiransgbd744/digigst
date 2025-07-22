package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DocIssueInvoices {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;
	
	@Expose
	@SerializedName("chksum")
	private String checkSum;
	
	@Expose
	@SerializedName("doc_det")
	List<DocIssueDetails> docIssueDetails;

}

