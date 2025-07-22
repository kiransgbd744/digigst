package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class AmdhistDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("portcd")
	private String portcd;

	@Expose
	@SerializedName("benum")
	private Long benum;

	@Expose
	@SerializedName("bedt")
	private String bedt;

	@Expose
	@SerializedName("amddtl")
	private List<AmdItemDto> inv;

}
