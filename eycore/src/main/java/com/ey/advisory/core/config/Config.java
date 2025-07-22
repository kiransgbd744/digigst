package com.ey.advisory.core.config;

import java.io.Serializable;

/**
 * An instance of this class represents a single configuration availabe to the
 * app that can otherwise be represented as a key/value pair in a property 
 * file.
 * 
 * @author Sai.Pakanati
 *
 */
public class Config implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected Long id;
	protected String category;
	protected String key;
	protected String value;
	protected String groupCode;
	protected ConfigType type;

	public Config(Long id, ConfigType type, String groupCode,
			String category, String key, String value) {
		super();
		this.groupCode = groupCode;
		this.type = type;
		this.id = id;
		this.category = category;
		this.key = key;
		this.value = value;
	}	
	
	public Config() {}

	public ConfigType getType() {
		return type;
	}

	public void setType(ConfigType type) {
		this.type = type;
	}


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

	@Override
	public String toString() {
		return "EYConfiguration [id=" + id + ", category=" + category + ", key="
				+ key + ", value=" + value + ", groupCode=" + groupCode
				+ ", type=" + type + "]";
	}
}
