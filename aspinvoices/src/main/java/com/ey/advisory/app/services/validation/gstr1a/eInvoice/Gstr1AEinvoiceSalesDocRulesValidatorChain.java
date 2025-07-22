package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

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
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.SourceInfoRepository;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.AspDocumentConstants.FormReturnTypes;
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

@Component("Gstr1AEinvoiceSalesDocRulesValidatorChain")
@Slf4j
public class Gstr1AEinvoiceSalesDocRulesValidatorChain
		implements DocRulesValidatorChain<Gstr1AOutwardTransDocument> {
	@Autowired
	@Qualifier("Gstr1AEwbStatusValidation")
	private Gstr1AEwbStatusValidation ewbStatusValidation;

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
			.of(GSTConstants.NEWB, GSTConstants.NBOTH, GSTConstants.NRETEWB);
	private static final List<String> TAX_SCHEMA_LIST_INVOICE = ImmutableList
			.of(GSTConstants.NBOTH, GSTConstants.NEINV, GSTConstants.NRETEINV);
	private static final List<String> DOCTYPE_IMPORTS = ImmutableList
			.of(GSTConstants.INV, GSTConstants.CR, GSTConstants.DR);
	private static final List<String> SUPTYPE_IMPORTS = ImmutableList
			.of(GSTConstants.EXPT, GSTConstants.EXPWT);
	private static final List<String> REGYPE_IMPORTS = ImmutableList.of(
			GSTConstants.SEZD, /* GSTConstants.SEZU, */ GSTConstants.REGULAR);

	private static final List<String> B2B = ImmutableList.of(GSTConstants.TAX,
			GSTConstants.DTA);
	private static final List<String> SUPTYPES = ImmutableList.of(
			GSTConstants.NIL, GSTConstants.NON, GSTConstants.EXT,
			GSTConstants.TAX, GSTConstants.DTA, GSTConstants.DXP,
			GSTConstants.SEZWP, GSTConstants.SEZWOP);
	private static final List<String> CGSTIN_FORMAT = ImmutableList
			.of(GSTConstants.UN, GSTConstants.ON);
	private static final DocRulesValidator[] CGSTIN_VALIDATION = {
			new Gstr1AInWardCgstinValidation(),
			new Gstr1ACgstInMandatoryValidation(),

	};

	private static final DocRulesValidator[] SUPPLY_AND_DOCTYPES = {
			new Gstr1ASupplyTypeMasterValidation(),
			new Gstr1ADocTypeMasterValidation(), new Gstr1ALUSgstinValidator(),
			new Gstr1AInWardCgstinValidation(),
			new Gstr1ACgstInMandatoryValidation(),
			new Gstr1ABigDecimalNegativeAmountValidator(),
			new Gstr1AHsnMandatory(), new Gstr1ACMEcomGstinValidator(),
			new Gstr1ATCSFlagGST(), new Gstr1AGstinOutwardPermissionValidator(), };

	private static final DocRulesValidator[] EWB_APPLICABLE = {
			new Gstr1ATransactionTypeMandatory(),
			new Gstr1ASubSupplyTypeMandatory(),
			new Gstr1ASuSupplyTypeValidation(),
			new Gstr1ALuTransactionTypeValidation(),
			// new DocTypeMasterValidation(),
	};

	private static final DocRulesValidator[] EWB = {
			new Gstr1ALuDocCategoryValidation(),
			new Gstr1AEInvoiceSgstinValidation(),
			new Gstr1ALuEwbCgstinValidation(),
			new Gstr1ACmDocCatogeryValdaton(), new Gstr1ALUHsnOrSacValidator(),
			new Gstr1AEinvoiceUqcValidator(),
			// new EInvoiceQuantityValidation(),
			new Gstr1ALuIgstRateValidator(), new Gstr1ALuCgstRateValidator(),
			new Gstr1ALuSgstRateValidator(), new Gstr1ACmOtherSupplyDesc(),
			new Gstr1ALuTransportValidation(),
			new Gstr1ALuVehicleTypeValidation(),
			new Gstr1ATransDocNoAndTransDocDateValidator(),
			new Gstr1AVehicleNovalidation(),
			// manDatory
			new Gstr1ADocCatoryManDatory(),
			new Gstr1ASupplierStateCodeMandatory(),
			new Gstr1ACustomerStateCodeMandatory(), new Gstr1AHsnMandatory(),
			// new ItemAssessableAmountMandatory(), new InvoiceValueMandatory(),
			new Gstr1ADistanceMandatory(),
			new Gstr1AGstinOutwardPermissionValidator(),

	};

	private static final DocRulesValidator[] EINVOICE_VALIDATORS = {
			new Gstr1ATaxSchemaValidation(),
			new Gstr1ASupplyTypeMasterValidation(),
			new Gstr1ADocTypeMasterValidation(), new Gstr1AReverseChargeFlag(),
			new Gstr1AEInvoiceSgstinValidation(), new Gstr1ASupplierStateCode(),
			new Gstr1ALuBillToStateValidator(), new Gstr1ALuPosvalidator(),
			new Gstr1AIsServiceValidation(), new Gstr1ALUHsnOrSacValidator(),
			new Gstr1AEinvoiceUqcValidator(),
			new Gstr1ALuCurrencyCodeValidation(),
			new Gstr1ALuCountryCodeValidation(),
			new Gstr1ALuIgstRateValidator(), new Gstr1ALuCgstRateValidator(),
			new Gstr1ALuSgstRateValidator(),
			new Gstr1ALuModeOfPaymentValidation(),
			new Gstr1ALuTransportValidation(),
			new Gstr1ALuVehicleTypeValidation(),
			// new EInvoiceQuantityValidation(),
			// Mandatory
			new Gstr1ASupplierLegalnameValidation(),
			new Gstr1ASupplierAddress1Validation(),
			new Gstr1ASupplierLocationValidation(),
			new Gstr1ASupplierPincodeMandatory(),
			new Gstr1ASupplierStateCodeMandatory(),
			new Gstr1ACustomerLegalnameValidation(),
			new Gstr1ACustomerAddress1Validation(),
			new Gstr1ACustomerLocValidation(),
			new Gstr1AInvoiceAssessableAmountMandatory(),
			new Gstr1ABillingPOSMandatory(), new Gstr1AHsnMandatory(),
			new Gstr1AItemAssessableAmountMandatory(),
			new Gstr1AInvoiceValueMandatory(),
			// new ItemAmountMandatoryValidation(),
			new Gstr1ATotalItemAmountValidation(),
			// new UnitPriceMandatory(),
			new Gstr1AIsServiceMandatory(),
			new Gstr1AGstinOutwardPermissionValidator(),

	};
	private static final DocRulesValidator[] ANX1 = { new Gstr1AProfitCenter(),
			new Gstr1APlant(), new Gstr1ADivision(), new Gstr1ALocation(),
			new Gstr1ASalesOrganisation(), new Gstr1ADistributionChannel(),
			new Gstr1AUserAccess1(), new Gstr1AUserAccess2(),
			new Gstr1AUserAccess3(), new Gstr1AUserAccess4(),
			new Gstr1AUserAccess5(), new Gstr1AUserAccess6(),
			new Gstr1ASupplyTypeMasterValidation(),
			new Gstr1ALuDocCategoryMasterValidation(),
			new Gstr1ADocTypeMasterValidation(),
			new Gstr1ADocDateTaxPeriodValidator(),
			new Gstr1ADocDateEffectiveDateRegValidator(),
			new Gstr1AReverseChargeFlag(), new Gstr1ALUSgstinValidator(),
			new Gstr1ALURecipientGSTINValidation(),
			new Gstr1ACgstinValidatWithMaster(),
			new Gstr1AEInvoiceCmCgstinDxpValidation(),
			new Gstr1ACMRecipientGSTINValidation(), new Gstr1ALuPosvalidator(),
			new Gstr1ACMEInvoicePosvalidator(), new Gstr1ALUHsnOrSacValidator(),
			new Gstr1ACMHsnOrSacsupplyTypeValidator(),
			new Gstr1ABigDecimalNegativeAmountValidator(),
			new Gstr1ASupplyTypeTaxableValueValidation(),

			new Gstr1ALuIgstRateValidator(), new Gstr1ALuCgstRateValidator(),
			new Gstr1ALuSgstRateValidator(), new Gstr1ALuPortCodeValidator(),
			new Gstr1AShippingBillNoAndDateAndPortCode(),
			new Gstr1ACmPreceedingDocNum(), new Gstr1ACmPreedingRcrDocNum(),
			new Gstr1ACmPreceedingDocDate(), new Gstr1ACmPreceedingExpDocDate(),
			new Gstr1ACmPreceedingEInvoiceDocDate(),
			new Gstr1ALuEcomGstinValidator(), new Gstr1ACMEcomGstinValidator(),
			new Gstr1ALuTransactionTypeValidation(),
			new Gstr1AReturnPeriodRegValidator(), new Gstr1ALuOriginalDocType(),
			new Gstr1ADifferentialPercentageFlag(),
			new Gstr1ASection7OfIGSTFlag(), new Gstr1AClaimRefundFlag(),
			new Gstr1AAutoPopulateToRefund(), new Gstr1ACustomerType(),
			new Gstr1AITCFlag(), new Gstr1ALUStateApplyingCess(),
			new Gstr1ATCSFlagGST(), new Gstr1AMulSupplyTypeCombValidations(),
			new Gstr1ASgstCgstIgstAmountValidator(),
			new Gstr1ASgstinCgstinAmountValidator(),
			new Gstr1AExportWithoutTaxValidator(),
			new Gstr1ASgstCgstIgstValidator(),
			new Gstr1AOnBoardingSezValidation(),
			new Gstr1ACgstAndSgstAmountSameValidation(),
			new Gstr1AArithmeticCheckTaxAmount(),
			new Gstr1AcgstinAndSgstinSameOrNot(), new Gstr1ALineNoValidation(),
			new Gstr1ADxpHsnValidation(),
			// new CrdrOriginaldocumentdetailsValidator(),
			new Gstr1AHsnAndRateProductMasterValidator(),
			new Gstr1AHsnProductMasterValidation(),
			new Gstr1ARateMasterValidation(),
			new Gstr1ASupplyTypeTaxableValueValidation(),
			new Gstr1ADocNoStructureValidation(), };

	private static final DocRulesValidator[] GSTR1 = { new Gstr1AProfitCenter(),
			new Gstr1APlant(), new Gstr1ADivision(), new Gstr1ALocation(),
			new Gstr1ASalesOrganisation(), new Gstr1ADistributionChannel(),
			new Gstr1AUserAccess1(), new Gstr1AUserAccess2(),
			new Gstr1AUserAccess3(), new Gstr1AUserAccess4(),
			new Gstr1AUserAccess5(), new Gstr1AUserAccess6(),
			new Gstr1ASupplyTypeMasterValidation(),
			new Gstr1ALuDocCategoryMasterValidation(),
			new Gstr1ADocTypeMasterValidation(),
			new Gstr1ADocDateTaxPeriodValidator(),
			new Gstr1ADocDateEffectiveDateRegValidator(),
			new Gstr1AReverseChargeFlag(), new Gstr1ALUSgstinValidator(),
			new Gstr1ALURecipientGSTINValidation(),
			new Gstr1ACgstinValidatWithMaster(),
			new Gstr1AEInvoiceCmCgstinDxpValidation(),
			new Gstr1ACMRecipientGSTINValidation(), new Gstr1ALuPosvalidator(),
			new Gstr1ACmGstr1PosValidation(),

			new Gstr1AHsnMandatory(), new Gstr1ALUHsnOrSacValidator(),
			new Gstr1AUqcValidator(),
			new Gstr1ABigDecimalNegativeAmountValidator(),
			new Gstr1ASupplyTypeTaxableValueValidation(),

			new Gstr1ALuIgstRateValidator(), new Gstr1ALuCgstRateValidator(),
			new Gstr1ALuSgstRateValidator(), new Gstr1ALuPortCodeValidator(),
			new Gstr1AShippingBillNoAndDateAndPortCode(),
			new Gstr1ACMgstr1OriginalDocdateValidator(),
			new Gstr1ACmPreceedingDocDate(),
			new Gstr1ALuTransactionTypeValidation(),
			new Gstr1ADifferentialPercentageFlag(), new Gstr1ACRDRPreGST(),
			new Gstr1ACustomerType(), new Gstr1AITCFlag(),
			new Gstr1ALUStateApplyingCess(), new Gstr1ATCSFlagGST(),
			new Gstr1AMulSupplyTypeCombValidations(),
			new Gstr1ASgstCgstIgstAmountValidator(),
			new Gstr1ASgstinCgstinAmountValidator(),
			new Gstr1AExportWithoutTaxValidator(),
			new Gstr1AExportWithoutTaxAmountValidator(),
			new Gstr1ASgstCgstIgstValidator(),
			new Gstr1AOnBoardingSezValidation(),
			new Gstr1ACgstAndSgstSameValidation(),
			new Gstr1ACgstAndSgstAmountSameValidation(),
			new Gstr1AArithmeticCheckTaxAmount(),
			new Gstr1AcgstinAndSgstinSameOrNot(), new Gstr1ALineNoValidation(),
			new Gstr1ADxpHsnValidation(),

			new Gstr1AHsnAndRateProductMasterValidator(),
			new Gstr1AHsnProductMasterValidation(),
			new Gstr1ARateMasterValidation(),
			new Gstr1ASupplyTypeTaxableValueValidation(),
			new Gstr1APreceedingDocDateValidator(),
			new Gstr1APrecedingDocNumSame(), new Gstr1APrecedingDocDateSame(),
			new Gstr1AShippingBillStructure(), new Gstr1ADocNoStructureValidation(),
			new Gstr1AInWardCgstinValidation(),
			new Gstr1ACgstInMandatoryValidation(),
			// new PostSeptFYValidation(),
			// new PreedingDocDateValidation(),
			new Gstr1AHsnCodeLengthValidation(),
			// new isGstr1ReturnFiled(),
			new Gstr1AHsnLengthValidation(), new Gstr1ACMEcomGstinValidator(),
			new Gstr1AGstinOutwardPermissionValidator(),

	};

	static class DocRulesValidatorPrefWrapper
			implements DocRulesValidator<Gstr1AOutwardTransDocument> {

		private DocRulesValidator<Gstr1AOutwardTransDocument> validator;
		private long execTime;

		DocRulesValidatorPrefWrapper(
				DocRulesValidator<Gstr1AOutwardTransDocument> validator) {
			this.validator = validator;
		}

		@Override
		public List<ProcessingResult> validate(
				Gstr1AOutwardTransDocument document,
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

	private DocRulesValidator<Gstr1AOutwardTransDocument> getWrappedValidator(
			DocRulesValidator<Gstr1AOutwardTransDocument> validator) {
		return new DocRulesValidatorPrefWrapper(validator);
	}

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		// List<ProcessingResult> cgstresults = new ArrayList<>();

		List<ProcessingResult> results = new ArrayList<>();

		if (document.getComplianceApplicable()) {
			DocRulesValidator[] VALIDATORS = FormReturnTypes.GSTR1.getType()
					.equalsIgnoreCase(document.getFormReturnType()) ? GSTR1
							: ANX1;

			for (DocRulesValidator<Gstr1AOutwardTransDocument> validator : VALIDATORS) {
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

			for (DocRulesValidator<Gstr1AOutwardTransDocument> validator : SUPPLY_AND_DOCTYPES) {
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

		boolean einvTaxSchema = TAX_SCHEMA_LIST_INVOICE
				.contains(trimAndConvToUpperCase(document.getTaxScheme()));
		if (einvTaxSchema) {
			document.setEinvApplicable(false);
		} else {
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
							for (DocRulesValidator<Gstr1AOutwardTransDocument> validator : VALIDATORS2) {
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
		boolean hsnCheck = Gstr1AEwbStatusValidation.HsnEwbcheck(document);
		boolean ewbApplCheck = Gstr1AEwbStatusValidation.validate(document);
		boolean ewbTaxSchema = TAX_SCHEMA_LIST_EWB
				.contains(trimAndConvToUpperCase(document.getTaxScheme()));
		if (ewbTaxSchema) {
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
							for (DocRulesValidator<Gstr1AOutwardTransDocument> ewbValidator : EWBAPPLICABLE) {
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
								document.setEwbProcessingStatus(
										EwbProcessingStatus.ASP_ERROR
												.getEwbProcessingStatusCode());
								document.setEwbStatus(EwbStatusNew.ASP_ERROR
										.getEwbNewStatusCode());
							} else {
								if (ewbApplCheck) {

									DocRulesValidator[] VALIDATORS1 = EWB;
									for (DocRulesValidator<Gstr1AOutwardTransDocument> validator : VALIDATORS1) {
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

		return results;

	}

	public boolean eInvoiceApplicability(Gstr1AOutwardTransDocument document) {

		if (document.getDocDate() == null)
			return false;
		LocalDate docDate = document.getDocDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

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
		if (document.getTransactionType().equalsIgnoreCase(GSTConstants.I))
			return false;
		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				document.getSgstin());
		if (gstin == null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Einvoice applicability false "
								+ "Because of we are not getting data "
								+ "from DB for docKey {}",
						document.getDocKey());
			}
			return false;
		}
		String regType = gstin.getRegistrationType().toUpperCase();
		if (!REGYPE_IMPORTS.contains(regType))
			return false;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registration Type-{} for docKey {}", regType,
					document.getDocKey());
		}
		if (!DOCTYPE_IMPORTS
				.contains(trimAndConvToUpperCase(document.getDocType())))
			return false;

		if ((!Strings.isNullOrEmpty(document.getCgstin()))
				&& (document.getCgstin().length() == 15)
				/*
				 * && (B2B.contains(
				 * trimAndConvToUpperCase(document.getSupplyType())))
				 */
				&& CGSTIN_FORMAT
						.contains(document.getCgstin().substring(12, 14))) {

			return false;

		}
		if (SUPTYPE_IMPORTS
				.contains(trimAndConvToUpperCase(document.getSupplyType()))) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Initiate  the Einvoice applicability "
						+ "true for docKey {}", document.getDocKey());
			}
			return true;
		}

		if ((!Strings.isNullOrEmpty(document.getCgstin()))
				&& (document.getCgstin().length() == 15) && (SUPTYPES.contains(
						trimAndConvToUpperCase(document.getSupplyType())))) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Initiate  the Einvoice applicability "
						+ "true for docKey {}", document.getDocKey());
			}
			return true;
		}

		/*
		 * if ((!Strings.isNullOrEmpty(document.getCgstin())) &&
		 * (document.getCgstin().length() == 15) && (B2B.contains(
		 * trimAndConvToUpperCase(document.getSupplyType())))) {
		 * 
		 * if (LOGGER.isDebugEnabled()) {
		 * LOGGER.debug("Initiate  the Einvoice applicability " +
		 * "true for docKey {}", document.getDocKey()); } return true;
		 * 
		 * }
		 */

		return false;
	}

}
