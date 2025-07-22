package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2ReconResultService {

	public Triplet<
    List<Gstr2ReconResultDto>,   
    List<ReconSummaryDto>,        
    Integer> findReconResult(
			Gstr2ReconResultReqDto reqDto, int pageNum, int pageSize);

	public Triplet<
    List<Gstr2ReconResultDto>,    // the detailed rows
    List<ReconSummaryDto>,        // your summary rows
    Integer                       // the total count
> findReconResult2bpr(
			Gstr2ReconResultReqDto reqDto, int pageNum, int pageSize);

}
