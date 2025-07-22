package com.ey.advisory.app.docs.dto.erp;

import java.util.Map;

import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr1ErpReviewSummaryDto {

	Gstr1SummaryDocSectionDto gstr1SummaryDoc;
	Gstr1SummaryNilSectionDto gstr1SummaryNil;
	Gstr1SummaryCDSectionDto gstr1SummaryHSN;
	
	Gstr1SummaryCDSectionDto gstr1SummaryAdvAdj;
	Gstr1SummaryCDSectionDto gstr1SummaryAdvAdjAme;
	Gstr1SummaryCDSectionDto gstr1SummaryAdvRec;
	Gstr1SummaryCDSectionDto gstr1SummaryAdvRecAme;
	
	Map<String,Gstr1SummaryCDSectionDto> gstr1SummaryOutward;
	
	
	
}
