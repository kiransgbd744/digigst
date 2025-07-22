package com.ey.advisory.app.report.convertor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.gstr7trans.Gstr7TransDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr7TransRSReportConvertor")
public class Gstr7TransRSReportConvertor implements ReportConvertor {

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public Object convert(Object[] arr, String reportType) {

		Gstr7TransDto obj = new Gstr7TransDto();
		LocalDate parsedDate = null;
		LocalDateTime parsedDateTime = null;
		String date = "";

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr7TransRSReportConvertor reportType %s ",
						reportType);
				LOGGER.debug(msg);

				LOGGER.debug("arr length : " + arr.length);
			}

			obj.setSourceIdentifier(arr[0] != null ? arr[0].toString() : null);
			obj.setSourcFilename(arr[1] != null ? arr[1].toString() : null);
			obj.setGlAccountCode(arr[2] != null ? arr[2].toString() : null);
			obj.setDivision(arr[3] != null ? arr[3].toString() : null);
			obj.setSubDivision(arr[4] != null ? arr[4].toString() : null);
			obj.setProfitCentre1(arr[5] != null ? arr[5].toString() : null);
			obj.setProfitCentre2(arr[6] != null ? arr[6].toString() : null);
			obj.setPlantCode(arr[7] != null ? arr[7].toString() : null);
			obj.setReturnPeriod(
					arr[8] != null ? DownloadReportsConstant.CSVCHARACTER
							.concat(arr[8].toString()) : null);
			obj.setDocType(arr[9] != null ? arr[9].toString() : null);
			obj.setSupplyType(arr[10] != null ? arr[10].toString() : null);
			obj.setDeductorGstin(arr[11] != null ? arr[11].toString() : null);
			obj.setDocNum(
					arr[12] != null ? DownloadReportsConstant.CSVCHARACTER
							.concat(arr[12].toString()) : null);

			if (arr[13] != null) {
				String strdate = arr[13].toString();
				strdate = strdate.substring(0, 10);

				parsedDate = DateUtil.parseObjToDate(strdate);

				date = EYDateUtil.fmtLocalDate(parsedDate);

				obj.setDocDate(
						DownloadReportsConstant.CSVCHARACTER.concat(date));
			} else {
				obj.setDocDate(null);
			}

			obj.setOriginalDocNum(
					arr[14] != null ? DownloadReportsConstant.CSVCHARACTER
							.concat(arr[14].toString()) : null);

			if (arr[15] != null) {
				String strdate = arr[15].toString();
				strdate = strdate.substring(0, 10);
				parsedDate = DateUtil.parseObjToDate(strdate);
				date = EYDateUtil.fmtLocalDate(parsedDate);

				obj.setOriginalDocDate(
						DownloadReportsConstant.CSVCHARACTER.concat(date));
			} else {
				obj.setOriginalDocDate(null);
			}

			obj.setDeducteeGstin(arr[16] != null ? arr[16].toString() : null);
			obj.setOriginalDeducteeGstin(
					arr[17] != null ? arr[17].toString() : null);

			obj.setOriginalReturnPeriod(
					arr[18] != null ? DownloadReportsConstant.CSVCHARACTER
							.concat(arr[18].toString()) : null);
			obj.setOriginalTaxableValue(arr[19] != null
					? GenUtil.processAmouts(arr[19].toString()) : null);
			obj.setOriginalInvoiceValue(arr[20] != null
			        ? GenUtil.processAmouts(arr[20].toString()) : null);
			obj.setLineItemNumber(arr[21] != null ? arr[21].toString() : null);
			obj.setTaxableValue(arr[22] != null
			        ? GenUtil.processAmouts(arr[22].toString()) : null);
			obj.setIgstAmt(arr[23] != null
			        ? GenUtil.processAmouts(arr[23].toString()) : null);
			obj.setCgstAmt(arr[24] != null
			        ? GenUtil.processAmouts(arr[24].toString()) : null);
			obj.setSgstAmt(arr[25] != null
			        ? GenUtil.processAmouts(arr[25].toString()) : null);
			obj.setInvoiceValue(arr[26] != null
			        ? GenUtil.processAmouts(arr[26].toString()) : null);
			obj.setContractNumber(arr[27] != null ? arr[27].toString() : null);

			if (arr[28] != null) {
			    String strdate = arr[28].toString();
			    strdate = strdate.substring(0, 10);
			    parsedDate = DateUtil.parseObjToDate(strdate);
			    date = EYDateUtil.fmtLocalDate(parsedDate);

			    obj.setContractDate(
			            DownloadReportsConstant.CSVCHARACTER.concat(date));
			} else {
			    obj.setContractDate(null);
			}

			obj.setContractValue(arr[29] != null
			        ? GenUtil.processAmouts(arr[29].toString()) : null);
			obj.setPaymentAdviceNumber(
			        arr[30] != null ? arr[30].toString() : null);

			if (arr[31] != null) {
			    String strdate = arr[31].toString();
			    strdate = strdate.substring(0, 10);
			    parsedDate = DateUtil.parseObjToDate(strdate);
			    date = EYDateUtil.fmtLocalDate(parsedDate);

			    obj.setPaymentAdviceDate(
			            DownloadReportsConstant.CSVCHARACTER.concat(date));
			} else {
			    obj.setPaymentAdviceDate(null);
			}

			obj.setUserdefinedField1(
			        arr[32] != null ? arr[32].toString() : null);
			obj.setUserdefinedField2(
			        arr[33] != null ? arr[33].toString() : null);
			obj.setUserdefinedField3(
			        arr[34] != null ? arr[34].toString() : null);
			obj.setUserdefinedField4(
			        arr[35] != null ? arr[35].toString() : null);
			obj.setUserdefinedField5(
			        arr[36] != null ? arr[36].toString() : null);
			obj.setUserdefinedField6(
			        arr[37] != null ? arr[37].toString() : null);
			obj.setUserdefinedField7(
			        arr[38] != null ? arr[38].toString() : null);
			obj.setUserdefinedField8(
			        arr[39] != null ? arr[39].toString() : null);
			obj.setUserdefinedField9(
			        arr[40] != null ? arr[40].toString() : null);
			obj.setUserdefinedField10(
			        arr[41] != null ? arr[41].toString() : null);
			obj.setUserdefinedField11(
			        arr[42] != null ? arr[42].toString() : null);
			obj.setUserdefinedField12(
			        arr[43] != null ? arr[43].toString() : null);
			obj.setUserdefinedField13(
			        arr[44] != null ? arr[44].toString() : null);
			obj.setUserdefinedField14(
			        arr[45] != null ? arr[45].toString() : null);
			obj.setUserdefinedField15(
			        arr[46] != null ? arr[46].toString() : null);


			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("ReportType : %s", reportType);
				LOGGER.debug(msg);
			}

			if (reportType
					.equalsIgnoreCase(DownloadReportsConstant.TOTALERECORDS)) {
				return obj;
			}

			if (reportType
					.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
				obj.setFileid(arr[47] != null ? arr[47].toString() : null);
				obj.setSection(arr[48] != null ? arr[48].toString() : null);
				logGstr7TransDto(obj);
				return obj;
			}

			if (reportType.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
				if (arr[47] != null) {
					String errCode = arr[47] != null ? arr[47].toString()
							: null;

					String[] parts = errCode.split(",");
					Set<String> errCodes = new LinkedHashSet<>();

					for (String part : parts) {
						errCodes.add(part.trim());
					}

					errCode = String.join(",", errCodes);

					obj.setErrorCode(errCode);

					String errorDesc = "";

					if (errCode != null) {
						List<String> errCodeList = new ArrayList<>(
								Arrays.asList(errCode.split(",")));
						errorDesc = ErrorMasterUtil.getErrorDesc(errCodeList,
								APIConstants.GSTR7.toUpperCase());
					}
					obj.setErrorDescription(errorDesc);

				}
				logGstr7TransDto(obj);
				return obj;
			}

			if (reportType.equalsIgnoreCase(
					DownloadReportsConstant.GSTR7ASUPLOADED)) {
				obj.setFileid(arr[47] != null ? arr[47].toString() : null);
				obj.setSection(arr[48] != null ? arr[48].toString() : null);
				logGstr7TransDto(obj);
				return obj;
			}

			if (reportType
					.equalsIgnoreCase(DownloadReportsConstant.GSTR7ASPERROR)) {
				// Conolidated DigiGST error Report
				obj.setSource(arr[47] != null ? arr[47].toString() : null);
				obj.setUserId(arr[48] != null ? arr[48].toString() : null);
				obj.setFileid(arr[49] != null ? arr[49].toString() : null);
				obj.setFileName(arr[50] != null ? arr[50].toString() : null);

				if (arr[51] != null) {
					String strdate = arr[51].toString().trim();
					
					parsedDateTime = DateUtil.stringToTime(strdate, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
					
					date = EYDateUtil.fmtDate3(parsedDateTime);
					
					obj.setUploadDataDateTime(
							DownloadReportsConstant.CSVCHARACTER.concat(date));
				} else {
					obj.setUploadDataDateTime(null);
				}

				if (arr[52] != null) {
					String errCode = arr[52] != null ? arr[52].toString()
							: null;
					
					String[] errparts = errCode.split(",");
					Set<String> errCodes = new LinkedHashSet<>();
					
					for (String part : errparts) {
						errCodes.add(part.trim());
					}

					errCode = String.join(",", errCodes);
					
					errparts = errCode.split(",");
					
					String errorDesc = "";
					if (errCode != null) {
						List<String> errCodeList = new ArrayList<>(
								Arrays.asList(errCode.split(",")));
						errorDesc = ErrorMasterUtil.getErrorDesc(errCodeList,
								APIConstants.GSTR7.toUpperCase());
					}
					String[] descparts = errorDesc.split(",");

			        StringBuilder aspErrorDesc = new StringBuilder();

			        for (int i = 0; i < errparts.length; i++) {
			        	aspErrorDesc.append(errparts[i]).append("-").append(descparts[i]);
			            if (i < errparts.length - 1) {
			            	aspErrorDesc.append(",");
			            }
			        }
					
//					obj.setAspError(errCode + "-" + errorDesc);
					obj.setAspError(aspErrorDesc.toString());
				}
				logGstr7TransDto(obj);
				return obj;

			}

			if (reportType
					.equalsIgnoreCase(DownloadReportsConstant.GSTR7GSTNERROR)) {
				// Conolidated GSTN error Report
				obj.setGstnStatus(arr[47] != null ? arr[47].toString() : null);
				obj.setGstnRefid(arr[48] != null ? arr[48].toString() : null);

				if (arr[49] != null) {
					String strdate = arr[49].toString();
					strdate = strdate.substring(0, 10);
					parsedDate = DateUtil.parseObjToDate(strdate);
					date = EYDateUtil.fmtLocalDate(parsedDate);

					obj.setGstnRefIdDateTime(
							DownloadReportsConstant.CSVCHARACTER.concat(date));
				} else {
					obj.setGstnRefIdDateTime(null);
				}

				obj.setGstnErrorCode(
						arr[50] != null ? arr[50].toString() : null);
				obj.setGstnErrorDescription(
						arr[51] != null ? arr[51].toString() : null);
				obj.setTableNumber(arr[52] != null ? arr[52].toString() : null);
				obj.setSource(arr[53] != null ? arr[53].toString() : null);
				obj.setUserId(arr[54] != null ? arr[54].toString() : null);
				obj.setFileid(arr[55] != null ? arr[55].toString() : null);
				obj.setFileName(arr[56] != null ? arr[56].toString() : null);

				if (arr[57] != null) {
					String strdate = arr[57].toString();
					strdate = strdate.substring(0, 10);
					parsedDate = DateUtil.parseObjToDate(strdate);
					date = EYDateUtil.fmtLocalDate(parsedDate);

					obj.setUploadDataDateTime(
							DownloadReportsConstant.CSVCHARACTER.concat(date));
				} else {
					obj.setUploadDataDateTime(null);
				}

				logGstr7TransDto(obj);
				return obj;

			}

		} catch (Exception ex) {
			String msg = String.format("Gstr7TransRSReportConvertor Error : ");
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
		return null;
	}

	private void logGstr7TransDto(Gstr7TransDto obj) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr7TransDto : " + obj.toString());
		}
	}

	public static void main(String[] args) {
		/*
		 * String errCodes="ER63004,ER63006";
		 * 
		 * String[] errCodesArr = errCodes.split(",");
		 * 
		 * StringBuilder sb = new StringBuilder();
		 * 
		 * for (String errCode : errCodesArr) { System.out.println(errCode);
		 * sb.append(errCode+" desc"+",");
		 * 
		 * } sb.deleteCharAt((sb.length()-1));
		 * System.out.println(sb.toString());
		 */

		/*
		 * 
		 * String strdate = "21-11-2024-123"; String strdate = "2024-11-21-123";
		 * strdate=strdate.substring(0,10);
		 * 
		 * System.out.println(strdate); System.out.println(strdate.length());
		 * 
		 * DateTimeFormatter f = new DateTimeFormatterBuilder()
		 * .appendPattern(OLDFARMATTER).toFormatter(); LocalDate parsedDate =
		 * LocalDate.parse(strdate, f); DateTimeFormatter f2 = DateTimeFormatter
		 * .ofPattern(NEWFARMATTER); String newDate = parsedDate.format(f2);
		 * 
		 * System.out.println(newDate);
		 */

		// String strdate = "21-11-2024";
		String strdate = "2024-11-21";

		// LocalDate parsedDate = DateUtil.parseObjToDate(strdate);
		//
		// String date = EYDateUtil.fmtLocalDate(parsedDate);
		// System.out.println(strdate);
		// System.out.println(date);

		// EYDateUtil.fmtLocalDate()
		//
		// System.out.println(EYDateUtil.fmtLocalDate(parseObjToDate(a)));

		/*
		 * 
		 * String strdate = "21-11-2024"; // String strdate = "2024-11-21";
		 * 
		 * 
		 * 
		 * // Regex patterns String pattern1 = "^\\d{4}-\\d{2}-\\d{2}$"; //
		 * "yyyy-MM-dd" String pattern2 = "^\\d{2}-\\d{2}-\\d{4}$"; //
		 * "dd-MM-yyyy";
		 * 
		 * // Output formatter DateTimeFormatter outputFormatter =
		 * DateTimeFormatter .ofPattern(NEWFARMATTER);
		 * 
		 * // Date formatter DateTimeFormatter f = null; LocalDate
		 * parsedDate=null; String newDate="";
		 * 
		 * 
		 * try {
		 * 
		 * // yyyy-MM-dd if (strdate.matches(pattern1)) { f= new
		 * DateTimeFormatterBuilder()
		 * .appendPattern(OLDFARMATTER).toFormatter();
		 * 
		 * parsedDate = LocalDate.parse(strdate, f);
		 * 
		 * newDate = parsedDate.format(outputFormatter);
		 * 
		 * 
		 * } else if (strdate.matches(pattern2)) { f= new
		 * DateTimeFormatterBuilder()
		 * .appendPattern(NEWFARMATTER).toFormatter(); parsedDate =
		 * LocalDate.parse(strdate, f);
		 * 
		 * newDate = parsedDate.format(outputFormatter);
		 * 
		 * } else { System.out.println("Unrecognized date format: " + strdate);
		 * return; }
		 * 
		 * // Print result System.out.println("Original: " + strdate);
		 * System.out.println("Formatted: " + newDate); System.out.println(); }
		 * catch (Exception ex) { System.out.println("Error parsing date: " +
		 * strdate);
		 * 
		 * String msg = String.format("Invalid Doc Date Error : ");
		 * LOGGER.error(msg, ex); }
		 * 
		 */
		
		/*

		String input = "ER63029,ER63030,ER63030,ER63035";
		// String input = "ER63029";

		// Split the string and add to a set to remove duplicates
		String[] parts = input.split(",");
		Set<String> unique = new LinkedHashSet<>(); // maintains insertion order

		for (String part : parts) {
			unique.add(part.trim());
		}

		// Join the unique elements back into a string
		String result = String.join(",", unique);

		System.out.println("Original: " + input);
		System.out.println("Without duplicates: " + result);
		*/
		
		/*
        String input = "Mon May 19 2025 06:10:59 GMT+0000 (UTC)";
        
        // Parser to match the input string format
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (z)", Locale.ENGLISH);
        
        // Desired output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        try {
            Date date = inputFormat.parse(input);
            String formattedDate = outputFormat.format(date);
            System.out.println("Formatted date: " + formattedDate);
        } catch (ParseException e) {
            System.err.println("Unable to parse the date: " + e.getMessage());
        }
        */
		
		
		
		String strdate1 = "19-05-2025 18:05:57";
		
		try {
			
			LocalDateTime dtTime = DateUtil.stringToTime(strdate1, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
			
			
			String fmtDate3 = EYDateUtil.fmtDate3(dtTime);
			
            System.out.println("Formatted date: " + fmtDate3);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		

	}

}
