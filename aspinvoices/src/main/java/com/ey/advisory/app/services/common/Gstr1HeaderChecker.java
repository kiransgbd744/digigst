package com.ey.advisory.app.services.common;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr1HeaderChecker")
@Slf4j
public class Gstr1HeaderChecker {
	public Pair<Boolean, String> validateHeaders(String[] expectedHeaders,
			Object[] headerCols) {

		int expectedLength = expectedHeaders.length;
		int headerLength = headerCols.length;
		try {
			if (expectedLength == headerLength) {
				for (int i = 0, j = 0; i < expectedLength
						&& j < headerLength; i++, j++) {
					String value0 = expectedHeaders[i].trim();
					String value1 = headerCols[j].toString().trim();
					if (value0 != null && !value0.equalsIgnoreCase(value1)) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(String.format("Column Name", value1));
						}
						return new Pair<>(false,
								"The size of the Header do not matched ");
					}
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"File Header Lengths are -> Expected file is : '%s', Original file is : '%s'",
							expectedLength, headerLength);
					LOGGER.debug(logMsg);
				}
				return new Pair<>(false,
						"The size of the Header do not matched ");
			}
		} catch (Exception e) {
			LOGGER.error("The size of the Header do not matched:{}", e);
		}
		return new Pair<>(true, "The size of the Header matched ");

	}

}
