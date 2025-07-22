package com.ey.advisory.app.docs.dto.gstr9;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table18ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@SerializedName("items")
	@Expose
	private List<Gstr9Table18ItemsReqDto> gstr9Table18ItemsReqDtos;
}
