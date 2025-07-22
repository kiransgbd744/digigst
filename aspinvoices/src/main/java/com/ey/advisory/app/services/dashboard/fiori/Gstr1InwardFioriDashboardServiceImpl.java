package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Service("Gstr1InwardFioriDashboardServiceImpl")
@Slf4j
public class Gstr1InwardFioriDashboardServiceImpl
		implements Gstr1InwardFioriDashboardService {

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	private Gstr1InwardFioriDashboardDao dashboardDao;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;
	
	@Autowired
	DefaultStateCache defaultStateCache;

	private static final String B2B_STOCK_TRANSFER = "B2B(Stock transfer)";
	private static final String B2B_OTHER_THAN_STOCK_TRANSFER = "B2B(Other than stock transfer)";
	private static final String CDN_STOCK_TRANSFER = "CDN(Stock transfer)";
	private static final String CDN_OTHER_THAN_STOCK_TRANSFER = "CDN(Other than stock transfer)";

	@Override
	public List<GstinDto> getAllRecepientGstins(List<Long> entityIds) {
		List<String> RecepientPanList = entityInfoRepository
				.findPanByEntityIds(entityIds);

		/*String recepientPan = RecepientPanList.get(0);

		List<String> list = dashboardDao.getRecGstinList(recepientPan);*/

		List<String> gstinsList = new ArrayList<>();

		for (String recepientPan : RecepientPanList) {
		    List<String> list = dashboardDao.getRecGstinList(recepientPan);
		    gstinsList.addAll(list);
		}

		// Now reqGstins should contain the combined list of GSTINs for all recipient PANs


		List<String> reqGstins = gSTNDetailRepository
				.findgstinByEntityIdWithRegTypeForGstr1(entityIds);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Fetched %s supplierGstins for pan %s",
					gstinsList.size(), RecepientPanList);
			LOGGER.debug(msg);
		}
		return gstinsList.stream().filter(c -> reqGstins.contains(c))
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
	public List<Gstr1FioriDashboardChartDto> getGrossInwardSuppplies(String fy,
			List<String> recepientGstins, List<String> returnPeriods,
			String flag) {
		List<Object[]> grossInwardObjects = dashboardDao
				.getAllGrossInwardSupp(fy, recepientGstins, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s grossInwardObjects for fy %s"
							+ ", recepientGstins %s , returnPeriods %s ",
					grossInwardObjects.size(), fy, recepientGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		List<Gstr1FioriDashboardChartDto> retlist = grossInwardObjects.stream()
				.map(o -> convertToChartDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
		List<Gstr1FioriDashboardChartDto> resp = new ArrayList<>();

		for (Gstr1FioriDashboardChartDto dto : retlist) {
			if ("off".equalsIgnoreCase(flag)) {
				if (!B2B_STOCK_TRANSFER.equalsIgnoreCase(dto.getXAxis())
						&& !B2B_OTHER_THAN_STOCK_TRANSFER
								.equalsIgnoreCase(dto.getXAxis())
						&& !CDN_STOCK_TRANSFER.equalsIgnoreCase(dto.getXAxis())
						&& !CDN_OTHER_THAN_STOCK_TRANSFER
								.equalsIgnoreCase(dto.getXAxis())) {
					resp.add(dto);
				}
			} else {
				if (!"B2B".equalsIgnoreCase(dto.getXAxis())
						&& !"CDN".equalsIgnoreCase(dto.getXAxis())) {
					resp.add(dto);
				}
			}
		}
		return resp;
	}

	private Gstr1FioriDashboardInwardChartDto convertToChartDto1(Object[] obj) {
		Gstr1FioriDashboardInwardChartDto dto = new Gstr1FioriDashboardInwardChartDto();
		dto.setXAxis((String) obj[0]);
		dto.setYAxis((BigDecimal) obj[1]);
		return dto;
	}

	private Gstr1FioriDashboardChartDto convertToChartDto(Object[] obj) {
		Gstr1FioriDashboardChartDto dto = new Gstr1FioriDashboardChartDto();
		dto.setXAxis((String) obj[0]);
		dto.setYAxis((BigDecimal) obj[1]);
		return dto;
	}

	@Override
	public List<Gstr1FioriDashboardChartDto> getMonthWiseTrendAnalysisList(
			String fy, String valueFlag, List<String> listOfRecepGstin,
			List<String> listOfReturnPrds) {
		List<Object[]> monthWiseTrendObjects = dashboardDao
				.getMonthWiseTrendAnalysis(fy, valueFlag, listOfRecepGstin,
						listOfReturnPrds);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s monthWiseTrendObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					monthWiseTrendObjects.size(), fy, listOfRecepGstin,
					listOfReturnPrds);
			LOGGER.debug(msg);
		}
		return monthWiseTrendObjects.stream().map(o -> convertToChartDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<Gstr1FioriDashboardChartDto> getTopCustomersB2BList(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods) {
		List<Object[]> topCustomersB2bObjects = dashboardDao
				.getTopCustomerB2BData(fy, listOfRecepGstin, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s topCustomersB2bObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					topCustomersB2bObjects.size(), fy, listOfRecepGstin,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return topCustomersB2bObjects.stream().map(o -> convertToChartDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<Gstr1FioriDashboardInwardChartDto> getMajorGoodsProcurred(
			String fy, List<String> listOfRecepGstin,
			List<String> returnPeriods) {
		List<Object[]> majorTaxPayingProductObjects = dashboardDao
				.getMajorGoodsProcurred(fy, listOfRecepGstin, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s majorTaxPayingProductObjects for fy %s"
							+ ", supplierGstins %s ",
					majorTaxPayingProductObjects.size(), fy, listOfRecepGstin);
			LOGGER.debug(msg);
		}
		return majorTaxPayingProductObjects.stream()
				.map(o -> convertToChartDto1(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<TaxRateWiseDistributionDto> getTaxRateWiseDistributionList(
			String fy, List<String> listOfRecepGstin,
			List<String> returnPeriods) {
		List<Object[]> taxRateWiseDistributionObjects = dashboardDao
				.getTaxRateWiseDistribution(fy, listOfRecepGstin,
						returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s taxRateWiseDistributionObjects for fy %s"
							+ ", listOfRecepGstin %s",
					taxRateWiseDistributionObjects.size(), fy,
					listOfRecepGstin);
			LOGGER.debug(msg);
		}
		return taxRateWiseDistributionObjects.stream()
				.map(o -> convertToTaxRateDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private TaxRateWiseDistributionDto convertToTaxRateDto(Object[] obj) {
		TaxRateWiseDistributionDto dto = new TaxRateWiseDistributionDto();
		dto.setTaxRate((BigDecimal) obj[0]);
		dto.setInvoiceValue((BigDecimal) obj[1]);
		return dto;
	}

	@Override
	public List<TaxInwardDetailsDto> getTaxInwardData(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods) {
		List<Object[]> taxInwardObjects = dashboardDao.getTaxInwardData(fy,
				listOfRecepGstin, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s taxLiabilityObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					taxInwardObjects.size(), fy, listOfRecepGstin,
					returnPeriods);
			LOGGER.debug(msg);
		}
		List<TaxInwardDetailsDto> retList = taxInwardObjects.stream()
				.map(o -> convertToTaxLiabilityDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;
	}

	private TaxInwardDetailsDto convertToTaxLiabilityDto(Object[] obj) {
		TaxInwardDetailsDto dto = new TaxInwardDetailsDto();
		String transactiontype = (String) obj[0];
		if (transactiontype.equalsIgnoreCase(B2B_STOCK_TRANSFER)) {
			dto.setLevel("L2");
		} else if (transactiontype
				.equalsIgnoreCase(B2B_OTHER_THAN_STOCK_TRANSFER)) {
			dto.setLevel("L2");
		} else if (transactiontype.equalsIgnoreCase(CDN_STOCK_TRANSFER)) {
			dto.setLevel("L2");
		} else if (transactiontype
				.equalsIgnoreCase(CDN_OTHER_THAN_STOCK_TRANSFER)) {
			dto.setLevel("L2");
		}

		dto.setTransactionType(transactiontype);

		// dto.setTransactionType((String) obj[0]);
		
		if(obj[1] != null){
			dto.setTaxableValue((BigDecimal) obj[1]);
		} else{
			dto.setTaxableValue(BigDecimal.ZERO);
		}
		if(obj[2] != null){
			dto.setIgst((BigDecimal) obj[2]);
		} else{
			dto.setIgst(BigDecimal.ZERO);
		}
		if(obj[3] != null){
			dto.setCgst((BigDecimal) obj[3]);
		}else{
			dto.setCgst(BigDecimal.ZERO);
		}
		if(obj[4] != null){
			dto.setSgst((BigDecimal) obj[4]);
		}else{
			dto.setSgst(BigDecimal.ZERO);
		}
		if(obj[5] != null){
			dto.setCess((BigDecimal) obj[5]);
		} else{
			dto.setCess(BigDecimal.ZERO);
		}
		if(obj[6] != null){
			dto.setInvoiceValue((BigDecimal) obj[6]);
		} else{
			dto.setInvoiceValue(BigDecimal.ZERO);
		}
		if(obj[7] != null){
			dto.setIgstCredit((BigDecimal) obj[7]);
		}else{
			dto.setIgstCredit(BigDecimal.ZERO);
		}
		if(obj[8] != null){
			dto.setCgstCredit((BigDecimal) obj[8]);
		}else{
			dto.setCgstCredit(BigDecimal.ZERO);
		}
		if(obj[9] != null){
			dto.setSgstCredit((BigDecimal) obj[9]);
		}else{
			dto.setSgstCredit(BigDecimal.ZERO);
		}
		if(obj[10] != null){
			dto.setCessCredit((BigDecimal) obj[10]);
		}else{
			dto.setCessCredit(BigDecimal.ZERO);
		}
		
		return dto;
	}

	@Override
	public TotalItcDetailsDto getTotalItcData(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds) {
		TotalItcDetailsDto responseDto = dashboardDao.getTotalItcDetails(fy,
				listOfRecepGstin, listOfReturnPrds);

		return responseDto;
	}

	@Override
	public List<LastUpdatedOnDto> getLastUpdatedOn(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods) {
		List<String> lastUpdatedOnObjects = dashboardDao.getLastUpdatedOn(fy,
				listOfRecepGstin, returnPeriods);
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
