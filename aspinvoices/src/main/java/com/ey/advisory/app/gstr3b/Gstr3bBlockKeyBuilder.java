package com.ey.advisory.app.gstr3b;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for Building a unique document key which is a
 * combination of GSTIN and RETURN PERIOD
 * 
 * @author vishal.verma
 *
 */
@Component("Gstr3bBlockKeyBuilder")
@Slf4j
public class Gstr3bBlockKeyBuilder implements DataBlockKeyBuilder<String> {

	private final String KEY_JOINER = "-";

	@Override
	public String buildDataBlockKey(Object[] arr, TabularDataLayout config) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside Gstr3bBlockKeyBuilder.buildDataBlockKey()" 
					+ "Object[] arr: "+arr+ "TabularDataLayout: "+config);
		}

		String gstn = (arr[1] != null) ? String.valueOf(arr[1]).trim() : "";
		
		String ret_period = (arr[0] != null) ? String.valueOf(arr[0]).trim()
				: "";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("befor return buildDataBlockKey() : "+
					gstn + KEY_JOINER + ret_period);
		}
		return gstn + KEY_JOINER + ret_period ;
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
