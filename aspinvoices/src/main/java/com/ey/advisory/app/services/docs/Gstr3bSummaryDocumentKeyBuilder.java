package com.ey.advisory.app.services.docs;

import java.util.StringJoiner;

import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for Building a unique document key which is a
 * combination of ReturnPeriod, GSTIN, Table Number,POS
 * 
 * @author Shashikant.Shukla
 *
 */

public class Gstr3bSummaryDocumentKeyBuilder implements DataBlockKeyBuilder<String> {
	
	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String buildDataBlockKey(Object[] arr, TabularDataLayout config) {
		String ReturnPeriod = (arr[0] != null && !arr[0].toString().isEmpty())
				? String.valueOf((arr[0])).trim() : "";

		String gstin = (arr[1] != null && !arr[1].toString().isEmpty())
				? String.valueOf((arr[1])).trim() : "";

		String tableNumber = (arr[2] != null && !arr[2].toString().isEmpty())
				?String.valueOf(arr[2]).trim() : "";
		String pos = (arr[8] != null && !arr[8].toString().isEmpty())
					? String.valueOf(arr[8]).trim() : "";


		return new StringJoiner(DOC_KEY_JOINER).add(ReturnPeriod).add(gstin)
				.add(tableNumber).add(pos).toString();
	}

	@Override
	public String buildComprehenceDataBlockKey(Object[] arr,
			TabularDataLayout config) {
		return null;
	}


	@Override
	public String buildItc04DataBlockKey(Object[] arr,
			TabularDataLayout layout) {
		// TODO Auto-generated method stub
		return null;
	}
}
