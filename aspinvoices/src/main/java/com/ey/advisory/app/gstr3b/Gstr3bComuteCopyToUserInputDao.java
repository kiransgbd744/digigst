package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.ey.advisory.common.AppException;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3bComuteCopyToUserInputDao {

	public List<Gstr3BGstinAspUserInputDto> gstr3bCopyData(String taxPeriod,
			String gstin) throws AppException;
}
