package com.ey.advisory.app.gstr1a.einv;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class Gstr1AEinvInitiateReconGstinDetailsDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("stateName")
	private String stateName;

	@Expose
	@SerializedName("regType")
	private String regType;

	@Expose
	@SerializedName("authStatus")
	private String auth;

	@Expose
	@SerializedName("reconStatus")
	private String reconStatus;

	@Expose
	@SerializedName("reconCreatedOn")
	private String reconCreatedOn;

}
