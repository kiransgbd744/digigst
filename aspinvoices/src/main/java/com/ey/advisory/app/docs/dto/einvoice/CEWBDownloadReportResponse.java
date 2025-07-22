/**
 * 
 */
package com.ey.advisory.app.docs.dto.einvoice;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class CEWBDownloadReportResponse {

	@Expose
	@SerializedName("CEWBNo")
	private String cewbNum;

	@Expose
	@SerializedName("PreviousCEWBNo")
	private String previousCewbNo;

	@Expose
	@SerializedName("CEWBStatus")
	private String cewbStatus;

	@Expose
	@SerializedName("CEWBTimestamp")
	private String cewbTimestamp;

	@Expose
	@SerializedName("vehicleNo")
	private String vehicleNo;

	@Expose
	@SerializedName("tansportMode")
	private String tansportMode;

	@Expose
	@SerializedName("fromPlace")
	private String fromPlace;

	@Expose
	@SerializedName("fromState")
	private String fromState;

	@Expose
	@SerializedName("transDocNum")
	private String transDocNum;

	@Expose
	@SerializedName("transDocDate")
	private String transDocDate;

	@Expose
	@SerializedName("noofEWB")
	private Integer noOfEwb;

	@Expose
	@SerializedName("GSTIN")
	private String gstin;

	@Expose
	@SerializedName("errorPoint")
	private String errorPoint;

	@Expose
	@SerializedName("errorCode")
	private String errorCode;

	@Expose
	@SerializedName("errorDesc")
	private String errorDesc;

	@Expose
	@SerializedName("serialNo")
	private Long serialNo;

	@Expose
	@SerializedName("fileId")
	private Long fileId;

	@Expose
	@SerializedName("EWB")
	private List<String> ewbNo;

	private String ewbNum;

}
