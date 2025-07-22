package com.ey.advisory.app.services.validation.sales;

/**
 * Siva krishna
 */
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.eInvoice.AutoPopulateToRefund;
import com.ey.advisory.app.services.validation.eInvoice.CMEInvoicePosvalidator;
import com.ey.advisory.app.services.validation.eInvoice.CRDRPreGST;
import com.ey.advisory.app.services.validation.eInvoice.ClaimRefundFlag;
import com.ey.advisory.app.services.validation.eInvoice.DifferentialPercentageFlag;
import com.ey.advisory.app.services.validation.eInvoice.DocNoStructureValidation;
import com.ey.advisory.app.services.validation.eInvoice.DocTypeMasterValidation;
import com.ey.advisory.app.services.validation.eInvoice.HsnLengthValidation;
import com.ey.advisory.app.services.validation.eInvoice.HsnMandatory;
import com.ey.advisory.app.services.validation.eInvoice.ITCFlag;
import com.ey.advisory.app.services.validation.eInvoice.LuDocCategoryMasterValidatipon;
import com.ey.advisory.app.services.validation.eInvoice.LuOriginalDocType;
import com.ey.advisory.app.services.validation.eInvoice.LuTransactionTypeValidation;
import com.ey.advisory.app.services.validation.eInvoice.MulSupplyTypeCombValidations;
import com.ey.advisory.app.services.validation.eInvoice.ReverseChargeFlag;
import com.ey.advisory.app.services.validation.eInvoice.Section7OfIGSTFlag;
import com.ey.advisory.app.services.validation.eInvoice.ShippingBillNoAndDateAndPortCode;
import com.ey.advisory.app.services.validation.eInvoice.SuppltTypeMasterValidation;
import com.ey.advisory.app.services.validation.eInvoice.TCSFlagGST;
import com.ey.advisory.app.util.AspDocumentConstants.FormReturnTypes;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("SalesDocRulesValidatorChain")
public class SalesDocRulesValidatorChain
		implements DocRulesValidatorChain<OutwardTransDocument> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SalesDocRulesValidatorChain.class);

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	
	
	
	private static final DocRulesValidator[] SUPPLY_AND_DOCTYPES = {
			new SuppltTypeMasterValidation(),
			new DocTypeMasterValidation(),	
			new CMEcomGstinValidator(),
			new GstinOutwardPermissionValidator(),
	};
	
	
	private static final DocRulesValidator[] ANX1_VALIDATORS = {
			new ProfitCenter(), new Plant(),
			new Division(), new Location(), new SalesOrganisation(),
			new DistributionChannel(), new UserAccess1(), new UserAccess2(),
			new UserAccess3(), new UserAccess4(), new UserAccess5(),
			new UserAccess6(),
			new SuppltTypeMasterValidation(),
			new LuDocCategoryMasterValidatipon(), new DocTypeMasterValidation(),
			new DocDateTaxPeriodValidator(),
			new DocDateEffectiveDateRegValidator(), new ReverseChargeFlag(),
			new LUSgstinValidator(), new LURecipientGSTINValidation(),
			new CgstinValidatWithMaster(), new EInvoiceCmCgstinDxpValidation(),
			new CMRecipientGSTINValidation(), new LuPosvalidator(),
			new CMEInvoicePosvalidator(),new LUHsnOrSacValidator(),
			new CMHsnOrSacsupplyTypeValidator(),
			new BigDecimalNegativeAmountValidator(),
			new SupplyTypeTaxableValueValidation(),

			new LuIgstRateValidator(), new LuCgstRateValidator(),
			new LuSgstRateValidator(), new LuPortCodeValidator(),
			new ShippingBillNoAndDateAndPortCode(),
			//new CmPreceedingDocNum(),
			new CmOriginalDocNum(),
			//new CmPreedingRcrDocNum(), 
			new CmOrgDocDateRcrDocNum(),
			//new CmPreceedingDocDate(),
			new CmOriginalDocDate(),
			//new CmPreceedingExpDocDate(),
			new CmOrgExpDocDate(),
			//new CmPreceedingEInvoiceDocDate(),
			new CmOriginalEInvoiceDocDate(),
			new LuEcomGstinValidator(), new CMEcomGstinValidator(),
			new LuTransactionTypeValidation(), new ReturnPeriodRegValidator(),
			new LuOriginalDocType(), new DifferentialPercentageFlag(),
			new Section7OfIGSTFlag(), new ClaimRefundFlag(),
			new AutoPopulateToRefund(), new CMRecipientTypeValidationRule(), new ITCFlag(),
			new LUStateApplyingCess(), new TCSFlagGST(),
			new MulSupplyTypeCombValidations(),
			new SgstCgstIgstAmountValidator(),
			new SgstinCgstinAmountValidator(), new ExportWithoutTaxValidator(),
			new SgstCgstIgstValidator(), new OnBoardingSezValidation(),
			new Section7OfIgstFlag(), new CgstAndSgstAmountSameValidation(),
			new ArithmeticCheckTaxAmount(), new cgstinAndSgstinSameOrNot(),
			new LineNoValidation(), new DxpHsnValidation(),
			
			new CgstinValidatWithMaster(),
			
			new HsnAndRateProductMasterValidator(),
			new HsnProductMasterValidation(), new RateMasterValidation(),
			new SupplyTypeTaxableValueValidation(),
			new ClaimedRefoundFlagValidation(),
			new AutoPopulatedToRefoundFlag(),
			new Anx1OriginalDocNumSame(),
			new Anx1OriginalDocDateSame(),
			new DocNoStructureValidation(),
			//new Anx1ShippingBillNoSame(),
			//new Anx1ShipplingBillDate(),
			new GstinOutwardPermissionValidator(),
			

	};

	private static final DocRulesValidator[] GSTR1_VALIDATORS = {
			new ProfitCenter(), new Plant(),
			new Division(), new Location(), new SalesOrganisation(),
			new DistributionChannel(), new UserAccess1(), new UserAccess2(),
			new UserAccess3(), new UserAccess4(), new UserAccess5(),
			new UserAccess6(),
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
			//new CMgstr1OriginalDocdateValidator(),
			new CMgstr1OrgDocdateValidator(),
			//new CmPreceedingDocDate(),
			new CmOriginalDocDate(),
			new LuTransactionTypeValidation(), new DifferentialPercentageFlag(),
			//new Section7OfIGSTFlag(),
			new CRDRPreGST(), new CMRecipientTypeValidationRule(),
			new ITCFlag(), new LUStateApplyingCess(), new TCSFlagGST(),
			new MulSupplyTypeCombValidations(),
			new SgstCgstIgstAmountValidator(),
			new SgstinCgstinAmountValidator(), new ExportWithoutTaxValidator(),
			new SgstCgstIgstValidator(), new OnBoardingSezValidation(),
			new CgstAndSgstSameValidation(),
			new CgstAndSgstAmountSameValidation(),
			new ArithmeticCheckTaxAmount(), new cgstinAndSgstinSameOrNot(),
			new LineNoValidation(), new DxpHsnValidation(),
			//new CanGstnValidatior(),
			//new IsSubmitTogstnValidation(),
			new CgstinValidatWithMaster(),
			new HsnAndRateProductMasterValidator(),
			new HsnProductMasterValidation(), new RateMasterValidation(),
			new SupplyTypeTaxableValueValidation(),
			new PrecedingInvNumSame(),
     		new PrecedingInvDateSame(),
     		new OrgDocDateValidator(),
     		//new Cmgstr1Quantity(),
     		new ShippingBillStructure(),
     		new DocNoStructureValidation(),
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
		List<ProcessingResult> results = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("FormReturnTypes:" + document.getFormReturnType());
		}
		if (document.getComplianceApplicable()) {
		DocRulesValidator[] VALIDATORS = FormReturnTypes.GSTR1.getType()
				.equalsIgnoreCase(document.getFormReturnType())
						? GSTR1_VALIDATORS : ANX1_VALIDATORS;

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
				DocRulesValidatorPrefWrapper wrappedValidator = 
						(DocRulesValidatorPrefWrapper) 
						getWrappedValidator(validator);
				List<ProcessingResult> tmpResults = 
						wrappedValidator.validate(document, context);
				
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
		}else{
			
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
					DocRulesValidatorPrefWrapper wrappedValidator = 
							(DocRulesValidatorPrefWrapper) 
							getWrappedValidator(validator);
					List<ProcessingResult> tmpResults = 
							wrappedValidator.validate(document, context);
					
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
		return results;
	}

}
