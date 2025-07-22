package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
/**
 * 
 * @author Kiran s
 *
 */
public interface Gstr6CredDistDao {

	List<Gstr6ComputeCredDistDataDto> getGstr6CredReqIdWiseStatus(
			Gstr2InitiateReconReqDto reqDto, String userName);
	
	List<Gstr1EinvRequesIdWiseDownloadTabDto> getGstr6CredDistData(Long requestId);

}
