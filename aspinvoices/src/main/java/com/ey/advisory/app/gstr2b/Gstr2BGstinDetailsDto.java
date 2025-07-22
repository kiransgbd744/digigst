package com.ey.advisory.app.gstr2b;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
*
* @author Hema G M
*
*/
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2BGstinDetailsDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("gstr2bTaxPeriodDetails")
	private List<Gstr2BTaxPeriodDetailsDto> taxPeriodDetails;
	
}

