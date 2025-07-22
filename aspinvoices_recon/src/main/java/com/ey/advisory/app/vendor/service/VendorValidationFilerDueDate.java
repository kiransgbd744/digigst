package com.ey.advisory.app.vendor.service;

/**
 * 
 * @author vishal.verma
 *
 */

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component("VendorValidationFilerDueDate")
public class VendorValidationFilerDueDate {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");

	// Maps to store due dates for filings
	private static final Map<String, MonthDay> dueDates = new HashMap<>();

	static {

		// GSTR1 Monthly due dates
		dueDates.put("GSTR1-Apr", MonthDay.of(5, 11));
		dueDates.put("GSTR1-May", MonthDay.of(6, 11));
		dueDates.put("GSTR1-Jun", MonthDay.of(7, 11));
		dueDates.put("GSTR1-Jul", MonthDay.of(8, 11));
		dueDates.put("GSTR1-Aug", MonthDay.of(9, 11));
		dueDates.put("GSTR1-Sep", MonthDay.of(10, 11));
		dueDates.put("GSTR1-Oct", MonthDay.of(11, 11));
		dueDates.put("GSTR1-Nov", MonthDay.of(12, 11));
		dueDates.put("GSTR1-Dec", MonthDay.of(1, 11));
		dueDates.put("GSTR1-Jan", MonthDay.of(2, 11));
		dueDates.put("GSTR1-Feb", MonthDay.of(3, 11));
		dueDates.put("GSTR1-Mar", MonthDay.of(4, 11));

		// GSTR1 Quarterly due dates
		dueDates.put("GSTR1-Apr-Jun", MonthDay.of(7, 13));
		dueDates.put("GSTR1-Jul-Sep", MonthDay.of(10, 13));
		dueDates.put("GSTR1-Oct-Dec", MonthDay.of(1, 13));
		dueDates.put("GSTR1-Jan-Mar", MonthDay.of(4, 13));

		// GSTR3B Monthly due dates
		dueDates.put("GSTR3B-Apr", MonthDay.of(5, 20));
		dueDates.put("GSTR3B-May", MonthDay.of(6, 20));
		dueDates.put("GSTR3B-Jun", MonthDay.of(7, 20));
		dueDates.put("GSTR3B-Jul", MonthDay.of(8, 20));
		dueDates.put("GSTR3B-Aug", MonthDay.of(9, 20));
		dueDates.put("GSTR3B-Sep", MonthDay.of(10, 20));
		dueDates.put("GSTR3B-Oct", MonthDay.of(11, 20));
		dueDates.put("GSTR3B-Nov", MonthDay.of(12, 20));
		dueDates.put("GSTR3B-Dec", MonthDay.of(1, 20));
		dueDates.put("GSTR3B-Jan", MonthDay.of(2, 20));
		dueDates.put("GSTR3B-Feb", MonthDay.of(3, 20));
		dueDates.put("GSTR3B-Mar", MonthDay.of(4, 20));

		// GSTR3B Quarterly due dates
		dueDates.put("GSTR3B-Apr-Jun", MonthDay.of(7, 24));
		dueDates.put("GSTR3B-Jul-Sep", MonthDay.of(10, 24));
		dueDates.put("GSTR3B-Oct-Dec", MonthDay.of(1, 24));
		dueDates.put("GSTR3B-Jan-Mar", MonthDay.of(4, 24));

		// GSTR-5 Monthly due dates
		dueDates.put("GSTR-5-Apr", MonthDay.of(5, 13));
		dueDates.put("GSTR-5-May", MonthDay.of(6, 13));
		dueDates.put("GSTR-5-Jun", MonthDay.of(7, 13));
		dueDates.put("GSTR-5-Jul", MonthDay.of(8, 13));
		dueDates.put("GSTR-5-Aug", MonthDay.of(9, 13));
		dueDates.put("GSTR-5-Sep", MonthDay.of(10, 13));
		dueDates.put("GSTR-5-Oct", MonthDay.of(11, 13));
		dueDates.put("GSTR-5-Nov", MonthDay.of(12, 13));
		dueDates.put("GSTR-5-Dec", MonthDay.of(1, 13));
		dueDates.put("GSTR-5-Jan", MonthDay.of(2, 13));
		dueDates.put("GSTR-5-Feb", MonthDay.of(3, 13));
		dueDates.put("GSTR-5-Mar", MonthDay.of(4, 13));

		// GSTR6 Monthly due dates
		dueDates.put("GSTR6-Apr", MonthDay.of(5, 13));
		dueDates.put("GSTR6-May", MonthDay.of(6, 13));
		dueDates.put("GSTR6-Jun", MonthDay.of(7, 13));
		dueDates.put("GSTR6-Jul", MonthDay.of(8, 13));
		dueDates.put("GSTR6-Aug", MonthDay.of(9, 13));
		dueDates.put("GSTR6-Sep", MonthDay.of(10, 13));
		dueDates.put("GSTR6-Oct", MonthDay.of(11, 13));
		dueDates.put("GSTR6-Nov", MonthDay.of(12, 13));
		dueDates.put("GSTR6-Dec", MonthDay.of(1, 13));
		dueDates.put("GSTR6-Jan", MonthDay.of(2, 13));
		dueDates.put("GSTR6-Feb", MonthDay.of(3, 13));
		dueDates.put("GSTR6-Mar", MonthDay.of(4, 13));

		// GSTR7 Monthly due dates
		dueDates.put("GSTR7-Apr", MonthDay.of(5, 10));
		dueDates.put("GSTR7-May", MonthDay.of(6, 10));
		dueDates.put("GSTR7-Jun", MonthDay.of(7, 10));
		dueDates.put("GSTR7-Jul", MonthDay.of(8, 10));
		dueDates.put("GSTR7-Aug", MonthDay.of(9, 10));
		dueDates.put("GSTR7-Sep", MonthDay.of(10, 10));
		dueDates.put("GSTR7-Oct", MonthDay.of(11, 10));
		dueDates.put("GSTR7-Nov", MonthDay.of(12, 10));
		dueDates.put("GSTR7-Dec", MonthDay.of(1, 10));
		dueDates.put("GSTR7-Jan", MonthDay.of(2, 10));
		dueDates.put("GSTR7-Feb", MonthDay.of(3, 10));
		dueDates.put("GSTR7-Mar", MonthDay.of(4, 10));

		// GSTR8 Monthly due dates
		dueDates.put("GSTR8-Apr", MonthDay.of(5, 10));
		dueDates.put("GSTR8-May", MonthDay.of(6, 10));
		dueDates.put("GSTR8-Jun", MonthDay.of(7, 10));
		dueDates.put("GSTR8-Jul", MonthDay.of(8, 10));
		dueDates.put("GSTR8-Aug", MonthDay.of(9, 10));
		dueDates.put("GSTR8-Sep", MonthDay.of(10, 10));
		dueDates.put("GSTR8-Oct", MonthDay.of(11, 10));
		dueDates.put("GSTR8-Nov", MonthDay.of(12, 10));
		dueDates.put("GSTR8-Dec", MonthDay.of(1, 10));
		dueDates.put("GSTR8-Jan", MonthDay.of(2, 10));
		dueDates.put("GSTR8-Feb", MonthDay.of(3, 10));
		dueDates.put("GSTR8-Mar", MonthDay.of(4, 10));
	}

