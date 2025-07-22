package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr3BMonthlyTrendTaxAmountRepository;
import com.ey.advisory.app.gstr3b.Gstr3bMonthlyTrendTaxAmountsEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Service("Gstr3BOutwardFioriDashboardServiceImpl")
@Slf4j
public class Gstr3BOutwardFioriDashboardServiceImpl
		implements Gstr3BOutwardFioriDashboardService {

	@Autowired
	@Qualifier("Gstr1Inward2FioriDashboardDaoImpl")
	private Gstr1Inward2FioriDashboardDao dashboardDao;

	@Autowired
	private Gstr3BMonthlyTrendTaxAmountRepository gstr3BRepo;

	@Override
	public Gstr3bHeaderGraphDetailsDto getHeaderGraphData(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds) {
		try {
			@SuppressWarnings("unused")
			Gstr3bHeaderGraphDetailsDto respDto = new Gstr3bHeaderGraphDetailsDto();

			List<Gstr3bMonthlyTrendTaxAmountsEntity> gstr3BList = gstr3BRepo
					.getGstinAndTaxPeriod(listOfRecepGstin, listOfReturnPrds);

			if (gstr3BList == null || gstr3BList.isEmpty())
				return null;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" gstr3BList -> " + gstr3BList.size());
			}

			Map<String, List<Gstr3bMonthlyTrendTaxAmountsEntity>> gstr3BTableMap = gstr3BList
					.stream()
					.collect(Collectors.groupingBy(e -> e.getTaxPeriod()));

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");

			List<Gstr3BLiabilityMonthDto> totalLiabList = new ArrayList<>();
			List<Gstr3BLiabilityMonthDto> liabItcList = new ArrayList<>();
			List<Gstr3BLiabilityMonthDto> liabCashList = new ArrayList<>();
			BigDecimal totalLiab = BigDecimal.ZERO;
			BigDecimal totlLiabItc = BigDecimal.ZERO;
			BigDecimal totlLiabCash = BigDecimal.ZERO;
			String refreshedOn = null;
			LocalDateTime latstCreateOn = gstr3BList.get(0).getCreateDate();
			/*LocalDateTime latstCreateOn = gstr3BRepo
					.getLatestCreatedOnGstinAndTaxPeriod(listOfRecepGstin,
							listOfReturnPrds);
							*/

			for (String month : listOfReturnPrds) {
				
				if (month.equalsIgnoreCase("All"))
					continue;
				else {

					if (gstr3BTableMap.containsKey(month)) {
						List<Gstr3bMonthlyTrendTaxAmountsEntity> entityList = gstr3BTableMap
								.get(month);
						Optional<Gstr3bMonthlyTrendTaxAmountsEntity> entities = entityList
								.stream().reduce((a, b) -> addDto(a, b));

						if (entities.isPresent()) {
							Gstr3bMonthlyTrendTaxAmountsEntity entity = entities
									.get();

							totalLiab = totalLiab.add(entity.getTaxPayTotal());
							totalLiabList.add(new Gstr3BLiabilityMonthDto(month,
									entity.getTaxPayTotal()));

							// ITC liability
							totlLiabItc = totlLiabItc.add(entity.getItcTotal());
							liabItcList.add(new Gstr3BLiabilityMonthDto(month,
									entity.getItcTotal()));

							// Cash Total Liability

							totlLiabCash = totlLiabCash
									.add(entity.getCashTotal());
							liabCashList.add(new Gstr3BLiabilityMonthDto(month,
									entity.getCashTotal()));
							
							if(latstCreateOn.compareTo(entity.getCreateDate()) >0)
							{
								continue;
							}else
							{
								latstCreateOn = entity.getCreateDate();
							}
							
						}
					}

					else {
						totalLiabList.add(new Gstr3BLiabilityMonthDto(month,
								BigDecimal.ZERO));
						liabItcList.add(new Gstr3BLiabilityMonthDto(month,
								BigDecimal.ZERO));
						liabCashList.add(new Gstr3BLiabilityMonthDto(month,
								BigDecimal.ZERO));
					}
				}
			}

			/*
			 * for (Gstr3bMonthlyTrendTaxAmountsEntity entity : gstr3BList) {
			 * LocalDateTime refOn = entity.getCreateDate();
			 * 
			 * // totalLiability
			 * 
			 * totalLiab = totalLiab.add(entity.getTaxPayTotal());
			 * totalLiabList.add(new Gstr3BLiabilityMonthDto(
			 * entity.getTaxPeriod(), entity.getTaxPayTotal()));
			 * 
			 * // ITC liability totlLiabItc =
			 * totlLiabItc.add(entity.getItcTotal()); liabItcList.add(new
			 * Gstr3BLiabilityMonthDto( entity.getTaxPeriod(),
			 * entity.getItcTotal()));
			 * 
			 * // Cash Total Liability
			 * 
			 * totlLiabCash = totlLiabCash.add(entity.getCashTotal());
			 * liabCashList.add(new Gstr3BLiabilityMonthDto(
			 * entity.getTaxPeriod(), entity.getCashTotal()));
			 * 
			 * listOfReturnPrds.remove(entity.getTaxPeriod());
			 * 
			 * if (entity.getCreateDate().compareTo(refOn) == 1) refOn =
			 * entity.getCreateDate();
			 * 
			 * refreshedOn = formatter
			 * .format(EYDateUtil.toISTDateTimeFromUTC(refOn)) .toString(); }
			 * 
			 * // remaining taxPeriods for (String taxPrd : listOfReturnPrds) {
			 * if (taxPrd.equalsIgnoreCase("All")) continue; else {
			 * 
			 * totalLiabList.add(new Gstr3BLiabilityMonthDto(taxPrd,
			 * BigDecimal.ZERO)); liabItcList.add(new
			 * Gstr3BLiabilityMonthDto(taxPrd, BigDecimal.ZERO));
			 * liabCashList.add(new Gstr3BLiabilityMonthDto(taxPrd,
			 * BigDecimal.ZERO)); } }
			 */
			// sort based on return period
		/*	Collections.sort(totalLiabList,
					Comparator.comparing(Gstr3BLiabilityMonthDto::getXAxis));*/
			Collections.sort(totalLiabList, (o1, o2) -> {
			    String xAxis1 = o1.getXAxis();
			    String xAxis2 = o2.getXAxis();

			    // Extract month and year
			    int month1 = Integer.parseInt(xAxis1.substring(0, 2));
			    int year1 = Integer.parseInt(xAxis1.substring(2));

			    int month2 = Integer.parseInt(xAxis2.substring(0, 2));
			    int year2 = Integer.parseInt(xAxis2.substring(2));

			    // Adjust months to start sorting from April
			    int adjustedMonth1 = (month1 + 8) % 12;
			    int adjustedMonth2 = (month2 + 8) % 12;

			    if (year1 != year2) {
			        return Integer.compare(year1, year2);
			    } else {
			        return Integer.compare(adjustedMonth1, adjustedMonth2);
			    }
			});

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" totalLiabList {}-> " + totalLiabList);
			}

		/*	Collections.sort(liabItcList,
					Comparator.comparing(Gstr3BLiabilityMonthDto::getXAxis));*/
			Collections.sort(liabItcList, (o1, o2) -> {
			    String xAxis1 = o1.getXAxis();
			    String xAxis2 = o2.getXAxis();

			    // Extract month and year
			    int month1 = Integer.parseInt(xAxis1.substring(0, 2));
			    int year1 = Integer.parseInt(xAxis1.substring(2));

			    int month2 = Integer.parseInt(xAxis2.substring(0, 2));
			    int year2 = Integer.parseInt(xAxis2.substring(2));

			    // Adjust months to start sorting from April
			    int adjustedMonth1 = (month1 + 8) % 12;
			    int adjustedMonth2 = (month2 + 8) % 12;

			    if (year1 != year2) {
			        return Integer.compare(year1, year2);
			    } else {
			        return Integer.compare(adjustedMonth1, adjustedMonth2);
			    }
			});

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" liabItcList {}-> " + liabItcList);
			}

		/*	Collections.sort(liabCashList,
					Comparator.comparing(Gstr3BLiabilityMonthDto::getXAxis));*/
			
			Collections.sort(liabCashList, (o1, o2) -> {
			    String xAxis1 = o1.getXAxis();
			    String xAxis2 = o2.getXAxis();

			    // Extract month and year
			    int month1 = Integer.parseInt(xAxis1.substring(0, 2));
			    int year1 = Integer.parseInt(xAxis1.substring(2));

			    int month2 = Integer.parseInt(xAxis2.substring(0, 2));
			    int year2 = Integer.parseInt(xAxis2.substring(2));

			    // Adjust months to start sorting from April
			    int adjustedMonth1 = (month1 + 8) % 12;
			    int adjustedMonth2 = (month2 + 8) % 12;

			    if (year1 != year2) {
			        return Integer.compare(year1, year2);
			    } else {
			        return Integer.compare(adjustedMonth1, adjustedMonth2);
			    }
			});
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" liabCashList {}-> " + liabCashList);
			}

			if (latstCreateOn != null) {
				refreshedOn = formatter
						.format(EYDateUtil.toISTDateTimeFromUTC(latstCreateOn))
						.toString();
			}

			respDto.setTotalLiab(totalLiab);
			respDto.setLiabCash(totlLiabCash);
			respDto.setLiabItc(totlLiabItc);
			respDto.setTotalLiabList(totalLiabList);
			respDto.setLiabItcList(liabItcList);
			respDto.setLiabCashList(liabCashList);
			respDto.setRefreshedOn(refreshedOn);
			return respDto;

		} catch (Exception ex) {
			String msg = String.format(
					"Error while getGstr3bHeaderGraphData for fy %s"
							+ ", listOfRecepGstin %s and listOfReturnPrds :%s",
					fy, listOfRecepGstin, listOfReturnPrds);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	public List<Gstr3bMonthlyTrendTaxAmountsEntity> getTableData(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds) {
		try {
			@SuppressWarnings("unused")
			List<Gstr3bMonthlyTrendTaxAmountsEntity> gstr3BList = new ArrayList<>();

			List<Gstr3bMonthlyTrendTaxAmountsEntity> gstr3BList1 = gstr3BRepo
					.getGstinAndTaxPeriod(listOfRecepGstin, listOfReturnPrds);

			Map<String, List<Gstr3bMonthlyTrendTaxAmountsEntity>> mapGstr3b = gstr3BList1
					.stream()
					.collect(Collectors.groupingBy(e -> e.getSuppGstin()));

			for (Map.Entry<String, List<Gstr3bMonthlyTrendTaxAmountsEntity>> entry : mapGstr3b
					.entrySet()) {
				List<Gstr3bMonthlyTrendTaxAmountsEntity> value = entry
						.getValue();

				Optional<Gstr3bMonthlyTrendTaxAmountsEntity> entities = value
						.stream().reduce((a, b) -> addDto(a, b));

				if (entities.isPresent()) {
					gstr3BList.add(entities.get());
				}
			}

			return gstr3BList;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while getGstr3bHeaderGraphData for fy %s"
							+ ", listOfRecepGstin %s and listOfReturnPrds :%s",
					fy, listOfRecepGstin, listOfReturnPrds);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private Gstr3bMonthlyTrendTaxAmountsEntity addDto(
			Gstr3bMonthlyTrendTaxAmountsEntity a,
			Gstr3bMonthlyTrendTaxAmountsEntity b) {

		b.setCashCgst(b.getCashCgst().add(a.getCashCgst()));
		b.setCashIgst(b.getCashIgst().add(a.getCashIgst()));
		b.setCashSgst(b.getCashSgst().add(a.getCashSgst()));
		b.setCashCess(b.getCashCess().add(a.getCashCess()));
		b.setCashTotal(b.getCashTotal().add(a.getCashTotal()));

		b.setItcCgst(b.getItcCgst().add(a.getItcCgst()));
		b.setItcSgst(b.getItcSgst().add(a.getItcSgst()));
		b.setItcIgst(b.getItcIgst().add(a.getItcIgst()));
		b.setItcCess(b.getItcCess().add(a.getItcCess()));
		b.setItcTotal(b.getItcTotal().add(a.getItcTotal()));

		b.setTaxPayCgst(b.getTaxPayCgst().add(a.getTaxPayCgst()));
		b.setTaxPayIgst(b.getTaxPayIgst().add(a.getTaxPayIgst()));
		b.setTaxPaySgst(b.getTaxPaySgst().add(a.getTaxPaySgst()));
		b.setTaxPayCess(b.getTaxPayCess().add(a.getTaxPayCess()));
		b.setTaxPayTotal(b.getTaxPayTotal().add(a.getTaxPayTotal()));
		if(b.getCreateDate().compareTo(a.getCreateDate()) <0 )
		{
			b.setCreateDate(a.getCreateDate());
		}else
		{
			b.setCreateDate(b.getCreateDate());
		}
		return b;
	}
	
	public static void main(String[] args) {
		Timestamp ts1 = Timestamp.valueOf("2018-09-18 09:01:15");
		Timestamp ts2 = Timestamp.valueOf("2018-09-25 09:01:16");
		
		 int value = ts1.compareTo(ts2);
		 System.out.println("Hi"+ value);
		 }

}
