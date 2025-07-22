package com.ey.advisory.app.docs.dto.gstr2x;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class Gstr2XErrorReport {
	
	@Expose
	@SerializedName("tds")
	private List<Gstr2XTdsDto> tds;
	
	@Expose
	@SerializedName("tdsa")
	private List<Gstr2XTdsaDto> tdsa;
	
	@Expose
	@SerializedName("tcs")
	private List<Gstr2XTcsDto> tcs;
	
	@Expose
	@SerializedName("tcsa")
	private List<Gstr2XTcsaDto> tcsa;
}
