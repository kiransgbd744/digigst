package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Sakshi.jain
 *
 */

@Data
public class Gstr1Vs3BRequestStatusDto {
	
	@Expose
	private Long requestId = 0L;

	@Expose
	private Integer gstinCount = 0;

	@Expose
	private Integer toTaxPeriod;

	@Expose
	private Integer fromTaxPeriod;

	@Expose
	private List<GstinDto> gstins;

	@Expose
	private String initiatedOn;

	@Expose
	private String initiatedBy;

	@Expose
	private String completionOn;

	@Expose
	private String status;

	@Expose
	private String emailId;
	
	@Expose
	private String rptDownldPath;
	
	@Expose
	private boolean isDownld;

}
