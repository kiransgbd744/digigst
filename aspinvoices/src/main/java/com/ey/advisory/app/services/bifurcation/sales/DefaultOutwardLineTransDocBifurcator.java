package com.ey.advisory.app.services.bifurcation.sales;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Component("DefaultOutwardLineTransDocBifurcator")
@Slf4j
public class DefaultOutwardLineTransDocBifurcator
		implements DocBifurcator<OutwardTransDocument> {

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public OutwardTransDocument bifurcate(OutwardTransDocument document,
			ProcessingContext context) {

		OutwardTransDocument doc = null;
		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to bifurcate the invoice, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		try {

			doc = checkforSupplyType(document, context);
			if (isBifurcated(doc))
				return doc;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Document bifurcation failed. "
						+ "Check the bifurcation rules");
			}
		} catch (Exception ex) {
			LOGGER.error("Bifurcation failed due to exception.", ex);
		}
		return doc;
	}

	private OutwardTransDocument checkforSupplyType(
			OutwardTransDocument document, ProcessingContext context) {
		Set<String> distinctSupplyTypes = document.getLineItems().stream()
				.map(OutwardTransDocLineItem::getSupplyType).distinct()
				.collect(Collectors.toSet());
		String multiSupplyTypeAns = (String) context.getAttribute("isMultiSupplyOpted");

		boolean isMultiSuppInv = false;
		if (Strings.isNullOrEmpty(multiSupplyTypeAns)
				|| "B".equalsIgnoreCase(multiSupplyTypeAns)) {
			if (LOGGER.isInfoEnabled()) {
				String logMsg = String
						.format("There is multi supply type is opted as No ,"
								+ " Hence stamping the header details in Itm Details as well. ");

				LOGGER.info(logMsg);
			}
			for (OutwardTransDocLineItem lineItem : document.getLineItems()) {
				lineItem.setItmGstnBifurcation(document.getGstnBifurcation());
				lineItem.setItmTableType(document.getTableType());
			}
			document.setMultiSuppInv(isMultiSuppInv);
			return document;
		} else {
			if (distinctSupplyTypes.contains("TAX")
					&& (distinctSupplyTypes.contains("NIL")
							|| distinctSupplyTypes.contains("NON")
							|| distinctSupplyTypes.contains("EXT")
							|| distinctSupplyTypes.contains("SCH3"))) {
				isMultiSuppInv = true;
				String pos = document.getPos();
				String cgstin = document.getCgstin();

				for (OutwardTransDocLineItem lineItem : document
						.getLineItems()) {
					String supplyType = lineItem.getSupplyType();
					if (supplyType.equalsIgnoreCase(GSTConstants.TAX)) {
						lineItem.setItmGstnBifurcation(
								document.getGstnBifurcation());
						lineItem.setItmTableType(document.getTableType());
					} else {
						lineItem.setItmGstnBifurcation(
								GSTConstants.NIL_EXT_NON);
						boolean isSame = document.getSgstin().substring(0, 2)
								.equalsIgnoreCase(pos);
						if (cgstin != null && !"URP".equalsIgnoreCase(cgstin)) {
							if (isSame)
								lineItem.setItmTableType(GSTConstants.GSTR1_8B);
							else
								lineItem.setItmTableType(GSTConstants.GSTR1_8A);
						} else if (Strings.isNullOrEmpty(cgstin)
								|| "URP".equalsIgnoreCase(cgstin)) {
							if (isSame)
								lineItem.setItmTableType(GSTConstants.GSTR1_8D);
							else
								lineItem.setItmTableType(GSTConstants.GSTR1_8C);
						}
					}
				}
				document.setMultiSuppInv(isMultiSuppInv);
			} else {
				for (OutwardTransDocLineItem lineItem : document
						.getLineItems()) {
					lineItem.setItmGstnBifurcation(
							document.getGstnBifurcation());
					lineItem.setItmTableType(document.getTableType());
				}
			}
			return document;
		}
	}

	@Override
	public boolean isBifurcated(OutwardTransDocument doc) {
		return true;
	}
}
