package com.ey.advisory.app.services.validation.sales;

import java.math.BigDecimal;
import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.common.GSTConstants;
import com.google.common.collect.ImmutableList;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

public class BigDecimalNagativeValueUtil {
	

	
	private static final List<String> CATEGOTY_IMPORTS1 = ImmutableList
			.of(GSTConstants.DIS, GSTConstants.CMB);
	private static final List<String> CATEGOTY_IMPORTS2 = ImmutableList
			.of(GSTConstants.SHP, GSTConstants.CMB);
	
	private static final List<String> CATEGOTY_IMPORTS3 = ImmutableList
			.of(GSTConstants.REG, GSTConstants.SHP);
	
	private static final List<String> CATEGOTY_IMPORTS4 = ImmutableList
			.of(GSTConstants.REG, GSTConstants.DIS);
	
	public static boolean validate(
			BigDecimal value) {

			if (value.compareTo(BigDecimal.ZERO) < 0) {
				return false;
			}

		
		return true;
	}
	public static boolean Negvalidate(OutwardTransDocument document,
			BigDecimal value) {

		if (!GSTConstants.CR.equalsIgnoreCase(document.getDocType())) {
			if (value.compareTo(BigDecimal.ZERO) < 0) {
				return false;
			}

		}
		return true;
	}
	
	public static boolean valid(String docCatogiry, String val,
			String indcator) {
		if (docCatogiry != null && !docCatogiry.isEmpty()) {
			if (indcator.equalsIgnoreCase("D")) {
				if (CATEGOTY_IMPORTS1
						.contains(trimAndConvToUpperCase(docCatogiry))) {
					if (val == null || val.isEmpty()) {
						return false;
					}

				}
			}
			if (indcator.equalsIgnoreCase("S")) {
				if (CATEGOTY_IMPORTS2
						.contains(trimAndConvToUpperCase(docCatogiry))) {
					if (val == null || val.isEmpty()) {
						return false;
					}

				}
			}
			if (indcator.equalsIgnoreCase("R")) {
				if (CATEGOTY_IMPORTS3
						.contains(trimAndConvToUpperCase(docCatogiry))) {
					if (val == null || val.isEmpty()) {
						return false;
					}

				}
			}
			if (indcator.equalsIgnoreCase("C")) {
				if (CATEGOTY_IMPORTS4
						.contains(trimAndConvToUpperCase(docCatogiry))) {
					if (val == null || val.isEmpty()) {
						return false;
					}

				}
			}
		}
		return true;
	}
	public static boolean valid(String docCatogiry, Integer val,
			String indcator) {
		if (docCatogiry != null && !docCatogiry.isEmpty()) {
			if (indcator.equalsIgnoreCase("D")) {
				if (CATEGOTY_IMPORTS1
						.contains(trimAndConvToUpperCase(docCatogiry))) {
					if (val == null) {
						return false;
					}

				}
			}
			if (indcator.equalsIgnoreCase("S")) {
				if (CATEGOTY_IMPORTS2
						.contains(trimAndConvToUpperCase(docCatogiry))) {
					if (val == null) {
						return false;
					}

				}
			}
			if (indcator.equalsIgnoreCase("R")) {
				if (CATEGOTY_IMPORTS3
						.contains(trimAndConvToUpperCase(docCatogiry))) {
					if (val == null) {
						return false;
					}

				}
			}
			if (indcator.equalsIgnoreCase("C")) {
				if (CATEGOTY_IMPORTS4
						.contains(trimAndConvToUpperCase(docCatogiry))) {
					if (val == null) {
						return false;
					}

				}
			}
		}
		return true;
	}
}
