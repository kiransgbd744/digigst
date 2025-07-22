package com.ey.advisory.app.docs.dto.gstr2;

import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.docs.dto.DocErrorDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class is responsible for transferring document header, item and error
 * details response data from java server to UI
 * 
 * @author Mohana.Dasari
 *
 */
public class InwardDocumentDto extends InwardTransDocument{
	

	@Expose
	@SerializedName("isDuplicateCheck")
	private boolean isDuplicateCheck;
	

	@Expose
	@SerializedName("isLocked")
	private boolean isLocked;
	
	@Expose
	@SerializedName("lockedReason")
	private String lockedReason;
	
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

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getLockedReason() {
		return lockedReason;
	}

	public void setLockedReason(String lockedReason) {
		this.lockedReason = lockedReason;
	}
	public boolean isDuplicateCheck() {
		return isDuplicateCheck;
	}

	public void setDuplicateCheck(boolean isDuplicateCheck) {
		this.isDuplicateCheck = isDuplicateCheck;
	}	
}
