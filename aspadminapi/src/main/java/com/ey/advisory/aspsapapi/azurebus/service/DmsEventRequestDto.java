package com.ey.advisory.aspsapapi.azurebus.service;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */
@Data
public class DmsEventRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("message")
	@Expose
	private String message;

	@SerializedName("status")
	@Expose
	private String status;

	@SerializedName("responseid")
	@Expose
	private String uuid;

	@SerializedName("filename")
	@Expose
	private String filename;

	@SerializedName("group_code")
	@Expose
	private String groupCode;

	@SerializedName("job_categ")
	@Expose
	private String jobCategory;

	@SerializedName("error")
	@Expose
	private List<DmsEventErrorRequestDto> error;

}
