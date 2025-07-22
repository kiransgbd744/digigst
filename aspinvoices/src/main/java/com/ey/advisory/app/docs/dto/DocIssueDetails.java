package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DocIssueDetails {

	@Expose
	@SerializedName("doc_num")
	private Integer docNum;
	
	@Expose
	@SerializedName("docs")
	List<DocIssueList> docIssueList;
	
}
