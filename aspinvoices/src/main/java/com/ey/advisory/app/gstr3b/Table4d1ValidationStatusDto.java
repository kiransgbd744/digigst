package com.ey.advisory.app.gstr3b;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author akhilesh.yadav
 *
 */

@Setter
@Getter
public class Table4d1ValidationStatusDto implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Expose
	protected String gstin;

	@Expose
	protected String taxPeriod;

	@Expose
	protected Boolean validationFlag = false;

	@Expose
	protected Boolean errorFlag = false;

	@Expose
	protected Boolean isActive = false;
	
	public Table4d1ValidationStatusDto() {
		super();
	}

	public Table4d1ValidationStatusDto(String gstin, String taxPeriod,
			Boolean validationFlag, Boolean errorFlag, Boolean isActive) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.validationFlag = validationFlag;
		this.errorFlag = errorFlag;
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Table4d1ValidationStatusDto [gstin=" + gstin + ", taxPeriod="
				+ taxPeriod + ", validationFlag=" + validationFlag
				+ ", errorFlag=" + errorFlag + ", isActive=" + isActive + "]";
	}
	
	

}
