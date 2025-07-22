package com.ey.advisory.app.data.daos.client;

import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.app.docs.dto.ApiSummaryResDto;

public interface BasicApiSummaryDao {

	List<ApiSummaryResDto> loadApiSummarySection(String criteria,
			List<String> selectedSgtins, List<LocalDate> selectedDates,
			LocalDate docDataFrom, LocalDate docDataTo, LocalDate docRecFrom,
			LocalDate docRecTo, int derRetPeriodFrom, int derRetPeriodTo,
			List<Long> entityId);

	
}
