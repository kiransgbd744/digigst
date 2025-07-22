/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("EinvoiceOutwardConfigParams")
public class EinvoiceOutwardConfigParams {

	public void configurableParameters(OutwardTransDocument document,
			Map<String, String> questionAnsMap) {
		// Set Invoice Value
		setDocAmount(document, questionAnsMap);
	}

	private static final List<String> SUPPLY_TYPE = ImmutableList
			.of(GSTConstants.NON, GSTConstants.SCH3);

	private static final List<String> DATAORIGINTYPECODE = ImmutableList.of("A",
			"AI", "B", "BI");

	private void setDocAmount(OutwardTransDocument document,
			Map<String, String> questionAnsMap) {
		String o1SelectedAnswer = questionAnsMap.get(
				OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O1.name());
		String ansA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name();
		String ansB = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name();
		String ansC = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.C.name();
		String ansD = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.D.name();

		BigDecimal taxableVal = document.getTaxableValue();
		BigDecimal otherValues = document.getOtherValues();
		BigDecimal igstAmt = document.getIgstAmount();
		BigDecimal cgstAmt = document.getCgstAmount();
		BigDecimal sgstAmt = document.getSgstAmount();
		BigDecimal cessAmtAdvalorem = document.getCessAmountAdvalorem();
		BigDecimal cessAmtSpecific = document.getCessAmountSpecific();
		BigDecimal stateCessAmt = document.getStateCessAmount();
		BigDecimal stateCessSpecificAmt = document.getStateCessSpecificAmt();
		BigDecimal invoiceOtherChargers = document.getInvoiceOtherCharges();
		BigDecimal userDiscountAmt = document.getUserDefinedField28();
		BigDecimal roundOffAmt = document.getRoundOff();

		if (taxableVal == null) {
			taxableVal = BigDecimal.ZERO;
		}
		if (otherValues == null) {
			otherValues = BigDecimal.ZERO;
		}
		if (igstAmt == null) {
			igstAmt = BigDecimal.ZERO;
		}
		if (cgstAmt == null) {
			cgstAmt = BigDecimal.ZERO;
		}
		if (sgstAmt == null) {
			sgstAmt = BigDecimal.ZERO;
		}
		if (cessAmtAdvalorem == null) {
			cessAmtAdvalorem = BigDecimal.ZERO;
		}
		if (cessAmtSpecific == null) {
			cessAmtSpecific = BigDecimal.ZERO;
		}
		if (stateCessAmt == null) {
			stateCessAmt = BigDecimal.ZERO;
		}
		if (stateCessSpecificAmt == null) {
			stateCessSpecificAmt = BigDecimal.ZERO;
		}

		if (invoiceOtherChargers == null) {
			invoiceOtherChargers = BigDecimal.ZERO;
		}
		if (userDiscountAmt == null) {
			userDiscountAmt = BigDecimal.ZERO;
		}
		if (roundOffAmt == null) {
			roundOffAmt = BigDecimal.ZERO;
		}

		BigDecimal docAmount = null;
		BigDecimal supplyAmt = BigDecimal.ZERO;
		BigDecimal OptCAmt = BigDecimal.ZERO;

		if (o1SelectedAnswer != null && !o1SelectedAnswer.trim().isEmpty()) {
			if (ansA.equalsIgnoreCase(o1SelectedAnswer)) {
				if ((document.getSupplyType() != null) && (SUPPLY_TYPE.contains(
						trimAndConvToUpperCase(document.getSupplyType())))) {

					if (null != document.getLineItems()) {
						for (OutwardTransDocLineItem item : document
								.getLineItems()) {
							if (item.getLineItemAmt() != null) {
								supplyAmt = supplyAmt
										.add(item.getLineItemAmt());
							}
						}
					}
					docAmount = supplyAmt;
				} else {
					docAmount = taxableVal.add(otherValues).add(igstAmt)
							.add(cgstAmt).add(sgstAmt).add(cessAmtAdvalorem)
							.add(cessAmtSpecific).add(invoiceOtherChargers)
							.add(roundOffAmt).subtract(userDiscountAmt);
				}
			} else if (ansB.equalsIgnoreCase(o1SelectedAnswer)) {
				if ((document.getSupplyType() != null) && (SUPPLY_TYPE.contains(
						trimAndConvToUpperCase(document.getSupplyType())))) {

					if (null != document.getLineItems()) {
						for (OutwardTransDocLineItem item : document
								.getLineItems()) {
							if (item.getLineItemAmt() != null) {
								supplyAmt = supplyAmt
										.add(item.getLineItemAmt());
							}
						}
					}
					docAmount = supplyAmt;
				} else {
					docAmount = taxableVal.add(otherValues).add(igstAmt)
							.add(cgstAmt).add(sgstAmt).add(cessAmtAdvalorem)
							.add(cessAmtSpecific).add(stateCessAmt)
							.add(stateCessSpecificAmt).add(invoiceOtherChargers)
							.add(roundOffAmt).subtract(userDiscountAmt);
				}
			} else if (ansC.equalsIgnoreCase(o1SelectedAnswer)) {
				if (null != document.getLineItems()) {
					for (OutwardTransDocLineItem item : document
							.getLineItems()) {
						if (item.getLineItemAmt() != null) {
							OptCAmt = OptCAmt.add(item.getLineItemAmt());
						}
					}
				}
				docAmount = OptCAmt;
			} else if (ansD.equalsIgnoreCase(o1SelectedAnswer)) {
				if ((!Strings.isNullOrEmpty(document.getDataOriginTypeCode()))
						&& (DATAORIGINTYPECODE.contains(trimAndConvToUpperCase(
								document.getDataOriginTypeCode())))) {
					docAmount = document.getDocAmount();
				} else {
					if (null != document.getLineItems()) {
						docAmount = document.getLineItems().get(0)
								.getLineItemAmt();
					}
				}
			} else {
				docAmount = BigDecimal.ZERO;
			}
		}
		document.setDocAmount(docAmount);
	}

