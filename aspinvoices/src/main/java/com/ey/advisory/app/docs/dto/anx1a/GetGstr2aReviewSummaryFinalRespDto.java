package com.ey.advisory.app.docs.dto.anx1a;

import java.util.List;

import lombok.Data;

@Data
public class GetGstr2aReviewSummaryFinalRespDto {

	private List<GetGstr2aReviewSummaryRespDto> b2b;
	private List<GetGstr2aReviewSummaryRespDto> b2ba;
	private List<GetGstr2aReviewSummaryRespDto> cdn;
	private List<GetGstr2aReviewSummaryRespDto> cdna;
	private List<GetGstr2aReviewSummaryRespDto> isd;
	private List<GetGstr2aReviewSummaryRespDto> isda;
	private List<GetGstr2aReviewSummaryRespDto> impg;
	private List<GetGstr2aReviewSummaryRespDto> impgsez;
	private List<GetGstr2aReviewSummaryRespDto> amdImpg;
	private List<GetGstr2aReviewSummaryRespDto> ecom;
	private List<GetGstr2aReviewSummaryRespDto> ecoma;

}
