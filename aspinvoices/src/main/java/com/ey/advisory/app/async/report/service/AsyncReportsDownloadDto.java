package com.ey.advisory.app.async.report.service;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AsyncReportsDownloadDto {


	@Expose
	@SerializedName("fileId")
	private Long fileId;

	@Expose
	@SerializedName("reportCateg")
	private List<String> reportCateg;
	
	@Expose
	@SerializedName("reportType")
	private String reportType;
	
	@Expose
	@SerializedName("reportStatus")
	private String reportStatus;
	
	@Expose
	@SerializedName("dataType")
	private List<String> dataType;
	
	@Expose
	@SerializedName("fileName")
	private String fileName;

	@Expose
	@SerializedName("requestFromDate")
	private String requestFromDate;

	@Expose
	@SerializedName("requestToDate")
	private String requestToDate;
	
	@Expose
	@SerializedName("requestToDate1")
	private LocalDateTime requestToDate1;
	
	@Expose
	@SerializedName("requestFromDate1")
	private LocalDateTime requestFromDate1;

	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("initialitedOn")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("initialitedBy")
	private String createdBy;
	

	@Expose
	@SerializedName("userName")
	private String userName;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
}
