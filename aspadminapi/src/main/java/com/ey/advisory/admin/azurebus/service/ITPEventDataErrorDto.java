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
public class ITPEventDataErrorDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("error_cd")
	private String errorCode;

	@Expose
	@SerializedName("msg")
	private String msg;

}
