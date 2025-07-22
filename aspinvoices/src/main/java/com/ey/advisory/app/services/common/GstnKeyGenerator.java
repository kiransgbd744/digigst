package com.ey.advisory.app.services.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;

@Component("GstnKeyGenerator")
public class GstnKeyGenerator {

	private static final String DOC_KEY_JOINER = "|";

	public String generateGstr7TdsKey(String deductorGstin, String returnPeroid,
			String deducteeGstin) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(deductorGstin).append(DOC_KEY_JOINER).append(returnPeroid)
				.append(DOC_KEY_JOINER).append(deducteeGstin);
		return buffer.toString();
	}

	public String generateGstr7TdsaKey(String deductorGstin,
			String returnPeroid, String orgReturnPeroid, String deducteeGstin) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(deductorGstin).append(DOC_KEY_JOINER).append(returnPeroid)
				.append(DOC_KEY_JOINER).append(orgReturnPeroid)
				.append(DOC_KEY_JOINER).append(deducteeGstin);
		return buffer.toString();
	}

	public String generateGstr8TcsUrdKey(String ecomGstin, String returnPeroid,
			String sTin, String section) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(ecomGstin).append(DOC_KEY_JOINER).append(returnPeroid)
				.append(DOC_KEY_JOINER).append(sTin).append(DOC_KEY_JOINER)
				.append(section);
		return buffer.toString();
	}

	public String generateGstr8TcsaUrdaKey(String ecomGstin,
			String returnPeroid, String orgReturnPeroid, String sTin,
			String section) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(ecomGstin).append(DOC_KEY_JOINER).append(returnPeroid)
				.append(DOC_KEY_JOINER).append(sTin).append(DOC_KEY_JOINER)
				.append(section).append(DOC_KEY_JOINER).append(orgReturnPeroid);
		return buffer.toString();
	}

	public String generateGstr6IsdKey(String isdGstin, String docNo, String idt,
			String dType) {
		String isdGstn = (isdGstin != null && !isdGstin.isEmpty()) ? isdGstin
				: null;
		String docNum = (docNo != null && !docNo.isEmpty()) ? docNo : null;
		String docYear = (idt != null && !idt.isEmpty()) ? idt : null;
		String docType = (dType != null && !dType.isEmpty()) ? dType : null;
		LocalDate parseObjToDate = DateUtil.parseObjToDate(docYear);
		LocalDate localOrigDocDate = EYDateUtil
				.toUTCDateTimeFromLocal(parseObjToDate);
		String fYear = localOrigDocDate.toString();
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(isdGstn)
				.add(docType).add(docNum).add(fYear).toString();
	}

	public String generateGstr9HsnKey(String gstin, String fy,
			String tableNumber, String hsn, BigDecimal rate, String uqc) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(gstin).append(DOC_KEY_JOINER).append(fy)
				.append(DOC_KEY_JOINER).append(tableNumber)
				.append(DOC_KEY_JOINER).append(hsn).append(DOC_KEY_JOINER)
				.append(rate).append(DOC_KEY_JOINER).append(uqc);
		return buffer.toString();
	}

}
