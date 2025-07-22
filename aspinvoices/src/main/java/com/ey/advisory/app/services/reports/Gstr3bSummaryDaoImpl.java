package com.ey.advisory.app.services.reports;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.service.gstr3bSummary.Gstr3bSummaryUploadDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr3bSummaryDaoImpl")
public class Gstr3bSummaryDaoImpl implements Gstr3bSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Gstr3bSummaryUploadDto> getTotalRecords(Integer fileId) {

		if (LOGGER.isDebugEnabled()) {

			String msg = String
					.format("Getting the getTotalRecords for fileid in DAO: [%s], "
							+ "TaxPeriod: %s", fileId);
			LOGGER.debug(msg);
		}

		String queryString = createQueryString(fileId);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for getTotalRecords : %s",
					queryString);
			LOGGER.debug(str);
		}

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("fileID", fileId);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Gstr3bSummaryUploadDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;

	}

	private Gstr3bSummaryUploadDto convert(Object[] arr) {

		Gstr3bSummaryUploadDto dto = new Gstr3bSummaryUploadDto();

		 dto.setErrorCode((String) arr[0]);
		 dto.setErrorDesc((String) arr[1]);
		dto.setReturnPeriod((String) arr[2]);
		 dto.setGstin((String) arr[3]);
		 dto.setTableNumber((String) arr[4]);
		 dto.setTotalTaxableValue((String) arr[5]);
		dto.setIgstAmount((String) arr[6]);
		dto.setCgstAmount((String) arr[7]);
		 dto.setSgstAmount((String) arr[8]);
		 dto.setCessAmount(((String) arr[9]));
		 dto.setPos((String) arr[10]);
		// ? appendDecimalDigit(arr[10]) : null);
		// dto.setStatutoryDeductionsApplicable((String) arr[11]);
		// dto.setStatutoryDeductionAmount((String) arr[12] != null
		// ? appendDecimalDigit(arr[12]) : null);
		// dto.setAnyOtherDeductionAmount((String) arr[13] != null
		// ? appendDecimalDigit(arr[13]) : null);
		// dto.setRemarksforDeductions((String) arr[14]);
		// dto.setDueDateofPayment(localDateStart((String) arr[15]));
		// dto.setPaymentReferenceNumber((String) arr[16]);
		// dto.setPaymentReferenceDate(localDateStart((String) arr[17]));
		// dto.setPaymentDescription((String) arr[18]);
		// dto.setPaymentStatus((String) arr[19]);
		// dto.setPaidAmounttoSupplier((String) arr[20] != null
		// ? appendDecimalDigit(arr[20]) : null);
		// dto.setCurrencyCode((String) arr[21]);
		// dto.setExchangeRate((String) arr[22]);
		// dto.setUnpaidAmounttoSupplier((String) arr[23] != null
		// ? appendDecimalDigit(arr[23]) : null);
		// dto.setPostingDate(localDateStart((String) arr[24]));
		// dto.setPlantCode((String) arr[25]);
		// dto.setProfitCentre((String) arr[26]);
		// dto.setDivision((String) arr[27]);
		// dto.setUserDefinedField1((String) arr[28]);
		// dto.setUserDefinedField2((String) arr[29]);
		// dto.setUserDefinedField3((String) arr[30]);

		return dto;
	}

	private String createQueryString(Integer fileId) {

		String totalRecords = "" + "SELECT " + "ERROR_CODE, "
				+ "ERROR_DESCRIPTION, " + "    ACTION_TYPE, "
				+ "    CUST_GSTIN, " + "    SUPP_GSTIN, " + "    SUPP_NAME, "
				+ "    SUPP_CODE, " + "    DOC_TYPE, " + "    DOC_NUM, "
				+ "    DOC_DATE, " + "    INV_VAL, "
				+ "    STATUTORY_DEDUCTIONS_APPL, "
				+ "    STATUTORY_DEDUCTION_AMT, "
				+ "    ANY_OTHER_DEDUCTION_AMT, "
				+ "    REMARKS_FOR_DEDUCTIONS, " + "    DUE_DATE_OF_PAYMENT, "
				+ "    PAYMENT_REF_NUM, " + "    PAYMENT_REF_DATE, "
				+ "    PAYMENT_DESC, " + "    PAYMENT_STATUS, "
				+ "    PAID_AMT_TO_SUPP, " + "    CURRENCY_CODE, "
				+ "    EXCHANGE_RATE, " + "    UNPAID_AMT_TO_SUPPLIER, "
				+ "    POSTING_DATE, " + "    PLANT_CODE, "
				+ "    PROFIT_CENTRE, " + "    DIVISION, "
				+ "    USER_DEFINED_FIELD_1, " + "    USER_DEFINED_FIELD_2, "
				+ "    USER_DEFINED_FIELD_3 "
				+ "FROM TBL_180_DAYS_REVERSAL_ERR " + "WHERE FILE_ID = :fileID "
				+ "UNION ALL " + "SELECT " + " ERROR_CODE, "
				+ " ERROR_DESCRIPTION, " + "    TO_VARCHAR(ACTION_TYPE), "
				+ "    TO_VARCHAR(CUST_GSTIN), "
				+ "    TO_VARCHAR(SUPP_GSTIN), " + "    TO_VARCHAR(SUPP_NAME), "
				+ "    TO_VARCHAR(SUPP_CODE), " + "    TO_VARCHAR(DOC_TYPE), "
				+ "    TO_VARCHAR(DOC_NUM), " + "    TO_VARCHAR(DOC_DATE), "
				+ "    TO_VARCHAR(INV_VAL), "
				+ "    TO_VARCHAR(STATUTORY_DEDUCTIONS_APPL), "
				+ "    TO_VARCHAR(STATUTORY_DEDUCTION_AMT), "
				+ "    TO_VARCHAR(ANY_OTHER_DEDUCTION_AMT), "
				+ "    TO_VARCHAR(REMARKS_FOR_DEDUCTIONS), "
				+ "    TO_VARCHAR(DUE_DATE_OF_PAYMENT), "
				+ "    TO_VARCHAR(PAYMENT_REF_NUM), "
				+ "    TO_VARCHAR(PAYMENT_REF_DATE), "
				+ "    TO_VARCHAR(PAYMENT_DESC), "
				+ "    TO_VARCHAR(PAYMENT_STATUS), "
				+ "    TO_VARCHAR(PAID_AMT_TO_SUPP), "
				+ "    TO_VARCHAR(CURRENCY_CODE), "
				+ "    TO_VARCHAR(EXCHANGE_RATE), "
				+ "    TO_VARCHAR(UNPAID_AMT_TO_SUPPLIER), "
				+ "    TO_VARCHAR(POSTING_DATE), "
				+ "    TO_VARCHAR(PLANT_CODE), "
				+ "    TO_VARCHAR(PROFIT_CENTRE), "
				+ "    TO_VARCHAR(DIVISION), "
				+ "    TO_VARCHAR(USER_DEFINED_FIELD_1), "
				+ "    TO_VARCHAR(USER_DEFINED_FIELD_2), "
				+ "    TO_VARCHAR(USER_DEFINED_FIELD_3) "
				+ "FROM TBL_180_DAYS_REVERSAL_PSD " + "WHERE FILE_ID = :fileID";
		return totalRecords;
	}

	private String localDateStart(String datetime) {
		String[] datetimeArray = datetime != null ? datetime.split("T") : null;
		String dateStart = datetimeArray != null ? datetimeArray[0] : null;
		return dateStart;
	}

	private String appendDecimalDigit(Object obj) {

		try {
			if (isPresent(obj)) {
				if (NumberFomatUtil.isNumber(obj)) {

					BigDecimal b = new BigDecimal(obj.toString());
					String val = b.setScale(2, BigDecimal.ROUND_HALF_UP)
							.toPlainString();

					String[] s = val.split("\\.");
					if (s.length == 2) {
						if (s[1].length() == 1) {
							val = "'" + (s[0] + "." + s[1] + "0");
							return val;
						} else {
							val = "'" + val;
							return val;
						}
					} else {
						val = "'" + (val + ".00");
						return val;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Gstr3bSummaryDaoImpl AppendDecimalDigit method {}",
					obj);
			return obj != null ? "'".concat(obj.toString()) : null;
		}
		return obj.toString();
	}

}
