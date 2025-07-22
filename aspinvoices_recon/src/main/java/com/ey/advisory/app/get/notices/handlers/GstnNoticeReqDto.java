package com.ey.advisory.app.get.notices.handlers;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class GstnNoticeReqDto {

	@Expose
	@SerializedName("entityId")
	public Long entityId;

	@Expose
	@SerializedName("gstins")
	public List<String> gstins;

	@Expose
	@SerializedName("fromTaxPeriod")
	public String fromTaxPeriod;

	@Expose
	@SerializedName("toTaxPeriod")
	public String toTaxPeriod;
	
	@Expose
	@SerializedName("fromDate")
	public String fromDate;

	@Expose
	@SerializedName("toDate")
	public String toDate;

	@Expose
	@SerializedName("selectionCriteria")
	public String selectionCriteria;
	
	@Expose
	@SerializedName("reportType")
	public String reportType;

	@Expose
	@SerializedName("fileId")
	private Long fileId;
	
	@SerializedName("pageNum")
	private int pageNum;
	
	@SerializedName("pageSize")
	private int pageSize;
	
}
