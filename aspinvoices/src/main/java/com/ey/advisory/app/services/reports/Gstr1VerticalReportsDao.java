/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Gstr1VerticalReportsDao {

	List<Object> getGstr1VerticalReports(
			Gstr1VerticalDownloadReportsReqDto request);

}
