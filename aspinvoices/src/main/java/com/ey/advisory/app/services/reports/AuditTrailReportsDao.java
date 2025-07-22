/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.AuditTrailReportsReqDto;

/**
 * @author Mahesh.Golla
 *
 */
public interface AuditTrailReportsDao {

	List<Object> getAuditTrailReports(AuditTrailReportsReqDto request);

}
