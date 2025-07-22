package com.ey.advisory.app.services.docs.gstr7;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Component("DefaultGstr7TransDocBifurcator")
@Slf4j
public class DefaultGstr7TransDocBifurcator
		implements Gstr7TransDocBifurcator<Gstr7TransDocHeaderEntity> {

	@Override
	public Gstr7TransDocHeaderEntity bifurcate(
			Gstr7TransDocHeaderEntity document, ProcessingContext context) {

		Gstr7TransDocHeaderEntity doc = null;
		String docNum = document.getDocNum();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to bifurcate the invoice, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		try {

			doc = setSection(document, context);

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

	private Gstr7TransDocHeaderEntity setSection(
			Gstr7TransDocHeaderEntity document, ProcessingContext context) {
		String docNum = document.getDocNum();
		String docType = document.getDocType();

		final List<String> docTypes = ImmutableList.of(GSTConstants.INV,
				GSTConstants.RNV);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table4, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}
		if (document.getDocType() == null
				|| !docTypes.contains(document.getDocType())) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table4,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}
			return document;
		}

		if (docType.equalsIgnoreCase(GSTConstants.INV))
			document.setSection(GSTConstants.TDS);
		else if (docType.equalsIgnoreCase(GSTConstants.RNV)) {
			document.setSection(GSTConstants.TDSA);
		} else {

		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table4, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}
		return document;
	}

	@Override
	public boolean isBifurcated(Gstr7TransDocHeaderEntity doc) {
		return doc.getSection() != null;
	}
}
