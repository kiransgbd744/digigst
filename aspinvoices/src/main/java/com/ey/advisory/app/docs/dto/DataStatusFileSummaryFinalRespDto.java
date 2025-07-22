package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DataStatusFileSummaryFinalRespDto {

	@Expose
	@SerializedName("fileName")
	private String fileName;
	
	@Expose
	@SerializedName("items")
	private List<DataStatusFileSummaryFirstRespDto> items = new ArrayList<>();

}
