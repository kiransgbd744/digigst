package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EntityConfigPrmtReqDto {

	@Expose
	@SerializedName("groupCode")
	private String groupCode;

	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("type")
	private String type;
	
	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("questId")
	private Long questId;
	
	@Expose
	@SerializedName("answerCode")
	private String answerCode;
	
	@Expose
	@SerializedName("keyType")
	private String keyType;
	
	@Expose
	@SerializedName("userName")
	private String userName;
	
	@Expose
	@SerializedName("password")
	private String password;
	
	
}
