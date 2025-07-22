package com.ey.advisory.app.services.strcutvalidation.shippingBill;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.purchase.RecipientGSTINValidationRule;
import com.ey.advisory.app.services.strcutvalidation.sales.*;
import com.ey.advisory.app.services.strcutvalidation.table3h3i.H3CGSTAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.table3h3i.H3CessAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.table3h3i.H3IGSTAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.table3h3i.H3SGSTAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.table3h3i.ReturnTypeValidationRule;
import com.ey.advisory.app.services.validation.sales.ShippingBillNumberValidator;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("ShippingBillStructValidationChain")
public class ShippingBillStructValidationChain {

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES 
	= new ImmutableMap.Builder()
		
		.put(0, new ValidationRule[] { new SgstinValidationRule() })
		.put(1, new ValidationRule[] { new TaxPeriodValidationRule() })
		.put(2,new ValidationRule[] { new DocumentnoValidator() })
		.put(3,new ValidationRule[] { new DocDateValidationRule() })
		.put(4,new ValidationRule[] { new PortcodeValidationRule() })
		.put(5,new ValidationRule[] { new ShippingBillNoValidationRule() })
		.put(6,new ValidationRule[] { new  ShippingbillDateValidationRule() })
		/*
		.put(26,new ValidationRule[] { new UserDefined1ValidationRule() })
		.put(27,new ValidationRule[] { new UserDefined2ValidationRule() })
		.put(28,new ValidationRule[] { new UserDefined3ValidationRule() })*/
			.build();
	
	
	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap) {
		
		Map<String, List<ProcessingResult>> map = 
					new HashMap<String, List<ProcessingResult>>();
	
			
		
			for (Object[] obj : rawDocMap) {
				List<ProcessingResult> results = new ArrayList<>();
				 String key=	ShippingBillStructValidationChain.getKeyValues(obj);
				for (int i = 0; i < 20; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(cellVal,
								obj, null);
						results.addAll(errors);

					});
				}
				if (results != null && results.size() > 0) {
					
					map.put(key, results);
				}		
			}
			
	
		return map;

	}

	private static String getKeyValues(Object[] obj) {
		String sgstin=null;
		String returnPeriod=null;
		String newPos=null;
		String TransactionType=null;
		String newRate=null;
		
			 sgstin =(obj[0]!=null)?(String.valueOf(obj[0])).trim():"";
			 returnPeriod =(obj[1]!=null)?(String.valueOf(obj[1])).trim():"";
			 newPos=(obj[7]!=null)?(String.valueOf(obj[7])).trim():"";
			 newRate=(obj[8]!=null)?(String.valueOf(obj[8])).trim():"";
			 TransactionType=(obj[2]!=null)?(String.valueOf(obj[2])).trim():"";
		return new StringJoiner("|").add(sgstin).add(returnPeriod)
				.add(TransactionType)
				.add(newPos).add(newRate)
				.toString();	
	}
}
