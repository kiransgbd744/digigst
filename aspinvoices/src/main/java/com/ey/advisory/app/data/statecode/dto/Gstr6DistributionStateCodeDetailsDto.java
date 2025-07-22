package com.ey.advisory.app.data.statecode.dto;

import java.io.Serializable;

import lombok.Data;
/**
 * 
 * @author Dibyakanta.s
 *
 */
@Data
public class Gstr6DistributionStateCodeDetailsDto implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

  
	
	private String stateCode;
	
	private String stateName;
	
	
    	@Override
	public String toString() {
		return "StateCodeDetailsDto [stateCode=" + stateCode + ", stateName="
				+ stateName + "]";
	}
}
