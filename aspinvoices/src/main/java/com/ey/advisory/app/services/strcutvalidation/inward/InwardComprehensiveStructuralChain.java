package com.ey.advisory.app.services.strcutvalidation.inward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.strcutvalidation.einvoice.BalanceAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.BatchExpiryDateValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.FreeQuantityValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.IRNDateValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.InvoiceAssessableAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.InvoiceCessAdvaloremAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.InvoiceCessSpecificAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.InvoiceCgstAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.InvoiceIgstAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.InvoiceOtherChargesValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.InvoiceSgstAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.InvoiceStateCessAdvaloremAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.InvoiceStateCessAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.ItemAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.ItemDiscountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.PaidAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.PaymentDueDateValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.PreTaxAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.SupplierStateCodeValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.TcsAmountIncomeTaxValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.TcsCgstAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.TcsRateIncomeTaxValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.TcsSgstAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.TdsCgstAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.TdsIgstAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.TdsSgstAmountValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.TotalItemAmtValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.UnitPriceValidation;
import com.ey.advisory.app.services.strcutvalidation.einvoice.WarrantyDateValidation;
import com.ey.advisory.app.services.strcutvalidation.outward.ComprehensiveInwardDocOtherStructuralValidations;
import com.ey.advisory.app.services.strcutvalidation.outward.DummyValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.InwardHeaderStructuralValidationUtil;
import com.ey.advisory.app.services.strcutvalidation.outward.ReceiptAdviceDateValidation;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva Krishna
 *
 */

@Slf4j
@Component("InwardComprehensiveStructuralChain")
public class InwardComprehensiveStructuralChain {
	
	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = 
			new ImmutableMap.Builder<Integer, ValidationRule[]>()

