package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.RateCache;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
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
public class CgstRateValidator
		implements DocRulesValidator<InwardTransDocument> {

	@Autowired
	@Qualifier("DefaultRateCache")
	private RateCache rateRepository;
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<InwardTransDocLineItem> items = document.getLineItems();
	/*	if (document.getCgstin() == null || document.getCgstin().isEmpty())
			return errors;

		String paramkryId = GSTConstants.I9;
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		//String paramtrvalue = util.valid(paramkryId, document.getCgstin());
		
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());

		if (paramtrvalue == null || paramtrvalue.isEmpty() 
				|| paramtrvalue.equalsIgnoreCase(GSTConstants.D)) {*/
			IntStream.range(0, items.size()).forEach(idx -> {
				InwardTransDocLineItem item = items.get(idx);

				rateRepository = StaticContextHolder.getBean("DefaultRateCache",
						RateCache.class);

				BigDecimal cgstrate = item.getCgstRate();
				if (cgstrate == null) {
					cgstrate = BigDecimal.ZERO;
				}
				
				
				int n = rateRepository.findByCgst(cgstrate);

				if (n <= 0) {
					
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.CGST_RATE);
						TransDocProcessingResultLoc location 
						        = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER1077", "Invalid CGST Rate", location));
					
					
				}

			});
		//}
		return errors;
	}

}
