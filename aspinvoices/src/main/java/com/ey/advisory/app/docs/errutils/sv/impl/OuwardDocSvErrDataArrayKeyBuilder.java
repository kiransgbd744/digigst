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
public class OuwardDocSvErrDataArrayKeyBuilder
		implements SvErrDataArrayKeyBuilder<String> {
	
	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String buildDataArrayKey(Object[] arr) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Entered buildDataArrayKey");
		}
		String docNo = (arr[25] != null) ? String.valueOf(arr[25])
				: "";
		
		String docType = (arr[23] != null) ? String.valueOf((arr[23])) : "";

		Object docDate = (arr[26] != null) ? arr[26] : "";
		
		LocalDate localDocDate = DateUtil.parseObjToDate(docDate);

		String finYear = GenUtil.getFinYear(localDocDate);
		String sgstin = (arr[22] != null) ? String.valueOf(arr[22]) : "";
		
		String docKey = new StringJoiner(DOC_KEY_JOINER)
				.add(finYear)				
				.add(sgstin)
				.add(docType)				
				.add(docNo)
				.toString();
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug(
					"Outward SV Err DocKey in OuwardDocSvErrDataArrayKeyBuilder"
							+ docKey);
		}
		return docKey;
	}
}
