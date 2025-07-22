package com.ey.advisory.app.services.search.docsummarysearch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

@Service("GstnDateCalculation")
public class GstnDateCalculation {
	
	public List<String> findingReturnPeriods(LocalDate startDate,
			LocalDate endDate) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		endDate = endDate.plusMonths(1);
		long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		List<LocalDate> date = IntStream.iterate(0, i -> i + 1)
				.limit(numOfDaysBetween).mapToObj(i -> startDate.plusDays(i))
				.collect(Collectors.toList());

		List<String> str = new ArrayList<String>();
		for (LocalDate d : date) {
			String date_to_string = formatter.format(d);
			String s23 = date_to_string.substring(2, 8);
			str.add(s23);
			// System.out.println(s23);
		}
		Set<String> s = new HashSet<String>(str);
		List dates = new ArrayList<>(s);
		

		return dates;
	}


}