	public boolean isDueDateAfterCurrentDate(String derivedTaxPeriod, String returnType, 
			String filingType){
	    String monthYear = derivedTaxPeriod.substring(4) + derivedTaxPeriod.substring(0,4);
	    String key = returnType + "-" + getMonthFromTaxPeriod(monthYear);

	    // Get the due date based on the key
	    MonthDay dueMonthDay = dueDates.get(key);
	    if (dueMonthDay == null) {
	        LOGGER.debug("Invalid tax period or return type.");
	        return false;
	    }

	    // Parse the filing date
	    LocalDate currentDate;
	    try {
	    	String date = LocalDate.now().toString();
	        currentDate = LocalDate.parse(date, DATE_FORMATTER);
	    } catch (DateTimeParseException e) {
	    	LOGGER.debug("Invalid filing date format. Please use yyyy-MM-dd.");
	        return false;
	    }

	    // Determine the due date based on filing type
	    LocalDate dueDate;
	    if ("Monthly".equalsIgnoreCase(filingType)) {
	        // For monthly filings, use the current year
	       // int currentYear = Year.now().getValue();
	        Integer month = Integer.parseInt(derivedTaxPeriod.substring(4,6));
	        Integer year = Integer.parseInt(derivedTaxPeriod.substring(0,4));
	        if(month == 12) {
	        	dueDate = dueMonthDay.atYear(year+1);
	        }
	        dueDate = dueMonthDay.atYear(year);
	    } else if ("Quarterly".equalsIgnoreCase(filingType)) {
	        // For quarterly filings, adjust the year if needed (e.g., for Jan-Mar quarter)
	        int currentYear = Integer.parseInt(derivedTaxPeriod.substring(0,4));
	        if (dueMonthDay.getMonthValue() == 1 && (key.endsWith("-Dec") ||
	        		key.endsWith("-Oct-Dec"))) {
	            dueDate = dueMonthDay.atYear(currentYear + 1);
	        } else {
	            dueDate = dueMonthDay.atYear(currentYear);
	        }
	    } else {
	        LOGGER.debug("Unknown filing type: {}", filingType);
	        return false;
	    }

	    // Check if current date is on or before due date
	    return currentDate.isAfter(dueDate);
	
	}
	
