package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AutoRecon2APRReqList {

	@Expose
	@SerializedName("Particulars")
	public String particulars;

	@Expose
	@SerializedName("Level")
	public int level;

	@Expose
	@SerializedName("categories")
	public List<AutoRecon2APRCategoriesDto> categories;

	@Expose
	@SerializedName("ReportType")
	public String reportType;

	@Expose
	@SerializedName("AutoLock")
	public String autoLock;

	@Expose
	@SerializedName("ERPReportType")
	public String eRPReportType;

	@Expose
	@SerializedName("ApporvalStatus")
	public String apporvalStatus;

	@Expose
	@SerializedName("ReverseFeed")
	public String reverseFeed;
	
	@Expose
	@SerializedName("ImsActionAllowed")
	public String imsActionAllowed;
	
	@Expose
	@SerializedName("ImsActionBlocked")
	public String imsActionBlocked;

}
