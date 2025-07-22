package com.ey.advisory.app.docs.service.gstr6;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr6DistributionEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr6DistributionRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DistributedSummaryScreenRequestDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DistributedSummaryScreenResponseDto;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import io.jsonwebtoken.lang.Collections;

@Service("Gstr6DistributedSummaryServiceImpl")
public class Gstr6DistributedSummaryServiceImpl
		implements Gstr6DistributedSummaryService {
	private static final Logger LOGGER = org.slf4j.LoggerFactory
			.getLogger(Gstr6DistributedSummaryServiceImpl.class);

	@Autowired
	@Qualifier("BasicCommonSecParam")
	private BasicCommonSecParam basicCommonSecParam;

	@Autowired
	@Qualifier("Gstr6DistributionRepository")
	private Gstr6DistributionRepository gstr6DistributionSummaryRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;
	

	@Override
	public List<Gstr6DistributedSummaryScreenResponseDto> getGstr6DistributedEliSummaryList(
			Annexure1SummaryReqDto reqDto) {
		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);

			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			List<Object[]> gstr6DistributionList = gstr6DistributionSummaryRepository
					.getGstr6DistributedSummaryData(taxPeriod, gstinList);
			for (Object[] arr : gstr6DistributionList) {

				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);

				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList1 :: {}",
					ex.getMessage());
		}

		return responseList;

	}

	/* pagination for summary1 */
	@Override
	public List<Gstr6DistributedSummaryScreenResponseDto> getGstr6DistributedEliSummaryListPagination(
			Annexure1SummaryReqDto reqDto, Pageable pageReq) {
		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);

			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			if (Collections.isEmpty(gstinList)) {
				try {
					List<Long> entityIds = reqDto.getEntity();
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getInwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs1 = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstinList = dataSecAttrs1.get(OnboardingConstant.GSTIN);
				} catch (Exception ex) {
					String msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
		
			List<Object[]> gstr6DistributionList = gstr6DistributionSummaryRepository
					.getGstr6DistributedSummaryDataPagination(taxPeriod,
							gstinList, pageReq);
			for (Object[] arr : gstr6DistributionList) {

				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);

				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList1 :: {}",
					ex.getMessage());
		}

		return responseList;

	}

	@Override
	public List<Gstr6DistributedSummaryScreenResponseDto> getGstr6DistributedInEliSummaryList(
			Annexure1SummaryReqDto reqDto) {
		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);
			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			List<Object[]> gstr6DistributionList1 = gstr6DistributionSummaryRepository
					.getGstr6DistributedSummaryIneligibleData(taxPeriod,
							gstinList);
			for (Object[] arr : gstr6DistributionList1) {
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);
				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList1 :: {}",
					ex.getMessage());
		}
		return responseList;

	}

	@Override
	public List<Gstr6DistributedSummaryScreenResponseDto> getGstr6ReDistributedSummaryList(
			Annexure1SummaryReqDto reqDto) {
		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);
			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			List<Object[]> gstr6DistributionList = gstr6DistributionSummaryRepository
					.getGstr6ReDistributedSummaryData(taxPeriod, gstinList);
			for (Object[] arr : gstr6DistributionList) {
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);
				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseDto.setOrigCrNoteNumber(
						arr[23] != null ? String.valueOf(arr[23]) : null);
				responseDto.setOrigCrNoteDate(
						arr[24] != null ? (LocalDate) arr[24] : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList2 :: {}",
					ex.getMessage());
		}
		return responseList;
	}

	@Override
	public List<Gstr6DistributedSummaryScreenResponseDto> getGstr6ReDistributedInEligibleSummaryList(
			Annexure1SummaryReqDto reqDto) {
		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);
			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			List<Object[]> gstr6DistributionList = gstr6DistributionSummaryRepository
					.getGstr6ReDistributedInEligibleSummaryData(taxPeriod,
							gstinList);
			for (Object[] arr : gstr6DistributionList) {
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);
				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseDto.setOrigCrNoteNumber(
						arr[23] != null ? String.valueOf(arr[23]) : null);
				responseDto.setOrigCrNoteDate(
						arr[24] != null ? (LocalDate) arr[24] : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList2 :: {}",
					ex.getMessage());
		}
		return responseList;
	}

	public Gstr6DistributedSummaryScreenRequestDto setGstr6DataSecuritySearchParams(
			Gstr6DistributedSummaryScreenRequestDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		if (req.getDataSecAttrs() == null || req.getDataSecAttrs().isEmpty()) {
			req.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = req.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				req.setDataSecAttrs(dataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				req.setDataSecAttrs(dataSecAttrs);
			}
		}
		return req;
	}

	@Override
	public void saveGstr6DistributedSummaryData(
			List<Gstr6DistributedSummaryScreenRequestDto> dtos) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("saveGstr6DistributedSummaryData1 Data begin");
		}
		List<Long> ids = new ArrayList<>();
		List<Gstr6DistributionEntity> entities = new ArrayList<>();
		for (Gstr6DistributedSummaryScreenRequestDto dto : dtos) {
			Gstr6DistributionEntity entity = new Gstr6DistributionEntity();
			if (dto.getId() != null) {
				ids.add(dto.getId());
			}
			entity.setReturnPeriod(dto.getReturnPeriod());
			entity.setDerivedRetPeriod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
			entity.setProcessKey(dto.getDocKey());
			entity.setFileId(dto.getFileId());
			entity.setIsdGstin(dto.getIsdGstin());
			entity.setRecipientGSTIN(dto.getRecipientGSTIN());
			entity.setStateCode(dto.getStateCode());
			entity.setOriginalRecipeintGstin(dto.getOriginalRecipeintGstin());
			entity.setOriginalStatecode(dto.getOriginalStatecode());
			entity.setDocumentType(dto.getDocumentType());
			entity.setSupplyType(dto.getSupplyType());
			entity.setDocNum(dto.getDocNum());
			entity.setDocDate(dto.getDocDate());
			entity.setOrigDocNumber(dto.getOrigDocNumber());
			entity.setOrigDocDate(dto.getOrigDocDate());
			entity.setEligibleIndicator(dto.getEligibleIndicator());
			entity.setIgstAsIgst(dto.getIgstAsIgst());
			entity.setIgstAsSgst(dto.getIgstAsSgst());
			entity.setIgstAsCgst(dto.getIgstAsCgst());
			entity.setSgstAsSgst(dto.getSgstAsSgst());
			entity.setSgstAsIgst(dto.getSgstAsIgst());
			entity.setCgstAsIgst(dto.getCgstAsIgst());
			entity.setCgstAsCgst(dto.getCgstAsCgst());
			entity.setCessAmount(dto.getCessAmount());
			entity.setProcessed(true);
			entity.setCreatedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entities.add(entity);
		}
		if (!entities.isEmpty()) {
			gstr6DistributionSummaryRepository.saveAll(entities);
		}
		if (!ids.isEmpty()) {
			gstr6DistributionSummaryRepository.deleteEntityByIds(ids);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("saveGstr6DistributedSummaryData Data End");
		}
	}

	@Override
	public void deleteEntity(
			final List<Gstr6DistributedSummaryScreenRequestDto> dtos) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6DistributedSummaryServiceImpl delete entityId Begin");
		}
		List<Long> ids = new ArrayList<>();
		if (!dtos.isEmpty()) {
			dtos.forEach(deleteDto -> {
				if (deleteDto.getId() != null && deleteDto.getId() > 0) {
					ids.add(deleteDto.getId());
				}
			});
			gstr6DistributionSummaryRepository.deleteEntityByIds(ids);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6DistributedSummaryServiceImpl delete entityId End");
		}
	}

	@Override
	public List<Gstr6DistributedSummaryScreenResponseDto> getGstr6DistributedInEliSummaryListPagination(
			Annexure1SummaryReqDto reqDto, Pageable pageReq) {

		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);
			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			if (Collections.isEmpty(gstinList)) {
				try {
					List<Long> entityIds = reqDto.getEntity();
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getInwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs1 = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstinList = dataSecAttrs1.get(OnboardingConstant.GSTIN);
				} catch (Exception ex) {
					String msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			List<Object[]> gstr6DistributionList1 = gstr6DistributionSummaryRepository
					.getGstr6DistributedSummaryIneligibleDataPagination(
							taxPeriod, gstinList, pageReq);
			for (Object[] arr : gstr6DistributionList1) {
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);
				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList1 :: {}",
					ex.getMessage());
		}
		return responseList;

	}

	@Override
	public List<Gstr6DistributedSummaryScreenResponseDto> getGstr6ReDistributedSummaryListPagination(
			Annexure1SummaryReqDto reqDto, Pageable pageReq) {

		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);
			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

			if (Collections.isEmpty(gstinList)) {
				try {
					List<Long> entityIds = reqDto.getEntity();
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getInwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs1 = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstinList = dataSecAttrs1.get(OnboardingConstant.GSTIN);
				} catch (Exception ex) {
					String msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			List<Object[]> gstr6DistributionList = gstr6DistributionSummaryRepository
					.getGstr6ReDistributedSummaryDataPagination(taxPeriod,
							gstinList, pageReq);
			for (Object[] arr : gstr6DistributionList) {
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);
				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseDto.setOrigCrNoteNumber(
						arr[23] != null ? String.valueOf(arr[23]) : null);
				responseDto.setOrigCrNoteDate(
						arr[24] != null ? (LocalDate) arr[24] : null);
				responseDto.setOriginalRecipeintGstin(arr[25] != null ? String.valueOf(arr[25]) : null);
				
				responseDto.setOriginalStatecode(arr[26] != null ? String.valueOf(arr[26]) : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList2 :: {}",
					ex.getMessage());
		}
		return responseList;

	}

	@Override
	public List<Gstr6DistributedSummaryScreenResponseDto> getGstr6ReDistributedInEligibleSummaryListPagination(
			Annexure1SummaryReqDto reqDto, Pageable pageReq) {
		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);
			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			if (Collections.isEmpty(gstinList)) {
				try {
					List<Long> entityIds = reqDto.getEntity();
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getInwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs1 = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstinList = dataSecAttrs1.get(OnboardingConstant.GSTIN);
				} catch (Exception ex) {
					String msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}

			List<Object[]> gstr6DistributionList = gstr6DistributionSummaryRepository
					.getGstr6ReDistributedInEligibleSummaryDataPagination(
							taxPeriod, gstinList, pageReq);
			for (Object[] arr : gstr6DistributionList) {
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);
				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseDto.setOrigCrNoteNumber(
						arr[23] != null ? String.valueOf(arr[23]) : null);
				responseDto.setOrigCrNoteDate(
						arr[24] != null ? (LocalDate) arr[24] : null);
				responseDto.setOriginalRecipeintGstin(arr[25] != null ? String.valueOf(arr[25]) : null);
				
				responseDto.setOriginalStatecode(arr[26] != null ? String.valueOf(arr[26]) : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList2 :: {}",
					ex.getMessage());
		}
		return responseList;
	}

	@Override
	public int getGstr6DistributedEliSummaryListCount(
			Annexure1SummaryReqDto reqDto) {

		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		int size = 0;
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);

			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			if (Collections.isEmpty(gstinList)) {
				try {
					List<Long> entityIds = reqDto.getEntity();
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getInwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs1 = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstinList = dataSecAttrs1.get(OnboardingConstant.GSTIN);
				} catch (Exception ex) {
					String msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
						
			List<Object[]> gstr6DistributionList = gstr6DistributionSummaryRepository
					.getGstr6DistributedSummaryData(taxPeriod, gstinList);
			size = gstr6DistributionList.size();

			for (Object[] arr : gstr6DistributionList) {
				
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);

				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList1 :: {}",
					ex.getMessage());
		}

		return size;

	}

	@Override
	public int getGstr6DistributedInEliSummaryListCount(
			Annexure1SummaryReqDto reqDto) {

		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		int size = 0;
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);
			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			if (Collections.isEmpty(gstinList)) {
				try {
					List<Long> entityIds = reqDto.getEntity();
					LOGGER.debug("entityIds  -> " + entityIds.toString());
					//entityIds.add(enityId);
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getOutwardSecurityAttributeMap();
					dataSecAttrs = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					LOGGER.debug("datasecAttrs  -> " + dataSecAttrs.toString());
				
					List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				
				List<String> regTypeList = Arrays.asList("ISD");
				ttlGstinList = gstnDetailRepository
						.filterGstinBasedByRegType(ttlGstinList, regTypeList);
				dataSecAttrs.put(OnboardingConstant.GSTIN, ttlGstinList);
				LOGGER.debug("dataSecAttrs  -> " + dataSecAttrs);
				gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				LOGGER.debug("gstinList  -> " + gstinList);
				} catch (Exception ex) {
					String msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			
			LOGGER.debug("gstinList  -> " + gstinList);

			LOGGER.debug("taxPeriod  -> " + taxPeriod.toString());
			
			List<Object[]> gstr6DistributionList1 = gstr6DistributionSummaryRepository
					.getGstr6DistributedSummaryIneligibleData(taxPeriod,
							gstinList);
			size = gstr6DistributionList1.size();
			for (Object[] arr : gstr6DistributionList1) {
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);
				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList1 :: {}",
					ex.getMessage());
		}
		return size;

	}

	@Override
	public int getGstr6ReDistributedSummaryListCount(
			Annexure1SummaryReqDto reqDto) {

		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		int size = 0;
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);
			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			if (Collections.isEmpty(gstinList)) {
				try {
					List<Long> entityIds = reqDto.getEntity();
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getInwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs1 = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstinList = dataSecAttrs1.get(OnboardingConstant.GSTIN);
				} catch (Exception ex) {
					String msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			List<Object[]> gstr6DistributionList = gstr6DistributionSummaryRepository
					.getGstr6ReDistributedSummaryData(taxPeriod, gstinList);
			size = gstr6DistributionList.size();
			for (Object[] arr : gstr6DistributionList) {
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);
				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseDto.setOrigCrNoteNumber(
						arr[23] != null ? String.valueOf(arr[23]) : null);
				responseDto.setOrigCrNoteDate(
						arr[24] != null ? (LocalDate) arr[24] : null);
				responseList.add(responseDto);
				responseDto.setOriginalRecipeintGstin(arr[25] != null ? String.valueOf(arr[25]) : null);
				
				responseDto.setOriginalStatecode(arr[26] != null ? String.valueOf(arr[26]) : null);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList2 :: {}",
					ex.getMessage());
		}
		return size;

	}

	@Override
	public int getGstr6ReDistributedInEligibleSummaryListCount(
			Annexure1SummaryReqDto reqDto) {
		List<Gstr6DistributedSummaryScreenResponseDto> responseList = new ArrayList<>();
		int size = 0;
		try {
			Annexure1SummaryReqDto req = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(reqDto);
			String taxPeriodReq = req.getTaxPeriod();
			Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
			if (Collections.isEmpty(gstinList)) {
				try {
					List<Long> entityIds = reqDto.getEntity();
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getInwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs1 = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstinList = dataSecAttrs1.get(OnboardingConstant.GSTIN);
				} catch (Exception ex) {
					String msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			List<Object[]> gstr6DistributionList = gstr6DistributionSummaryRepository
					.getGstr6ReDistributedInEligibleSummaryData(taxPeriod,
							gstinList);
			size = gstr6DistributionList.size();
			for (Object[] arr : gstr6DistributionList) {
				Gstr6DistributedSummaryScreenResponseDto responseDto = new Gstr6DistributedSummaryScreenResponseDto();
				responseDto.setId(arr[0] != null
						? new Long(String.valueOf(arr[0])) : null);
				responseDto.setReturnPeriod(
						arr[1] != null ? String.valueOf(arr[1]) : null);
				responseDto.setIsdGstin(
						arr[2] != null ? String.valueOf(arr[2]) : null);
				responseDto.setRecipientGSTIN(
						arr[3] != null ? String.valueOf(arr[3]) : null);
				responseDto.setStateCode(
						arr[4] != null ? String.valueOf(arr[4]) : null);
				responseDto.setDocumentType(
						arr[5] != null ? String.valueOf(arr[5]) : null);
				responseDto.setSupplyType(
						arr[6] != null ? String.valueOf(arr[6]) : null);
				responseDto.setDocNum(
						arr[7] != null ? String.valueOf(arr[7]) : null);
				responseDto
						.setDocDate(arr[8] != null ? (LocalDate) arr[8] : null);
				responseDto.setOrigDocNumber(
						arr[9] != null ? String.valueOf(arr[9]) : null);
				responseDto.setOrigDocDate(
						arr[10] != null ? (LocalDate) arr[10] : null);
				responseDto.setEligibleIndicator(
						arr[11] != null ? String.valueOf(arr[11]) : null);
				responseDto.setIgstAsIgst(arr[12] != null
						? new BigDecimal(String.valueOf(arr[12]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsSgst(arr[13] != null
						? new BigDecimal(String.valueOf(arr[13]))
						: BigDecimal.ZERO);
				responseDto.setIgstAsCgst(arr[14] != null
						? new BigDecimal(String.valueOf(arr[14]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsSgst(arr[15] != null
						? new BigDecimal(String.valueOf(arr[15]))
						: BigDecimal.ZERO);
				responseDto.setSgstAsIgst((arr[16] != null
						? new BigDecimal(String.valueOf(arr[16]))
						: BigDecimal.ZERO));
				responseDto.setCgstAsCgst(arr[17] != null
						? new BigDecimal(String.valueOf(arr[17]))
						: BigDecimal.ZERO);
				responseDto.setCgstAsIgst(arr[18] != null
						? new BigDecimal(String.valueOf(arr[18]))
						: BigDecimal.ZERO);
				responseDto.setCessAmount(arr[19] != null
						? new BigDecimal(String.valueOf(arr[19]))
						: BigDecimal.ZERO);
				responseDto.setDocKey(
						arr[20] != null ? String.valueOf(arr[20]) : null);
				responseDto.setFileId(arr[21] != null
						? new Long(String.valueOf(arr[21])) : null);
				responseDto.setAsEnterTableId(arr[22] != null
						? new Long(String.valueOf(arr[22])) : null);
				responseDto.setOrigCrNoteNumber(
						arr[23] != null ? String.valueOf(arr[23]) : null);
				responseDto.setOrigCrNoteDate(
						arr[24] != null ? (LocalDate) arr[24] : null);
responseDto.setOriginalRecipeintGstin(arr[25] != null ? String.valueOf(arr[25]) : null);
				
				responseDto.setOriginalStatecode(arr[26] != null ? String.valueOf(arr[26]) : null);
				responseList.add(responseDto);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error while proccessing getGstr6DistributedSummaryList2 :: {}",
					ex.getMessage());
		}
		return size;

	}

}
