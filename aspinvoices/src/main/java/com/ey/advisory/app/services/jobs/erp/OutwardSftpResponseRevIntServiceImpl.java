/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.core.dto.EinvEwbDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("OutwardSftpResponseRevIntServiceImpl")
public class OutwardSftpResponseRevIntServiceImpl
		implements OutwardSftpResponseRevIntService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardSftpResponseRevIntServiceImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String NEWFARMATTER = "dd/MM/yyyy hh:mm:ss a";

	@Override
	public List<EinvEwbDto> getEinvEwbDetails(Long fileId) {

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" AND HDR.FILE_ID = :fileId");
		}
		String queryStr = createEInvdownloadQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();

		List<EinvEwbDto> resultSet = list.parallelStream().map(o -> {
			try {
				return convertEInv(o);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toCollection(ArrayList::new));

		return resultSet;
	}

	private EinvEwbDto convertEInv(Object[] arr) throws ParseException {
		EinvEwbDto obj = new EinvEwbDto();

		String einvAspErrDesc = null;
		String einvAspErrorCodes = null;
		String aspErrDesc = null;

		obj.setCompanyCode(arr[0] != null ? arr[0].toString() : "");
		obj.setDocumentType(arr[1] != null ? arr[1].toString() : "");
		obj.setAccountingVoucherNumber(arr[2] != null ? arr[2].toString() : "");
		if (arr[3] != null) {
			String fyYear = arr[3].toString();
			String subFyYear = fyYear.substring(0, 4);
			obj.setFiscalYear(subFyYear);
		} else {
			obj.setFiscalYear("");
		}
		obj.setSupplierGstin(arr[4] != null ? arr[4].toString() : "");
		obj.setDocumentNumber(arr[5] != null ? arr[5].toString() : "");
		obj.setDocumentDate(arr[6] != null ? arr[6].toString() : "");
		obj.setEinvoicingStatus(arr[7] != null ? arr[7].toString() : "");
		obj.setAcknowledgementNumber(arr[8] != null ? arr[8].toString() : "");
		if (arr[9] != null) {

			Timestamp timeStamp1 = (Timestamp) arr[9];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern(NEWFARMATTER);
			String formatedDate = localDT1.format(formatter);

			obj.setAcknowledgementDate(formatedDate);
		} else {
			obj.setAcknowledgementDate("");
		}

		obj.setiRNNumber(arr[10] != null ? arr[10].toString() : "");
		// obj.setSignedInvoiceData(arr[11] != null ? arr[11].toString() :
		// null);
		obj.setSignedInvoiceData("");
		obj.setSignedQRCodeData(arr[12] != null ? arr[12].toString() : "");
		// obj.setqRData(arr[13] != null ? arr[13].toString() : null);
		// obj.setFormattedQRData(arr[14] != null ? arr[14].toString() : null);
		obj.setEwbNo(arr[15] != null ? arr[15].toString() : "");
		if (arr[16] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[16];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern(NEWFARMATTER);
			String formatedDate = localDT1.format(formatter);

			obj.setEwbDate(formatedDate);
		} else {
			obj.setEwbDate("");
		}
		if (arr[17] != null) {

			Timestamp timeStamp1 = (Timestamp) arr[17];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern(NEWFARMATTER);
			String formatedDate = localDT1.format(formatter);
			obj.setEwbValidityEnddate(formatedDate);
		} else {
			obj.setEwbValidityEnddate("");
		}
		obj.setnICDistance(arr[18] != null ? arr[18].toString() : "");
		obj.seteWBErrorCode(arr[19] != null ? arr[19].toString() : "");
		obj.seteWBErrormessage(arr[20] != null ? arr[20].toString() : "");
		obj.seteWBInformationCode(arr[21] != null ? arr[21].toString() : "");
		obj.seteWBInformationmessage(arr[22] != null ? arr[22].toString() : "");
		if (arr[23] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[23];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern(NEWFARMATTER);
			String formatedDate = localDT1.format(formatter);

			obj.setCancellationDate(formatedDate);
		} else {
			obj.setCancellationDate("");
		}
		String einvErrorcode = arr[24] != null ? arr[24].toString() : null;
		String aspErrCode = arr[29] != null ? String.valueOf(arr[29]) : null;

		if (einvErrorcode != null) {
			einvAspErrorCodes = einvErrorcode;
		}
		if (aspErrCode != null) {
			einvAspErrorCodes = aspErrCode;
		}
		if (einvErrorcode != null && aspErrCode != null) {
			einvAspErrorCodes = einvErrorcode.concat(",").concat(aspErrCode);
		}
		obj.seteINVErrorCode(
				einvAspErrorCodes != null ? einvAspErrorCodes.toString() : "");

		String einvErrDesc = arr[25] != null ? arr[25].toString() : null;
		if (!Strings.isNullOrEmpty(aspErrCode)) {
			String[] errorCodes = aspErrCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			aspErrDesc = ErrorMasterUtil.getErrorDesc(errCodeList, "OUTWARD");
		}
		if (einvErrDesc != null) {
			einvAspErrDesc = einvErrDesc;
		}
		if (aspErrDesc != null) {
			einvAspErrDesc = aspErrDesc;
		}
		if (einvErrDesc != null && aspErrDesc != null) {
			einvAspErrDesc = einvErrDesc.concat(",").concat(aspErrDesc);
		}
		obj.seteINVErrormessage(
				einvAspErrDesc != null ? einvAspErrDesc.toString() : "");
		obj.seteINVInformationCode(arr[26] != null ? arr[26].toString() : "");
		obj.seteINVInformationmessage(
				arr[27] != null ? arr[27].toString() : "");
		obj.setFileId(arr[28] != null ? arr[28].toString() : "");
		// obj.setAspErrorCode(errCode);
		// obj.setAspErrorDes(errDesc != null ? errDesc : "");
		return obj;
	}

	private String createEInvdownloadQueryString(String buildQuery) {

		return "select HDR.COMPANY_CODE, HDR.DOC_TYPE, "
				+ "TO_CHAR(HDR.ACCOUNTING_VOUCHER_NUM) AS ACCOUNTING_VOUCHER_NUM , "
				+ "HDR.FI_YEAR, HDR.SUPPLIER_GSTIN, HDR.DOC_NUM, HDR.DOC_DATE, "
				+ "case when HDR.IRN_STATUS = 1 then 'Not Opted'"
				+ "when HDR.IRN_STATUS = 2 then 'Not Applicable'"
				+ "when HDR.IRN_STATUS = 3 then 'Pending'"
				+ "when HDR.IRN_STATUS = 4 then 'Generation Error'"
				+ "when HDR.IRN_STATUS = 5 then 'Generated'"
				+ "when HDR.IRN_STATUS = 6 then 'Cancelled'"
				+ "when HDR.IRN_STATUS = 7 then 'ASP Error'"
				+ "when HDR.IRN_STATUS = 8 then 'Duplicate_Irn'"
				+ "WHEN HDR.IRN_STATUS = 9 then'Inprogress Cancellation'"
				+ "WHEN HDR.IRN_STATUS = 10 then'Pushed to NIC'"
				+ "WHEN HDR.IRN_STATUS = 13 then'Already Generated by User' "
				+ "END AS EINV_STATUS, "
				+ "EINV.ACK_NUM ,EINV.ACK_DATE,EINV.IRN, EINV.SIGNED_INV, "
				+ "EINV.SIGNED_QR, EINV.QR_CODE, EINV.FORMATTED_QR_CODE, "
				+ "EWB.EWB_NUM, EWB.EWB_DATE, EWB.VALID_UPTO, "
				+ "EWB.REMAINING_DISTANCE, HDR.EWB_ERROR_CODE, HDR.EWB_ERROR_DESC, "
				+ " '' AS EWB_INFO_CODE, '' AS EWB_INFO_DESC, EWB.CANCELLATION_DATE, "
				+ "HDR.EINV_ERROR_CODE, HDR.EINV_ERROR_DESC, '' AS EINV_INFO_CODE, "
				+ " '' AS EINV_INFO_DESC,HDR.FILE_ID,HDR.ERROR_CODES FROM ANX_OUTWARD_DOC_HEADER "
				+ "HDR LEFT OUTER JOIN EINV_MASTER EINV "
				+ "ON HDR.IRN_RESPONSE = EINV.IRN "
				+ "LEFT OUTER JOIN EWB_MASTER EWB "
				+ "ON HDR.EWB_NO_RESP = EWB.EWB_NUM "
				+ "WHERE HDR.IS_DELETE = FALSE AND IS_SUBMITTED = FALSE "
				+ buildQuery + " ORDER BY ID ASC  ";
	}
}
