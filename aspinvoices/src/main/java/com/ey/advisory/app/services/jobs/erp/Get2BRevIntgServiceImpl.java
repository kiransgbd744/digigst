package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgInvDto;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgSummaryDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Get2BRevIntgServiceImpl")
public class Get2BRevIntgServiceImpl implements Get2BRevIntgService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Get2BRevIntgInvDto> get2BTransactionalData(Long requestId,
			Integer chunkId, String entityName, String entityPan) {

		StoredProcedureQuery dispProc = entityManager
				.createStoredProcedureQuery("USP_AUTO2B_ERP_DISP_CHUNK");

		dispProc.registerStoredProcedureParameter("P_REQUEST_CONFIG_ID",
				Long.class, ParameterMode.IN);

		dispProc.setParameter("P_REQUEST_CONFIG_ID", requestId);

		dispProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
				Integer.class, ParameterMode.IN);

		dispProc.setParameter("P_CHUNK_VALUE", chunkId);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Data proc Executed"
					+ " USP_AUTO_2APR_DISP_CHUNK_ERP_GSTIN: configId '%d', "
					+ "noOfChunk %d ", requestId, chunkId);
			LOGGER.debug(msg);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> records = dispProc.getResultList();

		List<Get2BRevIntgInvDto> gstr2bDataList = new ArrayList<>();

		if (records != null && !records.isEmpty()) {

			gstr2bDataList = records.stream()
					.map(o -> convert(o, entityName, entityPan))
					.collect(Collectors.toCollection(ArrayList::new));
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Row count: '%s'",
						gstr2bDataList.size());
				LOGGER.debug(msg);
			}
		}

		return gstr2bDataList;
	}

	@Override
	public List<Get2BRevIntgSummaryDto> get2BSummaryData(String gstin,
			String taxPeriod, String entityName, String entityPan) {

		List<Get2BRevIntgSummaryDto> resp = new ArrayList<>();
		String msg = String.format("Inside Gstr2BDetailsDaoImpl"
				+ ".getDetailsResp() method {} gstins %s, " + "TaxPeriod %s ",
				gstin, taxPeriod);
		LOGGER.debug(msg);

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(
						"USP_GETGSTR2B_REV_FEED_TBLSMRY_RPT");

		storedProc.registerStoredProcedureParameter("P_gstin_LIST",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_gstin_LIST", String.join(",", gstin));

		storedProc.registerStoredProcedureParameter("FROM_RET_PERIOD",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("FROM_RET_PERIOD", Integer.valueOf(taxPeriod));

		storedProc.registerStoredProcedureParameter("TO_RET_PERIOD",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("TO_RET_PERIOD", Integer.valueOf(taxPeriod));

		@SuppressWarnings("unchecked")
		List<Object[]> list = storedProc.getResultList();

		LOGGER.debug("Converting Query And converting to List BEGIN");
		resp = list.stream()
				.map(o -> convertSummary(o, taxPeriod, entityName, entityPan))
				.collect(Collectors.toCollection(ArrayList::new));
		LOGGER.debug("Converting Query And converting to List END");
		return resp;
	}

	private Get2BRevIntgInvDto convert(Object[] arr, String entityName,
			String entityPan) {

		Get2BRevIntgInvDto obj = new Get2BRevIntgInvDto();

		obj.setBillOfEntryDate((arr[22] != null) ? arr[22].toString() : null);
		obj.setBillOfEntryNumber((arr[21] != null) ? arr[21].toString() : null);
		obj.setBoeAmendmentFlag((arr[23] != null) ? arr[23].toString() : null);
		obj.setBoeRecvDateGstin((arr[19] != null) ? arr[19].toString() : null);
		obj.setBoeRefDateIceGate((arr[18] != null) ? arr[18].toString() : null);
		obj.setCessAmount((arr[13] != null) ? (BigDecimal) arr[13] : null);
		obj.setCgstAmount((arr[11] != null) ? (BigDecimal) arr[11] : null);
		obj.setDate2bGenerationDate(
				(arr[29] != null) ? arr[29].toString() : null);
		obj.setDifferentialPercentage(
				(arr[32] != null) ? (BigDecimal) arr[32] : null);
		obj.setDocumentDate((arr[7] != null) ? arr[7].toString() : null);
		obj.setDocumentNumber((arr[6] != null) ? arr[6].toString() : null);
		obj.setDocumentType((arr[4] != null) ? arr[4].toString() : null);
		obj.setGstrFilingDate((arr[31] != null) ? arr[31].toString() : null);
		obj.setGstrFilingPeriod((arr[30] != null) ? arr[30].toString() : null);
		obj.setIgstAmount((arr[10] != null) ? (BigDecimal) arr[10] : null);
		obj.setInvoiceValue((arr[14] != null) ? (BigDecimal) arr[14] : null);
		obj.setItcAvailability((arr[35] != null) ? arr[35].toString() : null);
		obj.setLineNumber((arr[17] != null) ? arr[17].toString() : null);
		obj.setOriginalDocumentDate(
				(arr[25] != null) ? arr[25].toString() : null);
		obj.setOriginalDocumentNumber(
				(arr[24] != null) ? arr[24].toString() : null);
		obj.setOriginalDocumentType(
				(arr[26] != null) ? arr[26].toString() : null);
		obj.setOriginalInvoiceDate(
				(arr[28] != null) ? arr[28].toString() : null);
		obj.setOriginalInvoiceNumber(
				(arr[27] != null) ? arr[27].toString() : null);
		obj.setPortCode((arr[20] != null) ? arr[20].toString() : null);
		obj.setPos((arr[15] != null) ? arr[15].toString() : null);
		obj.setReasonForItcAvailability(
				(arr[36] != null) ? arr[36].toString() : null);
		obj.setRecipientGSTIN((arr[1] != null) ? arr[1].toString() : null);
		obj.setReturnPeriod((arr[0] != null) ? arr[0].toString() : null);
		obj.setReverseChargeFlag((arr[33] != null) ? arr[33].toString() : null);
		obj.setSgstAmount((arr[12] != null) ? (BigDecimal) arr[12] : null);
		obj.setStateName((arr[16] != null) ? arr[16].toString() : null);
		obj.setSupplierGSTIN((arr[2] != null) ? arr[2].toString() : null);
		obj.setSupplierName((arr[3] != null) ? arr[3].toString() : null);
		obj.setSupplyType((arr[5] != null) ? arr[5].toString() : null);
		obj.setTaxableValue((arr[8] != null) ? (BigDecimal) arr[8] : null);
		obj.setTaxRate((arr[9] != null) ? (BigDecimal) arr[9] : null);
		obj.setSrcTypeOfIrn((arr[37] != null) ? arr[37].toString() : null);
		obj.setIrnNum((arr[38] != null) ? arr[38].toString() : null);
		obj.setIrnGenDate((arr[39] != null) ? arr[39].toString() : null);
		obj.setTableType((arr[40] != null) ? arr[40].toString() : null);
		obj.setInvStatus((arr[41] != null) ? arr[41].toString() : null);
		obj.setEntityName(entityName);
		obj.setEntityPan(entityPan);
		return obj;
	}

	private Get2BRevIntgSummaryDto convertSummary(Object[] o, String retPeriod,
			String entityName, String entityPan) {

		Get2BRevIntgSummaryDto dto = new Get2BRevIntgSummaryDto();

		BigDecimal zero = BigDecimal.ZERO;

		dto.setRgstin((String) o[0]);

		dto.setNonAvailItcCess(o[16] != null ? (BigDecimal) o[16] : zero);
		dto.setNonAvailItcCgst(o[14] != null ? (BigDecimal) o[14] : zero);
		dto.setNonAvailItcIgst(o[13] != null ? (BigDecimal) o[13] : zero);
		dto.setNonAvailItcSgst(o[15] != null ? (BigDecimal) o[15] : zero);

		dto.setCount(o[3] != null ? (Integer) o[3] : 0);

		dto.setAvailItcCess(o[12] != null ? (BigDecimal) o[12] : zero);
		dto.setAvailItcCgst(o[10] != null ? (BigDecimal) o[10] : zero);
		dto.setAvailItcIgst(o[9] != null ? (BigDecimal) o[9] : zero);
		dto.setAvailItcSgst(o[11] != null ? (BigDecimal) o[11] : zero);

		dto.setDescription((String) o[1]);
		dto.setTaxablevalue(o[4] != null ? (BigDecimal) o[4] : zero);

		dto.setTotalTaxCess(o[8] != null ? (BigDecimal) o[8] : zero);
		dto.setTotalTaxCgst(o[6] != null ? (BigDecimal) o[6] : zero);
		dto.setTotalTaxIgst(o[5] != null ? (BigDecimal) o[5] : zero);
		dto.setTotalTaxSgst(o[7] != null ? (BigDecimal) o[7] : zero);
		dto.setRetPeriod(
				retPeriod.substring(4).concat(retPeriod.substring(0, 4)));
		dto.setEntityName(entityName);
		dto.setEntityPan(entityPan);
		return dto;
	}
}
