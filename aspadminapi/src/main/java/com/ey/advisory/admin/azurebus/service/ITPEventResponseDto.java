package com.ey.advisory.admin.azurebus.service;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
public class ITPEventResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String category;

	@Expose
	private String appType;
	
	@Expose
	private String requestId;

	@Expose
	@SerializedName("data")
	private List<ITPEventCommonResponseDto> data;

	@Expose
	@SerializedName("error")
	private List<ITPEventDataErrorDto> error;

}
