package com.ey.advisory.app.common;

import com.google.gson.annotations.Expose;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */
@Data
@ToString
public class GstinReqTypDto {

	@Expose
	private String gstin;

	@Expose
	private String regType;

	public GstinReqTypDto(String gstin, String regType)
	{
		this.gstin = gstin;
		this.regType = regType;
	}
	
}