	public boolean isFiledOnTime(String taxPeriod, String returnType, 
			String filingType, LocalDate filingDate) {
	    String monthYear = taxPeriod.substring(0, 2) + taxPeriod.substring(2);
	    String key = returnType + "-" + getMonthFromTaxPeriod(monthYear);
	    
	    // Get the due date based on the key
	    MonthDay dueMonthDay = dueDates.get(key);
	    if (dueMonthDay == null) {
	        LOGGER.debug("Invalid tax period or return type.");
	        return false;
	    }

	    // Parse the filing date
//	    LocalDate fileDate;
	    try {
//	        fileDate = LocalDate.parse(filingDate, DATE_FORMATTER);
	    } catch (DateTimeParseException e) {
	    	LOGGER.debug("Invalid filing date format. Please use yyyy-MM-dd.");
	        return false;
	    }

	    // Determine the due date based on filing type
	    LocalDate dueDate;
	    if ("Monthly".equalsIgnoreCase(filingType)) {
	        // For monthly filings, use the current year
	       // int currentYear = Year.now().getValue();
	        Integer month = Integer.parseInt(taxPeriod.substring(0, 2));
	        Integer year = Integer.parseInt(taxPeriod.substring(2, 6));
	        if(month == 12) {
	        	dueDate = dueMonthDay.atYear(year+1);
	        }
	        dueDate = dueMonthDay.atYear(year);
	    } else if ("Quarterly".equalsIgnoreCase(filingType)) {
	        // For quarterly filings, adjust the year if needed (e.g., for Jan-Mar quarter)
	        int currentYear = Integer.parseInt(taxPeriod.substring(2, 6));
	        if (dueMonthDay.getMonthValue() == 1 && (key.endsWith("-Dec") ||
	        		key.endsWith("-Oct-Dec"))) {
	            dueDate = dueMonthDay.atYear(currentYear + 1);
	        } else {
	            dueDate = dueMonthDay.atYear(currentYear);
	        }
	    } else {
	        LOGGER.debug("Unknown filing type: {}", filingType);
	        return false;
	    }

	    // Check if filing date is on or before due date
	    try {
		    return !filingDate.isAfter(dueDate);
	    } catch (DateTimeParseException e) {
	    	LOGGER.error("Invalid filing date format. Please use yyyy-MM-dd {} ",e);
	        return false;
	    }
	}

	public static String getMonthFromTaxPeriod(String taxPeriod) {
		// Create a DateTimeFormatter for the input format
		DateTimeFormatter inputFormatter = DateTimeFormatter
				.ofPattern("MMyyyy");

		// Parse the input string to a YearMonth object
		YearMonth yearMonth = YearMonth.parse(taxPeriod, inputFormatter);

		// Format the YearMonth to the desired output format
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM");
		return yearMonth.format(outputFormatter);
	}

	public static void main(String[] args) {
		String returnType = "GSTR1";
		String taxPeriod = "052024";
	    String monthYear = taxPeriod.substring(0, 2) + taxPeriod.substring(2);
	    String filingType = "Monthly";
	    String key = returnType + "-" + getMonthFromTaxPeriod(monthYear);
	    // Get the due date based on the key
	    MonthDay dueMonthDay = dueDates.get(key);
	    if (dueMonthDay == null) {
	        LOGGER.debug("Invalid tax period or return type.");
	    }

	    // Parse the filing date
	    LocalDate fileDate = null;
	    String filingDate = LocalDate.of(2024, 06, 12).toString();
	    try {
	        fileDate = LocalDate.parse(filingDate, DATE_FORMATTER);
	    } catch (DateTimeParseException e) {
	    	LOGGER.debug("Invalid filing date format. Please use yyyy-MM-dd.");
	    }

	    // Determine the due date based on filing type
		LocalDate dueDate = MonthDay.of(5, 11).atYear(2024);
	    if ("Monthly".equalsIgnoreCase(filingType)) {
	        // For monthly filings, use the current year
	       // int currentYear = Year.now().getValue();
	        Integer month = Integer.parseInt(taxPeriod.substring(0, 2));
	        Integer year = Integer.parseInt(taxPeriod.substring(2, 6));
	        if(month == 12) {
	        	dueDate = dueMonthDay.atYear(year+1);
	        }
	        try {
	        dueDate = dueMonthDay.atYear(year);
	        } catch (Exception e) {
		    	LOGGER.debug("Invalid filing date format. Please use yyyy-MM-dd.");
		    }
	    } else if ("Quarterly".equalsIgnoreCase(filingType)) {
	        // For quarterly filings, adjust the year if needed (e.g., for Jan-Mar quarter)
	        int currentYear = Integer.parseInt(taxPeriod.substring(2, 6));
	        if (dueMonthDay.getMonthValue() == 1 && (key.endsWith("-Dec") ||
	        		key.endsWith("-Oct-Dec"))) {
	            dueDate = dueMonthDay.atYear(currentYear + 1);
	        } else {
	            dueDate = dueMonthDay.atYear(currentYear);
	        }
	    } else {
	        LOGGER.debug("Unknown filing type: {}", filingType);
	    }

	    // Check if filing date is on or before due date
	
		System.out.println(!fileDate.isAfter(dueDate));
	}
}
