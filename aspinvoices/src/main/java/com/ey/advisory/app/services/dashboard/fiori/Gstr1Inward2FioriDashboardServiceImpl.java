package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Service("Gstr1Inward2FioriDashboardServiceImpl")
@Slf4j
public class Gstr1Inward2FioriDashboardServiceImpl
		implements Gstr1Inward2FioriDashboardService {

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("Gstr1Inward2FioriDashboardDaoImpl")
	private Gstr1Inward2FioriDashboardDao dashboardDao;

	@Override
	public List<Inward2HeaderDetailsDto> getInward2HeaderData(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds) {
		try {
			@SuppressWarnings("unused")
			Inward2HeaderDetailsDto dto = new Inward2HeaderDetailsDto();
			List<Object[]> list = dashboardDao.getAllHeadersData(fy,
					listOfRecepGstin, listOfReturnPrds);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getInward2HeaderData for fy %s"
								+ ", listOfRecepGstin %s and listOfReturnPrds :%s",
						list, fy, listOfRecepGstin, listOfReturnPrds);
				LOGGER.debug(msg);
			}

			return list.stream().map(o -> convertToInward2HeaderData(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format(
					"Error while getInward2HeaderData for fy %s"
							+ ", listOfRecepGstin %s and listOfReturnPrds :%s",
					fy, listOfRecepGstin, listOfReturnPrds);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private Inward2HeaderDetailsDto convertToInward2HeaderData(Object[] obj) {
		Inward2HeaderDetailsDto dto = new Inward2HeaderDetailsDto();
		dto.setNoOfSuppliersPr(
				obj[0] != null ? (GenUtil.getBigInteger(obj[0])).longValue() : 0);
		dto.setNoOfSuppliers2a(
				obj[1] != null ? (GenUtil.getBigInteger(obj[1])).longValue() : 0);
		dto.setNoOfSuppliers2b(
				obj[2] != null ? (GenUtil.getBigInteger(obj[2])).longValue() : 0);
		dto.setTotalTaxPr(obj[3] != null ? (BigDecimal) obj[3] : null);
		dto.setTotalTax2a(obj[4] != null ? (BigDecimal) obj[4] : null);
		dto.setTotalTax2b(obj[5] != null ? (BigDecimal) obj[5] : null);
		return dto;
	}

	@Override
	public List<Top10SuppliersDto> getprVsGstr2aRecords(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds) {
		try {
			List<Object[]> prVsGstr2aRecords = dashboardDao
					.getprVsGstr2aRecords(fy, listOfRecepGstin,
							listOfReturnPrds);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getprVsGstr2bRecords for fy %s"
								+ ", listOfRecepGstin %s and reutrnPeriods :%s of :%s",
						prVsGstr2aRecords, fy, listOfRecepGstin,
						listOfReturnPrds);
				LOGGER.debug(msg);
			}
			return prVsGstr2aRecords.stream().map(o -> convertToB2BDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getprVsGstr2aRecords for fy %s"
							+ ", listOfRecepGstin %s and listOfReturnPrds :%s",
					fy, listOfRecepGstin, listOfReturnPrds);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private Top10SuppliersDto convertToB2BDto(Object[] obj) {
		Top10SuppliersDto dto = new Top10SuppliersDto();
		dto.setGstin((String) obj[0]);
		dto.setPrTotalTax(obj[1] != null ? ((BigDecimal) obj[1]) : null);
		dto.setGstr2(obj[2] != null ? ((BigDecimal) obj[2]) : null);
		return dto;
	}

	@Override
	public List<Top10SuppliersDto> getprVsGstr2bRecords(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds) {
		try {
			List<Object[]> prVsGstr2bRecords = dashboardDao
					.getprVsGstr2bRecords(fy, listOfRecepGstin,
							listOfReturnPrds);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getprVsGstr2bRecords for fy %s"
								+ ", listOfRecepGstin %s and reutrnPeriods :%s of :%s",
						prVsGstr2bRecords, fy, listOfRecepGstin,
						listOfReturnPrds);
				LOGGER.debug(msg);
			}
			return prVsGstr2bRecords.stream().map(o -> convertToB2BDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getprVsGstr2bRecords for fy %s"
							+ ", listOfRecepGstin %s and listOfReturnPrds :%s",
					fy, listOfRecepGstin, listOfReturnPrds);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<Gstr2aVsPurchaseRegisterDto> getPurchaseRegisterVsGstr2b(
			String fy, List<String> listOfRecepGstin,
			List<String> listOfReturnPrds) {
		try {
			List<Object[]> prVsGstr2bRecords = dashboardDao
					.getPurchaseRegisterVsGstr2b(fy, listOfRecepGstin,
							listOfReturnPrds);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getPurchaseRegisterVsGstr2b for fy %s"
								+ ", listOfRecepGstin %s and reutrnPeriods :%s of :%s",
						prVsGstr2bRecords, fy, listOfRecepGstin,
						listOfReturnPrds);
				LOGGER.debug(msg);
			}
			return prVsGstr2bRecords.stream().map(o -> convertToB2BDto1(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getPurchaseRegisterVsGstr2b for fy %s"
							+ ", listOfRecepGstin %s and listOfReturnPrds :%s",
					fy, listOfRecepGstin, listOfReturnPrds);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private Gstr2aVsPurchaseRegisterDto convertToB2BDto1(Object[] obj) {
		Gstr2aVsPurchaseRegisterDto dto = new Gstr2aVsPurchaseRegisterDto();
		dto.setReportType((String) obj[0]);
		dto.setPrTotalTax(obj[1] != null ? ((BigDecimal) obj[1]) : null);
		dto.setGstr2TotalTax(obj[2] != null ? ((BigDecimal) obj[2]) : null);
		return dto;
	}

	@Override
	public List<PrSummary2a2bDto> get2aVs2bVsPrSummary(String fy,
			List<String> recepientGstins, List<String> returnPeriods) {
		List<Object[]> get2aVs2bVsPrSummary = dashboardDao
				.get2aVs2bVsPrSummary(fy, recepientGstins, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s get2aVs2bVsPrSummary for fy %s"
							+ ", recepientGstins %s , returnPeriods %s ",
					get2aVs2bVsPrSummary.size(), fy, recepientGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return get2aVs2bVsPrSummary.stream().map(o -> convertToChartDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private PrSummary2a2bDto convertToChartDto(Object[] obj) {
		PrSummary2a2bDto dto = new PrSummary2a2bDto();
		dto.setTransactionType((String) obj[0]);
		dto.setPrTotalTax((BigDecimal) obj[1]);
		dto.setA2TotalTax((BigDecimal) obj[2]);
		dto.setBrTotalTax((BigDecimal) obj[3]);
		dto.setPrTaxableValue((BigDecimal) obj[4]);
		dto.setA2TaxableValue((BigDecimal) obj[5]);
		dto.setB2TaxableValue((BigDecimal) obj[6]);
		return dto;
	}

	@Override
	public List<Pr2a2bDataDto> getPr2a2bData(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods) {
		List<Object[]> taxInwardObjects = dashboardDao.getPr2a2bData(fy,
				listOfRecepGstin, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s taxLiabilityObjects for fy %s"
							+ ", listOfRecepGstin %s , returnPeriods %s ",
					taxInwardObjects.size(), fy, listOfRecepGstin,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return taxInwardObjects.stream().map(o -> convertToTaxLiabilityDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Pr2a2bDataDto convertToTaxLiabilityDto(Object[] obj) {
		Pr2a2bDataDto dto = new Pr2a2bDataDto();
		dto.setTransactionType((String) obj[0]);
		dto.setPrTaxableValue((BigDecimal) obj[1]);
		dto.setPrTotalValue((BigDecimal) obj[2]);
		dto.setPrInvoiceValue((BigDecimal) obj[3]);
		dto.setGstr2aTaxableValue((BigDecimal) obj[4]);
		dto.setGstr2a((BigDecimal) obj[5]);
		dto.setGstr2aInvoiceValue((BigDecimal) obj[6]);
		dto.setGstr2bTaxableValue((BigDecimal) obj[7]);
		dto.setGstr2b((BigDecimal) obj[8]);
		dto.setGstr2bInvoiceValue((BigDecimal) obj[9]);
		return dto;
	}

	@Override
	public List<LastRefreshedOnDto> getLastRefereshedOn(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods) {
		List<Object[]> lastRefreshedObjects = dashboardDao
				.getLastRefereshedOn(fy, listOfRecepGstin, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s LastRefreshed for fy %s"
							+ ", listOfRecepGstin %s , returnPeriods %s ",
					lastRefreshedObjects.size(), fy, listOfRecepGstin,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return lastRefreshedObjects.stream()
				.map(o -> convertToLastRefreshedDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private LastRefreshedOnDto convertToLastRefreshedDto(Object[] obj) {
		LastRefreshedOnDto dto = new LastRefreshedOnDto();
		dto.setLastRefreshedOn2A((LocalDateTime) obj[0]);
		dto.setLastRefreshedOn2B((LocalDateTime) obj[1]);
		return dto;
	}

	@Override
	public List<Gstr2aVsPurchaseRegisterDto> getPurchaseRegisterVsGstr2a(
			String fy, List<String> listOfRecepGstin,
			List<String> listOfReturnPrds) {
		try {
			List<Object[]> prVsGstr2aRecords = dashboardDao
					.getPurchaseRegisterVsGstr2a(fy, listOfRecepGstin,
							listOfReturnPrds);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s getPurchaseRegisterVsGstr2a for fy %s"
								+ ", listOfRecepGstin %s and reutrnPeriods :%s of :%s",
						prVsGstr2aRecords, fy, listOfRecepGstin,
						listOfReturnPrds);
				LOGGER.debug(msg);
			}
			return prVsGstr2aRecords.stream().map(o -> convertToB2BDto1(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getPurchaseRegisterVsGstr2b for fy %s"
							+ ", listOfRecepGstin %s and listOfReturnPrds :%s",
					fy, listOfRecepGstin, listOfReturnPrds);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<B2bCntDto> get2aVs2bVsPrSummarySuppliers(String fy,
			String valueFlag, List<String> recepientGstins,
			List<String> returnPeriods) {
		List<Object[]> get2aVs2bVsPrSummarySuppliers = dashboardDao
				.get2aVs2bVsPrSummarySuppliers(fy, recepientGstins,
						returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s get2aVs2bVsPrSummary for fy %s"
							+ ", recepientGstins %s , returnPeriods %s ",
					get2aVs2bVsPrSummarySuppliers.size(), fy, recepientGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return get2aVs2bVsPrSummarySuppliers.stream()
				.map(o -> convertToSuppliersDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private B2bCntDto convertToSuppliersDto(Object[] obj) {
		B2bCntDto dto = new B2bCntDto();
		dto.setXAxis((String) obj[0]);
		dto.setYAxis(obj[1] != null ? (GenUtil.getBigInteger(obj[1])).longValue() : 0);
		return dto;
	}

	@Override
	public List<Gstr1FioriDashboardChartDto> get2aVs2bVsPrSummaryTaxable(
			String fy, String valueFlag, List<String> recepientGstins,
			List<String> returnPeriods) {
		List<Object[]> get2aVs2bVsPrSummaryTaxable = dashboardDao
				.get2aVs2bVsPrSummaryTaxable(fy, recepientGstins,
						returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s get2aVs2bVsPrSummary for fy %s"
							+ ", recepientGstins %s , returnPeriods %s ",
					get2aVs2bVsPrSummaryTaxable.size(), fy, recepientGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return get2aVs2bVsPrSummaryTaxable.stream()
				.map(o -> convertToTaxableDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr1FioriDashboardChartDto convertToTaxableDto(Object[] obj) {
		Gstr1FioriDashboardChartDto dto = new Gstr1FioriDashboardChartDto();
		dto.setXAxis((String) obj[0]);
		dto.setYAxis((BigDecimal) obj[1]);
		return dto;
	}

	@Override
	public List<Gstr1FioriDashboardChartDto> get2aVs2bVsPrSummaryTotalTax(
			String fy, String valueFlag, List<String> recepientGstins,
			List<String> returnPeriods) {
		List<Object[]> get2aVs2bVsPrSummaryTotalTax = dashboardDao
				.get2aVs2bVsPrSummaryTotalTax(fy, recepientGstins,
						returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s get2aVs2bVsPrSummary for fy %s"
							+ ", recepientGstins %s , returnPeriods %s ",
					get2aVs2bVsPrSummaryTotalTax.size(), fy, recepientGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return get2aVs2bVsPrSummaryTotalTax.stream()
				.map(o -> convertToTaxableDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<LastUpdatedOnDto> getReconLastUpdatedOn(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods) {
		List<String> lastUpdatedOnObjects = dashboardDao
				.getReconLastUpdatedOn(fy, listOfRecepGstin, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s getLastUpdatedOn for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					lastUpdatedOnObjects.size(), fy, listOfRecepGstin,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return lastUpdatedOnObjects.stream().map(o -> convertToLastUpdateDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private LastUpdatedOnDto convertToLastUpdateDto(String lastUpdatedOn) {
		LastUpdatedOnDto dto = new LastUpdatedOnDto();
		if (lastUpdatedOn != null) {
			String finalDate = lastUpdatedOn.substring(0, 19);
			dto.setLastUpdatedOn(finalDate);
		}
		return dto;
	}

}
