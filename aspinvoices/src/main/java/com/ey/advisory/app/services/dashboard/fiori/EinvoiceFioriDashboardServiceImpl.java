package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import com.ey.advisory.gstr2.userdetails.GstinDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */
@Service("EinvoiceFioriDashboardServiceImpl")
@Slf4j
public class EinvoiceFioriDashboardServiceImpl
		implements EinvoiceFioriDashboardService {

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("EinvoiceFioriDashboardDaoImpl")
	private EinvoiceFioriDashboardDao dashboardDao;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;
	
	@Autowired
	DefaultStateCache defaultStateCache;

	@Override
	public List<GstinDto> getAllSupplierGstins(List<Long> entityIds) {

		List<String> gstins = gSTNDetailRepository
				.findgstinByEntityIds(entityIds);

		List<String> list = dashboardDao.getSupGstinList(gstins);

		if (list.isEmpty())
			throw new AppException("No Data Found");
		
		return list.stream().map(o -> convertToGstins(o))
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
	public EinvHeaderDetailsDto getEinvHeaderDetails(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate) {

		List<Object[]> headerObj = dashboardDao
				.getHeaderDataDetails(supplierGstins, fromSummDate, toSummDate);
		if (headerObj.isEmpty())
			throw new AppException("No Data Found");
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s EinvHeaderDetails for supplierGstins %s"
							+ " and fromSummDate :%s , toSummDate :%s",
					headerObj, supplierGstins, fromSummDate, toSummDate);
			LOGGER.debug(msg);
		}
		return convertToEinvHeaders(headerObj.get(0));

	}

	private EinvHeaderDetailsDto convertToEinvHeaders(Object[] obj) {
		try {
			EinvHeaderDetailsDto dto = new EinvHeaderDetailsDto();
			dto.setTotalIrns(
					obj[0] != null ? GenUtil.getBigInteger(obj[0]) : BigInteger.ZERO);
			dto.setGenerated(
					obj[1] != null ? GenUtil.getBigInteger(obj[1]) : BigInteger.ZERO);
			dto.setCancelled(
					obj[2] != null ? GenUtil.getBigInteger(obj[2]) : BigInteger.ZERO);
			dto.setDuplicate(
					obj[3] != null ? GenUtil.getBigInteger(obj[3]) : BigInteger.ZERO);
			dto.setError(
					obj[4] != null ? GenUtil.getBigInteger(obj[4]) : BigInteger.ZERO);
			return dto;
		} catch (Exception ex) {
			String msg = String
					.format("Error while converting EinvHeaderDetails to dto");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public EinvIrnDto getAvgIrnGenPerMonth(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate) {
		try {
			List<Object[]> avgIrn = dashboardDao.getAvgIrnGen(supplierGstins,
					fromSummDate, toSummDate);
			if (avgIrn.isEmpty())
				throw new AppException("No Data Found");
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched %s AvgIrnGenPerMonth for supplierGstins %s"
								+ " and fromSummDate :%s , toSummDate :%s",
						avgIrn, supplierGstins, fromSummDate, toSummDate);
				LOGGER.debug(msg);
			}
			return convertToIrnDto(avgIrn.get(0));
		} catch (Exception ex) {
			String msg = String
					.format("Error while converting AvgIrnGenPerMonth to dto");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	private EinvIrnDto convertToIrnDto(Object[] obj) {
		try {
			EinvIrnDto dto = new EinvIrnDto();
			dto.setIrn(GenUtil.defaultToZeroIfNull((BigDecimal) obj[0]));
			String refreshedOn = obj[1] != null
					? ((Timestamp) obj[1]).toString() : "";
			if (refreshedOn != null && !refreshedOn.isEmpty() ) {
				refreshedOn = refreshedOn.replace(" ", "T");
				refreshedOn = refreshedOn.substring(0, 22);
				String itc = EYDateUtil
						.toISTDateTimeFromUTC(LocalDateTime.parse(refreshedOn))
						.toString();
				itc = itc.substring(0, 19);
				itc = itc.replace("T", " ");
				dto.setLastUpdatedOn(itc);
			}
			return dto;
		} catch (Exception ex) {
			String msg = String
					.format("Error while converting EinvHeaderDetails to dto");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public EInvoiceSummaryDto getEinvSummaryData(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate, Long entityId) {

		List<Object[]> einvSumm = dashboardDao.getEinvoiceSumm(supplierGstins,
				fromSummDate, toSummDate, entityId);
		if (einvSumm.isEmpty())
			throw new AppException("No Data Found");
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s EinvSummaryData for supplierGstins %s"
							+ " and fromSummDate :%s , toSummDate :%s",
					einvSumm, supplierGstins, fromSummDate, toSummDate);
			LOGGER.debug(msg);
		}
		String entityName = entityInfoRepository
				.findEntityNameByEntityId(entityId);
		return convertToEinvSummary(einvSumm.get(0), entityName);

	}

	private EInvoiceSummaryDto convertToEinvSummary(Object[] obj,
			String entityName) {
		try {
			EInvoiceSummaryDto dto = new EInvoiceSummaryDto();

			dto.setGenerated(
					obj[0] != null ? GenUtil.getBigInteger(obj[0]) : BigInteger.ZERO);
			dto.setCancelled(
					obj[1] != null ? GenUtil.getBigInteger(obj[1]) : BigInteger.ZERO);
			dto.setDuplicate(
					obj[2] != null ? GenUtil.getBigInteger(obj[2]) : BigInteger.ZERO);
			dto.setError(
					obj[3] != null ? GenUtil.getBigInteger(obj[3]) : BigInteger.ZERO);
			dto.setEntityName(entityName);
			return dto;
		} catch (Exception ex) {
			String msg = String
					.format("Error while converting EinvSummaryData to dto");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<EInvoiceDistributionDto> getEinvDistributionData(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate) {

		List<Object[]> distributionData = dashboardDao
				.getEinvDistribution(supplierGstins, fromSummDate, toSummDate);
		if (distributionData.isEmpty())
			throw new AppException("No Data Found");
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s EinvDistributionData for supplierGstins %s"
							+ " and fromSummDate :%s , toSummDate :%s",
					distributionData, supplierGstins, fromSummDate, toSummDate);
			LOGGER.debug(msg);
		}
		return convertToEinvDistribution(distributionData.get(0));
	}

	private List<EInvoiceDistributionDto> convertToEinvDistribution(
			Object[] obj) {
		try {
			EInvoiceDistributionDto Gdto = new EInvoiceDistributionDto();
			EInvoiceDistributionDto Cdto = new EInvoiceDistributionDto();
			EInvoiceDistributionDto Ddto = new EInvoiceDistributionDto();
			EInvoiceDistributionDto Edto = new EInvoiceDistributionDto();
			Gdto.setCount(
					obj[0] != null ? GenUtil.getBigInteger(obj[0]) : BigInteger.ZERO);
			Gdto.setStatus("GENERATED");
			Cdto.setCount(
					obj[1] != null ? GenUtil.getBigInteger(obj[1]) : BigInteger.ZERO);
			Cdto.setStatus("CANCELLED");
			Ddto.setCount(
					obj[2] != null ? GenUtil.getBigInteger(obj[2]) : BigInteger.ZERO);
			Ddto.setStatus("DUPLICATE");
			Edto.setCount(
					obj[3] != null ? GenUtil.getBigInteger(obj[3]) : BigInteger.ZERO);
			Edto.setStatus("ERROR");

			List<EInvoiceDistributionDto> list = new ArrayList<>();
			list.add(Gdto);
			list.add(Cdto);
			list.add(Ddto);
			list.add(Edto);
			return list;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while converting EinvDistributionData to dto");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<EinvGenerationDto1> getEinvGenTredForGenAndTotal(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate) {

		List<Object[]> einvGenTrend = dashboardDao.getEinvGenTredForGenAndTotal(
				supplierGstins, fromSummDate, toSummDate);
		if (einvGenTrend.isEmpty())
			throw new AppException("No Data Found");
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s EinvGenTredForGenAndTotal for supplierGstins %s"
							+ " and fromSummDate :%s , toSummDate :%s",
					einvGenTrend, supplierGstins, fromSummDate, toSummDate);
			LOGGER.debug(msg);
		}
		return einvGenTrend.stream().map(o -> convertToEinvGenForTotal(o))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private EinvGenerationDto1 convertToEinvGenForTotal(Object[] obj) {
		try {
			EinvGenerationDto1 dto = new EinvGenerationDto1();
			dto.setSummaryDate(((Date) obj[0]).toString());
			dto.setGenerated(
					obj[1] != null ? GenUtil.getBigInteger(obj[1]) : BigInteger.ZERO);
			dto.setTotal(
					obj[2] != null ? GenUtil.getBigInteger(obj[2]) : BigInteger.ZERO);

			return dto;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while converting EinvGenTredForGenAndTotal to dto");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<EinvGenerationDto2> getEinvGenTredForCanDupAndErr(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate) {

		List<Object[]> einvGenTrend = dashboardDao
				.getEinvGenTredForCanDupAndErr(supplierGstins, fromSummDate,
						toSummDate);
		if (einvGenTrend.isEmpty())
			throw new AppException("No Data Found");
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s EinvGenTredForCanDupAndErr for supplierGstins %s"
							+ " and fromSummDate :%s , toSummDate :%s",
					einvGenTrend, supplierGstins, fromSummDate, toSummDate);
			LOGGER.debug(msg);
		}
		return einvGenTrend.stream().map(o -> convertToEinvGenForError(o))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private EinvGenerationDto2 convertToEinvGenForError(Object[] obj) {
		try {
			EinvGenerationDto2 dto = new EinvGenerationDto2();
			dto.setSummaryDate(((Date) obj[0]).toString());
			dto.setCancelled(
					obj[1] != null ? GenUtil.getBigInteger(obj[1]) : BigInteger.ZERO);
			dto.setDuplicate(
					obj[2] != null ? GenUtil.getBigInteger(obj[2]) : BigInteger.ZERO);
			dto.setError(
					obj[3] != null ? GenUtil.getBigInteger(obj[3]) : BigInteger.ZERO);

			return dto;
		} catch (Exception ex) {
			String msg = String.format(
					"Error while converting EinvGenTredForCanDupAndErr to dto");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<EinvoiceStatusDto> getEinvStatusTable(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate) {

		List<Object[]> einvStatus = dashboardDao
				.getEinvStatusTable(supplierGstins, fromSummDate, toSummDate);
		if (einvStatus.isEmpty())
			throw new AppException("No Data Found");
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s EinvStatusTable for supplierGstins %s"
							+ " and fromSummDate :%s , toSummDate :%s",
					einvStatus, supplierGstins, fromSummDate, toSummDate);
			LOGGER.debug(msg);
		}
		return einvStatus.stream().map(o -> convertToEinvStatus(o))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private EinvoiceStatusDto convertToEinvStatus(Object[] obj) {
		try {
			EinvoiceStatusDto dto = new EinvoiceStatusDto();
			String date = ((Date) obj[0]).toString();
			LocalDate ldt = LocalDate.parse(date);
			dto.setSummaryDate(DateTimeFormatter
					.ofPattern("dd-MM-yyyy", Locale.ENGLISH).format(ldt));

			dto.setTotal(
					obj[1] != null ? GenUtil.getBigInteger(obj[1]) : BigInteger.ZERO);
			dto.setGenerated(
					obj[2] != null ? GenUtil.getBigInteger(obj[2]) : BigInteger.ZERO);
			dto.setCancelled(
					obj[3] != null ? GenUtil.getBigInteger(obj[3]) : BigInteger.ZERO);
			dto.setDuplicate(
					obj[4] != null ? GenUtil.getBigInteger(obj[4]) : BigInteger.ZERO);
			dto.setError(
					obj[5] != null ? GenUtil.getBigInteger(obj[5]) : BigInteger.ZERO);

			return dto;
		} catch (Exception ex) {
			String msg = String
					.format("Error while converting EinvStatusTable to dto");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

	@Override
	public List<EinvErrorDetailsDto> getEinvErrorDetails(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate) {

		List<Object[]> errorDetails = dashboardDao
				.getErrorDetails(supplierGstins, fromSummDate, toSummDate);
		if (errorDetails.isEmpty())
			throw new AppException("No Data Found");
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s EinvErrorDetails for supplierGstins %s"
							+ " and fromSummDate :%s , toSummDate :%s",
					errorDetails, supplierGstins, fromSummDate, toSummDate);
			LOGGER.debug(msg);
		}
		return errorDetails.stream().map(o -> convertToErrorDetails(o))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private EinvErrorDetailsDto convertToErrorDetails(Object[] obj) {
		try {
			EinvErrorDetailsDto dto = new EinvErrorDetailsDto();
			dto.setYear((String) obj[0]);
			dto.setMonth((String) obj[1]);
			dto.setErrored(
					obj[2] != null ? GenUtil.getBigInteger(obj[2]) : BigInteger.ZERO);
			return dto;
		} catch (Exception ex) {
			String msg = String
					.format("Error while converting EinvErrorDetails to dto");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

}
