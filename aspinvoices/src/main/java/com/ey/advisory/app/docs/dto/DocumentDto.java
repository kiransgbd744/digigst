package com.ey.advisory.app.docs.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * DocErrorDetailDto class is responsible for transferring document header
 * details response data from server to UI
 *
 * @author Mohana.Dasari
 */
@Component
public class DocumentDto extends OutwardTransDocument {
	
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
