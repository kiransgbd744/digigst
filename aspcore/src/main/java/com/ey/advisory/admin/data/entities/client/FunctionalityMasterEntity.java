package com.ey.advisory.admin.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FUNCTIONALITY_MASTER")
public class FunctionalityMasterEntity {

	@Id
	@Column(name = "FUNCTIONALITY_CODE")
	private String functCode;
	
	@Column(name = "FUNCTIONALITY_DESC")
	private String functDesc;

	/**
	 * @return the functCode
	 */
	public String getFunctCode() {
		return functCode;
	}

	/**
	 * @param functCode the functCode to set
	 */
	public void setFunctCode(String functCode) {
		this.functCode = functCode;
	}

	/**
	 * @return the functDesc
	 */
	public String getFunctDesc() {
		return functDesc;
	}

	/**
	 * @param functDesc the functDesc to set
	 */
	public void setFunctDesc(String functDesc) {
		this.functDesc = functDesc;
	}
	
	
}
