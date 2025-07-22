package com.ey.advisory.app.services.gstr7fileupload;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.MasterHeaderCheckService;
import com.ey.advisory.app.util.HeaderCheckerUtil;

/**
 * 
 * @author Anand3.M
 *
 */

	
	@Component("Gstr7HeaderCheckService")
	public class Gstr7HeaderCheckService implements MasterHeaderCheckService {
		
		@Autowired
		@Qualifier("HeaderCheckerUtil")
		private HeaderCheckerUtil headerCheckerUtil;
		
																					

		
		public static final String[] EXPECTED_HEADERS ={
				"ReturnPeriod","ActionType","TDSDeductorGSTIN","OriginalTDSDeducteeGSTIN","OriginalReturnPeriod",
				"OriginalGrossAmount","TDSDeducteeGSTIN","GrossAmount","TDSIGST","TDSCGST",
				"TDSSGST","ContractNumber","ContractDate","ContractValue","PaymentAdviceNumber","PaymentAdviceDate",
				"DocumentNumber","DocumentDate","InvoiceValue","PlantCode","Division",
				"PurchaseOrganisation","ProfitCentre1","ProfitCentre2","UserDefinedField1","UserDefinedField2",
				"UserDefinedField3",
				
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
			Pair<Boolean, String> pair =
					headerCheckerUtil.validateHeaders(EXPECTED_HEADERS,headerCols);
			return pair;
				}

	}




