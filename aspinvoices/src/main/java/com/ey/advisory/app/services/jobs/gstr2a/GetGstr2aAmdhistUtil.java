/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr2a;

import java.time.LocalDate;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("GetGstr2aAmdhistUtil")
public class GetGstr2aAmdhistUtil {

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private static final String DOC_KEY_JOINER = "|";
	
	public String generateKeyAmdhist(String sgtin, String portCode, Long billNum,
			LocalDate billCreated) {
		String formattedDocDate = (billCreated != null)
				? billCreated.format(DateUtil.SUPPORTED_DATE_FORMAT2) : "";
		return new StringJoiner(DOC_KEY_JOINER).add(sgtin).add(portCode)
				.add(String.valueOf(billNum)).add(formattedDocDate).toString();
	}

	public String generateImpgKey(String sgtin, String portCode,
			LocalDate billCreated, Long billNum) {
		String formattedDocDate = (billCreated != null)
				? billCreated.format(DateUtil.SUPPORTED_DATE_FORMAT2) : "";
		return new StringJoiner(DOC_KEY_JOINER).add(sgtin).add(portCode)
				.add(formattedDocDate).add(String.valueOf(billNum)).toString();
	}

	public String generateImpgSezKey(String ctin, String sgtin, String portCode,
			LocalDate billCreated, Long billNum) {
		String formattedDocDate = (billCreated != null)
				? billCreated.format(DateUtil.SUPPORTED_DATE_FORMAT2) : "";
		return new StringJoiner(DOC_KEY_JOINER).add(ctin).add(sgtin)
				.add(portCode).add(formattedDocDate)
				.add(String.valueOf(billNum)).toString();
	}

	public String createAsyncjob(String uniqueKey, String parentSection,
			Gstr1GetInvoicesReqDto dto) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		String tempGetType = dto.getType();
		String[] splitData = uniqueKey.split("\\".concat(DOC_KEY_JOINER));
		dto.setType(APIConstants.AMDHIST.toUpperCase());
		if (APIConstants.IMPG.toUpperCase()
				.equalsIgnoreCase(parentSection.toUpperCase())) {
			dto.setPortCode(splitData[1]);
			dto.setBeDate(splitData[2]);
			dto.setBeNum(
					splitData[3] != null ? Long.parseLong(splitData[3]) : null);
		} else if (APIConstants.IMPGSEZ.toUpperCase()
				.equalsIgnoreCase(parentSection.toUpperCase())) {
			dto.setPortCode(splitData[2]);
			dto.setBeDate(splitData[3]);
			dto.setBeNum(
					splitData[4] != null ? Long.parseLong(splitData[4]) : null);
		}
		
		dto.setParentSection(parentSection.toUpperCase());

		dto = createBatchAndSave(dto.getGroupcode(), dto);
		
		//String tempReturnPeriod = dto.getReturnPeriod();
		//Passing Null value to AMDHIST GET
		//dto.setReturnPeriod(null);
		String jsonParam = gson.toJson(dto);
		AsyncExecJob job = asyncJobsService.createJob(dto.getGroupcode(),
				JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
				APIConstants.SYSTEM, JobConstants.PRIORITY,
				JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);
		//Setting the value back to dto
		//dto.setReturnPeriod(tempReturnPeriod);
		dto.setType(tempGetType);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Job Created for Gstr2A Get as {} ", job);
		}
		return job.toString();
	}

	private Gstr1GetInvoicesReqDto createBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {

		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		String beDate = dto.getBeDate();
		LocalDate stringToDate = null;
		if (beDate != null) {
			LocalDate locDate = LocalDate.parse(beDate,
					DateUtil.SUPPORTED_DATE_FORMAT2);
			beDate = locDate.format(DateUtil.SUPPORTED_DATE_FORMAT1);
			stringToDate = DateUtil.tryConvertUsingFormat(beDate,
					DateUtil.SUPPORTED_DATE_FORMAT1);
		}
		batchRepo.softlyDeleteByPortCodeAndBillOfDetails(
				dto.getType().toUpperCase(), APIConstants.GSTR2A.toUpperCase(),
				dto.getGstin(), dto.getPortCode(), dto.getBeNum(),
				stringToDate);
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GSTR2A.toUpperCase());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

}
