package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.RateCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("Gstr1ALuIgstRateValidator")
public class Gstr1ALuIgstRateValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("DefaultRateCache")
	private RateCache rateRepository;
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		/*
		 * if (document.getSgstin() == null || document.getSgstin().isEmpty())
		 * return errors;
		 * 
		 * String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O8.name(); util =
		 * StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
		 * OnboardingQuestionValidationsUtil.class); //String paramtrvalue =
		 * util.valid(paramkryId, document.getSgstin()); Map<Long,
		 * List<EntityConfigPrmtEntity>> entityConfigParamMap = document
		 * .getEntityConfigParamMap(); String paramtrvalue =
		 * util.valid(entityConfigParamMap, paramkryId, document.getEntityId());
		 * if (paramtrvalue == null || paramtrvalue.isEmpty() || paramtrvalue
		 * .equalsIgnoreCase(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.D.name())) {
		 */ IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);

			rateRepository = StaticContextHolder.getBean("DefaultRateCache",
					RateCache.class);

			BigDecimal igstrate = item.getIgstRate();
			if (igstrate == null) {
				igstrate = BigDecimal.ZERO;
			}
			int n = rateRepository.findByIgst(igstrate);

			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.IGST_RATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0075",
						"Invalid IGST Rate", location));

			}
		});
		// }
		return errors;
	}

}
