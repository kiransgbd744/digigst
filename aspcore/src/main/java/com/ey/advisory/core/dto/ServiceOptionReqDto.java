package com.ey.advisory.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ServiceOptionReqDto {

	private Long id;

	private String groupCode;

	private Long entityId;

	private String gstin;

	private String plant;

	private String ewb;

	private String einv;

}
