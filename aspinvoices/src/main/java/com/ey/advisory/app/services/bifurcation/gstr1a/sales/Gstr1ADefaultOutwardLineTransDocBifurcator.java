package com.ey.advisory.app.services.bifurcation.gstr1a.sales;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr1ADefaultOutwardLineTransDocBifurcator")
@Slf4j
public class Gstr1ADefaultOutwardLineTransDocBifurcator
		implements DocBifurcator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("DocRepositoryGstr1A")
	DocRepositoryGstr1A docRepository;

	@Override
	public Gstr1AOutwardTransDocument bifurcate(
			Gstr1AOutwardTransDocument document, ProcessingContext context) {

		Gstr1AOutwardTransDocument doc = null;
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

	private Gstr1AOutwardTransDocument checkforSupplyType(
			Gstr1AOutwardTransDocument document, ProcessingContext context) {

		Set<String> distinctSupplyTypes = document.getLineItems().stream()
				.map(Gstr1AOutwardTransDocLineItem::getSupplyType).distinct()
				.collect(Collectors.toSet());

		boolean isMultiSuppInv = false;

		if (distinctSupplyTypes.contains("TAX")
				&& (distinctSupplyTypes.contains("NIL")
						|| distinctSupplyTypes.contains("NON")
						|| distinctSupplyTypes.contains("EXT")
						|| distinctSupplyTypes.contains("SCH3"))) {
			isMultiSuppInv = true;
			String pos = document.getPos();
			String cgstin = document.getCgstin();

			for (Gstr1AOutwardTransDocLineItem lineItem : document
					.getLineItems()) {
				String supplyType = lineItem.getSupplyType();
				if (supplyType.equalsIgnoreCase(GSTConstants.TAX)) {
					lineItem.setItmGstnBifurcation(
							document.getGstnBifurcation());
					lineItem.setItmTableType(document.getTableType());
				} else {
					lineItem.setItmGstnBifurcation(GSTConstants.NIL_EXT_NON);
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
			for (Gstr1AOutwardTransDocLineItem lineItem : document
					.getLineItems()) {
				lineItem.setItmGstnBifurcation(document.getGstnBifurcation());
				lineItem.setItmTableType(document.getTableType());
			}
		}
		return document;
	}

	@Override
	public boolean isBifurcated(Gstr1AOutwardTransDocument doc) {
		return true;
	}
}
