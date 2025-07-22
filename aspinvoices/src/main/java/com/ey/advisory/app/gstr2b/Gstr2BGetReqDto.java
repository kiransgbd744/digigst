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
 * @author vishal.verma
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2BGetReqDto {

	private List<String> gstins;

	private List<String> taxPeriod;

	private String fileNumber;
	
	private boolean isAutoEligible;
	
	@Expose
	@SerializedName("returnType")
	String returnType;
	
	@Expose
	@SerializedName("fy")
	String fy;

}
