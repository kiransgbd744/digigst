package com.ey.advisory.app.services.doc.gstr1a;

import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnx1OutWardErrHeader;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.GSTConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DefaultGstr1AOutwardTransDocErrorKeyGenerator")
public class DefaultGstr1AOutwardTransDocErrorKeyGenerator
		implements DocKeyGenerator<Gstr1AAnx1OutWardErrHeader, String> {

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
	public String generateKey(Gstr1AAnx1OutWardErrHeader doc) {

		/**
		 * sgstin, docType, docNo, docDate
		 */

			String docNo = (doc.getDocNo() != null
					&& !doc.getDocNo().trim().isEmpty()) ? doc.getDocNo().trim()
							: "";
			if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
				docNo = docNo.substring(1);
			}
			String finYear = (doc.getFinYear() != null
					&& !doc.getFinYear().trim().isEmpty()) ? doc.getFinYear().trim()
							: "";
			String sgstin = (doc.getSgstin() != null
					&& !doc.getSgstin().trim().isEmpty()) ? doc.getSgstin().trim()
							: "";
			String docType = (doc.getDocType() != null
					&& !doc.getDocType().trim().isEmpty()) ? doc.getDocType().trim()
							: "";

			return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(sgstin)
					.add(docType).add(docNo).toString();
		}
	
	@Override
	public String generateOrgKey(Gstr1AAnx1OutWardErrHeader doc) {
		// TODO Auto-generated method stub
		return null;
	}

}
