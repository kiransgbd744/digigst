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
public class GetFyCallDto {
	
	@Expose
	@SerializedName("gstins")
	List<String> gstins = new ArrayList<>();
	
	@Expose
	@SerializedName("returnType")
	String returnType;
	
	@Expose
	@SerializedName("fy")
	String fy;

}
