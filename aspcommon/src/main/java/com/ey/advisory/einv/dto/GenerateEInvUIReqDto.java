package com.ey.advisory.einv.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GenerateEInvUIReqDto {
	
	@Expose
	@SerializedName("docIdList")
	private List<Long> docIdList;
	

}
