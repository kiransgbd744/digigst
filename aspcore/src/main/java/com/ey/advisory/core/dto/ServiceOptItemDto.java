package com.ey.advisory.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServiceOptItemDto {

	private Long id;

	private String gstin;

	private String plant;

	private Integer ewb;

	private Integer einv;

}
