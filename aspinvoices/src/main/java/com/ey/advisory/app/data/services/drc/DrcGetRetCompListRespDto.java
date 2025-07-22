package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DrcGetRetCompListRespDto {

	@SerializedName("refidlist")
	@Expose
	private List<DrcGetCompSummaryDetails> refidlist;

	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("frmtyp")
	@Expose
	private String frmtyp;
}
