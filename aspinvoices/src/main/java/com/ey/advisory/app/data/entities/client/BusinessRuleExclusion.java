package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.stereotype.Component;

import com.google.gson.annotations.Expose;

/**
 * This entity class is responsible for storing Rule Items in the database
 * 
 * @author Murali.Singanamala
 *
 * @param <T>
 */

@Entity
@Table(name = "SALES_RULE_EXCLUSIONS")
public class BusinessRuleExclusion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "GROUP_CODE", length = 50)
	private String groupCode;

	@Expose
	@Column(name = "RULE_NAME", length = 50)
	private String ruleName;

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

}
