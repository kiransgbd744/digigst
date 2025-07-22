package com.ey.advisory.core.async.domain.master;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * This entity class represents configurations that are not security-sensitive.
 * A non-encrypted version of these configurations can be stored here. For
 * sensitive configurations, an encrypted version will be stored.
 * 
 * @author Sai.Pakanati
 *
 */
@Entity
@Table(name="EY_CONFIG")
public class EYRegularConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID") 
	protected Long id;
	
	@Column(name = "GROUP_CODE") 
	protected String groupCode;
	
	@Column(name = "CONF_CATEG") 
	protected String category;
	
	@Column(name = "CONF_KEY") 
	protected String key;
	
	@Column(name = "CONF_VALUE") 
	protected String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

}
