/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants
                                                             .APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.DOC_TYPE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.ImmutableList;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
@Component("RfvDocumentValidator45")
public class RfvDocumentValidator45
		implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("DocRepository")
	DocRepository docRepository;

	private static final List<String> RFV_TYPES_REQUIRING_IMPORTS 
	= ImmutableList.of(GSTConstants.RFV, GSTConstants.RRFV);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		docRepository = StaticContextHolder.getBean("DocRepository", 
				DocRepository.class);
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String groupCode = TenantContext.getTenantId();
		if (document.getOrigDocNo() != null
				&& !document.getOrigDocNo().isEmpty()) {
			if (document.getDocType() != null
					&& !document.getDocType().isEmpty()) {
				if (document.getOrigDocDate() != null) {

					if (RFV_TYPES_REQUIRING_IMPORTS
							.contains(
							trimAndConvToUpperCase(document.getDocType()))) {

						int count = docRepository.findOriginalDoc(
								document.getOrigDocNo(),
								document.getOrigDocDate());

						if (count <=0) {
							errorLocations.add(DOC_TYPE);
							TransDocProcessingResultLoc location = 
									new TransDocProcessingResultLoc(
									errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER232",
									"Original "
									+ "document to be reported in case of RFV",
									location));
						}

					}

				}
			}
		}
		return errors;
	}

}
*/