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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.DailyInwardReconEntity;
import com.ey.advisory.app.data.repositories.client.DailyInwardReconRepository;
import com.ey.advisory.app.docs.dto.DailyOutwardAndInwardReconDto;
import com.ey.advisory.app.docs.dto.DailyOutwardAndInwardReconReqDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("DailyInwardReconServiceImpl")
public class DailyInwardReconServiceImpl  implements DailyInwardReconService {

	@Autowired
	private DailyInwardReconRepository dailyInwardReconRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Override
	public void persistData(List<DailyOutwardAndInwardReconDto> payload) {

		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		User user = SecurityContext.getUser();
		//List<DailyInwardReconEntity> saveEntity = new ArrayList<>();

		for (DailyOutwardAndInwardReconDto obj : payload) {
			DailyInwardReconEntity entity = new DailyInwardReconEntity();
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
			dailyInwardReconRepository.softDelete(
					obj.getAccountingVoucherDate()/*, obj.getSourceId()*/);
			dailyInwardReconRepository.save(entity);
		}
		//Operational report multiple objects of same accounting voucher date in sinagle payload is not deleting
		//dailyInwardReconRepository.saveAll(saveEntity);
	}

	@Override
	public List<DailyOutwardAndInwardReconDto> getData(DailyOutwardAndInwardReconReqDto request) {

		LocalDate accVoucherDateFrom = request.getAccVoucherDateFrom();
		LocalDate accVoucherDateTo = request.getAccVoucherDateTo();
		LocalDate extractionDateFrom = request.getExtractionDateFrom();
		LocalDate extractionDateTo = request.getExtractionDateTo();

		List<Object[]> findAvailCountFromDocHeader = findAvailCountFromDocHeader(
				accVoucherDateFrom, accVoucherDateTo, extractionDateFrom,
				extractionDateTo);

		List<Object[]> findByAccVoucherDate = findByAccVoucherDate(
				accVoucherDateFrom, accVoucherDateTo, extractionDateFrom,
				extractionDateTo);

		Map<String, Long> avilCountMap = findAvailCountFromDocHeader.stream()
				.collect(
						Collectors.toMap(
								obj1 -> obj1[0] != null
										? (obj1[1] != null
												? (String) obj1[0]
														+ (String) obj1[1]
												: (String) obj1[0])
										: obj1[1] != null ? (String) obj1[1]
												: APIConstants.EMPTY,
								obj1 -> (Long) obj1[2]));

		List<DailyOutwardAndInwardReconDto> listObj = new ArrayList<>();

		findByAccVoucherDate.forEach(obj -> {
			DailyOutwardAndInwardReconDto dto = new DailyOutwardAndInwardReconDto();
			LocalDate accVoucherDate = obj[0]!= null ? (LocalDate) obj[0] : null;
			LocalDate extractedOn = obj[1] != null ? (LocalDate) obj[1] : null;
			
			String key = accVoucherDate != null
					? (extractedOn != null
							? accVoucherDate.toString()
									+ extractedOn.toString()
							: accVoucherDate.toString())
					: extractedOn != null ? extractedOn.toString()
							: APIConstants.EMPTY;
			
			Integer availCountsInCloud = 0;
			Long count = avilCountMap.get(key);
			if (count != null) {
				availCountsInCloud = count.intValue();
			}
			dto.setAccountingVoucherDate(accVoucherDate);
			dto.setExtractedOn(extractedOn);
			
			String extractionId = (String) obj[2];
			dto.setExtractionId(extractionId);
			
			Long extractedCount = (Long) obj[3];
			Integer extCount = extractedCount.intValue();
			dto.setExtractedDocCount(extCount);

			Long stErrorCount = (Long) obj[4];
			Integer strErrorCount = stErrorCount.intValue();
			dto.setStructuralErrorCount(strErrorCount);

			Long onHold = (Long) obj[5];
			Integer onHoldCount = onHold.intValue();
			dto.setOnHold(onHoldCount);

			Long avilCount = (Long) obj[6];
			Integer avilCountForPush = avilCount.intValue();
			dto.setAvailableForPush(avilCountForPush);

			Long pushedToCloud = (Long) obj[7];
			Integer pushedCount = pushedToCloud.intValue();
			dto.setPushedToCloud(pushedCount);

			Long errorInPush = (Long) obj[8];
			Integer errorInPushCount = errorInPush.intValue();
			dto.setErroredInPush(errorInPushCount);
			
			dto.setAvailInCloud(availCountsInCloud);

			Integer difference = 0;
			difference = pushedCount - availCountsInCloud;
			dto.setDifference(difference);
			
			listObj.add(dto);

		});

		return listObj;
	}
	
