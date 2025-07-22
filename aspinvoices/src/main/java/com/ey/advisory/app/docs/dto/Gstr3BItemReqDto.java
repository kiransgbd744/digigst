package com.ey.advisory.app.docs.dto;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr3BItemReqDto {

	@Expose
	@SerializedName("retPrd")
	private String returnPeriod;

	@Expose
	@SerializedName("taxPayerGstin")
	private String sgstin;

	@Expose
	@SerializedName("serialNo")
	private String serialNo;

	@Expose
	@SerializedName("description")
	private String description;

	@Expose
	@SerializedName("igstAmount")
	private String igstAmount;

	@Expose
	@SerializedName("cgstAmount")
	private String cgstAmount;

	@Expose
	@SerializedName("sgstAmount")
	private String sgstAmount;

	@Expose
	@SerializedName("cessAmount")
	private String cessAmount;

	@Expose
	@SerializedName("createdBy")
	private String createdBy;

	@Expose
	@SerializedName("createdOn")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	private String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	private LocalDateTime modifiedOn;
}
