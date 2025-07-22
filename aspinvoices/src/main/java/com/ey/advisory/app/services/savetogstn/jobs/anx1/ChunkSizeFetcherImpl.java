/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

/**
 * @author Hemasundar.J
 *
 */
@Service("ChunkSizeFetcherImpl")
public class ChunkSizeFetcherImpl implements ChunkSizeFetcher {

	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Override
	public int getSize() {
		Config config = configManager.getConfig("GSTNAPI",
				"api.gstn.id(CHUNK_SIZE).save_to_gstn");
		return Integer.parseInt(config != null ? config.getValue() : "2000");
	}

	public Long getDelayOf() {
		Config config = configManager.getConfig("GSTNAPI",
				"api.gstn.id(DEPENDENT_SAVE_DELAY_RETRY).save_to_gstn");
		return Long.parseLong(config != null ? config.getValue() : "30");
	}

}
