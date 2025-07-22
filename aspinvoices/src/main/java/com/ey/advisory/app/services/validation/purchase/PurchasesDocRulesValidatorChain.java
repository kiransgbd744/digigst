package com.ey.advisory.app.services.validation.purchase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.common.PerfStatistics;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("PurchasesDocRulesValidatorChain")
public class PurchasesDocRulesValidatorChain
		implements DocRulesValidatorChain<InwardTransDocument> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PurchasesDocRulesValidatorChain.class);

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.

	private static final DocRulesValidator[] GSTR2_ISD_VALIDATORS = {

			new ProfitValidatior(), new PlantValidator(),
			new DivisionValidator(), new LocationValidator(), new PurcheseOrg(),
			new UserAccess1(), new UserAccess2(), new UserAccess3(),
			new UserAccess4(), new UserAccess5(), new UserAccess6(),
			new ReturnPeriodValidator(), new RecipientGstin(),
			new DocDateTaxPeriodValidator(),
			new DocDateEffectiveDateRegValidator(),
			new Gstr2IsdLuDocTypeValidation(),
			new Gstr2IsdLuSupplyTypeValidation(), new CrDrFlag(),
			new DiffPercentFlag(), new HsnOrSac(),
			new Gstr6115EligibilityIndicator(), new CmEligibilityIndicator(),
			new AutoPopulateToRefound(), new RevChargeFlag(),
			new ReverseChargeFlagValidator(), new CommSupplyIndiCator(),
			new ITCEntitlement(), new ITCRevIdentifier(),
			new AvailableIgstValidator(), new AvailableCgstValidator(),
			new AvailableSgstValidator(), new AvailableCessValidator(),
			new AvailableIgstEligibilityValidator(),
			new AvailableCgstEligibilityValidator(),
			new AvailableSgstEligibilityValidator(),
			new AvailableCessEligibilityValidator(),
			// new gstr6OrgDocNumdateMandatory(),need to discuss for gstr2 isd
			new SgstinAndCgstinSame(),
			new InwardDecimalNegativeAmountValidator(),
			new VendorGstinMasterValiadtion(),
			new InwardDocNoStructureValidation(),
			new StateCessSpecificRateValidation(),
			new InwardsgstinStructValidtion(),
			new GstinPermissionValidator(),

	};

	private static final DocRulesValidator[] GSTR6_VALIDATORS = {
			new ProfitValidatior(), new PlantValidator(),
			new DivisionValidator(), new LocationValidator(), new PurcheseOrg(),
			new UserAccess1(), new UserAccess2(), new UserAccess3(),
			new UserAccess4(), new UserAccess5(), new UserAccess6(),
			new ReturnPeriodValidator(), new RecipientGstin(),
			new DocumentType(), new SupplyType(),
			new DocDateTaxPeriodValidator(),
			new DocDateEffectiveDateRegValidator(),
			new gstr6OrgDocNumdateMandatory(), new CrDrFlag(),
			new LUSgstinValidator(), new CMSgstinValidation(),
			new CMsupplierGstinValidation(), new SupplierType(), new Pos(),
			new PortCode(),
			// new BillOfEntry(),
			// new BillOfEntryDate(),
			new HsnOrSac(), new IgstRateValidator(), new CgstRateValidator(),
			new SgstRateValidator(), new AutoPopulateToRefound(),
			new CliamRefoundFlag(), new RevChargeFlag(),
			new ReverseChargeFlagValidator(),
			new Gstr6115EligibilityIndicator(), new CommSupplyIndiCator(),
			new AvailableIgstValidator(), new AvailableCgstValidator(),
			new AvailableSgstValidator(), new AvailableCessValidator(),
			new AvailableIgstEligibilityValidator(),
			new AvailableCgstEligibilityValidator(),
			new AvailableSgstEligibilityValidator(),
			new AvailableCessEligibilityValidator(), new ITCEntitlement(),
			new ITCRevIdentifier(), new Section7IgstFlag(),
			new DiffPercentFlag(), new Section7OfIgstFlagValidationRule(),
			new CgstAndSgstSameVaidation(),
			new gstr6MultipleSupplyTypeValidations(), new SgstinAndCgstinSame(),
			// new Gstr6SgstCgstIgstValidatorWithOutRcm(),
			new SgstCgstIgstValidatorWithRcm(),
			new SgstCgstIgstValidatorWithOutRcm(),
			new CgstIgstSgstValidationRule(),
			// new InwardCanValidation(),
			new VendorGstinMasterValiadtion(),
			new HsnOrSacANDRateMasterValidation(), new RateMasterValidation(),
			new HsnOrSacMasterValidation(), new CmEligibilityIndicator(),
			new NilNOnExtTaxValidation(), new ServiceEligibilityValidator(),
			new InwardDecimalNegativeAmountValidator(),
			new InwardDocNoStructureValidation(),
			new StateCessSpecificRateValidation(),
			new InwardsgstinStructValidtion(), new SezgOnboardingValidation(),
			new IsGstr6ReturnFiled(),
			new GstinPermissionValidator(),};

	private static final DocRulesValidator[] GSTR2_VALIDATORS = {

			new InwardDecimalNegativeAmountValidator(), new ProfitValidatior(),
			new PlantValidator(), new DivisionValidator(),
			new LocationValidator(), new PurcheseOrg(), new UserAccess1(),
			new UserAccess2(), new UserAccess3(), new UserAccess4(),
			new UserAccess5(), new UserAccess6(), new ReturnPeriodValidator(),
			new RecipientGstin(), new DocumentType(), new SupplyType(),
			new DocDateTaxPeriodValidator(),
			new DocDateEffectiveDateRegValidator(), new LUSgstinValidator(),
			new CMSgstinValidation(), new CMsupplierGstinValidation(),
			new SupplierType(), new Pos(), new PortCode(),
			new BillOfEntry(),
			new BillOfEntryDate(),
			new HsnOrSac(), new IgstRateValidator(), new CgstRateValidator(),
			new SgstRateValidator(), new ReverseChargeFlagValidator(),
			new EligibilityIndicator(), new HsnEligibilityIndicator(),
			new AvailableIgstValidator(), new AvailableCgstValidator(),
			new AvailableSgstValidator(), new AvailableCessValidator(),
			new AvailableIgstEligibilityValidator(),
			new AvailableCgstEligibilityValidator(),
			new AvailableSgstEligibilityValidator(),
			new AvailableCessEligibilityValidator(), new CrDrFlag(),
			new DiffPercentFlag(), new Section7IgstFlag(),
			new CliamRefoundFlag(), new AutoPopulateToRefound(),
			new RevChargeFlag(), new CommSupplyIndiCator(),
			new ITCEntitlement(), new ITCRevIdentifier(),
			// BR
			new CgstIgstSgstValidationRule(),
			new Section7OfIgstFlagValidationRule(), new SgstinAndCgstinSame(),
			new CgstinAndPosDiff(), new SupplyTypeSezs(),
			new SupplyTypeImports(), new SgstCgstIgstValidatorWithRcm(),
			new SgstCgstIgstValidatorWithOutRcm(),
			new CgstAndSgstSameVaidation(), new SupplyTypeSezg(),
			new MultipleSupplyTypeValidations(),
			new StateCessSpecificRateValidation(),

			// onboarding
			new CrdrOriginaldocumentdetailsValidator(),
			new VendorGstinMasterValiadtion(),
			new HsnOrSacANDRateMasterValidation(), new RateMasterValidation(),
			new HsnOrSacMasterValidation(), new CmEligibilityIndicator(),
			new InwardDocNoStructureValidation(),
			new ServiceEligibilityValidator(),
			new InwardsgstinStructValidtion(), new SezgOnboardingValidation(),
			new GstinPermissionValidator(),

	};

	static class DocRulesValidatorPrefWrapper
			implements DocRulesValidator<InwardTransDocument> {

		private DocRulesValidator<InwardTransDocument> validator;
		private long execTime;

		DocRulesValidatorPrefWrapper(
				DocRulesValidator<InwardTransDocument> validator) {
			this.validator = validator;
		}

		@Override
		public List<ProcessingResult> validate(InwardTransDocument document,
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

	private DocRulesValidator<InwardTransDocument> getWrappedValidator(
			DocRulesValidator<InwardTransDocument> validator) {
		return new DocRulesValidatorPrefWrapper(validator);
	}

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		// returns true,if the records satisfied gstr2 applicability else return
		// false
		boolean gstr2Applicability = gstr2Applicability(document);
		boolean gstr2IsdApplicability = gstr2IsdApplicability(document,
				context);

		DocRulesValidator[] VALIDATORS = GSTR2_VALIDATORS;

		if (gstr2Applicability) {
			if (gstr2IsdApplicability) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"gstr2 ISD validations are calling for docKey{}",
							document.getDocKey());
				}
				VALIDATORS = GSTR2_ISD_VALIDATORS;
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstr2 validations are calling for docKey{}",
							document.getDocKey());
				}
				VALIDATORS = GSTR2_VALIDATORS;
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("gstr6 validations are calling for docKey{}",
						document.getDocKey());
			}
			VALIDATORS = GSTR6_VALIDATORS;
		}

		/*
		 * DocRulesValidator[] VALIDATORS = gstr2Applicability ?
		 * GSTR2_VALIDATORS : GSTR6_VALIDATORS; if(gstr2IsdApplicability){
		 * VALIDATORS=GSTR2_ISD_VALIDATORS; }
		 */
		List<ProcessingResult> results = new ArrayList<>();
		for (DocRulesValidator<InwardTransDocument> validator : VALIDATORS) {
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
				results.add(result);
				LOGGER.error(msg);
			}
		}
		return results;
	}

	private boolean gstr2Applicability(InwardTransDocument document) {

		String groupCode = TenantContext.getTenantId();
		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				document.getCgstin());
		if (gstin != null) {
			String registrationType = gstin.getRegistrationType();
			if (GSTConstants.ISD.equalsIgnoreCase(registrationType)) {
				return false;
			}
		}
		return true;

	}

	private boolean gstr2IsdApplicability(InwardTransDocument document,
			ProcessingContext context) {
		if (Strings.isNullOrEmpty(document.getSgstin()))
			return false;
		if (GSTConstants.I.equalsIgnoreCase(document.getCustOrSuppType())) {
			return true;
		}
		Map<String, String> gstinInfoMap = (Map<String, String>) context
				.getAttribute("gstinInfoMap");
		if (gstinInfoMap.containsKey(document.getSgstin())
				&& GSTConstants.ISD.equalsIgnoreCase(gstinInfoMap.get(document.getSgstin()))) {
			return true;
		}
		return false;

	}
}
