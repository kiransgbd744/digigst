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
public class ErpPermissionDeleteDto {

	private Long entityId;
	private List<Long> gstnsId;
	private Long gstnId;
	private Long erpId;
	private Long scenarioId;
	private String jobType;
	private String destName;
	private String endPointURI;

}
