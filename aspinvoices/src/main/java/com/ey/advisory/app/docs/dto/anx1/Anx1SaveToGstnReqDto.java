package com.ey.advisory.app.docs.dto.anx1;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
public class Anx1SaveToGstnReqDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("gstins")
	private List<String> gstins = new ArrayList<>();

	@Expose
	@SerializedName("retPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("view")
	private String view;

	@Expose
	@SerializedName("dateOfUploadToGstn")
	private String dateOfUploadToGstn;
	

	@Expose
	@SerializedName("groupcode")
	private String groupcode;
	
	//For User flexibility, it comes from Screen
	@Expose
	@SerializedName("tables")
	private List<String> tables = new ArrayList<>();
	
}
