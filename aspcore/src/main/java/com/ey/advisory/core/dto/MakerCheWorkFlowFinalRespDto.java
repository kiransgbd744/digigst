package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class MakerCheWorkFlowFinalRespDto {

	@Expose
	List<String> gstins;

	@Expose
	List<MakerCheWorkFlowRespDto> mkrCkrRespDtos;

}
