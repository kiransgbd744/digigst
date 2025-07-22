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
public class ErpScenarioInfoDto {

	private Long entityId;
	private String entityName;
	private List<ErpGstinDetailsDto> gstinDetail;
	private List<ErpSPDetailsDto> erpDetails;
	private List<ErpScenarioDetailsDto> scenario;
	private List<ErpScenarioItmDetailsDto> items;

}
