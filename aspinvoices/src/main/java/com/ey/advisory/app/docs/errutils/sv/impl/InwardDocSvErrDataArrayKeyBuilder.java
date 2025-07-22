package com.ey.advisory.app.docs.errutils.sv.impl;

import java.time.LocalDate;
import java.util.StringJoiner;

import com.ey.advisory.app.docs.errutils.sv.SvErrDataArrayKeyBuilder;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Slf4j
public class InwardDocSvErrDataArrayKeyBuilder
		implements SvErrDataArrayKeyBuilder<String> {

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String buildDataArrayKey(Object[] arr) {

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Entered buildDataArrayKey");
		}
		String docNo = (arr[24] != null) ? (String.valueOf(arr[24])).trim()
				: "";

		String docType = (arr[22] != null) ? (arr[22]).toString().trim() : "";
		Object docDate = (arr[25] != null) ? arr[25] : "";

		String sgstin = (arr[30] != null) ? (arr[30]).toString().trim() : "";
		String cgstin = (arr[21] != null) ? (arr[21]).toString().trim() : "";

		String finYear = null;

		LocalDate localDocDate = DateUtil.parseObjToDate(docDate);
		finYear = GenUtil.getFinYear(localDocDate);
		// Inward Invoice Key Format - FY|RGSTIN|SGSTIN|DocType|DocNo
		String docKey = new StringJoiner(DOC_KEY_JOINER).add(finYear)
				.add(cgstin).add(sgstin).add(docType).add(docNo).toString();
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug(
					"Inward SV Err DocKey in InwardDocSvErrDataArrayKeyBuilder"
							+ docKey);
		}
		return docKey;
	}

}
