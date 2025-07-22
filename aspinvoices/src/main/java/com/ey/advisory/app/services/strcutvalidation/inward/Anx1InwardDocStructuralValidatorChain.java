package com.ey.advisory.app.services.strcutvalidation.inward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.outward.DummyValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("Anx1InwardDocStructuralValidatorChain")
public class Anx1InwardDocStructuralValidatorChain {
	@Autowired
	@Qualifier("Anx1InwardDocOtherStructuralValidations")
private Anx1InwardDocOtherStructuralValidations strut;
	
	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES 
	= new ImmutableMap.Builder<Integer, ValidationRule[]>()
	
			.put(2, new ValidationRule[] { new NewProfitCentre() })
			.put(3, new ValidationRule[] { new NewPlant() })
			.put(4, new ValidationRule[] { new NewDivision() })
			.put(5, new ValidationRule[] { new NewLocation() })
			.put(6, new ValidationRule[] { new NewPurOrganisation() })
			.put(7, new ValidationRule[] { new UserAccess1() })
			.put(8, new ValidationRule[] { new UserAccess2() })
			.put(9, new ValidationRule[] { new UserAccess3() })
			.put(10, new ValidationRule[] { new UserAccess4() })
			.put(11, new ValidationRule[] { new UserAccess5() })
			.put(12, new ValidationRule[] { new UserAccess6() })
			.put(13, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(14, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(15, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(16, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(17, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(18, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(19, new ValidationRule[] { DummyValidationRule.getInstance() })
		    .put(20, new ValidationRule[] { new ReturnPeriodValidationRule() })
		.put(21, new ValidationRule[] { new RecipientgstinValidationRule() })
		.put(22, new ValidationRule[] { new DocumentTypeValidationRule() })
		.put(23, new ValidationRule[] { new SupTypeValidationRule() })
		.put(24, new ValidationRule[] { new DocumentNoValidationRule() })
		.put(25, new ValidationRule[] { new DocumentDateValidationRule() })
		.put(26, new ValidationRule[] { new OrgDocumentNoValidationRule() })
		.put(27, new ValidationRule[] { new OrgDocumentDateValidationRule() })
		.put(28, new ValidationRule[] { new CrDrPreGstValidationRule() })
		.put(29, new ValidationRule[] { new LineNoValidationRule() })
		.put(30, new ValidationRule[] { new SuppliergstinValidationRule() })
		.put(31, new ValidationRule[] { new SupplierTypeValidationRule()})
		.put(32, new ValidationRule[] { new DifferentialFlagValidationRule()})
		.put(33, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(34, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(35, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(36, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(37, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(38, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(39, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(40, new ValidationRule[] { new PosValidationRule() })
		.put(41, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(42, new ValidationRule[] { new PortCodevalidationrule()})
		.put(43, new ValidationRule[] { new BillOfEntryNumValidationRule()})
		.put(44, new ValidationRule[] { new BillOfEntryDatevalidationRule()})
		.put(45, new ValidationRule[] { new CifValidationRule() })
		.put(46, new ValidationRule[] { new CustomDutyValidationRule() })
		.put(47, new ValidationRule[] { new HsnSacValidationRule() })
		.put(48, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(49, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(50, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(51, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(52, new ValidationRule[] { new QuantityvalidationRule() })
		.put(53, new ValidationRule[] { new Section7ofIGSTFlagValidationRule()})
		.put(54, new ValidationRule[] { new TaxableValidationRule()})
		.put(55, new ValidationRule[] { new IgstRateValidationRule()})
		.put(56, new ValidationRule[] { new IgstAmountValidationRule()})
		.put(57, new ValidationRule[] { new CgstRateValidationRule()})
		.put(58, new ValidationRule[] { new CgstAmountValidationRule()})
		.put(59, new ValidationRule[] { new SgstRateValidationRule()})
		.put(60, new ValidationRule[] { new SgstAmountValidationRule()})
		.put(61, new ValidationRule[] { new AdvaloremCessRateValidationRule()})
		.put(62, new ValidationRule[] { new AdvaloremCessAmountValidationRule()})
		.put(63, new ValidationRule[] { new SpecificCessRateValidationRule()})
		.put(64, new ValidationRule[] { new SpecificessAmountValidationRule()})
		.put(65, new ValidationRule[] { new StateCessRateValidationRule()})
		.put(66, new ValidationRule[] { new StateCessAmountValidationRule()})
		.put(67, new ValidationRule[] { new OtherValueValidationRule()})
		.put(68, new ValidationRule[] { new InvoicevalueValidationRule()})
		.put(69, new ValidationRule[] { new ClaimRefundFlagValidationRule()})
		.put(70, new ValidationRule[] { new AutoPopulateToRefundValidationRule()})
		.put(71, new ValidationRule[] { new AdjustementReferenceNoValidationRule()})
		.put(72, new ValidationRule[] { new AdjustementReferenceDateValidationRule()})
		.put(73, new ValidationRule[] { new taxValueAdjValidationRule()})
		.put(74, new ValidationRule[] { new IntegratedTaxAmtAdjtValidationRule()})
		.put(75, new ValidationRule[] { new CentralTaxAmountAdjustedValidationRule()})
		.put(76, new ValidationRule[] { new StateUTTaxAmountAdjustedValidationRule()})
		.put(77, new ValidationRule[] { new AdvaloremCessAmountAdjustedValidationRule()})
		.put(78, new ValidationRule[] { new SpecificCessAmountAdjustedValidationRule()})
		.put(79, new ValidationRule[] { new StateCessAmountAdjustedValidationRule()})
		.put(80, new ValidationRule[] { new ReverseChargeFlagValidationRule()})
		.put(81, new ValidationRule[] { new EligibilityIndicatorValidation()})
		.put(82, new ValidationRule[] { new CommonSupplyIndiValidationRule()})
		.put(83, new ValidationRule[] { new AvaliableIgstValidationRule()})
		.put(84, new ValidationRule[] { new AvaliableCgstValidationRule()})
		.put(85, new ValidationRule[] { new AvaliableSgstValidationRule()})
		.put(86, new ValidationRule[] { new AvaliableCessValidationRule()})
		.put(87, new ValidationRule[] { new ITCEntitlementValidationRule()})		
		.put(88, new ValidationRule[] { new ItcReverserValidationRule()})
		.put(89, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(90, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(91, new ValidationRule[] { new PurchasevoucherDateValidationRule()})
		.put(92, new ValidationRule[] { new PostingDateValidationRule()})
		.put(93, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(94, new ValidationRule[] { new PaymentdateValidationRule()})
		.put(95, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(96, new ValidationRule[] { new ContractdateValidationRule()})
		.put(97, new ValidationRule[] { new ContractValueValidationRule()})
		.put(98, new ValidationRule[] {DummyValidationRule.getInstance()})
		.put(99, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(100, new ValidationRule[] {DummyValidationRule.getInstance()})
		.put(101, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(102, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(103, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(104, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(105, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(106, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(107, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(108, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(109, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(110, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(111, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(112, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(113, new ValidationRule[] { new EWayBillNumberValidationRule()})
		.put(114, new ValidationRule[] { new EWayBillDateValidationRule()})
			.build();

	/**
	 * This
	 * 
	 * @param rawDoc
	 * @return
	 */
	
	
	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap) {

		Map<String, List<ProcessingResult>> map = 
					new HashMap<String, List<ProcessingResult>>();
		
			
		  rawDocMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();
			
			
			// First do normal structural valdiations (cell by cell)
			List<ProcessingResult> results = new ArrayList<>();

			for (Object[] obj : value) {

				for (int i = 2; i < 115; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						
						try{
							
						List<ProcessingResult> errors = rule.isValid(value.indexOf(obj),cellVal,
								obj, null);
						results.addAll(errors);
						}
						catch(Exception e){
							String validatorCls = rule.getClass().getSimpleName();
							
							String exName = e.getClass().getSimpleName();
							String msg = String
									.format("Error while executing "
											+ "the validator '%s' for docKey:'%s'"
											+ "Exception: '%s'", validatorCls,key, exName);
							ProcessingResult result = new ProcessingResult("LOCAL",
									ProcessingResultType.ERROR, "ER9999", msg, null);
							LOGGER.error(msg, e);
							results.add(result);
						}

					});
				}
			}
			
			// check for incompatible values across rows in the same colummn
						List<ProcessingResult> errors1 = 
								     strut.validate(value);
						
						results.addAll(errors1);
			
						
						List<ProcessingResult> errorsResult=	
								HeaderInwardStructuralValidationUtil
								.eliminateDuplicates(results);
						
			if (errorsResult != null && errorsResult.size() > 0) {
				map.put(key, errorsResult);
			}
		});
		return map;

	}
}