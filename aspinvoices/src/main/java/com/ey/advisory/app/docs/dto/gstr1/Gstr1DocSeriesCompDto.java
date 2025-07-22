package com.ey.advisory.app.docs.dto.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr1DocSeriesCompDto {

	@Expose
	@SerializedName("entityId")
	private String entityId;

	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("completedOn")
	private String completedOn;

	@Expose
	@SerializedName("requestStatus")
	private String requestStatus;

	@Expose
	@SerializedName("identifier")
	private String identifier;

	@Expose
	@SerializedName("gstins")
	private List<String> gstins;

}
