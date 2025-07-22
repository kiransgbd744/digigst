package com.ey.advisory.app.services.gstr1fileupload;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.annexure1fileupload.Annexure11HeaderCheckService;
import com.ey.advisory.app.services.common.Annexure1HeaderChecker;
/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("B2cHeaderCheckService")
public class B2cHeaderCheckService implements Annexure11HeaderCheckService {
	
	@Autowired
	@Qualifier("Annexure1HeaderChecker")
	private Annexure1HeaderChecker annexure1HeaderChecker;
	

	public static final String[] EXPECTED_HEADERS = { "ReturnType",
			"SupplierGSTIN", "ReturnPeriod", "DifferentialPercentageFlag",
			"Section7ofIGSTFlag", "AutopopulatetoRefund", "POS", "HSNorSAC",
			"UnitOfMeasurement", "Quantity", "Rate", "TaxableValue",
			"IntegratedTaxAmount", "CentralTaxAmount", "StateUTTaxAmount",
			"CessAmount", "TotalValue", "StateApplyingCess", "StateCessRate",
			"StateCessAmount", "TCSFlag", "eComGSTIN",
			"eComValueOfSuppliesMade", "eComValueOfSuppliesReturned",
			"eComNetValueOfSupplies", "TCSAmount", "ProfitCentre", "Plant",
			"Division", "Location", "SalesOrganisation", "DistributionChannel",
			"UserAccess1", "UserAccess2", "UserAccess3", "UserAccess4",
			"UserAccess5", "UserAccess6", "Userdefinedfield1",
			"Userdefinedfield2", "Userdefinedfield3" };

	@Override
	public Pair<Boolean, String> validate(Object[] headerCols) {
		
		Pair<Boolean, String> pair = annexure1HeaderChecker
				.validateHeaders(EXPECTED_HEADERS, headerCols);
		return pair;
	}

}
