package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.Document;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("SalesOriginaldocumentdetailsValidator")
public class SalesOriginaldocumentdetailsValidator
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> CRDR = ImmutableList.of(GSTConstants.CR,
			GSTConstants.DR);

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<OutwardTransDocLineItem> items = document.getLineItems();

		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O3.name();
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		//String paramtrvalue = util.valid(paramkryId, document.getSgstin());

		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		
		if (paramtrvalue == null || paramtrvalue.isEmpty() || paramtrvalue
				.equalsIgnoreCase(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.C.name()))
			return errors;
		if (document.getDocType() == null || document.getDocType().isEmpty())
			return errors;

		if (CRDR.contains(document.getDocType())) {
			IntStream.range(0, items.size()).forEach(idx -> {
				OutwardTransDocLineItem item = items.get(idx);
				if (item.getOrigDocNo() == null || item.getOrigDocNo().isEmpty()
						|| item.getOrigDocDate() == null) {

					if (paramtrvalue.equalsIgnoreCase(
							CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name())) {
						if (item.getPreceedingInvoiceNumber() == null
								|| item.getPreceedingInvoiceNumber().isEmpty()) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
							TransDocProcessingResultLoc location 
							     = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0138",
									"Original Document Number cannot "
											+ "be left Blank as per "
											+ "On-Boarding  parameter.",
									location));
						}
						if (item.getPreceedingInvoiceDate() == null) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
							TransDocProcessingResultLoc location 
							        = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0139",
									"Original Document Date cannot "
											+ "be left Blank as per "
											+ "On-Boarding  parameter.",
									location));
						}
					}

					if (paramtrvalue.equalsIgnoreCase(
							CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name())) {
						if (item.getPreceedingInvoiceNumber() == null
								|| item.getPreceedingInvoiceNumber().isEmpty()) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
							TransDocProcessingResultLoc location 
							     = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, " IN0025",
									"Original Document Number cannot "
											+ "be left Blank as per "
											+ "On-Boarding  parameter.",
									location));
						}
						if (item.getPreceedingInvoiceDate() == null) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
							TransDocProcessingResultLoc location 
							    = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, " IN0026",
									"Original Document Date cannot be "
											+ "left Blank as per On-Boarding "
											+ " parameter.",
									location));
						}
					}

				}

			});
		}
		return errors;
	}

}
