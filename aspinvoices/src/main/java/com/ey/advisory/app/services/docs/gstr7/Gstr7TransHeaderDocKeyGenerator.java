package com.ey.advisory.app.services.docs.gstr7;

import java.time.LocalDate;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.gstr7Trans.GSTR7TransCanValidation;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component("Gstr7TransHeaderDocKeyGenerator")
@Slf4j
public class Gstr7TransHeaderDocKeyGenerator
		implements DocKeyGenerator<Gstr7TransDocHeaderEntity, String> {

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String generateKey(Gstr7TransDocHeaderEntity doc) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr7TransHeaderDocKeyGenerator inside generateKey()");
		}

		String docType = (doc.getDocType() != null) ? doc.getDocType().trim()
				: "";
		String docNo = (doc.getDocNum() != null) ? doc.getDocNum().trim() : "";

		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}

		String deductorGstin = (doc.getDeductorGstin() != null)
				? doc.getDeductorGstin().trim() : "";

		String finYear = doc.getFiYear() != null ? doc.getFiYear() : "";
		
		String generateKey = new StringJoiner(DOC_KEY_JOINER).add(docType).add(deductorGstin)
		.add(finYear).add(docNo).toString();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr7TransHeaderDocKeyGenerator generateKey : "+generateKey);
		}

		return new StringJoiner(DOC_KEY_JOINER).add(docType).add(deductorGstin)
				.add(finYear).add(docNo).toString();
	}

	@Override
	public String generateOrgKey(Gstr7TransDocHeaderEntity doc) {

		String originalDocNum = (doc.getOriginalDocNum() != null)
				? doc.getOriginalDocNum().trim() : null;
		if (originalDocNum != null
				&& originalDocNum.startsWith(GSTConstants.SPE_CHAR)) {
			originalDocNum = originalDocNum.substring(1);
		}

		LocalDate orgDocDate = (doc.getOriginalDocDate() != null)
				? doc.getOriginalDocDate() : null;
		LocalDate localDocDate = DateUtil.parseObjToDate(orgDocDate);
		String orgFinYear = GenUtil.getFinYear(localDocDate);
		
		String deductorGstin = (doc.getDeductorGstin() != null)
				? doc.getDeductorGstin().trim() : "";
		String docType = (doc.getDocType() != null) ? doc.getDocType().trim()
				: "";

		return new StringJoiner(DOC_KEY_JOINER).add(docType).add(deductorGstin)
				.add(orgFinYear).add(originalDocNum).toString();
	}
	
	public static void main(String[] args) {
		
		Gstr7TransDocHeaderEntity doc= new Gstr7TransDocHeaderEntity();
		doc.setDocType("INV");
		doc.setDocNum("DFFGH990635");
		doc.setDeductorGstin("06AAAAK0204G6ZA");
//		doc.setFiYear("202425");
		
		String generateKey = new Gstr7TransHeaderDocKeyGenerator().generateKey(doc);
		
		System.out.println("generateKey : "+generateKey);
		
		
        generateKey = new Gstr7TransHeaderDocKeyGenerator().generateOrgKey(doc);
		
		System.out.println("generateOrgKey : "+generateKey);
		
	}

}
