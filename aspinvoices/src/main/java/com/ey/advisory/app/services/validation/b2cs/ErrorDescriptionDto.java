package com.ey.advisory.app.services.validation.b2cs;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class ErrorDescriptionDto {

	 private String errorType;
	 private String errorCode;
	 private String errorField;
	 private String errorDesc;
	
}
