/**
 * 
 */
package com.ey.advisory.app.data.services.itc04stocktrack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
public class Itc04StockTrackingRespDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("state")
	private String state;

	@Expose
	@SerializedName("regType")
	private String regType;

	@Expose
	@SerializedName("mftojwcnt")
	private int mftojwcnt;

	@Expose
	@SerializedName("jwtomfcnt")
	private int jwtomfcnt;

	@Expose
	@SerializedName("jwtojwcnt")
	private int jwtojwcnt;

	@Expose
	@SerializedName("soldjwcnt")
	private int soldjwcnt;

	@Expose
	@SerializedName("igOpenChallanGrYear")
	private int igOpenChallanGrYear;

	@Expose
	@SerializedName("igOpenChallanlsYear")
	private int igOpenChallanLsYear;

	@Expose
	@SerializedName("cgOpenChallanGrYear")
	private int cgOpenChallanGrYear;

	@Expose
	@SerializedName("cgOpenChallanlsYear")
	private int cgOpenChallanLsYear;

	@Expose
	@SerializedName("reportStatus")
	private String reportStatus;

	@Expose
	@SerializedName("initiatedOn")
	private String inititatedOn;
}
