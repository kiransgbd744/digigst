/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.TransactionalInwardEntity;
import com.ey.advisory.app.data.repositories.client.TransactionalInwardRepository;
import com.ey.advisory.app.docs.dto.TransactionalDto;
import com.ey.advisory.app.docs.dto.TransactionalReqDto;
import com.ey.advisory.app.docs.dto.TransactionalReportingDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("TransactionalInwardService")
public class TransactionalInwardService {

	@Autowired
	private TransactionalInwardRepository transactionalInwardRepo;
	
	@Autowired
	private GSTNDetailRepository gstinRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public static String ZERO = "0.00";

	public void persistData(LocalDate reporDate, List<TransactionalDto> payload) {

		if (reporDate == null || payload == null) {
			LOGGER.error(
					"Null values found in the request DateOfResp {}, Item {} ",
					reporDate, payload);
			return;
		}
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDate convertDate = EYDateUtil
				.toUTCDateTimeFromLocal(reporDate);
		User user = SecurityContext.getUser();
		List<TransactionalInwardEntity> saveEntity = new ArrayList<>();
		
		String gstin = null;
		Integer drvdRetPeriod = null;
		String sourceId = null;
		String docType = null;
		String status = null;
		Long entityId = null;
		for (TransactionalDto obj : payload) {
			gstin = obj.getGstin();
			drvdRetPeriod = GenUtil.convertTaxPeriodToInt(obj.getTaxPeriod());
			sourceId = obj.getSourceId();
			docType = obj.getDocType();
			status = obj.getStatus();
			entityId = gstinRepo.findEntityIdByGstin(gstin);
			TransactionalInwardEntity entity = new TransactionalInwardEntity();
			
			entity.setEntityId(entityId);
			entity.setGstin(gstin);
			entity.setTaxPeriod(obj.getTaxPeriod());
			entity.setDerivedRetPeriod(drvdRetPeriod);
			entity.setDateOfReporting(convertDate);
			entity.setSourceId(sourceId);
			entity.setStatus(obj.getStatus());
			entity.setDerivedStatus(getDerivedStatus(obj.getStatus()));
			entity.setDocType(obj.getDocType());
			entity.setErpNumOfInvoices(obj.getErpNumOfInvs());
			entity.setErpTotalInvValue(
					obj.getErpTotalInvValue() != null ? new BigDecimal(obj.getErpTotalInvValue()) : null);
			entity.setErpAssessableValue(obj.getErpAccValue() != null ? new BigDecimal(obj.getErpAccValue()): null);
			entity.setErpIgstAmt(obj.getErpIgst()!= null ? new BigDecimal(obj.getErpIgst()): null);
			entity.setErpSgstAmt(obj.getErpSgst()!= null ? new BigDecimal(obj.getErpSgst()): null);
			entity.setErpCgstAmt(obj.getErpCgst() != null ? new BigDecimal(obj.getErpCgst()): null);
			entity.setErpCessAmt(obj.getErpCess() != null ? new BigDecimal(obj.getErpCess()): null);
			entity.setCreatedBy(user.getUserPrincipalName());
			entity.setCreatedOn(convertNow);
			entity.setIsDelete(false);
			saveEntity.add(entity);
			transactionalInwardRepo.softDelete(gstin, drvdRetPeriod, sourceId);
		}
		if(gstin == null || drvdRetPeriod == null || sourceId == null){
			LOGGER.error(
					"Null values found in the request gstin {}, drvdRetPeriod {}, sourceId {} ",
					gstin, drvdRetPeriod, sourceId);
			return;
		}
		//transactionalInwardRepo.softDelete(gstin, drvdRetPeriod, sourceId);
		transactionalInwardRepo.saveAll(saveEntity);
		transactionalInwardRepo.setCloudValues(gstin, drvdRetPeriod, sourceId);
		//transactionalInwardRepo.setCloudValuesForPushPendingVerification(gstin, drvdRetPeriod, sourceId);
		
	}

