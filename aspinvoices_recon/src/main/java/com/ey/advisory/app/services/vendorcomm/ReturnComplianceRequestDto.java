/**
 * 
 */
package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import com.ey.advisory.app.vendorcomm.dto.GstinDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Setter
@Getter
@ToString
public class ReturnComplianceRequestDto {

	private Long requestId;
	private Long noOfGstins;
	private String createdOn;
	private String status;
	private long totalEmails;
	private long sentEmails;
	private String financialYear;
	private List<GstinDto> clientGstins;
}