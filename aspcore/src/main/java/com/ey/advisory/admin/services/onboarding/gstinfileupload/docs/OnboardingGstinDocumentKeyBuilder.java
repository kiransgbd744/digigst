package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.commons.lang3.time.FastDateFormat;

import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for Building a unique document key which is a
 * combination of Doc No, Doc Type, Doc Date,Supplier GSTIN
 * 
 * @author Mohana.Dasari
 *
 */
@Slf4j
public class OnboardingGstinDocumentKeyBuilder implements DataBlockKeyBuilder<String> {

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

	private static final FastDateFormat[] FAST_DATE_FORMATS = {
			SUPPORTED_DATE_FORMAT1, SUPPORTED_DATE_FORMAT2,
			SUPPORTED_DATE_FORMAT3, SUPPORTED_DATE_FORMAT4 };

	@Override
	public String buildDataBlockKey(Object[] arr, TabularDataLayout config) {

		String docNo = (arr[12] != null) ? (String.valueOf(arr[12])).trim()
				: "";

		String docType = (arr[10] != null) ? (arr[10]).toString().trim() : "";

		String docDate = (arr[13] != null) ? convertDateToStr(arr[13]) : "";

		String sgstin = (arr[9] != null) ? (arr[9]).toString().trim() : "";

		return new StringJoiner("|").add(docType).add(sgstin).add(docDate)
				.add(docNo).toString();
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
