package com.ey.advisory.admin.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Umesha.M
 *
 */
@Entity
@Table(name = "CONFG_PRMT")
public class ConfigPrmtEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	
	@Column(name = "PARAMTR_CATEGORY")
	private String paramtrCategory;
	
	@Column(name = "PARAMTR_SUB_CATEGORY")
	private String paramtrSubCategory;
	
	@Column(name = "PARAMTR_KEY_ID")
	private String paramtrKeyId;

	
	@Column(name = "PARAMTR_KEY")
	private String paramtrKey;

	
	@Column(name = "PARAMTR_VALUE")
	private String paramtrValue;

	
	@Column(name = "PARAMTR_VALUE_DESC")
	private String paramtrValuDesc;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	
	public String getParamtrCategory() {
		return paramtrCategory;
	}

	public void setParamtrCategory(String paramtrCategory) {
		this.paramtrCategory = paramtrCategory;
	}

	public String getParamtrSubCategory() {
		return paramtrSubCategory;
	}

	public void setParamtrSubCategory(String paramtrSubCategory) {
		this.paramtrSubCategory = paramtrSubCategory;
	}

	public String getParamtrKeyId() {
		return paramtrKeyId;
	}

	public void setParamtrKeyId(String paramtrKeyId) {
		this.paramtrKeyId = paramtrKeyId;
	}

	public String getParamtrKey() {
		return paramtrKey;
	}

	public void setParamtrKey(String paramtrKey) {
		this.paramtrKey = paramtrKey;
	}

	public String getParamtrValue() {
		return paramtrValue;
	}

	public void setParamtrValue(String paramtrValue) {
		this.paramtrValue = paramtrValue;
	}

	public String getParamtrValuDesc() {
		return paramtrValuDesc;
	}

	public void setParamtrValuDesc(String paramtrValuDesc) {
		this.paramtrValuDesc = paramtrValuDesc;
	}

	@Override
	public String toString() {
		return "ConfigPermitEntity [id=" + id + ", paramtrCategory="
		        + paramtrCategory + ", paramtrSubCategory=" + paramtrSubCategory
		        + ", paramtrKeyId=" + paramtrKeyId + ", paramtrKey="
		        + paramtrKey + ", paramtrValue=" + paramtrValue
		        + ", paramtrValuDesc=" + paramtrValuDesc + "]";
	}

}