package com.ey.advisory.einv.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GetSyncGstinUIResponseDto {
	
	public GetSyncGstinUIResponseDto() {
		super();
	}

	@Expose
	@SerializedName("Sgstin")
	private String sgstin;
	
	@Expose
	@SerializedName("SyncGstin")
	private String syncGstin;
	
	@Expose
	@SerializedName("response")
	private String response;

	public GetSyncGstinUIResponseDto(String sgstin, String syncGstin,
			String response) {
		super();
		this.sgstin = sgstin;
		this.syncGstin = syncGstin;
		this.response = response;
	}
	

	public GetSyncGstinUIResponseDto(String sgstin, String response) {
		super();
		this.sgstin = sgstin;
		this.response = response;
	}
	
	
	
}
