package com.ey.advisory.app.docs.dto.erp;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr1ReviewSummaryDto {

	Map<String,List<Gstr1ReviewSummaryTaxTypeDto>> gMap;
	
	
}
