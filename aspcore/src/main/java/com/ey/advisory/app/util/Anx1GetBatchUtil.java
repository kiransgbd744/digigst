/**
 * 
 */
package com.ey.advisory.app.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr3bGetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr6aDashboardReqDto;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("anx1GetBatchUtil")
public class Anx1GetBatchUtil {

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Autowired
	private GSTNDetailRepository gstnDetailRepo;

	public GetAnx1BatchEntity makeBatch(String gstin, String taxPeriod,
			String getType, String returnType, String userName) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setApiSection(returnType);
		batch.setSgstin(gstin);
		batch.setTaxPeriod(taxPeriod);
		batch.setType(getType);
		batch.setAction("N");
		batch.setDerTaxPeriod(GenUtil.convertTaxPeriodToInt(taxPeriod));
		batch.setCreatedBy(userName);
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		return batch;
	}

	public GetAnx1BatchEntity makeBatch(Anx1GetInvoicesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setApiSection(APIConstants.ANX1.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setFromTime(dto.getFromTime());
		batch.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		/*
		 * batch.setFromTime( dto.getFromTime() != null &&
		 * !dto.getFromTime().isEmpty() ?
		 * DateUtil.stringToTime(dto.getFromTime(), DateUtil.DATE_FORMAT1) :
		 * null);
		 */
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		return batch;
	}

	public GetAnx1BatchEntity makeBatchGstr1(Gstr1GetInvoicesReqDto dto,
			String type, String returnType) {

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setFileId(dto.getFileId());
		batch.setUserRequestId(dto.getUserRequestId());
		batch.setApiSection(
				returnType != null ? returnType.toUpperCase() : null);
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setETin(dto.getEtin());
		batch.setFromTime(dto.getFromTime());
		batch.setToTime(dto.getToTime());
		if (APIIdentifiers.IMS_COUNT.equalsIgnoreCase(dto.getApiSection())
				|| APIIdentifiers.IMS_INVOICE
						.equalsIgnoreCase(dto.getApiSection())) {
			batch.setDerTaxPeriod(Integer.parseInt("0"));
		} else {
			batch.setDerTaxPeriod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(userName);
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);
		batch.setFiled(dto.getIsFiled() != null
				? dto.getIsFiled().equalsIgnoreCase(APIConstants.Y) ? true
						: false
				: false);
		batch.setPortCode(dto.getPortCode());
		batch.setBillOfEntryNum(dto.getBeNum());
		String beDate = dto.getBeDate();
		if (beDate != null) {
			LocalDate locDate = LocalDate.parse(beDate,
					DateUtil.SUPPORTED_DATE_FORMAT2);
			beDate = locDate.format(DateUtil.SUPPORTED_DATE_FORMAT1);
			LocalDate stringToDate = DateUtil.tryConvertUsingFormat(beDate,
					DateUtil.SUPPORTED_DATE_FORMAT1);
			batch.setBillOfEntryCreated(stringToDate);
		}

		batch.setDeltaGet(
				dto.getIsDeltaGet() != null ? dto.getIsDeltaGet() : false);
		batch.setAutoGet(
				dto.getIsAutoGet() != null ? dto.getIsAutoGet() : false);

		if (!batch.isDeltaGet()
				&& APIConstants.GSTR2A.equalsIgnoreCase(returnType)
				|| APIConstants.GSTR2A_UPLOAD.equalsIgnoreCase(returnType)) {
			GSTNDetailEntity entity = gstnDetailRepo
					.findByGstinAndIsDeleteFalse(dto.getGstin());

			/** set isDeltaGet status **/
			boolean isFDeltaGetData = false;
			EntityConfigPrmtEntity entityConfig = entityConfigRepo
					.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
							TenantContext.getTenantId(), entity.getEntityId(),
							"I25");
			String paramValue = entityConfig != null
					? entityConfig.getParamValue()
					: "A";
			if ("B".equals(paramValue)) {
				isFDeltaGetData = true;
			}

			batch.setDeltaGet(isFDeltaGetData);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("batch {} inside makeBatchGstr1 ", batch);

		}

		return batch;
	}

	public GetAnx1BatchEntity makeBatchGstr1EInvoice(Gstr1GetInvoicesReqDto dto,
			String type, String returnType) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setFileId(dto.getFileId());
		batch.setUserRequestId(dto.getUserRequestId());
		batch.setApiSection(
				returnType != null ? returnType.toUpperCase() : null);
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setETin(dto.getEtin());
		batch.setFromTime(dto.getFromTime());
		if (dto.getReturnPeriod() != null) {
			batch.setDerTaxPeriod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		/*
		 * String fromTime = dto.getFromTime(); if (fromTime != null) {
		 * LocalDateTime stringToTime = DateUtil.stringToTime(fromTime,
		 * DateUtil.SUPPORTED_DATE_FORMAT1); batch.setFromTime(stringToTime); }
		 * else { batch.setFromTime(null); }
		 */
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);
		batch.setFiled(dto.getIsFiled() != null
				? dto.getIsFiled().equalsIgnoreCase(APIConstants.Y) ? true
						: false
				: false);
		batch.setPortCode(dto.getPortCode());
		batch.setBillOfEntryNum(dto.getBeNum());
		String beDate = dto.getBeDate();
		if (beDate != null) {
			LocalDate stringToDate = DateUtil.tryConvertUsingFormat(beDate,
					DateUtil.SUPPORTED_DATE_FORMAT1);
			batch.setBillOfEntryCreated(stringToDate);
		}

		return batch;
	}

	public GetAnx1BatchEntity makeBatchGstr6(Gstr6GetInvoicesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setUserRequestId(dto.getUserRequestId());
		batch.setApiSection(APIConstants.GSTR6.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		batch.setAction(dto.getAction() == null ? "N" : dto.getAction());
		// batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setFromTime(dto.getFromTime());
		/*
		 * String fromTime = dto.getFromTime(); if (fromTime != null) {
		 * LocalDateTime stringToTime = DateUtil.stringToTime(fromTime,
		 * DateUtil.SUPPORTED_DATE_FORMAT1); batch.setFromTime(stringToTime); }
		 * else { batch.setFromTime(null); }
		 */

		batch.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		batch.setType(type != null ? type.toUpperCase() : null);

		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		return batch;
	}

	public GetAnx1BatchEntity makeBatchGstr6a(Gstr6aGetInvoicesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setUserRequestId(dto.getUserRequestId());
		batch.setApiSection(APIConstants.GSTR6A.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		batch.setAction(dto.getAction() == null ? "N" : dto.getAction());
		// batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setFromTime(dto.getFromTime());
		/*
		 * String fromTime = dto.getFromTime(); if (fromTime != null) {
		 * LocalDateTime stringToTime = DateUtil.stringToTime(fromTime,
		 * DateUtil.SUPPORTED_DATE_FORMAT1); batch.setFromTime(stringToTime); }
		 * else { batch.setFromTime(null); }
		 */
		batch.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		batch.setType(type != null ? type.toUpperCase() : null);

		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		return batch;
	}

	public GetAnx1BatchEntity makeBatchGstr7(Gstr7GetInvoicesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setUserRequestId(dto.getUserRequestId());
		batch.setApiSection(APIConstants.GSTR7.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		// batch.setAction(batch.getAction() == null ? "N" : batch.getAction());
		batch.setAction(dto.getAction() != null ? dto.getAction() : "N");
		batch.setCtin(dto.getCtin());
		batch.setFromTime(dto.getFromTime());
		/*
		 * String fromTime = dto.getFromTime(); if (fromTime != null) {
		 * LocalDateTime stringToTime = DateUtil.stringToTime(fromTime,
		 * DateUtil.SUPPORTED_DATE_FORMAT1); batch.setFromTime(stringToTime); }
		 * else { batch.setFromTime(null); }
		 */

		batch.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		batch.setType(type != null ? type.toUpperCase() : null);

		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		return batch;
	}

	public GetAnx1BatchEntity makeBatchItc04(Itc04GetInvoicesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setUserRequestId(dto.getUserRequestId());
		batch.setApiSection(APIConstants.ITC04.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		String s1 = dto.getReturnPeriod().substring(2, 6);
		String s2 = dto.getReturnPeriod().substring(0, 2);
		String s3 = s1 + s2;
		Integer s4 = Integer.valueOf(s3);
		batch.setDerTaxPeriod(s4);

		batch.setAction(dto.getAction() != null ? dto.getAction() : "N");
		batch.setCtin(dto.getCtin());
		batch.setFromTime(dto.getFromTime());

		batch.setType(type != null ? type.toUpperCase() : null);

		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		return batch;
	}

	public GetAnx1BatchEntity makeBatchRet(RetGetInvoicesReqDto dto) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setApiSection(APIConstants.RET.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		batch.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		return batch;
	}

	public GetAnx1BatchEntity makeBatchGstr3B(Gstr3bGetInvoicesReqDto dto,
			String type, String returnType) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setAction("N");
		batch.setApiSection(APIConstants.GSTR3B.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getTaxPeriod());
		batch.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getTaxPeriod()));

		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		return batch;

	}

	public void updateById(Long batchId, String status, String errorCode,
			String errorDesc, Boolean isTokenResponse) {
		Optional<GetAnx1BatchEntity> optBatch = batchRepo.findById(batchId);
		if (optBatch.isPresent()) {
			LOGGER.error(
					"Batch Id is present {} and status is {} for GroupCode {} ",
					batchId, status, TenantContext.getTenantId());
			GetAnx1BatchEntity anx1BatchEntity = optBatch.get();
			anx1BatchEntity.setStatus(status);
			anx1BatchEntity.setEndTime(LocalDateTime.now());
			if (errorCode != null) {
				anx1BatchEntity.setErrorCode(errorCode);
			}
			if (errorDesc != null) {
				anx1BatchEntity.setErrorDesc(errorDesc);
			}
			if (isTokenResponse != null) {
				anx1BatchEntity.setTokenResponse(isTokenResponse);
			} else {
				isTokenResponse = false;
			}
			LOGGER.error(
					"Before Updating Batch Id {} and status {} for GroupCode {} ",
					batchId, status, TenantContext.getTenantId());
			batchRepo.save(anx1BatchEntity);
			LOGGER.error(
					"After Updating Batch Id {} and status {} for GroupCode {} ",
					batchId, status, TenantContext.getTenantId());
			try {
				int rowsEffected = batchRepo.updateBatchDetails(status,
						errorDesc, errorCode, LocalDateTime.now(),
						isTokenResponse, batchId);
				LOGGER.error("Rows Effected {} by the Update for batch Id {} ",
						rowsEffected, batchId);
			} catch (Exception e) {
				LOGGER.error(
						"Exception while updating the batch from update statement");
			}
		} else {
			String errMsg = String.format(
					"Batch ID %s is not present in the batch table,"
							+ " So unable to update the status for GroupCode %s",
					batchId, TenantContext.getTenantId());
			LOGGER.error(errMsg);
		}
	}

	public List<GetAnx1BatchEntity> makeBatchGstr6aDashboard(
			Gstr6aDashboardReqDto dto, String type, String returnPeriod) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		List<GetAnx1BatchEntity> batches = new ArrayList<GetAnx1BatchEntity>();

		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setUserRequestId(dto.getUserRequestId());
		batch.setApiSection(APIConstants.GSTR6A.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(returnPeriod);
		batch.setDerTaxPeriod(GenUtil.convertTaxPeriodToInt(returnPeriod));
		batch.setAction(dto.getAction() == null ? "N" : dto.getAction());
		// batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setFromTime(dto.getFromTime());
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);
		batches.add(batch);
		return batches;
	}

	public GetAnx1BatchEntity makeBatchImsSupplier(Gstr1GetInvoicesReqDto dto,
			String section, String returnType, String imsReturnType) {

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setFileId(dto.getFileId());
		batch.setUserRequestId(dto.getUserRequestId());
		batch.setApiSection(
				returnType != null ? returnType.toUpperCase() : null);
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setETin(dto.getEtin());
		batch.setFromTime(dto.getFromTime());
		batch.setToTime(dto.getToTime());

		batch.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

		batch.setType(section);
		batch.setImsReturnType(imsReturnType);
		batch.setCreatedBy(userName);
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);
		batch.setFiled(dto.getIsFiled() != null
				? dto.getIsFiled().equalsIgnoreCase(APIConstants.Y) ? true
						: false
				: false);
		batch.setPortCode(dto.getPortCode());
		batch.setBillOfEntryNum(dto.getBeNum());
		String beDate = dto.getBeDate();
		if (beDate != null) {
			LocalDate locDate = LocalDate.parse(beDate,
					DateUtil.SUPPORTED_DATE_FORMAT2);
			beDate = locDate.format(DateUtil.SUPPORTED_DATE_FORMAT1);
			LocalDate stringToDate = DateUtil.tryConvertUsingFormat(beDate,
					DateUtil.SUPPORTED_DATE_FORMAT1);
			batch.setBillOfEntryCreated(stringToDate);
		}

		batch.setDeltaGet(
				dto.getIsDeltaGet() != null ? dto.getIsDeltaGet() : false);
		batch.setAutoGet(
				dto.getIsAutoGet() != null ? dto.getIsAutoGet() : false);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("batch {} inside makeBatchGstr1 ", batch);

		}

		return batch;
	}
}
