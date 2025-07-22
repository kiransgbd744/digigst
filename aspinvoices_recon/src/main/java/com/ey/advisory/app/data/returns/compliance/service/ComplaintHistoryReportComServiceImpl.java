/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceClientEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceRequestEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnComplainceClientRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnComplianceRequestRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 */

@Slf4j
@Component("ComplaintHistoryReportComServiceImpl")
public class ComplaintHistoryReportComServiceImpl
		implements ComplianceHistoryReportComService {

	@Autowired
	private ReturnComplianceRequestRepository returnComplianceRequestRepository;

	@Autowired
	private ReturnComplainceClientRepository returnComplainceClientRepository;

	@Override
	public Long createEntryComplainceComReq(Long gstinList,
			String financialYear, Long entityId) {
		String status = "SUBMITTED";
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		ReturnComplianceRequestEntity returnComplianceRequestEntity = new ReturnComplianceRequestEntity();
		if (gstinList == 0) {
			status = ReportStatusConstants.NO_DATA_FOUND;
		}

		try {
			returnComplianceRequestEntity.setNoOfGstins(gstinList);
			returnComplianceRequestEntity.setFinancialYear(financialYear);
			returnComplianceRequestEntity.setStatus(status);
			returnComplianceRequestEntity.setCreatedOn(LocalDateTime.now());
			returnComplianceRequestEntity.setCreatedBy(userName);
			returnComplianceRequestEntity.setUpdatedOn(LocalDateTime.now());
			returnComplianceRequestEntity.setUpdatedBy(userName);
			returnComplianceRequestEntity.setEntityId(entityId);
			returnComplianceRequestRepository
					.save(returnComplianceRequestEntity);
			return returnComplianceRequestEntity.getRequestId();
		} catch (Exception e) {
			LOGGER.error("Exception while Persisting complaince Comm Request ",
					e);
			throw new AppException(e);
		}
	}

	@Override
	public void createEntryCompClientGstin(Long requestId, String Gstin,
			String returnType) {
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		ReturnComplianceClientEntity returnComplianceClientEntity = new ReturnComplianceClientEntity();
		try {

			returnComplianceClientEntity.setRequestId(requestId);
			returnComplianceClientEntity.setClientGstin(Gstin);
			returnComplianceClientEntity.setCreatedOn(LocalDateTime.now());
			returnComplianceClientEntity.setCreatedBy(userName);
			returnComplianceClientEntity.setUpdatedOn(LocalDateTime.now());
			returnComplianceClientEntity.setUpdatedBy(userName);
			returnComplianceClientEntity.setReturnType(returnType);
			returnComplianceClientEntity.setEmailStatus("DRAFTED");
			returnComplianceClientEntity.setReportStatus("SUBMITTED");
			returnComplainceClientRepository.save(returnComplianceClientEntity);
		} catch (Exception e) {
			LOGGER.error("Exception while Persisting complainceclientGstin ",
					e);
			new AppException(e);

		}

	}

}
