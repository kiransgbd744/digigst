/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.einvoice.ConsolidatedEWBResponse;
import com.ey.advisory.core.dto.ConsolidatedEWBRequest;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("ConsolidateEWBService")
public class ConsolidateEWBService {

	@Autowired
	@Qualifier("ConsolidateEWBDaoImpl")
	private ConsolidateEWBDao consolidateEWBDao;

	public List<ConsolidatedEWBResponse> getCewbDetails(
			ConsolidatedEWBRequest cewbRequest) {

		List<ConsolidatedEWBResponse> resp = consolidateEWBDao
				.getCEWBDetails(cewbRequest);

		return resp;
	}
}
