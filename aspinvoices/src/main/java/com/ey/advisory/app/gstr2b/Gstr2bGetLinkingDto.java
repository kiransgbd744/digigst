package com.ey.advisory.app.gstr2b;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Gstr2bGetLinkingDto {
	
	@Expose
	@SerializedName("req")
	List<InitiateGstr2bLinkingDto> gstinTaxPeriodList = new ArrayList<>();
	
	@Expose
	@SerializedName("fy")
	String fy;

}
