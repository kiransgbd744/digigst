package com.ey.advisory.app.services.strcutvalidation.einvoice;

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
import com.ey.advisory.app.services.strcutvalidation.outward.AccountingVoucDateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.AdvaloremCessAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.AdvaloremCessRateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.AutoPopulateToRefundValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.BranchOrIfscCodeValidation;
import com.ey.advisory.app.services.strcutvalidation.outward.CgstAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.CgstRateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.ClaimRefundFlagValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.ComprehensiveOutwardDocOtherStructuralValidations;
import com.ey.advisory.app.services.strcutvalidation.outward.CrDrPreGstValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.CustomerEmailValidation;
import com.ey.advisory.app.services.strcutvalidation.outward.DifferentialFlagValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.DocRefNoValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.DocumentDateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.DocumentNoValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.DocumentTypeValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.DummyValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.EWayBillDateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.EWayBillNumberValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.EinvoiceHeaderStructuralValidationUtil;
import com.ey.advisory.app.services.strcutvalidation.outward.ExportDutyValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.FobValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.ITCFlagValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.IgstAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.IgstRateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.LineNoValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.NewDistributionChannel;
import com.ey.advisory.app.services.strcutvalidation.outward.NewDivision;
import com.ey.advisory.app.services.strcutvalidation.outward.NewLocation;
import com.ey.advisory.app.services.strcutvalidation.outward.NewPlant;
import com.ey.advisory.app.services.strcutvalidation.outward.NewProfitCentre;
import com.ey.advisory.app.services.strcutvalidation.outward.NewSalesOrganisation;
import com.ey.advisory.app.services.strcutvalidation.outward.OriginalDocTypeValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.OtherSupplyTypeDescValidation;
import com.ey.advisory.app.services.strcutvalidation.outward.PortCodevalidationrule;
import com.ey.advisory.app.services.strcutvalidation.outward.ReceiptAdviceDateValidation;
import com.ey.advisory.app.services.strcutvalidation.outward.RecipientGgstinValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.ReturnPeriodValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.ReverseChargeFlagValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.Section7ofIGSTFlagValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.SgstAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.SgstRateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.ShippingBillDatevalidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.ShippingBillNumValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.SpecificCessAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.SpecificCessRateValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.SupTypeValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.TCSAmountValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.TCSFlagValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.TransctionTypeValidation;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.app.services.strcutvalidation.outward.eComGSTINValidationRule;
import com.ey.advisory.app.services.strcutvalidation.sales.HSNSACValidationRule;
import com.ey.advisory.app.services.strcutvalidation.sales.InvoiceValueValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("ComprehensiveStructuralChain")
public class ComprehensiveStructuralChain {

