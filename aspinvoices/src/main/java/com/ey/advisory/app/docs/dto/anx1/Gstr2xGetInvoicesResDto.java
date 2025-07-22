package com.ey.advisory.app.docs.dto.anx1;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr2xGetInvoicesResDto {

	@Expose
	@SerializedName("req_time")
	private String reqTime;

	@Expose
	@SerializedName("tds")
	private List<Gstr2xTdsAndTdsaResDto> tds = new ArrayList<>();

	@Expose
	@SerializedName("tdsa")
	private List<Gstr2xTdsAndTdsaResDto> tdsa = new ArrayList<>();

	@Expose
	@SerializedName("tcs")
	private List<Gstr2xTcsAndTcsaResDto> tcs = new ArrayList<>();

	@Expose
	@SerializedName("tcsa")
	private List<Gstr2xTcsAndTcsaResDto> tcsa = new ArrayList<>();
	
	@Expose
	@SerializedName("summary")
	private Gstr2xTcdsSummaryResDto summary = new Gstr2xTcdsSummaryResDto();

}
