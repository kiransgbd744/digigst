/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestDataHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusResultDto;

/**
 * @author Hemasundar.J
 *
 */
public interface AnxDataStatusDocsCount {

	public AnxDataStatusRequestDataHeaderDto convertDocsAsDtos(
			List<AnxDataStatusResultDto> results, String dataType);

	public Integer pushToErp(AnxDataStatusRequestHeaderDto dto,
			String destinationName, AnxErpBatchEntity batch);

}
