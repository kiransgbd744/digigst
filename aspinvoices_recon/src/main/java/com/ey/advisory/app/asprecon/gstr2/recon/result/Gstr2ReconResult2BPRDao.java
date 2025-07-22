package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.util.List;

import org.javatuples.Triplet;

public interface Gstr2ReconResult2BPRDao {

	public Triplet<
    List<Gstr2ReconResultDto>,   
    List<ReconSummaryDto>,        
    Integer> getReconResult2BPR(
			Gstr2ReconResultReqDto reqDot, int pageNum, int pageSize);

	public Integer getReconResult2BPRCount(Gstr2ReconResultReqDto reqDot);
}
