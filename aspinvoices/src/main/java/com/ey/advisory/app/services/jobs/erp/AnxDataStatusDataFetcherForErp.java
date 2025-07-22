
package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.app.docs.dto.erp.AnxDataStatusReqDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestDataHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestDataSummaryHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusResultDto;

/**
 * @author Sasidhar Reddy
 *
 */
public interface AnxDataStatusDataFetcherForErp {

	public List<Object[]> find(AnxDataStatusReqDto req);

	public List<AnxDataStatusResultDto> convertGstinWise(String entityPan,
			String entityName, List<Object[]> arrs, String companyCode);

	/**
	 * @param req
	 * @return
	 */
	List<Object[]> findDataStatusApiSummary(AnxDataStatusReqDto req);

	/**
	 * @param entityPan
	 * @param entityName
	 * @param outwordSummary
	 * @param companyCode
	 * @return
	 */
	public AnxDataStatusRequestDataSummaryHeaderDto convertDataToOutwarSummary(
			String entityPan, String entityName, List<Object[]> outwordSummary,
			Long enityId, String companyCode);

	/**
	 * @param entityPan
	 * @param entityName
	 * @param arrs
	 * @return
	 */
	public AnxDataStatusRequestDataSummaryHeaderDto convertDataToInwardDataSummary(
			String entityPan, String entityName, List<Object[]> arrs,
			Long entityId, String companyCode);

	/**
	 * @param itemDto
	 * @param entityId
	 * @return
	 */
	public AnxDataStatusRequestDataSummaryHeaderDto calculateDataByDocTypeAndReturnPeiod(
			AnxDataStatusRequestDataSummaryHeaderDto itemDto, Long entityId);

	/**
	 * @param results
	 * @param dataType
	 * @return
	 */
	public AnxDataStatusRequestDataHeaderDto convertDataToInward(
			List<AnxDataStatusResultDto> results, String dataType);

}