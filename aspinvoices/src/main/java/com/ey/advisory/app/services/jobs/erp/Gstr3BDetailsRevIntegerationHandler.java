package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.docs.dto.erp.Gstr3BDashboardSumOuterDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3BDashboardSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3BDetailRespOuterDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3BDetailResponseDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3BPosDetailRespOuterDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3BPosDetailResponseDto;
import com.ey.advisory.app.docs.dto.erp.Gstrn3BFinalResultDto;
import com.ey.advisory.app.gstr3b.Gstr3BEntityDashboardDto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr3BDetailsRevIntegerationHandler")
public class Gstr3BDetailsRevIntegerationHandler {

	@Autowired
	@Qualifier("Gstr3BDetailsDocsImpl")
	private GSTR3BDetailsDocs gstr3BDocsImpl;

	@Autowired
	@Qualifier("AnxErpBatchRepository")
	private AnxErpBatchRepository batchRepo;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepo;
	
	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepo; 
	
	Integer respcode = 0;

	public Integer gstr3bDetailsToErp(RevIntegrationScenarioTriggerDto erpDto)
	{

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr3B Details Reverse Integeration ToErp method called "
							+ "with reqDto {} ",
					erpDto);
		}
		try {
			String destinationName = erpDto.getDestinationName();
			Long scenarioId = erpDto.getScenarioId();
			String groupcode = erpDto.getGroupcode();
			String gstin = erpDto.getGstin();
			Long entityId = erpDto.getEntityId();
			TenantContext.setTenantId(groupcode);
			List<Gstr3BDetailResponseDto> gstr3BDetails = new
					ArrayList<Gstr3BDetailResponseDto>();
			List<Gstr3BPosDetailResponseDto> gstr3BPosDetails = new
					ArrayList<Gstr3BPosDetailResponseDto>();
			List<Gstr3BEntityDashboardDto> gstr3bDashboardEntity = new
					ArrayList<Gstr3BEntityDashboardDto>();
			List<Gstr3BDashboardSummaryDto> gstr3bSummaryDto = new
					ArrayList<Gstr3BDashboardSummaryDto>();
			Gstrn3BFinalResultDto resultDto = new Gstrn3BFinalResultDto();
			Gstr3BDetailRespOuterDto respDetOuter = new Gstr3BDetailRespOuterDto();
			Gstr3BPosDetailRespOuterDto respPoOuter = new Gstr3BPosDetailRespOuterDto();
			Gstr3BDashboardSumOuterDto respDashboard = new Gstr3BDashboardSumOuterDto();

			
			String entityName = entityInfoRepo
					.findEntityNameByEntityId(entityId);
			
			String companyCode = entityInfoRepo
					.findCompanyCodeByEntityId(entityId); 
			
			// fetching all processed + not sent to gstn active docs
			// List<Object[]> objs = docRepo
			// .approvalReqDocsForRevIntegration(returnPeriod, gstin);
			List<Object[]> objs = null;
			objs = gstr3BDocsImpl.get3BDetailsData(gstin);
			
			if (objs.isEmpty() || objs == null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"No 3B Details Found for This gstin " + ": %s",
							gstin);
					LOGGER.debug(msg);
				}
			}

			if (!objs.isEmpty() && objs != null) {
				gstr3BDetails = gstr3BDocsImpl.
						convertDetailsDocsAsDtos(objs, entityName, companyCode);
			}

			if (gstr3BDetails != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("No of Records that are to be "
					 + "pushed to Erp for Gstr3B for Details with Gstin Data"
					 + " for  Pos Section is %d for " + " %s Gstin",
							gstr3BDetails.size(), gstin);
					LOGGER.debug(msg);
				}
				//resultDto.setGstDetailResp(gstr3BDetails);
				respDetOuter.setGstDetailResp(gstr3BDetails);
			}
			

			if (!objs.isEmpty() && objs != null) {
				gstr3BPosDetails = gstr3BDocsImpl
						.convertPosDetails(objs, entityName, companyCode);
			}
			
			if (!gstr3BPosDetails.isEmpty() && gstr3BPosDetails != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
					"No of Records that are to be "
					+ "pushed to Erp for Gstr3B Pos Details with Gstin Data"
					+ " for  Pos Section 3.2 and 5  is %d for "
					+ " %s Gstin",
							gstr3BPosDetails.size(), gstin);
					LOGGER.debug(msg);
				}
				respPoOuter.setGstPosDetails(gstr3BPosDetails);
			}

			gstr3bDashboardEntity = gstr3BDocsImpl.get3BSummarisedData(gstin,null);

		    if (!gstr3bDashboardEntity.isEmpty()
					|| gstr3bDashboardEntity != null) {
				gstr3bSummaryDto = gstr3BDocsImpl
			     .convertSummarisedDocsAsDtos(gstr3bDashboardEntity, entityName, companyCode);
			}

			if (!gstr3bSummaryDto.isEmpty()
					|| gstr3bSummaryDto != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
					" %d of Records that are to be "
					+ "pushed to Erp for Gstr3B Gstin : %s  ",
					gstr3bSummaryDto.size(), gstin);
					LOGGER.debug(msg);
				}
				respDashboard.setGstDashBoard(gstr3bSummaryDto);

			}
			resultDto.setGstrDashboardDto(respDashboard);
			resultDto.setGstrRespDetDto(respDetOuter);
			resultDto.setGstrPosDetail(respPoOuter);
			int currentBatchSize = gstr3BDetails.size()
					+ gstr3BPosDetails.size()
					+ gstr3bSummaryDto.size();
			long totSize = (long)currentBatchSize;
			AnxErpBatchEntity batch = setErpBatch(groupcode, entityId, gstin,
					scenarioId, destinationName, totSize);
			// Erp Batch Id forming
			batch = batchRepo.save(batch);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Batch is created as {} ", batch);
			}
			/*
			 * //sgstin and return period are same through out the result set so
			 * we can consider first row. //sgstin and returnperiod are there as
			 * part of Query where clause. Object[] obj = objs.get(0); String
			 * retPeriod = String.valueOf(obj[0]); String sgstin =
			 * String.valueOf(obj[1]);
			 */

			// update selected invoices with batch id
			docRepo.updateDocsWithErpWorkflowBatchId(null, gstin,
					batch.getId());
			// Push Payload to erp
			respcode = gstr3BDocsImpl.pushToErp(resultDto, destinationName, batch);
			respcode = respcode != null ? respcode : 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ",
						destinationName, respcode);
			}
			/*if (respcode == 200) {
				batch.setSuccess(true);
				batch.setStatus(APIConstants.SUCCESS);
			} else {
				batch.setSuccess(false);
				batch.setStatus(APIConstants.FAILED);
			}
			// Erp Batch updation
			batchRepo.save(batch);*/
			// });
			return respcode;

		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			return null;
		}
	}

	private AnxErpBatchEntity setErpBatch(String groupcode, Long entityId,
			String gstin, Long size, String destinationName, Long scenarioId) {
		AnxErpBatchEntity batch = new AnxErpBatchEntity();
		batch.setGroupcode(groupcode);
		batch.setEntityId(entityId);
		batch.setDestinationName(destinationName);
		batch.setScenarioId(scenarioId);
		batch.setGstin(gstin);
		batch.setBatchSize(size);
		batch.setHttpStatus(APIConstants.INITIATED);
		batch.setCreatedBy(APIConstants.SYSTEM);
		batch.setCreatedOn(LocalDateTime.now());
		batch.setDelete(false);
		return batch;
	}
}
