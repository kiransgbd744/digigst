package com.ey.advisory.admin.azurebus.service;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
public class ITPEventEntityResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String groupCode;

	@Expose
	private String groupName;

	@Expose
	private String entityCode;

	@Expose
	private Long entityId;

	@Expose
	private String entityName;

	@Expose
	private String entityPan;

}
