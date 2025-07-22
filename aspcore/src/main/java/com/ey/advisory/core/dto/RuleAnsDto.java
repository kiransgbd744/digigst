package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * 
 * @author ashutosh.kar
 *
 */
@Setter
@Getter
@ToString
public class RuleAnsDto {
	
	@Expose
	@SerializedName("ruleNo")
	private Long ruleNo;

	@Expose
	@SerializedName("ruleName")
	private String ruleName;
}
