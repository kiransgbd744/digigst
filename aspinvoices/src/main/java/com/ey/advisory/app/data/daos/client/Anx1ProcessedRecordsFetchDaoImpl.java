package com.ey.advisory.app.data.daos.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsFinalRespDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Anx1ProcessedRecordsFetchDaoImpl")
public class Anx1ProcessedRecordsFetchDaoImpl
		implements Anx1ProcessedRecordsFetchDao {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ProcessedRecordsFetchDaoImpl.class);

	private static final SimpleDateFormat uiDateFormat = new SimpleDateFormat(
			"MMyyyy");

	private static final SimpleDateFormat dbDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static final String ANX1 = "ANX1";
	private static final String ANX1A = "ANX1A";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private Anx1ProcessedRecordsCommonUtil anx1ProcessedRecordsCommonUtil;

	public List<Anx1ProcessedRecordsFinalRespDto> loadAnx1ProcessedRecords(
			Anx1ProcessedRecordsReqDto processedRecordsReqDto,
			String functionType) {

		List<Long> entityId = processedRecordsReqDto.getEntityId();
		String taxPeriod = processedRecordsReqDto.getRetunPeriod();
		String gstinUploadDate = processedRecordsReqDto.getGstnUploadDate();
		Map<String, List<String>> outDataSecAttrs = processedRecordsReqDto
				.getOutwardDataSecAttrs();
		Map<String, List<String>> inDataSecAttrs = processedRecordsReqDto
				.getInwardDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx1ProcessedRecordsFetchDaoImpl->"
					+ "loadAnx1ProcessedRecords "
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, "
					+ "taxPeriod-> {}, gstinUploadDate -> {}, "
					+ "dataSecAttrs -> {}", processedRecordsReqDto);
		}
		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String purchase = null, distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null, sgstin = null;
		String cgstin = null;

		List<String> outGstinList = null;
		List<String> outPcList = null;
		List<String> outPlantList = null;
		List<String> outDivisionList = null;
		List<String> outLocationList = null;
		List<String> outSalesList = null;
		List<String> outDistList = null;
		List<String> outUd1List = null;
		List<String> outUd2List = null;
		List<String> outUd3List = null;
		List<String> outUd4List = null;
		List<String> outUd5List = null;
		List<String> outUd6List = null;

		List<String> inGstinList = null;
		List<String> inPcList = null;
		List<String> inPlantList = null;
		List<String> inDivisionList = null;
		List<String> inLocationList = null;
		List<String> inPurcList = null;
		List<String> inUd1List = null;
		List<String> inUd2List = null;
		List<String> inUd3List = null;
		List<String> inUd4List = null;
		List<String> inUd5List = null;
		List<String> inUd6List = null;

		if (outDataSecAttrs != null && !outDataSecAttrs.isEmpty()
				&& outDataSecAttrs.size() > 0) {
			for (String key : outDataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					outPcList = outDataSecAttrs.get(OnboardingConstant.PC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					outPlantList = outDataSecAttrs
							.get(OnboardingConstant.PLANT);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					outDivisionList = outDataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					outLocationList = outDataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					outSalesList = outDataSecAttrs.get(OnboardingConstant.SO);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					outDistList = outDataSecAttrs.get(OnboardingConstant.DC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					outUd1List = outDataSecAttrs.get(OnboardingConstant.UD1);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					outUd2List = outDataSecAttrs.get(OnboardingConstant.UD2);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					outUd3List = outDataSecAttrs.get(OnboardingConstant.UD3);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					outUd4List = outDataSecAttrs.get(OnboardingConstant.UD4);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					outUd5List = outDataSecAttrs.get(OnboardingConstant.UD5);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					outUd6List = outDataSecAttrs.get(OnboardingConstant.UD6);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					outGstinList = outDataSecAttrs
							.get(OnboardingConstant.GSTIN);
				}
			}
		}

		if (inDataSecAttrs != null && !inDataSecAttrs.isEmpty()
				&& inDataSecAttrs.size() > 0) {
			for (String key : inDataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					inPcList = inDataSecAttrs.get(OnboardingConstant.PC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					inPlantList = inDataSecAttrs.get(OnboardingConstant.PLANT);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					inDivisionList = inDataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					inLocationList = inDataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					inPurcList = inDataSecAttrs.get(OnboardingConstant.PO);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					inUd1List = inDataSecAttrs.get(OnboardingConstant.UD1);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					inUd2List = inDataSecAttrs.get(OnboardingConstant.UD2);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					inUd3List = inDataSecAttrs.get(OnboardingConstant.UD3);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					inUd4List = inDataSecAttrs.get(OnboardingConstant.UD4);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					inUd5List = inDataSecAttrs.get(OnboardingConstant.UD5);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					inUd6List = inDataSecAttrs.get(OnboardingConstant.UD6);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					cgstin = key;
					inGstinList = inDataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}

		int taxPeriod1 = 0;
		if (taxPeriod != null) {
			taxPeriod1 = GenUtil.convertTaxPeriodToInt(taxPeriod);
		}
		String queryStr = createQueryString(entityId, outGstinList, inGstinList,
				taxPeriod1, gstinUploadDate, taxPeriod, outDataSecAttrs,
				inDataSecAttrs, profitCenter, sgstin, cgstin, plant, division,
				location, sales, purchase, distChannel, ud1, ud2, ud3, ud4, ud5,
				ud6, outPcList, outPlantList, outSalesList, outDivisionList,
				outLocationList, outDistList, outUd1List, outUd2List,
				outUd3List, outUd4List, outUd5List, outUd6List, inPcList,
				inPlantList, inDivisionList, inLocationList, inPurcList,
				inUd1List, inUd2List, inUd3List, inUd4List, inUd5List,
				inUd6List, functionType);

		String arr[] = queryStr.split("#@");
		String outQueryStr = arr[0];
		String inQueryStr = arr[1];
		LOGGER.debug("outQueryStr-->" + outQueryStr);
		LOGGER.debug("inQueryStr-->" + inQueryStr);

		List<Anx1ProcessedRecordsRespDto> finalDtoList = new ArrayList<>();
		try {

			Query outQ = entityManager.createNativeQuery(outQueryStr);
			Query inQ = entityManager.createNativeQuery(inQueryStr);

			if (outGstinList != null && outGstinList.size() > 0
					&& !outGstinList.contains("")) {
				outQ.setParameter("sgstins", outGstinList);
			}
			if (inGstinList != null && inGstinList.size() > 0
					&& !inGstinList.contains("")) {
				inQ.setParameter("cgstins", inGstinList);
			}

			if (profitCenter != null && !profitCenter.isEmpty()
					&& !profitCenter.isEmpty() && outPcList != null
					&& outPcList.size() > 0 && inPcList != null
					&& inPcList.size() > 0) {
				outQ.setParameter("outPcList", outPcList);
				inQ.setParameter("inPcList", inPcList);
			}
			if (plant != null && !plant.isEmpty() && !plant.isEmpty()
					&& outPlantList != null && outPlantList.size() > 0
					&& inPlantList != null && inPlantList.size() > 0) {
				outQ.setParameter("outPlantList", outPlantList);
				inQ.setParameter("inPlantList", inPlantList);
			}
			if (sales != null && !sales.isEmpty() && outSalesList != null
					&& outSalesList.size() > 0) {
				outQ.setParameter("outSalesList", outSalesList);
			}
			if (division != null && !division.isEmpty()
					&& outDivisionList != null && outDivisionList.size() > 0
					&& inDivisionList != null && inDivisionList.size() > 0) {
				outQ.setParameter("outDivisionList", outDivisionList);
				inQ.setParameter("inDivisionList", inDivisionList);
			}
			if (location != null && !location.isEmpty()
					&& outLocationList != null && outLocationList.size() > 0
					&& inLocationList != null && inLocationList.size() > 0) {
				outQ.setParameter("outLocationList", outLocationList);
				inQ.setParameter("inLocationList", inLocationList);
			}
			if (purchase != null && !purchase.isEmpty() && inPurcList != null
					&& inPurcList.size() > 0) {
				inQ.setParameter("inPurcList", inPurcList);
			}
			if (distChannel != null && !distChannel.isEmpty()
					&& outDistList != null && outDistList.size() > 0) {
				outQ.setParameter("outDistList", outDistList);
			}
			if (ud1 != null && !ud1.isEmpty() && outUd1List != null
					&& outUd1List.size() > 0 && inUd1List != null
					&& inUd1List.size() > 0) {
				outQ.setParameter("outUd1List", outUd1List);
				inQ.setParameter("inUd1List", inUd1List);
			}
			if (ud2 != null && !ud2.isEmpty() && outUd2List != null
					&& outUd2List.size() > 0 && inUd2List != null
					&& inUd2List.size() > 0) {
				outQ.setParameter("outUd2List", outUd2List);
				inQ.setParameter("inUd2List", inUd2List);
			}
			if (ud3 != null && !ud3.isEmpty() && outUd3List != null
					&& outUd3List.size() > 0 && inUd3List != null
					&& inUd3List.size() > 0) {
				outQ.setParameter("outUd3List", outUd3List);
				inQ.setParameter("inUd3List", inUd3List);
			}
			if (ud4 != null && !ud4.isEmpty() && outUd4List != null
					&& outUd4List.size() > 0 && inUd4List != null
					&& inUd4List.size() > 0) {
				outQ.setParameter("outUd4List", outUd4List);
				inQ.setParameter("inUd4List", inUd4List);
			}
			if (ud5 != null && !ud5.isEmpty() && outUd5List != null
					&& outUd5List.size() > 0 && inUd5List != null
					&& inUd5List.size() > 0) {
				outQ.setParameter("outUd5List", outUd5List);
				inQ.setParameter("inUd5List", inUd5List);
			}
			if (ud6 != null && !ud6.isEmpty() && outUd6List != null
					&& outUd6List.size() > 0 && inUd6List != null
					&& inUd6List.size() > 0) {
				outQ.setParameter("outUd6List", outUd6List);
				inQ.setParameter("inUd6List", inUd6List);
			}

			if (taxPeriod1 != 0 && taxPeriod != null
					&& functionType.equals(ANX1) && gstinUploadDate != null
					&& !gstinUploadDate.equals("")) {
				switch (gstinUploadDate) {
				case "AGGREGATE":
					try {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(uiDateFormat.parse(taxPeriod));
						if (!entityId.get(0).equals("")) {
							outQ.setParameter("period",
									GenUtil.convertTaxPeriodToInt(taxPeriod));
							inQ.setParameter("period",
									GenUtil.convertTaxPeriodToInt(taxPeriod));
						}

					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e.getMessage());
					}
					break;
				default:
					throw new AppException("Insufficent Search");

				}
			} else if (taxPeriod1 != 0 && taxPeriod != null
					&& functionType.equals(ANX1A)) {
				try {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(uiDateFormat.parse(taxPeriod));
					if (!entityId.get(0).equals("")) {
						outQ.setParameter("period",
								GenUtil.convertTaxPeriodToInt(taxPeriod));
						inQ.setParameter("period",
								GenUtil.convertTaxPeriodToInt(taxPeriod));
					}

				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
			}

			@SuppressWarnings("unchecked")
			List<Object[]> outQlist = outQ.getResultList();
			@SuppressWarnings("unchecked")
			List<Object[]> inQlist = inQ.getResultList();

			LOGGER.debug("Outward data list from database is-->" + outQlist);
			LOGGER.debug("Inward data list from database is-->" + inQlist);

			List<Anx1ProcessedRecordsRespDto> outwardFinalList = anx1ProcessedRecordsCommonUtil
					.convertOutwardDbRecordsIntoObject(outQlist,
							gstinUploadDate, outGstinList, taxPeriod);

			List<Anx1ProcessedRecordsRespDto> inwardFinalList = anx1ProcessedRecordsCommonUtil
					.convertInwardDbRecordsIntoObject(inQlist, gstinUploadDate);

			Map<String, List<Anx1ProcessedRecordsRespDto>> combinedDataMap = new HashMap<String, List<Anx1ProcessedRecordsRespDto>>();
			anx1ProcessedRecordsCommonUtil.createMapByGstinBasedOnType(
					combinedDataMap, outwardFinalList, inwardFinalList);

			List<Anx1ProcessedRecordsRespDto> dataDtoList = new ArrayList<>();
			anx1ProcessedRecordsCommonUtil
					.calculateDataByDocType(combinedDataMap, dataDtoList);

			List<Anx1ProcessedRecordsRespDto> gstinDtoList = new ArrayList<Anx1ProcessedRecordsRespDto>();
			List<String> combinedGstinList = new ArrayList<>();
			if (sgstin != null && !sgstin.isEmpty() && outGstinList != null
					&& outGstinList.size() > 0) {
				combinedGstinList.addAll(outGstinList);
			}

			if (cgstin != null && !cgstin.isEmpty() && inGstinList != null
					&& inGstinList.size() > 0) {
				combinedGstinList.addAll(inGstinList);
			}

			if (!combinedGstinList.isEmpty() && combinedGstinList.size() > 0) {
				for (Anx1ProcessedRecordsRespDto processedDto : dataDtoList) {
					if (combinedGstinList.contains(processedDto.getGstin())) {
						gstinDtoList.add(processedDto);
					}
				}
				List<Anx1ProcessedRecordsRespDto> sortedGstinDtoList = gstinDtoList
						.stream()
						.sorted(Comparator.comparing(
								Anx1ProcessedRecordsRespDto::getGstin))
						.collect(Collectors.toList());
				return Anx1ProcessedRecordsCommonUtil
						.convertCalcuDataToResp(sortedGstinDtoList);
			}

			List<Anx1ProcessedRecordsRespDto> sortedGstinDtoList = dataDtoList
					.stream()
					.sorted(Comparator
							.comparing(Anx1ProcessedRecordsRespDto::getGstin))
					.collect(Collectors.toList());
			finalDtoList.addAll(sortedGstinDtoList);
			LOGGER.debug("Final list from dao is ->" + finalDtoList);
		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}

		return Anx1ProcessedRecordsCommonUtil
				.convertCalcuDataToResp(finalDtoList);
	}

	private String createQueryString(List<Long> entityId,
			List<String> outGstinList, List<String> inGstinList, int taxPeriod,
			String gstinUploadDate, String initialTaxPeriod,
			Map<String, List<String>> outDataSecAttrs,
			Map<String, List<String>> inDataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String purchase, String distChannel,
			String ud1, String ud2, String ud3, String ud4, String ud5,
			String ud6, List<String> outPcList, List<String> outPlantList,
			List<String> outSalesList, List<String> outDivisionList,
			List<String> outLocationList, List<String> outDistList,
			List<String> outUd1List, List<String> outUd2List,
			List<String> outUd3List, List<String> outUd4List,
			List<String> outUd5List, List<String> outUd6List,
			List<String> inPcList, List<String> inPlantList,
			List<String> inDivisionList, List<String> inLocationList,
			List<String> inPurcList, List<String> inUd1List,
			List<String> inUd2List, List<String> inUd3List,
			List<String> inUd4List, List<String> inUd5List,
			List<String> inUd6List, String functionType) {

		if (outDataSecAttrs != null && !outDataSecAttrs.isEmpty()
				&& outDataSecAttrs.size() > 0) {
			for (String key : outDataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
				}
				if (key.equalsIgnoreCase("SO")) {
					sales = key;
				}
				if (key.equalsIgnoreCase("DC")) {
					distChannel = key;
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
				}
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		if (inDataSecAttrs != null && !inDataSecAttrs.isEmpty()
				&& inDataSecAttrs.size() > 0) {
			for (String key : inDataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
				}
				if (key.equalsIgnoreCase("PO")) {
					purchase = key;
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
				}
				if (key.equalsIgnoreCase("GSTIN")) {
					cgstin = key;
				}
			}
		}

		StringBuilder outwardBuilder = new StringBuilder();
		StringBuilder outwardBuilderHdr = new StringBuilder();
		StringBuilder anx1aOutBuilder = new StringBuilder();
		StringBuilder anx1aInBuilder = new StringBuilder();
		StringBuilder anx1aInBuilderHdr = new StringBuilder();
		StringBuilder inwardBuilder = new StringBuilder();
		StringBuilder inwardBuilderHdr = new StringBuilder();
		StringBuilder verticalB2CBuilder = new StringBuilder();
		StringBuilder t3H_3IBuilder = new StringBuilder();
		if (outGstinList != null && outGstinList.size() > 0) {
			outwardBuilder.append(" AND SUPPLIER_GSTIN IN :sgstins");
			outwardBuilderHdr.append(" AND SUPPLIER_GSTIN IN :sgstins");
			verticalB2CBuilder.append(" AND SUPPLIER_GSTIN IN :sgstins");
			anx1aOutBuilder.append(" AND  SUPPLIER_GSTIN IN :sgstins");
		}
		if (inGstinList != null && inGstinList.size() > 0) {
			inwardBuilder.append(" AND CUST_GSTIN IN :cgstins");
			inwardBuilderHdr.append(" AND CUST_GSTIN IN :cgstins");
			anx1aInBuilderHdr.append(" AND CUST_GSTIN IN :cgstins");
			t3H_3IBuilder.append(" AND CUST_GSTIN IN :cgstins");
			anx1aInBuilder.append(" AND  CUST_GSTIN IN :cgstins");
		}
		if (profitCenter != null && !profitCenter.isEmpty() && outPcList != null
				&& outPcList.size() > 0 && inPcList != null
				&& inPcList.size() > 0) {
			outwardBuilder.append(" AND PROFIT_CENTRE IN :outPcList");
			outwardBuilderHdr.append(" AND HDR.PROFIT_CENTRE IN :outPcList");
			inwardBuilder.append(" AND PROFIT_CENTRE IN :inPcList");
			inwardBuilderHdr.append(" AND HDR.PROFIT_CENTRE IN :inPcList");
			anx1aInBuilderHdr.append(" AND HDR.PROFIT_CENTRE IN :inPcList");
			verticalB2CBuilder.append(" AND PROFIT_CENTER IN :outPcList");
			t3H_3IBuilder.append(" AND PROFIT_CENTER IN :inPcList");
			anx1aOutBuilder.append(" AND  PROFIT_CENTRE IN :outPcList");
			anx1aInBuilder.append(" AND  PROFIT_CENTRE IN :inPcList");
		}
		if (plant != null && !plant.isEmpty() && outPlantList != null
				&& outPlantList.size() > 0 && inPlantList != null
				&& inPlantList.size() > 0) {
			outwardBuilder.append(" AND PLANT_CODE IN :outPlantList");
			outwardBuilderHdr.append(" AND HDR.PLANT_CODE IN :outPlantList");
			inwardBuilder.append(" AND PLANT_CODE IN :inPlantList");
			inwardBuilderHdr.append(" AND HDR.PLANT_CODE IN :inPlantList");
			anx1aInBuilderHdr.append(" AND HDR.PLANT_CODE IN :inPlantList");
			verticalB2CBuilder.append(" AND PLANT IN :outPlantList");
			t3H_3IBuilder.append(" AND PLANT IN :inPlantList");
			anx1aOutBuilder.append(" AND  PLANT_CODE  IN :outPlantList");
			anx1aInBuilder.append(" AND  PLANT_CODE IN :inPlantList");
		}
		if (sales != null && !sales.isEmpty() && outSalesList != null
				&& outSalesList.size() > 0) {
			outwardBuilder.append(" AND SALES_ORGANIZATION IN :outSalesList");
			outwardBuilderHdr.append(" AND HDR.SALES_ORG IN :outSalesList");
			verticalB2CBuilder.append(" AND SALES_ORG IN :outSalesList");
			anx1aOutBuilder.append(" AND  SALES_ORGANIZATION IN :outSalesList");
		}
		if (distChannel != null && !distChannel.isEmpty() && outDistList != null
				&& outDistList.size() > 0) {
			outwardBuilder.append(" AND DISTRIBUTION_CHANNEL IN :outDistList");
			outwardBuilderHdr
					.append(" AND HDR.DISTRIBUTION_CHANNEL IN :outDistList");
			verticalB2CBuilder
					.append(" AND DISTRIBUTION_CHANNEL IN :outDistList");
			anx1aOutBuilder
					.append(" AND  DISTRIBUTION_CHANNEL IN :outDistList");
		}
		if (division != null && !division.isEmpty() && outDivisionList != null
				&& outDivisionList.size() > 0 && inDivisionList != null
				&& inDivisionList.size() > 0) {
			outwardBuilder.append(" AND DIVISION IN :outDivisionList");
			outwardBuilderHdr.append(" AND HDR.DIVISION IN :outDivisionList");
			inwardBuilder.append(" AND DIVISION IN :inDivisionList");
			inwardBuilderHdr.append(" AND HDR.DIVISION IN :inDivisionList");
			anx1aOutBuilder.append(" AND  DIVISION IN :outDivisionList");
			anx1aInBuilderHdr.append(" AND HDR.DIVISION IN :inDivisionList");
			anx1aInBuilder.append(" AND  DIVISION IN :inDivisionList");
			verticalB2CBuilder.append(" AND DIVISION IN :outDivisionList");
			t3H_3IBuilder.append(" AND DIVISION IN :inDivisionList");
		}
		if (location != null && !location.isEmpty() && outLocationList != null
				&& outLocationList.size() > 0 && inLocationList != null
				&& inLocationList.size() > 0) {
			outwardBuilder.append(" AND LOCATION IN :outLocationList");
			outwardBuilderHdr.append(" AND HDR.LOCATION IN :outLocationList");
			inwardBuilder.append(" AND LOCATION IN :inLocationList");
			inwardBuilderHdr.append(" AND HDR.LOCATION IN :inLocationList");
			anx1aOutBuilder.append(" AND  LOCATION IN :outLocationList");
			anx1aInBuilderHdr.append(" AND HDR.LOCATION IN :inLocationList");
			anx1aInBuilder.append(" AND  LOCATION IN :inLocationList");
			verticalB2CBuilder.append(" AND LOCATION IN :outLocationList");
			t3H_3IBuilder.append(" AND LOCATION IN :inLocationList");
		}
		if (purchase != null && !purchase.isEmpty() && inPurcList != null
				&& inPurcList.size() > 0) {
			inwardBuilder.append(" AND PURCHASE_ORGANIZATION IN :inPurcList");
			inwardBuilderHdr
					.append(" AND HDR.PURCHASE_ORGANIZATION IN :inPurcList");
			t3H_3IBuilder.append(" AND PURCHAGE_ORG IN :inPurcList");
			anx1aInBuilder.append(" AND  PURCHASE_ORGANIZATION IN :inPurcList");
			anx1aInBuilderHdr
					.append(" AND HDR.PURCHASE_ORGANIZATION IN :inPurcList");
		}
		if (ud1 != null && !ud1.isEmpty() && outUd1List != null
				&& outUd1List.size() > 0 && inUd1List != null
				&& inUd1List.size() > 0) {
			outwardBuilder.append(" AND USERACCESS1 IN :outUd1List");
			outwardBuilderHdr.append(" AND HDR.USERACCESS1 IN :outUd1List");
			inwardBuilder.append(" AND USERACCESS1 IN :inUd1List");
			inwardBuilderHdr.append(" AND HDR.USERACCESS1 IN :inUd1List");
			anx1aOutBuilder.append(" AND  USERACCESS1 IN :outUd1List");
			anx1aInBuilder.append(" AND  USERACCESS1 IN :inUd1List");
			anx1aInBuilderHdr.append(" AND HDR.USERACCESS1 IN :inUd1List");
			verticalB2CBuilder.append(" AND USER_ACCESS1 IN :outUd1List");
			t3H_3IBuilder.append(" AND USER_ACCESS1 IN :inUd1List");
		}
		if (ud2 != null && !ud2.isEmpty() && outUd2List != null
				&& outUd2List.size() > 0 && inUd2List != null
				&& inUd2List.size() > 0) {
			outwardBuilder.append(" AND USERACCESS2 IN :outUd2List");
			outwardBuilderHdr.append(" AND HDR.USERACCESS2 IN :outUd2List");
			inwardBuilder.append(" AND USERACCESS2 IN :inUd2List");
			inwardBuilderHdr.append(" AND HDR.USERACCESS2 IN :inUd2List");
			anx1aInBuilderHdr.append(" AND HDR.USERACCESS2 IN :inUd2List");
			anx1aOutBuilder.append(" AND  USERACCESS2 IN :outUd2List");
			anx1aInBuilder.append(" AND  USERACCESS2 IN :inUd2List");
			verticalB2CBuilder.append(" AND USER_ACCESS2 IN :outUd2List");
			t3H_3IBuilder.append("  AND USER_ACCESS2 IN :inUd2List");
		}
		if (ud3 != null && !ud3.isEmpty() && outUd3List != null
				&& outUd3List.size() > 0 && inUd3List != null
				&& inUd3List.size() > 0) {
			outwardBuilder.append(" AND USERACCESS3 IN :outUd3List");
			outwardBuilderHdr.append(" AND HDR.USERACCESS3 IN :outUd3List");
			inwardBuilder.append(" AND USERACCESS3 IN :inUd3List");
			anx1aInBuilderHdr.append(" AND HDR.USERACCESS3 IN :inUd3List");
			inwardBuilderHdr.append(" AND HDR.USERACCESS3 IN :inUd3List");
			anx1aOutBuilder.append(" AND  USERACCESS3 IN :outUd3List");
			anx1aInBuilder.append(" AND  USERACCESS3 IN :inUd3List");
			verticalB2CBuilder.append(" AND USER_ACCESS3 IN :outUd3List");
			t3H_3IBuilder.append(" AND USER_ACCESS3 IN :inUd3List");
		}
		if (ud4 != null && !ud4.isEmpty() && outUd4List != null
				&& outUd4List.size() > 0 && inUd4List != null
				&& inUd4List.size() > 0) {
			outwardBuilder.append(" AND USERACCESS4 IN :outUd4List");
			outwardBuilderHdr.append(" AND HDR.USERACCESS4 IN :outUd4List");
			inwardBuilder.append(" AND USERACCESS4 IN :inUd4List");
			inwardBuilderHdr.append(" AND HDR.USERACCESS4 IN :inUd4List");
			anx1aInBuilderHdr.append(" AND HDR.USERACCESS4 IN :inUd4List");
			anx1aOutBuilder.append(" AND  USERACCESS4 IN :outUd4List");
			anx1aInBuilder.append(" AND  USERACCESS4 IN :inUd4List");
			verticalB2CBuilder.append(" AND USER_ACCESS4 IN :outUd4List");
			t3H_3IBuilder.append(" AND USER_ACCESS4 IN :inUd4List");
		}
		if (ud5 != null && !ud5.isEmpty() && outUd5List != null
				&& outUd5List.size() > 0 && inUd5List != null
				&& inUd5List.size() > 0) {
			outwardBuilder.append(" AND USERACCESS5 IN :outUd5List");
			outwardBuilderHdr.append(" AND HDR.USERACCESS5 IN :outUd5List");
			inwardBuilder.append(" AND USERACCESS5 IN :inUd5List");
			inwardBuilderHdr.append(" AND HDR.USERACCESS5 IN :inUd5List");
			anx1aInBuilderHdr.append(" AND HDR.USERACCESS5 IN :inUd5List");
			anx1aOutBuilder.append(" AND  USERACCESS5 IN :outUd5List");
			anx1aInBuilder.append(" AND  USERACCESS5 IN :inUd5List");
			verticalB2CBuilder.append(" AND USER_ACCESS5 IN :outUd5List");
			t3H_3IBuilder.append(" AND USER_ACCESS5 IN :inUd5List");
		}
		if (ud6 != null && !ud6.isEmpty() && outUd6List != null
				&& outUd6List.size() > 0 && inUd6List != null
				&& inUd6List.size() > 0) {
			outwardBuilder.append(" AND USERACCESS6 IN :outUd6List");
			outwardBuilderHdr.append(" AND HDR.USERACCESS6 IN :outUd6List");
			inwardBuilder.append(" AND USERACCESS6 IN :inUd6List");
			inwardBuilderHdr.append(" AND HDR.USERACCESS6 IN :inUd6List");
			anx1aInBuilderHdr.append(" AND HDR.USERACCESS6 IN :inUd6List");
			anx1aOutBuilder.append(" AND  USERACCESS6 IN :outUd6List");
			anx1aInBuilder.append(" AND  USERACCESS6 IN :inUd6List");
			verticalB2CBuilder.append(" AND USER_ACCESS6 IN :outUd6List");
			t3H_3IBuilder.append(" AND USER_ACCESS6 IN :inUd6List");
		}

		/**
		 * @Required field
		 */

		deriveTheGstnUploadDate(taxPeriod, entityId, outGstinList, inGstinList,
				anx1aOutBuilder, anx1aInBuilder, anx1aInBuilderHdr,
				outwardBuilder, outwardBuilderHdr, inwardBuilder,
				gstinUploadDate, initialTaxPeriod, verticalB2CBuilder,
				t3H_3IBuilder, functionType, inwardBuilderHdr);

		String outCondition = outwardBuilder.toString();
		String outConditionHdr = outwardBuilderHdr.toString();
		String anx1aOutCondition = anx1aOutBuilder.toString();
		String anx1aInCondition = anx1aInBuilder.toString();
		String anx1aInConditionHdr = anx1aInBuilderHdr.toString();
		String inCondition = inwardBuilder.toString();
		String inConditionHdr = inwardBuilderHdr.toString();
		String verticalB2CCondition = verticalB2CBuilder.toString();
		String t3H_3ICondition = t3H_3IBuilder.toString();
		StringBuffer outWardBufferString = new StringBuffer();
		StringBuffer inwardBufferString = new StringBuffer();
		if (functionType.equals(ANX1)) {
			outWardBufferString
					.append("SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE, "
							+ "SUM(OUTWARDSUPPLIES) OUTWARDSUPPLIES, SUM(IGST)"
							+ " IGST, SUM(CGST) CGST, SUM(SGST) SGST, SUM(CESS)"
							+ " CESS, SUM(GSTN_NOT_SENT_COUNT)"
							+ " GSTN_NOT_SENT_COUNT, SUM(GSTN_SAVED_COUNT)"
							+ " GSTN_SAVED_COUNT, SUM(GSTN_NOT_SAVED_COUNT)"
							+ " GSTN_NOT_SAVED_COUNT, SUM(GSTN_ERROR_COUNT)"
							+ " GSTN_ERROR_COUNT, SUM(TOTAL_COUNT_IN_ASP) "
							+ "TOTAL_COUNT_IN_ASP, SUM(TOT_COUNT) TOT_COUNT,"
							+ "MAX(MODIFIED_ON) MODIFIED_ON FROM ( SELECT "
							+ "SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE, "
							+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS OUTWARDSUPPLIES, "
							+ "SUM(IFNULL(IGST_AMT,0)) AS IGST,"
							+ " SUM(IFNULL(CGST_AMT,0)) AS CGST, "
							+ "SUM(IFNULL(SGST_AMT,0)) AS SGST,"
							+ " SUM(IFNULL(CESS_AMT_ADVALOREM,0)+IFNULL"
							+ "(CESS_AMT_SPECIFIC,0)) AS CESS, COUNT(CASE "
							+ "WHEN IS_SENT_TO_GSTN = FALSE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_NOT_SENT_COUNT, COUNT(CASE WHEN "
							+ "IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE "
							+ "THEN 1 ELSE NULL END) GSTN_SAVED_COUNT, "
							+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_NOT_SAVED_COUNT, COUNT(CASE WHEN "
							+ "GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN"
							+ " 1 ELSE NULL END) GSTN_ERROR_COUNT, COUNT(CASE"
							+ " WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) "
							+ "TOTAL_COUNT_IN_ASP, COUNT(*) TOT_COUNT, "
							+ "MAX(MODIFIED_ON) MODIFIED_ON FROM "
							+ "ANX_OUTWARD_DOC_HEADER WHERE AN_TABLE_SECTION"
							+ " IN ('3B', '3C', '3D', '3E', '3F', '3G') "
							+ "AND AN_RETURN_TYPE='ANX1'  AND"
							+ " IS_PROCESSED = TRUE AND IS_DELETE = FALSE ");
			if (!outCondition.equals("")) {
				outWardBufferString.append(outCondition);
			}
			outWardBufferString
					.append("GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD, DOC_TYPE) "
							+ "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE "
							+ "UNION ALL SELECT SUPPLIER_GSTIN,RETURN_PERIOD,"
							+ "DOC_TYPE, SUM(OUTWARDSUPPLIES) OUTWARDSUPPLIES,"
							+ " SUM(IGST) IGST, SUM(CGST) CGST, SUM(SGST) SGST,"
							+ " SUM(CESS) CESS, SUM(GSTN_NOT_SENT_COUNT) "
							+ "GSTN_NOT_SENT_COUNT, SUM(GSTN_SAVED_COUNT) "
							+ "GSTN_SAVED_COUNT, SUM(GSTN_NOT_SAVED_COUNT) "
							+ "GSTN_NOT_SAVED_COUNT, SUM(GSTN_ERROR_COUNT)"
							+ " GSTN_ERROR_COUNT, SUM(TOTAL_COUNT_IN_ASP) "
							+ "TOTAL_COUNT_IN_ASP, COUNT(*) TOT_COUNT,"
							+ "MAX(MODIFIED_ON) MODIFIED_ON FROM ( SELECT "
							+ "SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE,"
							+ " SUM(OUTWARDSUPPLIES) OUTWARDSUPPLIES, "
							+ "SUM(IGST) IGST, SUM(CGST) CGST, SUM(SGST) SGST,"
							+ " SUM(CESS) CESS, SUM(GSTN_NOT_SENT_COUNT)"
							+ " GSTN_NOT_SENT_COUNT, SUM(GSTN_SAVED_COUNT) "
							+ "GSTN_SAVED_COUNT, SUM(GSTN_NOT_SAVED_COUNT)"
							+ " GSTN_NOT_SAVED_COUNT, SUM(GSTN_ERROR_COUNT)"
							+ " GSTN_ERROR_COUNT, SUM(TOTAL_COUNT_IN_ASP) "
							+ "TOTAL_COUNT_IN_ASP, MAX(MODIFIED_ON) MODIFIED_ON "
							+ "FROM (SELECT HDR.SUPPLIER_GSTIN,"
							+ "HDR.RETURN_PERIOD,DOC_TYPE, SUM(IFNULL"
							+ "(HDR.TAXABLE_VALUE,0)) AS OUTWARDSUPPLIES,"
							+ " SUM(IFNULL(HDR.IGST_AMT,0)) AS IGST,"
							+ " SUM(IFNULL(HDR.CGST_AMT,0)) AS CGST,"
							+ " SUM(IFNULL(HDR.SGST_AMT,0)) AS SGST,"
							+ " SUM(IFNULL(HDR.CESS_AMT_ADVALOREM,0)+IFNULL"
							+ "(HDR.CESS_AMT_SPECIFIC,0)) AS CESS, COUNT(CASE"
							+ " WHEN IS_SENT_TO_GSTN = FALSE AND HDR.IS_DELETE"
							+ " = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT,"
							+ " COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND "
							+ "HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_SAVED_COUNT, COUNT(CASE WHEN "
							+ "IS_SAVED_TO_GSTN = FALSE AND HDR.IS_DELETE = "
							+ "FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT, "
							+ "COUNT(CASE WHEN GSTN_ERROR = TRUE AND "
							+ "HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END)"
							+ " GSTN_ERROR_COUNT, COUNT(CASE WHEN "
							+ "HDR.IS_DELETE=FALSE THEN 1 ELSE NULL END) "
							+ "TOTAL_COUNT_IN_ASP, MAX(HDR.MODIFIED_ON) "
							+ "MODIFIED_ON FROM ANX_OUTWARD_DOC_HEADER HDR"
							+ " INNER JOIN ANX_OUTWARD_DOC_ITEM "
							+ "ITM ON HDR.ID=ITM.DOC_HEADER_ID WHERE "
							+ "AN_TABLE_SECTION IN ('3A') "
							+ " AND AN_RETURN_TYPE='ANX1' AND"
							+ " IS_PROCESSED = TRUE AND HDR.IS_DELETE = FALSE ");
			if (!outConditionHdr.equals("")) {
				outWardBufferString.append(outConditionHdr);
			}
			outWardBufferString
					.append(" GROUP BY DOC_TYPE,AN_RETURN_TYPE,HDR.SUPPLIER_GSTIN,"
							+ "HDR.RETURN_PERIOD,HDR.POS, HDR.DIFF_PERCENT,"
							+ "HDR.SECTION7_OF_IGST_FLAG,"
							+ "HDR.AUTOPOPULATE_TO_REFUND,TAX_RATE) GROUP BY "
							+ "SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE ) GROUP BY"
							+ " SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE UNION "
							+ "ALL SELECT SUPPLIER_GSTIN, RETURN_PERIOD,DOC_TYPE,"
							+ " SUM(OUTWARDSUPPLIES) OUTWARDSUPPLIES, "
							+ "SUM(IGST) IGST, SUM(CGST) CGST, SUM(SGST) SGST,"
							+ " SUM(CESS) CESS, SUM(GSTN_NOT_SENT_COUNT) "
							+ "GSTN_NOT_SENT_COUNT, SUM(GSTN_SAVED_COUNT)"
							+ " GSTN_SAVED_COUNT, SUM(GSTN_NOT_SAVED_COUNT)"
							+ " GSTN_NOT_SAVED_COUNT, SUM(GSTN_ERROR_COUNT) "
							+ "GSTN_ERROR_COUNT, SUM(TOTAL_COUNT_IN_ASP)"
							+ " TOTAL_COUNT_IN_ASP, COUNT(*) TOT_COUNT ,"
							+ "MAX(MODIFIED_ON) MODIFIED_ON FROM ( SELECT "
							+ "SUPPLIER_GSTIN,RETURN_PERIOD,'INV' DOC_TYPE,"
							+ "DIFF_PERCENT,SEC7_OF_IGST_FLAG,"
							+ "AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE, "
							+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS OUTWARDSUPPLIES,"
							+ " SUM(IFNULL(IGST_AMT,0)) AS IGST,"
							+ " SUM(IFNULL(CGST_AMT,0)) AS CGST, "
							+ "SUM(IFNULL(SGST_AMT,0)) AS SGST, "
							+ "SUM(IFNULL(CESS_AMT,0)) AS CESS, "
							+ "COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END)"
							+ " GSTN_NOT_SENT_COUNT, COUNT(CASE WHEN"
							+ " IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE "
							+ "THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
							+ " COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND"
							+ " IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_NOT_SAVED_COUNT, COUNT(CASE WHEN "
							+ "GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN 1"
							+ " ELSE NULL END) GSTN_ERROR_COUNT, COUNT(CASE"
							+ " WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) "
							+ "TOTAL_COUNT_IN_ASP, MAX(MODIFIED_ON)"
							+ " MODIFIED_ON FROM "
							+ "ANX_PROCESSED_B2C WHERE IS_DELETE = FALSE "
							+ "AND RETURN_TYPE='ANX-1' ");
			if (!verticalB2CCondition.equals("")) {
				outWardBufferString.append(verticalB2CCondition);
			}
			outWardBufferString.append(" GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
					+ "DIFF_PERCENT,SEC7_OF_IGST_FLAG ,AUTOPOPULATE_TO_REFUND ,"
					+ "POS,TAX_RATE) GROUP BY SUPPLIER_GSTIN, "
					+ "RETURN_PERIOD,DOC_TYPE;");
			LOGGER.debug(
					"Outward query from database is-->" + outWardBufferString);

			inwardBufferString
					.append("SELECT CUST_GSTIN,RETURN_PERIOD,DOC_TYPE,"
							+ " SUM(INWARDSUPPLIES) INWARDSUPPLIES, SUM(IGST) IGST, "
							+ "SUM(CGST) CGST, SUM(SGST) SGST, SUM(CESS) CESS,"
							+ " SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT, "
							+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT, "
							+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT, "
							+ "SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT, "
							+ "SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP,"
							+ " SUM(TOT_COUNT) TOT_COUNT,MAX(MODIFIED_ON) MODIFIED_ON "
							+ "FROM ( SELECT CUST_GSTIN,RETURN_PERIOD,DOC_TYPE, "
							+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARDSUPPLIES, "
							+ "SUM(IFNULL(IGST_AMT,0)) AS IGST,"
							+ " SUM(IFNULL(CGST_AMT,0)) AS CGST, "
							+ "SUM(IFNULL(SGST_AMT,0)) AS SGST,"
							+ " SUM(IFNULL(CESS_AMT_ADVALOREM,0)+IFNULL"
							+ "(CESS_AMT_SPECIFIC,0)) AS CESS, COUNT(CASE"
							+ " WHEN IS_SENT_TO_GSTN = FALSE AND IS_DELETE = FALSE"
							+ " THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT, "
							+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_SAVED_COUNT, COUNT(CASE WHEN"
							+ " IS_SAVED_TO_GSTN = FALSE AND IS_DELETE = FALSE "
							+ "THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT, "
							+ "COUNT(CASE WHEN GSTN_ERROR = TRUE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_ERROR_COUNT, COUNT(CASE WHEN "
							+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) "
							+ "TOTAL_COUNT_IN_ASP, COUNT(*) TOT_COUNT, "
							+ "MAX(MODIFIED_ON) MODIFIED_ON FROM "
							+ "ANX_INWARD_DOC_HEADER WHERE AN_TABLE_SECTION IN "
							+ "('3J','3K','3L') AND AN_RETURN_TYPE='ANX1' "
							+ "AND IS_PROCESSED = TRUE AND IS_DELETE = FALSE ");
			if (!inCondition.equals("")) {
				inwardBufferString.append(inCondition);
			}
			inwardBufferString
					.append(" GROUP BY CUST_GSTIN, RETURN_PERIOD, DOC_TYPE) GROUP "
							+ "BY CUST_GSTIN,RETURN_PERIOD,DOC_TYPE UNION ALL "
							+ "SELECT CUST_GSTIN,RETURN_PERIOD,DOC_TYPE,"
							+ " SUM(INWARDSUPPLIES) INWARDSUPPLIES, SUM(IGST) IGST,"
							+ " SUM(CGST) CGST, SUM(SGST) SGST, SUM(CESS) CESS, "
							+ "SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT, "
							+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT, "
							+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT, "
							+ "SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
							+ " SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP, "
							+ "COUNT(*) TOT_COUNT,MAX(MODIFIED_ON) MODIFIED_ON FROM"
							+ " ( SELECT CUST_GSTIN,RETURN_PERIOD,DOC_TYPE,"
							+ " SUM(INWARDSUPPLIES) INWARDSUPPLIES, SUM(IGST) IGST,"
							+ " SUM(CGST) CGST, SUM(SGST) SGST, SUM(CESS) CESS, "
							+ "SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT,"
							+ " SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT, "
							+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
							+ " SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT, "
							+ "SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP,"
							+ " MAX(MODIFIED_ON) MODIFIED_ON FROM ( SELECT "
							+ "CUST_GSTIN,HDR.RETURN_PERIOD RETURN_PERIOD,DOC_TYPE,"
							+ " SUM(IFNULL(HDR.TAXABLE_VALUE,0)) AS INWARDSUPPLIES, "
							+ "SUM(IFNULL(HDR.IGST_AMT,0)) AS IGST, SUM(IFNULL"
							+ "(HDR.CGST_AMT,0)) AS CGST, SUM(IFNULL(HDR.SGST_AMT,0))"
							+ " AS SGST, SUM(IFNULL(HDR.CESS_AMT_ADVALOREM,0)+IFNULL"
							+ "(HDR.CESS_AMT_SPECIFIC,0)) AS CESS, COUNT(CASE WHEN"
							+ " IS_SENT_TO_GSTN = FALSE AND IS_DELETE = FALSE THEN "
							+ "1 ELSE NULL END) GSTN_NOT_SENT_COUNT, COUNT(CASE WHEN"
							+ " IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE THEN 1"
							+ " ELSE NULL END) GSTN_SAVED_COUNT, COUNT(CASE WHEN"
							+ " IS_SAVED_TO_GSTN = FALSE AND IS_DELETE = FALSE THEN 1"
							+ " ELSE NULL END) GSTN_NOT_SAVED_COUNT, COUNT(CASE WHEN "
							+ "GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL"
							+ " END) GSTN_ERROR_COUNT, COUNT(CASE WHEN IS_DELETE=FALSE"
							+ " THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP,"
							+ " MAX(HDR.MODIFIED_ON) MODIFIED_ON FROM "
							+ "ANX_INWARD_DOC_HEADER HDR INNER JOIN "
							+ "ANX_INWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID"
							+ " WHERE AN_TABLE_SECTION IN ('3H','3I') AND "
							+ "AN_RETURN_TYPE='ANX1' AND IS_PROCESSED = TRUE AND"
							+ " IS_DELETE = FALSE");
			if (!inConditionHdr.equals("")) {
				inwardBufferString.append(inConditionHdr);
			}
			inwardBufferString
					.append(" GROUP BY DOC_TYPE,AN_RETURN_TYPE,CUST_GSTIN,"
							+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,HDR.POS,TAX_RATE,"
							+ "ITM_HSNSAC, HDR.DIFF_PERCENT,HDR.SECTION7_OF_IGST_FLAG,"
							+ "HDR.AUTOPOPULATE_TO_REFUND ) GROUP BY CUST_GSTIN,"
							+ "RETURN_PERIOD,DOC_TYPE ) GROUP BY CUST_GSTIN,"
							+ "RETURN_PERIOD,DOC_TYPE UNION ALL SELECT CUST_GSTIN,"
							+ " RETURN_PERIOD,DOC_TYPE, SUM(INWARDSUPPLIES),SUM(IGST),"
							+ " SUM(CGST),SUM(SGST),SUM(CESS), SUM(GSTN_NOT_SENT_COUNT) "
							+ "GSTN_NOT_SENT_COUNT, SUM(GSTN_SAVED_COUNT) "
							+ "GSTN_SAVED_COUNT, SUM(GSTN_NOT_SAVED_COUNT) "
							+ "GSTN_NOT_SAVED_COUNT, SUM(GSTN_ERROR_COUNT) "
							+ "GSTN_ERROR_COUNT, SUM(TOTAL_COUNT_IN_ASP) "
							+ "TOTAL_COUNT_IN_ASP, COUNT(*) TOT_COUNT ,"
							+ "MAX(MODIFIED_ON) MODIFIED_ON FROM ( SELECT "
							+ "CUST_GSTIN,RETURN_PERIOD,'SLF' DOC_TYPE, "
							+ "(CASE WHEN TRAN_FLAG='3H' THEN '3H' WHEN "
							+ "TRAN_FLAG='3I' THEN '3I' END )TABLE_SECTION,"
							+ "DIFF_PERCENT,SEC7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND ,"
							+ "POS,TAX_RATE, SUM(IFNULL(TAXABLE_VALUE,0)) AS "
							+ "INWARDSUPPLIES, SUM(IFNULL(IGST_AMT,0)) AS IGST, "
							+ "SUM(IFNULL(CGST_AMT,0)) AS CGST, SUM(IFNULL"
							+ "(SGST_AMT,0)) AS SGST, SUM(IFNULL(CESS_AMT,0)) "
							+ "AS CESS, COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE "
							+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_NOT_SENT_COUNT, COUNT(CASE WHEN "
							+ "IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE "
							+ "THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
							+ " COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_NOT_SAVED_COUNT, COUNT(CASE WHEN GSTN_ERROR = TRUE"
							+ " AND IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_ERROR_COUNT, COUNT(CASE WHEN IS_DELETE=FALSE "
							+ "THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP, "
							+ "MAX(MODIFIED_ON) MODIFIED_ON FROM ANX_PROCESSED_3H_3I "
							+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='ANX-1' ");
			if (!t3H_3ICondition.equals("")) {
				inwardBufferString.append(t3H_3ICondition);
			}
			inwardBufferString
					.append(" GROUP BY CUST_GSTIN,RETURN_PERIOD,TRAN_FLAG,"
							+ "DIFF_PERCENT,SEC7_OF_IGST_FLAG ,"
							+ "AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE)"
							+ " GROUP BY CUST_GSTIN, RETURN_PERIOD,DOC_TYPE ");
			LOGGER.debug("Inward query from database is-->"
					+ inwardBufferString.toString());
		} else if (functionType.equals(ANX1A)) {
			outWardBufferString
					.append("SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE,"
							+ " SUM(OUTWARDSUPPLIES) OUTWARDSUPPLIES, "
							+ "SUM(IGST) IGST, SUM(CGST) CGST, SUM(SGST) SGST,"
							+ " SUM(CESS) CESS, SUM(GSTN_NOT_SENT_COUNT)"
							+ " GSTN_NOT_SENT_COUNT, SUM(GSTN_SAVED_COUNT) "
							+ "GSTN_SAVED_COUNT, SUM(GSTN_NOT_SAVED_COUNT)"
							+ " GSTN_NOT_SAVED_COUNT, SUM(GSTN_ERROR_COUNT) "
							+ "GSTN_ERROR_COUNT, SUM(TOTAL_COUNT_IN_ASP)"
							+ " TOTAL_COUNT_IN_ASP, SUM(TOT_COUNT) TOT_COUNT,"
							+ "MAX(MODIFIED_ON) MODIFIED_ON FROM ( SELECT "
							+ "SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE, "
							+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS OUTWARDSUPPLIES,"
							+ " SUM(IFNULL(IGST_AMT,0)) AS IGST,"
							+ " SUM(IFNULL(CGST_AMT,0)) AS CGST,"
							+ " SUM(IFNULL(SGST_AMT,0)) AS SGST,"
							+ " SUM(IFNULL(CESS_AMT_ADVALOREM,0)+IFNULL"
							+ "(CESS_AMT_SPECIFIC,0)) AS CESS, COUNT(CASE"
							+ " WHEN IS_SENT_TO_GSTN = FALSE AND"
							+ " IS_DELETE = FALSE THEN 1 ELSE NULL END)"
							+ " GSTN_NOT_SENT_COUNT, COUNT(CASE WHEN"
							+ " IS_SAVED_TO_GSTN = TRUE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END)"
							+ " GSTN_SAVED_COUNT, COUNT(CASE WHEN"
							+ " IS_SAVED_TO_GSTN = FALSE AND IS_DELETE = FALSE"
							+ " THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT, "
							+ "COUNT(CASE WHEN GSTN_ERROR = TRUE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END)"
							+ " GSTN_ERROR_COUNT, COUNT(CASE WHEN "
							+ "IS_DELETE=FALSE THEN 1 ELSE NULL END) "
							+ "TOTAL_COUNT_IN_ASP, COUNT(*) TOT_COUNT,"
							+ " MAX(MODIFIED_ON) MODIFIED_ON FROM "
							+ "ANX_OUTWARD_DOC_HEADER WHERE AN_TABLE_SECTION"
							+ " IN ('3B', '3C') AND AN_RETURN_TYPE='ANX1A' AND"
							+ " IS_PROCESSED = TRUE AND IS_DELETE = FALSE ");
			if (!anx1aOutCondition.equals("")) {
				outWardBufferString.append(anx1aOutCondition);
			}
			outWardBufferString
					.append(" GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD, "
							+ "DOC_TYPE) GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
							+ "DOC_TYPE UNION ALL SELECT SUPPLIER_GSTIN,"
							+ "RETURN_PERIOD,DOC_TYPE, SUM(OUTWARDSUPPLIES)"
							+ " OUTWARDSUPPLIES, SUM(IGST) IGST, SUM(CGST)"
							+ " CGST, SUM(SGST) SGST, SUM(CESS) CESS, "
							+ "SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT,"
							+ " SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT,"
							+ " SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
							+ " SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
							+ " SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP, "
							+ "COUNT(*) TOT_COUNT,MAX(MODIFIED_ON) MODIFIED_ON"
							+ " FROM ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,"
							+ "DOC_TYPE, SUM(OUTWARDSUPPLIES) OUTWARDSUPPLIES,"
							+ " SUM(IGST) IGST, SUM(CGST) CGST, SUM(SGST) SGST,"
							+ " SUM(CESS) CESS, SUM(GSTN_NOT_SENT_COUNT)"
							+ " GSTN_NOT_SENT_COUNT, SUM(GSTN_SAVED_COUNT)"
							+ " GSTN_SAVED_COUNT, SUM(GSTN_NOT_SAVED_COUNT)"
							+ " GSTN_NOT_SAVED_COUNT, SUM(GSTN_ERROR_COUNT)"
							+ " GSTN_ERROR_COUNT, SUM(TOTAL_COUNT_IN_ASP)"
							+ " TOTAL_COUNT_IN_ASP, MAX(MODIFIED_ON)"
							+ " MODIFIED_ON FROM ( SELECT SUPPLIER_GSTIN,"
							+ "RETURN_PERIOD,DOC_TYPE, SUM(IFNULL"
							+ "(TAXABLE_VALUE,0)) AS OUTWARDSUPPLIES,"
							+ " SUM(IFNULL(IGST_AMT,0)) AS IGST, "
							+ "SUM(IFNULL(CGST_AMT,0)) AS CGST,"
							+ " SUM(IFNULL(SGST_AMT,0)) AS SGST,"
							+ " SUM(IFNULL(CESS_AMT_ADVALOREM,0)+IFNULL"
							+ "(CESS_AMT_SPECIFIC,0)) AS CESS, COUNT(CASE"
							+ " WHEN IS_SENT_TO_GSTN = FALSE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_NOT_SENT_COUNT, COUNT(CASE WHEN "
							+ "IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE"
							+ " THEN 1 ELSE NULL END) GSTN_SAVED_COUNT, "
							+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_NOT_SAVED_COUNT, COUNT(CASE WHEN"
							+ " GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN "
							+ "1 ELSE NULL END) GSTN_ERROR_COUNT, COUNT(CASE"
							+ " WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END)"
							+ " TOTAL_COUNT_IN_ASP, MAX(MODIFIED_ON) "
							+ "MODIFIED_ON FROM ANX_OUTWARD_DOC_HEADER WHERE "
							+ "AN_TABLE_SECTION IN ('3A') AND "
							+ "AN_RETURN_TYPE='ANX1A' AND IS_PROCESSED = TRUE"
							+ " AND IS_DELETE = FALSE ");
			if (!anx1aOutCondition.equals("")) {
				outWardBufferString.append(anx1aOutCondition);
			}
			outWardBufferString
					.append(" GROUP BY DOC_TYPE, AN_RETURN_TYPE,SUPPLIER_GSTIN,"
							+ "RETURN_PERIOD,POS,DIFF_PERCENT,"
							+ "SECTION7_OF_IGST_FLAG,AUTOPOPULATE_TO_REFUND )"
							+ " GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,DOC_TYPE"
							+ " ) GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
							+ "DOC_TYPE UNION ALL SELECT SUPPLIER_GSTIN,"
							+ " RETURN_PERIOD,DOC_TYPE, SUM(OUTWARDSUPPLIES)"
							+ " OUTWARDSUPPLIES, SUM(IGST) IGST, SUM(CGST)"
							+ " CGST, SUM(SGST) SGST, SUM(CESS) CESS, "
							+ "SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT, "
							+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT, "
							+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
							+ " SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
							+ " SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP, "
							+ "COUNT(*) TOT_COUNT ,MAX(MODIFIED_ON) MODIFIED_ON"
							+ " FROM ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,"
							+ "'RNV' DOC_TYPE,DIFF_PERCENT,SEC7_OF_IGST_FLAG,"
							+ "AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE,"
							+ " SUM(IFNULL(TAXABLE_VALUE,0)) AS OUTWARDSUPPLIES,"
							+ " SUM(IFNULL(IGST_AMT,0)) AS IGST,"
							+ " SUM(IFNULL(CGST_AMT,0)) AS CGST,"
							+ " SUM(IFNULL(SGST_AMT,0)) AS SGST,"
							+ " SUM(IFNULL(CESS_AMT,0)) AS CESS,"
							+ " COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END)"
							+ " GSTN_NOT_SENT_COUNT, COUNT(CASE WHEN "
							+ "IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE"
							+ " THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
							+ " COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND"
							+ " IS_DELETE = FALSE THEN 1 ELSE NULL END)"
							+ " GSTN_NOT_SAVED_COUNT, COUNT(CASE WHEN"
							+ " GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN "
							+ "1 ELSE NULL END) GSTN_ERROR_COUNT, COUNT(CASE"
							+ " WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END)"
							+ " TOTAL_COUNT_IN_ASP, MAX(MODIFIED_ON)"
							+ " MODIFIED_ON FROM ANX_PROCESSED_B2C WHERE"
							+ " IS_DELETE = FALSE AND RETURN_TYPE='ANX-1A' "
							+ "AND IS_AMENDMENT= TRUE ");
			if (!verticalB2CCondition.equals("")) {
				outWardBufferString.append(verticalB2CCondition);
			}
			outWardBufferString.append("GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,"
					+ "DIFF_PERCENT,SEC7_OF_IGST_FLAG ,"
					+ "AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE)"
					+ " GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD,DOC_TYPE");

			inwardBufferString
					.append("SELECT CUST_GSTIN,RETURN_PERIOD,DOC_TYPE,"
							+ " SUM(INWARDSUPPLIES) INWARDSUPPLIES, SUM(IGST) IGST, "
							+ "SUM(CGST) CGST, SUM(SGST) SGST, SUM(CESS) CESS,"
							+ " SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT,"
							+ " SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT, "
							+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT, "
							+ "SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT, "
							+ "SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP, "
							+ "SUM(TOT_COUNT) TOT_COUNT,MAX(MODIFIED_ON) MODIFIED_ON"
							+ " FROM ( SELECT CUST_GSTIN,RETURN_PERIOD,DOC_TYPE, "
							+ "SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARDSUPPLIES,"
							+ " SUM(IFNULL(IGST_AMT,0)) AS IGST, "
							+ "SUM(IFNULL(CGST_AMT,0)) AS CGST, "
							+ "SUM(IFNULL(SGST_AMT,0)) AS SGST, "
							+ "SUM(IFNULL(CESS_AMT_ADVALOREM,0)+IFNULL"
							+ "(CESS_AMT_SPECIFIC,0)) AS CESS, "
							+ "COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE AND"
							+ " IS_DELETE = FALSE THEN 1 ELSE NULL END)"
							+ " GSTN_NOT_SENT_COUNT, COUNT(CASE WHEN"
							+ " IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE "
							+ "THEN 1 ELSE NULL END) GSTN_SAVED_COUNT, COUNT(CASE"
							+ " WHEN IS_SAVED_TO_GSTN = FALSE AND IS_DELETE = FALSE "
							+ "THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT, COUNT(CASE "
							+ "WHEN GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN 1 "
							+ "ELSE NULL END) GSTN_ERROR_COUNT, COUNT(CASE WHEN"
							+ " IS_DELETE=FALSE THEN 1 ELSE NULL END) "
							+ "TOTAL_COUNT_IN_ASP, COUNT(*) TOT_COUNT, "
							+ "MAX(MODIFIED_ON) MODIFIED_ON FROM "
							+ "ANX_INWARD_DOC_HEADER WHERE AN_TABLE_SECTION IN"
							+ " ('3J','3K') AND AN_RETURN_TYPE='ANX1A' AND "
							+ "IS_PROCESSED = TRUE AND IS_DELETE = FALSE ");
			if (!anx1aInCondition.equals("")) {
				inwardBufferString.append(anx1aInCondition);
			}
			inwardBufferString.append(" GROUP BY CUST_GSTIN, RETURN_PERIOD,"
					+ " DOC_TYPE) GROUP BY CUST_GSTIN,RETURN_PERIOD,DOC_TYPE"
					+ " UNION ALL SELECT CUST_GSTIN,RETURN_PERIOD,DOC_TYPE, "
					+ "SUM(INWARDSUPPLIES) INWARDSUPPLIES, SUM(IGST) IGST, "
					+ "SUM(CGST) CGST, SUM(SGST) SGST, SUM(CESS) CESS, "
					+ "SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT, "
					+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT, "
					+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
					+ " SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
					+ " SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP, "
					+ "COUNT(*) TOT_COUNT,MAX(MODIFIED_ON) MODIFIED_ON FROM"
					+ " ( SELECT CUST_GSTIN,RETURN_PERIOD,DOC_TYPE, "
					+ "SUM(INWARDSUPPLIES) INWARDSUPPLIES, SUM(IGST) IGST,"
					+ " SUM(CGST) CGST, SUM(SGST) SGST, SUM(CESS) CESS, "
					+ "SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT, "
					+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT,"
					+ " SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
					+ " SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT, "
					+ "SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP, "
					+ "MAX(MODIFIED_ON) MODIFIED_ON FROM ( SELECT "
					+ "CUST_GSTIN,HDR.RETURN_PERIOD RETURN_PERIOD,DOC_TYPE,"
					+ " SUM(IFNULL(HDR.TAXABLE_VALUE,0)) AS INWARDSUPPLIES,"
					+ " SUM(IFNULL(HDR.IGST_AMT,0)) AS IGST, "
					+ "SUM(IFNULL(HDR.CGST_AMT,0)) AS CGST, "
					+ "SUM(IFNULL(HDR.SGST_AMT,0)) AS SGST,"
					+ " SUM(IFNULL(HDR.CESS_AMT_ADVALOREM,0)+IFNULL"
					+ "(HDR.CESS_AMT_SPECIFIC,0)) AS CESS, COUNT(CASE "
					+ "WHEN IS_SENT_TO_GSTN = FALSE AND IS_DELETE = FALSE "
					+ "THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT, "
					+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND "
					+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) "
					+ "GSTN_SAVED_COUNT, COUNT(CASE WHEN "
					+ "IS_SAVED_TO_GSTN = FALSE AND IS_DELETE = FALSE THEN"
					+ " 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT, COUNT(CASE "
					+ "WHEN GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN 1 "
					+ "ELSE NULL END) GSTN_ERROR_COUNT, COUNT(CASE WHEN "
					+ "IS_DELETE=FALSE THEN 1 ELSE NULL END)"
					+ " TOTAL_COUNT_IN_ASP, MAX(HDR.MODIFIED_ON)"
					+ " MODIFIED_ON FROM ANX_INWARD_DOC_HEADER HDR INNER"
					+ " JOIN ANX_INWARD_DOC_ITEM ITM ON "
					+ "HDR.ID=ITM.DOC_HEADER_ID WHERE AN_TABLE_SECTION IN"
					+ " ('3H','3I') AND AN_RETURN_TYPE='ANX1A' AND "
					+ "IS_PROCESSED = TRUE AND IS_DELETE = FALSE ");
			if (!inwardBuilderHdr.equals("")) {
				inwardBufferString.append(anx1aInConditionHdr);
			}
			inwardBufferString
					.append(" GROUP BY DOC_TYPE,AN_RETURN_TYPE,CUST_GSTIN,"
							+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,HDR.POS,"
							+ "TAX_RATE,ITM_HSNSAC, HDR.DIFF_PERCENT,"
							+ "HDR.SECTION7_OF_IGST_FLAG,"
							+ "HDR.AUTOPOPULATE_TO_REFUND ) GROUP BY "
							+ "CUST_GSTIN,RETURN_PERIOD,DOC_TYPE )"
							+ " GROUP BY CUST_GSTIN,RETURN_PERIOD,DOC_TYPE "
							+ "UNION ALL SELECT CUST_GSTIN, RETURN_PERIOD,"
							+ "DOC_TYPE, SUM(INWARDSUPPLIES),SUM(IGST), "
							+ "SUM(CGST),SUM(SGST),SUM(CESS), "
							+ "SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT,"
							+ " SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT, "
							+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT, "
							+ "SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
							+ " SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP, "
							+ "COUNT(*) TOT_COUNT ,MAX(MODIFIED_ON) MODIFIED_ON"
							+ " FROM ( SELECT CUST_GSTIN,RETURN_PERIOD,"
							+ " 'RSLF' AS DOC_TYPE, (CASE WHEN TRAN_FLAG='3H'"
							+ " THEN '3H' WHEN TRAN_FLAG='3I' THEN '3I' END )"
							+ "TABLE_SECTION,DIFF_PERCENT,SEC7_OF_IGST_FLAG,"
							+ "AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE,"
							+ " SUM(IFNULL(TAXABLE_VALUE,0)) AS INWARDSUPPLIES,"
							+ " SUM(IFNULL(IGST_AMT,0)) AS IGST,"
							+ " SUM(IFNULL(CGST_AMT,0)) AS CGST,"
							+ " SUM(IFNULL(SGST_AMT,0)) AS SGST,"
							+ " SUM(IFNULL(CESS_AMT,0)) AS CESS, "
							+ "COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE AND"
							+ " IS_DELETE = FALSE THEN 1 ELSE NULL END)"
							+ " GSTN_NOT_SENT_COUNT, COUNT(CASE WHEN "
							+ "IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE"
							+ " THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
							+ " COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND "
							+ "IS_DELETE = FALSE THEN 1 ELSE NULL END) "
							+ "GSTN_NOT_SAVED_COUNT, COUNT(CASE WHEN "
							+ "GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN"
							+ " 1 ELSE NULL END) GSTN_ERROR_COUNT, COUNT(CASE"
							+ " WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) "
							+ "TOTAL_COUNT_IN_ASP, MAX(MODIFIED_ON) MODIFIED_ON"
							+ " FROM ANX_PROCESSED_3H_3I WHERE "
							+ "IS_DELETE = FALSE AND RETURN_TYPE='ANX-1A' "
							+ "AND IS_AMENDMENT=TRUE ");
			if (!t3H_3ICondition.equals("")) {
				inwardBufferString.append(t3H_3ICondition);
			}
			inwardBufferString
					.append(" GROUP BY CUST_GSTIN,RETURN_PERIOD,TRAN_FLAG,"
							+ "DIFF_PERCENT,SEC7_OF_IGST_FLAG ,"
							+ "AUTOPOPULATE_TO_REFUND ,POS,TAX_RATE)"
							+ " GROUP BY CUST_GSTIN, RETURN_PERIOD,DOC_TYPE");

			LOGGER.debug("Inward query from database is-->"
					+ inwardBufferString.toString());

		}

		LOGGER.error(
				"bufferString-------------------------->" + outWardBufferString,
				inwardBufferString);
		return outWardBufferString + "#@" + inwardBufferString.toString();
	}

	private void deriveTheGstnUploadDate(int taxPeriod, List<Long> entityId,
			List<String> sgstins, List<String> cgstins,
			StringBuilder anx1aOutBuilder, StringBuilder anx1aInBuilder,
			StringBuilder anx1aInBuilderHdr, StringBuilder outwardBuilder,
			StringBuilder outwardBuilderhdr, StringBuilder inwardBuilder,
			String gstinUploadDate, String initialTaxPeriod,
			StringBuilder verticalB2CBuilder, StringBuilder t3H_3IBuilder,
			String functionType, StringBuilder inwardBuilderHdr) {

		if (taxPeriod != 0 && initialTaxPeriod != null
				&& functionType.equals(ANX1) && gstinUploadDate != null
				&& !gstinUploadDate.equals("")) {
			switch (gstinUploadDate) {
			case "AGGREGATE":
				if (entityId != null && !entityId.contains("")
						|| !sgstins.isEmpty()) {
					outwardBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
					outwardBuilderhdr
							.append(" AND HDR.DERIVED_RET_PERIOD = :period ");
					verticalB2CBuilder
							.append(" AND DERIVED_RET_PERIOD = :period ");
				} else if (!entityId.contains("")) {
					outwardBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
					verticalB2CBuilder
							.append(" AND DERIVED_RET_PERIOD = :period ");
				}

				if (entityId != null && !entityId.contains("")
						|| !cgstins.isEmpty()) {
					inwardBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
					inwardBuilderHdr
							.append(" AND HDR.DERIVED_RET_PERIOD = :period ");
					t3H_3IBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
				} else if (!entityId.contains("")) {
					inwardBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
					inwardBuilderHdr
							.append(" AND HDR.DERIVED_RET_PERIOD = :period ");
					t3H_3IBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
				}

				break;
			default:
				throw new AppException("Insufficent Search");

			}
		} else if (taxPeriod != 0 && initialTaxPeriod != null
				&& functionType.equals(ANX1A)) {
			if (entityId != null && !entityId.contains("")
					|| !sgstins.isEmpty()) {
				outwardBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
				outwardBuilderhdr
						.append(" AND  HDR.DERIVED_RET_PERIOD = :period ");
				anx1aOutBuilder.append(" AND  DERIVED_RET_PERIOD = :period ");
				verticalB2CBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
			} else if (!entityId.contains("")) {
				outwardBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
				outwardBuilderhdr
						.append(" AND  HDR.DERIVED_RET_PERIOD = :period ");
				verticalB2CBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
				anx1aOutBuilder.append(" AND  DERIVED_RET_PERIOD = :period ");
			}

			if (entityId != null && !entityId.contains("")
					|| !cgstins.isEmpty()) {
				inwardBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
				anx1aInBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
				anx1aInBuilderHdr
						.append(" AND  HDR.DERIVED_RET_PERIOD = :period ");
				t3H_3IBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
			} else if (!entityId.contains("")) {
				inwardBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
				anx1aInBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
				anx1aInBuilderHdr
						.append(" AND  HDR.DERIVED_RET_PERIOD = :period ");
				t3H_3IBuilder.append(" AND DERIVED_RET_PERIOD = :period ");
			}
		}
	}
}