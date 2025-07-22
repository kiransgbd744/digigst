/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.docs.dto.erp.OutwardErrorDocsDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1AspErrorDocs {

	public List<Pair<OutwardErrorDocsDto, List<Long>>> convertDocsAsDtosByChunking(
			List<Object[]> objs, String accountNum, 
			String entityName, String entityPan);

	/*public Integer pushToErp(OutwardErrorDocsDto dto, String destination,
			String dataType, AnxErpBatchEntity batch);*/

}
