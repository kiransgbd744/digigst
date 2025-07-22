package com.ey.advisory.app.gstr3b;

import com.ey.advisory.common.AppException;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3bGenerate3BService {

	String getGstr3bGenerateList(String taxPeriod, String gstin, Long entityId)
			throws AppException;

}
