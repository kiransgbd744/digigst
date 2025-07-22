package com.ey.advisory.app.docs.dto.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Data
public class Gstr6SaveStatusDownloadReqDto {
	@Expose
	@SerializedName("status")
	private String status;
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	@Expose
	@SerializedName("gstin")
	private String gstin;
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	@Expose
	@SerializedName("gstr6aSections")
	private List<String> gstr6aSections;

	@Expose
	@SerializedName("gstr6Sections")
	private List<String> gstr6Sections;
	@Expose
	@SerializedName("gstr7aSections")
	private List<String> gstr7aSections;
}
