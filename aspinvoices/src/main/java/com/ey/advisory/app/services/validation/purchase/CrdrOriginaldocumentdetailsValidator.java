package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
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
public class CrdrOriginaldocumentdetailsValidator
		implements DocRulesValidator<InwardTransDocument> {

	private static final List<String> CRDR = ImmutableList.of(GSTConstants.CR,
			GSTConstants.DR,GSTConstants.RCR,GSTConstants.RDR,GSTConstants.RNV);
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<InwardTransDocLineItem> items = document.getLineItems();

		List<ProcessingResult> errors = new ArrayList<>();
		
		if (document.getCgstin() == null || document.getCgstin().isEmpty())
			return errors;

		String paramkryId =GSTConstants.I2;
		util = StaticContextHolder
				.getBean("OnboardingQuestionValidationsUtil", 
						OnboardingQuestionValidationsUtil.class);
		//String paramtrvalue=util.valid(paramkryId, document.getCgstin());
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		if (paramtrvalue == null || paramtrvalue.isEmpty()
				|| paramtrvalue.equalsIgnoreCase(GSTConstants.C))
			return errors;
		if (document.getDocType() == null || document.getDocType().isEmpty())
			return errors;

		if (CRDR.contains(document.getDocType())) {
			IntStream.range(0, items.size()).forEach(idx -> {
				InwardTransDocLineItem item = items.get(idx);
				if (item.getOrigDocNo() == null || item.getOrigDocNo().isEmpty()
						|| item.getOrigDocDate() == null) {

					if (paramtrvalue.equalsIgnoreCase(GSTConstants.A)) {
						if (item.getOrigDocNo() == null 
								|| item.getOrigDocNo().isEmpty()
								) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
						TransDocProcessingResultLoc location 
						            = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1119",
									"Preceeding Invoice Number cannot "
									+ "be left Blank as per "
									+ "On-Boarding  parameter.",
										location));
						}
						if (item.getOrigDocDate() == null
								) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
						TransDocProcessingResultLoc location 
						            = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1120",
									"Preceeding Invoice Date cannot "
									+ "be left Blank as per On-Boarding  parameter.",
										location));
						}
					}

					if (paramtrvalue.equalsIgnoreCase(GSTConstants.B)) {
						if (item.getOrigDocNo() == null 
								|| item.getOrigDocNo().isEmpty()
								) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
						TransDocProcessingResultLoc location 
						            = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.INFO, "IN1001",
								"Original Document Number cannot "
									+ "be left Blank as per "
									+ "On-Boarding  parameter.",
								location));
						}
						if (item.getOrigDocDate() == null
								) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
						TransDocProcessingResultLoc location 
						            = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.INFO, "IN1002",
								"Original Document Date cannot be "
								+ "left Blank as per On-Boarding  parameter.",
								location));
						}
					}

				}

			});
		}
		return errors;
	}

}
