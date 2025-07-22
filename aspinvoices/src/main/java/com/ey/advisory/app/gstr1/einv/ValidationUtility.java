package com.ey.advisory.app.gstr1.einv;

import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;

@Service
public class ValidationUtility {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	private static final String DOC_KEY_JOINER = "|";

	public static String createDocKey(String sGstin, String docType,
			String docNum, String finyear) {

		return new StringJoiner(DOC_KEY_JOINER).add(finyear).add(sGstin)
				.add(docType).add(docNum).toString();
	}
}
