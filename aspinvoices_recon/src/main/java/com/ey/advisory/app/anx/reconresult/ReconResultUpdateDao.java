/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface ReconResultUpdateDao {

	public int updateReconUserActionsInDB(String taxPeriod, String reportName,
			String userAction, 
			List<String> a2KeyList, List<String> prKeyList , 
			List<BigInteger> reconId, List<String> gstinsList);
}
