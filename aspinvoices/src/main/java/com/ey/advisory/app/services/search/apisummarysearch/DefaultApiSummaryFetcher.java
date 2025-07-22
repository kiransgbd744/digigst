package com.ey.advisory.app.services.search.apisummarysearch;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.BasicApiSummaryDao;
import com.ey.advisory.app.docs.dto.ApiSummaryResDto;

@Component("DefaultApiSummaryFetcher")
public class DefaultApiSummaryFetcher implements ApiSummaryFecther {
	
	@Autowired
	@Qualifier("ApiSummaryDaoImpl")
	private BasicApiSummaryDao basicApiSummaryDao;

	@Override
	public List<ApiSummaryResDto> findApiSummary(String criteria,
			List<String> selectedSgtins, List<LocalDate> selectedDates,
			LocalDate docDataFrom, LocalDate docDataTo, LocalDate docRecFrom,
			LocalDate docRecTo, int derRetPeriodFrom, int derRetPeriodTo,
			List<Long> entityId) {
		
		List<ApiSummaryResDto> result =
				basicApiSummaryDao.loadApiSummarySection(criteria, 
						selectedSgtins, selectedDates, docDataFrom,docDataTo,
						docRecFrom,docRecTo,derRetPeriodFrom,
						derRetPeriodTo,entityId);	

		return result;
	}

		
}
