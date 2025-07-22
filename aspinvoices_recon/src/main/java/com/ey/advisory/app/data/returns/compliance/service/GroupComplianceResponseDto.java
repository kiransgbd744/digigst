package com.ey.advisory.app.data.returns.compliance.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Data
public class GroupComplianceResponseDto {
	
	@Expose
	@SerializedName("entityId")
	private String entityId;
	
	@Expose
	@SerializedName("entityName")
	private String entityName;
	
	@Expose
	@SerializedName("totalComplianceCount")
	private String totalComplianceCount;

	@Expose
	@SerializedName("initiateTime")
	private String initiateTime;
	@Expose
	@SerializedName("initiatestatus")
	private String initiatestatus;
	@Expose
	@SerializedName("state")
	private String state;

	@Expose
	@SerializedName("janFiledCount")
	private String janFiledCount;

	@Expose
	@SerializedName("janFiledTimestamp")
	private String janFiledTimestamp;

	@Expose
	@SerializedName("febFiledCount")
	private String febFiledCount;

	@Expose
	@SerializedName("febFiledTimestamp")
	private String febFiledTimestamp;

	@Expose
	@SerializedName("marchFiledCount")
	private String marchFiledCount;

	@Expose
	@SerializedName("marchFiledTimestamp")
	private String marchFiledTimestamp;

	@Expose
	@SerializedName("aprilFiledCount")
	private String aprilFiledCount;

	@Expose
	@SerializedName("aprilFiledTimestamp")
	private String aprilFiledTimestamp;

	@Expose
	@SerializedName("mayFiledCount")
	private String mayFiledCount;

	@Expose
	@SerializedName("mayFiledTimestamp")
	private String mayFiledTimestamp;

	@Expose
	@SerializedName("juneFiledCount")
	private String juneFiledCount;

	@Expose
	@SerializedName("juneFiledTimestamp")
	private String juneFiledTimestamp;

	@Expose
	@SerializedName("julyFiledCount")
	private String julyFiledCount;

	@Expose
	@SerializedName("julyFiledTimestamp")
	private String julyFiledTimestamp;

	@Expose
	@SerializedName("augFiledCount")
	private String augFiledCount;

	@Expose
	@SerializedName("augFiledTimestamp")
	private String augFiledTimestamp;

	@Expose
	@SerializedName("sepFiledCount")
	private String sepFiledCount;

	@Expose
	@SerializedName("sepFiledTimestamp")
	private String sepFiledTimestamp;

	@Expose
	@SerializedName("octFiledCount")
	private String octFiledCount;

	@Expose
	@SerializedName("octFiledTimestamp")
	private String octFiledTimestamp;

	@Expose
	@SerializedName("novFiledCount")
	private String novFiledCount;

	@Expose
	@SerializedName("novFiledTimestamp")
	private String novFiledTimestamp;

	@Expose
	@SerializedName("decFiledCount")
	private String decFiledCount;

	@Expose
	@SerializedName("decFiledTimestamp")
	private String decFiledTimestamp;

	@Expose
	@SerializedName("q1FiledCount")
	private String q1FiledCount;

	@Expose
	@SerializedName("q1FiledTimestamp")
	private String q1FiledTimestamp;

	@Expose
	@SerializedName("q2FiledCount")
	private String q2FiledCount;

	@Expose
	@SerializedName("q2FiledTimestamp")
	private String q2FiledTimestamp;

	@Expose
	@SerializedName("q3FiledCount")
	private String q3FiledCount;

	@Expose
	@SerializedName("q3FiledTimestamp")
	private String q3FiledTimestamp;

	@Expose
	@SerializedName("q4FiledCount")
	private String q4FiledCount;

	@Expose
	@SerializedName("q4FiledTimestamp")
	private String q4FiledTimestamp;

	@Expose
	@SerializedName("h1FiledCount")
	private String h1FiledCount;

	@Expose
	@SerializedName("h1FiledTimestamp")
	private String h1FiledTimestamp;

	@Expose
	@SerializedName("h2FiledCount")
	private String h2FiledCount;

	@Expose
	@SerializedName("h2FiledTimestamp")
	private String h2FiledTimestamp;
	
	@Expose
	@SerializedName("filingStatusCount")
	private String filingStatusCount;

	@Expose
	@SerializedName("ackNo")
	private String ackNo;

	@Expose
	@SerializedName("date")
	private String date;

	@Expose
	@SerializedName("time")
	private String time;


}
