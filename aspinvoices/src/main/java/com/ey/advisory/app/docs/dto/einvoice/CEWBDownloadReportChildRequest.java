package com.ey.advisory.app.docs.dto.einvoice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class CEWBDownloadReportChildRequest {

	@Expose
	@SerializedName("serialNo")
	private Long serialNo;

	@Expose
	@SerializedName("fileId")
	private Long fileId;

}
