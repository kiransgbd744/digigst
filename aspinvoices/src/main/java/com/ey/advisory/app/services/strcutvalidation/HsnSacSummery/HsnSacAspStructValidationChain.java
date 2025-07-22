package com.ey.advisory.app.services.strcutvalidation.HsnSacSummery;
/**
 * Balakrishna.S
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.ProductDescriptionValidationRule;
import com.ey.advisory.app.services.strcutvalidation.b2c.B2cCessValidationRule;
import com.ey.advisory.app.services.strcutvalidation.b2c.B2cCgstValidationRule;
import com.ey.advisory.app.services.strcutvalidation.b2c.B2cIgstValidationRule;
import com.ey.advisory.app.services.strcutvalidation.b2c.B2cSgstValidationRule;
import com.ey.advisory.app.services.strcutvalidation.b2c.B2cTaxableValueValidationRule;
import com.ey.advisory.app.services.strcutvalidation.b2c.B2cTotalValValidationRule;
import com.ey.advisory.app.services.strcutvalidation.b2cs.*;
import com.ey.advisory.app.services.strcutvalidation.b2cs.*;
import com.ey.advisory.app.services.strcutvalidation.sales.*;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;
@Component("HsnSacAspStructValidationChain")
public class HsnSacAspStructValidationChain {

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES 
	= new ImmutableMap.Builder()
		/*.put(0, new ValidationRule[] { new HsnSgstinValidationRule() })
		.put(1, new ValidationRule[] { new HsnRetPeriodValidationRule() })
	    .put(2, new ValidationRule[] { new B2csNewhsnSacValidationRule() })
		.put(3,new ValidationRule[] { new ProductDescriptionValidationRule() })
		.put(4,new ValidationRule[] { new B2csUOMValidationRule() })
		.put(5,new ValidationRule[] { new B2cIgstValidationRule() })
		.put(6,new ValidationRule[] { new B2cCgstValidationRule() })
		.put(7,new ValidationRule[] { new B2cSgstValidationRule() })
		.put(8,new ValidationRule[] { new B2cCessValidationRule() })
		.put(9,new ValidationRule[] { new B2cTaxableValueValidationRule() })
		.put(10,new ValidationRule[] { new B2cTotalValValidationRule() })
	//	.put(11,new ValidationRule[] { new B2csQuantityValidationRule() })//invoice Value
*/			
	
			.put(0, new ValidationRule[] { new HsnSgstinValidationRule() })
			.put(1, new ValidationRule[] { new HsnRetPeriodValidationRule() })
			.put(2, new ValidationRule[] { new HsnMasterValidationRule() })
			.put(3, new ValidationRule[] {
					new ProductDescriptionValidationRule() })
			.put(4, new ValidationRule[] { new HsnUOMValidationRule() })
			.put(5, new ValidationRule[] { new HsnQuantityValidationRule() })
			.put(6, new ValidationRule[] { new HsnTaxableValidationRule() })
			.put(7, new ValidationRule[] { new HsnIgstValidationRule() })
			.put(8, new ValidationRule[] { new HsnCgstValidationRule() })
			.put(9, new ValidationRule[] { new HsnSgstValidationRule() })
			.put(10, new ValidationRule[] { new HsnCessValidationRule() })
			.put(11, new ValidationRule[] { new HsnToatlValidationRule() })
	.build();
	
	public List<ProcessingResult> validation(
			List<Object[]> rawDocMap) {
		
		/*Map<String, List<ProcessingResult>> map = 
					new HashMap<String, List<ProcessingResult>>();
	*/
			
		List<ProcessingResult> results = new ArrayList<>();
			for (Object[] obj : rawDocMap) {
				
			//	 String key=	HsnSacAspStructValidationChain.getKeyValues(obj);
				for (int i = 0; i < 12; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(cellVal,
								obj, null);
						results.addAll(errors);

					});
				}
			/*	if (results != null && results.size() > 0) {
					
					map.put(key, results);
				}		*/
			}
			
	
		return results;

	}

	private static String getKeyValues(Object[] obj) {
		String sgstin=null;
		String returnPeriod=null;
		String newPos=null;
		
		String newRate=null;
		
			 sgstin =(obj[0]!=null)?(String.valueOf(obj[0])).trim():"";
			 returnPeriod =(obj[1]!=null)?(String.valueOf(obj[1])).trim():"";
			 newPos=(obj[2]!=null)?(String.valueOf(obj[2])).trim():"";
			 newRate=(obj[4]!=null)?(String.valueOf(obj[4])).trim():"";
			
		return new StringJoiner("|").add(sgstin).add(returnPeriod)
				.add(newPos).add(newRate)
				.toString();	
	}
}
