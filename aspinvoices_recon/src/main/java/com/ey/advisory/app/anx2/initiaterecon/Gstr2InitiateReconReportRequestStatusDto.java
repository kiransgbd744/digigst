package com.ey.advisory.app.anx2.initiaterecon;

import java.io.Serializable;
import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2InitiateReconReportRequestStatusDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private Long requestId = 0L;
	
	@Expose
	private String entityName;

	@Expose
	private Integer gstinCount = 0;

	@Expose
	private Integer toTaxPeriod;

	@Expose
	private Integer fromTaxPeriod;

	@Expose
	private Integer toTaxPeriod2A;

	@Expose
	private Integer fromTaxPeriod2A;

	@Expose
	private String toDocDate;

	@Expose
	private String fromDocDate;

	@Expose
	private String reqType;

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
	private String path;

	@Expose
	private String reconType;

	@Expose
	private String emailId;
	
	@Expose
	private String isItcRejOpted;
	
	

}
