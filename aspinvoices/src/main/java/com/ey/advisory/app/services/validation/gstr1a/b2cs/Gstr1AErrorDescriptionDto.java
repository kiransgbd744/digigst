package com.ey.advisory.app.services.validation.gstr1a.b2cs;

import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Data
public class Gstr1AErrorDescriptionDto {

	 private String errorType;
	 private String errorCode;
	 private String errorField;
	 private String errorDesc;
	
}
