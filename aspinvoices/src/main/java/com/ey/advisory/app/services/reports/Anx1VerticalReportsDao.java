/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.Anx1VerticalDownloadReportsReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Anx1VerticalReportsDao {

	List<Object> getVerticalReports(
			Anx1VerticalDownloadReportsReqDto request);

}
