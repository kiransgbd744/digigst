package com.ey.advisory.app.services.docs.gstr2;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * This class is responsible for Building a unique document key which is a
 * combination of Doc No, Doc Type, Doc Date,Supplier GSTIN,Recipient GSTIN
 * 115 DFS
 * @author Anand3.M
 *
 */
@Slf4j
public class InwardDocumentKeyBuilder implements DataBlockKeyBuilder<String> {

	private static final FastDateFormat DATE_FORMAT = FastDateFormat
			.getInstance("yyyyMMdd");

	private static final FastDateFormat SUPPORTED_DATE_FORMAT1 = FastDateFormat
			.getInstance("yyyy-MM-dd");

	private static final FastDateFormat SUPPORTED_DATE_FORMAT2 = FastDateFormat
			.getInstance("dd-MM-yyyy");

	private static final FastDateFormat SUPPORTED_DATE_FORMAT3 = FastDateFormat
			.getInstance("yyyy/MM/dd");

	private static final FastDateFormat SUPPORTED_DATE_FORMAT4 = FastDateFormat
			.getInstance("dd/MM/yyyy");

	private static final FastDateFormat SUPPORTED_DATE_FORMAT5 = FastDateFormat
			.getInstance("yyyy-MM-dd'T'HH:mm:ss");

	private static final FastDateFormat[] FAST_DATE_FORMATS = {
			SUPPORTED_DATE_FORMAT1, SUPPORTED_DATE_FORMAT2,
			SUPPORTED_DATE_FORMAT3, SUPPORTED_DATE_FORMAT4,
			SUPPORTED_DATE_FORMAT5 };

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String buildDataBlockKey(Object[] arr, TabularDataLayout config) {

		String docNo = (arr[24] != null && !arr[24].toString().isEmpty())
				? String.valueOf(arr[24]).trim() : "";

		String docType = (arr[22] != null && !arr[22].toString().isEmpty())
				? String.valueOf(arr[22]).trim() : "";
		Object docDate = (arr[25] != null && !arr[25].toString().isEmpty())
				? arr[25] : "";

		String sgstin = (arr[30] != null && !arr[30].toString().isEmpty())
				? String.valueOf(arr[30]).trim() : "";
		String cgstin = (arr[21] != null && !arr[21].toString().isEmpty())
				? String.valueOf(arr[21]).trim() : "";

		String finYear = "";

		LocalDate localDocDate = DateUtil.parseObjToDate(docDate);
		finYear = GenUtil.getFinYear(localDocDate);

		// Inward Invoice Key Format - FY |RGSTIN|SGSTIN |DocType | DocNo
		// New Invoice key - If SGSTIN is blank then supplier name to take
		// only first 25 characters

		if (!Strings.isNullOrEmpty(sgstin)) {
			return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
					.add(sgstin).add(docType).add(docNo).toString();
		} else {
			String suppName = (arr[34] != null && !arr[34].toString().isEmpty())
					? StringUtils.truncate(String.valueOf(arr[34]).trim(), 25)
					: "";
			return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
					.add(suppName).add(docType).add(docNo).toString();
		}
	}

	public LocalDate convertToLocalDate(Object docDate) {
		if (docDate == null)
			return null;

		if (docDate instanceof com.aspose.cells.DateTime) {
			Date date = ((com.aspose.cells.DateTime) docDate).toDate();
			return GenUtil.toLocalDate(date);
		}

		if (docDate instanceof Date) {
			return GenUtil.toLocalDate((Date) docDate);
		}

		if (docDate instanceof LocalDate)
			return (LocalDate) docDate;

		if (docDate instanceof LocalDateTime)
			return ((LocalDateTime) docDate).toLocalDate();

		if (docDate instanceof String) {
			for (FastDateFormat format : FAST_DATE_FORMATS) {
				Date date = tryParsingToDate((String) docDate, format);
				if (date != null)
					return GenUtil.toLocalDate(date);
				// else
				// return null;
			}
		}

		// if we are not able to parse using any pre-defined formats return
		// null
		return null;
	}

	public String convertDateToStr(Object docDate) {

		if (docDate == null) {
			return "";
		}

		if (docDate instanceof com.aspose.cells.DateTime) {
			Date date = ((com.aspose.cells.DateTime) docDate).toDate();
			return DATE_FORMAT.format(date);
		}

		if (docDate instanceof Date) {
			return DATE_FORMAT.format(docDate);
		}

		if (docDate instanceof String) {

			for (FastDateFormat format : FAST_DATE_FORMATS) {
				String date = tryConvertUsingFormat((String) docDate, format);
				if (date != null)
					return date;
			}

			// if we are not able to format using any pre defined formats return
			// the input string
			return (String) docDate;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"docDate is not instance of String or Util Date Aspose Date");
		}
		return docDate.toString();
	}

	private Date tryParsingToDate(String docDate, FastDateFormat dateFormat) {
		try {
			return dateFormat.parse(docDate);
		} catch (ParseException e) {
			LOGGER.error("Parse Exception of date " + e.getMessage());
			return null;
		}
	}

	private String tryConvertUsingFormat(String docDate,
			FastDateFormat dateFormat) {
		try {
			Date date1 = dateFormat.parse(docDate);
			return DATE_FORMAT.format(date1);
		} catch (ParseException e) {
			LOGGER.error("Parse Exception of date " + e.getMessage());
			return null;
		}
	}

	@Override
	public String buildComprehenceDataBlockKey(Object[] arr,
			TabularDataLayout config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildItc04DataBlockKey(Object[] arr,
			TabularDataLayout layout) {
		// TODO Auto-generated method stub
		return null;
	}

}