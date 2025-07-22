package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;

@Component("AnnuallyConfirmantRangeCalculator")
public class AnnuallyConfirmantRangeCalculator
		implements ConfirmantRangeCalculator {

	@Override
	public ConfirmantRangeDto calculate(String returnType, String filingType,
			String fy) {
		LocalDate currDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");
		String currentMonthAndYr = currDate.format(formatter);
		String month = currentMonthAndYr.substring(0, 2);
		int day = currDate.getDayOfMonth();
		List<String> mandatoryList = new ArrayList<>();
		List<String> optionalList = new ArrayList<>();

		List<String> taxPeriods = GenUtil.extractTaxPeriodsFromFY(fy,
				returnType);
		return calculateAnnualRange(mandatoryList, optionalList, taxPeriods,
				month, day, returnType);
	}

	private ConfirmantRangeDto calculateAnnualRange(List<String> mandatoryList,
			List<String> optionalList, List<String> taxPeriods, String month,
			int day, String returnType) {

		if (month.equals("12")) {
			if (day < 31) {
				optionalList.add(taxPeriods.get(0));
			} else {
				mandatoryList.add(taxPeriods.get(0));
			}
		} else {
			return null;
		}
		return new ConfirmantRangeDto(mandatoryList, optionalList);
	}

}
