/**
 * 
 */
package com.ey.advisory.app.anx1.recipientsummary;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Khalid1.Khan
 *
 */
@Setter
@Getter
public class RecipientSummaryFilterDto {

	@Expose
	private String gstin;

	@Expose
	private List<CgstinSgstinDto> gstinSgstinPan;

	public RecipientSummaryFilterDto(String gstin,
			List<CgstinSgstinDto> gstinSgstinPan) {
		super();
		this.gstin = gstin;
		this.gstinSgstinPan = gstinSgstinPan;
	}
	
	public RecipientSummaryFilterDto() {}
	
	

	
}
