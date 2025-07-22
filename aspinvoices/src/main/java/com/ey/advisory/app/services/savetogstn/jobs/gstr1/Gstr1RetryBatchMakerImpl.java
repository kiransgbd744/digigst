/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1RetryBatchMakerImpl")
public class Gstr1RetryBatchMakerImpl implements Gstr1RetryBatchMaker {

	
	public List<SaveToGstnBatchRefIds> reTrySaveGstr1Data(List<Object[]> docs,
			Gstr1SaveBatchEntity batch){
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside method reTrySaveGstr1Data");
		}
		if (docs.size() == batch.getBatchSize()) {
			// batch persited json can be stored.
		} else {

			// form json again with the selected records.
		}
		
		return null;
	}
}
