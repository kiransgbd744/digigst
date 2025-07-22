/**
 * 
 */
package com.ey.advisory.app.service.upload.way3recon;

import java.util.List;

import com.ey.advisory.ewb.dto.GetEwbResponseDto;

/**
 * @author Siva.Reddy
 *
 */
public interface Ewb3WayGetAPIService {

	public void prepareGetEwayBillData(List<GetEwbResponseDto> respList);

}
