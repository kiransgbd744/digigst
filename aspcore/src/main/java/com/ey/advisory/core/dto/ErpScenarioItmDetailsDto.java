/**
 * 
 */
package com.ey.advisory.core.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Laxmi.Salukuti
 *
 */
@Getter
@Setter
public class ErpScenarioItmDetailsDto {

	private Integer erpId;
	private Integer scenarioId;
	private String gstinId;
	private String destName;
	private String jobFrequency;
	private List<String> gstnItemList;
	private String startRootTag;
	private String endRootTag;
	private String jobTypeDesc;
	private String endPointURI;
	private String companyCode;
}
