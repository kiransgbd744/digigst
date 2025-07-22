package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2ReconResultDao {

	public Triplet<
    List<Gstr2ReconResultDto>,   
    List<ReconSummaryDto>,        
    Integer> getReconResult(
			Gstr2ReconResultReqDto reqDot, int pageNum, int pageSize);


}
