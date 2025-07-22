package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;


@Service("ProcessRecTaxPeriodsFinderImpl")
public class ProcessRecTaxPeriodsFinderImpl implements 
              ProcessRecTaxPeriodsFinder {

	@Override
	public List<String> getApplicableTaxPeriods() {
	
		LocalDate date = LocalDate.now();
		// Last Day of previous Month
		LocalDate last = date.with(TemporalAdjusters.firstDayOfMonth())
				 .minusDays(1);
		DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("MMyyyy");
		return new ImmutableList.Builder<String>()
				.add(last.format(formatter))
				.build();
	}
	
	
}
