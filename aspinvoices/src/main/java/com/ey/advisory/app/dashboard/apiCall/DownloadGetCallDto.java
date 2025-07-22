package com.ey.advisory.app.dashboard.apiCall;

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
public class DownloadGetCallDto {
	
	@Expose
	@SerializedName("gstin")
	List<String> gstinList = new ArrayList<>();
	
	@Expose
	@SerializedName("returnType")
	String returnType;
	
	@Expose
	@SerializedName("fy")
	String fy;

}
