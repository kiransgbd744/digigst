package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Service("Gstr2BbatchUtil")
public class Gstr2BbatchUtil {
	
	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public GetAnx1BatchEntity makeBatchGstr2B(Gstr2bGetInvoiceReqDto dto) {
		
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		batch.setApiSection(APIConstants.GSTR2B);
		batch.setAction(dto.getAction() == null ? "N" : dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setFromTime(dto.getFromTime());
		/*String fromTime = dto.getFromTime();
		if (fromTime != null) {
			LocalDateTime stringToTime = DateUtil.stringToTime(fromTime,
					DateUtil.SUPPORTED_DATE_FORMAT1);
			batch.setFromTime(stringToTime);
		} else {
			batch.setFromTime(null);
		}*/

		batch.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		batch.setType(APIConstants.GSTR2B_GET_ALL);

		batch.setCreatedBy(userName);
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		return batch;
	}

		
	public void updateById(Long batchId, String status, String errorCode,
			String errorDesc, Boolean isTokenResponse, Long userRequestId) {

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		Optional<GetAnx1BatchEntity> optBatch = batchRepo.findById(batchId);
		if (optBatch.isPresent()) {
			GetAnx1BatchEntity anx1BatchEntity = optBatch.get();
			anx1BatchEntity.setStatus(status);
			anx1BatchEntity.setEndTime(now);
			anx1BatchEntity.setUserRequestId(userRequestId);
			if (errorCode != null) {
				anx1BatchEntity.setErrorCode(errorCode);
			}
			if (errorDesc != null) {
				anx1BatchEntity.setErrorDesc(errorDesc);
			}
			if (isTokenResponse != null) {
				anx1BatchEntity.setTokenResponse(isTokenResponse);
			}
			batchRepo.save(anx1BatchEntity);
		}

	}
	
	public GetAnx1BatchEntity getGstinAndTaxPeriodByBatchId(Long id){
		
		try {
			String queryString = "select GSTIN, RETURN_PERIOD from "
					+ "GETANX1_BATCH_TABLE where id = :id ";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("id", id);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from GETANX1_BATCH_TABLE "
						+ "table for id : "+id);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<GetAnx1BatchEntity> retList = list.stream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList.get(0);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr2BbatchUtil.getGstinAndTaxPeriodByBatchId");
		}
	}
	

	private GetAnx1BatchEntity convert(Object[] arr) {

		GetAnx1BatchEntity obj = new GetAnx1BatchEntity();
		obj.setSgstin((String) arr[0]);
		obj.setTaxPeriod((String) arr[1]);
		return obj;
	}
		
	}
