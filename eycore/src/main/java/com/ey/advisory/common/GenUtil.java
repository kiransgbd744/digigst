package com.ey.advisory.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.javatuples.Pair;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenUtil {

	/**
	 * Make the class non-instantiable.
	 */
	private GenUtil() {
	}

	private static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("MMyyyy", Locale.ENGLISH);

	public static void setAsposeLicense() {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Setting Aspose License");
		}

		try {
			InputStream stream = GenUtil.class.getClassLoader()
					.getResourceAsStream("licenses/Aspose.Cells.lic");
			com.aspose.cells.License license = new com.aspose.cells.License();
			license.setLicense(stream);
		} catch (Exception ex) {
			String msg = "Failed to set Aspose License from file";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Aspose License set successfully");
		}

	}

	public static void appendStringToBuffer(StringBuilder writer,
			String input) {
		try {
			writer.append(input == null ? "" : input);
		} catch (Exception ex) {
			String errMsg = "Error occured while writing data to csv";
			LOGGER.error(errMsg, ex);
			throw new AppException(errMsg, ex);
		}

	}

	public static void appendStringToJoiner(StringJoiner joiner, String input) {
		try {
			joiner.add(input == null ? "" : input);
		} catch (Exception ex) {
			String errMsg = "Error occured while writing data to csv";
			LOGGER.error(errMsg, ex);
			throw new AppException(errMsg, ex);
		}

	}

	/**
	 * This method converts a normal string to a Csv String. It first checks for
	 * double quotes within the string and replaces each of its occurrence with
	 * 2 double quotes. It then checks if a quotes or a comma is available. If
	 * so, it encloses the string with double quotes and returns this value. If
	 * the original string does not have a double quote or a comma, then it
	 * returns the orignal string itself.
	 * 
	 * 
	 * @param input
	 * @return
	 */
	public static String toCsvString(String input) {

		if (input == null)
			return input;

		// First replace all Commas with semicolon(;).
		String retStr = input;
		if (retStr.indexOf(',') >= 0) {
			retStr = input.replaceAll(",", ";");
		}
		if (retStr.indexOf('"') >= 0) {
			retStr = retStr.replace("\"", "");
		}
		if (retStr.contains("\n")) {
			retStr = retStr.replace("\n", "");
		}

		return retStr;
	}

	public static int getRandomNumberInRange(int min, int max) {
		if (min >= max) {
			String msg = "Invalid Random Number Gen Range. "
					+ "Min Value must be less than the Max Value";
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		}
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	/**
	 * Return a string representation of the map in the form:
	 * 
	 * "(key1 -> val1), (key2 -> val2), (key3 -> val3)..."
	 * 
	 * where key is of type K and value is of type V. K annd V, both of them
	 * should either be strings OR have formatted/readable string
	 * representations by overriding the toString() method within these classes.
	 * Otherwise the output will not be readable.
	 * 
	 * Note that, this method is not for huge maps. It is a convenience method
	 * for printing/debugging very small maps. Using this for huge maps will
	 * have severe memory/CPU usage implications.
	 * 
	 * @param map
	 * @return
	 */
	public static <K, V> String mapToString(Map<K, V> map) {
		return map.entrySet().stream().reduce("",
				(s, ele) -> concatMapEntryToStr(s, ele), (s1, s2) -> s1 + s2);
	}

	/**
	 * Concatenate a string with a Map Entry formatted as "(key, value)". This
	 * can be used to chain the map entries within a map to form a single string
	 * of all the key/value pairs in the map, for debugging purposes. For
	 * example, "(key1 -> val1), (key2 -> val2), ..."
	 * 
	 * @param s
	 * @param ele
	 * @return
	 */
	private static <K, V> String concatMapEntryToStr(String s,
			Map.Entry<K, V> ele) {
		return String.format("%s%s(%s -> %s)", s, s.isEmpty() ? "" : ", ",
				ele.getKey(), ele.getValue());
	}

	/**
	 * Since tax period is generally stored as string in the format of MMyyyy,
	 * we cannot use it to search using the between or < or > operators in the
	 * DB. So, usually, we will store another calculated field called derived
	 * tax period in the DB which will be an integer and will have the format
	 * yyyyMM, so that this field can be used for comparisons.
	 * 
	 * This method will convert the string taxeperid in the MMyyyy format to the
	 * dervided taxperiod integer value (in the format yyyyMM)
	 * 
	 * The input TaxPeriod should be formatted in the MMyyyy format. Any other
	 * input will result in unpredictable behavior.
	 * 
	 * @return
	 */
	public static Integer convertTaxPeriodToInt(String taxPeriod) {

		if (StringUtils.isEmpty(taxPeriod)) {
			return null;
		}
		String dateStr = "01" + taxPeriod;
		DateTimeFormatter ddMMyyyyFormatter = DateTimeFormatter
				.ofPattern("ddMMyyyy");
		DateTimeFormatter yyyyMMFormatter = DateTimeFormatter
				.ofPattern("yyyyMM");
		Integer derivedTaxPer = null;
		try {
			LocalDate date = LocalDate.parse(dateStr, ddMMyyyyFormatter);
			String out = date.format(yyyyMMFormatter);
			derivedTaxPer = Integer.parseInt(out);
		} catch (Exception e) {
			return derivedTaxPer;
		}
		return derivedTaxPer;
	}

	/**
	 * Get the Financial Year based on Document Date
	 * 
	 * @param docDate
	 * @return
	 */
	public static String getFinYear(LocalDate docDate) {
		try {
			if (docDate == null)
				return "";

			int docYear = docDate.getYear();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Doc Year ", docYear);
			}
			String financialYearFrom = (docYear - 1) + "-04-01";
			String financiyalYearTo = docYear + "-03-31";
			LocalDate dateFrom = LocalDate.parse(financialYearFrom);
			LocalDate dateTo = LocalDate.parse(financiyalYearTo);
			if (docDate.isAfter(dateFrom)
					&& docDate.isBefore(dateTo.plusDays(1))) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Date:{} ", docDate, " Doc Date From :{}",
							dateFrom, " Doc Date To :{}", dateTo);
				}
				int finYearFrom = docYear - 1;
				int finYearTo = docYear;
				return finYear(finYearFrom, finYearTo);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Date ", docDate, " Doc Date From ",
							dateFrom, " Doc Date To ", dateTo);
				}
				int finYearFrom = docYear;
				int finYearTo = docYear + 1;
				return finYear(finYearFrom, finYearTo);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String getFinYearJanToDec(LocalDate docDate) {
		String finYear = null;
		try {
			if (docDate == null)
				return "";

			int docYear = docDate.getYear();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Doc Year ", docYear);
			}
			String financialYearFrom = (docYear - 1) + "-01-01";
			String financiyalYearTo = docYear + "-12-31";
			LocalDate dateFrom = LocalDate.parse(financialYearFrom);
			LocalDate dateTo = LocalDate.parse(financiyalYearTo);
			if (docDate.isAfter(dateFrom)
					&& docDate.isBefore(dateTo.plusDays(1))) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Date:{} ", docDate, " Doc Date From :{}",
							dateFrom, " Doc Date To :{}", dateTo);
				}
				finYear = String.valueOf(docYear);// 2021
			}
		} catch (Exception e) {
			return null;
		}
		return finYear;
	}

	public static String getFinYearJulToJune(LocalDate docDate) {
		try {
			if (docDate == null)
				return "";

			int docYear = docDate.getYear();// 2022
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Doc Year ", docYear);
			}
			String financialYearFrom = (docYear - 1) + "-07-01";
			String financiyalYearTo = docYear + "-06-30";
			LocalDate dateFrom = LocalDate.parse(financialYearFrom);
			LocalDate dateTo = LocalDate.parse(financiyalYearTo);
			if (docDate.isAfter(dateFrom)
					&& docDate.isBefore(dateTo.plusDays(1))) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Date:{} ", docDate, " Doc Date From :{}",
							dateFrom, " Doc Date To :{}", dateTo);
				}
				int finYearFrom = docYear - 1;// 2021
				int finYearTo = docYear;// 2022
				return finYear(finYearFrom, finYearTo);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Date ", docDate, " Doc Date From ",
							dateFrom, " Doc Date To ", dateTo);
				}
				int finYearFrom = docYear;
				int finYearTo = docYear + 1;
				return finYear(finYearFrom, finYearTo);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String getFinYearOctToSept(LocalDate docDate) {
		try {
			if (docDate == null)
				return "";

			int docYear = docDate.getYear();// 2022
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Doc Year ", docYear);
			}
			String financialYearFrom = (docYear - 1) + "-10-01";
			String financiyalYearTo = docYear + "-09-30";
			LocalDate dateFrom = LocalDate.parse(financialYearFrom);
			LocalDate dateTo = LocalDate.parse(financiyalYearTo);
			if (docDate.isAfter(dateFrom)
					&& docDate.isBefore(dateTo.plusDays(1))) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Date:{} ", docDate, " Doc Date From :{}",
							dateFrom, " Doc Date To :{}", dateTo);
				}
				int finYearFrom = docYear - 1;// 2021
				int finYearTo = docYear;// 2022
				return finYear(finYearFrom, finYearTo);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Doc Date ", docDate, " Doc Date From ",
							dateFrom, " Doc Date To ", dateTo);
				}
				int finYearFrom = docYear;
				int finYearTo = docYear + 1;
				return finYear(finYearFrom, finYearTo);
			}
		} catch (Exception e) {
			return null;
		}
	}

	private static String finYear(int finYearFrom, int finYearTo) {
		StringBuffer finYearSb = new StringBuffer();
		finYearSb.append(finYearFrom);
		String finYearTo1 = String.valueOf(finYearTo).substring(2);
		finYearSb.append(finYearTo1);
		String finYear = finYearSb.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Fin Year:{} ", finYear);
		}
		return finYear;
	}

	public static String trimAndConvToUpperCase(String str) {
		if (str == null)
			return null;
		return str.trim().toUpperCase();
	}

	public static String trimAndConvToLowerCase(String str) {
		if (str == null)
			return null;
		return str.trim().toLowerCase();
	}

	public static LocalDate toLocalDate(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant,
				ZoneId.systemDefault());
		return localDateTime.toLocalDate();
	}

	public static String convLineNumToString(String input) {

		if (input == null)
			return input;

		// If the line number is a floating number take only digit right of ".".

		String retStr = input;
		if (retStr.indexOf('.') >= 0) {
			retStr = input.substring(0, input.indexOf('.'));
		}

		return retStr;
	}

	public static String convertDerivedTaxPeriodToTaxPeriod(
			int derivedTaxPeriod) {

		String devrivedTaxPeriodAsString = String.valueOf(derivedTaxPeriod);
		return devrivedTaxPeriodAsString.substring(4)
				.concat(devrivedTaxPeriodAsString.substring(0, 4));
	}

	public static void deleteTempDir(File tempDir) {
		if (tempDir != null && tempDir.exists()) {
			try {
				FileUtils.deleteDirectory(tempDir);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempDir.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempDir.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}
	}

	// Financial year should be in YYYY-YY format like 2017-18
	public static List<String> extractTaxPeriodsFromFY(String finYear,
			String returnType) {
		int financialYear = Integer.parseInt(finYear.substring(0, 4));
		List<String> taxPeriod;
		if (returnType.equals("ITC04")) {
			if (financialYear < 2021) {
				taxPeriod = Arrays.asList("13" + financialYear,
						"14" + financialYear, "15" + financialYear,
						"16" + financialYear);
			} else if (financialYear == 2021) {
				taxPeriod = Arrays.asList("13" + financialYear,
						"14" + financialYear, "18" + financialYear);
			} else {
				taxPeriod = Arrays.asList("17" + financialYear,
						"18" + financialYear);
			}
		} else if (returnType.equals("CMP08")) {
			taxPeriod = Arrays.asList("06" + financialYear,
					"09" + financialYear, "12" + financialYear,
					"03" + (financialYear + 1));
		} else if (returnType.equals("GSTR4") || returnType.equals("GSTR9")
				|| returnType.equals("GSTR9A")) {
			taxPeriod = Arrays.asList("03" + (financialYear + 1));
		} else if (returnType.equals("ALL")) {
			taxPeriod = Arrays.asList("04" + financialYear,
					"05" + financialYear, "06" + financialYear,
					"07" + financialYear, "08" + financialYear,
					"09" + financialYear, "10" + financialYear,
					"11" + financialYear, "12" + financialYear,
					"01" + (financialYear + 1), "02" + (financialYear + 1),
					"03" + (financialYear + 1), "13" + financialYear,
					"14" + financialYear, "15" + financialYear,
					"16" + financialYear);
		} else {
			taxPeriod = Arrays.asList("04" + financialYear,
					"05" + financialYear, "06" + financialYear,
					"07" + financialYear, "08" + financialYear,
					"09" + financialYear, "10" + financialYear,
					"11" + financialYear, "12" + financialYear,
					"01" + (financialYear + 1), "02" + (financialYear + 1),
					"03" + (financialYear + 1));
		}
		return taxPeriod;
	}

	public static List<String> extractTaxPeriodsFromFYForReport(String finYear,
			String returnType) {
		int financialYear = Integer.parseInt(finYear.substring(0, 4));
		List<String> taxPeriod;
		if (returnType.equals("ITC04")) {
			if (financialYear < 2021) {
				taxPeriod = Arrays.asList("Q1" + financialYear,
						"Q2" + financialYear, "Q3" + financialYear,
						"Q4" + financialYear);
			} else if (financialYear == 2021) {
				taxPeriod = Arrays.asList("Q1" + financialYear,
						"Q2" + financialYear, "H2" + financialYear);
			} else {
				taxPeriod = Arrays.asList("H1" + financialYear,
						"H2" + financialYear);
			}
		} else if (returnType.equals("CMP08")) {
			taxPeriod = Arrays.asList("06" + financialYear,
					"09" + financialYear, "12" + financialYear,
					"03" + (financialYear + 1));
		} else if (returnType.equals("GSTR4") || returnType.equals("GSTR9")
				|| returnType.equals("GSTR9A")) {
			taxPeriod = Arrays.asList("03" + (financialYear + 1));
		} else if (returnType.equals("ALL")) {
			taxPeriod = Arrays.asList("04" + financialYear,
					"05" + financialYear, "06" + financialYear,
					"07" + financialYear, "08" + financialYear,
					"09" + financialYear, "10" + financialYear,
					"11" + financialYear, "12" + financialYear,
					"01" + (financialYear + 1), "02" + (financialYear + 1),
					"03" + (financialYear + 1), "13" + financialYear,
					"14" + financialYear, "15" + financialYear,
					"16" + financialYear);
		} else {
			taxPeriod = Arrays.asList("04" + financialYear,
					"05" + financialYear, "06" + financialYear,
					"07" + financialYear, "08" + financialYear,
					"09" + financialYear, "10" + financialYear,
					"11" + financialYear, "12" + financialYear,
					"01" + (financialYear + 1), "02" + (financialYear + 1),
					"03" + (financialYear + 1));
		}
		return taxPeriod;
	}

	// Generate Current FY in YYYY-YY format like 2017-18
	public static String getCurrentFinancialYear() {

		int year = LocalDate.now().getYear();
		int month = LocalDate.now().getMonthValue() + 1;

		String finYear = null;
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Financial month : {} ", month);
		if (month < 3) {
			finYear = (year - 1) + "-" + String.valueOf(year).substring(2);
		} else {
			finYear = year + "-" + String.valueOf(year + 1).substring(2);
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Financial year : {} ", finYear);
		return finYear;
	}

	public static String checkExpo(String amt) {

		return (amt != null) ? new BigDecimal(amt).toPlainString() : amt;
	}

	public static String checkExpoAndEmpty(String amt) {

		return (!Strings.isNullOrEmpty(amt) && !(amt.equals("0")
				|| amt.equals("0.0") || amt.equals("0.00")))
						? "'" + new BigDecimal(amt).toPlainString() : amt;
	}

	public static String checkExpoForInvoice(String amt) {

		return (!Strings.isNullOrEmpty(amt) && !(amt.equals("0")
				|| amt.equals("0.0") || amt.equals("0.00"))) ? "'" + amt : amt;
	}

	public static String checkExponenForAmt(BigDecimal amt) {

		if (amt == null || amt.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO.toPlainString();
		}
		return "'" + amt.toPlainString();
	}

	public static String formatCurrency(Object input) {
		com.ibm.icu.text.NumberFormat format = com.ibm.icu.text.NumberFormat
				.getInstance(new Locale("en", "in"));
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		String formattedOutput = format.format(input);
		return formattedOutput;
	}

	public static Object deepCopy(Object object) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream outputStrm = new ObjectOutputStream(
					outputStream);
			outputStrm.writeObject(object);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					outputStream.toByteArray());
			ObjectInputStream objInputStream = new ObjectInputStream(
					inputStream);
			return objInputStream.readObject();
		} catch (Exception e) {
			LOGGER.error("Exception while Deep Copying the Object", e);
			return null;
		}
	}

	// Generate FY in YYYY-YY format like 2021-22 based on taxperiod(042021)
	public static String getFinancialYearByTaxperiod(String taxPeriod) {

		String subMonth = taxPeriod.substring(0, 2);
		String subYear = taxPeriod.substring(2, 6);
		int year = Integer.valueOf(subYear);
		int month = Integer.valueOf(subMonth);
		String finYear = null;

		if (month < 4) {
			finYear = (year - 1) + "-" + String.valueOf(year).substring(2);
		} else {
			finYear = year + "-" + String.valueOf(year + 1).substring(2);
		}
		return finYear;
	}

	// Generating TaxPerid in 032019 format from Fy 2021-2022.
	public static String getFinancialPeriodFromFY(String fy) {
		String[] fyArr = fy.split("-");
		String taxPeriod = "03" + fyArr[1];
		return taxPeriod;
	}

	public static String getFormattedFy(String fy) {
		String[] fyArr = fy.split("-");
		if (fyArr.length < 2)
			throw new AppException("Invalid FY");
		String fyNew = fyArr[0] + "-20" + fyArr[1];
		return fyNew;
	}

	public static Integer convertFytoIntFromReturnPeriod(String retPeriod) {
		String fy = GenUtil.getFinancialYearByTaxperiod(retPeriod);
		Integer convertedFy = Integer.parseInt(fy.replace("-", ""));
		return convertedFy;
	}

	public static String convertClobtoString(Clob data) {
		String w = null;
		try {
			Reader charStream = data.getCharacterStream();
			w = IOUtils.toString(charStream);
			charStream.close();
		} catch (Exception e) {
			String msg = "Exception occured while converting clob to String";
			LOGGER.error(msg, e);
		}
		return w;
	}

	public static Clob convertStringToClob(String data) {
		Clob responseClob = null;
		try {
			if (!Strings.isNullOrEmpty(data)) {
				responseClob = new javax.sql.rowset.serial.SerialClob(
						data.toCharArray());
			}

		} catch (Exception e) {

			String msg = "Exception occured while converting String to Clob";
			LOGGER.error(msg);
		}
		return responseClob;
	}

	public static Integer getDerivedTaxPeriod(String taxPeriod) {
		try {
			return Integer.valueOf(
					taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));
		} catch (Exception e) {
			LOGGER.error("Exception while converting {} ", e);
			return 0;
		}
	}
	
	// This method return taxPeriod in MMYYYY format
	// for example if we are in june 2021 below method returns 052021
	public static String getCurrentTaxPeriod() {
		LocalDate currDate = LocalDate.now();
		int month = currDate.getMonthValue();
		int year = currDate.getYear();
		if (month == 1)
			return String.format("%s%s", 12, year);
		else
			return String.format("%02d%s", month - 1, year);
	}

	public static Pair<String, String> getCurrentAndPrevTaxPeriod() {
		LocalDate currDate = LocalDate.now();
		int month = currDate.getMonthValue();
		int year = currDate.getYear();
		String currentTaxPeriod = String.format("%02d%s", month, year);
		String prevTaxPeriod = null;
		if (month == 1)
			prevTaxPeriod = String.format("%s%s", 12, year - 1);
		else
			prevTaxPeriod = String.format("%02d%s", month - 1, year);

		return new Pair<String, String>(currentTaxPeriod, prevTaxPeriod);

	}

	public static Pair<String, String> getFrmDtToDtFromRetPeriod(
			String retPeriod) {

		String toDate = null;

		LocalDate today = LocalDate.now();
		int currentMonth = today.getMonthValue();
		int currentYear = today.getYear();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");
		DateTimeFormatter gstnFormatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy");
		YearMonth ym = YearMonth.parse(retPeriod, formatter);
		int returnPeriodMonth = ym.getMonthValue();
		int returnPeriodYear = ym.getYear();
		String fromDate = ym.atDay(1).format(gstnFormatter);
		if (currentMonth == returnPeriodMonth
				&& currentYear == returnPeriodYear) {
			LOGGER.debug("Current Date and UI are Equal");
			toDate = today.format(gstnFormatter);
		} else {
			toDate = ym.atEndOfMonth().format(gstnFormatter);
		}

		LOGGER.debug("From Date {}, To Date {} for Return Period", fromDate,
				toDate, retPeriod);
		return new Pair<String, String>(fromDate, toDate);
	}

	public static BigDecimal defaultToZeroIfNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	public static List<String> listOfPrevtaxPeriodForDate(LocalDate date,
			int noOfTaxPeriods) {

		List<String> taxPeriods = new ArrayList<>();
		int month = date.getMonthValue();
		int year = date.getYear();
		taxPeriods.add(String.format("%02d%s", month, year));
		noOfTaxPeriods--;

		while (noOfTaxPeriods > 0) {

			if (month == 1)
				taxPeriods.add(String.format("%s%s", 12, year - 1));
			else
				taxPeriods.add(String.format("%02d%s", month - 1, year));
			month--;
			noOfTaxPeriods--;
		}

		return taxPeriods;
	}

	public static List<String> listOfPrevtaxPeriod(int noOfTaxPeriods) {

		List<String> taxPeriods = new ArrayList<>();
		LocalDate currDate = LocalDate.now();
		int month = currDate.getMonthValue();
		int year = currDate.getYear();
		taxPeriods.add(String.format("%02d%s", month, year));
		noOfTaxPeriods--;

		while (noOfTaxPeriods > 0) {

			if (month == 1)
				taxPeriods.add(String.format("%s%s", 12, year - 1));
			else
				taxPeriods.add(String.format("%02d%s", month - 1, year));
			month--;
			noOfTaxPeriods--;
		}

		return taxPeriods;
	}

	public static List<String> listOfPrevTaxPeriodTillCurrentFy() {

		List<String> taxPeriods = new ArrayList<>();
		LocalDate currDate = LocalDate.now();
		int month = currDate.getMonthValue();
		int year = currDate.getYear();

		while (month != 3) {

			if (month == 0) {
				taxPeriods.add(String.format("%s%s", 12, year - 1));
				month = 11;
				year = year - 1;
			} else
				taxPeriods.add(String.format("%02d%s", month--, year));
		}
		return taxPeriods;
	}

	// This method will give the month in words-- Method expects 01,02...
	public static String getMonthinWordsFromNumber(String monthInNumb) {

		try {
			String month = Month.of(Integer.valueOf(monthInNumb)).name();
			String monthInWords = Month.valueOf(month)
					.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

			return monthInWords;
		} catch (Exception ex) {
			String msg = String.format("Error while parsing the taxPeriod");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}

	}

	// This Method will return if the taxPeriod is Valid or InValid for
	// CurrentFy. This Accepts taxperiod as input based on that taxperiod,
	// we will derive the fy, compare it with current fy.
	// If it is satisfies we will extract all the taxperiod of currentFy.
	// If the taxperiod matches the currentMonthYear then we will break it.
	// Will check and return ValidTaxPeriod List contains the Input TaxPerid.
	public static boolean isValidTaxPeriodForCurrentFy(String taxPeriod) {

		boolean isEligible = false;

		YearMonth currentYrMn = YearMonth.now();
		String curTaxPeriod = currentYrMn.format(formatter);

		Integer derivedCurTaxPeriod = getDerivedTaxPeriod(curTaxPeriod);
		Integer derivedtaxPeriod = getDerivedTaxPeriod(taxPeriod);

		if (derivedtaxPeriod <= derivedCurTaxPeriod)
			isEligible = true;

		return isEligible;
	}

	// This Method will return if the Quarter is Valid or InValid for
	// CurrentFy. This Accepts Itc04 taxperiod as input based on that Itc04
	// taxperiod,
	// we will derive the fy, compare it with current fy.
	// If it is satisfies we will extract all the Itc04 taxperiod of currentFy.
	// If the Itc04 taxperiod matches the currentMonthYear then we will break
	// it.
	// Will check and return ValidTaxPeriod List contains the Input Itc04
	// TaxPerid.
	public static boolean isValidQuarterForCurrentFy(String taxPeriod) {

		boolean isEligible = false;
		int fy = Integer.parseInt(taxPeriod.substring(2));
		YearMonth currentYrMn = YearMonth.now();
		String curTaxPeriod = currentYrMn.format(formatter);
		if (fy <= 2021) {
			if (currentYrMn.getMonthValue() == 4
					|| currentYrMn.getMonthValue() == 5
					|| currentYrMn.getMonthValue() == 6) {
				curTaxPeriod = "13".concat(curTaxPeriod.substring(2));
			} else if (currentYrMn.getMonthValue() == 7
					|| currentYrMn.getMonthValue() == 8
					|| currentYrMn.getMonthValue() == 9) {
				curTaxPeriod = "14".concat(curTaxPeriod.substring(2));
			} else {
				if (fy < 2021) {
					if (currentYrMn.getMonthValue() == 10
							|| currentYrMn.getMonthValue() == 11
							|| currentYrMn.getMonthValue() == 12) {
						curTaxPeriod = "15".concat(curTaxPeriod.substring(2));
					} else if (currentYrMn.getMonthValue() == 1
							|| currentYrMn.getMonthValue() == 2
							|| currentYrMn.getMonthValue() == 3) {
						curTaxPeriod = currentYrMn.minusYears(1)
								.format(formatter);
						curTaxPeriod = "16".concat(curTaxPeriod.substring(2));
					}
				} else {
					curTaxPeriod = "18".concat(curTaxPeriod.substring(2));
				}
			}
		} else {
			if (currentYrMn.getMonthValue() == 4
					|| currentYrMn.getMonthValue() == 5
					|| currentYrMn.getMonthValue() == 6
					|| currentYrMn.getMonthValue() == 7
					|| currentYrMn.getMonthValue() == 8
					|| currentYrMn.getMonthValue() == 9) {
				curTaxPeriod = "17".concat(curTaxPeriod.substring(2));
			} else if (currentYrMn.getMonthValue() == 10
					|| currentYrMn.getMonthValue() == 11
					|| currentYrMn.getMonthValue() == 12
					|| currentYrMn.getMonthValue() == 1
					|| currentYrMn.getMonthValue() == 2
					|| currentYrMn.getMonthValue() == 3) {
				curTaxPeriod = "18".concat(curTaxPeriod.substring(2));
			}
		}

		Integer derivedCurTaxPeriod = getDerivedTaxPeriod(curTaxPeriod);
		Integer derivedtaxPeriod = getDerivedTaxPeriod(taxPeriod);

		if (derivedtaxPeriod <= derivedCurTaxPeriod)
			isEligible = true;

		return isEligible;
	}

	// It takes taxPreiod as "mmyyyy" and returns yyyymm in integer format
	public static Integer getReturnPeriodFromTaxPeriod(String taxPeriod) {
		return Integer.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));
	}

	// It takes YYYY and returns YYYY-YYYY
	// Example: 2017 and "2017-2018"
	public static String getDerivedFyFromFy(String fy) {
		Integer prevFy = Integer.valueOf(fy);
		Integer currFy = prevFy + 1;
		return prevFy.toString() + "-" + currFy.toString();
	}

	public static List<String> getRegTypesBasedOnTypeForACD(String returnType) {

		if (returnType.equalsIgnoreCase("GSTR7")) {
			return ImmutableList.of("TDS");
		} else if (returnType.equalsIgnoreCase("GSTR6")) {
			return ImmutableList.of("ISD");
		} else if (returnType.equalsIgnoreCase("GSTR8")) {
			return ImmutableList.of("TCS");
		} else {
			return ImmutableList.of("REGULAR", "SEZ", "SEZU", "SEZD");
		}
	}

	public static String getMimeType(InputStream is) {
		try {

			Tika tika = new Tika();
			String mimeType = tika.detect(is);

			return mimeType;
		} catch (Exception e) {
			LOGGER.error("Exeption while detecting the mimeType", e);
			return null;
		}

	}

	public static String getFileExt(MultipartFile file) {
		try {

			String uploadedFileName = file.getOriginalFilename();

			String ext = uploadedFileName
					.substring(uploadedFileName.lastIndexOf(".") + 1);

			return ext;
		} catch (Exception e) {
			LOGGER.error("Exeption while detecting Extension", e);
			return null;
		}

	}

	public static BigDecimal roundOffTheAmount(BigDecimal val) {
		return val.setScale(0, BigDecimal.ROUND_HALF_UP);
	}

	public static String uploadFile(MultipartFile inputfile, File tempDir,
			String folderName) {
		try {
			String tempFileName = tempDir.getAbsolutePath() + File.separator
					+ inputfile.getOriginalFilename();

			File tempFile = new File(tempFileName);
			inputfile.transferTo(tempFile);

			LOGGER.debug("Transferred Successfully");

			String uploadedDocName = DocumentUtility.uploadZipFile(tempFile,
					folderName);
			return uploadedDocName;
		} catch (Exception e) {
			String msg = String.format(
					"Error While Upload the File to DocRepo %s",
					inputfile.getOriginalFilename());
			LOGGER.error(msg);
			throw new AppException(msg);
		}
	}

	public static List<String> deriveTaxPeriodsGivenFromAndToPeriod(
			String fromPeriod, String toPeriod) {
		List<String> taxPeriods = new ArrayList<>();

		// Parse input strings to YearMonth
		YearMonth fromYearMonth = YearMonth.parse(fromPeriod,
				DateTimeFormatter.ofPattern("MMyyyy"));
		YearMonth toYearMonth = YearMonth.parse(toPeriod,
				DateTimeFormatter.ofPattern("MMyyyy"));

		// Iterate through the range and add each tax period to the list
		while (!fromYearMonth.isAfter(toYearMonth)) {
			taxPeriods.add(fromYearMonth
					.format(DateTimeFormatter.ofPattern("MMyyyy")));
			fromYearMonth = fromYearMonth.plusMonths(1);
		}

		return taxPeriods;
	}

	// It will return year division Q1,Q2,Q3,Q4,H1,H2 for ITC04 for given
	// taxperiod
	public static String getYearDivisonItcO4(String taxPeriod) {
		int finYear = Integer.parseInt(taxPeriod.substring(2, 6));
		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		String yearDivison = "";
		if (finYear < 2021) {
			if (month <= 6 && month >= 4) {
				yearDivison = "Q1";
			} else if (month <= 9 && month >= 7) {
				yearDivison = "Q2";
			} else if (month <= 12 && month >= 10) {
				yearDivison = "Q3";
			} else if (month <= 3 && month >= 1) {
				yearDivison = "Q4";
			}
		} else if (finYear == 2021) {
			if (month <= 6 && month >= 4) {
				yearDivison = "Q1";
			} else if (month <= 9 && month >= 7) {
				yearDivison = "Q2";
			} else if (month <= 3 && month >= 1) {
				yearDivison = "Q4";
			} else {
				yearDivison = "H2";
			}
		} else {
			if (month <= 9 && month >= 4) {
				yearDivison = "H1";
			} else {
				yearDivison = "H2";
			}
		}

		return yearDivison;
	}

	private static Map<String, Integer> getYearDivisionMapItc04() {
		HashMap<String, Integer> yrDivMap = new HashMap<String, Integer>();

		yrDivMap.put("Q1", 13);
		yrDivMap.put("Q2", 14);
		yrDivMap.put("Q3", 15);
		yrDivMap.put("Q4", 16);
		yrDivMap.put("H1", 17);
		yrDivMap.put("H2", 18);
		return yrDivMap;
	}

	// It will return ITC04 Tax period for given taxperiod MMYYYY
	public static String getITC04TaxPeriod(String taxPeriod) {
		String yearDivison = "";
		int monthDivison = 0;

		Map<String, Integer> yrDivMap = getYearDivisionMapItc04();

		int finYear = Integer.parseInt(taxPeriod.substring(2, 6));
		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		yearDivison = getYearDivisonItcO4(taxPeriod);

		if (month < 4)
			finYear = finYear - 1;

		if (yrDivMap.containsKey(yearDivison))
			monthDivison = yrDivMap.get(yearDivison);

		return String.valueOf(monthDivison) + String.valueOf(finYear);
	}

	// Financial year should be in YYYY-YY format like 2017-18
	public static Pair<String, String> extractFromAndToTaxPeriodsFromFY(
			String finYear) {
		int financialYear = Integer.parseInt(finYear.substring(0, 4));

		String fromRetPeriod = "04" + financialYear;
		String toRetPeriod = "03" + (financialYear + 1);

		return new Pair<String, String>(fromRetPeriod, toRetPeriod);
	}

	// Financial year should be in YYYY-YY format like 2017-18
	public static Pair<String, String> extractFromAndToRetTaxPeriodsFromFY(
			String finYear) {
		int financialYear = Integer.parseInt(finYear.substring(0, 4));

		String fromRetPeriod = financialYear + "04";
		String toRetPeriod = (financialYear + 1) + "03";

		return new Pair<String, String>(fromRetPeriod, toRetPeriod);
	}
	

	public static Pair<Integer, Integer> getDerivedTaxPeriodsBasedOnMonthAndFY(
			List<String> months, String finYear) {

		int financialYear = Integer.parseInt(finYear.substring(0, 4));
		ArrayList<Integer> integerList = null;
		ArrayList<Integer> taxPeriods = null;
		integerList = months.stream().map(Integer::valueOf)
				.collect(Collectors.toCollection(ArrayList::new));

		System.out.println("FY : " + financialYear);

		taxPeriods = integerList.stream().map(month -> {
			if (month < 4) {
				return Integer.parseInt((financialYear + 1) + "0" + month);
			} else if (month < 10) {
				return Integer.parseInt(financialYear + "0" + month);
			} else {
				return Integer.parseInt(financialYear + "" + month);
			}
		}).collect(Collectors.toCollection(ArrayList::new));
		Collections.sort(taxPeriods);
		return new Pair<Integer, Integer>(taxPeriods.get(0),
				taxPeriods.get(taxPeriods.size() - 1));
	}
	
	public static ArrayList<String> getTaxPeriodsBasedOnMonthAndFY(
			List<String> months, String finYear) {

		int financialYear = Integer.parseInt(finYear.substring(0, 4));
		ArrayList<Integer> integerList = null;
		ArrayList<String> taxPeriods = null;
		integerList = months.stream().map(Integer::valueOf)
				.collect(Collectors.toCollection(ArrayList::new));

		taxPeriods = integerList.stream().map(month -> {
			if (month < 4) {
				return "0" + month + (financialYear + 1);
			} else if (month < 10) {
				return "0" + month + financialYear;
			} else {
				return "" + month + financialYear;
			}
		}).collect(Collectors.toCollection(ArrayList::new));

		return taxPeriods;
	}

	public static String getReturnType(ProcessingContext context) {
		String returnType = (String) context
				.getAttribute(APIConstants.RETURN_TYPE_STR);

		return Strings.isNullOrEmpty(returnType)
				? APIConstants.GSTR1.toUpperCase() : returnType;

	} 
	
	 public static String formatCurrency(BigDecimal value) {   
		 if (value != null) {
         String valueStr = value.toString();

         try {
             // Parse the input to a number
             double numValue = Double.parseDouble(valueStr);
             if (numValue != 0) {
                 boolean isNegative = numValue < 0;
                 String numberPart = valueStr.startsWith("-") ? valueStr.substring(1) : valueStr;

                 // Split integer and decimal parts
                 String[] parts = numberPart.split("\\.");
                 String integerPart = parts[0];
                 String decimalPart = parts.length > 1 ? parts[1] : null;

                 // Format the integer part using Indian numbering system
                 int length = integerPart.length();
                 String lastThree = integerPart.substring(length - Math.min(length, 3));
                 String remaining = length > 3 ? integerPart.substring(0, length - 3) : "";

                 if (!remaining.isEmpty()) {
                     remaining = remaining.replaceAll("(\\d)(?=(\\d{2})+$)", "$1,");
                 }

                 String formattedValue = (isNegative ? "-" : "") + remaining + (remaining.isEmpty() ? "" : ",") + lastThree;
                 if (decimalPart != null) {
                     formattedValue += "." + decimalPart;
                 }

                 return formattedValue;
             }
         } catch (NumberFormatException e) {
             // If value cannot be parsed, return as is
             return valueStr;
         }
     }
     return String.valueOf(value);
     
	 }
	 
	 public String convertClobToString(Clob clob) {
		    if (clob == null) {
		        return null;
		    }
		    try (Reader reader = clob.getCharacterStream();
		         BufferedReader bufferedReader = new BufferedReader(reader)) {
		        StringBuilder sb = new StringBuilder();
		        String line;
		        while ((line = bufferedReader.readLine()) != null) {
		            sb.append(line);
		        }
		        return sb.toString();
		    } catch (SQLException | IOException e) {
		        e.printStackTrace();
		        return null;
		    }
		}
	 public static BigInteger getBigInteger(Object arr) {
			if (arr == null) {
				return BigInteger.ZERO;
			} else {
				if (arr instanceof BigInteger) {
					return (BigInteger) arr;
				} else if (arr instanceof Long) {
					return BigInteger.valueOf((Long) arr);
				} else if (arr instanceof Integer) {
					return BigInteger.valueOf((Integer) arr);
				} else {
					return BigInteger.ZERO;
				}
			}
		}
	 public static List<String> getRegTypesBasedOnTypeForACDIms() {

			return ImmutableList.of("REGULAR", "SEZ", "SEZU", "SEZD");

		}
	 
	 
	 public static File createTempDir() throws IOException {
			return Files.createTempDirectory("FileHashFolder").toFile();
		}
	 
	 public static String processAmouts(String amt) {

			if (!Strings.isNullOrEmpty(amt) && !(amt.equals("0")
					|| amt.equals("0.0") || amt.equals("0.00"))) {
				// Check if the string is a valid number
				try {
					// Try parsing the amt as a number
					new BigDecimal(amt); // If valid number, this won't throw an
											// exception
					return "'" + new BigDecimal(amt).toPlainString(); // Avoid
																		// scientific
																		// notation
				} catch (NumberFormatException e) {
					// If it throws an exception, it's not a number, so return as is
					return amt;
				}
			}
			return amt; // Return amt if empty or is 0 or 0.0 or 0.00
		}
	 
	 public static String trimString(String value, int lastIndex) {
			try {
				if (value == null) {
					LOGGER.error("Input string cannot be null");
				}
				if (lastIndex < 0 || lastIndex > value.length()) {
					LOGGER.error("Invalid last index");
					lastIndex = Math.min(value.length(), Math.max(0, lastIndex));
				}
				return value.substring(0, lastIndex);
			} catch (Exception e) {
				LOGGER.error("Exception while triming the String {} ", e);
				return value;
			}
		}
	
}
