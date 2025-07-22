package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
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
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
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
@Component("Gstr1ASupplyTypeTaxableValueValidation")
public class Gstr1ASupplyTypeTaxableValueValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	private static final List<String> SUPPLY_TYPE = ImmutableList
			.of(GSTConstants.TAX, GSTConstants.DXP, GSTConstants.DTA);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O2.name();
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);

		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());

		if (paramtrvalue == null || paramtrvalue.isEmpty())
			return errors;

		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if (SUPPLY_TYPE.contains(document.getSupplyType())) {
				IntStream.range(0, items.size()).forEach(idx -> {
					Gstr1AOutwardTransDocLineItem item = items.get(idx);
					BigDecimal taxableValue = item.getTaxableValue();
					if (taxableValue == null) {
						taxableValue = BigDecimal.ZERO;
					}
					if (taxableValue.compareTo(BigDecimal.ZERO) == 0) {
						if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
								.equalsIgnoreCase(paramtrvalue)) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.TAXABLE_VALUE);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0140",
									" Taxable Value cannot be left "
											+ "blank or 0 as per On-Boarding parameter.",
									location));
						}
						if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name()
								.equalsIgnoreCase(paramtrvalue)) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.TAXABLE_VALUE);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, "IN0001",
									"Taxable Value cannot be left "
											+ "blank or 0 as per On-Boarding parameter.",
									location));
						}
					}

				});

			}
		}
		return errors;
	}

}
