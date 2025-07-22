/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.DailyOutwardReconEntity;
import com.ey.advisory.app.data.repositories.client.DailyOutwardReconRepository;
import com.ey.advisory.app.docs.dto.DailyOutwardAndInwardReconDto;
import com.ey.advisory.app.docs.dto.DailyOutwardAndInwardReconReqDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("DailyOutwardReconServiceImpl")
@Slf4j
public class DailyOutwardReconServiceImpl implements DailyOutwardReconService {

	@Autowired
	@Qualifier("DailyOutwardReconRepository")
	private DailyOutwardReconRepository dailyOutwardReconRepository;

	@Override
	public void persistData(List<DailyOutwardAndInwardReconDto> payload) {

		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		User user = SecurityContext.getUser();
		//List<DailyOutwardReconEntity> saveEntity = new ArrayList<>();

		for (DailyOutwardAndInwardReconDto obj : payload) {
			DailyOutwardReconEntity entity = new DailyOutwardReconEntity();
			entity.setExtractionId(obj.getExtractionId());
			entity.setAccountingVoucherDate(obj.getAccountingVoucherDate());
			entity.setExtractionType(obj.getExtractionType());
			entity.setExtractedDocCount(obj.getExtractedDocCount());
			entity.setStructuralErrorCount(obj.getStructuralErrorCount());
			entity.setOnHold(obj.getOnHold());
			entity.setAvailableForPush(obj.getAvailableForPush());
			entity.setPushedToCloud(obj.getPushedToCloud());
			entity.setErroredInPush(obj.getErroredInPush());
			entity.setSourceId(obj.getSourceId());
			entity.setExtractedOn(obj.getExtractedOn());
			entity.setCreatedBy(user.getUserPrincipalName());
			entity.setCreatedOn(convertNow);
			entity.setDelete(false);
			//saveEntity.add(entity);
			dailyOutwardReconRepository.softDelete(
					obj.getAccountingVoucherDate()/*, obj.getSourceId()*/);
			dailyOutwardReconRepository.save(entity);
		}
		//Operational report multiple objects of same accounting voucher date in sinagle payload is not deleting
		//dailyOutwardReconRepository.saveAll(saveEntity);
	}

	@Override
	public List<DailyOutwardAndInwardReconDto> getData(
			DailyOutwardAndInwardReconReqDto request) {

		LocalDate accVoucherDateFrom = request.getAccVoucherDateFrom();
		LocalDate accVoucherDateTo = request.getAccVoucherDateTo();

		List<Object[]> findAvailCountFromDocHeader = dailyOutwardReconRepository
				.findAvailCountFromDocHeader(accVoucherDateFrom,
						accVoucherDateTo);

		List<Object[]> findByAccVoucherDate = dailyOutwardReconRepository
				.findByAccVoucherDate(accVoucherDateFrom, accVoucherDateTo);

		Map<LocalDate, Long> avilCountMap = findAvailCountFromDocHeader.stream()
				.collect(Collectors.toMap(obj1 -> (LocalDate) obj1[0],
						obj1 -> (Long) obj1[1]));

		List<DailyOutwardAndInwardReconDto> listObj = new ArrayList<>();

		findByAccVoucherDate.forEach(obj -> {
			DailyOutwardAndInwardReconDto dto = new DailyOutwardAndInwardReconDto();
			LocalDate accVoucherDate = (LocalDate) obj[0];
			Integer availCountsInCloud = 0;
			Long count = avilCountMap.get(accVoucherDate);
			if (count != null) {
				availCountsInCloud = count.intValue();
			}
			dto.setAccountingVoucherDate(accVoucherDate);
			
			Integer extCount = 0;
			if (obj[1] != null) {
				Long extractedCount = (Long) obj[1];
				extCount = extractedCount.intValue();
			}
			dto.setExtractedDocCount(extCount);
			
			Integer strErrorCount = 0;
			if (obj[2] != null) {
				Long stErrorCount = (Long) obj[2];
				strErrorCount = stErrorCount.intValue();
			}
			dto.setStructuralErrorCount(strErrorCount);
			
			Integer onHoldCount = 0;
			if (obj[3] != null) {
				Long onHold = (Long) obj[3];
				onHoldCount = onHold.intValue();
			}
			dto.setOnHold(onHoldCount);
			
			Integer avilCountForPush = 0;
			if (obj[4] != null) {
				Long avilCount = (Long) obj[4];
				avilCountForPush = avilCount.intValue();
			}
			dto.setAvailableForPush(avilCountForPush);
			
			Integer pushedCount = 0;
			if (obj[5] != null) {
				Long pushedToCloud = (Long) obj[5];
				pushedCount = pushedToCloud.intValue();
			}
			dto.setPushedToCloud(pushedCount);
			
			Integer errorInPushCount = 0;
			if (obj[6] != null) {
				Long errorInPush = (Long) obj[6];
				errorInPushCount = errorInPush.intValue();
			}
			dto.setErroredInPush(errorInPushCount);
			dto.setAvailInCloud(availCountsInCloud);

			Integer difference = 0;
			difference = pushedCount - availCountsInCloud;
			dto.setDifference(difference);

			listObj.add(dto);

		});

		return listObj;
	}
}