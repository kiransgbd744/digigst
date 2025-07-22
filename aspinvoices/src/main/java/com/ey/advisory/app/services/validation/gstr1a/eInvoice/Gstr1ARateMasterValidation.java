package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

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
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
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
@Component("Gstr1ARateMasterValidation")
public class Gstr1ARateMasterValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	public static final BigDecimal TWO = new BigDecimal("2");

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
				.equalsIgnoreCase(CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name())) {
			IntStream.range(0, items.size()).forEach(idx -> {
				Gstr1AOutwardTransDocLineItem item = items.get(idx);
				BigDecimal igstRate = item.getIgstRate();
				BigDecimal cgstRate = item.getCgstRate();
				BigDecimal sgstRate = item.getSgstRate();
				if (igstRate == null) {
					igstRate = BigDecimal.ZERO;
				}

				if (cgstRate == null) {
					cgstRate = BigDecimal.ZERO;
				}

				if (sgstRate == null) {
					sgstRate = BigDecimal.ZERO;
				}
				BigDecimal cgstRatemul = cgstRate.multiply(TWO);
				BigDecimal sgstRatemul = sgstRate.multiply(TWO);

				Map<String, List<Pair<Integer, BigDecimal>>> map = document
						.getMasterProductMap();
				if (map == null) {

					if (paramtrvalue9 != null && paramtrvalue9.equalsIgnoreCase(
							CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name())) {
						if (igstRate.compareTo(BigDecimal.ZERO) > 0) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.IGST_RATE);

							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0147",
									"IGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
						}
						if (sgstRate.compareTo(BigDecimal.ZERO) > 0) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.SGST_RATE);

							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0149",
									"sGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
						}
						if (cgstRate.compareTo(BigDecimal.ZERO) > 0) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.CGST_RATE);

							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0148",
									"cGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
						}
					} else if (paramtrvalue9 != null
							&& paramtrvalue9.equalsIgnoreCase(
									CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name())) {
						if (igstRate.compareTo(BigDecimal.ZERO) > 0) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.IGST_RATE);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, "IN0022",
									"IGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
						}
						if (sgstRate.compareTo(BigDecimal.ZERO) > 0) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.SGST_RATE);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, "IN0024",
									"SGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
						}
						if (cgstRate.compareTo(BigDecimal.ZERO) > 0) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.CGST_RATE);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO, "IN0023",
									"CGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
						}
					}

				}
				if (map != null) {
					Boolean igstvalid = ProductMasterRateAndHsnValUtil
							.isValidRate(map, igstRate, document.getSgstin());

					Boolean cgstvalid = ProductMasterRateAndHsnValUtil
							.isValidRate(map, cgstRatemul,
									document.getSgstin());
					Boolean sgstvalid = ProductMasterRateAndHsnValUtil
							.isValidRate(map, sgstRatemul,
									document.getSgstin());

					if (paramtrvalue9 != null && paramtrvalue9.equalsIgnoreCase(
							CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name())) {

						if (igstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!igstvalid) {

								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.IGST_RATE);

								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0147",
										"IGST Rate not as per Product "
												+ "Master provided at the time "
												+ "of On Boarding",
										location));
							}
						}
						if (cgstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!cgstvalid) {

								Set<String> errorLocations = new HashSet<>();

								errorLocations.add(GSTConstants.CGST_RATE);

								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0148",
										"CGST Rate not as per Product "
												+ "Master provided at the "
												+ "time of On Boarding",
										location));
							}
						}
						if (sgstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!sgstvalid) {

								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.SGST_RATE);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER0149",
										"SGST Rate not as per Product "
												+ "Master provided at the "
												+ "time of On Boarding",
										location));
							}

						}

					} else if (paramtrvalue9 != null
							&& paramtrvalue9.equalsIgnoreCase(
									CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name())) {
						if (igstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!igstvalid) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.IGST_RATE);

								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.INFO, "IN0022",
										"IGST Rate not as per Product "
												+ "Master provided at the time "
												+ "of On Boarding",
										location));
							}
						}
						if (cgstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!cgstvalid) {
								Set<String> errorLocations = new HashSet<>();

								errorLocations.add(GSTConstants.CGST_RATE);

								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.INFO, "IN0023",
										"CGST Rate not as per Product "
												+ "Master provided at the time "
												+ "of On Boarding",
										location));
							}
						}
						if (sgstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!sgstvalid) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.SGST_RATE);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.INFO, "IN0024",
										"SGST Rate not as per Product Master"
												+ " provided at the time "
												+ "of On Boarding",
										location));
							}
						}
					}

				}
			});
		}

		return errors;

	}

}
