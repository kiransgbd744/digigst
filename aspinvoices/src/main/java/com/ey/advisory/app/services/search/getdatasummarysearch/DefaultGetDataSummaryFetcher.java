package com.ey.advisory.app.services.search.getdatasummarysearch;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.BasicGetDataSummaryDao;
import com.ey.advisory.app.docs.dto.GetDataSummaryResDto;

@Component("DefaultGetDataSummaryFetcher")
public class DefaultGetDataSummaryFetcher implements GetDataSummaryFetcher {
	
	@Autowired
	@Qualifier("GetDataSummaryDaoImpl")
	private BasicGetDataSummaryDao basicGetDataSummaryDao;

	public List<GetDataSummaryResDto> findGetDataSummary(String criteria,
			List<String> selectedSgtins, List<LocalDate> selectedDates,
			LocalDate docDataFrom, LocalDate docDataTo, LocalDate docRecFrom,
			LocalDate docRecTo, int derRetPeriodFrom, int derRetPeriodTo,
			List<Long> entityId) {
		
		List<GetDataSummaryResDto> result =
				basicGetDataSummaryDao.loadGetDataSummarySection(criteria, 
						selectedSgtins, selectedDates, docDataFrom,docDataTo,
						docRecFrom,docRecTo,derRetPeriodFrom,
						derRetPeriodTo,entityId);	

		return result;
	}

		
}