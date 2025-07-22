
package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.docs.DefaultOutwardTransDocKeyGenerator;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * This class is Responsible for In case of Supply Type is "CAN", and 
 * return for said tax period signed and submitted on GSTN portal,
 *  error message should come.
 */
/**
 * 
 * 
 * @author Siva.Nandam
 *
 */
@Component("CanGstnValidatior")
public class CanGstnValidatior
		implements DocRulesValidator<OutwardTransDocument> {
	
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DefaultOutwardTransDocKeyGenerator dockey;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		if (!GSTConstants.CAN.equalsIgnoreCase(document.getSupplyType()))
			return errors;

		if (document.getDocKey() == null || document.getDocKey().isEmpty())
			return errors;

		docRepository = StaticContextHolder.getBean("DocRepository",
				DocRepository.class);

		List<Boolean> docList = docRepository
				.findDocsCountByDocKey(document.getDocKey());

		if (docList == null || docList.isEmpty()) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location 
			= new TransDocProcessingResultLoc(
					errorLocations.toArray());

			errors.add(
					new ProcessingResult(APP_VALIDATION, "ER0518",
							"Document cannot be cancelled as the "
									+ "same was not reported to ASP System",
							location));
			return errors;
		}
		boolean isSubmitted = docList.get(0);
		
		if (isSubmitted) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					errorLocations.toArray());
			errors.add(
					new ProcessingResult(APP_VALIDATION, "ER0519",
							"Document cannot be cancelled as the same has "
									+ "been submitted on GSTN portal",
							location));
			return errors;

		}

		return errors;

	}

}
