package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@Component("Gstr1AHsnAndRateProductMasterValidator")
public class Gstr1AHsnAndRateProductMasterValidator
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

		String paramkryId = GSTConstants.O8;
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

		if (paramtrvalue.equalsIgnoreCase(GSTConstants.C)) {
			IntStream.range(0, items.size()).forEach(idx -> {
				Gstr1AOutwardTransDocLineItem item = items.get(idx);
				boolean isIntra = isIntraState(document);
				if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {
					String regex = "^[0-9]*$";
					Pattern pattern = Pattern.compile(regex);

					Matcher matcher = pattern.matcher(item.getHsnSac().trim());
					if (matcher.matches()) {
						Integer hsnOrsac = Integer.parseInt(item.getHsnSac());
						if (hsnOrsac > 0) {

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
								if (paramtrvalue9 != null && paramtrvalue9
										.equalsIgnoreCase(GSTConstants.A)) {
									Set<String> errorLocations = new HashSet<>();
									errorLocations.add(HSNORSAC);
									errorLocations.add(GSTConstants.IGST_RATE);
									errorLocations.add(GSTConstants.CGST_RATE);
									errorLocations.add(GSTConstants.SGST_RATE);
									TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION, "ER0521",
											"HSN + Rate combination provided "
													+ "is not as per Product Master",
											location));
								}
								if (paramtrvalue9 != null && paramtrvalue9
										.equalsIgnoreCase(GSTConstants.B)) {
									Set<String> errorLocations = new HashSet<>();
									errorLocations.add(GSTConstants.IGST_RATE);
									errorLocations.add(GSTConstants.CGST_RATE);
									errorLocations.add(GSTConstants.SGST_RATE);
									errorLocations.add(HSNORSAC);
									TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION,
											ProcessingResultType.INFO, "IN0508",
											"HSN + Rate combination provided "
													+ "is not as per Product Master",
											location));

								}
							}
							if (map != null) {
								boolean igstValid = ProductMasterRateAndHsnValUtil
										.isValidHsnAndRate(map, hsnOrsac,
												igstRate, document.getSgstin());

								boolean cgstValid = ProductMasterRateAndHsnValUtil
										.isValidHsnAndRate(map, hsnOrsac,
												cgstRatemul,
												document.getSgstin());

								boolean sgstValid = ProductMasterRateAndHsnValUtil
										.isValidHsnAndRate(map, hsnOrsac,
												sgstRatemul,
												document.getSgstin());

								if ((!isIntra && !igstValid)
										|| (isIntra && !cgstValid)
										|| (isIntra && !sgstValid)) {
									if (paramtrvalue9 != null && paramtrvalue9
											.equalsIgnoreCase(GSTConstants.A)) {
										Set<String> errorLocations = new HashSet<>();
										errorLocations.add(HSNORSAC);
										errorLocations
												.add(GSTConstants.IGST_RATE);
										errorLocations
												.add(GSTConstants.CGST_RATE);
										errorLocations
												.add(GSTConstants.SGST_RATE);
										TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
												idx, errorLocations.toArray());
										errors.add(new ProcessingResult(
												APP_VALIDATION, "ER0521",
												"HSN + Rate combination provided "
														+ "is not as per Product Master",
												location));
									}
									if (paramtrvalue9 != null && paramtrvalue9
											.equalsIgnoreCase(GSTConstants.B)) {
										Set<String> errorLocations = new HashSet<>();
										errorLocations
												.add(GSTConstants.IGST_RATE);
										errorLocations
												.add(GSTConstants.CGST_RATE);
										errorLocations
												.add(GSTConstants.SGST_RATE);
										errorLocations.add(HSNORSAC);
										TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
												idx, errorLocations.toArray());
										errors.add(new ProcessingResult(
												APP_VALIDATION,
												ProcessingResultType.INFO,
												"IN0508",
												"HSN + Rate combination provided "
														+ "is not as per Product Master",
												location));
									}
								}
							}
						}
					}
				}
			});
		}

		return errors;

	}

	private String getFirst2CharsOfSgstin(Gstr1AOutwardTransDocument doc) {
		return doc.getSgstin().substring(0, 2);
	}

	private boolean isIntraState(Gstr1AOutwardTransDocument doc) {
		String first2Chars = getFirst2CharsOfSgstin(doc);
		return first2Chars.equals(doc.getPos());
	}
}
