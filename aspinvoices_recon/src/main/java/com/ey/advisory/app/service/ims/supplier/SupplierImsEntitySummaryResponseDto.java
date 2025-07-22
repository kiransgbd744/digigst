package com.ey.advisory.app.service.ims.supplier;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class SupplierImsEntitySummaryResponseDto {

	@Expose
	private String gstin;

	@Expose
	private String authTokenStatus;
	
	@Expose
	private String state;

	@Expose
	private String regType;
	
	@Expose
	private String imsStatus;

	@Expose
	private String imsTimeStamp;

	@Expose
	private Integer totalRecords = 0;
	
	@Expose
	private Integer accepted = 0;

	@Expose
	private Integer pending = 0;
	
	@Expose
	private Integer rejected = 0;
	
	@Expose
	private Integer noAction = 0;

	@Expose
	private boolean diffWithGstr1And1A;
	
	@Expose
	private String gstr1SummaryStatus;
	
	@Expose
	private String gstr1ASummaryStatus;
	
	@Expose
	private String gstr1SummaryStatusTimeStamp;
	
	@Expose
	private String gstr1ASummaryStatusTimeStamp;
}
