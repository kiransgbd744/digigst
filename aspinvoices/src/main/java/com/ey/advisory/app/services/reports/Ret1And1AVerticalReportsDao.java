/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.Anx1VerticalDownloadReportsReqDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Ret1And1AVerticalReportsDao {

	List<Object> getRet1VerticalReports(
			Anx1VerticalDownloadReportsReqDto request);

}
