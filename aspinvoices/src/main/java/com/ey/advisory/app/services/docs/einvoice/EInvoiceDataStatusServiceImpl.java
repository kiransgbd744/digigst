package com.ey.advisory.app.services.docs.einvoice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.EInvoiceDataStatusSearchScreenDao;
import com.ey.advisory.app.data.entities.client.Anx1NewDataStatusEntity;
import com.ey.advisory.app.services.search.datastatus.anx1.Anx1DataStatusService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Umesha.M
 *
 *         This class represent Getting count of E Invoice Data Status values
 *         for Data Status Screen
 */
@Service("EInvoiceDataStatusServiceImpl")
public class EInvoiceDataStatusServiceImpl implements SearchService {

	@Autowired
	@Qualifier("EInvoiceDataStatusSearchScreenDaoImpl")
	EInvoiceDataStatusSearchScreenDao einvoiceDataStatusSearchScreenDao;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvoiceDataStatusServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		DataStatusSearchReqDto req = (DataStatusSearchReqDto) criteria;
		LocalDate recivedFromDate = req.getDataRecvFrom();
		LocalDate recivedToDate = req.getDataRecvTo();
		LocalDate docFromdate = req.getDocDateFrom();
		LocalDate docTodate = req.getDocDateTo();
		LocalDate documentToDate = req.getDocumentDateTo();
		LocalDate documentFromDate = req.getDocumentDateFrom();
		LocalDate accVoucherFromDate = req.getAccVoucherDateFrom();
		LocalDate accVoucherToDate = req.getAccVoucherDateTo();
		String retunPeriodFrom = req.getRetPeriodFrom();
		String retunPeriodTo = req.getRetPeriodTo();
		String dataType = req.getDataType();

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String profitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String gstin = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					if (dataSecAttrs.get(OnboardingConstant.PC) != null
							&& !dataSecAttrs.get(OnboardingConstant.PC)
									.isEmpty()) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					if (dataSecAttrs.get(OnboardingConstant.PLANT) != null
							&& !dataSecAttrs.get(OnboardingConstant.PLANT)
									.isEmpty()) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (dataSecAttrs.get(OnboardingConstant.DIVISION) != null
							&& !dataSecAttrs.get(OnboardingConstant.DIVISION)
									.isEmpty()) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (dataSecAttrs.get(OnboardingConstant.LOCATION) != null
							&& !dataSecAttrs.get(OnboardingConstant.LOCATION)
									.isEmpty()) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (dataSecAttrs.get(OnboardingConstant.SO) != null
							&& !dataSecAttrs.get(OnboardingConstant.SO)
									.isEmpty()) {
						salesList = dataSecAttrs.get(OnboardingConstant.SO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& !dataSecAttrs.get(OnboardingConstant.PO)
									.isEmpty()) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& !dataSecAttrs.get(OnboardingConstant.DC)
									.isEmpty()) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD1)
									.isEmpty()) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD2)
									.isEmpty()) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD3)
									.isEmpty()) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD4)
									.isEmpty()) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD5)
									.isEmpty()) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD6)
									.isEmpty()) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Profit Center List:{} ", pcList);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Plant Code List:{} ", pcList);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstin List:{} ", gstinList);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstin {} ", gstin);
		}
		
		List<Anx1NewDataStatusEntity> anx1Response = new ArrayList<>();
		StringBuilder build = new StringBuilder();
		if (dataType.equalsIgnoreCase("outward")) {
			if (gstin != null && !gstin.isEmpty() && gstinList != null
					&& !gstinList.isEmpty()) {
				build.append(
						" AND ((HDR.SUPPLIER_GSTIN IN :gstinList AND HDR.TRANS_TYPE='O')  OR (HDR.TRANS_TYPE='I' AND HDR.CUST_GSTIN IN :gstinList)) ");
			}
		} else if ("ITC-04".equalsIgnoreCase(dataType)) {
			if (gstin != null && !gstin.isEmpty() && gstinList != null
					&& !gstinList.isEmpty()) {
				build.append(" AND HDR.SUPPLIER_GSTIN  IN :gstinList ");

			}
		} 
		// GSTR7 TXN
        else if(dataType.equalsIgnoreCase("gstr7txn")){
        	
        	if (gstin != null && !gstin.isEmpty() && gstinList != null
					&& !gstinList.isEmpty()) {
				build.append(" AND HDR.DEDUCTOR_GSTIN IN (:gstinList) ");
			}
			
		} else {
			if (gstin != null && !gstin.isEmpty() && gstinList != null
					&& !gstinList.isEmpty()) {
				build.append(" AND HDR.CUST_GSTIN  IN :gstinList ");

			}
		}
		
		// setting data security parameter
		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& !pcList.isEmpty()) {
			build.append(" AND PROFIT_CENTRE IN :pcList");

		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& !plantList.isEmpty()) {
			build.append(" AND PLANT_CODE IN :plantList");
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& !salesList.isEmpty()) {
			build.append(" AND SALES_ORGANIZATION IN :salesList");
		}
		if (purchase != null && !purchase.isEmpty() && purcList != null
				&& !purcList.isEmpty()) {
			build.append(" AND PURCHASE_ORGANIZATION IN :purcList");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& !distList.isEmpty()) {
			build.append(" AND DISTRIBUTION_CHANNEL IN :distList");
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& !divisionList.isEmpty()) {
			build.append(" AND DIVISION IN :divisionList");
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& !locationList.isEmpty()) {
			build.append(" AND LOCATION IN :locationList");
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& !ud1List.isEmpty()) {
			build.append(" AND USERACCESS1 IN :ud1List");
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& !ud2List.isEmpty()) {
			build.append(" AND USERACCESS2 IN :ud2List");
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& !ud3List.isEmpty()) {
			build.append(" AND USERACCESS3 IN :ud3List");
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& !ud4List.isEmpty()) {
			build.append(" AND USERACCESS4 IN :ud4List");
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& !ud5List.isEmpty()) {
			build.append(" AND USERACCESS5 IN :ud5List");
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& !ud6List.isEmpty()) {
			build.append(" AND USERACCESS6 IN :ud6List");
		}
		
		//GATR7 TXN
		if (dataType.equalsIgnoreCase("gstr7txn")) {

			if (recivedFromDate != null && recivedToDate != null) {
				build.append(
						" AND TO_VARCHAR(HDR.CREATED_ON, 'YYYY-MM-DD') "
						+ " BETWEEN :recivedFromDate AND :recivedToDate");

			}
		}
		//vendor_payment
		else if (dataType.equalsIgnoreCase("vendor_payment")) {

			if (recivedFromDate != null && recivedToDate != null) {
				build.append(
						" AND TO_VARCHAR(HDR.CREATED_ON, 'YYYY-MM-DD')"
						+ " BETWEEN :recivedFromDate AND :recivedToDate");

			}
		} else {
		if (recivedFromDate != null && recivedToDate != null) {
			build.append(" AND RECEIVED_DATE BETWEEN :recivedFromDate "
					+ "AND :recivedToDate");
		}

		else if (retunPeriodFrom != null && retunPeriodTo != null) {
			build.append(" AND DERIVED_RET_PERIOD BETWEEN "
					+ ":retunPeriodFrom AND :retunPeriodTo");
		}

		else if (docFromdate != null && docTodate != null) {
			build.append(" AND HDR.DOC_DATE BETWEEN :docFromdate "
					+ "AND :docTodate");
		}
		
		else if (documentFromDate != null && documentToDate != null) {
			if("inward".equalsIgnoreCase(dataType)||"outward".equalsIgnoreCase(dataType)){
				build.append(" AND HDR.DOC_DATE BETWEEN :documentFromDate "
						+ "AND :documentToDate");	
		}
		}
		else if (accVoucherFromDate != null && accVoucherToDate != null) {
			if("inward".equalsIgnoreCase(dataType)){
				build.append(" AND HDR.PURCHASE_VOUCHER_DATE BETWEEN :accVoucherFromDate "
						+ "AND :accVoucherToDate");	
			}
			else if("outward".equalsIgnoreCase(dataType)){
				build.append(" AND HDR.ACCOUNTING_VOUCHER_DATE BETWEEN :accVoucherFromDate "
						+ "AND :accVoucherToDate");	
			}
	    }
		}
		String buildQuery = build.toString();

		if ((retunPeriodFrom != null && !retunPeriodFrom.trim().isEmpty())
				&& (retunPeriodTo != null && !retunPeriodTo.trim().isEmpty())) {

			anx1Response = einvoiceDataStatusSearchScreenDao
					.dataAnx1NewStatusSection("RETURN_PERIOD", req, buildQuery,
							dataType, retunPeriodFrom, retunPeriodTo);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Datastatus TaxPeriod --> After Executing "
						+ "Dao, Response -----> ", anx1Response);
			}
		}

		else if ((recivedFromDate != null && recivedFromDate.lengthOfYear() > 0)
				&& (recivedToDate != null
						&& recivedToDate.lengthOfYear() > 0)) {

			anx1Response = einvoiceDataStatusSearchScreenDao
					.dataAnx1NewStatusSection("DATA_RECEIVED", req, buildQuery,
							dataType, recivedFromDate, recivedToDate);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Datastatus Receive Date --> After Executing "
						+ "Dao, Response -----> ", anx1Response);
			}
		}
		else if ((documentFromDate != null && documentToDate.lengthOfYear() > 0)
				&& (documentFromDate != null
						&& documentToDate.lengthOfYear() > 0)) {

			anx1Response = einvoiceDataStatusSearchScreenDao
					.dataAnx1NewStatusSection("DOCUMENT_DATE", req, buildQuery,
							dataType, documentFromDate, documentToDate);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Datastatus Document Date --> After Executing "
						+ "Dao, Response -----> ", anx1Response);
			}
		}
		else if ((accVoucherFromDate != null && accVoucherToDate.lengthOfYear() > 0)
				&& (accVoucherFromDate != null
						&& accVoucherToDate.lengthOfYear() > 0)) {

			anx1Response = einvoiceDataStatusSearchScreenDao
					.dataAnx1NewStatusSection("ACCOUNTING_VOUCHER_DATE", req, buildQuery,
							dataType, accVoucherFromDate, accVoucherToDate);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Datastatus Accounting Voucher Date --> After Executing "
						+ "Dao, Response -----> ", anx1Response);
			}
		}
		return (SearchResult<R>) new SearchResult<>(anx1Response);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		return null;
	}
}
