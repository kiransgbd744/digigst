package com.ey.advisory.app.docs.dto.anx1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Data
public class GstnErrorCodeDto {
	@Expose
	@SerializedName("errCd")
	private String errCd;
	@Expose
	@SerializedName("errMsg")
	private String errMsg;
	
	@Expose
	@SerializedName("error_msg")
	private String error_msg;
	@Expose
	@SerializedName("error_cd")
	private String error_cd;
}
