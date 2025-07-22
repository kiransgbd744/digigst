package com.ey.advisory.app.services.search.getdatasummarysearch;

import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.app.docs.dto.GetDataSummaryResDto;

public interface GetDataSummaryFetcher {

	public List<GetDataSummaryResDto> findGetDataSummary(String criteria,
			List<String> selectedSgtins, List<LocalDate> selectedDates,
			LocalDate docDateFrom, LocalDate docDateTo, LocalDate recvDateFrom,
			LocalDate recvDateTo, int derRetPeriodFrom, int derRetPeriodTo,
			List<Long> entityId);
}
