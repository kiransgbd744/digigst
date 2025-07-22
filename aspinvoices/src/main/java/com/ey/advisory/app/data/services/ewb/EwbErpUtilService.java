/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBResponseDto;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.ExtendEWBResponseDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface EwbErpUtilService {
	public void updatePartBErpPush(UpdatePartBEwbRequestDto req,
			UpdatePartBEwbResponseDto resp);
	public void updateTransporterErpPush(UpdateEWBTransporterReqDto req,
			UpdateEWBTransporterRespDto resp);
	public void extendErpPush(ExtendEWBReqDto req,
			ExtendEWBResponseDto resp);
	public void consolidateEwbErpPush(ConsolidateEWBReqDto req,
			ConsolidateEWBResponseDto resp);
	public void generateEwbErpPush(EwayBillRequestDto req,
			EwbResponseDto resp);
	public void cancelEwbErpPush(CancelEwbReqDto req,
			CancelEwbResponseDto resp);

}
