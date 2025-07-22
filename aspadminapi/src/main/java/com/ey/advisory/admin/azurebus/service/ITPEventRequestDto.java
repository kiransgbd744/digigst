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
public class ITPEventRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@SerializedName("data")
	@Expose
	private ITPEventDataRequestDto data;

}
