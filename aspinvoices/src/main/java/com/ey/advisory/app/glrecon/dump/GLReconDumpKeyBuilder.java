package com.ey.advisory.app.glrecon.dump;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.commons.lang3.time.FastDateFormat;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for Building a unique document key which is a
 * combination of Doc No, Doc Type, Doc Date,Supplier GSTIN
 * 
 * @author Shashikant.Shukla
 *
 */
@Slf4j
public class GLReconDumpKeyBuilder implements DataBlockKeyBuilder<String> {

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
		String transactionType = (arr[0] != null && !arr[0].toString().isEmpty())
				? String.valueOf((arr[0])).trim() : "";

		String accountingVoucherNumber = (arr[11] != null && !arr[11].toString().isEmpty())
				? String.valueOf((arr[11])).trim() : "";

		String accountingVoucherDate = (arr[12] != null && !arr[12].toString().isEmpty())
				?String.valueOf(arr[12]).trim() : "";


//		LocalDate localDocDate = DateUtil.parseObjToDate(documentDate);
		return new StringJoiner(DOC_KEY_JOINER).add(transactionType).add(accountingVoucherNumber)
				.add(accountingVoucherDate).toString();
	}

	@Override
	public String buildComprehenceDataBlockKey(Object[] arr,
			TabularDataLayout config) {
		
		arr[0] = CommonUtility.singleQuoteCheck(arr[0]);
		arr[11] = CommonUtility.singleQuoteCheck(arr[11]);
		arr[12] = CommonUtility.singleQuoteCheck(arr[12]);
		
		String transactionType = (arr[0] != null && !arr[0].toString().isEmpty())
				? String.valueOf((arr[0])).trim() : "";

		String accountingVoucherNumber = (arr[11] != null && !arr[11].toString().isEmpty())
				? String.valueOf((arr[11])).trim() : "";

		String accountingVoucherDate = (arr[12] != null && !arr[12].toString().isEmpty())
				?String.valueOf(arr[12]).trim() : "";


//		LocalDate localDocDate = DateUtil.parseObjToDate(documentDate);
		return new StringJoiner(DOC_KEY_JOINER).add(transactionType).add(accountingVoucherNumber)
				.add(accountingVoucherDate).toString();
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
				else
					return null;
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

	

	private String GstinOrStateCode(String jwStateCode, String jwStateGstin) {

		if (jwStateCode != null && jwStateGstin != null)
			return jwStateGstin;
		if (jwStateGstin != null)
			return jwStateGstin;
		if (jwStateCode != null)
			return jwStateCode;

		return null;

	}

	@Override
	public String buildItc04DataBlockKey(Object[] arr,
			TabularDataLayout layout) {
		// TODO Auto-generated method stub
		return null;
	}
}