	@Autowired
	@Qualifier("ComprehensiveOutwardDocOtherStructuralValidations")
	private ComprehensiveOutwardDocOtherStructuralValidations strut;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ComprehensiveStructuralChain.class);
	private static final Map<Integer, ValidationRule[]> STRUCT_VAL_RULES = 
			new ImmutableMap.Builder<Integer, ValidationRule[]>()

			.put(0, new ValidationRule[] { new IRNValidation() })
			.put(1, new ValidationRule[] { new IRNDateValidation() })
			.put(2, new ValidationRule[] { new TaxSchemeValidation() })
																		
			.put(3, new ValidationRule[] { new CanReasonValidation() })
																		
			.put(4, new ValidationRule[] { DummyValidationRule
					.getInstance()})
			.put(5, new ValidationRule[] { new SupTypeValidationRule() })
			.put(6, new ValidationRule[] { new DocCategoryValidation() })
			.put(7, new ValidationRule[] { new DocumentTypeValidationRule() })
			.put(8, new ValidationRule[] { new DocumentNoValidationRule() })
			.put(9, new ValidationRule[] { new DocumentDateValidationRule() })
			.put(10, new ValidationRule[] {
					new ReverseChargeFlagValidationRule() })
			.put(11, new ValidationRule[] { new EInvoiceSuppliergstinValidationRule() })
			.put(12, new ValidationRule[] { new SupplierTradeValidation() })
			.put(13, new ValidationRule[] {
					new SupplierLegalValidation() })
			.put(14, new ValidationRule[] { new SuppAddress1Validation() })
																				
			.put(15, new ValidationRule[] { new SuppAddress2Validation() })
			.put(16, new ValidationRule[] {
					new SupplierLocationValidation() })
			.put(17, new ValidationRule[] { new SupplierPinCodeValidation() })
			.put(18, new ValidationRule[] { new SupplierStateCodeValidation() })
			.put(19, new ValidationRule[] { new SupplierPhoneValidation() })
			.put(20, new ValidationRule[] { new SuppliermailValidation() })
			.put(21, new ValidationRule[] {
					new RecipientGgstinValidationRule() })
			.put(22, new ValidationRule[] { new CustomerTradeName()})
			.put(23, new ValidationRule[] {
					new CustomerLegalName() })
			.put(24, new ValidationRule[] { new CustomerAddress1() })
			.put(25, new ValidationRule[] { new CustomerAddress2() })
			.put(26, new ValidationRule[] {
					new CustomerLocation() })
			.put(27, new ValidationRule[] { new CustomerPinCodeValidation() })
			.put(28, new ValidationRule[] {
					new EinvBillToStateValidationRule() })//CustomerStateCode
			.put(29, new ValidationRule[] { new EInvPosValidationRule() })
			.put(30, new ValidationRule[] {
					new CustomerPhoneValidation() })
			.put(31, new ValidationRule[] {
					new CustomerEmailValidation()}) 
			.put(32, new ValidationRule[] { new DispatcherGstinValidation() })
			.put(33, new ValidationRule[] { new DispatcherTradeName()})
			.put(34, new ValidationRule[] { new DispatcherAddress1() })
			.put(35, new ValidationRule[] { new DispatcherAddress2() })
			.put(36, new ValidationRule[] { new DispatcherLocation()})
			.put(37, new ValidationRule[] { new DispatcherPinCodeValidation() })
			.put(38, new ValidationRule[] {
					new DispatcherStateCodeValidation() })
			.put(39, new ValidationRule[] { new ShipToGstinValidation() })
			.put(40, new ValidationRule[] { new ShipToTradeName()})
			.put(41, new ValidationRule[] { new ShipToLegalName() })
			.put(42, new ValidationRule[] { new ShipToAddress1() })
			.put(43, new ValidationRule[] { new ShipToAddress2() })
			.put(44, new ValidationRule[] { new ShipToLocation()})
			.put(45, new ValidationRule[] { new ShipToPinCodeValidation() })
			.put(46, new ValidationRule[] { new ShipToStateCodeValidation() })
			.put(47, new ValidationRule[] { new LineNoValidationRule() })
			.put(48, new ValidationRule[] {  new SerialNumber2Validation()  })
			.put(49, new ValidationRule[] { new ProductNameValidation() })
			.put(50, new ValidationRule[] { new ProductDescValidationRule() })
			.put(51, new ValidationRule[] { new IsServiceValidation() })
			.put(52, new ValidationRule[] { new HSNSACValidationRule() })
			.put(53, new ValidationRule[] { new BarcodeValidationRule() })
			.put(54, new ValidationRule[] { new BatchNameValidationRule() })
			.put(55, new ValidationRule[] { new BatchExpiryDateValidation() })
			.put(56, new ValidationRule[] { new WarrantyDateValidation() })
			.put(57, new ValidationRule[] { DummyValidationRule
					.getInstance() }) 
																		
			.put(58, new ValidationRule[] { DummyValidationRule
					.getInstance() }) 
																			
			.put(59, new ValidationRule[] { DummyValidationRule
					.getInstance() }) 
																			
			.put(60, new ValidationRule[] { new OriginCountryValidation() })
			.put(61, new ValidationRule[] { new UnitOfMeasurementValidation() })
			.put(62, new ValidationRule[] { new QuantityValidation() })
			.put(63, new ValidationRule[] { new FreeQuantityValidation() })
			.put(64, new ValidationRule[] { new UnitPriceValidation() })
			.put(65, new ValidationRule[] { new ItemAmountValidation() })
			.put(66, new ValidationRule[] { new ItemDiscountValidation() })
			.put(67, new ValidationRule[] { new PreTaxAmountValidation() })
			.put(68, new ValidationRule[] {
					new ItemAssessableAmountValidation() })
			.put(69, new ValidationRule[] { new IgstRateValidationRule() })
			.put(70, new ValidationRule[] { new IgstAmountValidationRule() })
			.put(71, new ValidationRule[] { new CgstRateValidationRule() })
			.put(72, new ValidationRule[] { new CgstAmountValidationRule() })
			.put(73, new ValidationRule[] { new SgstRateValidationRule() })
			.put(74, new ValidationRule[] { new SgstAmountValidationRule() })
			.put(75, new ValidationRule[] {
					new AdvaloremCessRateValidationRule() })
			.put(76, new ValidationRule[] {
					new AdvaloremCessAmountValidationRule() })
			.put(77, new ValidationRule[] {
					new SpecificCessRateValidationRule() })
			.put(78, new ValidationRule[] {
					new SpecificCessAmountValidationRule() })
			.put(79, new ValidationRule[] {
					new StateCessAdvaloremRateValidationRule() }) 
			.put(80, new ValidationRule[] {
					new StateCessAdvaloremAmountValidationRule() }) 
			.put(81, new ValidationRule[] { new StateCessRateValidationRule() })
			.put(82, new ValidationRule[] {
					new StateCessAmountValidationRule() })
			.put(83, new ValidationRule[] { new ItemOtherChargesValidation() })
			.put(84, new ValidationRule[] { new TotalItemAmtValidation() })
			.put(85, new ValidationRule[] {
					new InvoiceOtherChargesValidation() })
			.put(86, new ValidationRule[] {
					new InvoiceAssessableAmountValidation() })
			.put(87, new ValidationRule[] { new InvoiceIgstAmountValidation() })
			.put(88, new ValidationRule[] { new InvoiceCgstAmountValidation() })
			.put(89, new ValidationRule[] { new InvoiceSgstAmountValidation() })
			.put(90, new ValidationRule[] {
					new InvoiceCessAdvaloremAmountValidation() })
			.put(91, new ValidationRule[] {
					new InvoiceCessSpecificAmountValidation() })
			.put(92, new ValidationRule[] {
					new InvoiceStateCessAmountValidation() })
			.put(93, new ValidationRule[] {
					new InvoiceStateCessAdvaloremAmountValidation() }) 
			.put(94, new ValidationRule[] { new InvoiceValueValidationRule() })
			.put(95, new ValidationRule[] { new RoundOffValidation() }) 
																		
			.put(96, new ValidationRule[] { new TotalInvValueValidation() }) 
																				
			.put(97, new ValidationRule[] { new TcsFlagIncomeTaxValidation() })
																				
			.put(98, new ValidationRule[] { new TcsRateIncomeTaxValidation() })
																				
			.put(99, new ValidationRule[] {
					new TcsAmountIncomeTaxValidation() })
			.put(100,
					new ValidationRule[] { new CustomerPanOrAdharValidation() })
																				
			.put(101, new ValidationRule[] { new ForeignCurrencyValidation() })//CurrencyCode
			.put(102, new ValidationRule[] { new CountryCodeValidation() })
			.put(103, new ValidationRule[] { new InvoiceFCValidation() })
			.put(104, new ValidationRule[] { new PortCodevalidationrule() })
			.put(105,
					new ValidationRule[] {
							new ShippingBillNumValidationRule() })
			.put(106,
					new ValidationRule[] {
							new ShippingBillDatevalidationRule() })
			.put(107,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(108,
					new ValidationRule[] {
							new InvoicePeriodStartDateValidation() })
			.put(109,
					new ValidationRule[] {
							new InvoicePeriodEndDateValidation() })
			.put(110,
					new ValidationRule[] {
							new PreceedingInvoiceNumberValidation() })
			.put(111,
					new ValidationRule[] {
							new PreceedingInvoiceDateValidation() })
			.put(112,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(113, new ValidationRule[] { DummyValidationRule
					.getInstance() })
			.put(114,
					new ValidationRule[] { new ReceiptAdviceDateValidation() })
			.put(115,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(116,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(117,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(118,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(119,
					new ValidationRule[] {
							DummyValidationRule.getInstance()})
			.put(120,
					new ValidationRule[] {
							new CustomerPoReferenceDateValidation() })
			.put(121,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(122, new ValidationRule[] { new ModeOfPaymentValidation() })
			.put(123, new ValidationRule[] { new BranchOrIfscCodeValidation() })
			.put(124,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(125,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(126,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(127,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(128, new ValidationRule[] { new CreditDaysValidation() })
			.put(129, new ValidationRule[] { new PaidAmountValidation() })
			.put(130, new ValidationRule[] { new BalanceAmountValidation() })
			.put(131, new ValidationRule[] { new PaymentDueDateValidation() })
			.put(132, new ValidationRule[] { new AccountDetailValidation() })
			.put(133, new ValidationRule[] { new eComGSTINValidationRule() })
			.put(134,
					new ValidationRule[] { new EcomTransactionIdValidation() })
			.put(135,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(136,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(137,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(138, new ValidationRule[] { new TransctionTypeValidation() }) 
																				
			.put(139, new ValidationRule[] { new SubSupplyTypeValidation() })
			.put(140,
					new ValidationRule[] {
							new OtherSupplyTypeDescValidation() }) 
			.put(141, new ValidationRule[] { new TransporterIDValidation() })
			.put(142,
					new ValidationRule[] { new TransporterNameValidation() })
			.put(143, new ValidationRule[] { new TransportModeValidation() })
			.put(144, new ValidationRule[] { new TransportDocNoValidation() })
			.put(145, new ValidationRule[] { new TransportDocDateValidation() })
			.put(146, new ValidationRule[] { new DistanceValidation() })
			.put(147, new ValidationRule[] { new VehicleNoValidation() })
			.put(148, new ValidationRule[] { new VehicleTypeValidation() })
			.put(149, new ValidationRule[] { new ReturnPeriodValidationRule() })
			.put(150,
					new ValidationRule[] {
							new OriginalDocTypeValidationRule() })
			.put(151,
					new ValidationRule[] {
							new OriginalCustGstinValidationRule() }) 
			.put(152,
					new ValidationRule[] {
							new DifferentialFlagValidationRule() })
			.put(153,
					new ValidationRule[] {
							new Section7ofIGSTFlagValidationRule() })
			.put(154,
					new ValidationRule[] {
							new ClaimRefundFlagValidationRule() })
			.put(155,
					new ValidationRule[] {
							new AutoPopulateToRefundValidationRule() })
			.put(156, new ValidationRule[] { new CrDrPreGstValidationRule() })
			.put(157,
					new ValidationRule[] {
							new CustomerTypeValidationRule() })
			.put(158, new ValidationRule[] { new CustomerCodeValidationRule() })
																				
			.put(159, new ValidationRule[] { new ProductCodesValidationRule() })
			.put(160,
					new ValidationRule[] {
							new CategoryOfProductValidationRule() })
			.put(161, new ValidationRule[] { new ITCFlagValidationRule() })
			.put(162,
					new ValidationRule[] {
							new StateApplyingCessValidationRule() })
			.put(163, new ValidationRule[] { new FobValidationRule() })
			.put(164, new ValidationRule[] { new ExportDutyValidationRule() })
			.put(165, new ValidationRule[] { new ExchangeRateValidation() })
			.put(166, new ValidationRule[] { new ReasonForCrDrNote() })
			.put(167, new ValidationRule[] { new TCSFlagValidationRule() })
			.put(168, new ValidationRule[] { new TCSAmountValidationRule() })
			.put(169, new ValidationRule[] { new TcsCgstAmountValidation() })
			.put(170, new ValidationRule[] { new TcsSgstAmountValidation() })
			.put(171, new ValidationRule[] { new TDSFlagValidationRule() })
			.put(172, new ValidationRule[] { new TdsIgstAmountValidation() })
			.put(173, new ValidationRule[] { new TdsCgstAmountValidation() })
			.put(174, new ValidationRule[] { new TdsSgstAmountValidation() })
			.put(175,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(176,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(177,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(178,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(179, new ValidationRule[] { new NewPlant() })
			.put(180, new ValidationRule[] { new NewDivision() })
			.put(181,
					new ValidationRule[] { new SubDivision() })
			.put(182, new ValidationRule[] { new NewLocation() })
			.put(183, new ValidationRule[] { new NewSalesOrganisation() })
			.put(184, new ValidationRule[] { new NewDistributionChannel() })
			.put(185, new ValidationRule[] { new NewProfitCentre() })
			.put(186, new ValidationRule[] { new NewProfitCentre2() })
			.put(187, new ValidationRule[] { new NewProfitCentre3() })
			.put(188, new ValidationRule[] { new NewProfitCentre4() })
			.put(189, new ValidationRule[] { new NewProfitCentre5() })
			.put(190, new ValidationRule[] { new NewProfitCentre6() })
			.put(191, new ValidationRule[] { new NewProfitCentre7() })
			.put(192, new ValidationRule[] { new NewProfitCentre8() })
			.put(193,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(194,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(195,
					new ValidationRule[] { DummyValidationRule.getInstance() })
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
			.put(201, new ValidationRule[] { new GLPostingDateValidation() })
			.put(202,
					new ValidationRule[] { new SalesOrderNoValidatiopnRule() })
			.put(203,
					new ValidationRule[] { new EWayBillNumberValidationRule() })
			.put(204, new ValidationRule[] { new EWayBillDateValidationRule() })
			.put(205,
					new ValidationRule[] { new AccountingVoucNoValidationRule() })
			.put(206,
					new ValidationRule[] {
							new AccountingVoucDateValidationRule() })
			.put(207, new ValidationRule[] { new DocRefNoValidationRule() })
			.put(208, new ValidationRule[] { new CustomerTanValidation() })
			.put(209,
					new ValidationRule[] { DummyValidationRule.getInstance() })
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
					new ValidationRule[] { new UserDefined28() })
			.put(237,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.put(238,
					new ValidationRule[] { DummyValidationRule.getInstance() })
			.build();

	
	public Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> rawDocMap) {
		Map<String, List<ProcessingResult>> map 
		          = new HashMap<>();
		rawDocMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();

			// First do normal structural valdiations (cell by cell)
			List<ProcessingResult> results = new ArrayList<>();
			for (Object[] obj : value) {
				for (int i = 0; i < 239; i++) {
					// First get the validators to be applied
					ValidationRule[] rules = STRUCT_VAL_RULES.get(i);
					Object cellVal = obj[i];
					Arrays.stream(rules).forEach(rule -> {
						List<ProcessingResult> errors = rule.isValid(
								value.indexOf(obj), cellVal, obj, null);
						results.addAll(errors);
					});
				}
			}

			// check for incompatible values across rows in the same colummn
			List<ProcessingResult> sameColummnErrors = strut.validate(value);

			results.addAll(sameColummnErrors);
			List<ProcessingResult> errorsResult = EinvoiceHeaderStructuralValidationUtil
					.eliminateDuplicates(results);

			if (errorsResult != null && errorsResult.size() > 0) {
				map.put(key, errorsResult);
			}
		});
		return map;

	}
}