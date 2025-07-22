package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DrcGetRespDto {

	@SerializedName("drcDetails")
	@Expose
	private List<DrcGetCompSummaryDetails> respList;

	@SerializedName("errMsg")
	@Expose
	private String errMsg;
}
