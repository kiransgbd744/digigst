package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class LUgstr1HsnOrSacValidator 
                    implements DocRulesValidator<OutwardTransDocument> {
	
	@Autowired
	@Qualifier("DefaultHsnCache")
	private HsnCache hsnCache;

	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<OutwardTransDocLineItem> items = document.getLineItems();

		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		String paramkryId = GSTConstants.O8;
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		
	

		if (paramtrvalue == null || paramtrvalue.isEmpty() 
				|| paramtrvalue.equalsIgnoreCase(GSTConstants.D)) {
			IntStream.range(0, items.size()).forEach(idx -> {
				OutwardTransDocLineItem item = items.get(idx);

				hsnCache = StaticContextHolder.getBean(
						"DefaultHsnCache", HsnCache.class);
				if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {

					int hsn = Integer.parseInt(item.getHsnSac());
					if (hsn > 0) {
						String hsnOrSac = item.getHsnSac();
						int i = hsnCache
								.findhsn(hsnOrSac);
						
						
						if(i<=0){
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(HSNORSAC);
							TransDocProcessingResultLoc location 
							          = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0069", "Invalid HSN or SAC code",
									location));
						}
						if(i > 0 && item.getHsnSac().length()==2){
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(HSNORSAC);
							TransDocProcessingResultLoc location 
							          = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0069", "Invalid HSN or SAC code",
									location));
						}
						
					}
				}

			});
		}
		return errors;
	}

}
