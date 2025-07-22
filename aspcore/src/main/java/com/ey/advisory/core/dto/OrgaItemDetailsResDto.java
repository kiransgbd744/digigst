package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class OrgaItemDetailsResDto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("isApplicable")
	private boolean isApplicable;
	
	@Expose
	@SerializedName("attCode")
	private String attCode;
	
	@Expose
	@SerializedName("attName")
	private String attName;
	
	@Expose
	@SerializedName("outword")
	private String outword;
	
	@Expose
	@SerializedName("inword")
	private String inword;

	@Expose
	@SerializedName("isActive")
	private boolean isActive;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the isApplicable
	 */
	public boolean isApplicable() {
		return isApplicable;
	}

	/**
	 * @param isApplicable the isApplicable to set
	 */
	public void setApplicable(boolean isApplicable) {
		this.isApplicable = isApplicable;
	}

	/**
	 * @return the attCode
	 */
	public String getAttCode() {
		return attCode;
	}

	/**
	 * @param attCode the attCode to set
	 */
	public void setAttCode(String attCode) {
		this.attCode = attCode;
	}

	/**
	 * @return the attName
	 */
	public String getAttName() {
		return attName;
	}

	/**
	 * @param attName the attName to set
	 */
	public void setAttName(String attName) {
		this.attName = attName;
	}

	
	public boolean isActive() {
	    return isActive;
	}

	public void setActive(boolean isActive) {
	    this.isActive = isActive;
	}

	/**
	 * @return the outword
	 */
	public String getOutword() {
		return outword;
	}

	/**
	 * @param outword the outword to set
	 */
	public void setOutword(String outword) {
		this.outword = outword;
	}

	/**
	 * @return the inword
	 */
	public String getInword() {
		return inword;
	}

	/**
	 * @param inword the inword to set
	 */
	public void setInword(String inword) {
		this.inword = inword;
	}
}