	public TransactionalReportingDto getData(
			TransactionalReqDto request) {

		List<String> gstin = request.getGstin();
		String taxPeriod = request.getTaxPeriod();
		String sourceId = request.getSourceId();
		List<String> status = request.getStatus();
		Long entityId = request.getEntityId();
		List<TransactionalInwardEntity> enities = findAllDataByCondition(
				gstin, taxPeriod, sourceId, status, entityId);
		List<TransactionalDto> listObj = new ArrayList<>();
		enities.forEach(obj -> {
			TransactionalDto dto = new TransactionalDto();

			dto.setGstin(obj.getGstin());
			dto.setTaxPeriod(obj.getTaxPeriod());
			dto.setSourceId(obj.getSourceId());
			dto.setStatus(obj.getDerivedStatus());
			dto.setDocType(obj.getDocType());
		//	dto.setSupplyType(obj.getSupplyType());
			dto.setErpNumOfInvs(obj.getErpNumOfInvoices());
			dto.setErpTotalInvValue(obj.getErpTotalInvValue() != null
					? obj.getErpTotalInvValue().toString() : ZERO);
			dto.setErpAccValue(obj.getErpAssessableValue() != null
					? obj.getErpAssessableValue().toString() : ZERO);
			dto.setErpIgst(obj.getErpIgstAmt() != null
					? obj.getErpIgstAmt().toString() : ZERO);
			dto.setErpSgst(obj.getErpSgstAmt() != null
					? obj.getErpSgstAmt().toString() : ZERO);
			dto.setErpCgst(obj.getErpCgstAmt() != null
					? obj.getErpCgstAmt().toString() : ZERO);
			dto.setErpCess(obj.getErpCessAmt() != null
					? obj.getErpCessAmt().toString() : ZERO);
			dto.setCloudNumOfInvs(obj.getCloudNumOfInvoices());
			dto.setCloudTotalInvValue(obj.getCloudTotalInvValue() != null
					? obj.getCloudTotalInvValue().toString() : ZERO);
			dto.setCloudAccValue(obj.getCloudAssessableValue() != null
					? obj.getCloudAssessableValue().toString() : ZERO);
			dto.setCloudIgst(obj.getCloudIgstAmt() != null
					? obj.getCloudIgstAmt().toString() : ZERO);
			dto.setCloudSgst(obj.getCloudSgstAmt() != null
					? obj.getCloudSgstAmt().toString() : ZERO);
			dto.setCloudCgst(obj.getCloudCgstAmt() != null
					? obj.getCloudCgstAmt().toString() : ZERO);
			dto.setCloudCess(obj.getCloudCessAmt() != null
					? obj.getCloudCessAmt().toString() : ZERO);
			listObj.add(dto);
		});
		TransactionalReportingDto dto1 = new TransactionalReportingDto();
		if (enities != null && enities.size() > 0) {
			dto1.setDateOfReporting(enities.get(0).getDateOfReporting());
		}
		dto1.setData(listObj);
		return dto1;

	}

	@SuppressWarnings("unchecked")
	private List<TransactionalInwardEntity> findAllDataByCondition(
			List<String> gstin, String taxPeriod, String sourceId,
			List<String> status, Long entityId) {

		StringBuilder criteria = new StringBuilder();
		if (gstin != null && gstin.size() > 0) {
			criteria.append("AND doc.gstin IN(:gstin) ");
		}
		if (!StringUtils.isEmpty(taxPeriod)) {
			criteria.append("AND doc.taxPeriod = :taxPeriod ");
		}
		if (!StringUtils.isEmpty(sourceId)) {
			criteria.append("AND doc.sourceId = :sourceId ");
		}
		if (status != null && status.size() > 0) {
			criteria.append("AND doc.derivedStatus IN(:derivedStatus) ");
		}
		if (entityId != null) {
			criteria.append("AND doc.entityId = :entityId ");
		}
		String sql = "SELECT doc FROM TransactionalInwardEntity doc WHERE "
				+ "doc.isDelete = FALSE " + criteria
				+ "ORDER BY doc.gstin DESC";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SQL query1 is {} ", sql);
		}
		Query query = entityManager.createQuery(sql);
		/**
		 * setting the values based on condition the key values in the
		 * setParameter should be same as index names. index names are
		 * dynamically coming from the criteria.
		 */
		if (gstin != null && gstin.size() > 0) {
			query.setParameter("gstin", gstin);
		}
		if (!StringUtils.isEmpty(taxPeriod)) {
			query.setParameter("taxPeriod", taxPeriod);
		}
		if (!StringUtils.isEmpty(sourceId)) {
			query.setParameter("sourceId", sourceId);
		}
		if (status != null && status.size() > 0) {
			query.setParameter("derivedStatus", status);
		}
		if (entityId != null) {
			query.setParameter("entityId", entityId);
		}
		return query.getResultList();
	}

	private String getDerivedStatus(String status) {

		if ("0".equals(status)) {
			return "Not in scope of raw upload";
		}
		if ("1".equals(status)) {
			return "Compliance on-hold";
		}
		if ("2".equals(status)) {
			return "Ready to push";
		}
		if ("5".equals(status)) {
			return "DQ error on erp";
		}
		if ("3".equals(status) || "6".equals(status) || "7".equals(status)) {
			// 3 (Push success but not verified) /6 (Push in suspense) /7 (Retry
			// due to JSON error)
			return "Push pending verification";
		}
		if ("4".equals(status) || "8".equals(status)) {
			// 4 (auth token erorr), 8 (invoice level JSON error)
			return "Push error";
		}
		if ("9".equals(status)) {
			return "Asp error";
		}
		if ("10".equals(status)) {
			return "Asp processed";
		}
		if ("11".equals(status)) {
			return "Gstn error";
		}
		if ("12".equals(status)) {
			return "Filed at GSTN";
		}
		return null;
	}

}
