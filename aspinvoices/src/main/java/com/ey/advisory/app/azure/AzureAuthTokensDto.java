package com.ey.advisory.app.azure;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Saif.S
 *
 */
@Data
public class AzureAuthTokensDto {

	@Expose
	@SerializedName("statusCd")
	private String statusCd;

	@Expose
	@SerializedName("authTokenDetails")
	private List<AuthTokenDetailsDto> authTokenDetails;

	@Expose
	@SerializedName("error")
	private ErrorDto error;
}
