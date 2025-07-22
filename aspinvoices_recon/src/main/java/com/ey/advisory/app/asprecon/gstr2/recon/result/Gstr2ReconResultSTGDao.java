package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.util.List;

import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2ReconResponseUploadEntity;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2ReconResultSTGDao {

	public List<Gstr2ReconResponseUploadEntity> getStgData(Long prId, Long a2Id,
			Long reconLinkId, String fmResponse, String taxPeriod3B,
			String action);
}
