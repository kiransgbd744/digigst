/**
 * @author kiran s
 
 
 */
package com.ey.advisory.gl.master.upload.processors;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GlReconReportConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository;
import com.ey.advisory.app.services.gl.masterFile.uploads.GLProcessedReconFileServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.TraverserFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GLProcessedReconFileProcessor")
public class GLProcessedReconFileProcessor implements TaskProcessor {


	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("GLProcessedReconFileServiceImpl")
	private GLProcessedReconFileServiceImpl gLProcessedReconFileServiceImpl;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GLReconReportConfigRepository")
	private GLReconReportConfigRepository glReconReportConfig;

	@Override
	public void execute(Message message, AppExecContext context) {
		LOGGER.debug("Begin GLProcessedReconFileProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		Document document = null;

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GLInitiateReconProcessor Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		
		
		JsonElement element = JsonParser.parseString(jsonString);
		JsonObject json = element.getAsJsonObject();
		Long configId = json.get("reconId").getAsLong();
		if (LOGGER.isDebugEnabled()) {
		    String msg = String.format("Recon configId received: %d", configId);
		    LOGGER.debug(msg);
		}
		try {
			Optional<GlReconReportConfigEntity> reconConfigEntity = glReconReportConfig
					.findById(configId);
			
			if (LOGGER.isDebugEnabled()) {
			    if (reconConfigEntity.isPresent()) {
			        String msg = String.format("Recon Config Entity found for configId %d: %s", 
			                                   configId, reconConfigEntity.get().toString());
			        LOGGER.debug(msg);
			    } else {
			        String msg = String.format("No Recon Config Entity found for configId %d", configId);
			        LOGGER.debug(msg);
			    }
			}
			String status = gLProcessedReconFileServiceImpl
					.ReadProcessedFileAndPersist(configId, reconConfigEntity);
			if("Success".equalsIgnoreCase(status))
			{
			glReconReportConfig.updateReconConfigStatusAndReportName(
					"REPORT_GENERATED", null, null, LocalDateTime.now(),
					configId);
			}

		}

		catch (Exception e) {
			glReconReportConfig.updateReconConfigStatusAndReportName(
					"REPORT_GENERATION_FAILED", null, null, LocalDateTime.now(),
					configId);

			String msg = "Exception while calling downloadVendorJsonFiles ";
			LOGGER.error(msg, e);

			throw new AppException(e, msg);
		}

	}

}
