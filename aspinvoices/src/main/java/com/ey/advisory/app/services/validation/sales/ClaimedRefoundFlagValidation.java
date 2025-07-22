package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("ClaimedRefoundFlagValidation")
public class ClaimedRefoundFlagValidation 
                implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLY_TYPE = ImmutableList
			.of(GSTConstants.SEZT,GSTConstants.DXP);

	
	private boolean isValidTax(OutwardTransDocLineItem firstType,
			OutwardTransDocLineItem curType) {
		
		String firstTypeclaimRefundFlag=firstType.getClaimRefundFlag();
		String curTypeclaimRefundFlag=curType.getClaimRefundFlag();
		
		if(firstTypeclaimRefundFlag==null || firstTypeclaimRefundFlag.isEmpty()){
			firstTypeclaimRefundFlag=GSTConstants.N;
		}
		if(curTypeclaimRefundFlag==null || curTypeclaimRefundFlag.isEmpty()){
			curTypeclaimRefundFlag=GSTConstants.N;
		}
		
		if(!curTypeclaimRefundFlag.equalsIgnoreCase(firstTypeclaimRefundFlag)){
			return false;
		}
		return true;

	}
	
	@Override
	public List<ProcessingResult> validate(
			     OutwardTransDocument document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if(document.getDocType()==null 
				|| document.getDocType().isEmpty()) return errors;
if(!SUPPLY_TYPE.contains(document.getSupplyType())) return errors;
		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item  = items.get(0);

			if (!isValidTax(item, items.get(idx))) {
				errorLocations.add(GSTConstants.CLAIMREFUNDFLAG);
				TransDocProcessingResultLoc location 
				                  = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());

				errors.add(
						new ProcessingResult(APP_VALIDATION, "ER0104",
								"Claim Refund Flag should be same across "
								+ "all line items of a document.",
								location));
			}

		});

		return errors;
	}

}
