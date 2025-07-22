package com.ey.advisory.core.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ServiceOptionDto {

	private String groupCode;

	private boolean ewbFlag;

	private boolean einvFlag;

	private Long entityId;

	private String entityName;

	private List<GstinDto> gstinDto;

	private List<GstinDto> plant;

	private List<GstinDto> ewb;

	private List<GstinDto> einv;

	private List<ServiceOptItemDto> items;

}
