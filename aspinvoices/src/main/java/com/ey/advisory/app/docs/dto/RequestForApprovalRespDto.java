/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author Balakrishna.S
 *
 */
@Data
public class RequestForApprovalRespDto {

	private String reqFlag;
	private String wfStatus;
	private String saveFlag;
	private String gstin;
	private String taxPeriod;
	private String status;
	private String timeStamp;

	
}
