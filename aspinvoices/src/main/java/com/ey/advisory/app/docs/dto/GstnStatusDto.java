package com.ey.advisory.app.docs.dto;

import com.ey.advisory.app.docs.dto.anx1.GstnErrorCodeDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Data
public class GstnStatusDto {

	@Expose
	@SerializedName("form_typ")
	private String form_typ;
	@Expose
	@SerializedName("status_cd")
	private String status_cd;
	@Expose
	@SerializedName("action")
	private String action;
	@Expose
	@SerializedName("error_report")
    private GstnErrorCodeDto error_report;
	@Expose
	@SerializedName("errorCd")
	private String errorCd;
	@Expose
	@SerializedName("errorMsg")
	private String errorMsg;
}
