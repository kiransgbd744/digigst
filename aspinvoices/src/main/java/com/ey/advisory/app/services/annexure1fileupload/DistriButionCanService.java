package com.ey.advisory.app.services.annexure1fileupload;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

/**
 * @author Siva.Nandam
 *
 */
public interface DistriButionCanService {

	Map<String, List<ProcessingResult>> DistriButionCanLookUp(List<Gstr6DistributionExcelEntity> strucProcessed, 
			ProcessingContext context);


}
