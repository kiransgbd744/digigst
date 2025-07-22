package com.ey.advisory.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ErpEntityInfoSaveDto {

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
