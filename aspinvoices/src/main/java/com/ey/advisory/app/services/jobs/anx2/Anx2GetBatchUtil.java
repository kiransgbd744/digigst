/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx2GetBatchUtil")
@Slf4j
public class Anx2GetBatchUtil {
	
	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	public GetAnx1BatchEntity makeBatch(Anx2GetInvoicesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setApiSection(APIConstants.ANX2.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		
		batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setETin(dto.getEtin());
		batch.setFromTime(dto.getFromTime());
		/*batch.setFromTime(
				dto.getFromTime() != null && !dto.getFromTime().isEmpty()
						? DateUtil.stringToTime(dto.getFromTime(),
								DateUtil.DATE_FORMAT1)
						: null);*/
		batch.setDerTaxPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		//batch.setProcessingStatus(APIConstants.PROCESSING_STATUS);
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);
		
		//Extra column added only in ANX2 GET to Generate Request ID
		//batch.setRequestId(dto.getRequestId());
		return batch;
	}

	/*public GetAnx1BatchEntity makeBatchWithGstr1Dto(
			Gstr1GetInvoicesReqDto dto, String type) {
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setApiSection(APIConstants.GSTR2A.toUpperCase());
		batch.setSGstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		
		batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setETin(dto.getEtin());
		batch.setFromTime(
				dto.getFromTime() != null && !dto.getFromTime().isEmpty()
						? DateUtil.stringToTime(dto.getFromTime(),
								DateUtil.DATE_FORMAT1)
						: null);
		batch.setDerTaxPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		//batch.setProcessingStatus(APIConstants.PROCESSING_STATUS);
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);
		return batch;
	}*/
	
	
	
	/*public void updateById(Long batchId, String status) {

		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			Optional<GetAnx1BatchEntity> optBatch = batchRepo.findById(batchId);
			if (optBatch.isPresent()) {
				GetAnx1BatchEntity anx1BatchEntity = optBatch.get();
				anx1BatchEntity.setStatus(status);
				anx1BatchEntity.setEndTime(now);
				batchRepo.save(anx1BatchEntity);
			}
		
	}*/
}
