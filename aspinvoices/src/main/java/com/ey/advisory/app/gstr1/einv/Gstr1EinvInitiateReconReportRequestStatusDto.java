package com.ey.advisory.app.gstr1.einv;

import java.io.Serializable;
import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Rajesh N K
 *
 */
@Data
public class Gstr1EinvInitiateReconReportRequestStatusDto
		implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Expose
	@SerializedName("requestId")
	private Long requestId = 0L;
	
	@Expose
	@SerializedName("gstinCount")
	private Integer gstinCount = 0;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
	@Expose
	private List<GstinDto> gstins;
	
	@Expose
	@SerializedName("initiatedOn")
	private String initiatedOn;
	
	@Expose
	@SerializedName("initiatedBy")
	private String initiatedBy;
	
	@Expose
	@SerializedName("completionOn")
	private String completionOn;
	
	@Expose
	@SerializedName("status")
	private String status;
	
	@Expose
	@SerializedName("path")
	private String path;

}
