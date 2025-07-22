package com.ey.advisory.admin.services.onboarding.common;

import java.util.StringJoiner;

import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class MasterVendorDocumentKeyBuilder
		implements DataBlockKeyBuilder<String> {

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String buildDataBlockKey(Object[] arr, TabularDataLayout config) {
		String supplierGstinPan = (arr[2] != null) ? String.valueOf((arr[2]))
				: "";
		String supplierName = (arr[3] != null) ? String.valueOf(arr[3]) : "";
		return new StringJoiner(DOC_KEY_JOINER).add(supplierGstinPan)
				.add(supplierName).toString();
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
