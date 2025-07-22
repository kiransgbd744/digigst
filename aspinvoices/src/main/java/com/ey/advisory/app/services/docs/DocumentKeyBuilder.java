package com.ey.advisory.app.services.docs;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.commons.lang3.time.FastDateFormat;

import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
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
public class DocumentKeyBuilder implements DataBlockKeyBuilder<String> {

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
		String docNo = (arr[25] != null && !arr[25].toString().isEmpty())
				? String.valueOf((arr[25])).trim() : "";

		String docType = (arr[23] != null && !arr[23].toString().isEmpty())
				? String.valueOf((arr[23])).trim() : "";

		Object docDate = (arr[26] != null && !arr[26].toString().isEmpty())
				? arr[26] : "";

		String finYear = "";
		LocalDate localDocDate = DateUtil.parseObjToDate(docDate);

		finYear = GenUtil.getFinYear(localDocDate);
		String sgstin = (arr[22] != null && !arr[22].toString().isEmpty())
				? String.valueOf(arr[22]).trim() : "";

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(sgstin)
				.add(docType).add(docNo).toString();
	}

	@Override
	public String buildComprehenceDataBlockKey(Object[] arr,
			TabularDataLayout config) {
		String docNo = (arr[8] != null && !arr[8].toString().isEmpty())
				? String.valueOf((arr[8])).trim() : "";
		String docType = (arr[7] != null && !arr[7].toString().isEmpty())
				? String.valueOf((arr[7])).trim() : "";

		Object docDate = (arr[9] != null && !arr[9].toString().isEmpty())
				? arr[9] : "";

		String finYear = "";
		LocalDate localDocDate = DateUtil.parseObjToDate(docDate);

		/*
		 * if(convertToLocalDate(docDate)!= null){ finYear =
		 * GenUtil.getFinYear(convertToLocalDate(docDate)); }
		 */

		finYear = GenUtil.getFinYear(localDocDate);
		String sgstin = (arr[11] != null && !arr[11].toString().isEmpty())
				? String.valueOf(arr[11]).trim() : "";

		/*
		 * return new StringJoiner("|").add(docType).add(sgstin).add(docDate)
		 * .add(docNo).toString();
		 */

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(sgstin)
				.add(docType).add(docNo).toString();
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

	@Override
	public String buildItc04DataBlockKey(Object[] arr,
			TabularDataLayout layout) {
		String docKey = null;
		String tableNo = (arr[0] != null && !arr[0].toString().isEmpty())
				? String.valueOf((arr[0])).trim() : null;

		String jwStateCode = (arr[13] != null && !arr[13].toString().isEmpty())
				? String.valueOf((arr[13])).trim() : null;
		String jwStateGstin = (arr[12] != null && !arr[12].toString().isEmpty())
				? String.valueOf((arr[12])).trim() : null;

		String gstinOrStateCode = GstinOrStateCode(jwStateCode, jwStateGstin);
		if (tableNo != null
				&& tableNo.equalsIgnoreCase(GSTConstants.TABLE_NUMBER_4)) {
			String supplierGstin = (arr[4] != null
					&& !arr[4].toString().isEmpty())
							? String.valueOf((arr[4])).trim() : "";

			String deliveryChallanNo = (arr[5] != null
					&& !arr[5].toString().isEmpty())
							? String.valueOf((arr[5])).trim() : "";
			Object deliveryChallanDate = (arr[6] != null
					&& !arr[6].toString().isEmpty()) ? arr[6] : "";

			String finYear = "";
			LocalDate localDocDate = DateUtil
					.parseObjToDate(deliveryChallanDate);

			finYear = GenUtil.getFinYear(localDocDate);
			docKey = new StringJoiner(DOC_KEY_JOINER).add(tableNo)
					.add(supplierGstin).add(deliveryChallanNo).add(finYear)
					.toString();
			return docKey;

		}
		String fy = (arr[2] != null && !arr[2].toString().isEmpty())
				? String.valueOf((arr[2])).trim() : "";
		if ((tableNo != null
				&& GSTConstants.TABLE_NUMBER_5A.equalsIgnoreCase(tableNo))
				|| (tableNo != null && GSTConstants.TABLE_NUMBER_5B
						.equalsIgnoreCase(tableNo))) {

			Object deliveryChallanDate = (arr[6] != null
					&& !arr[6].toString().isEmpty()) ? arr[6] : null;

			Object jwDeliveryChallanDate = (arr[8] != null
					&& !arr[8].toString().isEmpty()) ? arr[8] : null;

			if (deliveryChallanDate != null && jwDeliveryChallanDate != null) {
				String supplierGstin = (arr[4] != null
						&& !arr[4].toString().isEmpty())
								? String.valueOf((arr[4])).trim() : "";
				String deliveryChallanNo = (arr[5] != null
						&& !arr[5].toString().isEmpty())
								? String.valueOf((arr[5])).trim() : "";
				String retPeriod = (arr[3] != null
						&& !arr[3].toString().isEmpty())
								? String.valueOf((arr[3])).trim() : "";
				String jwDeliveryChallanNo = (arr[7] != null
						&& !arr[7].toString().isEmpty())
								? String.valueOf((arr[7])).trim() : "";

				String dFinYear = "";
				LocalDate localDocDate = DateUtil
						.parseObjToDate(deliveryChallanDate);
				dFinYear = GenUtil.getFinYear(localDocDate);

				String jwDFinYear = "";
				LocalDate jwDlocalDocDate = DateUtil
						.parseObjToDate(jwDeliveryChallanDate);
				jwDFinYear = GenUtil.getFinYear(jwDlocalDocDate);
				docKey = new StringJoiner(DOC_KEY_JOINER).add(tableNo)
						.add(retPeriod.concat(fy)).add(supplierGstin)
						.add(deliveryChallanNo).add(dFinYear)
						.add(jwDeliveryChallanNo).add(jwDFinYear)
						.add(gstinOrStateCode).toString();

				return docKey;

			}
			if (jwDeliveryChallanDate != null) {
				String supplierGstin = (arr[4] != null
						&& !arr[4].toString().isEmpty())
								? String.valueOf((arr[4])).trim() : "";
				String retPeriod = (arr[3] != null
						&& !arr[3].toString().isEmpty())
								? String.valueOf((arr[3])).trim() : "";
				String jwDeliveryChallanNo = (arr[7] != null
						&& !arr[7].toString().isEmpty())
								? String.valueOf((arr[7])).trim() : "";

				String jwDFinYear = "";
				LocalDate jwDlocalDocDate = DateUtil
						.parseObjToDate(jwDeliveryChallanDate);
				jwDFinYear = GenUtil.getFinYear(jwDlocalDocDate);
				docKey = new StringJoiner(DOC_KEY_JOINER).add(tableNo)
						.add(retPeriod.concat(fy)).add(supplierGstin)
						.add(jwDeliveryChallanNo).add(jwDFinYear)
						.add(gstinOrStateCode).toString();

				return docKey;

			}
			if (deliveryChallanDate != null) {
				String supplierGstin = (arr[4] != null
						&& !arr[4].toString().isEmpty())
								? String.valueOf((arr[4])).trim() : "";
				String deliveryChallanNo = (arr[5] != null
						&& !arr[5].toString().isEmpty())
								? String.valueOf((arr[5])).trim() : "";
				String retPeriod = (arr[3] != null
						&& !arr[3].toString().isEmpty())
								? String.valueOf((arr[3])).trim() : "";
				String dFinYear = "";
				LocalDate localDocDate = DateUtil
						.parseObjToDate(deliveryChallanDate);
				dFinYear = GenUtil.getFinYear(localDocDate);
				docKey = new StringJoiner(DOC_KEY_JOINER).add(tableNo)
						.add(retPeriod.concat(fy)).add(supplierGstin)
						.add(deliveryChallanNo).add(dFinYear)
						.add(gstinOrStateCode).toString();
				return docKey;

			}

		}
		if (tableNo != null
				&& tableNo.equalsIgnoreCase(GSTConstants.TABLE_NUMBER_5C)) {
			String supplierGstin = (arr[4] != null
					&& !arr[4].toString().isEmpty())
							? String.valueOf((arr[4])).trim() : "";

			String invNumber = (arr[10] != null
					&& !arr[10].toString().isEmpty())
							? String.valueOf((arr[10])).trim() : "";
			Object invDate = (arr[11] != null && !arr[11].toString().isEmpty())
					? arr[11] : "";

			String finYear = "";
			LocalDate localDocDate = DateUtil.parseObjToDate(invDate);
			finYear = GenUtil.getFinYear(localDocDate);

			String deliveryChallanNo = (arr[5] != null
					&& !arr[5].toString().isEmpty())
							? String.valueOf((arr[5])).trim() : "";
			Object deliveryChallanDate = (arr[6] != null
					&& !arr[6].toString().isEmpty()) ? arr[6] : "";

			String dfinYear = "";
			LocalDate dlocalDocDate = DateUtil
					.parseObjToDate(deliveryChallanDate);

			dfinYear = GenUtil.getFinYear(dlocalDocDate);

			docKey = new StringJoiner(DOC_KEY_JOINER).add(tableNo)
					.add(supplierGstin).add(invNumber).add(finYear)
					.add(deliveryChallanNo).add(dfinYear).add(gstinOrStateCode)
					.toString();
			return docKey;
		} else {
			String supplierGstin = (arr[4] != null
					&& !arr[4].toString().isEmpty())
							? String.valueOf((arr[4])).trim() : "";

			String deliveryChallanNo = (arr[5] != null
					&& !arr[5].toString().isEmpty())
							? String.valueOf((arr[5])).trim() : "";
			Object deliveryChallanDate = (arr[6] != null
					&& !arr[6].toString().isEmpty()) ? arr[6] : "";

			String finYear = "";
			LocalDate localDocDate = DateUtil
					.parseObjToDate(deliveryChallanDate);

			finYear = GenUtil.getFinYear(localDocDate);

			String invNumber = (arr[10] != null
					&& !arr[10].toString().isEmpty())
							? String.valueOf((arr[10])).trim() : "";
			Object invDate = (arr[11] != null && !arr[11].toString().isEmpty())
					? arr[11] : "";

			String invFinYear = "";
			LocalDate localInvDate = DateUtil.parseObjToDate(invDate);
			invFinYear = GenUtil.getFinYear(localInvDate);

			String retPeriod = (arr[3] != null && !arr[3].toString().isEmpty())
					? String.valueOf((arr[3])).trim() : "";
			String jwDeliveryChallanNo = (arr[7] != null
					&& !arr[7].toString().isEmpty())
							? String.valueOf((arr[7])).trim() : "";

			Object jwDeliveryChallanDate = (arr[8] != null
					&& !arr[8].toString().isEmpty()) ? arr[8] : null;

			String jwDFinYear = "";
			LocalDate jwDlocalDocDate = DateUtil
					.parseObjToDate(jwDeliveryChallanDate);
			jwDFinYear = GenUtil.getFinYear(jwDlocalDocDate);
			docKey = new StringJoiner(DOC_KEY_JOINER).add(tableNo)
					.add(retPeriod.concat(fy)).add(supplierGstin)
					.add(deliveryChallanNo).add(finYear)
					.add(jwDeliveryChallanNo).add(jwDFinYear).add(invNumber)
					.add(invFinYear).add(gstinOrStateCode).toString();
			return docKey;
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
}
