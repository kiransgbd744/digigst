package com.ey.advisory.filter;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class X5CMainCeritificateDto {

	@Expose
	@SerializedName("keys")
	private List<X5CCeritificateDto> keys;

}
