/**
 * 
 */
package com.ey.advisory.app.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.SaveToGstnEventStatusRepository;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("GstnUserRequestUtil")
public class GstnUserRequestUtil {

	@Autowired
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository saveBatchRepo;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("saveToGstnEventStatusRepository")
	SaveToGstnEventStatusRepository saveToGstnEventStatusRepository;

	@Autowired
	@Qualifier("DocRepository")
	DocRepository docRepository;

	public Long createGstnUserRequest(String gstin, String taxPeriod,
			String requestType, String returnType, String groupCode,
			String userName, Boolean isNilUserInput, Boolean isHsnUserInput,
			Boolean isCrossItcUserInput) {

		TenantContext.setTenantId(groupCode);
		// Delete the old entries for GET
		if (APIConstants.GET.equalsIgnoreCase(requestType)) {
			gstnUserRequestRepo.deleteOldEntries(gstin, taxPeriod, requestType,
					returnType);
		}
		GstnUserRequestEntity entity = new GstnUserRequestEntity();
		entity.setGstin(gstin);
		entity.setTaxPeriod(taxPeriod);
		entity.setDerivedRetPeriod(GenUtil.convertTaxPeriodToInt(taxPeriod));
		entity.setRequestType(requestType);
		entity.setReturnType(returnType);
		entity.setCreatedBy(userName);
		entity.setCreatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		if (isNilUserInput == null || isHsnUserInput == null) {

			Optional<GstnUserRequestEntity> previousEntity = gstnUserRequestRepo
					.findTop1ByGstinAndTaxPeriodAndRequestTypeAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
							gstin, taxPeriod, requestType.toUpperCase(),
							returnType.toUpperCase());

			if (previousEntity.isPresent()) {
				GstnUserRequestEntity gstnUserRequestEntity = previousEntity
						.get();
				isNilUserInput = gstnUserRequestEntity.isNilUserInput();
				isHsnUserInput = gstnUserRequestEntity.isHsnUserInput();
			}
		}
		entity.setNilUserInput(isNilUserInput != null ? isNilUserInput : false);
		entity.setHsnUserInput(isHsnUserInput != null ? isHsnUserInput : false);
		entity.setCrossItcUserInput(
				isCrossItcUserInput != null ? isCrossItcUserInput : false);
		GstnUserRequestEntity save = gstnUserRequestRepo.save(entity);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GstnUserRequestEntity {} inside createGstnUserRequest ",
					save);
		}

		return save.getId();
	}

	public boolean isNextSaveRequestEligible(String gstin, String taxPeriod,
			String requestType, String returnType, String groupCode) {

		TenantContext.setTenantId(groupCode);

		// return true if last request is more thatn 45 minutes.
		Optional<GstnUserRequestEntity> optionalUserRequest = gstnUserRequestRepo
				.findTop1ByGstinAndTaxPeriodAndRequestTypeAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
						gstin, taxPeriod, requestType, returnType);
		if (optionalUserRequest.isPresent()) {
			GstnUserRequestEntity lastUserRequest = optionalUserRequest.get();
			LocalDateTime lastRequestTime = EYDateUtil
					.toISTDateTimeFromUTC(lastUserRequest.getCreatedOn());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			// Last request time greater than 45 min
			if (now.minusMinutes(45).compareTo(lastRequestTime) > 0) {
				return true;
			}

			// return false if last request is inprogress.
			List<Gstr1SaveBatchEntity> pendingRefIds = saveBatchRepo
					.findPendingRefIdByUserRequestId(returnType, gstin,
							taxPeriod, lastUserRequest.getId());

			if (pendingRefIds != null && !pendingRefIds.isEmpty()) {
				return false;
			}

			// To check if the job has reached upto sandbox call.
			Integer lastJobStatusCode = saveToGstnEventStatus
					.findLastJobStatusCode(gstin, taxPeriod, groupCode);
			// Just to make sure the last SaveToGstn job has ran.
			if (lastJobStatusCode != null && lastJobStatusCode < 30) {
				return false;
			}
		}
		return true;
	}

	public boolean isNextSaveRequestEligibleGstr8(String gstin,
			String taxPeriod) {

		return true;
	}

	public boolean isNextLvlSaveRequestEligible(String gstin, String taxPeriod,
			String requestType, String returnType, String section) {

		// return false if last request is inprogress.
		List<Gstr1SaveBatchEntity> pendingRefIds = saveBatchRepo
				.findPendingRefIdByUserRequestId(returnType, gstin, taxPeriod,
						section.toUpperCase());

		if (pendingRefIds != null && !pendingRefIds.isEmpty()) {
			return false;
		}

		// To check if the job has reached upto sandbox call.
		Integer lastJobStatusCode = saveToGstnEventStatusRepository
				.findStatsCode(taxPeriod, gstin, section);
		// Just to make sure the last SaveToGstn job has ran.
		if (lastJobStatusCode != null && lastJobStatusCode < 30) {
			return false;
		}

		return true;
	}

	public boolean isNextLvlDeleteRequestEligible(String docKey) {

		// return true if last request is more thatn 45 minutes.

		// return false if last request is inprogress.
		List<OutwardTransDocument> isOrgDocSaved = docRepository
				.findOrgDocsByDocKey(docKey);

		if (isOrgDocSaved.size() > 0) {
			return true;
		}

		return false;
	}

	public void allowNextSaveIfNoDataToSaveNow(String gstin, String taxPeriod,
			String requestType, String returnType, String groupCode,
			Long userRequestId) {

		TenantContext.setTenantId(groupCode);
		// return false if last request is inprogress.
		List<Gstr1SaveBatchEntity> recs = saveBatchRepo
				.findPendingRefIdByUserRequestId(returnType, gstin, taxPeriod,
						userRequestId);

		if (recs == null || recs.isEmpty()) {
			// status code 70 says status as NO DATA TO SAVE
			saveToGstnEventStatus.EventEntry(taxPeriod, gstin, 70, groupCode);
		}
	}

	public void allowNextSaveIfNoDataToSaveNow(String gstin, String taxPeriod,
			String requestType, String returnType, String groupCode,
			Long userRequestId, String section) {

		TenantContext.setTenantId(groupCode);
		// return false if last request is inprogress.
		List<Gstr1SaveBatchEntity> recs = saveBatchRepo
				.findPendingRefIdByUserRequestId(returnType, gstin, taxPeriod,
						userRequestId);

		if (recs == null || recs.isEmpty()) {
			// status code 70 says status as NO DATA TO SAVE
			LOGGER.debug("Section {} ", section);
			saveToGstnEventStatus.EventEntry(taxPeriod, gstin, 70, groupCode,
					section);
		}
	}

	public void allowNextSaveIfNoDataToSaveNowForItc04(String gstin,
			String taxPeriod, String requestType, String returnType,
			String groupCode, Long userRequestId) {

		TenantContext.setTenantId(groupCode);
		// return false if last request is inprogress.
		List<Gstr1SaveBatchEntity> recs = saveBatchRepo
				.findPendingRefIdByUserRequestId(returnType, gstin, taxPeriod,
						userRequestId);

		if (recs == null || recs.isEmpty()) {
			// status code 70 says status as NO DATA TO SAVE
			saveToGstnEventStatus.Itc04EventEntry(taxPeriod, gstin, 70,
					groupCode);
		}

	}
}
