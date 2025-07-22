/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import org.springframework.stereotype.Component;

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
@Component("EwbErpUtilServiceImpl")
public class EwbErpUtilServiceImpl implements EwbErpUtilService{

	@Override
	public void updatePartBErpPush(UpdatePartBEwbRequestDto req,
			UpdatePartBEwbResponseDto resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTransporterErpPush(UpdateEWBTransporterReqDto req,
			UpdateEWBTransporterRespDto resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extendErpPush(ExtendEWBReqDto req, ExtendEWBResponseDto resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void consolidateEwbErpPush(ConsolidateEWBReqDto req,
			ConsolidateEWBResponseDto resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateEwbErpPush(EwayBillRequestDto req,
			EwbResponseDto resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelEwbErpPush(CancelEwbReqDto req,
			CancelEwbResponseDto resp) {
		// TODO Auto-generated method stub
		
	}

}
