package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class is responsible for sending Structurally Validated Error Documents
 * to UI
 * 
 * @author Mohana.Dasari
 *
 */
public class OutwardSvErrDocDto extends Anx1OutWardErrHeader {

	@Expose
	@SerializedName("errorList")
	private List<DocErrorDto> errors;

	public List<DocErrorDto> getErrors() {
		return errors;
	}

	public void setErrors(List<DocErrorDto> errors) {
		this.errors = errors;
	}
}
