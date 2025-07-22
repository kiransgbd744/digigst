package com.ey.advisory.app.get.notices.handlers;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Data
public class GstNoticesReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("apiSection")
	private String apiSection;

	@Expose
	@SerializedName("groupCode")
	private String groupcode;

	@Expose
	@SerializedName("batchId")
	private Long batchId;

	@Expose
	@SerializedName("isFailed")
	private Boolean isFailed;

	@Expose
	private Long userRequestId;
	
	@Expose
	private String date;
	
	@Expose
	@SerializedName("criteria")
	private String criteria;

	@Expose
	@SerializedName("gstins")
	private List<String> gstins = Lists.newArrayList();
	
	@Expose
	@SerializedName("type")
	private String type;

}
