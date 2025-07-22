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
@Setter
@Getter
public class ErpPermissionSaveDto {

	private Long entityId;
	private List<Long> gstnsId;
	private Long gstnId;
	private Long erpId;
	private Long scenarioId;
	private String destName;
	private String jobFrequency;

	private String startRootTag;
	private String endRootTag;
	private Integer frequency;

	private String endPointURI;

	private String dataType;
	private String jobType;
	private String companyCode;
}
