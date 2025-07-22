package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class MessageDto {

	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("message")
	private String message;
}
