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
public class InitiateGstr2bLinkingDto {
	
	@Expose
	@SerializedName("gstin")
	String gstin;
	
	@Expose
	@SerializedName("taxPeriod")
	List<String> taxPeriodList = new ArrayList<>();

}