	public void configurebleOnboardingLineItemAmt(OutwardTransDocument document,
			Map<String, String> questionAnsMap) {
		// Set Onboarding lint item Value
		setOnbLineItemAmt(document, questionAnsMap);
	}

	private void setOnbLineItemAmt(OutwardTransDocument document,
			Map<String, String> questionAnsMap) {

		String o1SelectedAnswer = questionAnsMap.get(
				OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O1.name());
		String ansA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name();
		String ansB = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name();
		String ansC = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.C.name();
		String ansD = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.D.name();

		BigDecimal onbLineItemAmt = BigDecimal.ZERO;

		if (o1SelectedAnswer != null && !o1SelectedAnswer.trim().isEmpty()) {
			if (ansA.equalsIgnoreCase(o1SelectedAnswer)) {
				BigDecimal zero = BigDecimal.ZERO;

				if (null != document.getLineItems()) {
					for (OutwardTransDocLineItem item : document
							.getLineItems()) {
						onbLineItemAmt = zero;

						if (item.getTaxableValue() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getTaxableValue());
						}
						if (item.getOtherValues() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getOtherValues());
						}
						if (item.getIgstAmount() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getIgstAmount());
						}
						if (item.getCgstAmount() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getCgstAmount());
						}
						if (item.getSgstAmount() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getSgstAmount());
						}
						if (item.getCessAmountSpecific() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getCessAmountSpecific());
						}
						if (item.getCessAmountAdvalorem() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getCessAmountAdvalorem());
						}
						item.setOnbLineItemAmt(onbLineItemAmt);
					}
				}
			} else if (ansB.equalsIgnoreCase(o1SelectedAnswer)
					|| ansD.equalsIgnoreCase(o1SelectedAnswer)) {
				BigDecimal zero = BigDecimal.ZERO;
				if (null != document.getLineItems()) {
					for (OutwardTransDocLineItem item : document
							.getLineItems()) {
						onbLineItemAmt = zero;

						if (item.getTaxableValue() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getTaxableValue());
						}
						if (item.getOtherValues() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getOtherValues());
						}
						if (item.getIgstAmount() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getIgstAmount());
						}
						if (item.getCgstAmount() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getCgstAmount());
						}
						if (item.getSgstAmount() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getSgstAmount());
						}
						if (item.getCessAmountSpecific() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getCessAmountSpecific());
						}
						if (item.getCessAmountAdvalorem() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getCessAmountAdvalorem());
						}
						if (item.getStateCessAmount() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getStateCessAmount());
						}
						if (item.getStateCessSpecificAmt() != null) {
							onbLineItemAmt = onbLineItemAmt
									.add(item.getStateCessSpecificAmt());
						}
						item.setOnbLineItemAmt(onbLineItemAmt);
					}
				}
			} else if (ansC.equalsIgnoreCase(o1SelectedAnswer)) {
				BigDecimal zero = BigDecimal.ZERO;
				if (null != document.getLineItems()) {
					for (OutwardTransDocLineItem item : document
							.getLineItems()) {
						onbLineItemAmt = zero;
						onbLineItemAmt = item.getLineItemAmt();
						item.setOnbLineItemAmt(onbLineItemAmt);
					}
				}
			}
		}
	}
}
