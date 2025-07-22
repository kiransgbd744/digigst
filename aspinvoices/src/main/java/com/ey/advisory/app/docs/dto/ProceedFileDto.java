package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class ProceedFileDto {
	
	@Expose
	private String gstin;
	
	@Expose
	private String ret_period;
}
