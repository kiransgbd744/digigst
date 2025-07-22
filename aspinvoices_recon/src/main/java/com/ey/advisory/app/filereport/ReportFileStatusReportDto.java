package com.ey.advisory.app.filereport;

import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportFileStatusReportDto {
	

private static final long serialVersionUID = 1L;
	
	@Expose
	private Long requestId = 0L;
	
	@Expose
	private String reportCateg ;
	
	@Expose
	private String reportType ;
	
	@Expose
	private String fileName ;
	
	@Expose
	private Integer gstinCount;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private List<GstinDto> gstins;
	
	@Expose
	private LocalDateTime initiatedOn;
	
	@Expose
	private String initiatedBy;
	
	@Expose
	private LocalDateTime completionOn;
	
	@Expose
	private String status;
	
	@Expose
	private String path;
	
	@Expose
	private String dataType;
	
	
	public ReportFileStatusReportDto() { }

}