	@SuppressWarnings("unchecked")
	private List<Object[]> findAvailCountFromDocHeader(LocalDate accVoucherDateFrom,
			LocalDate accVoucherDateTo, LocalDate extractionDateFrom,
			LocalDate extractionDateTo) {

		String criteria = "";

		if (accVoucherDateFrom != null && accVoucherDateTo != null
				&& extractionDateFrom != null && extractionDateTo != null) {
			criteria = "ON hdr.purchaseVoucherDate = rec.accountingVoucherDate "
					+ "AND hdr.extractedDate = rec.extractedOn AND "
					+ "dataOriginTypeCode = 'A' WHERE rec.accountingVoucherDate"
					+ " BETWEEN :accVoucherDateFrom AND :accVoucherDateTo AND "
					+ "rec.extractedOn BETWEEN :extractionDateFrom AND :extractionDateTo "
					+ "AND rec.isDelete = FALSE AND hdr.isDeleted = FALSE";
		} else if (accVoucherDateFrom != null && accVoucherDateTo != null) {
			criteria = "ON hdr.purchaseVoucherDate = rec.accountingVoucherDate "
					+ "AND dataOriginTypeCode = 'A' WHERE rec.accountingVoucherDate"
					+ " BETWEEN :accVoucherDateFrom AND :accVoucherDateTo "
					+ "AND rec.isDelete = FALSE AND hdr.isDeleted = FALSE";
		} else if (extractionDateFrom != null && extractionDateTo != null) {
			criteria = "ON hdr.extractedDate = rec.extractedOn AND "
					+ "dataOriginTypeCode = 'A' WHERE rec.extractedOn BETWEEN "
					+ ":extractionDateFrom AND :extractionDateTo "
					+ "AND rec.isDelete = FALSE AND hdr.isDeleted = FALSE";
		}
		
		String sql = "SELECT rec.accountingVoucherDate,rec.extractedOn,COUNT(distinct hdr.id ) as AVAIL_CLOUD "
				+ "FROM DailyInwardReconEntity rec INNER JOIN InwardTransDocument hdr "
				+ criteria + " GROUP BY rec.accountingVoucherDate,rec.extractedOn "
				+ "ORDER BY rec.accountingVoucherDate,rec.extractedOn DESC";

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("SQL query1 is {} ", sql);
		}
		Query query = entityManager.createQuery(sql);
		/**
		 * setting the values based on condition the key values in the
		 * setParameter should be same as index names. index names are
		 * dynamically coming from the criteria.
		 */
		if (accVoucherDateFrom != null && accVoucherDateTo != null) {
			query.setParameter("accVoucherDateFrom", accVoucherDateFrom);
			query.setParameter("accVoucherDateTo", accVoucherDateTo);

		}
		if (extractionDateFrom != null && extractionDateTo != null) {
			query.setParameter("extractionDateFrom", extractionDateFrom);
			query.setParameter("extractionDateTo", extractionDateTo);
		}

		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	private List<Object[]> findByAccVoucherDate(LocalDate accVoucherDateFrom,
			LocalDate accVoucherDateTo, LocalDate extractionDateFrom,
			LocalDate extractionDateTo) {

		String criteria = "";

		if (accVoucherDateFrom != null && accVoucherDateTo != null
				&& extractionDateFrom != null && extractionDateTo != null) {
			criteria = "WHERE doc.accountingVoucherDate"
					+ " BETWEEN :accVoucherDateFrom AND :accVoucherDateTo AND "
					+ "doc.extractedOn BETWEEN :extractionDateFrom AND :extractionDateTo "
					+ "AND doc.isDelete = FALSE";
		} else if (accVoucherDateFrom != null && accVoucherDateTo != null) {
			criteria = "WHERE doc.accountingVoucherDate"
					+ " BETWEEN :accVoucherDateFrom AND :accVoucherDateTo "
					+ "AND doc.isDelete = FALSE";
		} else if (extractionDateFrom != null && extractionDateTo != null) {
			criteria = "WHERE doc.extractedOn BETWEEN "
					+ ":extractionDateFrom AND :extractionDateTo "
					+ "AND doc.isDelete = FALSE";
		}

		String sql = "SELECT doc.accountingVoucherDate,doc.extractedOn,doc.extractionId,SUM(doc.extractedDocCount),"
				+ "SUM(doc.structuralErrorCount),SUM(doc.onHold),SUM(doc.availableForPush),"
				+ "SUM(doc.pushedToCloud),SUM(doc.erroredInPush) FROM DailyInwardReconEntity doc "
				+ criteria + " GROUP BY doc.accountingVoucherDate,doc.extractedOn,doc.extractionId "
				+ "ORDER BY doc.accountingVoucherDate,doc.extractedOn,doc.extractionId DESC";

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("SQL query2 is {} ", sql);
		}
		
		Query query = entityManager.createQuery(sql);
		/**
		 * setting the values based on condition the key values in the
		 * setParameter should be same as index names. index names are
		 * dynamically coming from the criteria.
		 */
		if (accVoucherDateFrom != null && accVoucherDateTo != null) {
			query.setParameter("accVoucherDateFrom", accVoucherDateFrom);
			query.setParameter("accVoucherDateTo", accVoucherDateTo);

		}
		if (extractionDateFrom != null && extractionDateTo != null) {
			query.setParameter("extractionDateFrom", extractionDateFrom);
			query.setParameter("extractionDateTo", extractionDateTo);
		}

		return query.getResultList();
	}
}