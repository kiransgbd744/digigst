/**
 * 
 */
package com.ey.advisory.app.service.reconresponse;

import com.ey.advisory.app.anx2.initiaterecon.Anx2InitiateReconReportDto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@Setter
public class ErrorResponseDto extends Anx2InitiateReconReportDto{
	
	private String errorMessage;

}
