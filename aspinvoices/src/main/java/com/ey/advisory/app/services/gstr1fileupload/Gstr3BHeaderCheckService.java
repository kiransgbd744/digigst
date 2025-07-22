package com.ey.advisory.app.services.gstr1fileupload;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.annexure1fileupload.*;
import com.ey.advisory.app.services.common.Annexure1HeaderChecker;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Gstr3BHeaderCheckService")
public class Gstr3BHeaderCheckService implements Annexure11HeaderCheckService {

	@Autowired
	@Qualifier("Annexure1HeaderChecker")
	private Annexure1HeaderChecker annexure1HeaderChecker;

	public static final String[] EXPECTED_HEADERS = { "TaxpayerGSTIN",
			"ReturnPeriod", "SerialNo.", "Description", "IGSTAmount",
			"CGSTAmount", "SGSTAmount", "CessAmount" };

	@Override
	public Pair<Boolean, String> validate(Object[] headerCols) {
		Pair<Boolean, String> pair = annexure1HeaderChecker
				.validateHeaders(EXPECTED_HEADERS, headerCols);
		return pair;
	}

}
