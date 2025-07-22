/**
 * 
 */
package com.ey.advisory.core.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Umesh
 *
 */
@Getter
@Setter
public class ErpScenarioInfoSftpDto {

	private List<ErpSPDetailsDto> erpDetails;
	private List<ErpScenarioDetailsDto> scenario;
	private List<ErpScenarioSftpItmDetailsDto> items;

}
