package com.ey.advisory.app.recipientmaster.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh N K
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class RecipientGstinDto {
	
	@Expose
	@SerializedName("recipientGstin")
	private String recipientGstin;

	@Expose
	@SerializedName(" recipientName")
	private String  recipientName;

	@Expose
	@SerializedName(" recipientPan")
	private String  recipientPan;

	@Expose
	@SerializedName(" recipientCode")
	private String  recipientCode;

}
