package com.ey.advisory.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Getter
@Setter
@ToString
public class GenerateParamsForDeepLink {

	@Expose
	@SerializedName("payeeAddress")
	protected String payeeAddress;

	@Expose
	@SerializedName("payeeMerCode")
	protected String payeeMerCode;

	@Expose
	@SerializedName("payeeName")
	protected String payeeName;

	@Expose
	@SerializedName("transMode")
	protected String transMode;

	@Expose
	@SerializedName("qrExpireTime")
	protected String qrExpireTime;

	@Expose
	@SerializedName("transQRMed")
	protected String transQRMed;

	@Expose
	@SerializedName("paymentInfo")
	protected String paymentInfo;
}
