package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * 
 * @author Siva.Nandam
 *
 */
@Component("CMHsnOrSacValidator")
public class CMHsnOrSacValidator
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLYTYPE_IMPORTS = ImmutableList
			.of(GSTConstants.TAX, GSTConstants.DTA);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			if (document.getSupplyType() != null
					&& !document.getSupplyType().isEmpty()) {
				if (SUPPLYTYPE_IMPORTS.contains(
						trimAndConvToUpperCase(document.getSupplyType()))) {
					if (document.getCgstin() != null
							&& !document.getCgstin().isEmpty()) {
						if(item.getHsnSac()==null || item.getHsnSac().isEmpty()){
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(HSNORSAC);
							TransDocProcessingResultLoc location 
							       = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0070",
									"HSN or SAC cannot be left blank.",
									location));
							
						}
						if (item.getHsnSac() != null
								&& !item.getHsnSac().isEmpty()) {
						int hsn=Integer.parseInt(item.getHsnSac());
							if (hsn!=0 || item.getHsnSac().trim().length() == 6
									|| item.getHsnSac().trim().length() == 8) {
								// do nothing

							} else {

								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(HSNORSAC);
								TransDocProcessingResultLoc location 
								       = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0070",
										"HSN or SAC cannot be left blank.",
										location));

							}
						}
					}
				}
			}
		});
		return errors;
	}

}
