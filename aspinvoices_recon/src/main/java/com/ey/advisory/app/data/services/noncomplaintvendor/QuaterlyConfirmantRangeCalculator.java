package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("QuaterlyConfirmantRangeCalculator")
public class QuaterlyConfirmantRangeCalculator
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
		return calculateQuarterlyRange(mandatoryList, optionalList, taxPeriods,
				month, day, returnType);
	}

	private ConfirmantRangeDto calculateQuarterlyRange(
			List<String> mandatoryList, List<String> optionalList,
			List<String> taxPeriods, String month, int day, String returnType) {

		int dueDate = getQuaterlyDueDate(returnType);
		if (month.equals("04") || month.equals("05") || month.equals("06")) {
			return null;
		} else if (month.equals("07") || month.equals("08")
				|| month.equals("09")) {
			if (month.equals("07") && day <= dueDate) {
				optionalList.add(taxPeriods.get(0));
			} else {
				mandatoryList.add(taxPeriods.get(0));
			}
			return new ConfirmantRangeDto(mandatoryList, optionalList);
		} else if (month.equals("10") || month.equals("11")
				|| month.equals("12")) {
			if (month.equals("10") && day <= dueDate) {
				optionalList.add(taxPeriods.get(1));
			} else {
				mandatoryList.add(taxPeriods.get(0));
				mandatoryList.add(taxPeriods.get(1));
			}
			return new ConfirmantRangeDto(mandatoryList, optionalList);
		} else {
			if (month.equals("01") && day <= dueDate) {
				optionalList.add(taxPeriods.get(2));
			} else {
				mandatoryList.add(taxPeriods.get(0));
				mandatoryList.add(taxPeriods.get(1));
				mandatoryList.add(taxPeriods.get(2));
			}
			return new ConfirmantRangeDto(mandatoryList, optionalList);
		}
	}

	private int getQuaterlyDueDate(String returnType) {
		int dueDate = 0;
		switch (returnType) {
		case NCVReturnTypeConstants.ITC04:
			dueDate = 25;
			break;
		case NCVReturnTypeConstants.CMP08:
			dueDate = 18;
			break;
		default:
			String msg = String.format(
					"Currently ReturnType - %s is not supported for Quaterly Calculator",
					returnType);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dueDate;

	}
}
