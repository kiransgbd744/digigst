package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("Gstr1Outward3FioriDashboardServiceImpl")
public class Gstr1Outward3FioriDashboardServiceImpl
		implements Gstr1Outward3FioriDashboardService {

	@Autowired
	@Qualifier("Gstr1Outward3FioriDashboardDaoImpl")
	private Gstr1Outward3FioriDashboardDao dashboardDao;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Override
	public Outward3HeaderDetailsDto getOutward3HeaderData(String fy,
			List<String> supplierGstins, List<String> taxPeriods) {
		try {
			Outward3HeaderDetailsDto dto = new Outward3HeaderDetailsDto();
			List<Object[]> list = dashboardDao.getAllHeadersData(fy,
					supplierGstins, taxPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getOutward3HeaderData for fy %s"
								+ ", supplierGstins %s and taxPeriods :%s",
						list, fy, supplierGstins, taxPeriods);
				LOGGER.debug(msg);
			}
			if (list.isEmpty())
				return dto;

			Object[] obj = list.get(0);
			dto.setItcBal(
					obj[0] != null ? (BigDecimal) obj[0] : BigDecimal.ZERO);
			dto.setTotalLiab(
					obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);
			dto.setPaidThrItc(
					obj[2] != null ? (BigDecimal) obj[2] : BigDecimal.ZERO);
			dto.setNetLiab(
					obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
			dto.setItcBalIgst(
					obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
			dto.setItcBalCgst(
					obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
			dto.setItcBalSgst(
					obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
			dto.setItcBalCess(
					obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO);
			dto.setInterestPybl(
					obj[8] != null ? (BigDecimal) obj[8] : BigDecimal.ZERO);
			dto.setLateFeePybl(
					obj[9] != null ? (BigDecimal) obj[9] : BigDecimal.ZERO);

			String refreshedOn = obj[10] != null ? (String) obj[10] : null;
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
					"Error while getOutward3HeaderData for fy %s"
							+ ", supplierGstins %s and taxPeriods :%s",
					fy, supplierGstins, taxPeriods);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}

	}

	@Override
	public List<Gstr1FioriDashboardChartDto> getUtilizationSummData(String fy,
			List<String> supplierGstins, List<String> taxPeriods) {
		try {
			List<Object[]> revenueCompAnalysis = dashboardDao
					.getUtilizationSummaryData(fy, supplierGstins, taxPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getUtilizationSummData for fy %s"
								+ ", supplierGstins %s and taxPeriods :%s",
						revenueCompAnalysis, fy, supplierGstins, taxPeriods);
				LOGGER.debug(msg);
			}
			if (revenueCompAnalysis.isEmpty())
				return null;
			return revenueCompAnalysis.stream().map(o -> convertToChartDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getUtilizationSummData for fy %s"
							+ ", supplierGstins %s and taxPeriods :%s",
					fy, supplierGstins, taxPeriods);
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

	@Override
	public List<Gstr1FioriDashboardChartDto> getGstNetLiabDetails(String fy,
			List<String> supplierGstins, List<String> taxPeriods) {
		try {
			List<Object[]> revenueCompAnalysis = dashboardDao
					.getGstNetLiabilityData(fy, supplierGstins, taxPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getGstNetLiabDetails for fy %s"
								+ ", supplierGstins %s and taxPeriods :%s",
						revenueCompAnalysis, fy, supplierGstins, taxPeriods);
				LOGGER.debug(msg);
			}
			if (revenueCompAnalysis.isEmpty())
				return null;
			return revenueCompAnalysis.stream().map(o -> convertToChartDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getGstNetLiabDetails for fy %s"
							+ ", supplierGstins %s and taxPeriods :%s",
					fy, supplierGstins, taxPeriods);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<GetLiabTableDto> getLiabilityTbleDetails(String fy,
			List<String> supplierGstins, List<String> taxPeriods) {
		try {
			List<Object[]> liabTableData = dashboardDao
					.getLiabilityTableData(fy, supplierGstins, taxPeriods);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getLiabilityTbleDetails for fy %s"
								+ ", supplierGstins %s and taxPeriods :%s",
						liabTableData, fy, supplierGstins, taxPeriods);
				LOGGER.debug(msg);
			}
			if (liabTableData.isEmpty())
				return null;
			return liabTableData.stream().map(o -> convertToLiabTabDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getLiabilityTbleDetails for fy %s"
							+ ", supplierGstins %s and taxPeriods :%s",
					fy, supplierGstins, taxPeriods);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private GetLiabTableDto convertToLiabTabDto(Object[] obj) {
		GetLiabTableDto dto = new GetLiabTableDto();
		dto.setGstin((String) obj[0]);
		dto.setLiabFrwdChrg(
				obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);
		dto.setLiabRevChrg(
				obj[2] != null ? (BigDecimal) obj[2] : BigDecimal.ZERO);
		dto.setInterest(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
		dto.setLateFee(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
		dto.setPaidThrItc(
				obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
		dto.setCashPybl(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
		return dto;
	}

	@Override
	public List<GstinDto> getAllSupplierGstins(Long entityId) {

		List<String> supplierPanList = entityInfoRepository
				.findPanByEntityId(entityId);

		String supplierPan = supplierPanList.get(0);

		List<String> list = dashboardDao.getSupGstinList(supplierPan);

		List<String> reqGstins = gSTNDetailRepository
				.findgstinByEntityIdWithRegTypeForGstr1(entityId);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Fetched %s supplierGstins for pan %s",
					list.size(), supplierPan);
			LOGGER.debug(msg);
		}
		return list.stream().filter(c -> reqGstins.contains(c))
				.map(o -> convertToGstins(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GstinDto convertToGstins(String gstin) {
		GstinDto obj = new GstinDto();
		obj.setGstin((String) gstin);
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

}
