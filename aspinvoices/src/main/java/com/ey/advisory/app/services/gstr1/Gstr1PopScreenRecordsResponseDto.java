package com.ey.advisory.app.services.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr1PopScreenRecordsResponseDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("b2bTimeStamp")
	private String b2bTimeStamp;
	
	@Expose
	@SerializedName("b2bStatus")
	private String b2bStatus;

	@Expose
	@SerializedName("cdnrTimeStamp")
	private String cdnrTimeStamp;
	
	@Expose
	@SerializedName("cdnrStatus")
	private String cdnrStatus;
	
	@Expose
	@SerializedName("cdnurTimeStamp")
	private String cdnurTimeStamp;
	
	@Expose
	@SerializedName("cdnurStatus")
	private String cdnurStatus;

	@Expose
	@SerializedName("exportsTimestamp")
	private String exportsTimestamp;

	@Expose
	@SerializedName("exportStatus")
	private String exportStatus;
	
	@Expose
	@SerializedName("table4TimeStamp")
	private String table4TimeStamp;

	@Expose
	@SerializedName("table4Status")
	private String table4Status;
	
	@Expose
	@SerializedName("table5ATimeStamp")
	private String table5ATimeStamp;

	@Expose
	@SerializedName("table5AStatus")
	private String table5AStatus;
	
	@Expose
	@SerializedName("table5BTimeStamp")
	private String table5BTimeStamp;

	@Expose
	@SerializedName("table5BStatus")
	private String table5BStatus;
	
	@Expose
	@SerializedName("table5CTimeStamp")
	private String table5CTimeStamp;

	@Expose
	@SerializedName("table5CStatus")
	private String table5CStatus;

}
