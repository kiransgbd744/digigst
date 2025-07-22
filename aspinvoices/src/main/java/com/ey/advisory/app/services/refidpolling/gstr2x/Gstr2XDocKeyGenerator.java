package com.ey.advisory.app.services.refidpolling.gstr2x;

import static com.ey.advisory.common.GSTConstants.WEB_UPLOAD_KEY;

import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr2XProcessedTcsTdsEntity;
import com.ey.advisory.app.services.docs.DocKeyGenerator;

/**
 * 
 * @author SriBhavya
 *
 */
@Component("Gstr2XDocKeyGenerator")
public class Gstr2XDocKeyGenerator implements DocKeyGenerator<Gstr2XProcessedTcsTdsEntity, String>{
	
	/*public String genearateKey(String category, String taxGstin,
			String retPeriod, String deGstin, String monthOfDedUpl,
			String orgMonthOfdeductorOrCollectorUpl) {
		return new StringJoiner(WEB_UPLOAD_KEY).add(category).add(taxGstin)
				.add(retPeriod).add(deGstin).add(monthOfDedUpl)
				.add(orgMonthOfdeductorOrCollectorUpl).toString();
	}*/

	@Override
	public String generateKey(Gstr2XProcessedTcsTdsEntity doc) {
		return new StringJoiner(WEB_UPLOAD_KEY).add(doc.getRecordType()).add(doc.getGstin())
				.add(doc.getRetPeriod()).add(doc.getCtin()).add(doc.getDeductorUplMonth())
				.add(doc.getOrgDeductorUplMonth()).toString();
	}

	@Override
	public String generateOrgKey(Gstr2XProcessedTcsTdsEntity doc) {
		// TODO Auto-generated method stub
		return null;
	}

}
