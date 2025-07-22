package com.ey.advisory.common;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EwbStatusInputDto {
	@Expose
	@SerializedName(value = "entity", alternate = "entityId")
	private Long entityId;

	@Expose
	@SerializedName("gstins")
	private List<String> gstins;

	@Expose
	@SerializedName("fromdate")
	private String fromdate;

	@Expose
	@SerializedName("toDate")
	private String toDate;

}
