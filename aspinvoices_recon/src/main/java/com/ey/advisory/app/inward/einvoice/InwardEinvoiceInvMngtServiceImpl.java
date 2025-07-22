package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GetIrnListEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.base.Strings;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.Jain
 *
 */
@Slf4j
@Component("InwardEinvoiceInvMngtServiceImpl")
public class InwardEinvoiceInvMngtServiceImpl
		implements InwardEinvoiceInvMngtService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GetIrnListingRepository")
	public GetIrnListingRepository listingRepo;

	@Override
	public Pair<List<InwardEinvoiceInvMngtRespDto>, Integer> findTableData(
			InwardEinvoiceInvMngtReqDto dto, int pageNum, int pageSize) {
		List<InwardEinvoiceInvMngtRespDto> respDto = new ArrayList<>();
		Integer totalCnt = 0;
		try {

			String condtion = queryCondition(dto);
			String queryString = createQuery(condtion, true);

			Query q = entityManager.createNativeQuery(queryString);

			q.setParameter("gstins", dto.getGstins());

			if ("Month".equalsIgnoreCase(dto.criteria)) {
				if (dto.getFrmTaxPrd() != null
						&& (!dto.getFrmTaxPrd().isEmpty())) {
					q.setParameter("frmTaxPrd",
							Integer.valueOf(dto.getFrmTaxPrd().substring(2, 6)
									+ dto.getFrmTaxPrd().substring(0, 2)));
				}

				if (dto.getToTaxPrd() != null
						&& (!dto.getToTaxPrd().isEmpty())) {
					q.setParameter("toTaxPrd",
							Integer.valueOf(dto.getToTaxPrd().substring(2, 6)
									+ dto.getToTaxPrd().substring(0, 2)));
				}

			} else {
				if (dto.getFromDate() != null
						&& (!dto.getFromDate().isEmpty())) {
					q.setParameter("fromDate", dto.getFromDate());
				}

				if (dto.getToDate() != null && (!dto.getToDate().isEmpty())) {
					q.setParameter("toDate", dto.getToDate());
				}
			}

			if (!CollectionUtils.isEmpty(dto.getSupplyType())) {
				q.setParameter("supplyType", dto.getSupplyType());
			}

			if (!CollectionUtils.isEmpty(dto.getIrnStatus())) {
				q.setParameter("irnSts", dto.getIrnStatus());
			}

			if (!Strings.isNullOrEmpty(dto.getIrn())) {
				q.setParameter("irn", dto.getIrn());
			}

			if (!Strings.isNullOrEmpty(dto.getDocNum())) {
				q.setParameter("docNum", dto.getDocNum());
			}

			if (!Strings.isNullOrEmpty(dto.getVendrGstin())) {
				q.setParameter("vndrGstin", dto.getVendrGstin());
			}

			q.setParameter("pageNum", (pageNum * pageSize));

			q.setParameter("pageSize", pageSize);

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();

			respDto = list.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			
			entityManager.clear();
			
			String queryString1 = createQuery(condtion, false);

			Query q1 = entityManager.createNativeQuery(queryString1);

			q1.setParameter("gstins", dto.getGstins());

			if ("Month".equalsIgnoreCase(dto.criteria)) {
				if (dto.getFrmTaxPrd() != null
						&& (!dto.getFrmTaxPrd().isEmpty())) {
						q1.setParameter("frmTaxPrd",
								Integer.valueOf(dto.getFrmTaxPrd().substring(2, 6)
										+ dto.getFrmTaxPrd().substring(0, 2)));
				}

				if (dto.getToTaxPrd() != null
						&& (!dto.getToTaxPrd().isEmpty())) {
					q1.setParameter("toTaxPrd",
							Integer.valueOf(dto.getToTaxPrd().substring(2, 6)
									+ dto.getToTaxPrd().substring(0, 2)));
				}

			} else {
				if (dto.getFromDate() != null
						&& (!dto.getFromDate().isEmpty())) {
					q1.setParameter("fromDate", dto.getFromDate());
				}

				if (dto.getToDate() != null && (!dto.getToDate().isEmpty())) {
					q1.setParameter("toDate", dto.getToDate());
				}
			}

			if (!CollectionUtils.isEmpty(dto.getSupplyType())) {
				q1.setParameter("supplyType", dto.getSupplyType());
			}

			if (!CollectionUtils.isEmpty(dto.getIrnStatus())) {
				q1.setParameter("irnSts", dto.getIrnStatus());
			}

			if (!Strings.isNullOrEmpty(dto.getIrn())) {
				q1.setParameter("irn", dto.getIrn());
			}

			if (!Strings.isNullOrEmpty(dto.getDocNum())) {
				q1.setParameter("docNum", dto.getDocNum());
			}

			if (!Strings.isNullOrEmpty(dto.getVendrGstin())) {
				q1.setParameter("vndrGstin", dto.getVendrGstin());
			}

			@SuppressWarnings("unchecked")
			List<Long> totalCntlist = q1.getResultList();
			
			LOGGER.debug(" totalCntlist {} ",totalCntlist );
			if (!Collections.isEmpty(totalCntlist)) {

				LOGGER.debug(" Inside if block");
				int obj = Integer.parseInt(totalCntlist.get(0).toString());
				totalCnt = Integer.valueOf(obj);
				LOGGER.debug(" totalCnt {} ",totalCnt);
			}

		} catch (Exception ex) {
			String msg = String.format(
					"Error Occured while executing INV Mngt Screen query", ex);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return new Pair<>(respDto, totalCnt);
	}

	private String queryCondition(InwardEinvoiceInvMngtReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin InwardEinvoiceInvMngtServiceImpl .queryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();

		if ("Month".equalsIgnoreCase(reqDto.criteria)) {
			if (reqDto.getFrmTaxPrd() != null
					&& (!reqDto.getFrmTaxPrd().isEmpty())) {
				condition1.append(
						" AND LST.DERIVED_MONTHYEAR BETWEEN :frmTaxPrd ");
			}

			if (reqDto.getToTaxPrd() != null
					&& (!reqDto.getToTaxPrd().isEmpty())) {
				condition1.append(" AND :toTaxPrd ");
			}

		} else {
			if (reqDto.getFromDate() != null
					&& (!reqDto.getFromDate().isEmpty())) {
				condition1.append(
						" AND TO_VARCHAR(LST.ACK_DATE,'YYYY-MM-DD') BETWEEN :fromDate ");
			}

			if (reqDto.getToDate() != null && (!reqDto.getToDate().isEmpty())) {
				condition1.append(" AND :toDate ");
			}
		}
		if (!CollectionUtils.isEmpty(reqDto.getSupplyType())) {
			condition1.append(" AND LST.SUPPLY_TYPE IN (:supplyType) ");
		}

		if (!CollectionUtils.isEmpty(reqDto.getIrnStatus())) {
			condition1.append(" AND LST.IRN_STATUS IN (:irnSts) ");
		}

		if (!Strings.isNullOrEmpty(reqDto.getIrn())) {
			condition1.append(" AND LST.IRN  = :irn ");
		}

		if (!Strings.isNullOrEmpty(reqDto.getDocNum())) {
			condition1.append(" AND LST.DOC_NUM = :docNum ");
		}

		if (!Strings.isNullOrEmpty(reqDto.getVendrGstin())) {
			condition1.append(" AND LST.SUPPLIER_GSTIN = :vndrGstin ");
		}

		return condition1.toString();
	}

	private String createQuery(String condition, boolean flag) {

		StringBuilder query = new StringBuilder();
		if (flag) {
			query.append(" SELECT * FROM  ");

		} else {
			query.append("select Count(*) FROM ");

		}

		query.append(
				" ( SELECT LST.ID, LST.CUST_GSTIN,LST.SUPPLIER_GSTIN, LST.DOC_NUM,  "
						+ " LST.DOC_DATE, LST.DOC_TYPE,LST.SUPPLY_TYPE,HDR.INV_ASSESSABLE_AMT,   "
						+ " (IFNULL(HDR.INV_IGST_AMT,0)+IFNULL(HDR.INV_CGST_AMT,0)+  "
						+ " IFNULL(HDR.INV_SGST_AMT,0)+IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS TOTALTAX, "
						+ "  HDR.INV_IGST_AMT,HDR.INV_CGST_AMT,HDR.INV_SGST_AMT,  "
						+ " (IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0) +  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS CESSAMT, "
						+ "  LST.TOT_INV_AMT,LST.IRN,LST.IRN_STATUS,LST.ACK_NO,LST.ACK_DATE, "
						+ " LST.EWB_NUM,LST.EWB_DATE,LST.CANCEL_DATE, HDR.IRP_NAME,  LST.PR_TAGGING, LST. PR_TAGGING_TIME_STATUS, LST.QR_CODE_VALIDATED, LST.QR_CODE_VALIDATION_RESULT, LST.QR_CODE_MATCH_COUNT,LST.QR_CODE_MISMATCH_COUNT,LST.QR_CODE_MISMATCH_ATTRIBUTES   "
						+ " FROM  TBL_GETIRN_LIST LST INNER JOIN TBL_GETIRN_B2B_HEADER HDR ON LST.BATCH_ID = HDR.BATCH_ID AND LST.IRN=HDR.IRN  AND LST.IRN_STATUS=HDR.IRN_STATUS  "
						+ " WHERE LST.CUST_GSTIN IN (:gstins)" + condition
						+ " AND LST.IS_DELETE = FALSE "
						+ " AND UPPER(LST.GET_DETAIL_IRN_STATUS) = 'SUCCESS' "
						+ " UNION ALL"
						+ " SELECT LST.ID, LST.CUST_GSTIN,LST.SUPPLIER_GSTIN, LST.DOC_NUM,  "
						+ " LST.DOC_DATE, LST.DOC_TYPE,LST.SUPPLY_TYPE,HDR.INV_ASSESSABLE_AMT,   "
						+ " (IFNULL(HDR.INV_IGST_AMT,0)+IFNULL(HDR.INV_CGST_AMT,0)+  "
						+ " IFNULL(HDR.INV_SGST_AMT,0)+IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS TOTALTAX, "
						+ "  HDR.INV_IGST_AMT,HDR.INV_CGST_AMT,HDR.INV_SGST_AMT,  "
						+ " (IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0) +  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS CESSAMT, "
						+ "  LST.TOT_INV_AMT,LST.IRN,LST.IRN_STATUS,LST.ACK_NO,LST.ACK_DATE, "
						+ " LST.EWB_NUM,LST.EWB_DATE,LST.CANCEL_DATE,HDR.IRP_NAME , LST.PR_TAGGING, LST. PR_TAGGING_TIME_STATUS, LST.QR_CODE_VALIDATED, LST.QR_CODE_VALIDATION_RESULT, LST.QR_CODE_MATCH_COUNT,LST.QR_CODE_MISMATCH_COUNT,LST.QR_CODE_MISMATCH_ATTRIBUTES "
						+ " FROM  TBL_GETIRN_LIST LST INNER JOIN TBL_GETIRN_SEZWP_HEADER HDR ON LST.BATCH_ID = HDR.BATCH_ID AND LST.IRN=HDR.IRN  AND LST.IRN_STATUS=HDR.IRN_STATUS  "
						+ " WHERE LST.CUST_GSTIN IN (:gstins)" + condition
						+ " AND LST.IS_DELETE = FALSE "
						+ " AND UPPER(LST.GET_DETAIL_IRN_STATUS) = 'SUCCESS' "
						+ " UNION ALL "
						+ " SELECT LST.ID, LST.CUST_GSTIN,LST.SUPPLIER_GSTIN, LST.DOC_NUM,  "
						+ " LST.DOC_DATE, LST.DOC_TYPE,LST.SUPPLY_TYPE,HDR.INV_ASSESSABLE_AMT,   "
						+ " (IFNULL(HDR.INV_IGST_AMT,0)+IFNULL(HDR.INV_CGST_AMT,0)+  "
						+ " IFNULL(HDR.INV_SGST_AMT,0)+IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS TOTALTAX, "
						+ "  HDR.INV_IGST_AMT,HDR.INV_CGST_AMT,HDR.INV_SGST_AMT,  "
						+ " (IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0) +  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS CESSAMT, "
						+ "  LST.TOT_INV_AMT,LST.IRN,LST.IRN_STATUS,LST.ACK_NO,LST.ACK_DATE, "
						+ " LST.EWB_NUM,LST.EWB_DATE,LST.CANCEL_DATE, HDR.IRP_NAME , LST.PR_TAGGING, LST. PR_TAGGING_TIME_STATUS,  LST.QR_CODE_VALIDATED, LST.QR_CODE_VALIDATION_RESULT, LST.QR_CODE_MATCH_COUNT,LST.QR_CODE_MISMATCH_COUNT,LST.QR_CODE_MISMATCH_ATTRIBUTES "
						+ " FROM  TBL_GETIRN_LIST LST INNER JOIN TBL_GETIRN_SEZWOP_HEADER HDR ON LST.BATCH_ID = HDR.BATCH_ID AND LST.IRN=HDR.IRN  AND LST.IRN_STATUS=HDR.IRN_STATUS  "
						+ " WHERE LST.CUST_GSTIN IN (:gstins)" + condition
						+ " AND LST.IS_DELETE = FALSE "
						+ " AND UPPER(LST.GET_DETAIL_IRN_STATUS) = 'SUCCESS' "
						+ " UNION ALL "
						+ " SELECT LST.ID, LST.CUST_GSTIN,LST.SUPPLIER_GSTIN, LST.DOC_NUM,  "
						+ " LST.DOC_DATE, LST.DOC_TYPE,LST.SUPPLY_TYPE,HDR.INV_ASSESSABLE_AMT,   "
						+ " (IFNULL(HDR.INV_IGST_AMT,0)+IFNULL(HDR.INV_CGST_AMT,0)+  "
						+ " IFNULL(HDR.INV_SGST_AMT,0)+IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS TOTALTAX, "
						+ "  HDR.INV_IGST_AMT,HDR.INV_CGST_AMT,HDR.INV_SGST_AMT,  "
						+ " (IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0) +  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS CESSAMT, "
						+ "  LST.TOT_INV_AMT,LST.IRN,LST.IRN_STATUS,LST.ACK_NO,LST.ACK_DATE, "
						+ " LST.EWB_NUM,LST.EWB_DATE,LST.CANCEL_DATE, HDR.IRP_NAME ,  LST.PR_TAGGING, LST. PR_TAGGING_TIME_STATUS, LST.QR_CODE_VALIDATED, LST.QR_CODE_VALIDATION_RESULT, LST.QR_CODE_MATCH_COUNT,LST.QR_CODE_MISMATCH_COUNT,LST.QR_CODE_MISMATCH_ATTRIBUTES "
						+ " FROM  TBL_GETIRN_LIST LST INNER JOIN TBL_GETIRN_DEXP_HEADER HDR ON LST.BATCH_ID = HDR.BATCH_ID AND LST.IRN=HDR.IRN  AND LST.IRN_STATUS=HDR.IRN_STATUS  "
						+ " WHERE LST.CUST_GSTIN IN (:gstins)" + condition
						+ " AND LST.IS_DELETE = FALSE "
						+ " AND UPPER(LST.GET_DETAIL_IRN_STATUS) = 'SUCCESS' "
						+ " UNION ALL "
						+ " SELECT LST.ID, LST.CUST_GSTIN,LST.SUPPLIER_GSTIN, LST.DOC_NUM,  "
						+ " LST.DOC_DATE, LST.DOC_TYPE,LST.SUPPLY_TYPE,HDR.INV_ASSESSABLE_AMT,   "
						+ " (IFNULL(HDR.INV_IGST_AMT,0)+IFNULL(HDR.INV_CGST_AMT,0)+  "
						+ " IFNULL(HDR.INV_SGST_AMT,0)+IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS TOTALTAX, "
						+ "  HDR.INV_IGST_AMT,HDR.INV_CGST_AMT,HDR.INV_SGST_AMT,  "
						+ " (IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0) +  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS CESSAMT, "
						+ "  LST.TOT_INV_AMT,LST.IRN,LST.IRN_STATUS,LST.ACK_NO,LST.ACK_DATE, "
						+ " LST.EWB_NUM,LST.EWB_DATE,LST.CANCEL_DATE, HDR.IRP_NAME,  LST.PR_TAGGING, LST. PR_TAGGING_TIME_STATUS, LST.QR_CODE_VALIDATED, LST.QR_CODE_VALIDATION_RESULT, LST.QR_CODE_MATCH_COUNT,LST.QR_CODE_MISMATCH_COUNT,LST.QR_CODE_MISMATCH_ATTRIBUTES  "
						+ " FROM  TBL_GETIRN_LIST LST INNER JOIN TBL_GETIRN_EXPWP_HEADER HDR ON LST.BATCH_ID = HDR.BATCH_ID AND LST.IRN=HDR.IRN  AND LST.IRN_STATUS=HDR.IRN_STATUS  "
						+ " WHERE LST.CUST_GSTIN IN (:gstins)" + condition
						+ " AND LST.IS_DELETE = FALSE "
						+ " AND UPPER(LST.GET_DETAIL_IRN_STATUS) = 'SUCCESS' "
						+ " UNION ALL "
						+ " SELECT LST.ID, LST.CUST_GSTIN,LST.SUPPLIER_GSTIN, LST.DOC_NUM,  "
						+ " LST.DOC_DATE, LST.DOC_TYPE,LST.SUPPLY_TYPE,HDR.INV_ASSESSABLE_AMT,   "
						+ " (IFNULL(HDR.INV_IGST_AMT,0)+IFNULL(HDR.INV_CGST_AMT,0)+  "
						+ " IFNULL(HDR.INV_SGST_AMT,0)+IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS TOTALTAX, "
						+ "  HDR.INV_IGST_AMT,HDR.INV_CGST_AMT,HDR.INV_SGST_AMT,  "
						+ " (IFNULL(HDR.INV_CESS_ADVALOREM_AMT,0)+IFNULL(HDR.INV_CESS_SPECIFIC_AMT,0)+  "
						+ " IFNULL(HDR.INV_STATECESS_ADVALOREM_AMT,0) +  "
						+ " IFNULL(HDR.INV_STATECESS_SPECIFIC_AMT,0)) AS CESSAMT, "
						+ "  LST.TOT_INV_AMT,LST.IRN,LST.IRN_STATUS,LST.ACK_NO,LST.ACK_DATE, "
						+ " LST.EWB_NUM,LST.EWB_DATE,LST.CANCEL_DATE, HDR.IRP_NAME,  LST.PR_TAGGING, LST. PR_TAGGING_TIME_STATUS, LST.QR_CODE_VALIDATED, LST.QR_CODE_VALIDATION_RESULT, LST.QR_CODE_MATCH_COUNT,LST.QR_CODE_MISMATCH_COUNT,LST.QR_CODE_MISMATCH_ATTRIBUTES "
						+ " FROM  TBL_GETIRN_LIST LST INNER JOIN TBL_GETIRN_EXPWOP_HEADER HDR ON LST.BATCH_ID = HDR.BATCH_ID AND LST.IRN=HDR.IRN  AND LST.IRN_STATUS=HDR.IRN_STATUS  "
						+ " WHERE LST.CUST_GSTIN IN (:gstins)" + condition
						+ " AND LST.IS_DELETE = FALSE "
						+ " AND UPPER(LST.GET_DETAIL_IRN_STATUS) = 'SUCCESS' ) "
						+ " ORDER BY 1 DESC ");

		if (flag) {
			query.append(" LIMIT :pageSize OFFSET :pageNum ; ");
		}

		return query.toString();
	}

	private InwardEinvoiceInvMngtRespDto convert(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " InwardEinvoiceInvMngtRespDto object";
			LOGGER.debug(str);
		}

		InwardEinvoiceInvMngtRespDto convert = new InwardEinvoiceInvMngtRespDto();

		convert.setId(arr[0] != null ? Long.valueOf(arr[0].toString()) : null);
		convert.setGstin(arr[1] != null ? arr[1].toString() : null);
		convert.setVendorGstin(arr[2] != null ? arr[2].toString() : null);
		convert.setDocNo(arr[3] != null ? arr[3].toString() : null);
		convert.setDocDate(arr[4] != null ? arr[4].toString() : null);
		convert.setDocType(arr[5] != null ? arr[5].toString() : null);
		String docType = arr[5] != null ? arr[5].toString() : null;

		convert.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		convert.setTaxableVal(arr[7] != null
				? new BigDecimal(
						CheckForNegativeValue(arr[7], docType).toString())
				: null);
		convert.setTotalTax(arr[8] != null
				? new BigDecimal(
						CheckForNegativeValue(arr[8], docType).toString())
				: null);
		convert.setIgst(arr[9] != null
				? new BigDecimal(
						CheckForNegativeValue(arr[9], docType).toString())
				: null);
		convert.setCgst(arr[10] != null
				? new BigDecimal(
						CheckForNegativeValue(arr[10], docType).toString())
				: null);
		convert.setSgst(arr[11] != null
				? new BigDecimal(
						CheckForNegativeValue(arr[11], docType).toString())
				: null);
		convert.setCess(arr[12] != null
				? new BigDecimal(
						CheckForNegativeValue(arr[12], docType).toString())
				: null);
		convert.setTotInvVal(arr[13] != null
				? new BigDecimal(
						CheckForNegativeValue(arr[13], docType).toString())
				: null);
		convert.setIrnNum(arr[14] != null ? arr[14].toString() : null);
		if (arr[15] != null) {
			String sts = ("ACT".equalsIgnoreCase(arr[15].toString()) ? "Active"
					: "Cancelled");

			convert.setIrnSts(sts);
		}
		convert.setAckNum(arr[16] != null ? arr[16].toString() : null);

		if (arr[17] != null) {
			String accDt[] = arr[17].toString().split("\\.");
			convert.setAckDt(accDt[0]);
		}

		convert.setEwbNo(arr[18] != null ? arr[18].toString() : null);

		if (arr[19] != null) {
			String ewbDt[] = arr[19].toString().split("\\.");

			convert.setEwbDt(ewbDt[0]);
		}

		if (arr[20] != null) {
			String cnlDt[] = arr[20].toString().split("\\.");
			convert.setCnclDt(cnlDt[0]);
		}

		convert.setIrpName(arr[21] != null ? arr[21].toString() : null);


		//need to set the newly added variables in InwardEinvoiceInvMngtRespDto
		convert.setPrTagging(arr[22] != null ? arr[22].toString() : "Not Available");
		LOGGER.debug("arr [23] {} ",arr[23]);
		convert.setLastPrTagged(arr[23] != null ? dateChange(
				arr[23].toString().substring(0, 19)) : null);

		//need to set newly added variables in InwardEinvoiceInvMngtRespDto
		convert.setQrCodeValidated(arr[24] != null ? arr[24].toString() : "No");
		convert.setQrCodeValidationResult(arr[25] != null ? arr[25].toString() : null);
		convert.setQrCodeMatchCount(arr[26] != null ? Integer.parseInt(arr[26].toString()) : null);
		convert.setQrCodeMismatchCount(arr[27] != null ? Integer.parseInt(arr[27].toString()) : null);
		convert.setQrCodeMismatchAttributes(arr[28] != null ? arr[28].toString() : null);

		return convert;
	}

	private String CheckForNegativeValue(Object value, String docType) {

		if (value != null && !Strings.isNullOrEmpty(docType)) {
			if ("CR".equalsIgnoreCase(docType)) {
				if (value instanceof BigDecimal) {
					return (value != null ? ((((BigDecimal) value)
							.compareTo(BigDecimal.ZERO) > 0)
									? "-" + value.toString() : value.toString())
							: null);
				} else if (value instanceof Integer) {
					return (value != null ? (((Integer) value > 0)
							? "-" + value.toString() : value.toString())
							: null);
				} else if (value instanceof Long) {
					return (value != null ? (((Long) value > 0)
							? "-" + value.toString() : value.toString())
							: null);
				} else if (value instanceof BigInteger) {
					return (value != null ? ((((BigInteger) value)
							.compareTo(BigInteger.ZERO) > 0)
									? "-" + value.toString() : value.toString())
							: null);
				} else {
					if (!value.toString().isEmpty()) {
						return "-" + value.toString().replaceFirst("-", "");
					} else {
						return null;
					}
				}
			} else
				return value.toString();
		}
		return value.toString();
	}

	@Override
	public InwardEinvoiceInvMngtTabLayoutRespDto findTabLayoutData(String irn)
	{	
		
		InwardEinvoiceInvMngtTabLayoutRespDto reconDataList = new InwardEinvoiceInvMngtTabLayoutRespDto();
		
		GetIrnListEntity entity = listingRepo.getByIrnNum(irn);
		Long batchId = entity.getBatchId();
		String msg = null;
		Integer noOfChunk = 0;
		
		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(
						"USP_INSERT_CHUNK_IRN_NESTED_DETAIL_REPORT_SFTP");

		storedProc.registerStoredProcedureParameter("P_BATCH_ID", Long.class,
				ParameterMode.IN);

		storedProc.setParameter("P_BATCH_ID", entity.getBatchId());

		storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SPLIT_VAL", 500);
		
		storedProc.registerStoredProcedureParameter("P_SOURCE_TYPE",String.class, ParameterMode.IN);

		storedProc.setParameter("P_SOURCE_TYPE", "SCREEN");


		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Executing chunking proc"
					+ " USP_INSERT_CHUNK_IRN_NESTED_DETAIL_REPORT_SFTP: '%s'",
					batchId.toString());
			LOGGER.debug(msg);
		}

		Integer chunks = (Integer) storedProc.getSingleResult();

		noOfChunk = chunks;

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Chunking proc Executed"
					+ " USP_INSERT_CHUNK_IRN_NESTED_DETAIL_REPORT_SFTP: id '%d', "
					+ "noOfChunk %d ", batchId, noOfChunk);
			LOGGER.debug(msg);
		}

		noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

		if (noOfChunk <= 0) {
			msg = "No Data ";
			LOGGER.error(msg);
			
			return null;
		}

		int j = 1;

		try {
				StoredProcedureQuery storedProcNestedReport = entityManager
						.createStoredProcedureQuery(
								"USP_DISPLAY_CHUNK_IRN_NESTED_DETAIL_REPORT_SFTP");

				storedProcNestedReport.registerStoredProcedureParameter(
						"P_BATCH_ID", Long.class, ParameterMode.IN);

				storedProcNestedReport.setParameter("P_BATCH_ID", batchId);

				storedProcNestedReport.registerStoredProcedureParameter(
						"P_CHUNK_NUM", Integer.class, ParameterMode.IN);
				storedProcNestedReport.setParameter("P_CHUNK_NUM", j);
				
				storedProcNestedReport.registerStoredProcedureParameter("P_SOURCE_TYPE",String.class, ParameterMode.IN);

				storedProcNestedReport.setParameter("P_SOURCE_TYPE", "SCREEN");


				if (LOGGER.isDebugEnabled()) {
					msg = String.format("call stored proc with "
							+ "params {} Config ID is '%s', "
							+ " chunkNo is %d ", batchId.toString(), j);
					LOGGER.debug(msg);
				}

				long dbLoadStTime = System.currentTimeMillis();

				@SuppressWarnings("unchecked")
				List<Object[]> records = storedProcNestedReport.getResultList();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("no of records after proc call {} ",
							records.size());
				}
				long dbLoadEndTime = System.currentTimeMillis();
				long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Total Time taken to load the Data"
									+ " from DB is '%d' millisecs,"
									+ " Report Name and Data count:" + "  '%s'",
							dbLoadTimeDiff, records.size());
					LOGGER.debug(msg);
				}

				if (records != null && !records.isEmpty()) {
					reconDataList = convertToNested(records) ;
				}
		}
		catch(Exception ex)
		{
			msg = "error while calling display proc"+ ex;
			LOGGER.error(msg);
			throw new AppException(ex);
		}
	
		return reconDataList;
	}

	private InwardEinvoiceInvMngtTabLayoutRespDto convertToNested(
			List<Object[]> arr1) {

		InwardEinvoiceInvMngtTabLayoutRespDto reconDataList = new InwardEinvoiceInvMngtTabLayoutRespDto();

		List<EinvoiceNestedReportDto> preDoc = new ArrayList<>();
		List<EinvoiceNestedReportDto> contrctDetails = new ArrayList<>();
		List<EinvoiceNestedReportDto> suppDoc = new ArrayList<>();
		List<EinvoiceNestedReportDto> attribDetails = new ArrayList<>();

		for (Object[] arr : arr1) {
			EinvoiceNestedReportDto obj = new EinvoiceNestedReportDto();

			if (arr[10] != null || arr[11] != null || arr[12] != null) {
				obj.setPreceedingInvoiceNumber(
						(arr[10] != null) ? arr[10].toString() : null);
				obj.setPreceedingInvoiceDate(
						(arr[11] != null) ? arr[11].toString() : null);
				obj.setOtherReference(
						(arr[12] != null) ? arr[12].toString() : null);

				preDoc.add(obj);
			}

			else if (arr[13] != null || arr[14] != null || arr[15] != null
					|| arr[16] != null || arr[17] != null || arr[18] != null
					|| arr[19] != null || arr[20] != null) {
				obj.setReceiptAdviceReference(
						(arr[13] != null) ? arr[13].toString() : null);
				obj.setReceiptAdviceDate(
						(arr[14] != null) ? arr[14].toString() : null);
				obj.setTenderReference(
						(arr[15] != null) ? arr[15].toString() : null);
				obj.setContractReference(
						(arr[16] != null) ? arr[16].toString() : null);
				obj.setExternalReference(
						(arr[17] != null) ? arr[17].toString() : null);
				obj.setProjectReference(
						(arr[18] != null) ? arr[18].toString() : null);
				obj.setCustomerPOReferenceNumber(
						(arr[19] != null) ? arr[19].toString() : null);
				obj.setCustomerPOReferenceDate(
						(arr[20] != null) ? arr[20].toString() : null);
				contrctDetails.add(obj);

			} else if (arr[21] != null || arr[22] != null || arr[23] != null) {
				obj.setSupportingDocURL(
						(arr[21] != null) ? arr[21].toString() : null);
				obj.setSupportingDocument(
						(arr[22] != null) ? arr[22].toString() : null);
				obj.setAdditionalInformation(
						(arr[23] != null) ? arr[23].toString() : null);
				suppDoc.add(obj);
			} else if (arr[24] != null || arr[25] != null) {
				obj.setAttributeName(
						(arr[24] != null) ? arr[24].toString() : null);
				obj.setAttributeValue(
						(arr[25] != null) ? arr[25].toString() : null);
				attribDetails.add(obj);
			}

		}
		reconDataList.setPrecDocDtls(preDoc);
		reconDataList.setContrDtls(contrctDetails);
		reconDataList.setAddlDocDtls(suppDoc);
		reconDataList.setAttribDtls(attribDetails);
		return reconDataList;
	}
	
	public String dateChange(String oldDate) {
		DateTimeFormatter formatter = null;
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTimes = LocalDateTime.parse(oldDate, formatter);
		LocalDateTime dateTimeFormatter = EYDateUtil
				.toISTDateTimeFromUTC(dateTimes);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;
	}
	
}
