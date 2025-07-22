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

@Component("Ret1And1AHeaderCheckService")
public class Ret1And1AHeaderCheckService implements Annexure11HeaderCheckService {
	
	@Autowired
	@Qualifier("Annexure1HeaderChecker")
	private Annexure1HeaderChecker annexure1HeaderChecker;
	
	public static final String[] EXPECTED_HEADERS = { "ReturnType", "GSTIN",
			"ReturnPeriod", "ReturnTable", "Value", "IntegratedTaxAmount",
			"CentralTaxAmount", "StateUTTaxAmount", "CessAmount",
			"ProfitCentre", "Plant", "Division", "Location",
			"SalesOrganisation", "DistributionChannel", "UserAccess1",
			"UserAccess2", "UserAccess3", "UserAccess4", "UserAccess5",
			"UserAccess6", "Userdefinedfield1", "Userdefinedfield2",
			"Userdefinedfield3" };


	@Override
	public Pair<Boolean, String> validate(Object[] headerCols) {
		Pair<Boolean, String> pair = annexure1HeaderChecker
				.validateHeaders(EXPECTED_HEADERS, headerCols);
		return pair;
	}

}
