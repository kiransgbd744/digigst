package com.ey.advisory.app.services.validation.purchase;

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

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
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
public class HsnOrSacMasterValidation
		implements DocRulesValidator<InwardTransDocument> {
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	
	public static final BigDecimal TWO = new BigDecimal("2");

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {

		List<InwardTransDocLineItem> items = document.getLineItems();

		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getCgstin() == null || document.getCgstin().isEmpty())
			return errors;

		String paramkryId = GSTConstants.I9;
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		//String paramtrvalue = util.valid(paramkryId, document.getCgstin());
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		String paramkryId10 = GSTConstants.I10;
		//String paramtrvalue10 = util.valid(paramkryId10, document.getCgstin());
		String paramtrvalue10 = util.valid(entityConfigParamMap, paramkryId10,
				document.getEntityId());
		if (paramtrvalue == null || paramtrvalue.isEmpty())
			return errors;

		if (paramtrvalue.equalsIgnoreCase(GSTConstants.A) 
				) {
			IntStream.range(0, items.size()).forEach(idx -> {
				InwardTransDocLineItem item = items.get(idx);
				if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {

					String regex = "^[0-9]*$";
					Pattern pattern = Pattern.compile(regex);

					Matcher matcher = pattern.matcher(item.getHsnSac().trim());
					if(matcher.matches()){
					Integer hsnOrsac = Integer.parseInt(item.getHsnSac());
					if (hsnOrsac > 0) {
						
			Map<String, List<Pair<Integer, BigDecimal>>> 
			                masterItemMap = document.getMasterItemMap();
			if(masterItemMap==null){
				if (paramtrvalue10 != null && paramtrvalue10
						.equalsIgnoreCase(GSTConstants.A)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(HSNORSAC);
					
					TransDocProcessingResultLoc location 
					     = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION,
							"ER1133",
							"HSN or SAC not as per Item Master "
							+ "provided at the time of On Boarding",
							location));
				}
				if (paramtrvalue10 != null && paramtrvalue10
						.equalsIgnoreCase(GSTConstants.B)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(HSNORSAC);
					
					TransDocProcessingResultLoc location 
					          = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(
							new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO,
									"IN1015",
								"HSN or SAC not as per Item Master "
								+ "provided at the time of On Boarding",
									location));
				}
			}
						if (masterItemMap != null) {

							Boolean valid = ProductMasterRateAndHsnValUtil
									.isValidHsn(masterItemMap, item.getHsnSac(),
											document.getSgstin());

							if (!valid) {

								if (paramtrvalue10 != null && paramtrvalue10
										.equalsIgnoreCase(GSTConstants.A)) {
									Set<String> errorLocations = new HashSet<>();
									errorLocations.add(HSNORSAC);

									TransDocProcessingResultLoc location 
									= new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION, "ER1133",
											"HSN or SAC not as per Item Master "
													+ "provided at the "
													+ "time of On Boarding",
											location));
								}
								if (paramtrvalue10 != null && paramtrvalue10
										.equalsIgnoreCase(GSTConstants.B)) {
									Set<String> errorLocations = new HashSet<>();
									errorLocations.add(HSNORSAC);

									TransDocProcessingResultLoc location 
									= new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION,
											ProcessingResultType.INFO, "IN1015",
											"HSN or SAC not as per Item Master "
													+ "provided at the "
													+ "time of On Boarding",
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

}
