package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class Gstr2AVsGstr3bProcessSummaryReqDto {

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();

	@Expose
	@SerializedName("fromtaxPeriod")
	private String fromtaxPeriod;

	@Expose
	@SerializedName("totaxPeriod")
	private String totaxPeriod;

	@Expose
    @SerializedName("type")
    private String type;

	
	@Expose
	@SerializedName("gstin")
	private List<String> gstin = new ArrayList<>();
}
