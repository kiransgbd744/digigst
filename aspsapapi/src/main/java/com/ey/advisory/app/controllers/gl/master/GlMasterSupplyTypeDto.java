/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import lombok.Data;

@Data
public class GlMasterSupplyTypeDto {
	private String errorCode;
	private String errorDescription;
	private String supplyTypeReg;
	private String supplyTypeMs;
	private Long id;

}
