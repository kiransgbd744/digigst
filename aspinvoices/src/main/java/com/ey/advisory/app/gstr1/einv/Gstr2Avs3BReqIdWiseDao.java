package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr2a2bVs3BbReqIdWiseDataDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
/**
 * 
 * @author Kiran s
 *
 */
public interface Gstr2Avs3BReqIdWiseDao {

	List<Gstr2a2bVs3BbReqIdWiseDataDto> get2a2bvs3aReqIdWiseStatus(
			Gstr2InitiateReconReqDto reqDto, String userName);
	
/*	List<Gstr1EinvRequesIdWiseDownloadTabDto> getGstr6CredDistData(Long requestId);
*/
}
