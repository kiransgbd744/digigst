
package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr6GetSummaryData {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("ret_period")
	private String returnPeriod;

	@Expose
	@SerializedName("chksum")
	private String checksum;
	
	@Expose
	@SerializedName("itcDetails")
	private TtcDetails itcDetails;
	
	@Expose
	@SerializedName("lateFeemain")
	private LateFeemain lateFeemain;
	
	@Expose
	@SerializedName("section_summary")
	private List<SectionSummary> sectionSummary;

	}