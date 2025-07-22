package com.ey.advisory.core.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DataSecurityDto {

	private List<DataSecuriDto> gstin;
	private List<DataSecuriDto> profitCenter;
	private List<DataSecuriDto> profitCenter2;
	private List<DataSecuriDto> plant;
	private List<DataSecuriDto> division;
	private List<DataSecuriDto> subDivision;
	private List<DataSecuriDto> location;
	private List<DataSecuriDto> salesOrg;
	private List<DataSecuriDto> purchOrg;
	private List<DataSecuriDto> distChannel;
	private List<DataSecuriDto> userAccess1;
	private List<DataSecuriDto> userAccess2;
	private List<DataSecuriDto> userAccess3;
	private List<DataSecuriDto> userAccess4;
	private List<DataSecuriDto> userAccess5;
	private List<DataSecuriDto> userAccess6;
	private List<DataSecuriDto> sourceId;

	private ItemsDto items;
	
	private ItemsDto inwarditems;
}
