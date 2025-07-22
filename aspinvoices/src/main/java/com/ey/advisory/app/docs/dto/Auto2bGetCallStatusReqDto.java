package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Auto2bGetCallStatusReqDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("fy")
	private String fy;

	public Auto2bGetCallStatusReqDto(){
		
	}

	
}
