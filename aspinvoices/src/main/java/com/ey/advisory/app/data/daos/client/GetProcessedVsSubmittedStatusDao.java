/**
 * 
 */
package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.core.dto.ProcessedVsSubmittedRequestDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface GetProcessedVsSubmittedStatusDao {

	public List<Object[]> findDataByCriteria(
			ProcessedVsSubmittedRequestDto criteria, List<String> gstinList);

}
