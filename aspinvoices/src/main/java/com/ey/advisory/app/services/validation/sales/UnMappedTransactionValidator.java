package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.INV;
import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.RNV;
import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.RCR;
import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.RDR;
import static com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants.SUPPLY_TYPE;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.ARFV;
import static com.ey.advisory.common.GSTConstants.CR;
import static com.ey.advisory.common.GSTConstants.DR;
import static com.ey.advisory.common.GSTConstants.RFV;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * This class is responsible to validate Any unmapped transactions 
 * related to outward supply shall be shared as part 
 * of separate report as "Unmapped fields Report"
 */

/**
 * 
 * @author Siva.Nandam
 *
 */
 //BR_OUTWARD_33

@Component("UnMappedTransactionValidator")
public class UnMappedTransactionValidator
    implements DocRulesValidator<OutwardTransDocument> {
	
	private static final List<String> DOC_TYPES_NOT_REQUIRING_IMPORTS = 
			ImmutableList.of(INV, RNV,RFV,ARFV,CR,RCR,DR,RDR);


@Override
public List<ProcessingResult> validate(OutwardTransDocument document,
		ProcessingContext context) {

	List<OutwardTransDocLineItem> items = document.getLineItems();
	List<String> infoLocations = new ArrayList<>();
	List<ProcessingResult> errors = new ArrayList<>();
	IntStream.range(0, items.size()).forEach(idx -> {
		OutwardTransDocLineItem item = items.get(idx);
		infoLocations.add(SUPPLY_TYPE);
	
	if(!(DOC_TYPES_NOT_REQUIRING_IMPORTS.contains(document.getDocType()))){ 
		TransDocProcessingResultLoc location = 
				new TransDocProcessingResultLoc(
						idx, infoLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION,"ERRXXX",
				"Unmapped transactions",location));
		
	}
	});
		return errors;
		
}
}
