package com.ey.advisory.app.docs.dto.anx1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr2aProcessedDataRecordsRespDto {

	@Expose
	@SerializedName("state")
	private String state;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("regType")
	private String regType;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("janStatus")
	private String janStatus;

	@Expose
	@SerializedName("janTimestamp")
	private String janTimestamp;

	@Expose
	@SerializedName("febStatus")
	private String febStatus;

	@Expose
	@SerializedName("febTimeStamp")
	private String febTimeStamp;

	@Expose
	@SerializedName("marchStatus")
	private String marchStatus;

	@Expose
	@SerializedName("marchTimestamp")
	private String marchTimestamp;

	@Expose
	@SerializedName("aprilStatus")
	private String aprilStatus;

	@Expose
	@SerializedName("apriltimestamp")
	private String apriltimestamp;

	@Expose
	@SerializedName("mayStatus")
	private String mayStatus;

	@Expose
	@SerializedName("mayTimeStamp")
	private String mayTimeStamp;

	@Expose
	@SerializedName("juneStatus")
	private String juneStatus;

	@Expose
	@SerializedName("juneTimeStamp")
	private String juneTimeStamp;

	@Expose
	@SerializedName("julyStatus")
	private String julyStatus;

	@Expose
	@SerializedName("julyTimestamp")
	private String julyTimestamp;

	@Expose
	@SerializedName("augStatus")
	private String augStatus;

	@Expose
	@SerializedName("augTimeStamp")
	private String augTimeStamp;

	@Expose
	@SerializedName("sepStatus")
	private String sepStatus;

	@Expose
	@SerializedName("sepTimeStamp")
	private String sepTimeStamp;

	@Expose
	@SerializedName("octStatus")
	private String octStatus;

	@Expose
	@SerializedName("octTimestamp")
	private String octTimestamp;

	@Expose
	@SerializedName("novStatus")
	private String novStatus;

	@Expose
	@SerializedName("novTimeStamp")
	private String novTimeStamp;

	@Expose
	@SerializedName("decStatus")
	private String decStatus;

	@Expose
	@SerializedName("decTimestamp")
	private String decTimestamp;

	@Expose
	@SerializedName("q1Status")
	private String q1Status;

	@Expose
	@SerializedName("q1Timestamp")
	private String q1Timestamp;

	@Expose
	@SerializedName("q2Status")
	private String q2Status;

	@Expose
	@SerializedName("q2Timestamp")
	private String q2Timestamp;

	@Expose
	@SerializedName("q3Status")
	private String q3Status;

	@Expose
	@SerializedName("q3Timestamp")
	private String q3Timestamp;

	@Expose
	@SerializedName("q4Status")
	private String q4Status;

	@Expose
	@SerializedName("q4Timestamp")
	private String q4Timestamp;

	@Expose
	@SerializedName("filingStatus")
	private String filingStatus;

	@Expose
	@SerializedName("ackNo")
	private String ackNo;

	@Expose
	@SerializedName("date")
	private String date;

	@Expose
	@SerializedName("time")
	private String time;
	
	@Expose
	@SerializedName("initiateTime")
	private String initiateTime;
	
	@Expose
	@SerializedName("initiatestatus")
	private String initiatestatus;

}
