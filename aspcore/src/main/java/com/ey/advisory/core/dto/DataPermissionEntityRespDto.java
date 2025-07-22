package com.ey.advisory.core.dto;

import java.util.List;

import lombok.Data;

/**
 * @author Umesha.M
 *
 */
@Data
public class DataPermissionEntityRespDto {

	private String groupCode;

	private Long entityId;

	private String entityName;
	
	private List<Long> userIds;
	

}
