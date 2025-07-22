package com.ey.advisory.app.services.validation.sales;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.BusinessRuleExclusionRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.DocRulesValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.google.common.collect.ImmutableList;

/**
 * This class is responsible for skip the business rules
 * 
 * @author Murali.Singanamala
 *
 */
@Component("SkipRulesValidatorChain")
public class SkipRulesValidatorChain
		implements DocRulesValidatorChain<OutwardTransDocument> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SkipRulesValidatorChain.class);

	// Maintain a list of validators that can be invoked in sequence.
	// This can be the preliminary implementation. Later on we can have
	// more complex rule ordering and rule evaluation decisions stored in the
	// DB and invoked for the list of validations.
	
	private static final List<String> VALIDATORS = ImmutableList.of(
			"LUHsnOrSacValidator","LuIgstRateValidator","LuCgstRateValidator",
			"LuSgstRateValidator","EinvoiceUqcValidator","DocTypeMasterValidation",
			"SuppltTypeMasterValidation","LuTransportValidation",
			"EInvoiceSgstinValidation","EInvoiceQuantityValidation",
			"EInvoiceCmCgstinDxpValidation","LuBillToStateValidator",
			"LuPosvalidator","LuCurrencyCodeValidation","LuCountryCodeValidation",
			"LuVehicleTypeValidation","IsServiceValidation","SupplierStateCode",
			"LuModeOfPaymentValidation","TaxSchemaValidation",
			"LuDocCategoryValidation","LuTransactionTypeValidation",
			"LuEwbCgstinValidation","CmDocCatogeryValdaton","TransDocNoAndTransDocDateValidator",
			"VehicleNovalidation","SuSupplyTypeValidation","CmOtherSupplyDesc",
			"ReverseChargeFlag", "LUSgstinValidator","LURecipientGSTINValidation",
			 "CgstinValidatWithMaster","LuPosvalidator","CMPosvalidator",
			 "CMHsnOrSacsupplyTypeValidator","BigDecimalNegativeAmountValidator",
			 "SupplyTypeTaxableValueValidation", "LuPortCodeValidator",
			 "LuEcomGstinValidator","CMEcomGstinValidator","MulSupplyTypeCombValidations",
			 "SgstCgstIgstAmountValidator","SgstinCgstinAmountValidator", 
			 "ExportWithoutTaxValidator","OnBoardingSezValidation",
			"SgstCgstIgstValidator","Section7OfIgstFlag",
			"ArithmeticCheckTaxAmount",
			 "CgstAndSgstAmountSameValidation",
			 "cgstinAndSgstinSameOrNot",  "LineNoValidation","DxpHsnValidation",
			 "CmGstr1PosValidation","UqcValidator","CanGstnValidatior",
			 "ShippingBillNoAndDateAndPortCode","LuOriginalDocType",
			 "DifferentialPercentageFlag","Section7OfIGSTFlag","CRDRPreGST",
			 "ITCFlag","LUStateApplyingCess","TCSFlagGST",
			 "ClaimRefundFlag","AutoPopulateToRefund",
			 "PrecedingInvDateSame","PrecedingInvDateSame",
			 "OriginalInvNoSame","OriginalInvDateSame","CustomerType"
			
			);
	

	@Autowired
	@Qualifier("BusinessRuleExclusionRepository")
	private BusinessRuleExclusionRepository repository;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		// Get the group code from the processing context.
		String groupCode = (String) context.getAttribute("groupCode");
		// get the list of business rules to be excluded.
		List<String> exclusions = repository.findByGroupCode(groupCode);
		List<ProcessingResult> results = new ArrayList<>();

		// Remove the excluded rules from the list.
		List<String> finalRules = ListUtils.subtract(VALIDATORS, exclusions);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Skippling rules",exclusions);
		}
		finalRules.forEach(ruleName -> {
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Executing validator '%s'",
							ruleName);
					LOGGER.debug(msg);
				}

				@SuppressWarnings("unchecked")
				DocRulesValidator<OutwardTransDocument> validator = (DocRulesValidator<OutwardTransDocument>) StaticContextHolder
						.getBean(ruleName, DocRulesValidator.class);

				List<ProcessingResult> tmpResults = new ArrayList<>();
				tmpResults = validator.validate(document, context);
				results.addAll(tmpResults);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Executed validator '%s'. No: of Results "
									+ "available: '%d'",
							ruleName,
							(tmpResults != null) ? tmpResults.size() : 0);
					LOGGER.debug(msg);
				}
			} catch (Exception ex) {
				// create a processing result to inform that the validator
				// failed.
				String exName = ex.getClass().getSimpleName();
				String msg = String
						.format("Error while executing the validator '%s'. "
								+ "Exception: '%s'", ruleName, exName);
				ProcessingResult result = new ProcessingResult("LOCAL",
						ProcessingResultType.ERROR, "ER9999", msg, null);
				results.add(result);
				LOGGER.error(msg);
			}
		});

		return results;
	}

}
