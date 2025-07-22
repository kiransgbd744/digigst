package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EntityConfigPrmtResDto {

	@Expose
	@SerializedName("quesCode")
	private String questionCode;
	
	@Expose
	@SerializedName("ques")
	private String question;
	
	@Expose
	@SerializedName("selected")
	private Integer selected;
	
	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("questId")
	private Long questId;
	
	@Expose
	@SerializedName("sequenceId")
	private Long sequenceId;
	
	@Expose
	@SerializedName("items")
	private List<ItemsAnsDto> list;

	@Expose
	@SerializedName("keyType")
	private String keyType;
	
	@Expose
	@SerializedName("answerDesc")
	private String answerDesc;
	
	@Expose
	@SerializedName("get2AHour")
	private String get2AHour;
	
	@Expose
	@SerializedName("get2Amin")
	private String get2Amin;
	
	@Expose
	@SerializedName("rules")
	private List<RuleAnsDto> rules;
	
}
