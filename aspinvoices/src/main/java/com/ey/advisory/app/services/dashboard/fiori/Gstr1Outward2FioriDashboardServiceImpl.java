package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Service("Gstr1Outward2FioriDashboardServiceImpl")
@Slf4j
public class Gstr1Outward2FioriDashboardServiceImpl
		implements Gstr1Outward2FioriDashboardService {

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("Gstr1Outward2FioriDashboardDaoImpl")
	private Gstr1Outward2FioriDashboardDao dashboardDao;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;
	
	@Autowired
	DefaultStateCache defaultStateCache;

	@Override
	public Outward2HeaderDetailsDto getOutward2HeaderData(String fy,
			List<String> supplierGstins, List<String> returnPeriods) {
		try {
			Outward2HeaderDetailsDto dto = new Outward2HeaderDetailsDto();
			List<Object[]> list = dashboardDao.getAllHeadersData(fy,
					supplierGstins, returnPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getOutward2HeaderData for fy %s"
								+ ", supplierGstins %s and reutrnPeriods :%s",
						list, fy, supplierGstins, returnPeriods);
				LOGGER.debug(msg);
			}
			if (list.isEmpty())
				return dto;

			Object[] obj = list.get(0);
			dto.setNoOfInvoices(
					obj[0] != null ? (GenUtil.getBigInteger(obj[0])).longValue() : 0);
			dto.setNoOfCreditNotes(
					obj[1] != null ? (GenUtil.getBigInteger(obj[1])).longValue() : 0);
			dto.setNoOfDebitNotes(
					obj[2] != null ? (GenUtil.getBigInteger(obj[2])).longValue() : 0);
			dto.setNoOfSelfInvoices(
					obj[3] != null ? (GenUtil.getBigInteger(obj[3])).longValue() : 0);
			dto.setNoOfDlc(
					obj[4] != null ? (GenUtil.getBigInteger(obj[4])).longValue() : 0);
			dto.setNoOfB2BCustomers(
					obj[5] != null ? (GenUtil.getBigInteger(obj[5])).longValue() : 0);
			dto.setTotalTurnOver(
					obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
			dto.setTotalTax(
					obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO);

			String refreshedOn = obj[8] != null ? (String) obj[8] : null;
			if (refreshedOn != null) {
				refreshedOn = refreshedOn.replace(" ", "T");
				refreshedOn = refreshedOn.substring(0, 22);
				String itc = EYDateUtil
						.toISTDateTimeFromUTC(LocalDateTime.parse(refreshedOn))
						.toString();
				itc = itc.substring(0, 19);
				itc = itc.replace("T", " ");
				dto.setLastRefreshedOn(itc);
			}
			return dto;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getOutward2HeaderData for fy %s"
							+ ", supplierGstins %s and reutrnPeriods :%s",
					fy, supplierGstins, returnPeriods);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<B2bCntDto> getPsdVsErrRecords(String fy,
			List<String> supplierGstins, List<String> returnPeriods) {
		try {
			List<B2bCntDto> psdVsErrList = new ArrayList<>();
			B2bCntDto dto1 = new B2bCntDto();
			B2bCntDto dto2 = new B2bCntDto();
			List<Object[]> list = dashboardDao.getPsdVsErrData(fy,
					supplierGstins, returnPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getPsdVsErrRecords for fy %s"
								+ ", supplierGstins %s and reutrnPeriods :%s",
						list, fy, supplierGstins, returnPeriods);
				LOGGER.debug(msg);
			}

			Object[] obj = list.get(0);
			dto1.setXAxis("Processed");
			dto1.setYAxis(
					obj[0] != null ? (GenUtil.getBigInteger(obj[0])).longValue() : 0);
			dto2.setXAxis("Error");
			dto2.setYAxis(
					obj[1] != null ? (GenUtil.getBigInteger(obj[1])).longValue() : 0);
			psdVsErrList.add(dto1);
			psdVsErrList.add(dto2);
			return psdVsErrList;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getPsdVsErrRecords for fy %s"
							+ ", supplierGstins %s and reutrnPeriods :%s",
					fy, supplierGstins, returnPeriods);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Gstr1FioriDashboardChartDto> getRevenueComparativeAnalysis(
			String fy, String valueFlag, List<String> supplierGstins,
			List<String> retunPeriods) {
		try {
			List<Object[]> revenueCompAnalysis = dashboardDao
					.getRevenueComparativeAnalysis(fy, valueFlag,
							supplierGstins, retunPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getRevenueComparativeAnalysis for fy %s"
								+ ", supplierGstins %s and reutrnPeriods :%s of :%s",
						revenueCompAnalysis, fy, supplierGstins, retunPeriods,
						valueFlag);
				LOGGER.debug(msg);
			}
			return revenueCompAnalysis.stream().map(o -> convertToChartDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getRevenueComparativeAnalysis for fy %s"
							+ ", supplierGstins %s and reutrnPeriods :%s",
					fy, supplierGstins, retunPeriods);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<B2bCntDto> getRevenueComparativeAnalysisForB2b(String fy,
			String valueFlag, List<String> supplierGstins,
			List<String> retunPeriods) {
		try {
			List<Object[]> revenueCompAnalysis = dashboardDao
					.getRevenueComparativeAnalysis(fy, valueFlag,
							supplierGstins, retunPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getRevenueComparativeAnalysisForB2b for fy %s"
								+ ", supplierGstins %s and reutrnPeriods :%s of :%s",
						revenueCompAnalysis, fy, supplierGstins, retunPeriods,
						valueFlag);
				LOGGER.debug(msg);
			}
			return revenueCompAnalysis.stream().map(o -> convertToB2BDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getRevenueComparativeAnalysisForB2b for fy %s"
							+ ", supplierGstins %s and reutrnPeriods :%s",
					fy, supplierGstins, retunPeriods);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private Gstr1FioriDashboardChartDto convertToChartDto(Object[] obj) {
		Gstr1FioriDashboardChartDto dto = new Gstr1FioriDashboardChartDto();
		dto.setXAxis((String) obj[0]);
		dto.setYAxis(obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);
		return dto;
	}

	private B2bCntDto convertToB2BDto(Object[] obj) {
		B2bCntDto dto = new B2bCntDto();
		dto.setXAxis((String) obj[0]);
		dto.setYAxis(obj[1] != null ? (GenUtil.getBigInteger(obj[1])).longValue() : 0);
		return dto;
	}

	@Override
	public List<OutwardSupplyDetailsDto> getOutwardSuppliyDetails(String fy,
			List<String> supplierGstins, List<String> returnPeriods) {
		try {
			List<Object[]> supplyDetails = dashboardDao
					.getOutwardSupplyDetails(fy, supplierGstins, returnPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getOutwardSuppliyDetails for fy %s"
								+ ", supplierGstins %s and reutrnPeriods :%s",
						supplyDetails.size(), fy, supplierGstins,
						returnPeriods);
				LOGGER.debug(msg);
			}
			return supplyDetails.stream().map(o -> convertToSuppDetailsDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getOutwardSuppliyDetails for fy %s"
							+ ", supplierGstins %s and reutrnPeriods :%s",
					fy, supplierGstins, returnPeriods);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private OutwardSupplyDetailsDto convertToSuppDetailsDto(Object[] obj) {
		OutwardSupplyDetailsDto dto = new OutwardSupplyDetailsDto();
		dto.setGstin((String) obj[0]);
		dto.setStateName((String) obj[1]);
		dto.setRegType((String) obj[2]);
		dto.setIgstAmt((BigDecimal) obj[3]);
		dto.setCgstAmt((BigDecimal) obj[4]);
		dto.setSgstAmt((BigDecimal) obj[5]);
		dto.setCessAmt((BigDecimal) obj[6]);
		dto.setTotalTax((BigDecimal) obj[7]);
		dto.setInvoiceVal((BigDecimal) obj[8]);
		dto.setTaxableVal((BigDecimal) obj[9]);
		return dto;
	}

	@Override
	public List<GstinDto> getAllSupplierGstins(List<Long> entityIds) {

		List<String> supplierPanList = entityInfoRepository
				.findPanByEntityIds(entityIds);
		/*
		String supplierPan = supplierPanList.get(0);

		List<String> list = dashboardDao.getSupGstinList(supplierPan);*/
		
		List<String> list = new ArrayList<>();

		for (String supplierPan : supplierPanList) {
		    List<String> gstinListForPan = dashboardDao.getSupGstinList(supplierPan);
		    list.addAll(gstinListForPan);
		}

		List<String> reqGstins = gSTNDetailRepository
				.findgstinByEntityIdsWithRegTypeForGstr1(entityIds);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Fetched %s supplierGstins for pan %s",
					list.size(), supplierPanList);
			LOGGER.debug(msg);
		}
		return list.stream().filter(c -> reqGstins.contains(c))
				.map(o -> convertToGstins(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GstinDto convertToGstins(String gstin) {
		GstinDto obj = new GstinDto();
		obj.setGstin((String) gstin);
		String stateCode = (obj.getGstin()).substring(0, 2);
		String stateName = defaultStateCache.getStateName(stateCode);
		obj.setStateName(stateName);
		return obj;
	}

	@Override
	public List<FinancialYearDto> getAllReturnPeriods(String fy) {
		List<String> list = dashboardDao.getAllReturnPeriods(fy);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s returnPeriods for" + " fy %s", list.size(), fy);
			LOGGER.debug(msg);
		}
		return list.stream().map(o -> convertToReturnPeriods(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private FinancialYearDto convertToReturnPeriods(String returnPeriod) {
		FinancialYearDto obj = new FinancialYearDto();
		obj.setFy((String) returnPeriod);
		return obj;
	}

	@Override
	public Outward2TurnOverDto getTotalTurnOverAndTax(String fy,
			List<String> supplierGstins, List<String> returnPeriods) {
		try {
			Outward2TurnOverDto dto = new Outward2TurnOverDto();
			List<Object[]> list = dashboardDao.getTotalTurnOverAndTax(fy,
					supplierGstins, returnPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getTotalTurnOverAndTax for fy %s"
								+ ", supplierGstins %s and reutrnPeriods :%s",
						list, fy, supplierGstins, returnPeriods);
				LOGGER.debug(msg);
			}
			if (list.isEmpty())
				return dto;

			Object[] obj = list.get(0);
			dto.setTurnOver(
					obj[0] != null ? (BigDecimal) obj[0] : BigDecimal.ZERO);
			dto.setTax(obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);

			return dto;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getTotalTurnOverAndTax for fy %s"
							+ ", supplierGstins %s and reutrnPeriods :%s",
					fy, supplierGstins, returnPeriods);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

}
