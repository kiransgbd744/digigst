package com.ey.advisory.app.services.docs.gstr2;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.common.GSTConstants;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Component("InwardConfigParams")
public class InwardConfigParams {

	public void configurableParameters(InwardTransDocument document,
			Map<String, String> questionAnsMap) {
		// Set Invoice Value
		setDocAmount(document, questionAnsMap);
	}

	private void setDocAmount(InwardTransDocument document,
			Map<String, String> questionAnsMap) {
		String i1SelectedAnswer = questionAnsMap.get(
				OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID.I1.name());
		String ansA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name();
		String ansB = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name();
		String ansC = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.C.name();
		String ansD = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.D.name();

		List<InwardTransDocLineItem> items = document.getLineItems();

		BigDecimal itemStateCessSpecificAmt = BigDecimal.ZERO;
		for (InwardTransDocLineItem item : items) {
			BigDecimal stateCessSpecificAmt = item.getStateCessSpecificAmt();
			if (stateCessSpecificAmt == null) {
				stateCessSpecificAmt = BigDecimal.ZERO;
			}
			if (item.getStateCessSpecificAmt() != null) {
				itemStateCessSpecificAmt = itemStateCessSpecificAmt
						.add(stateCessSpecificAmt);
			}
		}
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
		BigDecimal OptCAmt = BigDecimal.ZERO;
		if (i1SelectedAnswer != null && !i1SelectedAnswer.trim().isEmpty()) {
			if (ansA.equalsIgnoreCase(i1SelectedAnswer)) {
				docAmount = taxableVal.add(otherValues).add(igstAmt)
						.add(cgstAmt).add(sgstAmt).add(cessAmtAdvalorem)
						.add(cessAmtSpecific);
				document.setDocAmount(docAmount);
			} else if (ansB.equalsIgnoreCase(i1SelectedAnswer)) {
				docAmount = taxableVal.add(otherValues).add(igstAmt)
						.add(cgstAmt).add(sgstAmt).add(cessAmtAdvalorem)
						.add(cessAmtSpecific).add(stateCessAmt)
						.add(itemStateCessSpecificAmt);
				document.setDocAmount(docAmount);
			} else if (ansC.equalsIgnoreCase(i1SelectedAnswer)) {
				if (null != document.getLineItems()) {
					for (InwardTransDocLineItem item : document
							.getLineItems()) {
						if (item.getLineItemAmt() != null) {
							OptCAmt = OptCAmt.add(item.getLineItemAmt());
						}
					}
				}
				document.setDocAmount(OptCAmt);
			} else if (ansD.equalsIgnoreCase(i1SelectedAnswer)) {
				if ((GSTConstants.DataOriginTypeCodes.ERP_API
						.getDataOriginTypeCode()
						.equalsIgnoreCase(document.getDataOriginTypeCode()))
						|| (GSTConstants.DataOriginTypeCodes.ERP_API_INV_MANAGMENT_CORR
								.getDataOriginTypeCode().equalsIgnoreCase(
										document.getDataOriginTypeCode()))) {
					docAmount = document.getDocAmount();
					document.setDocAmount(docAmount);
				} else {
					if (null != document.getLineItems()) {
						docAmount = document.getLineItems().get(0)
								.getLineItemAmt();
						document.setDocAmount(docAmount);
					}
				}
			}
		}
	}
}