package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Component("OutwardConfigParams")
public class OutwardConfigParams {

	public void configurableParameters(OutwardTransDocument document,
			Map<String, String> questionAnsMap) {
		// Set Invoice Value
		setDocAmount(document, questionAnsMap);
	}

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
		BigDecimal docAmount = null;
		if (o1SelectedAnswer != null && !o1SelectedAnswer.trim().isEmpty()) {
			if (ansA.equalsIgnoreCase(o1SelectedAnswer)) {
				docAmount = taxableVal.add(otherValues).add(igstAmt)
						.add(cgstAmt).add(sgstAmt).add(cessAmtAdvalorem)
						.add(cessAmtSpecific);
			} else if (ansB.equalsIgnoreCase(o1SelectedAnswer)) {
				docAmount = taxableVal.add(otherValues).add(igstAmt)
						.add(cgstAmt).add(sgstAmt).add(cessAmtAdvalorem)
						.add(cessAmtSpecific).add(stateCessAmt);
			} else if (ansC.equalsIgnoreCase(o1SelectedAnswer)) {
				docAmount = document.getDocAmount();
			} else if (ansD.equalsIgnoreCase(o1SelectedAnswer)) {
				docAmount = document.getLineItems().get(0).getLineItemAmt();
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
			} else if (ansB.equalsIgnoreCase(o1SelectedAnswer)) {
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
			} else if (ansD.equalsIgnoreCase(o1SelectedAnswer)) {
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
			}
		}
	}
}
