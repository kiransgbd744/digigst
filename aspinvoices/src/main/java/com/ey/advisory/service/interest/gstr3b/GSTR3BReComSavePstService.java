/**
 * 
 */
package com.ey.advisory.service.interest.gstr3b;

import org.javatuples.Pair;

/**
 * @author Siva.Reddy
 *
 */

public interface GSTR3BReComSavePstService {

	public Pair<String, String> gstr3bReComSavePstImp(String apiAction, String gstin,
			String retPeriod, String reqJson);

}
