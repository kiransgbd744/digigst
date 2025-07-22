package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class Gstr6TaxMandatory
		implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

	

	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
String igstAsIgst = document.getIgstAsIgst();
String igstAsSgst = document.getIgstAsSgst();
String igstAsCgst = document.getIgstAsCgst();
String sgstAsSgst = document.getSgstAsSgst();
String sgstAsIgst = document.getSgstAsIgst();
String cgstAsCgst = document.getCgstAsCgst();
String cgstAsIgst = document.getCgstAsIgst();
String cessAmount = document.getCessAmount();

BigDecimal igstAsIgstRate = BigDecimal.ZERO;
BigDecimal igstAsSgstRate = BigDecimal.ZERO;
BigDecimal igstAsCgstRate = BigDecimal.ZERO;
BigDecimal sgstAsSgstRate = BigDecimal.ZERO;
BigDecimal sgstAsIgstRate = BigDecimal.ZERO;
BigDecimal cgstAsCgstRate = BigDecimal.ZERO;
BigDecimal cgstAsIgstRate = BigDecimal.ZERO;
BigDecimal cessAmountRate = BigDecimal.ZERO;
if (cessAmount != null && !cessAmount.isEmpty()) {
	cessAmountRate = NumberFomatUtil.getBigDecimal(cessAmount);
	
}
if (cgstAsIgst != null && !cgstAsIgst.isEmpty()) {
	cgstAsIgstRate = NumberFomatUtil.getBigDecimal(cgstAsIgst);
	
}
if (cgstAsCgst != null && !cgstAsCgst.isEmpty()) {
	cgstAsCgstRate = NumberFomatUtil.getBigDecimal(cgstAsCgst);
	
}

if (sgstAsIgst != null && !sgstAsIgst.isEmpty()) {
	sgstAsIgstRate = NumberFomatUtil.getBigDecimal(sgstAsIgst);
	
}
if (igstAsIgst != null && !igstAsIgst.isEmpty()) {
	igstAsIgstRate = NumberFomatUtil.getBigDecimal(igstAsIgst);
	
}
if (igstAsSgst != null && !igstAsSgst.isEmpty()) {
	igstAsSgstRate = NumberFomatUtil.getBigDecimal(igstAsSgst);
	
}
if (igstAsCgst != null && !igstAsCgst.isEmpty()) {
	igstAsCgstRate = NumberFomatUtil.getBigDecimal(igstAsCgst);
	
}
if (sgstAsSgst != null && !sgstAsSgst.isEmpty()) {
	sgstAsSgstRate = NumberFomatUtil.getBigDecimal(sgstAsSgst);
	
}
if(BigDecimal.ZERO.compareTo(sgstAsSgstRate) == 0 
&& BigDecimal.ZERO.compareTo(sgstAsIgstRate) == 0 
&& BigDecimal.ZERO.compareTo(cessAmountRate) == 0 
&& BigDecimal.ZERO.compareTo(cgstAsIgstRate) == 0 
&& BigDecimal.ZERO.compareTo(cgstAsCgstRate) == 0 
&& BigDecimal.ZERO.compareTo(igstAsIgstRate) == 0 
&& BigDecimal.ZERO.compareTo(igstAsCgstRate) == 0 
&& BigDecimal.ZERO.compareTo(igstAsSgstRate) == 0){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CESS_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER3026",
					"Minimum one field should have value", location));
		
}
if(GSTConstants.CR.equalsIgnoreCase(document.getDocType())) return errors;
if(sgstAsSgstRate.compareTo(BigDecimal.ZERO) < 0 
|| sgstAsIgstRate.compareTo(BigDecimal.ZERO) < 0 
|| cessAmountRate.compareTo(BigDecimal.ZERO) < 0 
|| cgstAsIgstRate.compareTo(BigDecimal.ZERO) < 0 
|| cgstAsCgstRate.compareTo(BigDecimal.ZERO) < 0 
|| igstAsIgstRate.compareTo(BigDecimal.ZERO) < 0 
|| igstAsCgstRate.compareTo(BigDecimal.ZERO) < 0 
|| igstAsSgstRate.compareTo(BigDecimal.ZERO) < 0){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CESS_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER3027",
					"Only Positive Values accepted", location));
		
}
		return errors;
	}

}
