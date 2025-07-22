package com.ey.advisory.app.asprecon.reconresponse;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultReqDto;

/**
 * @author sakshi.jain
 *
 */
public interface Gstr2APRReconResponseDao {

	public Pair<List<Gstr2ReconResponseDashboardDto>,Integer> getReconResponseData(
			Gstr2ReconResultReqDto reqDot, int pageNum, int pageSize);


}
