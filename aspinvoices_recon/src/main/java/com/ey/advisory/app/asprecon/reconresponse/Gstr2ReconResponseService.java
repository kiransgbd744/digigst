package com.ey.advisory.app.asprecon.reconresponse;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultReqDto;

/**
 * @author sakshi.jain
 *
 */
public interface Gstr2ReconResponseService {

	public Pair<List<Gstr2ReconResponseDashboardDto>,Integer> getReconResponseDashboardData(
			Gstr2ReconResultReqDto reqDot, int pageNum, int pageSize);

	public String getLastReconTimeStamp(Long entityId, String ReconType);
	
	public List<Gstr2ReconResponseButtonReqDto> validations(Gstr2ReconResponseButtonReqDto reqDto);
	
	public List<Gstr2ReconResponseButtonReqDto> reconResponseLockProc(Gstr2ReconResponseButtonReqDto reqDto, Long id, Long fileId);
}
