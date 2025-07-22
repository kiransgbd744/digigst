package com.ey.advisory.app.services.structuralvalidation.master;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToCustomerConverter;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("CustomerStructValidationChain")
public class CustomerStructValidationChain {
	
	@Autowired
	@Qualifier("MasterDataToCustomerConverter")
	private MasterDataToCustomerConverter masterDataToCustomerConverter;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES 
	                                           = new ImmutableMap.Builder()
		.put(0, new ValidationRule[] { new CustomerRecipientGstnOrPan() })
		.put(1, new ValidationRule[] { new CustomerLagalName() })
		.put(2,new ValidationRule[] { new CustomerTradeName() })
		.put(3,new ValidationRule[] { new CustomerRecipientType() })
		.put(4,new ValidationRule[] { new CustomerRecCode() })
		.put(5,new ValidationRule[] { new CustomerOutSideIndia() })
		.put(6,new ValidationRule[] { new CustomerEmailIds() })
		.put(7,new ValidationRule[] { new CustomerPhoneNumber() }).build();

	
	
	public Map<String, List<ProcessingResult>> validation(
			List<Object[]> rawDocMap) {
		
		Map<String, List<ProcessingResult>> map = 
					new HashMap<String, List<ProcessingResult>>();
	
			
		
			for (Object[] obj : rawDocMap) {
				List<ProcessingResult> results = new ArrayList<>();
				 String key= 
						 masterDataToCustomerConverter.
						                         getCustomerValues(obj);
				for (int i = 0; i < 8; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(cellVal,
								obj, null);
						results.addAll(errors);

					});
				}
				
				if(results != null && results.size() > 0){
					List<ProcessingResult> current = map.get(key);	
					if (current == null) {
						current =new ArrayList<ProcessingResult>();
						map.put(key, results);
					}	
					else{
						map.put(key, current);
						current.addAll(results);
						
					}
				}
			}
			
	
		return map;

	}
}
