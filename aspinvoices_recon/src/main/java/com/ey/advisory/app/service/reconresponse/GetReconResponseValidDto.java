/**
 * 
 */
package com.ey.advisory.app.service.reconresponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Arun KA
 *
 */

@Getter
@Setter
@ToString
public class GetReconResponseValidDto {
	
	private String userResponse;
	private String reportType;
	private String taxPeriod;
	private Long requestId;
	private String idPR;
	private String idA2;
	private String invoiceKeyPR;
	private String invoiceKeyA2;
	private String gstin;
	private String errMSg;

}
