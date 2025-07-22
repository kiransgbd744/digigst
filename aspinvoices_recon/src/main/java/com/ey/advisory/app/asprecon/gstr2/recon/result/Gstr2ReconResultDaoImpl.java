package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("Gstr2ReconResultDaoImpl")
public class Gstr2ReconResultDaoImpl implements Gstr2ReconResultDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public Triplet<
    List<Gstr2ReconResultDto>,   
    List<ReconSummaryDto>,        
    Integer> getReconResult(
			Gstr2ReconResultReqDto reqDto, int pageNum, int pageSize) {

		List<Gstr2ReconResultDto> respList = null;
		List<String> gstins = reqDto.getGstins();
		List<ReconSummaryDto> summaryListResp = null;

		String gstin = !gstins.isEmpty() ? String.join(",", gstins) : "";

		String toTaxPeriod = reqDto.getToTaxPeriod();

		Integer toTaxPerd = Integer.parseInt(toTaxPeriod);

		String fromTaxPeriod = reqDto.getFromTaxPeriod();

		Integer fromTaxPerd = Integer.parseInt(fromTaxPeriod);

		String fromDocDate = !Strings.isNullOrEmpty(reqDto.getFromDocDate())
				? reqDto.getFromDocDate() : "";

		String toDocDate = !Strings.isNullOrEmpty(reqDto.getToDocDate())
				? reqDto.getFromDocDate() : "";

		String docNum = !reqDto.getDocNumberList().isEmpty()
				? String.join(",", reqDto.getDocNumberList()) : "";
		List<String> reportType = reqDto.getReportType();

		List<String> docType = reqDto.getDocType();
		String dcType = !docType.isEmpty() ? String.join(",", docType) : "";
		String vendrPans = !reqDto.getVndrPans().isEmpty()
				? String.join(",", reqDto.getVndrPans()) : "";

		String vendrGstin = !reqDto.getVndrGstins().isEmpty()
				? String.join(",", reqDto.getVndrGstins()) : "";

		String accVoucherNums = !reqDto.getAccVoucherNums().isEmpty()
				? String.join(",", reqDto.getAccVoucherNums()) : "";
		Integer fromTaxPrd3B = !Strings.isNullOrEmpty(reqDto.getFrmTaxPrd3b())
				? Integer.parseInt(reqDto.getFrmTaxPrd3b()) :1;

		Integer toTaxPrd3B =  !Strings.isNullOrEmpty(reqDto.getToTaxPrd3b())
				?  Integer.parseInt(reqDto.getToTaxPrd3b()) : 1;
				
		String reconCriteria = reqDto.getReconCriteria();
		
		String taxPeriodBase = reqDto.getTaxPeriodBase();
		

		Integer totalCnt = 0;

		try {
			List<String> newReportList = new ArrayList<>();

			Integer fmr = 3;
			if (reportType.contains(APIConstants.FORCE_MATCH)
					&& !reportType.contains(APIConstants.RESPONSE_B3)) {
				fmr = 0;

			} else if (!reportType.contains(APIConstants.FORCE_MATCH)
					&& reportType.contains(APIConstants.RESPONSE_B3)) {
				fmr = 1;

			} else if (reportType.contains(APIConstants.FORCE_MATCH)
					&& reportType.contains(APIConstants.RESPONSE_B3)) {
				fmr = 2;
			}

			for (String rtpe : reportType) {
				if (fmr == 0
						&& rtpe.equalsIgnoreCase(APIConstants.FORCE_MATCH)) {

					continue;
				} else if (fmr == 1
						&& rtpe.equalsIgnoreCase(APIConstants.RESPONSE_B3)) {
					continue;
				} else if (fmr == 2
						&& rtpe.equalsIgnoreCase(APIConstants.RESPONSE_B3)) {
					continue;

				} else if (fmr == 2
						&& rtpe.equalsIgnoreCase(APIConstants.FORCE_MATCH)) {
					continue;

				} else {
					newReportList.add(rtpe);
				}

			}

			if (fmr == 2) {
				newReportList.add("ForceMatch/GSTR3B");
			}
			String rptType = !newReportList.isEmpty()
					? String.join(",", newReportList) : "";

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"The parameters passed to USP_AUTO_2APR_RECON_RESULT_SCREEN_UI proc "
								+ " toTaxPerd {}, fromTaxPerd {}, gstins {}, "
								+ " reportType {}, docNum {}, docType {}, pageSize {},"
								+ " pageNum {}, fmr {}, toDocDate {}, fromDocDate {} ",
						toTaxPerd, fromTaxPerd, gstin, rptType, docNum, dcType,
						pageSize, pageNum, fmr, toDocDate, fromDocDate);
			}

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_AUTO_2APR_RECON_RESULT_SCREEN_UI");

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("About to execute USP_2APR_RECON_RESULT_UI SP with "
								+ "gstin :%s", gstins);
				LOGGER.debug(msg);
			}

			storedProc.registerStoredProcedureParameter("IN_FROM_TAX_PERIOD",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("IN_FROM_TAX_PERIOD", fromTaxPerd);

			storedProc.registerStoredProcedureParameter("IN_TO_TAX_PERIOD",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("IN_TO_TAX_PERIOD", toTaxPerd);

			storedProc.registerStoredProcedureParameter("IN_REPORT_TYPES",
					String.class, ParameterMode.IN);
			storedProc.setParameter("IN_REPORT_TYPES", rptType);

			storedProc.registerStoredProcedureParameter("IN_RECIPIENT_GSTIN",
					String.class, ParameterMode.IN);
			storedProc.setParameter("IN_RECIPIENT_GSTIN", gstin);

			storedProc.registerStoredProcedureParameter("IN_DOC_TYPE",
					String.class, ParameterMode.IN);
			storedProc.setParameter("IN_DOC_TYPE", dcType);

			storedProc.registerStoredProcedureParameter("FROM_DOC_DATE",
					String.class, ParameterMode.IN);
			storedProc.setParameter("FROM_DOC_DATE", fromDocDate);

			storedProc.registerStoredProcedureParameter("TO_DOC_DATE",
					String.class, ParameterMode.IN);
			storedProc.setParameter("TO_DOC_DATE", toDocDate);

			storedProc.registerStoredProcedureParameter("IN_DOC_NUM",
					String.class, ParameterMode.IN);
			storedProc.setParameter("IN_DOC_NUM", docNum);

			storedProc.registerStoredProcedureParameter("IN_FMR", Integer.class,
					ParameterMode.IN);
			storedProc.setParameter("IN_FMR", fmr);
			

			storedProc.registerStoredProcedureParameter("IN_ACCVOUCHERNUM",
					String.class, ParameterMode.IN);
			storedProc.setParameter("IN_ACCVOUCHERNUM", accVoucherNums);

			storedProc.registerStoredProcedureParameter("IN_VENDORPANS",
					String.class, ParameterMode.IN);
			storedProc.setParameter("IN_VENDORPANS", vendrPans);

			storedProc.registerStoredProcedureParameter("IN_VENDORGSTINS",
					String.class, ParameterMode.IN);
			storedProc.setParameter("IN_VENDORGSTINS", vendrGstin);

			storedProc.registerStoredProcedureParameter("IN_FROM3BTAXPERIOD",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("IN_FROM3BTAXPERIOD", fromTaxPrd3B);

			storedProc.registerStoredProcedureParameter("IN_TO3BTAXPERIOD",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("IN_TO3BTAXPERIOD", toTaxPrd3B);
			
			storedProc.registerStoredProcedureParameter("IN_PAGESIZE",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("IN_PAGESIZE", pageSize);

			storedProc.registerStoredProcedureParameter("IN_PAGENO",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("IN_PAGENO", pageNum);

			// identifier
			storedProc.registerStoredProcedureParameter("IN_LOCKTYPE",
					String.class, ParameterMode.IN);

			storedProc.setParameter("IN_LOCKTYPE",
					reqDto.getIdentifier() != null ? reqDto.getIdentifier()
							: "SINGLE");
			
			storedProc.registerStoredProcedureParameter("IN_RECONCRITERIA",
					String.class, ParameterMode.IN);
			storedProc.setParameter("IN_RECONCRITERIA", reconCriteria);
			
			storedProc.registerStoredProcedureParameter("TAX_PERIOD_BASE",
					String.class, ParameterMode.IN);
			storedProc.setParameter("TAX_PERIOD_BASE", taxPeriodBase);

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("The result size -> {} ", list.size());
			}

			if (list == null || list.isEmpty())
				return null;

			respList = list.stream().map(o -> convert(o, reconCriteria))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("The result set after respList -> {}",
						respList.size());
			}
			
			StoredProcedureQuery storedProcSummary = entityManager
					.createStoredProcedureQuery(
							"USP_AUTO_2APR_RECON_RESULT_SCREEN_UI_SMRY");

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("About to execute USP_AUTO_2APR_RECON_RESULT_SCREEN_UI_SMRY SP with "
								+ "gstin :%s", gstins);
				LOGGER.debug(msg);
			}

			storedProcSummary.registerStoredProcedureParameter("IN_FROM_TAX_PERIOD",
					Integer.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_FROM_TAX_PERIOD", fromTaxPerd);

			storedProcSummary.registerStoredProcedureParameter("IN_TO_TAX_PERIOD",
					Integer.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_TO_TAX_PERIOD", toTaxPerd);

			storedProcSummary.registerStoredProcedureParameter("IN_REPORT_TYPES",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_REPORT_TYPES", rptType);

			storedProcSummary.registerStoredProcedureParameter("IN_RECIPIENT_GSTIN",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_RECIPIENT_GSTIN", gstin);

			storedProcSummary.registerStoredProcedureParameter("IN_DOC_TYPE",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_DOC_TYPE", dcType);

			storedProcSummary.registerStoredProcedureParameter("FROM_DOC_DATE",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("FROM_DOC_DATE", fromDocDate);

			storedProcSummary.registerStoredProcedureParameter("TO_DOC_DATE",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("TO_DOC_DATE", toDocDate);

			storedProcSummary.registerStoredProcedureParameter("IN_DOC_NUM",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_DOC_NUM", docNum);

			storedProcSummary.registerStoredProcedureParameter("IN_FMR", Integer.class,
					ParameterMode.IN);
			storedProcSummary.setParameter("IN_FMR", fmr);
			

			storedProcSummary.registerStoredProcedureParameter("IN_ACCVOUCHERNUM",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_ACCVOUCHERNUM", accVoucherNums);

			storedProcSummary.registerStoredProcedureParameter("IN_VENDORPANS",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_VENDORPANS", vendrPans);

			storedProcSummary.registerStoredProcedureParameter("IN_VENDORGSTINS",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_VENDORGSTINS", vendrGstin);

			storedProcSummary.registerStoredProcedureParameter("IN_FROM3BTAXPERIOD",
					Integer.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_FROM3BTAXPERIOD", fromTaxPrd3B);

			storedProcSummary.registerStoredProcedureParameter("IN_TO3BTAXPERIOD",
					Integer.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_TO3BTAXPERIOD", toTaxPrd3B);
			
			storedProcSummary.registerStoredProcedureParameter("IN_PAGESIZE",
					Integer.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_PAGESIZE", pageSize);

			storedProcSummary.registerStoredProcedureParameter("IN_PAGENO",
					Integer.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_PAGENO", pageNum);

			// identifier
			storedProcSummary.registerStoredProcedureParameter("IN_LOCKTYPE",
					String.class, ParameterMode.IN);

			storedProcSummary.setParameter("IN_LOCKTYPE",
					reqDto.getIdentifier() != null ? reqDto.getIdentifier()
							: "SINGLE");
			
			storedProcSummary.registerStoredProcedureParameter("IN_RECONCRITERIA",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("IN_RECONCRITERIA", reconCriteria);
			
			storedProcSummary.registerStoredProcedureParameter("TAX_PERIOD_BASE",
					String.class, ParameterMode.IN);
			storedProcSummary.setParameter("TAX_PERIOD_BASE", taxPeriodBase);

			@SuppressWarnings("unchecked")
			List<Object[]> summaryList = storedProcSummary.getResultList();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("The summaryList size -> {} ", summaryList.size());
			}
			
			summaryListResp = summaryList.stream().map(o -> convertSummary(o))
					.collect(Collectors.toCollection(ArrayList::new));

			Object[] obj = list.get(0);
			totalCnt = (obj[31] != null ? Integer.parseInt(obj[31].toString())
					: 0);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("The totalCnt fetched -> {}", totalCnt);
			}

			if ("MULTI".equalsIgnoreCase(reqDto.getIdentifier())) {
				Collections.sort(respList, Comparator
						.comparing(Gstr2ReconResultDto::getBatchId).reversed());
			}
		} catch (Exception ex) {
			String msg = String.format("Error Occured while executing query %s",
					ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		return Triplet.with(respList, summaryListResp, totalCnt);
	}
	
	private ReconSummaryDto convertSummary(Object[] arr) {

	    ReconSummaryDto dto = new ReconSummaryDto();

	    // 1) read each column as String
	    String col0  = checkNull(arr[0]);
	    String col1  = checkNull(arr[1]);
	    String col2  = checkNull(arr[2]);
	    String col3  = checkNull(arr[3]);
	    String col4  = checkNull(arr[4]);
	    String col5  = checkNull(arr[5]);
	    String col6  = checkNull(arr[6]);
	    String col7  = checkNull(arr[7]);
	    String col8  = checkNull(arr[8]);
	    String col9  = checkNull(arr[9]);
	    String col10 = checkNull(arr[10]);
	    String col11 = checkNull(arr[11]);
	    String col12  = checkNull(arr[12]);
	    String col13  = checkNull(arr[13]);
	    String col14  = checkNull(arr[14]);
	    String col15  = checkNull(arr[15]);
	    String col16  = checkNull(arr[16]);
	    String col17  = checkNull(arr[17]);
	    String col18  = checkNull(arr[18]);

	    // 2) parse and set
	    dto.setPrVendorGstinCount(col0  != null ? Long.valueOf(col0)       : null);
	    dto.setPrDocumentCount   (col1  != null ? Integer.valueOf(col1)    : null);
	    dto.setPrTotalTax        (col2  != null ? new BigDecimal(col2)     : null);
	    dto.setPrIgst            (col3  != null ? new BigDecimal(col3)     : null);
	    dto.setPrCgst            (col4  != null ? new BigDecimal(col4)     : null);
	    dto.setPrSgst            (col5  != null ? new BigDecimal(col5)     : null);
	    dto.setPrCess            (col6  != null ? new BigDecimal(col6)     : null);
	    dto.setPrAvailableIgst   (col7  != null ? new BigDecimal(col7)     : null);
	    dto.setPrAvailableCgst   (col8  != null ? new BigDecimal(col8)     : null);
	    dto.setPrAvailableSgst   (col9  != null ? new BigDecimal(col9)     : null);
	    dto.setPrAvailableCess   (col10 != null ? new BigDecimal(col10)    : null);
	    dto.setA2VendorGstinCount(col0  != null ? Long.valueOf(col11)       : null);
	    dto.setA2DocumentCount   (col1  != null ? Integer.valueOf(col12)    : null);
	    dto.setA2TotalTax        (col2  != null ? new BigDecimal(col13)     : null);
	    dto.setA2Igst            (col3  != null ? new BigDecimal(col14)     : null);
	    dto.setA2Cgst            (col4  != null ? new BigDecimal(col15)     : null);
	    dto.setA2Sgst            (col5  != null ? new BigDecimal(col16)     : null);
	    dto.setA2Cess            (col6  != null ? new BigDecimal(col17)     : null);
	    dto.setCnt             (col11 != null ? Integer.valueOf(col18)   : null);
	    

	    return dto;
	}

	private Gstr2ReconResultDto convert(Object[] arr, String reconCriteria) {

		Gstr2ReconResultDto dto = new Gstr2ReconResultDto();
		try {
			dto.setGstin(checkNull(arr[0]));
			dto.setVendorGstin(checkNull(arr[1]));

			if (arr[26] != null && (arr[26].toString()
					.equalsIgnoreCase(APIConstants.addition2A)
					|| arr[26].toString()
							.equalsIgnoreCase(APIConstants.ADDITION_2A_IMPG))) {

				convertDtoForAddition2AIMPG(arr, dto, reconCriteria);

			} else if (arr[26] != null
					&& arr[26].toString()
							.equalsIgnoreCase(APIConstants.additionPR)
					|| arr[26].toString()
							.equalsIgnoreCase(APIConstants.ADDITION_PR_IMPG)) {

				convertDtoForAdditionPRIMPG(arr, dto, reconCriteria);

			} else if (arr[26] != null
					&& arr[26].toString()
							.equalsIgnoreCase(APIConstants.EXACT_MATCH_IMPG)
					|| arr[26].toString()
							.equalsIgnoreCase(APIConstants.MISMATCH_IMPG)) {

				convertDtoForIMPG(arr, dto, reconCriteria);

			} else if (arr[26] != null && arr[26].toString()
					.equalsIgnoreCase(APIConstants.forceMatch_GSTR3B)) {

				convertDtoForForceMatch(arr, dto, reconCriteria);

			} else {

				convertDtoForOtherRepTypes(arr, dto, reconCriteria);
			}
			String docTypePR = checkNull(arr[2]);
			if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
					|| docTypePR.equalsIgnoreCase("C")
					|| docTypePR.equalsIgnoreCase("RCR"))) {
				dto.setAvalIgst(checkForNegativeValue(arr[32]));
				dto.setAvalCgst(checkForNegativeValue(arr[33]));
				dto.setAvalSgst(checkForNegativeValue(arr[34]));
				dto.setAvalCess(checkForNegativeValue(arr[35]));

			} else {
				dto.setAvalIgst(
						(arr[32] != null) ? arr[32].toString() : "0.00");
				dto.setAvalCgst(
						(arr[33] != null) ? arr[33].toString() : "0.00");
				dto.setAvalSgst(
						(arr[34] != null) ? arr[34].toString() : "0.00");
				dto.setAvalCess(
						(arr[35] != null) ? arr[35].toString() : "0.00");
			}

			dto.setItcReversal(arr[36] != null ? arr[36].toString()
					: (dto.getDocDatePR() != null ? "" : null));

			dto.setBatchId(arr[37] != null ? arr[37].toString() : null);
			dto.setGstin2A(arr[38] != null ? arr[38].toString() : null);
			dto.setVendorGstin2A(arr[39] != null ? arr[39].toString() : null);
			dto.setAccVoucherNo(arr[40] != null ? arr[40].toString() : null);
			
			dto.setImsActionGstn(arr[41] != null ? arr[41].toString() : null);
			dto.setImsActionDigiGst(arr[42] != null ? arr[42].toString() : null);
			dto.setImsUniqId(arr[43] != null ? arr[43].toString() : null);

		} catch (Exception ex) {
			String msg = String.format("Error while converting dto %s", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		return dto;
	}

	private void convertDtoForOtherRepTypes(Object[] arr,
			Gstr2ReconResultDto dto, String reconCriteria) {
		String docTypePR = checkNull(arr[2]);
		String docType2A = checkNull(arr[3]);
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {

			dto.setTotalTaxPR(checkForNegativeValue(arr[8]));
			dto.setIgstPR(checkForNegativeValue(arr[10]));
			dto.setCgstPR(checkForNegativeValue(arr[12]));
			dto.setSgstPR(checkForNegativeValue(arr[14]));
			dto.setCessPR(checkForNegativeValue(arr[16]));

		} else {

			dto.setTotalTaxPR(checkNull(arr[8]));
			dto.setIgstPR(checkNull(arr[10]));
			dto.setCgstPR(checkNull(arr[12]));
			dto.setSgstPR(checkNull(arr[14]));
			dto.setCessPR(checkNull(arr[16]));

		}
		if (docType2A != null && (docType2A.equalsIgnoreCase("CR")
				|| docType2A.equalsIgnoreCase("C")
				|| docType2A.equalsIgnoreCase("RCR"))) {

			dto.setTotalTax2A(checkForNegativeValue(arr[9]));
			dto.setIgst2A(checkForNegativeValue(arr[11]));
			dto.setCgst2A(checkForNegativeValue(arr[13]));
			dto.setSgst2A(checkForNegativeValue(arr[15]));
			dto.setCess2A(checkForNegativeValue(arr[17]));

		} else {

			dto.setTotalTax2A(checkNull(arr[9]));
			dto.setIgst2A(checkNull(arr[11]));
			dto.setCgst2A(checkNull(arr[13]));
			dto.setSgst2A(checkNull(arr[15]));
			dto.setCess2A(checkNull(arr[17]));

		}

		dto.setDocTypePR(checkNull(arr[2]));
		dto.setDocType2A(checkNull(arr[3]));
		dto.setDocTypeMatch(isMatch(dto.getDocTypePR(), dto.getDocType2A()));

		if("Import".equalsIgnoreCase(reconCriteria)){
			dto.setBoeNoPR(checkNull(arr[4]));
			dto.setBoeNo2A(checkNull(arr[5]));

			if (arr[26] != null
					&& arr[26].toString().equalsIgnoreCase(APIConstants.EXACT_MATCH)
					|| arr[26].toString()
							.equalsIgnoreCase(APIConstants.MATCH_WITH_TOLERANCE)) {
				dto.setBoeNoMatch(true);
			} else {
				dto.setBoeNoMatch(
						isMatch(dto.getBoeNoPR(), dto.getBoeNo2A()));
			}
			dto.setBoeDatePR(checkNull(arr[6]));
			dto.setBoeDate2A(checkNull(arr[7]));
			dto.setBoeDateMatch(isMatch(dto.getBoeDatePR(), dto.getBoeDate2A()));

		}else{
			dto.setDocNumberPR(checkNull(arr[4]));
			dto.setDocNumber2A(checkNull(arr[5]));
			if (arr[26] != null
					&& arr[26].toString().equalsIgnoreCase(APIConstants.EXACT_MATCH)
					|| arr[26].toString()
							.equalsIgnoreCase(APIConstants.MATCH_WITH_TOLERANCE)) {
				dto.setDocNumberMatch(true);
			} else {
				dto.setDocNumberMatch(
						isMatch(dto.getDocNumberPR(), dto.getDocNumber2A()));
			}
			dto.setDocDatePR(checkNull(arr[6]));
			dto.setDocDate2A(checkNull(arr[7]));
			dto.setDocDateMatch(isMatch(dto.getDocDatePR(), dto.getDocDate2A()));
		}
		
		dto.setTotalTaxMatch(
				isBigDecimalMatch((BigDecimal) arr[8], (BigDecimal) arr[9]));
		dto.setIgstMatch(
				isBigDecimalMatch((BigDecimal) arr[10], (BigDecimal) arr[11]));
		dto.setCgstMatch(
				isBigDecimalMatch((BigDecimal) arr[12], (BigDecimal) arr[13]));
		dto.setSgstMatch(
				isBigDecimalMatch((BigDecimal) arr[14], (BigDecimal) arr[15]));
		dto.setCessMatch(
				isBigDecimalMatch((BigDecimal) arr[16], (BigDecimal) arr[17]));

		dto.setMismatchReason(checkNull(arr[18]));

		setOtherValues(arr, dto);
	}

	private void setOtherValues(Object[] arr, Gstr2ReconResultDto dto) {

		if ((arr[19] != null
				&& (arr[19].toString().equalsIgnoreCase(APIConstants.LOCK)))) {
			dto.setResponseTaken("Force Match");
		}
		if (arr[19] != null && arr[25] != null) {
			dto.setResponseTaken("3B-" + arr[25].toString());
		}
		if (dto.getResponseTaken() == null) {
			dto.setResponseTaken(APIConstants.NO_ACTION);
		}
		dto.setRespRemarks(checkNull(arr[20]));
		BigInteger reconLinkIdBigInt = GenUtil.getBigInteger(arr[24]);
		Long reconLinkId = reconLinkIdBigInt != null
				? reconLinkIdBigInt.longValue() : null;
		dto.setReconLinkId(reconLinkId);
		dto.setReportType((String) arr[26]);
	}

	private void convertDtoForForceMatch(Object[] arr,
			Gstr2ReconResultDto dto, String reconCriteria) {
		String docTypePR = checkNull(arr[2]);
		String docType2A = checkNull(arr[3]);
		String id2A = checkNull(arr[22]);
		String idPR = checkNull(arr[23]);

		if (id2A != null && idPR != null) {
			if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
					|| docTypePR.equalsIgnoreCase("C")
					|| docTypePR.equalsIgnoreCase("RCR"))) {

				dto.setTotalTaxPR(checkForNegativeValue(arr[8]));
				dto.setIgstPR(checkForNegativeValue(arr[10]));
				dto.setCgstPR(checkForNegativeValue(arr[12]));
				dto.setSgstPR(checkForNegativeValue(arr[14]));
				dto.setCessPR(checkForNegativeValue(arr[16]));

			} else {

				dto.setTotalTaxPR(checkNull(arr[8]));
				dto.setIgstPR(checkNull(arr[10]));
				dto.setCgstPR(checkNull(arr[12]));
				dto.setSgstPR(checkNull(arr[14]));
				dto.setCessPR(checkNull(arr[16]));

			}
			if (docType2A != null && (docType2A.equalsIgnoreCase("CR")
					|| docType2A.equalsIgnoreCase("C")
					|| docType2A.equalsIgnoreCase("RCR"))) {

				dto.setTotalTax2A(checkForNegativeValue(arr[9]));
				dto.setIgst2A(checkForNegativeValue(arr[11]));
				dto.setCgst2A(checkForNegativeValue(arr[13]));
				dto.setSgst2A(checkForNegativeValue(arr[15]));
				dto.setCess2A(checkForNegativeValue(arr[17]));

			} else {

				dto.setTotalTax2A(checkNull(arr[9]));
				dto.setIgst2A(checkNull(arr[11]));
				dto.setCgst2A(checkNull(arr[13]));
				dto.setSgst2A(checkNull(arr[15]));
				dto.setCess2A(checkNull(arr[17]));

			}

			dto.setDocTypePR(checkNull(arr[2]));
			dto.setDocType2A(checkNull(arr[3]));
			dto.setDocTypeMatch(
					isMatch(dto.getDocTypePR(), dto.getDocType2A()));

			if("Import".equalsIgnoreCase(reconCriteria)){
				dto.setBoeNoPR(checkNull(arr[4]));
				dto.setBoeNo2A(checkNull(arr[5]));
				dto.setBoeNoMatch(
						isMatch(dto.getBoeNoPR(), dto.getBoeNo2A()));
				dto.setBoeDatePR(checkNull(arr[6]));
				dto.setBoeDate2A(checkNull(arr[7]));
				dto.setBoeDateMatch(
						isMatch(dto.getBoeDatePR(), dto.getBoeDate2A()));

			}else{
				dto.setDocNumberPR(checkNull(arr[4]));
				dto.setDocNumber2A(checkNull(arr[5]));
				dto.setDocNumberMatch(
						isMatch(dto.getDocNumberPR(), dto.getDocNumber2A()));
				dto.setDocDatePR(checkNull(arr[6]));
				dto.setDocDate2A(checkNull(arr[7]));
				dto.setDocDateMatch(
						isMatch(dto.getDocDatePR(), dto.getDocDate2A()));
			}
			
			dto.setTotalTaxMatch(isBigDecimalMatch((BigDecimal) arr[8],
					(BigDecimal) arr[9]));
			dto.setIgstMatch(isBigDecimalMatch((BigDecimal) arr[10],
					(BigDecimal) arr[11]));
			dto.setCgstMatch(isBigDecimalMatch((BigDecimal) arr[12],
					(BigDecimal) arr[13]));
			dto.setSgstMatch(isBigDecimalMatch((BigDecimal) arr[14],
					(BigDecimal) arr[15]));
			dto.setCessMatch(isBigDecimalMatch((BigDecimal) arr[16],
					(BigDecimal) arr[17]));

		} else if (id2A != null && idPR == null) {

			dto.setDocType2A(docType2A);
			dto.setDocTypeMatch(true);
			dto.setTotalTaxMatch(true);
			dto.setIgstMatch(true);
			dto.setCgstMatch(true);
			dto.setSgstMatch(true);
			dto.setCessMatch(true);
			
			if("Import".equalsIgnoreCase(reconCriteria)){
				dto.setBoeNo2A(checkNull(arr[5]));
				dto.setBoeNoMatch(true);
				dto.setBoeDate2A(checkNull(arr[7]));
				dto.setBoeDateMatch(true);
			}else{
				dto.setDocNumber2A(checkNull(arr[5]));
				dto.setDocNumberMatch(true);
				dto.setDocDate2A(checkNull(arr[7]));
				dto.setDocDateMatch(true);
			}

			if (docType2A != null && (docType2A.equalsIgnoreCase("CR")
					|| docType2A.equalsIgnoreCase("C")
					|| docType2A.equalsIgnoreCase("RCR"))) {

				dto.setTotalTax2A(checkForNegativeValue(arr[9]));
				dto.setIgst2A(checkForNegativeValue(arr[11]));
				dto.setCgst2A(checkForNegativeValue(arr[13]));
				dto.setSgst2A(checkForNegativeValue(arr[15]));
				dto.setCess2A(checkForNegativeValue(arr[17]));

			} else {

				dto.setTotalTax2A(checkNull(arr[9]));
				dto.setIgst2A(checkNull(arr[11]));
				dto.setCgst2A(checkNull(arr[13]));
				dto.setSgst2A(checkNull(arr[15]));
				dto.setCess2A(checkNull(arr[17]));

			}

		} else if (id2A == null && idPR != null) {

			dto.setDocTypePR(docTypePR);
			dto.setDocTypeMatch(true);
			dto.setTotalTaxMatch(true);
			dto.setIgstMatch(true);
			dto.setCgstMatch(true);
			dto.setSgstMatch(true);
			dto.setCessMatch(true);
			
			if("Import".equalsIgnoreCase(reconCriteria)){
				dto.setBoeNoPR(checkNull(arr[4]));
				dto.setBoeNoMatch(true);
				dto.setBoeDatePR(checkNull(arr[6]));
				dto.setBoeDateMatch(true);
			}else{
				dto.setDocNumberPR(checkNull(arr[4]));
				dto.setDocNumberMatch(true);
				dto.setDocDatePR(checkNull(arr[6]));
				dto.setDocDateMatch(true);
			}

			if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
					|| docTypePR.equalsIgnoreCase("C")
					|| docTypePR.equalsIgnoreCase("RCR"))) {

				dto.setTotalTaxPR(checkForNegativeValue(arr[8]));
				dto.setIgstPR(checkForNegativeValue(arr[10]));
				dto.setCgstPR(checkForNegativeValue(arr[12]));
				dto.setSgstPR(checkForNegativeValue(arr[14]));
				dto.setCessPR(checkForNegativeValue(arr[16]));

			} else {

				dto.setTotalTaxPR(checkNull(arr[8]));
				dto.setIgstPR(checkNull(arr[10]));
				dto.setCgstPR(checkNull(arr[12]));
				dto.setSgstPR(checkNull(arr[14]));
				dto.setCessPR(checkNull(arr[16]));

			}
		}

		if ((arr[18] != null && (arr[26] != null
				&& arr[26].toString().equalsIgnoreCase(APIConstants.additionPR)
				|| arr[26].toString()
						.equalsIgnoreCase(APIConstants.addition2A)))) {
			dto.setMismatchReason(checkNull(arr[18]));
		} else {
			dto.setMismatchReason(checkNull(arr[18]));
		}

		setOtherValues(arr, dto);
	}

	private void convertDtoForIMPG(Object[] arr, Gstr2ReconResultDto dto, String reconCriteria) {
		String docTypePR = checkNull(arr[2]);
		String docType2A = checkNull(arr[3]);

		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {

			dto.setTotalTaxPR(checkForNegativeValue(arr[8]));
			dto.setIgstPR(checkForNegativeValue(arr[10]));
			dto.setCgstPR(checkForNegativeValue(arr[12]));
			dto.setSgstPR(checkForNegativeValue(arr[14]));
			dto.setCessPR(checkForNegativeValue(arr[16]));

		} else {

			dto.setTotalTaxPR(checkNull(arr[8]));
			dto.setIgstPR(checkNull(arr[10]));
			dto.setCgstPR(checkNull(arr[12]));
			dto.setSgstPR(checkNull(arr[14]));
			dto.setCessPR(checkNull(arr[16]));

		}
		if (docType2A != null && (docType2A.equalsIgnoreCase("CR")
				|| docType2A.equalsIgnoreCase("C")
				|| docType2A.equalsIgnoreCase("RCR"))) {

			dto.setTotalTax2A(checkForNegativeValue(arr[9]));
			dto.setIgst2A(checkForNegativeValue(arr[11]));
			dto.setCgst2A(checkForNegativeValue(arr[13]));
			dto.setSgst2A(checkForNegativeValue(arr[15]));
			dto.setCess2A(checkForNegativeValue(arr[17]));

		} else {

			dto.setTotalTax2A(checkNull(arr[9]));
			dto.setIgst2A(checkNull(arr[11]));
			dto.setCgst2A(checkNull(arr[13]));
			dto.setSgst2A(checkNull(arr[15]));
			dto.setCess2A(checkNull(arr[17]));

		}

		dto.setDocTypePR(checkNull(arr[2]));
		dto.setDocType2A(checkNull(arr[3]));
		
		
		if("Import".equalsIgnoreCase(reconCriteria)){
			dto.setBoeNoPR(checkNull(arr[4]));
			dto.setBoeNo2A(checkNull(arr[5]));
			dto.setBoeDatePR(checkNull(arr[6]));
			dto.setBoeDate2A(checkNull(arr[7]));
			dto.setBoeNoMatch(true);
			dto.setBoeDateMatch(true);
		}else{
			dto.setDocNumberPR(checkNull(arr[4]));
			dto.setDocNumber2A(checkNull(arr[5]));
			dto.setDocDatePR(checkNull(arr[6]));
			dto.setDocDate2A(checkNull(arr[7]));
			dto.setDocNumberMatch(true);
			dto.setDocDateMatch(true);
		}
		
		dto.setTotalTaxMatch(
				isBigDecimalMatch((BigDecimal) arr[8], (BigDecimal) arr[9]));
		dto.setIgstMatch(
				isBigDecimalMatch((BigDecimal) arr[10], (BigDecimal) arr[11]));
		dto.setCgstMatch(
				isBigDecimalMatch((BigDecimal) arr[12], (BigDecimal) arr[13]));
		dto.setSgstMatch(
				isBigDecimalMatch((BigDecimal) arr[14], (BigDecimal) arr[15]));
		dto.setCessMatch(
				isBigDecimalMatch((BigDecimal) arr[16], (BigDecimal) arr[17]));

		dto.setMismatchReason(checkNull(arr[18]));
		dto.setDocTypeMatch(true);
		setOtherValues(arr, dto);
	}

	private void convertDtoForAdditionPRIMPG(Object[] arr,
			Gstr2ReconResultDto dto, String reconCriteria) {
		String docTypePR = checkNull(arr[2]);
		dto.setDocTypePR(docTypePR);
		dto.setDocTypeMatch(true);
		
          if("Import".equalsIgnoreCase(reconCriteria)){
			
			dto.setBoeNoPR(checkNull(arr[4]));
			dto.setBoeNoMatch(true);
			dto.setBoeDatePR(checkNull(arr[6]));
			dto.setBoeDateMatch(true);
		}else{
			dto.setDocNumberPR(checkNull(arr[4]));
			dto.setDocNumberMatch(true);
			dto.setDocDatePR(checkNull(arr[6]));
			dto.setDocDateMatch(true);
		}
		
		dto.setTotalTaxMatch(true);
		dto.setIgstMatch(true);
		dto.setCgstMatch(true);
		dto.setSgstMatch(true);
		dto.setCessMatch(true);

		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {

			dto.setTotalTaxPR(checkForNegativeValue(arr[8]));
			dto.setIgstPR(checkForNegativeValue(arr[10]));
			dto.setCgstPR(checkForNegativeValue(arr[12]));
			dto.setSgstPR(checkForNegativeValue(arr[14]));
			dto.setCessPR(checkForNegativeValue(arr[16]));

		} else {

			dto.setTotalTaxPR(checkNull(arr[8]));
			dto.setIgstPR(checkNull(arr[10]));
			dto.setCgstPR(checkNull(arr[12]));
			dto.setSgstPR(checkNull(arr[14]));
			dto.setCessPR(checkNull(arr[16]));

		}

		if ((arr[18] != null && (arr[26] != null
				&& arr[26].toString().equalsIgnoreCase(APIConstants.additionPR)
				|| arr[26].toString()
						.equalsIgnoreCase(APIConstants.ADDITION_PR_IMPG)))) {
			dto.setMismatchReason(checkNull(arr[18]));
		} else {
			dto.setMismatchReason(checkNull(arr[18]));
		}

		setOtherValues(arr, dto);
	}

	private void convertDtoForAddition2AIMPG(Object[] arr,
			Gstr2ReconResultDto dto, String reconCriteria) {
		String docType2A = checkNull(arr[3]);
		dto.setDocType2A(docType2A);
		dto.setDocTypeMatch(true);
		
		if("Import".equalsIgnoreCase(reconCriteria)){
			dto.setBoeNo2A(checkNull(arr[5]));
			dto.setBoeNoMatch(true);
			dto.setBoeDate2A(checkNull(arr[7]));
			dto.setBoeDateMatch(true);
		}else{
			dto.setDocNumber2A(checkNull(arr[5]));
			dto.setDocNumberMatch(true);
			dto.setDocDate2A(checkNull(arr[7]));
			dto.setDocDateMatch(true);
			}
		
		dto.setTotalTaxMatch(true);
		dto.setIgstMatch(true);
		dto.setCgstMatch(true);
		dto.setSgstMatch(true);
		dto.setCessMatch(true);

		if (docType2A != null && (docType2A.equalsIgnoreCase("CR")
				|| docType2A.equalsIgnoreCase("C")
				|| docType2A.equalsIgnoreCase("RCR"))) {

			dto.setTotalTax2A(checkForNegativeValue(arr[9]));
			dto.setIgst2A(checkForNegativeValue(arr[11]));
			dto.setCgst2A(checkForNegativeValue(arr[13]));
			dto.setSgst2A(checkForNegativeValue(arr[15]));
			dto.setCess2A(checkForNegativeValue(arr[17]));

		} else {

			dto.setTotalTax2A(checkNull(arr[9]));
			dto.setIgst2A(checkNull(arr[11]));
			dto.setCgst2A(checkNull(arr[13]));
			dto.setSgst2A(checkNull(arr[15]));
			dto.setCess2A(checkNull(arr[17]));

		}

		if ((arr[18] != null && (arr[26] != null
				&& arr[26].toString().equalsIgnoreCase(APIConstants.addition2A)
				|| arr[26].toString()
						.equalsIgnoreCase(APIConstants.ADDITION_2A_IMPG)))) {
			dto.setMismatchReason(checkNull(arr[18]));
		} else {
			dto.setMismatchReason(checkNull(arr[18]));
		}

		setOtherValues(arr, dto);

	}

	private String checkNull(Object obj) {
		return (obj != null) ? obj.toString() : null;
	}

	private boolean isMatch(String str1, String str2) {
		return str1 != null && str1.equalsIgnoreCase(str2);
	}

	private boolean isBigDecimalMatch(BigDecimal bg1, BigDecimal bg2) {

		return bg1.compareTo(bg2) == 0 ? true : false;
	}

	private String checkForNegativeValue(Object value) {
		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null ? (((Integer) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof Long) {
				return (value != null ? (((Long) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else {
				if (!value.toString().isEmpty()) {
					return "-" + value.toString().replaceFirst("-", "");
				} else {
					return null;
				}
			}
		}
		return null;
	}

}
