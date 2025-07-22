package com.ey.advisory.core.dto;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class ErpEntityInfoDto {

	private Long id;
	private String systemId;
	private String hostName;
	private String userName;
	private String password;
	private boolean status;
	private String port;
	private String portocal;
	private String sourceType;
}