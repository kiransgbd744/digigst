package com.ey.advisory.services.gstr3b.entity.setoff;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3BSetRule86BSaveDto {
	
	private String gstin;
	
	private String taxPeriod;
	
	private boolean isRule86B;

}
