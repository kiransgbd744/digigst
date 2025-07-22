package com.ey.advisory.app.services.credit.reversal;

import com.ey.advisory.app.gstr3b.Gstr3bRatioUserInputDto;

/**
 * @author ashutosh.kar
 *
 */
public interface CreditRevForUserInputService {

	public void saveCredRevUserInputSummary(Gstr3bRatioUserInputDto reqDto);

	public void moveToCredRevUserInputSummary(Gstr3bRatioUserInputDto reqDto);
}
