package com.ey.advisory.app.dashboard.apiCall;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class APIFyRespDto {

	@Expose
	@SerializedName("fy")
	private String fy;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	public APIFyRespDto(String fy, String taxPeriod, String apiStatus,
			LocalDateTime initiatedOn) {
		super();
		this.fy = fy;
		this.taxPeriod = taxPeriod;
		this.apiStatus = apiStatus;
		this.initiatedOn = initiatedOn;
	}
	

	public APIFyRespDto(String fy, String apiStatus) {
		super();
		this.fy = fy;
		this.apiStatus = apiStatus;
	}


	@Expose
	@SerializedName("status")
	private String apiStatus;
	
	@Expose
	@SerializedName("initiatedOn")
	private LocalDateTime initiatedOn;

}
