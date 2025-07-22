package com.ey.advisory.app.services.strcutvalidation.outward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.b2c.StateApplyingCessValidationRule;
import com.ey.advisory.app.services.strcutvalidation.b2c.StateCessAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.b2c.StateCessRateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.einvoice.UnitOfMeasurementValidation;
import com.ey.advisory.app.services.strcutvalidation.sales.CgstinValidationRule;
import com.ey.advisory.app.services.strcutvalidation.sales.HSNSACValidationRule;
import com.ey.advisory.app.services.strcutvalidation.sales.InvoiceValueValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.google.common.collect.ImmutableMap;

@Component("Anx1OutwardDocStructuralValidatorChain")
public class Anx1OutwardDocStructuralValidatorChain {
	@Autowired
	@Qualifier("Anx1OutwardDocOtherStructuralValidations")
private Anx1OutwardDocOtherStructuralValidations strut;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1OutwardDocStructuralValidatorChain.class);
	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES 
	= new ImmutableMap.Builder<Integer, ValidationRule[]>()
	
			.put(2, new ValidationRule[] { new NewProfitCentre() })
			.put(3, new ValidationRule[] { new NewPlant() })
			.put(4, new ValidationRule[] { new NewDivision() })
			.put(5, new ValidationRule[] { new NewLocation() })
			.put(6, new ValidationRule[] { new NewSalesOrganisation() })
			.put(7, new ValidationRule[] { new NewDistributionChannel() })
			.put(8, new ValidationRule[] { new UserAccess1() })
			.put(9, new ValidationRule[] { new UserAccess2() })
			.put(10, new ValidationRule[] { new UserAccess3() })
			.put(11, new ValidationRule[] { new UserAccess4() })
			.put(12, new ValidationRule[] { new UserAccess5() })
			.put(13, new ValidationRule[] { new UserAccess6() })
			.put(14, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(15, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(16, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(17, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(18, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(19, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(20, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(21, new ValidationRule[] { new ReturnPeriodValidationRule() })
		.put(22, new ValidationRule[] { new SuppliergstinValidationRule() })
		.put(23, new ValidationRule[] { new DocumentTypeValidationRule() })
		.put(24, new ValidationRule[] { new SupTypeValidationRule() })
		.put(25, new ValidationRule[] { new DocumentNoValidationRule() })
		.put(26, new ValidationRule[] { new DocumentDateValidationRule() })
		.put(27, new ValidationRule[] { new OriginalDocTypeValidationRule() })
		.put(28, new ValidationRule[] { new OrgDocumentNoValidationRule() })
		.put(29, new ValidationRule[] { new OrgDocumentDateValidationRule() })
		.put(30, new ValidationRule[] { new CrDrPreGstValidationRule() })
		.put(31, new ValidationRule[] { new LineNoValidationRule() })
		.put(32, new ValidationRule[] { new CgstinValidationRule() })
		.put(33, new ValidationRule[] { new RecipientTypeValidationRule()})
		.put(34, new ValidationRule[] { new DifferentialFlagValidationRule()})
		
		.put(35, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(36, new ValidationRule[] {  DummyValidationRule.getInstance() })
		.put(37, new ValidationRule[] {DummyValidationRule.getInstance() })
		.put(38, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(39, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(40, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(41, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(42, new ValidationRule[] { new BillToStateValidationRule()})
		.put(43, new ValidationRule[] { new ShipToStateValidationRule()})
		.put(44, new ValidationRule[] { new PosValidationRule() })
		.put(45, new ValidationRule[] { new StateApplyingCessValidationRule() })
		.put(46, new ValidationRule[] { new PortCodevalidationrule()})
		.put(47, new ValidationRule[] { new ShippingBillNumValidationRule()})
		.put(48, new ValidationRule[] { new ShippingBillDatevalidationRule()})
		.put(49, new ValidationRule[] { new FobValidationRule() })
		.put(50, new ValidationRule[] { new ExportDutyValidationRule() })
		.put(51, new ValidationRule[] { new HSNSACValidationRule() })
		.put(52, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(53, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(54, new ValidationRule[] { DummyValidationRule.getInstance() })
		.put(55, new ValidationRule[] { new UnitOfMeasurementValidation() })
		.put(56, new ValidationRule[] { new QuantityvalidationRule() })
		.put(57, new ValidationRule[] { new Section7ofIGSTFlagValidationRule()})
		.put(58, new ValidationRule[] { new TaxableValidationRule()})
		.put(59, new ValidationRule[] { new IgstRateValidationRule()})
		.put(60, new ValidationRule[] { new IgstAmountValidationRule()})
		.put(61, new ValidationRule[] { new CgstRateValidationRule()})
		.put(62, new ValidationRule[] { new CgstAmountValidationRule()})
		.put(63, new ValidationRule[] { new SgstRateValidationRule()})
		.put(64, new ValidationRule[] { new SgstAmountValidationRule()})
		.put(65, new ValidationRule[] { new AdvaloremCessRateValidationRule()})
		.put(66, new ValidationRule[] { new AdvaloremCessAmountValidationRule()})
		.put(67, new ValidationRule[] { new SpecificCessRateValidationRule()})
		.put(68, new ValidationRule[] { new SpecificCessAmountValidationRule()})
		.put(69, new ValidationRule[] { new StateCessRateValidationRule()})
		.put(70, new ValidationRule[] { new StateCessAmountValidationRule()})
		.put(71, new ValidationRule[] { new OtherValueValidationRule()})
		.put(72, new ValidationRule[] { new InvoiceValueValidationRule()})
		.put(73, new ValidationRule[] { new AdjustementReferenceNoValidationRule()})
		.put(74, new ValidationRule[] { new AdjustementReferenceDateValidationRule()})
		.put(75, new ValidationRule[] { new taxValueAdjValidationRule()})
		.put(76, new ValidationRule[] { new IntegratedTaxAmtAdjtValidationRule()})
		.put(77, new ValidationRule[] { new CentralTaxAmountAdjustedValidationRule()})
		.put(78, new ValidationRule[] { new StateUTTaxAmountAdjustedValidationRule()})
		.put(79, new ValidationRule[] { new AdvaloremCessAmountAdjustedValidationRule()})
		.put(80, new ValidationRule[] { new SpecificCessAmountAdjustedValidationRule()})
		.put(81, new ValidationRule[] { new StateCessAmountAdjustedValidationRule()})
		.put(82, new ValidationRule[] { new ReverseChargeFlagValidationRule()})
		.put(83, new ValidationRule[] { new TCSFlagValidationRule()})
		.put(84, new ValidationRule[] { new eComGSTINValidationRule()})
		.put(85, new ValidationRule[] { new TCSAmountValidationRule()})
		.put(86, new ValidationRule[] { new ITCFlagValidationRule()})
		.put(87, new ValidationRule[] { new ClaimRefundFlagValidationRule()})
		.put(88, new ValidationRule[] { new AutoPopulateToRefundValidationRule()})
		.put(89, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(90, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(91, new ValidationRule[] { new AccountingVoucDateValidationRule()})
		.put(92, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(93, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(94, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(95, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(96, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(97, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(98, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(99, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(100, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(101, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(102, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(103, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(104, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(105, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(106, new ValidationRule[] { DummyValidationRule.getInstance()})
		.put(107, new ValidationRule[] { new EWayBillNumberValidationRule()})
		.put(108, new ValidationRule[] { new EWayBillDateValidationRule()})
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
      
				for (int i = 2; i < 109; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);

					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						try{
						List<ProcessingResult> errors = rule.
								isValid(value.indexOf(obj),cellVal,
								obj, null);
						results.addAll(errors);
						}catch(Exception e){
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
						List<ProcessingResult> sameColummnErrors = 
								     strut.validate(value);
						
						results.addAll(sameColummnErrors);
						List<ProcessingResult> errorsResult=	
								HeaderStructuralValidationUtil
								.eliminateDuplicates(results);
			
			if (errorsResult != null && errorsResult.size() > 0) {
				map.put(key, errorsResult);
			}
		});
		return map;

	}
}