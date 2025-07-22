package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.TypeOfGstFiling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MonthlyConfirmantRangeCalculator")
public class MonthlyConfirmantRangeCalculator
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

		if (filingType.equals(TypeOfGstFiling.MONTHLY.toString())) {
			List<String> taxPeriods = GenUtil.extractTaxPeriodsFromFY(fy,
					returnType);
			return calculateMonthlyRange(mandatoryList, optionalList,
					taxPeriods, month, day, returnType);
		} else {
			List<String> taxPeriods = GenUtil.extractTaxPeriodsFromFY(fy,
					"CMP08");
			return calculateQuarterlyRange(mandatoryList, optionalList,
					taxPeriods, month, day);
		}

	}

	private ConfirmantRangeDto calculateMonthlyRange(List<String> mandatoryList,
			List<String> optionalList, List<String> taxPeriods, String month,
			int day, String returnType) {
		int dueDate = getMothlyDueDate(returnType);
		if (month.equals("04")) {
			return null;
		} else if (month.equals("05")) {
			if (day > dueDate) {
				mandatoryList.add(taxPeriods.get(0));
			} else {
				optionalList.add(taxPeriods.get(0));
			}
			return new ConfirmantRangeDto(mandatoryList, optionalList);
		} else {
			if (month.equals("01"))
				month = "13";
			if (month.equals("02"))
				month = "14";
			if (month.equals("03"))
				month = "15";
			int index = Integer.valueOf(month) - 4;
			for (int i = 0; i < index; i++) {
				if (i == index - 1) {
					if (day > dueDate)
						mandatoryList.add(taxPeriods.get(index - 1));
					else
						optionalList.add(taxPeriods.get(index - 1));
				} else {
					mandatoryList.add(taxPeriods.get(i));
				}
			}
			return new ConfirmantRangeDto(mandatoryList, optionalList);
		}
	}

	private ConfirmantRangeDto calculateQuarterlyRange(
			List<String> mandatoryList, List<String> optionalList,
			List<String> taxPeriods, String month, int day) {

		if (month.equals("04") || month.equals("05") || month.equals("06")) {
			return null;
		} else if (month.equals("07") || month.equals("08")
				|| month.equals("09")) {
			if (month.equals("07") && day <= 13) {
				optionalList.add(taxPeriods.get(0));
			} else {
				mandatoryList.add(taxPeriods.get(0));
			}
			return new ConfirmantRangeDto(mandatoryList, optionalList);
		} else if (month.equals("10") || month.equals("11")
				|| month.equals("12")) {
			if (month.equals("10") && day <= 13) {
				optionalList.add(taxPeriods.get(1));
			} else {
				mandatoryList.add(taxPeriods.get(0));
				mandatoryList.add(taxPeriods.get(1));
			}
			return new ConfirmantRangeDto(mandatoryList, optionalList);
		} else {
			if (month.equals("01") && day <= 13) {
				optionalList.add(taxPeriods.get(2));
			} else {
				mandatoryList.add(taxPeriods.get(0));
				mandatoryList.add(taxPeriods.get(1));
				mandatoryList.add(taxPeriods.get(2));
			}
			return new ConfirmantRangeDto(mandatoryList, optionalList);
		}
	}

	private int getMothlyDueDate(String returnType) {
		int dueDate = 0;
		switch (returnType) {
		case NCVReturnTypeConstants.GSTR1:
			dueDate = 11;
			break;
		case NCVReturnTypeConstants.GSTR1A:
			dueDate = 11;
			break;
		case NCVReturnTypeConstants.GSTR5:
			dueDate = 20;
			break;
		case NCVReturnTypeConstants.GSTR6:
			dueDate = 13;
			break;
		case NCVReturnTypeConstants.GSTR7:
			dueDate = 10;
			break;
		case NCVReturnTypeConstants.GSTR8:
			dueDate = 10;
			break;
		case NCVReturnTypeConstants.GSTR3B:
			dueDate = 20;
			break;
		default:
			String msg = String.format(
					"Currently ReturnType - %s is not supported for Monthly Calculator",
					returnType);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dueDate;

	}
}
