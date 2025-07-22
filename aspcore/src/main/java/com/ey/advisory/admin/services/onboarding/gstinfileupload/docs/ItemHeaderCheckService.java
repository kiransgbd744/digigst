package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.HeaderCheckerUtil;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("ItemHeaderCheckService")
public class ItemHeaderCheckService implements MasterHeaderCheckService {

	@Autowired
	@Qualifier("HeaderCheckerUtil")
	private HeaderCheckerUtil headerCheckerUtil;

	protected static final String[] EXPECTED_HEADERS = { "GSTIN/PAN", "ItemCode",
			"ItemDescription", "CategoryOfItem", "HSNorSAC",
			"UnitofMeasurement", "ReverseChargeFlag", "TDSFlag",
			"Differential%Flag", "NIL/NON/Exempt",
			"IfY,thencircular/Notificationnumber",
			"IfY,thencircular/Notificationdate", "EffectivedateofCircular",
			"Rate", "EligibilityIndicator", "%ofEligibility",
			"CommonSupplyIndicator", "ITCReversalIdentifier", "ITCEntitlement"

	};

	// Check if the size of the headerCols array is less than
	// EXPECTED_HEADERS size. If so, return new
	// Pair<Boolean, String>(false,
	// "The size of the headers do not match!".

	// If the size is equal or greater, then get the first required
	// no of elements from the headerCols array into another array.

	// iterate and compare the elements of the EXPECTED_HEADERS and the
	// above array. If any element is different, then return a false
	// and an error message.

	@Override
	public Pair<Boolean, String> validate(Object[] headerCols) {
		Pair<Boolean, String> pair = headerCheckerUtil
				.validateHeaders(EXPECTED_HEADERS, headerCols);
		return pair;
	}

}