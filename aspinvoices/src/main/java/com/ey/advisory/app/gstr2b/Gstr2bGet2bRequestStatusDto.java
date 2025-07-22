package com.ey.advisory.app.gstr2b;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr2bGet2bRequestStatusDto {

	@Expose
	private Long reqId;

	@Expose
	private String reportType;

	@Expose
	private Long gstinCount;
	
	@Expose
	private List<Gstr2BReqStatusDetailsDto> taxPeriodList;
	
	@Expose
	private List<Gstr2BReqStatusDetailsDto> gstinList;

	@Expose
	private Long taxPeriodCount;

	@Expose
	private String filePath;

	@Expose
	private String status;

	@Expose
	private String createdOn;

	@Expose
	private String createdBy;

	@Expose
	private String completedOn;

}
