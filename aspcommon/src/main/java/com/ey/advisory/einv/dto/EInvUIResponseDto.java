package com.ey.advisory.einv.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class EInvUIResponseDto {
	
	@Expose
	@SerializedName("docId")
	private String docId;
	
	@Expose
	@SerializedName("response")
	private String response;
}
