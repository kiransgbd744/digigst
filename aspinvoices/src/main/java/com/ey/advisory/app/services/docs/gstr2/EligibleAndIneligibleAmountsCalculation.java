/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr2;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_INWARD_ANS_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.google.common.collect.ImmutableList;

import com.google.common.base.Strings;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("EligibleAndIneligibleAmountsCalculation")
public class EligibleAndIneligibleAmountsCalculation {

	public void configParamForEligibleAndIneligibleItemAmts(
			InwardTransDocLineItem item, Map<String, String> questionAnsMap) {
		// Set Eligible and Ineligible amts in item level
		setEligibleIneligibleItemAmts(item, questionAnsMap);
	}

	private static final List<String> ELIGIBILITY_TYPES = ImmutableList
			.of(GSTConstants.IS, GSTConstants.IG, GSTConstants.CG);

	private void setEligibleIneligibleItemAmts(InwardTransDocLineItem item,
			Map<String, String> questionAnsMap) {
		String i15SelectedAnswer = questionAnsMap.get(
				OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID.I15.name());
		String ansA = CONFIG_PARAM_INWARD_ANS_KEY_ID.A.name();
		String ansB = CONFIG_PARAM_INWARD_ANS_KEY_ID.B.name();
		String ansC = CONFIG_PARAM_INWARD_ANS_KEY_ID.C.name();

		BigDecimal availableIgst = item.getAvailableIgst();
		BigDecimal availableCgst = item.getAvailableCgst();
		BigDecimal availableSgst = item.getAvailableSgst();
		BigDecimal availableCess = item.getAvailableCess();
		BigDecimal igst = item.getIgstAmount();
		BigDecimal cgst = item.getCgstAmount();
		BigDecimal sgst = item.getSgstAmount();
		BigDecimal cess = item.getCessAmount();

		if (availableIgst == null) {
			availableIgst = BigDecimal.ZERO;
		}
		if (availableCgst == null) {
			availableCgst = BigDecimal.ZERO;
		}
		if (availableSgst == null) {
			availableSgst = BigDecimal.ZERO;
		}
		if (availableCess == null) {
			availableCess = BigDecimal.ZERO;
		}
		if (igst == null) {
			igst = BigDecimal.ZERO;
		}
		if (cgst == null) {
			cgst = BigDecimal.ZERO;
		}
		if (sgst == null) {
			sgst = BigDecimal.ZERO;
		}
		if (cess == null) {
			cess = BigDecimal.ZERO;
		}

		if (i15SelectedAnswer != null && !i15SelectedAnswer.trim().isEmpty()) {
			if (ansA.equalsIgnoreCase(i15SelectedAnswer)) {
				if (!Strings.isNullOrEmpty(item.getEligibilityIndicator())) {
					if (ELIGIBILITY_TYPES.contains(trimAndConvToUpperCase(
							item.getEligibilityIndicator()))) {

						item.setEligibleIgst(availableIgst);
						item.setEligibleCgst(availableCgst);
						item.setEligibleSgst(availableSgst);
						item.setEligibleCess(availableCess);
						item.setInEligibleIgst(igst.subtract(availableIgst));
						item.setInEligibleCgst(cgst.subtract(availableCgst));
						item.setInEligibleSgst(sgst.subtract(availableSgst));
						item.setInEligibleCess(cess.subtract(availableCess));
					} else if (GSTConstants.NO
							.equalsIgnoreCase(item.getEligibilityIndicator())) {
						item.setEligibleIgst(availableIgst);
						item.setEligibleCgst(availableCgst);
						item.setEligibleSgst(availableSgst);
						item.setEligibleCess(availableCess);
						item.setInEligibleIgst(igst.subtract(availableIgst));
						item.setInEligibleCgst(cgst.subtract(availableCgst));
						item.setInEligibleSgst(sgst.subtract(availableSgst));
						item.setInEligibleCess(cess.subtract(availableCess));
					}
				}
			} else if (ansB.equalsIgnoreCase(i15SelectedAnswer)) {

				item.setEligibleIgst(igst);
				item.setEligibleCgst(cgst);
				item.setEligibleSgst(sgst);
				item.setEligibleCess(cess);
				item.setInEligibleIgst(BigDecimal.ZERO);
				item.setInEligibleCgst(BigDecimal.ZERO);
				item.setInEligibleSgst(BigDecimal.ZERO);
				item.setInEligibleCess(BigDecimal.ZERO);

			} else if (ansC.equalsIgnoreCase(i15SelectedAnswer)) {

				if (!Strings.isNullOrEmpty(item.getEligibilityIndicator())) {
					if (ELIGIBILITY_TYPES.contains(trimAndConvToUpperCase(
							item.getEligibilityIndicator()))) {
						item.setEligibleIgst(igst);
						item.setEligibleCgst(cgst);
						item.setEligibleSgst(sgst);
						item.setEligibleCess(cess);
						item.setInEligibleIgst(BigDecimal.ZERO);
						item.setInEligibleCgst(BigDecimal.ZERO);
						item.setInEligibleSgst(BigDecimal.ZERO);
						item.setInEligibleCess(BigDecimal.ZERO);
					} else if (GSTConstants.NO
							.equalsIgnoreCase(item.getEligibilityIndicator())) {
						item.setEligibleIgst(BigDecimal.ZERO);
						item.setEligibleCgst(BigDecimal.ZERO);
						item.setEligibleSgst(BigDecimal.ZERO);
						item.setEligibleCess(BigDecimal.ZERO);
						item.setInEligibleIgst(igst);
						item.setInEligibleCgst(cgst);
						item.setInEligibleSgst(sgst);
						item.setInEligibleCess(cess);
					}
				}
			}
		}
	}
}
