/**
 * 
 */
package com.ey.advisory.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Laxmi.Salukuti
 *
 */
@Getter
@Setter
public class ErpScenarioSftpItmDetailsDto {

	private Long erpId;
	private Long scenarioId;
	private String jobFrequency;
	private String jobTypeDesc;
	private String endPointURI;
}
