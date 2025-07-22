/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBResponseDto;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.ExtendEWBResponseDto;
import com.ey.advisory.ewb.dto.RejectEwbReqDto;
import com.ey.advisory.ewb.dto.RejectEwbResponseDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface EwbDbUtilService {
	
	public void updatePartBDbUpdate(UpdatePartBEwbRequestDto req,
			UpdatePartBEwbResponseDto resp);
	public void updateTransporterDbUpdate(UpdateEWBTransporterReqDto req,
			UpdateEWBTransporterRespDto resp);
	public void extendDbUpdate(ExtendEWBReqDto req,
			ExtendEWBResponseDto resp);
	public void consolidateEwbDbUpdate(ConsolidateEWBReqDto req,
			ConsolidateEWBResponseDto resp);
	public void generateEwbDbUpdate(OutwardTransDocument doc,
			EwayBillRequestDto nicReq, EwbResponseDto resp,
			String apiIdentifier);
	public void cancelEwbDbUpdate(CancelEwbReqDto req,
			CancelEwbResponseDto resp);

	public void rejectEwbDbUpdate(RejectEwbReqDto req,
			RejectEwbResponseDto resp);


}
