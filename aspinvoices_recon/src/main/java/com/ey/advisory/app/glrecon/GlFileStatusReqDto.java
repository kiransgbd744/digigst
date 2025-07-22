package com.ey.advisory.app.glrecon;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GlFileStatusReqDto {

	@SerializedName("fileType")
	private List<String> fileType = new ArrayList<>();
	
	@SerializedName("status")
	private List<String> status= new ArrayList<>();
	
	@SerializedName("dataRecvFrom")
	private String dataRecvFrom;
	
	@SerializedName("dataRecvTo")
	private String dataRecvTo;
	
	@SerializedName("entityId")
	private Long entityId;
	
	@SerializedName("flag")
	private Boolean flag;
	
}
