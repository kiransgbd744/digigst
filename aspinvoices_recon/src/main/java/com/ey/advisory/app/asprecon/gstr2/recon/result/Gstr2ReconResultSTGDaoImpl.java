package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2ReconResponseUploadEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2ReconResultSTGDaoImpl")
public class Gstr2ReconResultSTGDaoImpl implements Gstr2ReconResultSTGDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override

	public List<Gstr2ReconResponseUploadEntity> getStgData(Long prId, Long a2Id,
			Long reconLinkId, String fmResponse, String taxPeriod3B,
			String action) {

		String queryString = createQueryString();

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for Gstr2ReconResultSTGDaoImpl"
							+ "on FilterConditions Provided By User : %s",
					queryString);
			LOGGER.debug(str);
		}

		String batchId = createBatchId(reconLinkId);

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("PR_ID", prId);

		q.setParameter("A2_ID", a2Id);

		q.setParameter("Recon_ID", reconLinkId);

		q.setParameter("BatchID", batchId);

		q.setParameter("FMResponse", fmResponse);

		q.setParameter("RspTaxPeriod3B", taxPeriod3B);
		
		q.setParameter("USERRESPONSE", action);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Gstr2ReconResponseUploadEntity> retList = list.stream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;
	}

	private String createQueryString() {

		String query = "SELECT :BatchID, "
				+ "LT.PR_Invoice_key "
				+ ",LT.A2_Invoice_key "
				+ ",CASE WHEN (UPPER(:FMResponse) = 'LOCK')  THEN '1' "
				+ "ELSE '0' END AS IsFM "
				+ ",:FMResponse "
				+ ",:RspTaxPeriod3B "
				+ ",LT.A2_Tax_Period "
				+ ",LT.PR_Tax_Period "
				+ ",LT.A2_RECIPIENT_GSTIN "
				+ ",LT.PR_RECIPIENT_GSTIN "
				+ ",LT.A2_SUPPLIER_GSTIN "
				+ ",LT.PR_SUPPLIER_GSTIN "
				+ ",CASE WHEN LT.A2_DOC_TYPE = 'CR' THEN 'C' "
				+ " WHEN LT.A2_DOC_TYPE = 'DR' THEN 'D' "
				+ " ELSE LT.A2_DOC_TYPE "
				+ "END AS DocType2A "
				+ ",CASE WHEN LT.PR_DOC_TYPE = 'CR' THEN 'C' "
				+ " WHEN LT.PR_DOC_TYPE = 'DR' THEN 'D' "
				+ " ELSE LT.PR_DOC_TYPE "
				+ "END AS DocTypePR "
				+ ",LT.PR_DOC_NUM "
				+ ",LT.A2_DOC_NUM "
				+ ", CAST(LT.A2_DOC_DATE AS varchar(10)) AS DocDate2A  "
				+ ",CAST(LT.PR_DOC_DATE AS varchar(10)) AS DocDatePR "
				+ ",A2.CFS AS CfsFlag "
				+ ", CAST(PR.IGST_AMT AS varchar(25)) AS IGSTPR "
				+ ",CAST(PR.CGST_AMT AS varchar(25)) AS CGSTPR "
				+ ",CAST(PR.SGST_AMT AS varchar(25)) AS SGSTPR "
				+ ",CAST(PR.CESS_AMT AS varchar(25)) AS CESSPR "
				+ ",CAST(PR.TAXABLE_VALUE AS varchar(25)) AS TaxablePR "
				+ ",CAST(A2.IGST_AMT AS varchar(25)) AS IGST2A "
				+ ",CAST(A2.CGST_AMT AS varchar(25)) AS CGST2A "
				+ ",CAST(A2.SGST_AMT AS varchar(25)) AS SGST2A "
				+ ",CAST(A2.CESS_AMT AS varchar(25)) AS CESS2A "
				+ ",CAST(A2.TAXABLE_VALUE AS varchar(25)) AS Taxable2A "
				+ ",LT.A2_TABLE "
				+ ",CAST(LT.PR_ID AS varchar(25)) AS PRID "
				+ ",CAST(LT.A2_ID AS varchar(25)) AS A2ID"
				+ ",'RECON_RESULT_SCR' AS CreatedBy "
				+ ",CURRENT_TIMESTAMP AS CreateDTM "
				+ ",CASE WHEN (UPPER(:FMResponse) = 'LOCK' OR :RspTaxPeriod3B "
				+ "IS NOT NULL) THEN NULL ELSE CURRENT_TIMESTAMP END AS ENDDTM "
				+ ",LT.RECON_REPORT_CONFIG_ID, "
				+ ":USERRESPONSE "
				+ "FROM TBL_LINK_2A_PR LT "
				+ "LEFT JOIN ANX_INWARD_DOC_HEADER PR ON LT.PR_ID = PR.ID "
				+ "LEFT JOIN ( "
				+ "SELECT ID, CFS,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT, "
				+ "TAXABLE_VALUE,INV_TYPE,SUPPLIER_INV_NUM AS DOC_NUM, "
				+ "SUPPLIER_INV_DATE AS DOC_DATE FROM GETGSTR2A_B2B_HEADER "
				+ "UNION ALL "
				+ "SELECT ID, CFS,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT, "
				+ "TAXABLE_VALUE, "
				+ "INV_TYPE,SUPPLIER_INV_NUM AS DOC_NUM,SUPPLIER_INV_DATE AS "
				+ "DOC_DATE FROM GETGSTR2A_B2BA_HEADER "
				+ "UNION ALL "
				+ "SELECT ID, CFS,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT, "
				+ "TAXABLE_VALUE,NOTE_TYPE AS INV_TYPE,NOTE_NUMBER AS "
				+ "DOC_NUM,NOTE_DATE AS DOC_DATE FROM GETGSTR2A_CDN_HEADER "
				+ "UNION ALL "
				+ "SELECT ID, CFS,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT, "
				+ "TAXABLE_VALUE, "
				+ "NOTE_TYPE AS INV_TYPE,NOTE_NUMBER AS DOC_NUM,NOTE_DATE AS "
				+ "DOC_DATE FROM GETGSTR2A_CDNA_HEADER "
				+ ") A2 ON LT.A2_DOC_TYPE=A2.INV_TYPE AND LT.A2_ID = A2.ID AND "
				+ "LT.A2_DOC_NUM=A2.DOC_NUM AND LT.A2_DOC_DATE=A2.DOC_DATE "
				+ "WHERE LT.RECON_LINK_ID = :Recon_ID "
				+ "AND LT.PR_ID = :PR_ID "
				+ "AND LT.A2_ID = :A2_ID";
		return query;
	}

	private String createBatchId(Long reconLinkId) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		String currentDate = currentYear
				+ (currentMonth < 10 ? ("0" + currentMonth)
						: String.valueOf(currentMonth))
				+ (currentDay < 10 ? ("0" + currentDay)
						: String.valueOf(currentDay));
		String batchId = currentDate.concat(String.valueOf(reconLinkId));

		return batchId;
	}

	private Gstr2ReconResponseUploadEntity convert(Object arr[]) {

		Gstr2ReconResponseUploadEntity dto = new Gstr2ReconResponseUploadEntity();

		dto.setBatchID(arr[0] != null ? (String) arr[0] : null);
		dto.setInvoicekeyPR(arr[1] != null ? (String) arr[1] : null);
		dto.setInvoicekeyA2(arr[2] != null ? (String) arr[2] : null);
		dto.setIsFM(arr[3] != null ? (String) arr[3] : null);
		dto.setFMResponse(arr[4] != null ? (String) arr[4] : null);
		dto.setRspTaxPeriod3B(arr[5] != null ? (String) arr[5] : null);
		dto.setTaxPeriod2A(arr[6] != null ? (String) arr[6] : null);
		dto.setTaxPeriodPR(arr[7] != null ? (String) arr[7] : null);
		dto.setRGSTIN2A(arr[8] != null ? (String) arr[8] : null);
		dto.setRGSTINPR(arr[9] != null ? (String) arr[9] : null);
		dto.setSGSTIN2A(arr[10] != null ? (String) arr[10] : null);
		dto.setSGSTINPR(arr[11] != null ? (String) arr[11] : null);
		dto.setDocType2A(arr[12] != null ? (String) arr[12] : null);
		dto.setDocTypePR(arr[13] != null ? (String) arr[13] : null);
		dto.setDocumentNumberPR(arr[14] != null ? (String) arr[14] : null);
		dto.setDocumentNumber2A(arr[15] != null ? (String) arr[15] : null);
		dto.setDocDate2A(arr[16] != null ? (String) arr[16] : null);
		dto.setDocDatePR(arr[17] != null ? (String) arr[17] : null);
		dto.setCfsFlag(arr[18] != null ? (String) arr[18] : null);
		dto.setIGSTPR(arr[19] != null ? (String) arr[19] : null);
		dto.setCGSTPR(arr[20] != null ? (String) arr[20] : null);
		dto.setSGSTPR(arr[21] != null ? (String) arr[21] : null);
		dto.setCESSPR(arr[22] != null ? (String) arr[22] : null);
		dto.setTaxablePR(arr[23] != null ? (String) arr[23] : null);
		dto.setIGST2A(arr[24] != null ? (String) arr[24] : null);
		dto.setCGST2A(arr[25] != null ? (String) arr[25] : null);
		dto.setSGST2A(arr[26] != null ? (String) arr[26] : null);
		dto.setCESS2A(arr[27] != null ? (String) arr[27] : null);
		dto.setTaxable2A(arr[28] != null ? (String) arr[28] : null);
		dto.setTableType(arr[29] != null ? (String) arr[29] : null);
		dto.setIDPR(arr[30] != null ? (String) arr[30] : null);
		dto.setID2A(arr[31] != null ? (String) arr[31] : null);
		dto.setCreatedBy(arr[32] != null ? (String) arr[32] : null);
		if (arr[33] != null) {
			Timestamp t = (Timestamp) arr[33];
			dto.setCreateDTM(t.toLocalDateTime());
		}
		if (arr[34] != null) {
			Timestamp t = (Timestamp) arr[34];
			dto.setEndDtm(t.toLocalDateTime().toString());
		}
		//dto.setEndDtm(arr[34] != null ? (String) arr[34] : null);
		dto.setConfigId(arr[35] != null ? (String) arr[35].toString() : null);
		dto.setUserResponse(
				arr[36] != null ? (String) arr[36].toString() : null);

		return dto;

	}
}