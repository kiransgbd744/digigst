package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveToGstnSectionWiseReqDto {
	
	@Expose
	private String gstin;
	
	@Expose
	private String returnPeriod;
	
	@Expose
	private Long userRequestId;
	
	@Expose
	private List<String> sections;
}
