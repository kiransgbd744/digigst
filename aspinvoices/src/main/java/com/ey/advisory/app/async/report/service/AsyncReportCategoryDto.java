package com.ey.advisory.app.async.report.service;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
@Data
public class AsyncReportCategoryDto {
	@Expose
	@SerializedName("repCateg")
	public String repCateg;

	@SerializedName("dataType")
	public List<String> dataType;
	
	public AsyncReportCategoryDto(String repCateg)
	{this.repCateg = repCateg;}
}
