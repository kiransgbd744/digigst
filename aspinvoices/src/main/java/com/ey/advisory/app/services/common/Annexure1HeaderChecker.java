package com.ey.advisory.app.services.common;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Annexure1HeaderChecker")
@Slf4j
public class Annexure1HeaderChecker {
	
	public Pair<Boolean, String> validateHeaders(String[] expectedHeaders,
			Object[] headerCols) {

		int expectedLength = expectedHeaders.length;
		int headerLength = headerCols.length;
		try {
			if (expectedLength == headerLength) {
				for (int i = 0, j = 0; i < expectedLength
						&& j < headerLength; i++, j++) {
					String value0 = expectedHeaders[i].toString().trim();
					String value1 = headerCols[j].toString().trim();
					if (value0 != null) {
						if (!value0.equalsIgnoreCase(value1)) {
							return new Pair<>(false,
									"The size of the Header do "
											+ "not matched ");
						}
					}
				}

			} else {
				return new Pair<>(false,
						"The size of the Header do not matched ");
			}

		} catch (Exception e) {
			LOGGER.error("The size of the Header do not matched"+e);
		}
		return new Pair<>(true, "The size of the Header matched ");

	}



}

