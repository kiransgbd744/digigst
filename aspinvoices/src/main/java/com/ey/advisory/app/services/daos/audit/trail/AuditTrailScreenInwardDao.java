/**
 * 
 */
package com.ey.advisory.app.services.daos.audit.trail;

import com.ey.advisory.app.docs.dto.AuditTrailScreenReqDto;
import com.ey.advisory.app.docs.dto.AuditTrailScreenSummaryRespDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface AuditTrailScreenInwardDao {

	AuditTrailScreenSummaryRespDto getAuditTrailInwardData(
			AuditTrailScreenReqDto request);

}
