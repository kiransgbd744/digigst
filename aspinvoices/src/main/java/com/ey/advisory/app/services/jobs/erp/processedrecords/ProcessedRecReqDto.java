package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProcessedRecReqDto {

	@NonNull
	private Long entityID;
	
	@NonNull
	private List<String> taxPeriods;
	
}
