package com.ey.advisory.app.services.strcutvalidation.itc04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Itc04StructuralValidatorChain")
@Slf4j
public class Itc04StructuralValidatorChain {

	@Autowired
	@Qualifier("Itc04HeaderStructuralValidations")
	private Itc04HeaderStructuralValidations strut;

	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = new ImmutableMap.Builder<Integer, ValidationRule[]>()
			.put(0, new ValidationRule[] { new TableNumberValidationRule() })
			.put(1, new ValidationRule[] { new ActionTypeValidationRule() })
			.put(2, new ValidationRule[] { new FYValidationRule() })
			.put(3, new ValidationRule[] { new RetPeriodValidationRule() })
			.put(4, new ValidationRule[] { new SgtinValidationRule() })
			.put(5, new ValidationRule[] {
					new DeliveryChallanNumberValidationRule() })
			.put(6, new ValidationRule[] {
					new DeliveryChallanDateValidationRule() })
			.put(7, new ValidationRule[] {
					new JWDeliveryChallanNumberValidationRule() })
			.put(8, new ValidationRule[] {
					new JWDeliveryChallanDateValidationRule() })
			.put(9, new ValidationRule[] {
					new GoodsReceiveDateValidationRule() })
			.put(10, new ValidationRule[] { new InvoiceNumberValidationRule() })
			.put(11, new ValidationRule[] { new InvoiceDateValidationRule() })
			.put(12, new ValidationRule[] { new JobWorkeGstinValidationRule() })
			.put(13, new ValidationRule[] {
					new JobWorkeStateCodeValidationRule() })
			.put(14, new ValidationRule[] { new JobWorkeTypeValidationRule() })
			.put(15, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(16, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(17, new ValidationRule[] { new TypesOfGoodsValidationRule() })
			.put(18, new ValidationRule[] {
					new ItemSerialNumberValidationRule() })
			.put(19, new ValidationRule[] {
					new ProductDescriptionValidationRule() })
			.put(20, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(21, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(22, new ValidationRule[] { new HsnValidationRule() })
			.put(23, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(24, new ValidationRule[] { new QuantityValidationRule() })
			.put(25, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(26, new ValidationRule[] {
					new LossessQuantityValidationRule() })
			.put(27, new ValidationRule[] {
					new ItemAccessableAmtValidationRule() })
			.put(28, new ValidationRule[] { new IgstRateValidationRule() })
			.put(29, new ValidationRule[] { new IgstAmtValidationRule() })
			.put(30, new ValidationRule[] { new CgstRateValidationRule() })
			.put(31, new ValidationRule[] { new CgstAmtValidationRule() })
			.put(32, new ValidationRule[] { new SgstRateValidationRule() })
			.put(33, new ValidationRule[] { new SgstAmtValidationRule() })
			.put(34, new ValidationRule[] { new CessAdvRateValidationRule() })
			.put(35, new ValidationRule[] { new CessAdvAmtValidationRule() })
			.put(36, new ValidationRule[] {
					new CessSpecificRateValidationRule() })
			.put(37, new ValidationRule[] {
					new CessSpecificAdvAmtValidationRule() })
			.put(38, new ValidationRule[] {
					new StateCessAdvRateValidationRule() })
			.put(39, new ValidationRule[] {
					new StateCessAdvAmtValidationRule() })
			.put(40, new ValidationRule[] {
					new StateSpecificRateValidationRule() })
			.put(41, new ValidationRule[] {
					new StateSpecificAmtValidationRule() })
			.put(42, new ValidationRule[] { new TotalValueAmtValidationRule() })
			.put(43, new ValidationRule[] { new PostingDateValidationRule() })
			.put(44, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(45, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(46, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(47, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(48, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(49, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(50, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(51, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(52, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(53, new ValidationRule[] {
					new AccountingVocharDateValidationRule() })
			.build();

	/**
	 * This
	 * 
	 * @param rawDoc
	 * @return
	 */

	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap) {

		Map<String, List<ProcessingResult>> map = new HashMap<String, List<ProcessingResult>>();

		rawDocMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();

			// First do normal structural valdiations (cell by cell)
			List<ProcessingResult> results = new ArrayList<>();

			for (Object[] obj : value) {

				for (int i = 0; i < 54; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						try {
							List<ProcessingResult> errors = rule.isValid(
									value.indexOf(obj), cellVal, obj, null);
							results.addAll(errors);
						} catch (Exception e) {
							String validatorCls = rule.getClass()
									.getSimpleName();

							String exName = e.getClass().getSimpleName();
							String msg = String.format(
									"Error while executing "
											+ "the validator '%s' for docKey:'%s'"
											+ "Exception: '%s'",
									validatorCls, key, exName);
							ProcessingResult result = new ProcessingResult(
									"LOCAL", ProcessingResultType.ERROR,
									"ER9999", msg, null);
							LOGGER.error(msg, e);
							results.add(result);
						}

					});
				}
			}
			// check for incompatible values across rows in the same colummn
			List<ProcessingResult> sameColummnErrors = strut.validate(value);

			results.addAll(sameColummnErrors);
			List<ProcessingResult> errorsResult = Itc04HeaderStructuralValidationUtil
					.eliminateDuplicates(results);

			if (errorsResult != null && errorsResult.size() > 0) {
				map.put(key, errorsResult);
			}
		});
		return map;

	}
}