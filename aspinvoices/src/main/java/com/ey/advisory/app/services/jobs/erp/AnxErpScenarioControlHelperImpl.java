/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDateTime;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpScenarioControlEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpScenarioControlRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("AnxErpScenarioControlHelperImpl")
public class AnxErpScenarioControlHelperImpl implements AnxErpScenarioControlHelper{

	
	@Autowired
	private AnxErpScenarioControlRepository erpScenarioControlRepo;
	
	@Autowired
	private DocRepository outDocRepo;
	
	@Autowired
	private InwardTransDocRepository inDocRepo;	
	
	/*@Autowired
	private ErpScenarioPermissionRepository scenPermiRepo;*/
	
	@Autowired
	private GSTNDetailRepository gstnRepo;
	
	@Override
	public Pair<Long, Long> getRange(String gstin, Long scenarioId,
			 OutwardTransDocument out) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ERP scenario outward Range(MinId and MaxId) selection"
							+ " started for gstin {} and scenarioId",
					gstin, scenarioId);
		}
		GSTNDetailEntity gstinInfo = gstnRepo
				.findByGstinAndIsDeleteFalse(gstin);
		if (gstinInfo == null) {
			LOGGER.error("GSTIN {} is not onboarded.", gstin);
			return null;
		}
		/*ErpScenarioPermissionEntity scenPermission = scenPermiRepo
				.findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId,
						gstinInfo.getId());

		if (scenPermission == null) {
			LOGGER.error("scenarioId {} is not onboarded for the GSTIN {} ",
					scenarioId, gstin);
			return null;
		}*/
		
		Long previousOutMaxId = erpScenarioControlRepo
				.findLastScenarioMaxIdForOutward(gstin, scenarioId);

		Long maxId = outDocRepo.findMaxIdBySgstinAndIsDeletedFalse(gstin);

		if(previousOutMaxId !=null && previousOutMaxId.compareTo(maxId) == 0L) {
			return null;
		}
		Long minId = previousOutMaxId == null ? 0L : previousOutMaxId + 1;
		maxId = maxId == null ? 0L : maxId;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ERP scenario outward Range is {} and {} ", minId,
					maxId);
		}
		AnxErpScenarioControlEntity entity = new AnxErpScenarioControlEntity();
		entity.setGstin(gstin);
		entity.setScenarioId(scenarioId);
		//entity.setDestination(destination);
		entity.setStatus(APIConstants.INITIATED);
		entity.setMinIdOut(minId);
		entity.setMaxIdOut(maxId);
		entity.setCreatedBy(APIConstants.SYSTEM);
		entity.setCreatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		erpScenarioControlRepo.save(entity);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"New record inserted in control table with above range");
		}
		return new Pair<>(minId, maxId);
	}

	@Override
	public Pair<Long, Long> getRange(String gstin, Long scenarioId,
			InwardTransDocument out) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ERP scenario inward Range(MinId and MaxId) selection"
							+ " started for gstin {} and scenarioId",
					gstin, scenarioId);
		}
		GSTNDetailEntity gstinInfo = gstnRepo
				.findByGstinAndIsDeleteFalse(gstin);
		if (gstinInfo == null) {
			LOGGER.error("GSTIN {} is not onboarded.", gstin);
			return null;
		}
		/*ErpScenarioPermissionEntity scenPermission = scenPermiRepo
				.findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId,
						gstinInfo.getId());

		if (scenPermission == null) {
			LOGGER.error("scenarioId {} is not onboarded for the GSTIN {} ",
					scenarioId, gstin);
			return null;
		}*/
		Long previousInMaxId = erpScenarioControlRepo
				.findLastScenarioMaxIdForInward(gstin, scenarioId);

		Long maxId = inDocRepo.findMaxIdByCgstinAndIsDeletedFalse(gstin);
		if (previousInMaxId != null && previousInMaxId.compareTo(maxId) == 0L) {
			return null;
		}
		Long minId = previousInMaxId == null ? 0L : previousInMaxId + 1;
		maxId = maxId == null ? 0L : maxId;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ERP scenario outward Range is {} and {} ", minId,
					maxId);
		}
		AnxErpScenarioControlEntity entity = new AnxErpScenarioControlEntity();
		entity.setGstin(gstin);
		entity.setScenarioId(scenarioId);
		//entity.setDestination(destination);
		entity.setStatus(APIConstants.INITIATED);
		entity.setMinIdIn(minId);
		entity.setMaxIdIn(maxId);
		entity.setCreatedBy(APIConstants.SYSTEM);
		entity.setCreatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		erpScenarioControlRepo.save(entity);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"New record inserted in control table with above range");
		}
		return new Pair<>(minId, maxId);
	}

	@Override
	public Long getMaxRange(String gstin, Long scenarioId,
			OutwardTransDocument out) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ERP scenario outward getMaxRange(MaxId) selection"
							+ " started for gstin {} and scenarioId",
					gstin, scenarioId);
		}
		GSTNDetailEntity gstinInfo = gstnRepo
				.findByGstinAndIsDeleteFalse(gstin);
		if (gstinInfo == null) {
			LOGGER.error("GSTIN {} is not onboarded.", gstin);
			return null;
		}
		/*ErpScenarioPermissionEntity scenPermission = scenPermiRepo
				.findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId,
						gstinInfo.getId());

		if (scenPermission == null) {
			LOGGER.error("scenarioId {} is not onboarded for the GSTIN {} ",
					scenarioId, gstin);
			return null;
		}*/
		Long previousOutMaxId = erpScenarioControlRepo
				.findLastScenarioMaxIdForOutward(gstin,
						scenarioId);

		Long maxId = outDocRepo.findMaxIdBySgstinAndIsDeletedFalse(gstin);
		
		if(previousOutMaxId !=null && previousOutMaxId.compareTo(maxId) == 0L) {
			return null;
		}

		//Long minId = previousOutMaxId == null ? 0L : previousOutMaxId + 1;
		maxId = maxId == null ? 0L : maxId;
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ERP scenario outward getMaxRange is {} ", maxId);
		}
		AnxErpScenarioControlEntity entity = new AnxErpScenarioControlEntity();
		entity.setGstin(gstin);
		entity.setScenarioId(scenarioId);
		//entity.setDestination(destination);
		entity.setStatus(APIConstants.INITIATED);
		//entity.setMinIdOut(minId);
		entity.setMaxIdOut(maxId);
		entity.setCreatedBy(APIConstants.SYSTEM);
		entity.setCreatedOn(EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		erpScenarioControlRepo.save(entity);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"New record inserted in control table with above range");
		}
		return maxId;
	}

	@Override
	public Long getMaxRange(String gstin, Long scenarioId,
			InwardTransDocument out) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ERP scenario inward getMaxRange (MaxId) selection"
							+ " started for gstin {} and scenarioId",
					gstin, scenarioId);
		}
		GSTNDetailEntity gstinInfo = gstnRepo
				.findByGstinAndIsDeleteFalse(gstin);
		if (gstinInfo == null) {
			LOGGER.error("GSTIN {} is not onboarded.", gstin);
			return null;
		}
		/*ErpScenarioPermissionEntity scenPermission = scenPermiRepo
				.findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId,
						gstinInfo.getId());

		if (scenPermission == null) {
			LOGGER.error("scenarioId {} is not onboarded for the GSTIN {} ",
					scenarioId, gstin);
			return null;
		}*/
		Long previousInMaxId = erpScenarioControlRepo
				.findLastScenarioMaxIdForInward(gstin,
						scenarioId);

		Long maxId = inDocRepo.findMaxIdByCgstinAndIsDeletedFalse(gstin);

		if(previousInMaxId !=null && previousInMaxId.compareTo(maxId) == 0L) {
			return null;
		}

	//	Long minId = previousInMaxId == null ? 0L : previousInMaxId + 1;
		maxId = maxId == null ? 0L : maxId;
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ERP scenario outward getMaxRange is {} ", maxId);
		}
		AnxErpScenarioControlEntity entity = new AnxErpScenarioControlEntity();
		entity.setGstin(gstin);
		entity.setScenarioId(scenarioId);
		//entity.setDestination(destination);
		entity.setStatus(APIConstants.INITIATED);
		//entity.setMinIdIn(minId);
		entity.setMaxIdIn(maxId);
		entity.setCreatedBy(APIConstants.SYSTEM);
		entity.setCreatedOn(EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		erpScenarioControlRepo.save(entity);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"New record inserted in control table with above range");
		}
		return maxId;
	}

	
	@Override
	public Long getBatchId(String gstin, Long scenarioId,
			Long batchId, OutwardTransDocument out)
			throws Exception {

		/*if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ERP scenario outward getMaxRangeByBatchId(MaxId) selection"
							+ " started for gstin {} and destionation",
					gstin, destination);
		}
		Long previousOutMaxId = erpScenarioControlRepo
				.findMaxIdByGstnAndScenarioIdAndDestinationAndIsDeleteFalse(gstin,
						scenarioId, destination);

		Long maxId = outDocRepo.findMaxIdBySgstinAndIsDeletedFalse(gstin);

		Long minId = previousOutMaxId == null ? 0L : previousOutMaxId + 1;
		maxId = maxId == null ? 0L : maxId;*/
		GSTNDetailEntity gstinInfo = gstnRepo
				.findByGstinAndIsDeleteFalse(gstin);
		if (gstinInfo == null) {
			LOGGER.error("GSTIN {} is not onboarded.", gstin);
			return null;
		}
		/*ErpScenarioPermissionEntity scenPermission = scenPermiRepo
				.findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId,
						gstinInfo.getId());

		if (scenPermission == null) {
			LOGGER.error("scenarioId {} is not onboarded for the GSTIN {} ",
					scenarioId, gstin);
			return null;
		}*/
		Long batchIdOut = erpScenarioControlRepo.findByBatchIdForOutward(gstin,
				scenarioId, batchId);
		
		if(batchIdOut != null && batchIdOut.compareTo(batchId) == 0L) {
			return null;
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ERP scenario outward getMaxRangeByBatchId is {} ", batchId);
		}
		AnxErpScenarioControlEntity entity = new AnxErpScenarioControlEntity();
		entity.setGstin(gstin);
		entity.setScenarioId(scenarioId);
		//entity.setDestination(destination);
		entity.setStatus(APIConstants.INITIATED);
		//entity.setMinIdOut(minId);
		//entity.setMaxIdOut(maxId);
		entity.setBatchIdOut(batchId);
		entity.setCreatedBy(APIConstants.SYSTEM);
		entity.setCreatedOn(EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		erpScenarioControlRepo.save(entity);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"New record inserted in control table with above range");
		}
		return batchId;
	}

	
	@Override
	public Long getBatchId(String gstin, Long scenarioId,
			Long batchId, InwardTransDocument out)
			throws Exception {

		/*if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ERP scenario inward getMaxRange (MaxId) selection"
							+ " started for gstin {} and destionation",
					gstin, destination);
		}
		Long previousInMaxId = erpScenarioControlRepo
				.findMaxIdByGstnAndScenarioIdAndDestinationAndIsDeleteFalse(gstin,
						scenarioId, destination);

		Long maxId = inDocRepo.findMaxIdByCgstinAndIsDeletedFalse(gstin);

		Long minId = previousInMaxId == null ? 0L : previousInMaxId + 1;
		maxId = maxId == null ? 0L : maxId;
		*/
		GSTNDetailEntity gstinInfo = gstnRepo
				.findByGstinAndIsDeleteFalse(gstin);
		if (gstinInfo == null) {
			LOGGER.error("GSTIN {} is not onboarded.", gstin);
			return null;
		}
		/*ErpScenarioPermissionEntity scenPermission = scenPermiRepo
				.findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId,
						gstinInfo.getId());

		if (scenPermission == null) {
			LOGGER.error("scenarioId {} is not onboarded for the GSTIN {} ",
					scenarioId, gstin);
			return null;
		}*/
		Long batchIdIn = erpScenarioControlRepo.findByBatchIdForInward(gstin,
				scenarioId, batchId);
		
		if (batchIdIn != null && batchIdIn.compareTo(batchId) == 0L) {
			return null;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ERP scenario outward getMaxRange is {} ", batchId);
		}
		AnxErpScenarioControlEntity entity = new AnxErpScenarioControlEntity();
		entity.setGstin(gstin);
		entity.setScenarioId(scenarioId);
		//entity.setDestination(destination);
		entity.setStatus(APIConstants.INITIATED);
		//entity.setMinIdIn(minId);
		//entity.setMaxIdIn(maxId);
		entity.setBatchIdIn(batchId);
		entity.setCreatedBy(APIConstants.SYSTEM);
		entity.setCreatedOn(EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		erpScenarioControlRepo.save(entity);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"New record inserted in control table with above range");
		}
		return batchId;
	}

}
