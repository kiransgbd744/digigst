package com.ey.advisory.app.service.ims;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@Data
public class ImsSaveToGstnReqDto {
	
	@Expose
	@SerializedName("gstins") 
	private List<String> gstins = new ArrayList<>();

	@Expose
	@SerializedName("tableType") 
	private List<String> tableType = new ArrayList<>();
	
}
