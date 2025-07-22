package com.ey.advisory.app.services.docs;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

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
public class Gstr1HsnDocumentKeyBuilder implements DataBlockKeyBuilder<String> {

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
		String supplierGstin = (arr[1] != null && !arr[1].toString().isEmpty())
				? String.valueOf((arr[1])).trim()
				: "";

		String taxPeriod = (arr[2] != null && !arr[2].toString().isEmpty())
				? GenUtil.getDerivedTaxPeriod(String.valueOf((arr[2])).trim())
						.toString().trim()
				: "";

		String recordType = (arr[3] != null && !arr[3].toString().isEmpty())
				? String.valueOf(arr[3]).trim()
				: "";

		String hsnOrSac = (arr[4] != null && !arr[4].toString().isEmpty())
				? String.valueOf(arr[4]).trim()
				: "";
		String taxRate = (arr[8] != null && !arr[8].toString().isEmpty())
				? String.valueOf(arr[8]).trim()
				: "";
		String uqc = (arr[6] != null && !arr[6].toString().isEmpty())
				? String.valueOf(arr[6]).trim()
				: "";

		return new StringJoiner(DOC_KEY_JOINER).add(supplierGstin)
				.add(taxPeriod).add(recordType).add(hsnOrSac).add(uqc)
				.add(taxRate).toString();
	}

	@Override
	public String buildComprehenceDataBlockKey(Object[] arr,
			TabularDataLayout config) {
		String supplierGstin = (arr[1] != null && !arr[1].toString().isEmpty())
				? String.valueOf((arr[1])).trim()
				: "";

		String taxPeriod;
		if (arr[2] != null && !arr[2].toString().isEmpty()) {
			taxPeriod = arr[2].toString().trim();
			if (StringUtils.isNumeric(taxPeriod) && taxPeriod.length() == 6) {
				taxPeriod = GenUtil
						.getDerivedTaxPeriod(String.valueOf((arr[2])).trim())
						.toString().trim();
			}
		} else {
			taxPeriod = null;
		}

		String recordType = (arr[3] != null && !arr[3].toString().isEmpty())
				? String.valueOf(arr[3]).trim()
				: "";

		String hsnOrSac = (arr[4] != null && !arr[4].toString().isEmpty())
				? String.valueOf(arr[4]).trim()
				: "";
		String taxRate = (arr[8] != null && !arr[8].toString().isEmpty())
				? String.valueOf(arr[8]).trim()
				: "";
		String uqc = (arr[6] != null && !arr[6].toString().isEmpty())
				? String.valueOf(arr[6]).trim()
				: "";

		return new StringJoiner(DOC_KEY_JOINER).add(supplierGstin)
				.add(taxPeriod).add(recordType).add(hsnOrSac).add(uqc)
				.add(taxRate).toString();
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
