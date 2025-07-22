/**
 * 
 */
package com.ey.advisory.app.services.daos.audit.trail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.AuditTrailScreenReqDto;
import com.ey.advisory.app.docs.dto.AuditTrailScreenSummaryRespDto;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("AuditTrailScreenServiceImpl")
public class AuditTrailScreenServiceImpl implements AuditTrailScreenService {

	@Autowired
	@Qualifier("AuditTrailScreenOutwardDaoImpl")
	private AuditTrailScreenOutwardDao auditTrailScreenOutwardDao;

	@Autowired
	@Qualifier("AuditTrailScreenInwardDaoImpl")
	private AuditTrailScreenInwardDao auditTrailScreenInwardDao;

	@Override
	public AuditTrailScreenSummaryRespDto getAuditTrailOutwardResp(
			AuditTrailScreenReqDto request) {

		AuditTrailScreenSummaryRespDto response = auditTrailScreenOutwardDao
				.getAuditTrailOutwardData(request);
		return response;
	}

	@Override
	public AuditTrailScreenSummaryRespDto getAuditTrailInwardResp(
			AuditTrailScreenReqDto request) {
		AuditTrailScreenSummaryRespDto response = auditTrailScreenInwardDao
				.getAuditTrailInwardData(request);
		return response;
	}

}
