package com.ey.advisory.einv.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class EInvEWBUIResponseDto {
	
	public EInvEWBUIResponseDto() {
		super();
	}

	@Expose
	@SerializedName("docId")
	private Long docId;
	
	@Expose
	@SerializedName("docNo")
	private String docNo;
	
	@Expose
	@SerializedName("response")
	private String response;

	public EInvEWBUIResponseDto(Long docId, String response) {
		super();
		this.docId = docId;
		this.response = response;
	}
	
	public EInvEWBUIResponseDto(Long docId, String docNo, String response) {
		super();
		this.docId = docId;
		this.docNo = docNo;
		this.response = response;
	}
	
	
	
}
