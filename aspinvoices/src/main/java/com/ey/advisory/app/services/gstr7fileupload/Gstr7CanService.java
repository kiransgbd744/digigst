package com.ey.advisory.app.services.gstr7fileupload;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

/**
 * @author Siva.Nandam
 *
 */
public interface Gstr7CanService {
	
	Map<String, List<ProcessingResult>> DistriButionCanLookUp(List<Gstr7AsEnteredTdsEntity> strucProcessed, ProcessingContext context);
	

}
