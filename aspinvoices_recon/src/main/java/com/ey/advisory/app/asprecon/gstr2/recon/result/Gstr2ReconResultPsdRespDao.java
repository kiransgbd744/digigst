package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.Gstr2ReconResultRespPsdEntity;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2ReconResultPsdRespDao {

	public List<Gstr2ReconResultRespPsdEntity> getPsdData(Long prId, Long a2Id,
			Long reconLinkId,String fmResponse, String taxPeriod3B, Long stgID);
}
