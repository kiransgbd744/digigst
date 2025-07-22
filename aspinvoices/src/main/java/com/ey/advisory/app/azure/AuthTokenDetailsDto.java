package com.ey.advisory.app.azure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Saif.S
 *
 */
@Data
public class AuthTokenDetailsDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("token")
	private String token;

	@Expose
	@SerializedName("sk")
	private String sk;

	@Expose
	@SerializedName("genTime")
	private String genTime;

	@Expose
	@SerializedName("expTime")
	private String expTime;

	@Expose
	@SerializedName("errorMsg")
	private String errorMsg;
}
