package com.ey.advisory.processors.test;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.dms.DmsExecuteSequenceApiServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("DMSFileUploadProcessor")
public class DMSFileUploadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("DmsExecuteSequenceApiServiceImpl")
	DmsExecuteSequenceApiServiceImpl execService;
	
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;
	
	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository glReconFileStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		String groupCode = TenantContext.getTenantId();
		
		try {
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Long fileId = json.get("fileId").getAsLong();

			String jobCateg = json.get("jobCateg").getAsString();

			String fileName=json.get("fileName").getAsString();
			
			String uuid = "";
			String ruleId = null;
			String ruleName = "";
			
			if(jobCateg.equalsIgnoreCase(JobConstants.GLRECON_DUMP_FILEUPLOAD)) {
				Optional<GlReconFileStatusEntity> fileStatus1 = glReconFileStatusRepository.findById(fileId);
				uuid = fileStatus1.get().getPayloadId();
				ruleId = fileStatus1.get().getTransformationRule();
				ruleName = fileStatus1.get().getTransformationRuleName();
			} else {
				Optional<Gstr1FileStatusEntity> fileStatus = gstr1FileStatusRepository.findById(fileId);
				uuid = fileStatus.get().getUuid();
				ruleId = fileStatus.get().getTransformationRule();
				ruleName = fileStatus.get().getTransformationRuleName();
			}	 
			
			Long ruleIdLong = ruleId != null ? Long.valueOf(ruleId) : null;
			
			String resp = execService.getExecSequenceApi(groupCode, ruleIdLong, uuid, fileId, fileName, jobCateg,
					ruleName);
			
			LOGGER.debug("DMSFileUploadProcessor called sucessfully Resp {}", resp);

		} catch (Exception e) {
			String errMsg = "Error Occured in DMSFileUploadProcessor";
			LOGGER.error(errMsg, e);

			throw new AppException(errMsg, e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}

	}

}