			.put(0, new ValidationRule[] { DummyValidationRule
					.getInstance() })
			.put(1, new ValidationRule[] { new IRNDateValidation() })
			.put(2, new ValidationRule[] { DummyValidationRule
					.getInstance() })
			.put(3, new ValidationRule[] { new SupTypeValidationRule() })
			.put(4, new ValidationRule[] { DummyValidationRule
					.getInstance() })
			.put(5, new ValidationRule[] { new DocumentTypeValidationRule() })
			.put(6, new ValidationRule[] { new DocumentNoValidationRule() })
			.put(7, new ValidationRule[] { new DocumentDateValidationRule() })
			.put(8, new ValidationRule[] {
					new ReverseChargeFlagValidationRule() })
			.put(9, new ValidationRule[] { new SuppliergstinValidationRule() })
			.put(10, new ValidationRule[] { DummyValidationRule
					.getInstance() })
			.put(11, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(12, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(13, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(14, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(15, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(16, new ValidationRule[] { new SupplierStateCodeValidation() })
			.put(17, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(18, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(19, new ValidationRule[] {
					new RecipientgstinValidationRule() })
			.put(20, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(21, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(22, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(23, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(24, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(25, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(26, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(27, new ValidationRule[] { new PosValidationRule() })
			
			.put(28, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(29, new ValidationRule[] {DummyValidationRule
					.getInstance()}) 
			.put(30, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(31, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(32, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(33, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(34, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(35, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(36, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(37, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(38, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(39, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(40, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(41, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(42, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(43, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(44, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(45, new ValidationRule[] { new LineNoValidationRule() })
			.put(46, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(47, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(48, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(49, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(50, new ValidationRule[] { new HsnSacValidationRule() })
			.put(51, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(52, new ValidationRule[] {DummyValidationRule
					.getInstance()})
			.put(53, new ValidationRule[] { new BatchExpiryDateValidation() })
			.put(54, new ValidationRule[] { new WarrantyDateValidation() })
			.put(55, new ValidationRule[] { DummyValidationRule
					.getInstance() }) 
			.put(56, new ValidationRule[] { DummyValidationRule
					.getInstance() }) 
			.put(57, new ValidationRule[] { DummyValidationRule
					.getInstance() }) 
			.put(58, new ValidationRule[] { DummyValidationRule
					.getInstance() })
			.put(59, new ValidationRule[] { DummyValidationRule
					.getInstance() })
			.put(60, new ValidationRule[] { new QuantityvalidationRule() })
			.put(61, new ValidationRule[] { new FreeQuantityValidation() })
			.put(62, new ValidationRule[] { new UnitPriceValidation() })
			.put(63, new ValidationRule[] { new ItemAmountValidation() })
			.put(64, new ValidationRule[] { new ItemDiscountValidation() })
			.put(65, new ValidationRule[] { new PreTaxAmountValidation() })
			.put(66, new ValidationRule[] { new TaxableValidationRule()})
			.put(67, new ValidationRule[] { new IgstRateValidationRule()})
			.put(68, new ValidationRule[] { new IgstAmountValidationRule()})
			.put(69, new ValidationRule[] { new CgstRateValidationRule()})
			.put(70, new ValidationRule[] { new CgstAmountValidationRule()})
			.put(71, new ValidationRule[] { new SgstRateValidationRule()})
			.put(72, new ValidationRule[] { new SgstAmountValidationRule()})
			.put(73, new ValidationRule[] { new AdvaloremCessRateValidationRule()})
			.put(74, new ValidationRule[] { new AdvaloremCessAmountValidationRule()})
			.put(75, new ValidationRule[] { new SpecificCessRateValidationRule()})
			.put(76, new ValidationRule[] { new SpecificessAmountValidationRule()})
			.put(77, new ValidationRule[] { new StateCessRateValidationRule()})
			.put(78, new ValidationRule[] { new StateCessAmountValidationRule()})
			.put(79, new ValidationRule[] {
					new StateCessAdvaloremRateValidationRule() })
			.put(80, new ValidationRule[] {
					new StateCessAdvaloremAmountValidationRule() })
			.put(81, new ValidationRule[] { new OtherValueValidationRule() })
			.put(82, new ValidationRule[] { new TotalItemAmtValidation() })
			.put(83, new ValidationRule[] {
					new InvoiceOtherChargesValidation() })
			.put(84, new ValidationRule[] {
					new InvoiceAssessableAmountValidation() })
			.put(85, new ValidationRule[] { new InvoiceIgstAmountValidation() })
			.put(86, new ValidationRule[] { new InvoiceCgstAmountValidation() })
			.put(87, new ValidationRule[] { new InvoiceSgstAmountValidation() })
			.put(88, new ValidationRule[] {
					new InvoiceCessAdvaloremAmountValidation() })
			.put(89, new ValidationRule[] {
					new InvoiceCessSpecificAmountValidation() })
			.put(90, new ValidationRule[] {
					new InvoiceStateCessAdvaloremAmountValidation() })
			.put(91, new ValidationRule[] {
					new InvoiceStateCessAmountValidation() }) 
			.put(92, new ValidationRule[] { new InvoicevalueValidationRule() })
			.put(93, new ValidationRule[] { new RoundOffValidationInward() }) 
			.put(94, new ValidationRule[] {DummyValidationRule.getInstance()}) 
			.put(95, new ValidationRule[] { new EligibilityIndicatorValidation()})
			.put(96, new ValidationRule[] { new CommonSupplyIndiValidationRule()})
			.put(97, new ValidationRule[] { new AvaliableIgstValidationRule()})
			.put(98, new ValidationRule[] { new AvaliableCgstValidationRule()})
			.put(99, new ValidationRule[] { new AvaliableSgstValidationRule()})
			.put(100, new ValidationRule[] { new AvaliableCessValidationRule()})
			.put(101, new ValidationRule[] { new ITCEntitlementValidationRule()})		
			.put(102, new ValidationRule[] { new ItcReverserValidationRule()})
			.put(103, new ValidationRule[] {DummyValidationRule.getInstance()})
			.put(104, new ValidationRule[] { new TcsRateIncomeTaxValidation() })
			.put(105, new ValidationRule[] {
					new TcsAmountIncomeTaxValidation() })
			.put(106, new ValidationRule[] {DummyValidationRule.getInstance()})
			.put(107, new ValidationRule[] {DummyValidationRule.getInstance()})
			.put(108, new ValidationRule[] { new InvoiceFCValidation() })
			.put(109, new ValidationRule[] {DummyValidationRule.getInstance()})
			.put(110,
					new ValidationRule[] {DummyValidationRule.getInstance()})
			.put(111,
					new ValidationRule[] {
							new BillOfEntryDatevalidationRule() })
			.put(112,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(113,
					new ValidationRule[] {
							new InvoicePeriodStartDateValidation() })
			.put(114,
					new ValidationRule[] {
							new InvoicePeriodEndDateValidation() })
			.put(115,
					new ValidationRule[] {
							new OrgDocumentNoValidationRule() })
			.put(116,
					new ValidationRule[] {
							new OrgDocumentDateValidationRule() })
			.put(117,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(118, new ValidationRule[] { DummyValidationRule
					.getInstance() })
			.put(119,
					new ValidationRule[] { new ReceiptAdviceDateValidation() })
			.put(120,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(121,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(122,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(123,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(124,
					new ValidationRule[] {
							DummyValidationRule.getInstance()})
			.put(125,
					new ValidationRule[] {
							new ContractdateValidationRule() })
			.put(126,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(127, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(128, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(129,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(130,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(131,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(132,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(133, new ValidationRule[] { DummyValidationRule.getInstance() })
			
			.put(134, new ValidationRule[] { new PaidAmountValidation() })
			.put(135, new ValidationRule[] { new BalanceAmountValidation() })
			.put(136, new ValidationRule[] { new PaymentDueDateValidation() })
			.put(137, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(138, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(139,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(140,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(141,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(142,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(143, new ValidationRule[] { DummyValidationRule.getInstance() }) 
																				
			.put(144, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(145,
					new ValidationRule[] { DummyValidationRule.getInstance() }) 
			.put(146, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(147,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(148, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(149, new ValidationRule[] { new TransportDocDateValidation() })
			.put(150, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(151, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(152, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(153, new ValidationRule[] { new ReturnPeriodValidationRule() })
			.put(154,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(155,
					new ValidationRule[] { DummyValidationRule.getInstance() }) 
			.put(156,
					new ValidationRule[] {
							new DifferentialFlagValidationRule() })
			.put(157,
					new ValidationRule[] {
							new Section7ofIGSTFlagValidationRule() })
			.put(158,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(159,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(160, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(161,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(162, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(163, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(164,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(165, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(166, new ValidationRule[] { new CifValidationRule() })
			.put(167, new ValidationRule[] { new CustomDutyValidationRule() })
			//.put(168, new ValidationRule[] { new ExchangeRateValidation() })
			.put(168, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(169,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(170, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(171, new ValidationRule[] { new TcsIgstAmountValidation() })
			.put(172, new ValidationRule[] { new TcsCgstAmountValidation() })
			.put(173, new ValidationRule[] { new TcsSgstAmountValidation() })
			.put(174, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(175, new ValidationRule[] { new TdsIgstAmountValidation() })
			.put(176, new ValidationRule[] { new TdsCgstAmountValidation() })
			.put(177, new ValidationRule[] { new TdsSgstAmountValidation() })
			.put(178,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(179,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(180,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(181,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(182, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(183, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(184,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(185, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(186, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(187, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(188, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(189, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(190, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(191, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(192, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(193, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(194, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(195, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(196,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(197,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(198,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(199,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			
			.put(200,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(201,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(202,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			
			.put(203, new ValidationRule[] { new PostingDateValidationRule() })
			
			.put(204,
					new ValidationRule[] { new ContractValueValidationRule() })
			.put(205,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(206, new ValidationRule[] { new EWayBillDateValidationRule() })
			.put(207,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(208,
					new ValidationRule[] {
							new PurchasevoucherDateValidationRule() })
			
			.put(209, new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(210,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(211,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(212,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(213,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(214,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(215,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(216,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(217,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(218,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(219,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(220,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(221,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(222,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(223,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(224,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(225,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(226,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(227,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(228,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(229,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(230,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(231,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(232,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(233,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(234,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(235,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(236,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(237,
					new ValidationRule[] { new UserDefined28Inward() })
			.put(238,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(239,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.build();

	
	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap) {

		Map<String, List<ProcessingResult>> map 
		          = new HashMap<String, List<ProcessingResult>>();

		rawDocMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();

			// First do normal structural valdiations (cell by cell)
			List<ProcessingResult> results = new ArrayList<>();

			for (Object[] obj : value) {

				for (int i = 0; i < 238; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);
					LOGGER.debug(rules.toString());
					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(
								value.indexOf(obj), cellVal, obj, null);
						results.addAll(errors);

					});
				}
			}

			// check for incompatible values across rows in the same colummn
			List<ProcessingResult> sameColummnErrors = 
					ComprehensiveInwardDocOtherStructuralValidations.validate(value);
			results.addAll(sameColummnErrors);
			List<ProcessingResult> errorsResult = InwardHeaderStructuralValidationUtil
					.eliminateDuplicates(results);
			if (errorsResult != null && errorsResult.size() > 0) {
				map.put(key, errorsResult);
			}
		});
		return map;

	}
}