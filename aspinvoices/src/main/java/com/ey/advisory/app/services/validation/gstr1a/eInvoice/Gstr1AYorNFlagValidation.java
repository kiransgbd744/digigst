package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import java.util.List;

import com.ey.advisory.common.GSTConstants;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AYorNFlagValidation {

	private static final List<String> YORNGLAG = ImmutableList
			.of(GSTConstants.Y, GSTConstants.N);
	private static final List<String> DIFF = ImmutableList
			.of(GSTConstants.L65, GSTConstants.N);
	private static final List<String> ITC = ImmutableList
			.of(GSTConstants.T1, GSTConstants.T3);
	private Gstr1AYorNFlagValidation() {
	}

	public static boolean valid(String flag) {
		if (YORNGLAG.contains(flag.toUpperCase())) {
			return true;
		}
		return false;

	}
	public static boolean itcvalid(String flag) {
		if (ITC.contains(flag.toUpperCase())) {
			return true;
		}
		return false;

	}
	public static boolean Diffvalid(String flag) {
		if (DIFF.contains(flag.toUpperCase())) {
			return true;
		}
		return false;

	}
}
