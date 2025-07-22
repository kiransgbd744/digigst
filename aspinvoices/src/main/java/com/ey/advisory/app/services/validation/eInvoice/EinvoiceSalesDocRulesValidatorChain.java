package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 * Siva krishna
 */
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.SourceInfoRepository;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.sales.ArithmeticCheckTaxAmount;
import com.ey.advisory.app.services.validation.sales.BigDecimalNegativeAmountValidator;
import com.ey.advisory.app.services.validation.sales.CMEcomGstinValidator;
import com.ey.advisory.app.services.validation.sales.CMHsnOrSacsupplyTypeValidator;
import com.ey.advisory.app.services.validation.sales.CMRecipientGSTINValidation;
import com.ey.advisory.app.services.validation.sales.CMgstr1OriginalDocdateValidator;
import com.ey.advisory.app.services.validation.sales.CgstAndSgstAmountSameValidation;
import com.ey.advisory.app.services.validation.sales.CgstAndSgstSameValidation;
import com.ey.advisory.app.services.validation.sales.CgstinValidatWithMaster;
import com.ey.advisory.app.services.validation.sales.CmGstr1PosValidation;
import com.ey.advisory.app.services.validation.sales.DistributionChannel;
import com.ey.advisory.app.services.validation.sales.Division;
import com.ey.advisory.app.services.validation.sales.DocDateEffectiveDateRegValidator;
import com.ey.advisory.app.services.validation.sales.DocDateTaxPeriodValidator;
import com.ey.advisory.app.services.validation.sales.DxpHsnValidation;
import com.ey.advisory.app.services.validation.sales.EInvoiceCmCgstinDxpValidation;
import com.ey.advisory.app.services.validation.sales.ExportWithoutTaxAmountValidator;
import com.ey.advisory.app.services.validation.sales.ExportWithoutTaxValidator;
import com.ey.advisory.app.services.validation.sales.GstinOutwardPermissionValidator;
import com.ey.advisory.app.services.validation.sales.HsnAndRateProductMasterValidator;
import com.ey.advisory.app.services.validation.sales.HsnProductMasterValidation;
import com.ey.advisory.app.services.validation.sales.LUHsnOrSacValidator;
import com.ey.advisory.app.services.validation.sales.LURecipientGSTINValidation;
import com.ey.advisory.app.services.validation.sales.LUSgstinValidator;
import com.ey.advisory.app.services.validation.sales.LUStateApplyingCess;
import com.ey.advisory.app.services.validation.sales.LineNoValidation;
import com.ey.advisory.app.services.validation.sales.Location;
import com.ey.advisory.app.services.validation.sales.LuBillToStateValidator;
import com.ey.advisory.app.services.validation.sales.LuCgstRateValidator;
import com.ey.advisory.app.services.validation.sales.LuEcomGstinValidator;
import com.ey.advisory.app.services.validation.sales.LuIgstRateValidator;
import com.ey.advisory.app.services.validation.sales.LuPortCodeValidator;
import com.ey.advisory.app.services.validation.sales.LuPosvalidator;
import com.ey.advisory.app.services.validation.sales.LuSgstRateValidator;
import com.ey.advisory.app.services.validation.sales.OnBoardingSezValidation;
import com.ey.advisory.app.services.validation.sales.Plant;
import com.ey.advisory.app.services.validation.sales.ProfitCenter;
import com.ey.advisory.app.services.validation.sales.RateMasterValidation;
import com.ey.advisory.app.services.validation.sales.ReturnPeriodRegValidator;
import com.ey.advisory.app.services.validation.sales.SalesOrganisation;
import com.ey.advisory.app.services.validation.sales.Section7OfIgstFlag;
import com.ey.advisory.app.services.validation.sales.SgstCgstIgstAmountValidator;
import com.ey.advisory.app.services.validation.sales.SgstCgstIgstValidator;
import com.ey.advisory.app.services.validation.sales.SgstinCgstinAmountValidator;
import com.ey.advisory.app.services.validation.sales.ShippingBillStructure;
import com.ey.advisory.app.services.validation.sales.SupplyTypeTaxableValueValidation;
import com.ey.advisory.app.services.validation.sales.UqcValidator;
import com.ey.advisory.app.services.validation.sales.UserAccess1;
import com.ey.advisory.app.services.validation.sales.UserAccess2;
import com.ey.advisory.app.services.validation.sales.UserAccess3;
import com.ey.advisory.app.services.validation.sales.UserAccess4;
import com.ey.advisory.app.services.validation.sales.UserAccess5;
import com.ey.advisory.app.services.validation.sales.UserAccess6;
import com.ey.advisory.app.services.validation.sales.cgstinAndSgstinSameOrNot;
import com.ey.advisory.app.services.validation.sales.isGstr1ReturnFiled;
import com.ey.advisory.app.util.AspDocumentConstants.FormReturnTypes;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.EInvoiceStatus;
import com.ey.advisory.common.EinvEwbJobStatus;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.EwbStatusNew;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.IrnStatusMaster;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Component("EinvoiceSalesDocRulesValidatorChain")
@Slf4j
public class EinvoiceSalesDocRulesValidatorChain
		implements DocRulesValidatorChain<OutwardTransDocument> {
	@Autowired
	@Qualifier("EwbStatusValidation")
	private EwbStatusValidation ewbStatusValidation;

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.

	@Autowired
	@Qualifier("SourceInfoRepository")
	private SourceInfoRepository sourceInfoRepository;

	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	
	private static final List<String> TAX_SCHEMA_LIST_EWB = ImmutableList
			.of(GSTConstants.NEWB, GSTConstants.NBOTH,GSTConstants.NRETEWB);
	private static final List<String> TAX_SCHEMA_LIST_INVOICE = ImmutableList
			.of(GSTConstants.NBOTH, GSTConstants.NEINV,GSTConstants.NRETEINV);
	private static final List<String> DOCTYPE_IMPORTS = ImmutableList
			.of(GSTConstants.INV, GSTConstants.CR, GSTConstants.DR);
	private static final List<String> SUPTYPE_IMPORTS = ImmutableList.of(
			GSTConstants.SEZWP, GSTConstants.SEZWOP, GSTConstants.EXPT,
			GSTConstants.EXPWT, GSTConstants.DXP);
	private static final List<String> REGYPE_IMPORTS = ImmutableList.of(
			GSTConstants.SEZD, /* GSTConstants.SEZU, */ GSTConstants.REGULAR);
	private static final List<String> B2B= ImmutableList
			.of(GSTConstants.TAX,  GSTConstants.DTA);
	private static final List<String> SUPTYPES= ImmutableList
			.of(GSTConstants.NIL,  GSTConstants.NON,GSTConstants.EXT);
	private static final List<String> CGSTIN_FORMAT = ImmutableList
			.of(GSTConstants.UN, GSTConstants.ON);
	private static final DocRulesValidator[] CGSTIN_VALIDATION = {
			new InWardCgstinValidation(), new CgstInMandatoryValidation(),

	};

	private static final DocRulesValidator[] SUPPLY_AND_DOCTYPES = {
			new SuppltTypeMasterValidation(), new DocTypeMasterValidation(),
			new LUSgstinValidator(),new InWardCgstinValidation(), 
			new CgstInMandatoryValidation(),
			new HsnMandatory(),
			new CMEcomGstinValidator(),
			new TCSFlagGST(),
			new GstinOutwardPermissionValidator(),
			};

	private static final DocRulesValidator[] EWB_APPLICABLE = {
			new TransactionTypeMandatory(), new SubSupplyTypeMandatory(),
			new SuSupplyTypeValidation(), new LuTransactionTypeValidation(),
			// new DocTypeMasterValidation(),
	};

	private static final DocRulesValidator[] EWB = {
			new LuDocCategoryValidation(), new EInvoiceSgstinValidation(),
			new LuEwbCgstinValidation(), new CmDocCatogeryValdaton(),
			new LUHsnOrSacValidator(), new EinvoiceUqcValidator(),
			// new EInvoiceQuantityValidation(),
			new LuIgstRateValidator(), new LuCgstRateValidator(),
			new LuSgstRateValidator(), new CmOtherSupplyDesc(),
			new LuTransportValidation(), new LuVehicleTypeValidation(),
			new TransDocNoAndTransDocDateValidator(), new VehicleNovalidation(),
			// manDatory
			new DocCatoryManDatory(), new SupplierStateCodeMandatory(),
			new CustomerStateCodeMandatory(), new HsnMandatory(),
			//new ItemAssessableAmountMandatory(), 
			new InvoiceValueMandatory(),
			new DistanceMandatory(),
			new GstinOutwardPermissionValidator(),

	};

	private static final DocRulesValidator[] EINVOICE_VALIDATORS = {
			new TaxSchemaValidation(), new SuppltTypeMasterValidation(),
			new DocTypeMasterValidation(), new ReverseChargeFlag(),
			new EInvoiceSgstinValidation(), new SupplierStateCode(),
			new LuBillToStateValidator(), new LuPosvalidator(),
			new IsServiceValidation(), new LUHsnOrSacValidator(),
			new EinvoiceUqcValidator(), new LuCurrencyCodeValidation(),
			new LuCountryCodeValidation(), new LuIgstRateValidator(),
			new LuCgstRateValidator(), new LuSgstRateValidator(),
			new LuModeOfPaymentValidation(), new LuTransportValidation(),
			new LuVehicleTypeValidation(),
			// new EInvoiceQuantityValidation(),
			// Mandatory
			new SupplierLegalnameValidation(), new SupplierAddress1Validation(),
			new SupplierLocationValidation(), new SupplierPincodeMandatory(),
			new SupplierStateCodeMandatory(), new CustomerLegalnameValidation(),
			new CustomerAddress1Validation(), new CustomerLocValidation(),
			//new InvoiceAssessableAmountMandatory(),
			new BillingPOSMandatory(),
			new HsnMandatory(), 
			//new ItemAssessableAmountMandatory(),
			new InvoiceValueMandatory(),
			// new ItemAmountMandatoryValidation(),
			new TotalItemAmountValidation(),
			// new UnitPriceMandatory(),
			new IsServiceMandatory(),
			new GstinOutwardPermissionValidator(),

	};
	private static final DocRulesValidator[] ANX1 = { new ProfitCenter(),
			new Plant(), new Division(), new Location(),
			new SalesOrganisation(), new DistributionChannel(),
			new UserAccess1(), new UserAccess2(), new UserAccess3(),
			new UserAccess4(), new UserAccess5(), new UserAccess6(),
			new SuppltTypeMasterValidation(),
			new LuDocCategoryMasterValidatipon(), new DocTypeMasterValidation(),
			new DocDateTaxPeriodValidator(),
			new DocDateEffectiveDateRegValidator(), new ReverseChargeFlag(),
			new LUSgstinValidator(), new LURecipientGSTINValidation(),
			new CgstinValidatWithMaster(), new EInvoiceCmCgstinDxpValidation(),
			new CMRecipientGSTINValidation(), new LuPosvalidator(),
			new CMEInvoicePosvalidator(), new LUHsnOrSacValidator(),
			new CMHsnOrSacsupplyTypeValidator(),
			new BigDecimalNegativeAmountValidator(),
			new SupplyTypeTaxableValueValidation(),

			new LuIgstRateValidator(), new LuCgstRateValidator(),
			new LuSgstRateValidator(), new LuPortCodeValidator(),
			new ShippingBillNoAndDateAndPortCode(), new CmPreceedingDocNum(),
			new CmPreedingRcrDocNum(), new CmPreceedingDocDate(),
			new CmPreceedingExpDocDate(), new CmPreceedingEInvoiceDocDate(),
			new LuEcomGstinValidator(), new CMEcomGstinValidator(),
			new LuTransactionTypeValidation(), new ReturnPeriodRegValidator(),
			new LuOriginalDocType(), new DifferentialPercentageFlag(),
			new Section7OfIGSTFlag(), new ClaimRefundFlag(),
			new AutoPopulateToRefund(), new CustomerType(), new ITCFlag(),
			new LUStateApplyingCess(), new TCSFlagGST(),
			new MulSupplyTypeCombValidations(),
			new SgstCgstIgstAmountValidator(),
			new SgstinCgstinAmountValidator(), new ExportWithoutTaxValidator(),
			new SgstCgstIgstValidator(), new OnBoardingSezValidation(),
			new Section7OfIgstFlag(), new CgstAndSgstAmountSameValidation(),
			new ArithmeticCheckTaxAmount(), new cgstinAndSgstinSameOrNot(),
			new LineNoValidation(), new DxpHsnValidation(),
			// new CrdrOriginaldocumentdetailsValidator(),
			new HsnAndRateProductMasterValidator(),
			new HsnProductMasterValidation(), new RateMasterValidation(),
			new SupplyTypeTaxableValueValidation(),
			new DocNoStructureValidation(), };

	private static final DocRulesValidator[] GSTR1 = { new ProfitCenter(),
			new Plant(), new Division(), new Location(),
			new SalesOrganisation(), new DistributionChannel(),
			new UserAccess1(), new UserAccess2(), new UserAccess3(),
			new UserAccess4(), new UserAccess5(), new UserAccess6(),
			new SuppltTypeMasterValidation(),
			new LuDocCategoryMasterValidatipon(), new DocTypeMasterValidation(),
			new DocDateTaxPeriodValidator(),
			new DocDateEffectiveDateRegValidator(), new ReverseChargeFlag(),
			new LUSgstinValidator(), new LURecipientGSTINValidation(),
			new CgstinValidatWithMaster(), new EInvoiceCmCgstinDxpValidation(),
			new CMRecipientGSTINValidation(), new LuPosvalidator(),
			new CmGstr1PosValidation(),

			new HsnMandatory(), new LUHsnOrSacValidator(), new UqcValidator(),
			new BigDecimalNegativeAmountValidator(),
			new SupplyTypeTaxableValueValidation(),
			new LuIgstRateValidator(), new LuCgstRateValidator(),
			new LuSgstRateValidator(), new LuPortCodeValidator(),
			new ShippingBillNoAndDateAndPortCode(),
			new CMgstr1OriginalDocdateValidator(), new CmPreceedingDocDate(),
			new LuTransactionTypeValidation(), new DifferentialPercentageFlag(),
			new CRDRPreGST(), new CustomerType(), new ITCFlag(),
			new LUStateApplyingCess(), new TCSFlagGST(),
			new MulSupplyTypeCombValidations(),
			new SgstCgstIgstAmountValidator(),
			new SgstinCgstinAmountValidator(), new ExportWithoutTaxValidator(),
			new ExportWithoutTaxAmountValidator(),
			new SgstCgstIgstValidator(), new OnBoardingSezValidation(),
			new CgstAndSgstSameValidation(),
			new CgstAndSgstAmountSameValidation(),
			new ArithmeticCheckTaxAmount(), new cgstinAndSgstinSameOrNot(),
			new LineNoValidation(), new DxpHsnValidation(),

			new HsnAndRateProductMasterValidator(),
			new HsnProductMasterValidation(), new RateMasterValidation(),
			new SupplyTypeTaxableValueValidation(),
			new PreceedingDocDateValidator(), new PrecedingDocNumSame(),
			new PrecedingDocDateSame(),
			new ShippingBillStructure(), new DocNoStructureValidation(),
			new InWardCgstinValidation(), new CgstInMandatoryValidation(),
			//new PreedingDocDateValidation(),
			//new PostSeptFYValidation(),
			new isGstr1ReturnFiled(),
			new HsnLengthValidation(),
			 new CMEcomGstinValidator(),
			 new GstinOutwardPermissionValidator(),
	};

	static class DocRulesValidatorPrefWrapper
			implements DocRulesValidator<OutwardTransDocument> {

		private DocRulesValidator<OutwardTransDocument> validator;
		private long execTime;

		DocRulesValidatorPrefWrapper(
				DocRulesValidator<OutwardTransDocument> validator) {
			this.validator = validator;
		}

		@Override
		public List<ProcessingResult> validate(OutwardTransDocument document,
				ProcessingContext context) {
			long startTime = System.currentTimeMillis();
			List<ProcessingResult> result = this.validator.validate(document,
					context);
			long endTime = System.currentTimeMillis();
			this.execTime = endTime - startTime;
			return result;
		}

		public long getExecTime() {
			return this.execTime;
		}
	}

	private DocRulesValidator<OutwardTransDocument> getWrappedValidator(
			DocRulesValidator<OutwardTransDocument> validator) {
		return new DocRulesValidatorPrefWrapper(validator);
	}

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		// List<ProcessingResult> cgstresults = new ArrayList<>();

		List<ProcessingResult> results = new ArrayList<>();

		if (document.getComplianceApplicable()) {
			DocRulesValidator[] VALIDATORS = FormReturnTypes.GSTR1.getType()
					.equalsIgnoreCase(document.getFormReturnType()) ? GSTR1
							: ANX1;

			for (DocRulesValidator<OutwardTransDocument> validator : VALIDATORS) {
				if (validator == null) {
					LOGGER.warn("Validator cannot be null. "
							+ "Ignoring the null validator!!");
					continue;
				}
				// Get the class name of the validator, for logging in case of
				// any errors.
				String validatorCls = validator.getClass().getSimpleName();
				try {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format("Executing validator '%s'",
								validatorCls);
						LOGGER.debug(msg);
					}

					DocRulesValidatorPrefWrapper wrappedValidator = (DocRulesValidatorPrefWrapper) getWrappedValidator(
							validator);

					List<ProcessingResult> tmpResults = wrappedValidator
							.validate(document, context);

					PerfStatistics.updateValidatorStat(validatorCls,
							wrappedValidator.execTime);

					results.addAll(tmpResults);

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Executed validator '%s'. No: of Results "
										+ "available: '%d'",
								validatorCls,
								(tmpResults != null) ? tmpResults.size() : 0);
						LOGGER.debug(msg);
					}

				} catch (Exception ex) {
					// create a processing result to inform that the validator
					// failed.
					String exName = ex.getClass().getSimpleName();
					String msg = String
							.format("Error while executing the validator '%s'. "
									+ "Exception: '%s'", validatorCls, exName);
					ProcessingResult result = new ProcessingResult("LOCAL",
							ProcessingResultType.ERROR, "ER9999", msg, null);
					LOGGER.error(msg, ex);
					results.add(result);
				}
			}

		} else {

			for (DocRulesValidator<OutwardTransDocument> validator : SUPPLY_AND_DOCTYPES) {
				if (validator == null) {
					LOGGER.warn("Validator cannot be null. "
							+ "Ignoring the null validator!!");
					continue;
				}
				// Get the class name of the validator, for logging in case of
				// any errors.
				String validatorCls = validator.getClass().getSimpleName();
				try {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format("Executing validator '%s'",
								validatorCls);
						LOGGER.debug(msg);
					}
					DocRulesValidatorPrefWrapper wrappedValidator = (DocRulesValidatorPrefWrapper) getWrappedValidator(
							validator);
					List<ProcessingResult> tmpResults = wrappedValidator
							.validate(document, context);

					PerfStatistics.updateValidatorStat(validatorCls,
							wrappedValidator.execTime);

					results.addAll(tmpResults);

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Executed validator '%s'. No: of Results "
										+ "available: '%d'",
								validatorCls,
								(tmpResults != null) ? tmpResults.size() : 0);
						LOGGER.debug(msg);
					}

				} catch (Exception ex) {
					// create a processing result to inform that the validator
					// failed.
					String exName = ex.getClass().getSimpleName();
					String msg = String
							.format("Error while executing the validator '%s'. "
									+ "Exception: '%s'", validatorCls, exName);
					ProcessingResult result = new ProcessingResult("LOCAL",
							ProcessingResultType.ERROR, "ER9999", msg, null);
					LOGGER.error(msg);
					results.add(result);
				}
			}

		}

		if (!results.isEmpty()) {
			Boolean complianceError = results.stream()
					.anyMatch(r -> r.getType() == ProcessingResultType.ERROR);

			Boolean complianceInfo = results.stream()
					.anyMatch(r -> r.getType() == ProcessingResultType.INFO);
			if (complianceInfo) {
				document.setIsInfo(true);
			}
			if (complianceError) {
				document.setIsProcessed(false);
				document.setIsError(true);
			} else {
				// If there are no errors, then set the isProcessed to
				// true
				// and isError to false
				document.setIsProcessed(true);
				document.setIsError(false);
			}
		} else {
			document.setIsProcessed(true);
			document.setIsError(false);
		}

		List<ProcessingResult> results2 = new ArrayList<>();

		
		boolean einvTaxSchema=TAX_SCHEMA_LIST_INVOICE.contains(
				trimAndConvToUpperCase(document.getTaxScheme()));
		if(einvTaxSchema){
			document.setEinvApplicable(false);
		}else{
		document.setEinvApplicable(eInvoiceApplicability(document));
		}	
		if (GSTConstants.A.equalsIgnoreCase(document.getOptedForEinv())) {

			if (document.getEinvJob() == null) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"We are not getting any data from DB."
									+ "So Setting Einvoice status as Not "
									+ "applicable for docKey{}",
							document.getDocKey());
				}
				document.seteInvStatus(
						EInvoiceStatus.NOT_APPLICABLE.geteInvoiceStatusCode());
				document.setIrnStatus(
						IrnStatusMaster.NOT_APPLICABLE.getIrnStatusMaster());
			}
			if (document.getEinvJob() != null) {
				if (document.getEinvJob() == EinvEwbJobStatus.EINV_ERP_AUTO
						.getEinvEwbJobStatusCode()
						|| document
								.getEinvJob() == EinvEwbJobStatus.EINV_ERP_MANUAL
										.getEinvEwbJobStatusCode()) {
					if (document.getIrn() == null
							|| document.getIrn().isEmpty()) {
						document.seteInvStatus(EInvoiceStatus.ASP_PROCESSED
								.geteInvoiceStatusCode());
						document.setIrnStatus(
								IrnStatusMaster.IRN_NOT_GENERATED_IN_ERP
										.getIrnStatusMaster());
					} else {
						document.seteInvStatus(EInvoiceStatus.ASP_PROCESSED
								.geteInvoiceStatusCode());
						document.setIrnStatus(
								IrnStatusMaster.IRN_GENERATED_IN_ERP
										.getIrnStatusMaster());
					}
				} else {

					if (einvTaxSchema) {
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"tax Sxhema value is {} So setting "
											+ "Einvoice status as Not applicable for docKey{}",
									document.getTaxScheme(),
									document.getDocKey());
						}

						document.seteInvStatus(EInvoiceStatus.NOT_APPLICABLE
								.geteInvoiceStatusCode());
						document.setIrnStatus(IrnStatusMaster.NOT_APPLICABLE
								.getIrnStatusMaster());

					} else {

						if (document.isEinvApplicable()) {
							DocRulesValidator[] VALIDATORS2 = EINVOICE_VALIDATORS;
							for (DocRulesValidator<OutwardTransDocument> validator : VALIDATORS2) {
								if (validator == null) {
									LOGGER.warn("Validator cannot be null. "
											+ "Ignoring the null validator!!");
									continue;
								}
								// Get the class name of the validator, for
								// logging in case of
								// any errors.
								String validatorCls = validator.getClass()
										.getSimpleName();
								try {
									if (LOGGER.isDebugEnabled()) {
										String msg = String.format(
												"Executing validator '%s'",
												validatorCls);
										LOGGER.debug(msg);
									}
									List<ProcessingResult> InvtmpResults = validator
											.validate(document, context);
									results2.addAll(InvtmpResults);

									if (LOGGER.isDebugEnabled()) {
										String invmsg = String.format(
												"Executed validator '%s'. No: of Results "
														+ "available: '%d'",
												validatorCls,
												(InvtmpResults != null)
														? InvtmpResults.size()
														: 0);
										LOGGER.debug(invmsg);
									}

								} catch (Exception ex) {
									// create a processing result to inform that
									// the validator
									// failed.
									String exName = ex.getClass()
											.getSimpleName();
									String invmsg = String.format(
											"Error while executing the validator '%s'. "
													+ "Exception: '%s'",
											validatorCls, exName);
									ProcessingResult invresultErr = new ProcessingResult(
											"LOCAL", ProcessingResultType.ERROR,
											"ER9999", invmsg, null);
									LOGGER.error(invmsg);
									results2.add(invresultErr);
								}
							}
							Boolean inverror = results2.stream().anyMatch(r -> r
									.getType() == ProcessingResultType.ERROR);
							if (inverror) {

								document.seteInvStatus(EInvoiceStatus.ASP_ERROR
										.geteInvoiceStatusCode());
								document.setIrnStatus(IrnStatusMaster.ASP_ERROR
										.getIrnStatusMaster());
							} else {

								document.seteInvStatus(
										EInvoiceStatus.ASP_PROCESSED
												.geteInvoiceStatusCode());
								document.setIrnStatus(IrnStatusMaster.PENDING
										.getIrnStatusMaster());
							}

						} else {

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Applicability rule fail."
												+ "So Einvoice status Not applicable for dockey{}",
										document.getDocKey());
							}
							document.seteInvStatus(EInvoiceStatus.NOT_APPLICABLE
									.geteInvoiceStatusCode());
							document.setIrnStatus(IrnStatusMaster.NOT_APPLICABLE
									.getIrnStatusMaster());
						}
					}
				}
			}
		} else {
			document.seteInvStatus(
					EInvoiceStatus.NOT_OPTED.geteInvoiceStatusCode());
			document.setIrnStatus(
					IrnStatusMaster.NOT_OPTED.getIrnStatusMaster());
		}
		// EWB rules start

		// logic changed

		List<ProcessingResult> results1 = new ArrayList<>();
		boolean hsnCheck=EwbStatusValidation.HsnEwbcheck(document);
		boolean ewbApplCheck=EwbStatusValidation.validate(document);
		boolean ewbTaxSchema=TAX_SCHEMA_LIST_EWB.contains(
				trimAndConvToUpperCase(document.getTaxScheme()));
		if(ewbTaxSchema){
			document.setEwbApplicable(false);
		} else {
			if (hsnCheck && ewbApplCheck) {
				document.setEwbApplicable(true);
			} else {
				document.setEwbApplicable(false);
			}
		}
		if (GSTConstants.A.equalsIgnoreCase(document.getOptedForEwb())) {

			if (document.getEwbJob() == null) {
				document.setEwbProcessingStatus(
						EwbProcessingStatus.NOT_APPLICABLE
								.getEwbProcessingStatusCode());
				document.setEwbStatus(
						EwbStatusNew.NOT_APPLICABLE.getEwbNewStatusCode());
			}
			if (document.getEwbJob() != null) {
				if (document.getEwbJob() == EinvEwbJobStatus.EWB_ERP_AUTO
						.getEinvEwbJobStatusCode()
						|| document
								.getEwbJob() == EinvEwbJobStatus.EWB_ERP_MANUAL
										.getEinvEwbJobStatusCode()) {
					if (document.geteWayBillNo() != null
							&& !document.geteWayBillNo().isEmpty()) {
						document.setEwbProcessingStatus(
								EwbProcessingStatus.ASP_PROCESSED
										.getEwbProcessingStatusCode());

						document.setEwbStatus(
								EwbStatusNew.EWAY_BILL_GENERATED_ERP
										.getEwbNewStatusCode());

					} else {
						document.setEwbProcessingStatus(
								EwbProcessingStatus.ASP_PROCESSED
										.getEwbProcessingStatusCode());

						document.setEwbStatus(
								EwbStatusNew.EWAY_BILL_NOT_GENERATED_ERP
										.getEwbNewStatusCode());
					}
				} else {
					if (ewbTaxSchema) {
						
						document.setEwbProcessingStatus(
								EwbProcessingStatus.NOT_APPLICABLE
										.getEwbProcessingStatusCode());
						document.setEwbStatus(EwbStatusNew.NOT_APPLICABLE
								.getEwbNewStatusCode());
					} else {

						if (hsnCheck) {

							DocRulesValidator[] EWBAPPLICABLE = EWB_APPLICABLE;
							for (DocRulesValidator<OutwardTransDocument> ewbValidator : EWBAPPLICABLE) {
								if (ewbValidator == null) {
									LOGGER.warn("Validator cannot be null. "
											+ "Ignoring the null validator!!");
									continue;
								}
								// Get the class name of the validator, for
								// logging in case
								// of
								// any errors.
								String ewbValidatorCls = ewbValidator.getClass()
										.getSimpleName();
								try {
									if (LOGGER.isDebugEnabled()) {
										String msg = String.format(
												"Executing validator '%s'",
												ewbValidatorCls);
										LOGGER.debug(msg);
									}
									List<ProcessingResult> tmpResults = ewbValidator
											.validate(document, context);
									results1.addAll(tmpResults);
									if (LOGGER.isDebugEnabled()) {
										String msg = String.format(
												"Executed validator '%s'. No: of Results "
														+ "available: '%d'",
												ewbValidatorCls,
												(tmpResults != null)
														? tmpResults.size()
														: 0);
										LOGGER.debug(msg);
									}

								} catch (Exception ex) {
									// create a processing result to inform that
									// the
									// validator
									// failed.
									String exName = ex.getClass()
											.getSimpleName();
									String msg = String.format(
											"Error while executing the validator '%s'. "
													+ "Exception: '%s'",
											ewbValidatorCls, exName);
									ProcessingResult resultErr = new ProcessingResult(
											"LOCAL", ProcessingResultType.ERROR,
											"ER9999", msg, null);
									LOGGER.error(msg);
									results1.add(resultErr);
								}
							}
							if (!results1.isEmpty()) {
								LOGGER.debug("Setting setEwbProcessingStatus ASP_ERROR and List of Errors is {} ", results1);
								document.setEwbProcessingStatus(
										EwbProcessingStatus.ASP_ERROR
												.getEwbProcessingStatusCode());
								document.setEwbStatus(EwbStatusNew.ASP_ERROR
										.getEwbNewStatusCode());
							} else {
								if (ewbApplCheck) {

									DocRulesValidator[] VALIDATORS1 = EWB;
									for (DocRulesValidator<OutwardTransDocument> validator : VALIDATORS1) {
										if (validator == null) {
											LOGGER.warn(
													"Validator cannot be null. "
															+ "Ignoring the null validator!!");
											continue;
										}
										// Get the class name of the validator,
										// for logging in case
										// of
										// any errors.
										String validatorCls = validator
												.getClass().getSimpleName();
										try {
											if (LOGGER.isDebugEnabled()) {
												String msg = String.format(
														"Executing validator '%s'",
														validatorCls);
												LOGGER.debug(msg);
											}
											List<ProcessingResult> tmpResults = validator
													.validate(document,
															context);
											results1.addAll(tmpResults);
											if (LOGGER.isDebugEnabled()) {
												String msg = String.format(
														"Executed validator '%s'. No: of Results "
																+ "available: '%d'",
														validatorCls,
														(tmpResults != null)
																? tmpResults
																		.size()
																: 0);
												LOGGER.debug(msg);
											}

										} catch (Exception ex) {
											// create a processing result to
											// inform that the
											// validator
											// failed.
											String exName = ex.getClass()
													.getSimpleName();
											String msg = String.format(
													"Error while executing the validator '%s'. "
															+ "Exception: '%s'",
													validatorCls, exName);
											ProcessingResult resultErr = new ProcessingResult(
													"LOCAL",
													ProcessingResultType.ERROR,
													"ER9999", msg, null);
											LOGGER.error(msg);
											results1.add(resultErr);
										}
									}
									Boolean error = results1.stream().anyMatch(
											r -> r.getType() == ProcessingResultType.ERROR);
									if (error) {
										document.setEwbProcessingStatus(
												EwbProcessingStatus.ASP_ERROR
														.getEwbProcessingStatusCode());
										document.setEwbStatus(
												EwbStatusNew.ASP_ERROR
														.getEwbNewStatusCode());
									} else {
										document.setEwbProcessingStatus(
												EwbProcessingStatus.ASP_PROCESSED
														.getEwbProcessingStatusCode());
										document.setEwbStatus(
												EwbStatusNew.PENDING
														.getEwbNewStatusCode());
									}

								} else {
									document.setEwbProcessingStatus(
											EwbProcessingStatus.NOT_APPLICABLE
													.getEwbProcessingStatusCode());
									document.setEwbStatus(
											EwbStatusNew.NOT_APPLICABLE
													.getEwbNewStatusCode());
								}
							}

						} else {
							document.setEwbProcessingStatus(
									EwbProcessingStatus.NOT_APPLICABLE
											.getEwbProcessingStatusCode());
							document.setEwbStatus(EwbStatusNew.NOT_APPLICABLE
									.getEwbNewStatusCode());
						}
					}
				}
			}
		} else {
			document.setEwbProcessingStatus(
					EwbProcessingStatus.NOT_OPTED.getEwbProcessingStatusCode());
			document.setEwbStatus(EwbStatusNew.NOT_OPTED.getEwbNewStatusCode());
		}
		results.addAll(results1);
		results.addAll(results2);

		if (!results.isEmpty()) {
			Boolean complianceError = results.stream()
					.anyMatch(r -> r.getType() == ProcessingResultType.ERROR);

			Boolean complianceInfo = results.stream()
					.anyMatch(r -> r.getType() == ProcessingResultType.INFO);
			if (complianceInfo) {
				document.setIsInfo(true);
			}
			if (complianceError) {
				document.setIsProcessed(false);
				document.setIsError(true);
			} else {
				// If there are no errors, then set the isProcessed to
				// true
				// and isError to false
				document.setIsProcessed(true);
				document.setIsError(false);
			}
		} else {
			document.setIsProcessed(true);
			document.setIsError(false);
		}
		return results;

	}

	public boolean eInvoiceApplicability(OutwardTransDocument document) {

		if (document.getDocDate() == null) return false;
			LocalDate docDate = document.getDocDate();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");

			LocalDate gstStartDate = LocalDate.parse("01102020", formatter);
				if (docDate.compareTo(gstStartDate) < 0) {
					return false;
				}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Einvoice applicability false");
		}
		
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return false;
		if (Strings.isNullOrEmpty(document.getTransactionType())) {
			document.setTransactionType(GSTConstants.O);
		}
		if(document.getTransactionType().equalsIgnoreCase(GSTConstants.I)) return false;
		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				document.getSgstin());
		if (gstin == null){
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Einvoice applicability false "
						+ "Because of we are not getting data "
						+ "from DB for docKey {}",document.getDocKey());
			}
			return false;
		}
		String regType = gstin.getRegistrationType().toUpperCase();
		if (!REGYPE_IMPORTS.contains(regType)) return false;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registration Type-{} for docKey {}",
					regType,document.getDocKey());
		}
		if (!DOCTYPE_IMPORTS.contains(
				trimAndConvToUpperCase(document.getDocType()))) return false;
		
		if ((!Strings.isNullOrEmpty(document.getCgstin()))
				&& (document.getCgstin().length() == 15)
				&& CGSTIN_FORMAT
				.contains(document.getCgstin().substring(12, 14))) {

				 return false;
		}
		if(SUPTYPE_IMPORTS
				.contains(trimAndConvToUpperCase(document.getSupplyType()))){
			
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Initiate  the Einvoice applicability "
					+ "true for docKey {}",document.getDocKey());
		}
		return true;
	}
		if((!Strings.isNullOrEmpty(document.getCgstin()))
							&& (document.getCgstin().length() == 15)
							&& (SUPTYPES.contains(trimAndConvToUpperCase
									(document.getSupplyType())))){
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Initiate  the Einvoice applicability "
						+ "true for docKey {}",document.getDocKey());
			}
			return true;
		}
		
			if ((!Strings.isNullOrEmpty(document.getCgstin()))
							&& (document.getCgstin().length() == 15)
							&& (B2B.contains(trimAndConvToUpperCase
									(document.getSupplyType())))){
				

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Initiate  the Einvoice applicability "
								+ "true for docKey {}",document.getDocKey());
					}
					return true;
				
			}
		

		return false;
	}
	
	
}
