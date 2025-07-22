package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class is responsible for sending Structurally Validated Error Documents
 * to UI
 * 
 * @author Mohana.Dasari
 *
 */
public class InwardSvErrDocDto extends Anx2InwardErrorHeaderEntity{

	@Expose
	@SerializedName("errorList")
	private List<DocErrorDto> errors;

	/**
	 * @return the errors
	 */
	public List<DocErrorDto> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<DocErrorDto> errors) {
		this.errors = errors;
	}
}
