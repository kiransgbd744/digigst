/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceClientEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceRequestEntity;
import com.ey.advisory.app.services.vendorcomm.ReturnComplianceEmailCommDto;
import com.ey.advisory.app.services.vendorcomm.ReturnComplianceRequestDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface ReturnComplianceSummaryService {

	List<ReturnComplianceRequestEntity> getComplianceDataByUserName(
			String userName);

	List<ReturnComplianceRequestDto> getComplianceCommResponse(
			List<ReturnComplianceRequestEntity> complianceComReqList);

	public List<ReturnComplianceClientEntity> getComplianceCgstinData(
			Long requestId);

	Pair<List<ReturnComplianceEmailCommDto>, Integer> getComReturnEmailCommunicationDetails(
			Long requestId, Long entityId, int pageSize, int pageNum);
}
