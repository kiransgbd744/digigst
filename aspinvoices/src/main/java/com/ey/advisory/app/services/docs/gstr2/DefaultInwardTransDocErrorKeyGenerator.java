package com.ey.advisory.app.services.docs.gstr2;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DefaultInwardTransDocErrorKeyGenerator")
public class DefaultInwardTransDocErrorKeyGenerator
		implements DocKeyGenerator<Anx2InwardErrorHeaderEntity, String> {

	/**
	 * sgstin, docType, docNo, docDate
	 */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("yyyyMMdd");
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

	private static final FastDateFormat[] FAST_DATE_FORMATS = { DATE_FORMAT,
			SUPPORTED_DATE_FORMAT1, SUPPORTED_DATE_FORMAT2,
			SUPPORTED_DATE_FORMAT3, SUPPORTED_DATE_FORMAT4 };
	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String generateKey(Anx2InwardErrorHeaderEntity doc) {

		String docNo = (doc.getDocNo() != null) ? doc.getDocNo().trim() : "";
		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}
		String docDate = (doc.getDocDate() != null) ? doc.getDocDate().trim()
				: "";
		String finYear = "";

		LocalDate localDocDate = DateUtil.parseObjToDate(docDate);
		finYear = GenUtil.getFinYear(localDocDate);

		String sgstin = (doc.getSgstin() != null) ? doc.getSgstin().trim() : "";
		String cgstin = (doc.getCgstin() != null) ? doc.getCgstin().trim() : "";
		String docType = (doc.getDocType() != null) ? doc.getDocType().trim()
				: "";

		if (!Strings.isNullOrEmpty(sgstin)) {
			return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
					.add(sgstin).add(docType).add(docNo).toString();
		} else {
			String suppName = !Strings.isNullOrEmpty(doc.getSupplierLegalName())
							? StringUtils.truncate(doc.getSupplierLegalName().trim(),
									25)
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
			}
		}

		// if we are not able to parse using any pre-defined formats return
		// null
		return null;
	}

	private Date tryParsingToDate(String docDate, FastDateFormat dateFormat) {
		try {
			return dateFormat.parse(docDate);
		} catch (ParseException e) {
			LOGGER.error("Parse Exception of date " + e.getMessage());
			return null;
		}
	}

	@Override
	public String generateOrgKey(Anx2InwardErrorHeaderEntity doc) {
		// TODO Auto-generated method stub
		return null;
	}

}
