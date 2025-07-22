package com.ey.advisory.admin.azurebus.service;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
public class ITPEventDataRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String category;

	@Expose
	private String appType;

	@Expose
	private String tenenatid;
	
	@Expose
	private String sapUserId;
	
	@Expose
	private String itpUserId;
	
	@Expose
	private String requestId;
	
	@Expose
	@SerializedName("requestParams")
	private ITPEventRequestParamsDto requestParams;


}
