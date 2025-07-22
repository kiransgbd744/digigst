package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
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
public class Gstr6BillOfEntryDate implements DocRulesValidator<InwardTransDocument> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	private static final List<String> DOCTYPE_IMPORTS = ImmutableList.of(

			GSTConstants.RCR, GSTConstants.RDR);
	
	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

	
				if (document.getSupplyType() != null
						&& !document.getSupplyType().isEmpty()) {

					if (DOCTYPE_IMPORTS.contains(
							trimAndConvToUpperCase(document.getDocType()))) {
						if (document.getBillOfEntryDate() == null) {

							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.BillOfEntryDate);
							TransDocProcessingResultLoc location 
							 = new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1068",
									" Bill of Entry Date cannot be left Blank.",
									location));
					
				}
			}
		}
		return errors;
	}

}
