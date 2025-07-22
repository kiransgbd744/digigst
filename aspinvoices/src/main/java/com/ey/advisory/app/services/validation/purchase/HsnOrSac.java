package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.strcutvalidation.ValidatorUtil;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Siva.Nandam
 *
 */
public class HsnOrSac implements DocRulesValidator<InwardTransDocument> {
	@Autowired
	@Qualifier("DefaultHsnCache")
	private HsnCache hsnCache;
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnRep;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<InwardTransDocLineItem> items = document.getLineItems();
		
		String paramkryId = GSTConstants.I9;
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		String paramkryId10 = GSTConstants.I10;
		String paramtrvalue10 = util.valid(entityConfigParamMap, paramkryId10,
				document.getEntityId());
		
		for (InwardTransDocLineItem item : items) {
			if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {

				if (!ValidatorUtil.isEvenNumber(item.getHsnSac().length())) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(HSNORSAC);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							items.indexOf(item), errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1070",
							"Invalid HSN or SAC code", location));
					
				}
				else if ((GSTConstants.A.equalsIgnoreCase(paramtrvalue) ||  
						GSTConstants.C.equalsIgnoreCase(paramtrvalue))&&
					     GSTConstants.A.equalsIgnoreCase(paramtrvalue10)) {
				    

					hsnCache = StaticContextHolder.getBean("DefaultHsnCache",
							HsnCache.class);
					String hsnOrSac = item.getHsnSac();
					int i = hsnCache.findhsn(hsnOrSac);

					if (i == 0) {
						hsnRep = StaticContextHolder.getBean(
								"HsnOrSacRepositoryMaster",
								HsnOrSacRepository.class);

						int findByHsnOrSac = hsnRep.findByHsnOrSac(hsnOrSac);

						if (findByHsnOrSac == 0) {

							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(HSNORSAC);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									items.indexOf(item),
									errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1070", "Invalid HSN or SAC code",
									location));
						}
					}	
				}
			}
		}
		return errors;
	}

}
