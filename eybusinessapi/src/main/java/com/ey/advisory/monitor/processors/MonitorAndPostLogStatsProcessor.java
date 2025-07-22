package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.master.IdTokenGrpMapEntity;
import com.ey.advisory.app.data.repositories.master.IdTokenGrpMapRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ERPReqAggParamsDto;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.MonitorCommonUtility;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MonitorAndPostLogStatsProcessor")
public class MonitorAndPostLogStatsProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private IdTokenGrpMapRepository idtokenGrpMapRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	private MonitorCommonUtility monCommUtility;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {

			IdTokenGrpMapEntity idtokenGrpEntity = idtokenGrpMapRepo
					.findByGroupCode(group.getGroupCode());

			
			if (idtokenGrpEntity == null) {
				String msg = String.format(
						"Group is not configured in GrpMapping Table,"
								+ "Hence We are not Monitoring Einv Dashboard Summary Push",
						group.getGroupCode());
				LOGGER.error(msg);
				return;
			}

			monCommUtility.updateIdToken(idtokenGrpEntity, group.getGroupCode());

			JsonObject jobParams = new JsonObject();
			StoredProcedureQuery einvRecSummRespProc = entityManager
					.createNamedStoredProcedureQuery(
							"insertERPReqAggSummaryData");
			List<Object[]> resultList = einvRecSummRespProc.getResultList();
			List<ERPReqAggParamsDto> retList = resultList.stream()
					.map(o -> convert(o)).collect(Collectors.toList());
			if ("SUCCESS".equalsIgnoreCase(retList.get(0).getStatus())) {
				jobParams.addProperty("dateFromProc", retList.get(0).getFromDate());
				jobParams.addProperty("dateToProc", retList.get(0).getToDate());

				jobParams.addProperty("date", LocalDate.now().toString());
				LOGGER.debug(
						"insertERPReqAggSummaryData data loaded successfully "
								+ "for group {} for Date {}",
						group.getGroupCode(), retList.get(0).getFromDate(), retList.get(0).getToDate());
				asyncJobsService.createJob(group.getGroupCode(),
						"PushErpStatsToCloud", jobParams.toString(), "SYSTEM",
						1L, message.getId(), null);
			} else if ("NO DATA FOUND"
					.equalsIgnoreCase(retList.get(0).getStatus())) {
				LOGGER.debug(
						"No Data Found from Proc insertERPReqAggSummaryData for group {}",
						group.getGroupCode());
			} else {
				String msg = String.format(
						"Proc InsertERPReqAggSummaryData is failed for group %s",
						group.getGroupCode());
				LOGGER.error(msg);
				throw new AppException(msg);
			}
		} catch (Exception ex) {
			String msg = "Exception occured in MonitorAndSaveLogStats job of BcAPI";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}

	private ERPReqAggParamsDto convert(Object[] o) {
		ERPReqAggParamsDto dto = new ERPReqAggParamsDto();
		dto.setStatus(String.valueOf(o[0]));
		dto.setFromDate(String.valueOf(o[1]));
		dto.setToDate(String.valueOf(o[2]));

		return dto;
	}

}
