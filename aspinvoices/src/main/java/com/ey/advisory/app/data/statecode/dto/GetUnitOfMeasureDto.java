package com.ey.advisory.app.data.statecode.dto;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class GetUnitOfMeasureDto {


private static final long serialVersionUID = 1L;

  
	
	private String uom;
	
	private String uomDesc;

	@Override
	public String toString() {
		return "GetUnitOfMeasureDto [uom=" + uom + ", uomDesc=" + uomDesc + "]";
	}
	
	
    /*	@Override
	public String toString() {
		return "StateCodeDetailsDto [stateCode=" + stateCode + ", stateName="
				+ stateName + "]";
	}
	*/
	
	
	
}
