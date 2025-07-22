package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.sales.ProductMasterRateAndHsnValUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
@Component("Gstr1AHsnProductMasterValidation")
public class Gstr1AHsnProductMasterValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {

		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O8.name();
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		String paramkryId9 = GSTConstants.O9;

		String paramtrvalue9 = util.valid(entityConfigParamMap, paramkryId9,
				document.getEntityId());
		if (paramtrvalue == null || paramtrvalue.isEmpty())
			return errors;

		if (paramtrvalue
				.equalsIgnoreCase(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name())) {
			IntStream.range(0, items.size()).forEach(idx -> {
				Gstr1AOutwardTransDocLineItem item = items.get(idx);
				if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {

					/*
					 * Integer hsnOrsac = Integer.parseInt(item.getHsnSac()); if
					 * (hsnOrsac > 0) {
					 */
					Map<String, List<Pair<Integer, BigDecimal>>> map = document
							.getMasterProductMap();
					if (map == null) {

						if (paramtrvalue9 != null
								&& paramtrvalue9.equalsIgnoreCase(
										CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
												.name())) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(HSNORSAC);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0151",
									"HSN or SAC is not as per "
											+ "master provided during On-Boarding.",
									location));
						}
						if (paramtrvalue9 != null
								&& paramtrvalue9.equalsIgnoreCase(
										CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B
												.name())) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(HSNORSAC);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, "IN0009",
									"HSN or SAC is not as per "
											+ "master provided during On-Boarding.",
									location));
						}

					}
					if (map != null) {
						Boolean valid = ProductMasterRateAndHsnValUtil
								.isValidHsn(map, item.getHsnSac(),
										document.getSgstin());

						if (!valid) {
							if (paramtrvalue9 != null
									&& paramtrvalue9.equalsIgnoreCase(
											CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
													.name())) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(HSNORSAC);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0151",
										"HSN or SAC is not as per "
												+ "master provided during On-Boarding.",
										location));
							}
							if (paramtrvalue9 != null
									&& paramtrvalue9.equalsIgnoreCase(
											CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B
													.name())) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(HSNORSAC);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.INFO, "IN0009",
										"HSN or SAC is not as per "
												+ "master provided during On-Boarding.",
										location));
							}
						}
					}
					// }
				}

			});
		}

		return errors;

	}

}
