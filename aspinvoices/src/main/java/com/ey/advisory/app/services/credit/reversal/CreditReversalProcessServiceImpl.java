package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr3bRatioUserInputRepository;
import com.ey.advisory.app.data.entities.client.Gstr3bItcStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspComputeRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bItcStatusRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspComputeEntity;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

/**
 * 
 * @author Umesha.M
 *
 */
@Service("CreditReversalProcessServiceImpl")
public class CreditReversalProcessServiceImpl
		implements CreditReversalProcessService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreditReversalProcessServiceImpl.class);

	@Autowired
	@Qualifier(value = "CreditReversalProcessDaoImpl")
	private CreditReversalProcessDaoImpl daoImpl;

	@Autowired
	@Qualifier("Gstr3bItcStatusRepository")
	private Gstr3bItcStatusRepository gstr3bItcStatusRepository;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinDetailRepository;

	@Autowired
	@Qualifier("Gstr3BGstinAspComputeRepository")
	private Gstr3BGstinAspComputeRepository repository;
	
	@Autowired
	private Gstr3bRatioUserInputRepository gstr3bRatioUserInputRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	private static final String DATE_TIME_FORMATE = "yyyy-MM-dd hh:mm:ss";
	private static final String ITC_REVERSAL_RATIO = "ITC Reversal Ratio";
	private static final String SECTION_B2CS = "B2CS = 0";
	private static final String SECTION_NON_GST_SCH3 = "Non GST /SCH3";
	private static final String SECTION_OUTWARD_REVERSE_CHARGE_16 = "Outward Reverse Charge Supplies (16)";
	private static final String SECTION_NON_GST_9 = "NON GST (9)";
	private static final String SECTION_TTL_RCS = "TTL_RCS";
	private static final String EXPLANATION_TO_RULE_42_10 = "Explanation to Rule 42 (10)";
	private static final String TOTAL_GSTIN_SUPPLIES_11_9 = "Total Gstin supplies (11-9)";
	private static final String TOTAL_GSTIN_SUPPLIES_11 = "Total Gstin supplies (11)";
	private static final String NIL_RATED_B2CS_VERTICLE_UPLOAD = "Nil Rated - B2CS vertical Upload";
	private static final String DTA_SEZ_DTA = "DTA ( SEZ to DTA )";
	private static final String EXEMPT = "Exempt";
	private static final String EXEMPT7 = "Exempt (7)";
	private static final String NIL_RATED8 = "Nil Rated (8)";
	private static final String NIL_RATED_B2CS_VERTICLE_UPLOAD_8A = "Nil Rated - B2CS vertical Upload (8A)";
	private static final String TOTAL_GSTIN_SUPPLIES2 = "TOTAL_GSTIN_SUPPLIES";
	private static final String DEEMED_EXPORTS = "Deemed Exports";
	private static final String TAXABLE_NORMAL = "Taxable (Normal)";
	private static final String RATIO311 = "Ratio 3 (%) (7+8+8A+9-10+16) / 11";
	private static final String NET_ELIGIGLE_COMMON = "Net Eligible Common Credit (E = C-D) denoted as C2";
	private static final String TOTAL_TAX_AMOUNT = "Total Tax Amount (As per PR for Input Services and Input Goods)";
	private static final String TOTAL_COMMON_CREDIT = "Total Common Credit (C = A+B)";
	private static final String T1_USED_EXECLUSIVELY = "T1: Used exclusively for the purposes other than business";
	private static final String T2_USED_EXECLUSIVELY = "T2: Used exclusively for effecting exempt supplies";
	private static final String T3_CREDIT_IS_NOT = "T3: Credit is not available under sub-section (5) of section 17";
	private static final String T4_USED_EXCLUSIVE = "T4: Used exclusively for effecting supplies other than exempted but including zero rated";
	private static final String RATIO1_REVERSAL_AMOUNT = "Ratio 1: Reversal Amount as per Rule 42 =D1 (C2 * Ratio 1)";
	private static final String RATIO2_REVERSAL_AMOUNT = "Ratio 2: Reversal Amount as per Rule 42= D1 (C2 * Ratio 2)";
	private static final String RATIO3_REVERSAL_AMOUNT = "Ratio 3: Reversal Amount as per Rule 42= D1 (C2 * Ratio 3)";
	private static final String RATIO1_PERC = "Ratio 1 (%) (7+8+8A-10+16) / (11-9)";
	private static final String RATIO2_PERC = "Ratio 2 (%) (7+8+8A+9-10+16+17) / 11";
	private static final String COMMON_CREDIT_A = "Common Credit for Input Goods (IG) - A";
	private static final String COMMON_CREDIT_B = "Common Credit for Input Services(IS) - B";
	private static final String LESS_INELIGIBLE_CREDIT = "Less: Ineligible credit based on ITC Reversal Identifier - D";
	private static final String CREDIT_USED_PARTLY = "Credit used partly for business and partly for non-business,Denoted as D2 (C2*5%)";
	private static final String TAXABLE_NORMAL_REVERSE_CHARGE = "Taxable (Normal)- Reverse Charge";
	private static final String RATIO1 = "Ratio 1 = 0.00 %";
	private static final String RATIO2 = "Ratio 2 = 0.00 %";
	private static final String RATIO3 = "Ratio 3 = 0.00 %";
	private static final String EXT = "EXT";
	private static final String NIL = "NIL";
	private static final String SEZ_DTA = "SEZ ( DTA to SEZ )";

	private static final String TOTAL_REVERSAL_AS_PER_RATIO1 = "Total Reversal As Per Ratio 1 (D1 + D2)";
	private static final String TOTAL_REVERSAL_AS_PER_RATIO2 = "Total Reversal As Per Ratio 2 (D1 + D2)";
	private static final String TOTAL_REVERSAL_AS_PER_RATIO3 = "Total Reversal As Per Ratio 3 (D1 + D2)";
	private static final String ZERO = "0.0 %";

	private static final String NIL_RATED = "Nil Rated";
	private static final String EXPWT = "EXPWT";
	private static final String GRAND_TOTAL_FOR_CONTROLS = "Grand Total for Controls";
	private static final String TOTAL_GSTIN_SUPPLIES = "Total GSTIN Supplies (A+B) - 10";
	private static final String NON_GST_SCHEDULE = "Non GST /Schedule III";
	private static final String NSY_SEC_17 = "NSY- Sec 17(5) (17)";

	private static final String ACTIVE = "Active";
	private static final String INACTIVE = "Inactive";

	public String proceCallForComputeReversal(
			final Annexure1SummaryReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessServiceImpl proceCallForComputeReversal start");
		}
		List<String> gstinList = getDataSecurity(reqDto);
		String msg = null;
		if (gstinList != null && !gstinList.isEmpty()) {
			for (String gstinvalue : gstinList) {
				int derivedRetPer = GenUtil
						.convertTaxPeriodToInt(reqDto.getTaxPeriod());
				gstr3bItcStatusRepository.gstr3bItcInActiveUpdate(gstinvalue,
						derivedRetPer);
				Gstr3bItcStatusEntity entity = new Gstr3bItcStatusEntity();
				entity.setStatus("INITIATED");
				entity.setGstin(gstinvalue);
				entity.setDeriverdRetPeriod(derivedRetPer);
				entity.setCreatedOn(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				User user = SecurityContext.getUser();
				entity.setCreatedBy(user.getUserPrincipalName());
				gstr3bItcStatusRepository.save(entity);

				int count = daoImpl.proceCallForComputeReversal(gstinvalue,
						derivedRetPer);
				if (count > 0) {
					msg = "Updated Successfully";
				} else {
					msg = "Invalid Gstin";
				}
			}
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessServiceImpl proceCallForComputeReversal end ");
		}
		return msg;
	}

	private List<String> getDataSecurity(final Annexure1SummaryReqDto reqDto) {
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
						&& dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}
		return gstinList;
	}

	public List<CreditReversalProcessDto> getCredRevrProcess(
			final Annexure1SummaryReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessServiceImpl getCredRevrProcess begin");
		}
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		String taxPeriod = reqDto.getTaxPeriod();
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
						&& dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}
		int deriverdRetPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);
		List<CreditReversalProcessDto> processDtos = convertObToCreditReversal(
				deriverdRetPeriod, gstinList);

		List<CreditReversalProcessDto> creditReversalProcessDtos = new ArrayList<>();
		if (processDtos != null && !processDtos.isEmpty()) {
			Map<String, List<CreditReversalProcessDto>> mapCreditReversal = mapCreditReversalProcess(
					processDtos);
			gstinList.forEach(gstin -> {
				List<CreditReversalProcessDto> reversalProcessDtos = mapCreditReversal
						.get(gstin);
				if (reversalProcessDtos != null
						&& !reversalProcessDtos.isEmpty()) {
					reversalProcessDtos.forEach(reversalProcessDto -> {
						Gstr3bItcStatusEntity entity = gstr3bItcStatusRepository
								.get3btcStatus(reversalProcessDto.getGstin(),
										deriverdRetPeriod);
						String dateTime = null;
						String gstintoken = defaultGSTNAuthTokenService
								.getAuthTokenStatusForGstin(
										reversalProcessDto.getGstin());
						if (gstintoken != null) {
							if ("A".equalsIgnoreCase(gstintoken)) {
								reversalProcessDto.setAuthToken(ACTIVE);
							} else {
								reversalProcessDto.setAuthToken(INACTIVE);
							}
						} else {
							reversalProcessDto.setAuthToken(INACTIVE);
						}
						if (entity != null) {
							reversalProcessDto.setStatus(entity.getStatus());
							LocalDateTime createdOn = EYDateUtil
									.toISTDateTimeFromUTC(
											entity.getCreatedOn());
							reversalProcessDto.setDateTime(EYDateUtil.fmtDate3(createdOn));
						}
						String stateCode = gstin.substring(0, 2);
						// Getting State Name based on state code
						String stateName = statecodeRepository
								.findStateNameByCode(stateCode);
						reversalProcessDto.setState(stateName);
						GSTNDetailEntity gstnEntity = gstinDetailRepository
								.findRegDates(gstin);
						reversalProcessDto
								.setRegType(gstnEntity.getRegistrationType());
						creditReversalProcessDtos.add(reversalProcessDto);
					});
				} else {
					CreditReversalProcessDto processDto = new CreditReversalProcessDto();
					processDto.setGstin(gstin);
					String stateCode = gstin.substring(0, 2);

					// Getting State Name based on state code
					String stateName = statecodeRepository
							.findStateNameByCode(stateCode);
					processDto.setState(stateName);
					GSTNDetailEntity gstnEntity = gstinDetailRepository
							.findRegDates(gstin);
					processDto.setRegType(gstnEntity.getRegistrationType());
					Gstr3bItcStatusEntity entity = gstr3bItcStatusRepository
							.get3btcStatus(gstin, deriverdRetPeriod);
					String gstintoken = defaultGSTNAuthTokenService
							.getAuthTokenStatusForGstin(processDto.getGstin());
					if (gstintoken != null) {
						if ("A".equalsIgnoreCase(gstintoken)) {
							processDto.setAuthToken(ACTIVE);
						} else {
							processDto.setAuthToken(INACTIVE);
						}
					} else {
						processDto.setAuthToken(INACTIVE);
					}
					if (entity != null) {
						processDto.setStatus(entity.getStatus());
						LocalDateTime createdOn = EYDateUtil
								.toISTDateTimeFromUTC(entity.getCreatedOn());
						processDto.setDateTime(EYDateUtil.fmtDate3(createdOn));
					}
					creditReversalProcessDtos.add(processDto);
				}
			});

		} else {
			gstinList.forEach(gstinValue -> {
				CreditReversalProcessDto processDto = new CreditReversalProcessDto();
				processDto.setGstin(gstinValue);
				String stateCode = gstinValue.substring(0, 2);

				// Getting State Name based on state code
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				processDto.setState(stateName);
				GSTNDetailEntity gstnEntity = gstinDetailRepository
						.findRegDates(gstinValue);
				processDto.setRegType(gstnEntity.getRegistrationType());
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(processDto.getGstin());
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						processDto.setAuthToken(ACTIVE);
					} else {
						processDto.setAuthToken(INACTIVE);
					}
				} else {
					processDto.setAuthToken(INACTIVE);
				}
				Gstr3bItcStatusEntity entity = gstr3bItcStatusRepository
						.get3btcStatus(gstinValue, deriverdRetPeriod);

				if (entity != null) {
					processDto.setStatus(entity.getStatus());
					LocalDateTime createdOn = EYDateUtil
							.toISTDateTimeFromUTC(entity.getCreatedOn());
					processDto.setDateTime(EYDateUtil.fmtDate3(createdOn));
				}
				creditReversalProcessDtos.add(processDto);
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessServiceImpl getCredRevrProcess end");
		}
		creditReversalProcessDtos
				.sort(Comparator.comparing(CreditReversalProcessDto::getGstin));
		return creditReversalProcessDtos;
	}

	private List<CreditReversalProcessDto> convertObToCreditReversal(
			int derivedRetPer, List<String> gstinList) {
		List<CreditReversalProcessDto> processDtos = new ArrayList<>();

		List<Object[]> objs = daoImpl.getCreditReversalProcess(gstinList,
				derivedRetPer);
		for (Object[] obj : objs) {
			CreditReversalProcessDto processDto = new CreditReversalProcessDto();
			processDto.setGstin(obj[0] != null ? String.valueOf(obj[0]) : null);
			processDto.setRatio1Ratio(obj[1] != null
					? new BigDecimal(String.valueOf(obj[1])) : BigDecimal.ZERO);

			processDto.setRatio1Igst(obj[2] != null
					? new BigDecimal(String.valueOf(obj[2])) : BigDecimal.ZERO);

			processDto.setRatio1Cgst(obj[3] != null
					? new BigDecimal(String.valueOf(obj[3])) : BigDecimal.ZERO);

			processDto.setRatio1Sgst(obj[4] != null
					? new BigDecimal(String.valueOf(obj[4])) : BigDecimal.ZERO);

			processDto.setRatio1Cess(obj[5] != null
					? new BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);

			processDto.setRatio2Ratio(obj[6] != null
					? new BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);

			processDto.setRatio2Igst(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);

			processDto.setRatio2Cgst(obj[8] != null
					? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);

			processDto.setRatio2Sgst(obj[9] != null
					? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);

			processDto.setRatio2Cess(
					obj[10] != null ? new BigDecimal(String.valueOf(obj[10]))
							: BigDecimal.ZERO);

			processDto.setRatio3Ratio(
					obj[11] != null ? new BigDecimal(String.valueOf(obj[11]))
							: BigDecimal.ZERO);

			processDto.setRatio3Igst(
					obj[12] != null ? new BigDecimal(String.valueOf(obj[12]))
							: BigDecimal.ZERO);
			processDto.setRatio3Cgst(
					obj[13] != null ? new BigDecimal(String.valueOf(obj[13]))
							: BigDecimal.ZERO);
			processDto.setRatio3Sgst(
					obj[14] != null ? new BigDecimal(String.valueOf(obj[14]))
							: BigDecimal.ZERO);

			processDto.setRatio3Cess(
					obj[15] != null ? new BigDecimal(String.valueOf(obj[15]))
							: BigDecimal.ZERO);
			processDto.setRatio1TotalTax(
					obj[16] != null ? new BigDecimal(String.valueOf(obj[16]))
							: BigDecimal.ZERO);
			processDto.setRatio2TotalTax(
					obj[17] != null ? new BigDecimal(String.valueOf(obj[17]))
							: BigDecimal.ZERO);
			processDto.setRatio3TotalTax(
					obj[18] != null ? new BigDecimal(String.valueOf(obj[18]))
							: BigDecimal.ZERO);
			processDtos.add(processDto);
		}
		return processDtos;
	}

	private Map<String, List<CreditReversalProcessDto>> mapCreditReversalProcess(
			List<CreditReversalProcessDto> processDtos) {
		Map<String, List<CreditReversalProcessDto>> mapProcessDto = new HashMap<>();
		processDtos.forEach(processDto -> {
			StringBuilder key = new StringBuilder();
			key.append(processDto.getGstin());
			String docKey = key.toString();
			if (mapProcessDto.containsKey(docKey)) {
				List<CreditReversalProcessDto> creditReversalProcessDtos = mapProcessDto
						.get(docKey);
				mapProcessDto.put(docKey, creditReversalProcessDtos);
			} else {
				List<CreditReversalProcessDto> creditReversalProcessDtos = new ArrayList<>();
				creditReversalProcessDtos.add(processDto);
				mapProcessDto.put(docKey, creditReversalProcessDtos);
			}
		});

		return mapProcessDto;

	}

	public List<CreditReversalDto> getCredReversal(
			final Annexure1SummaryReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessServiceImpl getCredReversal begin");
		}
		List<CreditReversalDto> processDtoList = new ArrayList<>();

		List<CreditReversalItemDto> convertObj = convertObjToCredRevrProcess(
				reqDto);
		if (convertObj != null && !convertObj.isEmpty()) {
			Map<String, List<CreditReversalItemDto>> mapCreditRevers = mapCreditReversProcess(
					convertObj);

			totalTaxAmount(mapCreditRevers, processDtoList);
			commonCreditForInputGoodsA(mapCreditRevers, processDtoList);
			commonCreditForInputServicesB(mapCreditRevers, processDtoList);
			commonCreditC(mapCreditRevers, processDtoList);
			inEligibleCreditOnITCReversalD(mapCreditRevers, processDtoList);
			netElgCommonCreditE(mapCreditRevers, processDtoList);
			itcReversalRatio(mapCreditRevers, processDtoList);
			ratio1ReversalAmountRule42(mapCreditRevers, processDtoList);
			ratio2ReversalAmountRule42(mapCreditRevers, processDtoList);
			ratio3ReversalAmountRule42(mapCreditRevers, processDtoList);
			creditUsedPartlyforBusiness(mapCreditRevers, processDtoList);
			totalReversalRatio1(mapCreditRevers, processDtoList);
			totalReversalRatio2(mapCreditRevers, processDtoList);
			totalReversalRatio3(mapCreditRevers, processDtoList);

		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(TOTAL_TAX_AMOUNT);
			processDtoList.add(processDto);

			CreditReversalDto creditAProcessDto = new CreditReversalDto();
			creditAProcessDto.setParticulars(COMMON_CREDIT_A);
			processDtoList.add(creditAProcessDto);

			CreditReversalDto credAProcessDto = new CreditReversalDto();
			credAProcessDto.setParticulars(COMMON_CREDIT_B);
			processDtoList.add(credAProcessDto);

			CreditReversalDto commCredtProcessDto = new CreditReversalDto();
			commCredtProcessDto.setParticulars(TOTAL_COMMON_CREDIT);
			processDtoList.add(commCredtProcessDto);

			CreditReversalDto useExecluProcessDto = new CreditReversalDto();
			List<CreditReversalItemDto> itemDtos = new ArrayList<>();

			CreditReversalItemDto itemDto = new CreditReversalItemDto();
			itemDto.setParticulars(T1_USED_EXECLUSIVELY);
			itemDtos.add(itemDto);

			CreditReversalItemDto t2UserItemDto = new CreditReversalItemDto();
			t2UserItemDto.setParticulars(T2_USED_EXECLUSIVELY);
			itemDtos.add(t2UserItemDto);

			CreditReversalItemDto t3ItemDto = new CreditReversalItemDto();
			t3ItemDto.setParticulars(T3_CREDIT_IS_NOT);
			itemDtos.add(t3ItemDto);

			CreditReversalItemDto t4ItemDto = new CreditReversalItemDto();
			t4ItemDto.setParticulars(T4_USED_EXCLUSIVE);
			itemDtos.add(t4ItemDto);

			useExecluProcessDto.setItemDtos(itemDtos);
			useExecluProcessDto.setParticulars(LESS_INELIGIBLE_CREDIT);
			processDtoList.add(useExecluProcessDto);

			CreditReversalDto nerEligProcessDto = new CreditReversalDto();
			nerEligProcessDto.setParticulars(NET_ELIGIGLE_COMMON);
			processDtoList.add(nerEligProcessDto);

			CreditReversalDto ratio1ProcessDto = new CreditReversalDto();
			ratio1ProcessDto.setParticulars(RATIO1);
			processDtoList.add(ratio1ProcessDto);
			CreditReversalDto ratio2ProcessDto = new CreditReversalDto();
			ratio2ProcessDto.setParticulars(RATIO2);
			processDtoList.add(ratio2ProcessDto);
			CreditReversalDto ratio3ProcessDto = new CreditReversalDto();
			ratio3ProcessDto.setParticulars(RATIO3);
			processDtoList.add(ratio3ProcessDto);

			CreditReversalDto ratiRevProcessDto = new CreditReversalDto();
			ratiRevProcessDto.setParticulars(RATIO1_REVERSAL_AMOUNT);
			processDtoList.add(ratiRevProcessDto);

			CreditReversalDto ratiRev2ProcessDto = new CreditReversalDto();
			ratiRev2ProcessDto.setParticulars(RATIO2_REVERSAL_AMOUNT);
			processDtoList.add(ratiRev2ProcessDto);

			CreditReversalDto ratiRev3ProcessDto = new CreditReversalDto();
			ratiRev3ProcessDto.setParticulars(RATIO3_REVERSAL_AMOUNT);
			processDtoList.add(ratiRev3ProcessDto);

			CreditReversalDto credUsedPartlyProcessDto = new CreditReversalDto();
			credUsedPartlyProcessDto.setParticulars(CREDIT_USED_PARTLY);
			processDtoList.add(credUsedPartlyProcessDto);

			CreditReversalDto totalRevAsPerRatio1 = new CreditReversalDto();
			totalRevAsPerRatio1.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO1);
			processDtoList.add(totalRevAsPerRatio1);

			CreditReversalDto totalRevAsPerRatio2 = new CreditReversalDto();
			totalRevAsPerRatio2.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO2);
			processDtoList.add(totalRevAsPerRatio2);

			CreditReversalDto totalRevAsPerRatio3 = new CreditReversalDto();
			totalRevAsPerRatio3.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO3);
			processDtoList.add(totalRevAsPerRatio3);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessServiceImpl getCredReversal end");
		}
		return processDtoList;
	}

	private void itcReversalRatio(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get(ITC_REVERSAL_RATIO);
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto1 = new CreditReversalDto();
				processDto1.setParticulars("Ratio 1 = "
						+ itcReverRatioItemDto.getRatio1().longValue() + " %");
				processDtoList.add(processDto1);
				CreditReversalDto processDto2 = new CreditReversalDto();
				processDto2.setParticulars("Ratio 2 = "
						+ itcReverRatioItemDto.getRatio2().longValue() + " %");
				processDtoList.add(processDto2);
				CreditReversalDto processDto3 = new CreditReversalDto();
				processDto3.setParticulars("Ratio 3 = "
						+ itcReverRatioItemDto.getRatio3().longValue() + " %");
				processDtoList.add(processDto3);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(RATIO1);
			processDtoList.add(processDto);
			CreditReversalDto processDto2 = new CreditReversalDto();
			processDto2.setParticulars(RATIO2);
			processDtoList.add(processDto2);
			CreditReversalDto processDto3 = new CreditReversalDto();
			processDto3.setParticulars(RATIO3);
			processDtoList.add(processDto3);
		}
	}

	private void totalTaxAmount(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Total Tax Amount");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(TOTAL_TAX_AMOUNT);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(TOTAL_TAX_AMOUNT);
			processDtoList.add(processDto);
		}
	}

	private void commonCreditForInputGoodsA(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("A");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(COMMON_CREDIT_A);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(COMMON_CREDIT_A);
			processDtoList.add(processDto);
		}
	}

	private void commonCreditForInputServicesB(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("B");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(COMMON_CREDIT_B);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(COMMON_CREDIT_B);
			processDtoList.add(processDto);
		}
	}

	private void commonCreditC(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("C");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(TOTAL_COMMON_CREDIT);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(TOTAL_COMMON_CREDIT);
			processDtoList.add(processDto);
		}
	}

	private void inEligibleCreditOnITCReversalD(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {

		CreditReversalDto processDto = new CreditReversalDto();

		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cess = BigDecimal.ZERO;
		BigDecimal totalTax = BigDecimal.ZERO;

		List<CreditReversalItemDto> itemDtos = new ArrayList<>();

		List<CreditReversalItemDto> itcReversalDT1Dtos = mapCreditRevers
				.get("D_T1");
		if (itcReversalDT1Dtos != null && !itcReversalDT1Dtos.isEmpty()) {
			for (CreditReversalItemDto itcReversalDT1Dto : itcReversalDT1Dtos) {
				CreditReversalItemDto itemDto = new CreditReversalItemDto();
				itemDto.setParticulars(T1_USED_EXECLUSIVELY);
				itemDto.setIgst(itcReversalDT1Dto.getIgst());
				itemDto.setCgst(itcReversalDT1Dto.getCgst());
				itemDto.setSgst(itcReversalDT1Dto.getSgst());
				itemDto.setCess(itcReversalDT1Dto.getCess());
				itemDto.setTotalTax(itcReversalDT1Dto.getTotalTax());
				itemDtos.add(itemDto);

				igst = igst.add(itcReversalDT1Dto.getIgst() != null
						? itcReversalDT1Dto.getIgst() : BigDecimal.ZERO);
				cgst = cgst.add(itcReversalDT1Dto.getCgst() != null
						? itcReversalDT1Dto.getCgst() : BigDecimal.ZERO);
				sgst = sgst.add(itcReversalDT1Dto.getSgst() != null
						? itcReversalDT1Dto.getSgst() : BigDecimal.ZERO);
				cess = cess.add(itcReversalDT1Dto.getCess() != null
						? itcReversalDT1Dto.getCess() : BigDecimal.ZERO);
				totalTax = totalTax.add(itcReversalDT1Dto.getTotalTax() != null
						? itcReversalDT1Dto.getTotalTax() : BigDecimal.ZERO);
			}
		} else {
			CreditReversalItemDto itemDto = new CreditReversalItemDto();
			itemDto.setParticulars(T1_USED_EXECLUSIVELY);
			itemDtos.add(itemDto);
		}

		List<CreditReversalItemDto> itcReversalDT2Dtos = mapCreditRevers
				.get("D_T2");
		if (itcReversalDT2Dtos != null && !itcReversalDT2Dtos.isEmpty()) {
			for (CreditReversalItemDto itcReversalDT2Dto : itcReversalDT2Dtos) {
				CreditReversalItemDto itemDto = new CreditReversalItemDto();
				itemDto.setParticulars(T2_USED_EXECLUSIVELY);
				itemDto.setIgst(itcReversalDT2Dto.getIgst());
				itemDto.setCgst(itcReversalDT2Dto.getCgst());
				itemDto.setSgst(itcReversalDT2Dto.getSgst());
				itemDto.setCess(itcReversalDT2Dto.getCess());
				itemDto.setTotalTax(itcReversalDT2Dto.getTotalTax());
				itemDtos.add(itemDto);

				igst = igst.add(itcReversalDT2Dto.getIgst() != null
						? itcReversalDT2Dto.getIgst() : BigDecimal.ZERO);
				cgst = cgst.add(itcReversalDT2Dto.getCgst() != null
						? itcReversalDT2Dto.getCgst() : BigDecimal.ZERO);
				sgst = sgst.add(itcReversalDT2Dto.getSgst() != null
						? itcReversalDT2Dto.getSgst() : BigDecimal.ZERO);
				cess = cess.add(itcReversalDT2Dto.getCess() != null
						? itcReversalDT2Dto.getCess() : BigDecimal.ZERO);
				totalTax = totalTax.add(itcReversalDT2Dto.getTotalTax() != null
						? itcReversalDT2Dto.getTotalTax() : BigDecimal.ZERO);
			}
		} else {
			CreditReversalItemDto itemDto = new CreditReversalItemDto();
			itemDto.setParticulars(T2_USED_EXECLUSIVELY);
			itemDtos.add(itemDto);
		}

		List<CreditReversalItemDto> itcReversalDT3Dtos = mapCreditRevers
				.get("D_T3");
		if (itcReversalDT3Dtos != null) {
			for (CreditReversalItemDto itcReversalDT3Dto : itcReversalDT3Dtos) {
				CreditReversalItemDto itemDto = new CreditReversalItemDto();
				itemDto.setParticulars(T3_CREDIT_IS_NOT);
				itemDto.setIgst(itcReversalDT3Dto.getIgst());
				itemDto.setCgst(itcReversalDT3Dto.getCgst());
				itemDto.setSgst(itcReversalDT3Dto.getSgst());
				itemDto.setCess(itcReversalDT3Dto.getCess());
				itemDto.setTotalTax(itcReversalDT3Dto.getTotalTax());
				itemDtos.add(itemDto);

				igst = igst.add(itcReversalDT3Dto.getIgst() != null
						? itcReversalDT3Dto.getIgst() : BigDecimal.ZERO);
				cgst = cgst.add(itcReversalDT3Dto.getCgst() != null
						? itcReversalDT3Dto.getCgst() : BigDecimal.ZERO);
				sgst = sgst.add(itcReversalDT3Dto.getSgst() != null
						? itcReversalDT3Dto.getSgst() : BigDecimal.ZERO);
				cess = cess.add(itcReversalDT3Dto.getCess() != null
						? itcReversalDT3Dto.getCess() : BigDecimal.ZERO);
				totalTax = totalTax.add(itcReversalDT3Dto.getTotalTax() != null
						? itcReversalDT3Dto.getTotalTax() : BigDecimal.ZERO);
			}
		} else {
			CreditReversalItemDto itemDto = new CreditReversalItemDto();
			itemDto.setParticulars(T3_CREDIT_IS_NOT);
			itemDtos.add(itemDto);
		}

		List<CreditReversalItemDto> itcReversalDT4Dtos = mapCreditRevers
				.get("D_T4");
		if (itcReversalDT4Dtos != null) {
			for (CreditReversalItemDto itcReversalDT4Dto : itcReversalDT4Dtos) {
				CreditReversalItemDto itemDto = new CreditReversalItemDto();
				itemDto.setParticulars(T4_USED_EXCLUSIVE);
				itemDto.setIgst(itcReversalDT4Dto.getIgst());
				itemDto.setCgst(itcReversalDT4Dto.getCgst());
				itemDto.setSgst(itcReversalDT4Dto.getSgst());
				itemDto.setCess(itcReversalDT4Dto.getCess());
				itemDto.setTotalTax(itcReversalDT4Dto.getTotalTax());
				itemDtos.add(itemDto);

				igst = igst.add(itcReversalDT4Dto.getIgst() != null
						? itcReversalDT4Dto.getIgst() : BigDecimal.ZERO);
				cgst = cgst.add(itcReversalDT4Dto.getCgst() != null
						? itcReversalDT4Dto.getCgst() : BigDecimal.ZERO);
				sgst = sgst.add(itcReversalDT4Dto.getSgst() != null
						? itcReversalDT4Dto.getSgst() : BigDecimal.ZERO);
				cess = cess.add(itcReversalDT4Dto.getCess() != null
						? itcReversalDT4Dto.getCess() : BigDecimal.ZERO);
				totalTax = totalTax.add(itcReversalDT4Dto.getTotalTax() != null
						? itcReversalDT4Dto.getTotalTax() : BigDecimal.ZERO);
			}
		} else {
			CreditReversalItemDto itemDto = new CreditReversalItemDto();
			itemDto.setParticulars(T4_USED_EXCLUSIVE);
			itemDtos.add(itemDto);
		}
		processDto.setItemDtos(itemDtos);
		processDto.setParticulars(LESS_INELIGIBLE_CREDIT);
		processDto.setIgst(igst);
		processDto.setCgst(cgst);
		processDto.setSgst(sgst);
		processDto.setCess(cess);
		processDto.setTotalTax(totalTax);

		processDtoList.add(processDto);
	}

	private void netElgCommonCreditE(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("E");
		if (itcReverRatioItemDtos != null) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(NET_ELIGIGLE_COMMON);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(NET_ELIGIGLE_COMMON);
			processDtoList.add(processDto);
		}
	}

	private void ratio1ReversalAmountRule42(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Ratio_1_R42");
		if (itcReverRatioItemDtos != null) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(RATIO1_REVERSAL_AMOUNT);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(RATIO1_REVERSAL_AMOUNT);
			processDtoList.add(processDto);
		}
	}

	private void ratio2ReversalAmountRule42(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Ratio_2_R42");
		if (itcReverRatioItemDtos != null) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(RATIO2_REVERSAL_AMOUNT);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(RATIO2_REVERSAL_AMOUNT);
			processDtoList.add(processDto);
		}
	}

	private void ratio3ReversalAmountRule42(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Ratio_3_R42");
		if (itcReverRatioItemDtos != null) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(RATIO3_REVERSAL_AMOUNT);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(RATIO3_REVERSAL_AMOUNT);
			processDtoList.add(processDto);
		}
	}

	private void totalReversalRatio1(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Total Reversal_RATIO_1");
		if (itcReverRatioItemDtos != null) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO1);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO1);
			processDtoList.add(processDto);
		}
	}

	private void totalReversalRatio2(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Total Reversal_RATIO_2");
		if (itcReverRatioItemDtos != null) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO2);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO2);
			processDtoList.add(processDto);
		}
	}

	private void totalReversalRatio3(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Total Reversal_RATIO_3");
		if (itcReverRatioItemDtos != null) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO3);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO3);
			processDtoList.add(processDto);
		}
	}

	private void creditUsedPartlyforBusiness(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("D2");
		if (itcReverRatioItemDtos != null) {
			itcReverRatioItemDtos.forEach(itcReverRatioItemDto -> {
				CreditReversalDto processDto = new CreditReversalDto();
				processDto.setParticulars(CREDIT_USED_PARTLY);
				processDto.setIgst(itcReverRatioItemDto.getIgst());
				processDto.setCgst(itcReverRatioItemDto.getCgst());
				processDto.setSgst(itcReverRatioItemDto.getSgst());
				processDto.setCess(itcReverRatioItemDto.getCess());
				processDto.setTotalTax(itcReverRatioItemDto.getTotalTax());
				processDtoList.add(processDto);
			});
		} else {
			CreditReversalDto processDto = new CreditReversalDto();
			processDto.setParticulars(CREDIT_USED_PARTLY);
			processDtoList.add(processDto);
		}
	}

	private Map<String, List<CreditReversalItemDto>> mapCreditReversProcess(
			List<CreditReversalItemDto> itemDtos) {
		Map<String, List<CreditReversalItemDto>> mapCredRevTurnItemDto = new HashMap<>();

		itemDtos.forEach(itemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(itemDto.getParticulars());
			String docKey = key.toString();
			if (mapCredRevTurnItemDto.containsKey(docKey)) {
				List<CreditReversalItemDto> turnOverItems = mapCredRevTurnItemDto
						.get(docKey);
				turnOverItems.add(itemDto);
				mapCredRevTurnItemDto.put(docKey, turnOverItems);

			} else {
				List<CreditReversalItemDto> turnOverItems = new ArrayList<>();
				turnOverItems.add(itemDto);
				mapCredRevTurnItemDto.put(docKey, turnOverItems);
			}
		});
		return mapCredRevTurnItemDto;
	}

	private List<CreditReversalItemDto> convertObjToCredRevrProcess(
			final Annexure1SummaryReqDto reqDto) {
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();

		String gstin = setGstin(dataSecAttrs);

		List<CreditReversalItemDto> processItemDtos = new ArrayList<>();

		int derivedRetPer = GenUtil
				.convertTaxPeriodToInt(reqDto.getTaxPeriod());
		List<Object[]> reversalObjs = daoImpl
				.getCreditReversalAndTurnvolProcess("reversel", gstin,
						derivedRetPer);
		for (Object[] obj : reversalObjs) {
			CreditReversalItemDto processDto = new CreditReversalItemDto();
			processDto.setParticulars(
					obj[0] != null ? String.valueOf(obj[0]) : null);
			processDto.setTotalTax(obj[1] != null
					? new BigDecimal(String.valueOf(obj[1])) : BigDecimal.ZERO);
			processDto.setIgst(obj[2] != null
					? new BigDecimal(String.valueOf(obj[2])) : BigDecimal.ZERO);
			processDto.setCgst(obj[3] != null
					? new BigDecimal(String.valueOf(obj[3])) : BigDecimal.ZERO);
			processDto.setSgst(obj[4] != null
					? new BigDecimal(String.valueOf(obj[4])) : BigDecimal.ZERO);
			processDto.setCess(obj[5] != null
					? new BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
			processDto.setRatio1(obj[6] != null
					? new BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);
			processDto.setRatio2(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
			processDto.setRatio3(obj[8] != null
					? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);

			processItemDtos.add(processDto);
		}
		return processItemDtos;
	}

	public List<CreditTurnOverDto> getCredTurnOverPartA(
			final Annexure1SummaryReqDto reqDto) {

		List<CreditTurnOverDto> processDtoList = new ArrayList<>();

		List<CreditTurnOverItemDto> convertObj = convertObjToCredTurnOver(
				reqDto);
		if (convertObj != null && !convertObj.isEmpty()) {
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers = mapCreditTurnOver(
					convertObj);
			taxableSuppliesTax(mapCreditRevers, processDtoList);
			exmptSupplies(mapCreditRevers, processDtoList);
			lessExpRule42(mapCreditRevers, processDtoList);
			totalGstinSupp10(mapCreditRevers, processDtoList);
			reverseChargeSupplies(mapCreditRevers, processDtoList);
			overOthersD(mapCreditRevers, processDtoList);
		} else {
			defultValuesForTaxSuppliesTax(processDtoList);
			defultValuesForExmptSupplies(processDtoList);
			defultValuesForLessExpRule42(processDtoList);
			defultValuesForTotalGstinSupp10(processDtoList);
			defultValuesForReverseChargeSupplies(processDtoList);
			defultValuesForOverOthersD(processDtoList);
		}
		return processDtoList;
	}

	private void defultValuesForTaxSuppliesTax(
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();
		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setSno("1");
		itemDto.setTurnoverComp(TAXABLE_NORMAL);
		itemDtos.add(itemDto);

		CreditTurnOverItemUiDto b2csVertUpladItemDto = new CreditTurnOverItemUiDto();
		b2csVertUpladItemDto.setSno("1A");
		b2csVertUpladItemDto
				.setTurnoverComp("Taxable (Normal) B2CS Vertical Upload");
		itemDtos.add(b2csVertUpladItemDto);

		CreditTurnOverItemUiDto sezDtaItemDto = new CreditTurnOverItemUiDto();
		sezDtaItemDto.setSno("2");
		sezDtaItemDto.setTurnoverComp(SEZ_DTA);
		itemDtos.add(sezDtaItemDto);

		CreditTurnOverItemUiDto dxpItemDto = new CreditTurnOverItemUiDto();
		dxpItemDto.setSno("3");
		dxpItemDto.setTurnoverComp("DXP");
		itemDtos.add(dxpItemDto);

		CreditTurnOverItemUiDto exptItemDto = new CreditTurnOverItemUiDto();
		exptItemDto.setSno("4");
		exptItemDto.setTurnoverComp("EXPT");
		itemDtos.add(exptItemDto);

		CreditTurnOverItemUiDto expwtItemDto = new CreditTurnOverItemUiDto();
		expwtItemDto.setSno("5");
		expwtItemDto.setTurnoverComp(EXPWT);
		itemDtos.add(expwtItemDto);

		CreditTurnOverItemUiDto dtaSezDtaItemDto = new CreditTurnOverItemUiDto();
		dtaSezDtaItemDto.setSno("6");
		dtaSezDtaItemDto.setTurnoverComp(DTA_SEZ_DTA);
		itemDtos.add(dtaSezDtaItemDto);
		processDto.setItems(itemDtos);

		processDtoList.add(processDto);
	}

	private void defultValuesForExmptSupplies(
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();

		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setSno("7");
		itemDto.setTurnoverComp(EXEMPT);
		itemDtos.add(itemDto);

		CreditTurnOverItemUiDto nilRatedItemDto = new CreditTurnOverItemUiDto();
		nilRatedItemDto.setSno("8");
		nilRatedItemDto.setTurnoverComp(NIL_RATED);
		itemDtos.add(nilRatedItemDto);

		CreditTurnOverItemUiDto nilRatedB2CItemDto = new CreditTurnOverItemUiDto();
		nilRatedB2CItemDto.setSno("8A");
		nilRatedB2CItemDto.setTurnoverComp(NIL_RATED_B2CS_VERTICLE_UPLOAD);
		itemDtos.add(nilRatedB2CItemDto);

		CreditTurnOverItemUiDto nonGstSceItemDto = new CreditTurnOverItemUiDto();
		nonGstSceItemDto.setSno("9");
		nonGstSceItemDto.setTurnoverComp(NON_GST_SCHEDULE);
		itemDtos.add(itemDto);

		processDto.setItems(itemDtos);
		processDto.setTurnoverComp("Exempt Supplies (B)");
		processDtoList.add(processDto);
	}

	public List<CreditTurnOverDto> getCredTurnOverPartB(
			final Annexure1SummaryReqDto reqDto) {

		List<CreditTurnOverDto> processDtoList = new ArrayList<>();
		
		//userInputValue
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		String gstin = setGstin(dataSecAttrs);
		String derivedRetPer = "0"; // Default to "0" if null
		if (reqDto.getTaxPeriod() != null) {
		    Integer taxPeriodInt = GenUtil.convertTaxPeriodToInt(reqDto.getTaxPeriod());
		    if (taxPeriodInt != null) {
		        String derivedStr = String.format("%06d", taxPeriodInt);
		        derivedRetPer = derivedStr.substring(4, 6) + derivedStr.substring(0, 4);
		    }
		}

		
		Object[] userInputRatio = gstr3bRatioUserInputRepository.findRatio1AndRatio2ByGstinAndTaxPeriod(gstin,
				String.valueOf(derivedRetPer));
		String ratio1 = "0"; // Default value
		String ratio2 = "0"; // Default value

		if (userInputRatio != null && userInputRatio.length > 0 
				&& userInputRatio[0] instanceof Object[]) {
			Object[] ratios = (Object[]) userInputRatio[0]; 

			ratio1 = (ratios.length > 0 && ratios[0] != null) ? String.valueOf(ratios[0]) : "0";
			ratio2 = (ratios.length > 1 && ratios[1] != null) ? String.valueOf(ratios[1]) : "0";
		}
		
		LOGGER.debug("UserInput Ratio values:{}",ratio1,ratio2);

		List<CreditTurnOverItemDto> convertObj = convertObjToCredTurnOver(
				reqDto);
		if (convertObj != null && !convertObj.isEmpty()) {
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers = mapCreditTurnOver(
					convertObj);
			itcReversalRatioForTurnOver(mapCreditRevers, processDtoList);
			ratio1ForTurnOver(mapCreditRevers, processDtoList, ratio1);
			ratio2ForTurnOver(mapCreditRevers, processDtoList, ratio2);
			ratio3ForTurnOver(mapCreditRevers, processDtoList);

		} else {
			CreditTurnOverDto processDto = new CreditTurnOverDto();
			processDto.setTurnoverComp(ITC_REVERSAL_RATIO);
			processDto.setDigigstAutoComp(null);
			processDtoList.add(processDto);

			defultValueForRatio1ForTurnOver(processDtoList,ratio1);
			defultValueForRatio2ForTurnOver(processDtoList,ratio2);
			defultValueForRatio3ForTurnOver(processDtoList);

		}

		return processDtoList;
	}

	private void taxableSuppliesTax(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		if (mapCreditRevers != null && !mapCreditRevers.isEmpty()) {
			List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();
			BigDecimal digigstAutoComp = BigDecimal.ZERO;
			List<CreditTurnOverItemDto> overTaxItemDtos = mapCreditRevers
					.get("TAX");
			if (overTaxItemDtos != null && !overTaxItemDtos.isEmpty()) {
				for (CreditTurnOverItemDto overTaxItemDto : overTaxItemDtos) {
					CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
					itemDto.setSno("1");
					itemDto.setTurnoverComp(TAXABLE_NORMAL);
					itemDto.setDigigstAutoComp(
							overTaxItemDto.getDigigstAutoComp());
					itemDtos.add(itemDto);
					digigstAutoComp = digigstAutoComp
							.add(overTaxItemDto.getDigigstAutoComp());
				}
			} else {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("1");
				itemDto.setTurnoverComp(TAXABLE_NORMAL);
				itemDtos.add(itemDto);
			}
			List<CreditTurnOverItemDto> overB2csItemDtos = mapCreditRevers
					.get("B2CS <>0");
			if (overB2csItemDtos != null && !overB2csItemDtos.isEmpty()) {
				for (CreditTurnOverItemDto overB2csItemDto : overB2csItemDtos) {
					CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
					itemDto.setSno("1A");
					itemDto.setTurnoverComp(
							"Taxable (Normal) B2CS Vertical Upload");
					itemDto.setDigigstAutoComp(
							overB2csItemDto.getDigigstAutoComp());
					itemDtos.add(itemDto);
					digigstAutoComp = digigstAutoComp
							.add(overB2csItemDto.getDigigstAutoComp());
				}
			} else {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("1A");
				itemDto.setTurnoverComp(
						"Taxable (Normal) B2CS Vertical Upload");
				itemDtos.add(itemDto);
			}
			List<CreditTurnOverItemDto> overSezItemDtos = mapCreditRevers
					.get("SEZ");
			if (overSezItemDtos != null && !overSezItemDtos.isEmpty()) {
				for (CreditTurnOverItemDto overSezItemDto : overSezItemDtos) {
					CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
					itemDto.setSno("2");
					itemDto.setTurnoverComp(SEZ_DTA);
					itemDto.setDigigstAutoComp(
							overSezItemDto.getDigigstAutoComp());
					itemDtos.add(itemDto);
					digigstAutoComp = digigstAutoComp
							.add(overSezItemDto.getDigigstAutoComp());
				}
			} else {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("2");
				itemDto.setTurnoverComp(SEZ_DTA);
				itemDtos.add(itemDto);
			}
			List<CreditTurnOverItemDto> overDxpItemDtos = mapCreditRevers
					.get("DXP");
			if (overDxpItemDtos != null && !overDxpItemDtos.isEmpty()) {
				for (CreditTurnOverItemDto overDxpItemDto : overDxpItemDtos) {
					CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
					itemDto.setSno("3");
					itemDto.setTurnoverComp("DXP");
					itemDto.setDigigstAutoComp(
							overDxpItemDto.getDigigstAutoComp());
					itemDtos.add(itemDto);
					digigstAutoComp = digigstAutoComp
							.add(overDxpItemDto.getDigigstAutoComp());
				}
			} else {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("3");
				itemDto.setTurnoverComp("DXP");
				itemDtos.add(itemDto);
			}

			List<CreditTurnOverItemDto> overExptItemDtos = mapCreditRevers
					.get("EXPT");
			if (overExptItemDtos != null && !overExptItemDtos.isEmpty()) {
				for (CreditTurnOverItemDto overExptItemDto : overExptItemDtos) {
					CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
					itemDto.setSno("4");
					itemDto.setTurnoverComp("EXPT");
					itemDto.setDigigstAutoComp(
							overExptItemDto.getDigigstAutoComp());
					itemDtos.add(itemDto);
					digigstAutoComp = digigstAutoComp
							.add(overExptItemDto.getDigigstAutoComp());
				}
			} else {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("4");
				itemDto.setTurnoverComp("EXPT");
				itemDtos.add(itemDto);
			}

			List<CreditTurnOverItemDto> overExpwtItemDtos = mapCreditRevers
					.get(EXPWT);

			if (overExpwtItemDtos != null && !overExpwtItemDtos.isEmpty()) {
				for (CreditTurnOverItemDto overExpwtItemDto : overExpwtItemDtos) {
					CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
					itemDto.setSno("5");
					itemDto.setTurnoverComp(EXPWT);
					itemDto.setDigigstAutoComp(
							overExpwtItemDto.getDigigstAutoComp());
					itemDtos.add(itemDto);
					digigstAutoComp = digigstAutoComp
							.add(overExpwtItemDto.getDigigstAutoComp());
				}
			} else {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("5");
				itemDto.setTurnoverComp(EXPWT);
				itemDtos.add(itemDto);
			}
			List<CreditTurnOverItemDto> overDtaItemDtos = mapCreditRevers
					.get("DTA");
			if (overDtaItemDtos != null && !overDtaItemDtos.isEmpty()) {
				for (CreditTurnOverItemDto overDtaItemDto : overDtaItemDtos) {
					CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
					itemDto.setSno("6");
					itemDto.setTurnoverComp(DTA_SEZ_DTA);
					itemDto.setDigigstAutoComp(
							overDtaItemDto.getDigigstAutoComp());
					itemDtos.add(itemDto);
					digigstAutoComp = digigstAutoComp
							.add(overDtaItemDto.getDigigstAutoComp());
				}
			} else {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("6");
				itemDto.setTurnoverComp(DTA_SEZ_DTA);
				itemDtos.add(itemDto);
			}
			processDto.setItems(itemDtos);
			processDto.setTurnoverComp("Taxable Supplies (A)");
			processDto.setDigigstAutoComp(String.valueOf(digigstAutoComp));
			processDtoList.add(processDto);
		}
	}

	private void exmptSupplies(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();

		BigDecimal digigstAutoComp = BigDecimal.ZERO;
		List<CreditTurnOverItemDto> overExtItemDtos = mapCreditRevers.get(EXT);
		if (overExtItemDtos != null && !overExtItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overExtItemDto : overExtItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("7");
				itemDto.setTurnoverComp(EXEMPT);
				itemDto.setDigigstAutoComp(overExtItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
				digigstAutoComp = digigstAutoComp
						.add(overExtItemDto.getDigigstAutoComp());
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setSno("7");
			itemDto.setTurnoverComp(EXEMPT);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overNilItemDtos = mapCreditRevers.get(NIL);
		if (overNilItemDtos != null && !overNilItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overNilItemDto : overNilItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("8");
				itemDto.setTurnoverComp(NIL_RATED);
				itemDto.setDigigstAutoComp(overNilItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
				digigstAutoComp = digigstAutoComp
						.add(overNilItemDto.getDigigstAutoComp());
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setSno("8");
			itemDto.setTurnoverComp(NIL_RATED);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNilRatedB2CSItemDtos = mapCreditRevers
				.get(SECTION_B2CS);
		if (overNilRatedB2CSItemDtos != null
				&& !overNilRatedB2CSItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overNilRatedB2CSItemDto : overNilRatedB2CSItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("8A");
				itemDto.setTurnoverComp(NIL_RATED_B2CS_VERTICLE_UPLOAD);
				itemDto.setDigigstAutoComp(
						overNilRatedB2CSItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
				digigstAutoComp = digigstAutoComp
						.add(overNilRatedB2CSItemDto.getDigigstAutoComp());
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setSno("8A");
			itemDto.setTurnoverComp(NIL_RATED_B2CS_VERTICLE_UPLOAD);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overNonItemDtos = mapCreditRevers
				.get(SECTION_NON_GST_SCH3);
		if (overNonItemDtos != null && !overNonItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overNonItemDto : overNonItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("9");
				itemDto.setTurnoverComp(NON_GST_SCHEDULE);
				itemDto.setDigigstAutoComp(overNonItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
				digigstAutoComp = digigstAutoComp
						.add(overNonItemDto.getDigigstAutoComp());
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setSno("9");
			itemDto.setTurnoverComp(NON_GST_SCHEDULE);
			itemDtos.add(itemDto);
		}
		processDto.setItems(itemDtos);
		processDto.setTurnoverComp("Exempt Supplies (B)");
		processDto.setDigigstAutoComp(String.valueOf(digigstAutoComp));
		processDtoList.add(processDto);
	}

	private void lessExpRule42(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		processDto.setSno("10");
		processDto.setTurnoverComp("Less : (Explanation to Rule 42)");
		processDtoList.add(processDto);
	}

	private void defultValuesForLessExpRule42(
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		processDto.setSno("10");
		processDto.setTurnoverComp("Less : (Explanation to Rule 42)");
		processDtoList.add(processDto);
	}

	private void totalGstinSupp10(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverDto> processDtoList) {
		List<CreditTurnOverItemDto> itemDtos = mapCreditRevers
				.get(TOTAL_GSTIN_SUPPLIES2);
		if (itemDtos != null && !itemDtos.isEmpty()) {
			itemDtos.forEach(itemDto -> {
				CreditTurnOverDto processDto = new CreditTurnOverDto();
				processDto.setSno("11");
				processDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES);
				processDto.setDigigstAutoComp(
						String.valueOf(itemDto.getDigigstAutoComp()));
				processDtoList.add(processDto);
			});
		} else {
			CreditTurnOverDto processDto = new CreditTurnOverDto();
			processDto.setSno("11");
			processDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES);
			processDto.setDigigstAutoComp(null);
			processDtoList.add(processDto);
		}
	}

	private void defultValuesForTotalGstinSupp10(
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		processDto.setSno("11");
		processDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES);
		processDto.setDigigstAutoComp(null);
		processDtoList.add(processDto);

	}

	private void reverseChargeSupplies(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();

		BigDecimal digigstAutoComp = BigDecimal.ZERO;
		List<CreditTurnOverItemDto> overTaxReveCharItemDtos = mapCreditRevers
				.get("TAX_REVERSE_CHARGE");
		if (overTaxReveCharItemDtos != null
				&& !overTaxReveCharItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overExtItemDto : overTaxReveCharItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("12");
				itemDto.setTurnoverComp(TAXABLE_NORMAL_REVERSE_CHARGE);
				itemDto.setDigigstAutoComp(overExtItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
				digigstAutoComp = digigstAutoComp
						.add(overExtItemDto.getDigigstAutoComp());
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setSno("12");
			itemDto.setTurnoverComp(TAXABLE_NORMAL_REVERSE_CHARGE);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overDataRevChargeItemDtos = mapCreditRevers
				.get("DTA_REVERSE_CHARGE");
		if (overDataRevChargeItemDtos != null
				&& !overDataRevChargeItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overDataRevChargeItemDto : overDataRevChargeItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("13");
				itemDto.setTurnoverComp(DTA_SEZ_DTA);
				itemDto.setDigigstAutoComp(
						overDataRevChargeItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
				digigstAutoComp = digigstAutoComp
						.add(overDataRevChargeItemDto.getDigigstAutoComp());
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setSno("13");
			itemDto.setTurnoverComp(DTA_SEZ_DTA);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overDeemedExportsItemDtos = mapCreditRevers
				.get("DXP_REVERSE_CHARGE");
		if (overDeemedExportsItemDtos != null
				&& !overDeemedExportsItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overDeemedExportsItemDto : overDeemedExportsItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("14");
				itemDto.setTurnoverComp(DEEMED_EXPORTS);
				itemDto.setDigigstAutoComp(
						overDeemedExportsItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
				digigstAutoComp = digigstAutoComp
						.add(overDeemedExportsItemDto.getDigigstAutoComp());
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setSno("14");
			itemDto.setTurnoverComp(DEEMED_EXPORTS);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> oversSezRevChargeItemDtos = mapCreditRevers
				.get("SEZ_REVERSE_CHARGE");
		if (oversSezRevChargeItemDtos != null
				&& !oversSezRevChargeItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto oversSezRevChargeItemDto : oversSezRevChargeItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setSno("15");
				itemDto.setTurnoverComp("SEZ (DTA to SEZ)");
				itemDto.setDigigstAutoComp(
						oversSezRevChargeItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
				digigstAutoComp = digigstAutoComp
						.add(oversSezRevChargeItemDto.getDigigstAutoComp());
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setSno("15");
			itemDto.setTurnoverComp("SEZ (DTA to SEZ)");
			itemDtos.add(itemDto);
		}
		processDto.setItems(itemDtos);
		processDto.setTurnoverComp("Reverse Charge Supplies (C )");
		processDto.setDigigstAutoComp(String.valueOf(digigstAutoComp));
		processDtoList.add(processDto);
	}

	private void defultValuesForReverseChargeSupplies(
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();

		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setSno("12");
		itemDto.setTurnoverComp(TAXABLE_NORMAL_REVERSE_CHARGE);
		itemDtos.add(itemDto);

		CreditTurnOverItemUiDto dtaSezItemDto = new CreditTurnOverItemUiDto();
		dtaSezItemDto.setSno("13");
		dtaSezItemDto.setTurnoverComp(DTA_SEZ_DTA);
		itemDtos.add(dtaSezItemDto);

		CreditTurnOverItemUiDto deemedExpoItemDto = new CreditTurnOverItemUiDto();
		deemedExpoItemDto.setSno("14");
		deemedExpoItemDto.setTurnoverComp(DEEMED_EXPORTS);
		itemDtos.add(deemedExpoItemDto);

		CreditTurnOverItemUiDto sezDtaItemDto = new CreditTurnOverItemUiDto();
		sezDtaItemDto.setSno("15");
		sezDtaItemDto.setTurnoverComp("SEZ (DTA to SEZ)");
		itemDtos.add(sezDtaItemDto);

		processDto.setItems(itemDtos);
		processDto.setTurnoverComp("Reverse Charge Supplies (C )");
		processDtoList.add(processDto);
	}

	private void overOthersD(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverDto> processDtoList) {

		CreditTurnOverDto processDto = new CreditTurnOverDto();
		BigDecimal digigstAutoComp = BigDecimal.ZERO;
		List<CreditTurnOverItemUiDto> otherDitemDtos = new ArrayList<>();

		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setSno("17");
		itemDto.setTurnoverComp("NSY - Non Supply Transactions");
		itemDto.setDigigstAutoComp(null);
		otherDitemDtos.add(itemDto);

		processDto.setItems(otherDitemDtos);
		processDto.setTurnoverComp("Others (D)");
		processDto.setDigigstAutoComp(String.valueOf(digigstAutoComp));
		processDtoList.add(processDto);
		List<CreditTurnOverItemDto> overTotalGstinItemDtos = mapCreditRevers
				.get(TOTAL_GSTIN_SUPPLIES2);
		if (overTotalGstinItemDtos != null
				&& !overTotalGstinItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overTotalGstinItemDto : overTotalGstinItemDtos) {
				CreditTurnOverDto creditTurnOverDto = new CreditTurnOverDto();
				creditTurnOverDto.setSno("18");
				creditTurnOverDto.setTurnoverComp(GRAND_TOTAL_FOR_CONTROLS);
				creditTurnOverDto.setDigigstAutoComp(String
						.valueOf(overTotalGstinItemDto.getDigigstAutoComp()));
				processDtoList.add(creditTurnOverDto);

			}
		} else {
			CreditTurnOverDto creditTurnOverDto = new CreditTurnOverDto();
			creditTurnOverDto.setSno("18");
			creditTurnOverDto.setTurnoverComp(GRAND_TOTAL_FOR_CONTROLS);
			processDtoList.add(creditTurnOverDto);
		}

	}

	private void defultValuesForOverOthersD(
			List<CreditTurnOverDto> processDtoList) {

		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> otherDitemDtos = new ArrayList<>();

		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setSno("17");
		itemDto.setTurnoverComp("NSY - Non Supply Transactions");
		itemDto.setDigigstAutoComp(null);
		otherDitemDtos.add(itemDto);
		processDto.setItems(otherDitemDtos);
		processDto.setTurnoverComp("Others (D)");
		processDtoList.add(processDto);

		CreditTurnOverDto creditTurnOverDto = new CreditTurnOverDto();
		creditTurnOverDto.setSno("18");
		creditTurnOverDto.setTurnoverComp(GRAND_TOTAL_FOR_CONTROLS);
		processDtoList.add(creditTurnOverDto);

	}

	private void itcReversalRatioForTurnOver(
			Map<String, List<CreditTurnOverItemDto>> is,
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		processDto.setTurnoverComp(ITC_REVERSAL_RATIO);
		processDto.setDigigstAutoComp(null);
		processDtoList.add(processDto);
	}

	private void ratio1ForTurnOver(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverDto> processDtoList, String ratio1) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();

		List<CreditTurnOverItemDto> overItcRevItemDtos = mapCreditRevers
				.get(ITC_REVERSAL_RATIO);

		if (overItcRevItemDtos != null && !overItcRevItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overItcRevItemDto : overItcRevItemDtos) {
				processDto.setTurnoverComp(RATIO1_PERC);
				processDto.setDigigstAutoComp(
						String.valueOf(overItcRevItemDto.getRatio1()) + " %");
			}
		} else {
			processDto.setTurnoverComp(RATIO1_PERC);
			processDto.setDigigstAutoComp(String.valueOf(ZERO));
		}

		List<CreditTurnOverItemDto> overExtItemDtos = mapCreditRevers.get(EXT);
		if (overExtItemDtos != null && !overExtItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overExtItemDto : overExtItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(EXEMPT7);
				itemDto.setDigigstAutoComp(overExtItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(EXEMPT7);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNilItemDtos = mapCreditRevers.get(NIL);
		if (overNilItemDtos != null && !overNilItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overNilItemDto : overNilItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(NIL_RATED8);
				itemDto.setDigigstAutoComp(overNilItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(NIL_RATED8);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overTtlItemDtos = mapCreditRevers
				.get(SECTION_TTL_RCS);
		if (overTtlItemDtos != null && !overTtlItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overTtlItemDto : overTtlItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
				itemDto.setDigigstAutoComp(overTtlItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
			itemDtos.add(itemDto);
		}
		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(itemDto);

		List<CreditTurnOverItemDto> overTotalGstinItemDtos = mapCreditRevers
				.get("T_GSTIN_SUPPLIES");
		if (overTotalGstinItemDtos != null
				&& !overTotalGstinItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overTotalGstinItemDto : overTotalGstinItemDtos) {
				CreditTurnOverItemUiDto overItemDto = new CreditTurnOverItemUiDto();
				overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11_9);
				overItemDto.setDigigstAutoComp(
						overTotalGstinItemDto.getDigigstAutoComp());
				itemDtos.add(overItemDto);
			}
		} else {
			CreditTurnOverItemUiDto overItemDto = new CreditTurnOverItemUiDto();
			overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11_9);
			itemDtos.add(overItemDto);
		}
		
		processDto.setUserInputRatio(ratio1);
		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}

	private void defultValueForRatio1ForTurnOver(
			List<CreditTurnOverDto> processDtoList, String ratio1) {

		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();
		processDto.setTurnoverComp(RATIO1_PERC);
		processDto.setDigigstAutoComp(String.valueOf(ZERO));

		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setTurnoverComp(EXEMPT7);
		itemDtos.add(itemDto);

		CreditTurnOverItemUiDto nilRated8ItemDto = new CreditTurnOverItemUiDto();
		nilRated8ItemDto.setTurnoverComp(NIL_RATED8);
		itemDtos.add(nilRated8ItemDto);

		CreditTurnOverItemUiDto secOutwardRevItemDto = new CreditTurnOverItemUiDto();
		secOutwardRevItemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
		itemDtos.add(secOutwardRevItemDto);

		CreditTurnOverItemUiDto explaToRuleItemDto = new CreditTurnOverItemUiDto();
		explaToRuleItemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(explaToRuleItemDto);

		CreditTurnOverItemUiDto overItemDto = new CreditTurnOverItemUiDto();
		overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11_9);
		itemDtos.add(overItemDto);

		processDto.setUserInputRatio(ratio1);
		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}

	private void ratio2ForTurnOver(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverDto> processDtoList, String ratio2) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();

		List<CreditTurnOverItemDto> overItcRevItemDtos = mapCreditRevers
				.get(ITC_REVERSAL_RATIO);
		if (overItcRevItemDtos != null && !overItcRevItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overItcRevItemDto : overItcRevItemDtos) {
				processDto.setTurnoverComp(RATIO2_PERC);
				processDto.setDigigstAutoComp(
						String.valueOf(overItcRevItemDto.getRatio2() + " %"));
			}
		} else {
			processDto.setTurnoverComp(RATIO2_PERC);
			processDto.setDigigstAutoComp(String.valueOf(ZERO));
		}

		List<CreditTurnOverItemDto> overExtItemDtos = mapCreditRevers.get(EXT);
		if (overExtItemDtos != null && !overExtItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overExtItemDto : overExtItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(EXEMPT7);
				itemDto.setDigigstAutoComp(overExtItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(EXEMPT7);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNilItemDtos = mapCreditRevers.get(NIL);
		if (overNilItemDtos != null && !overNilItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overNilItemDto : overNilItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(NIL_RATED8);
				itemDto.setDigigstAutoComp(overNilItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(NIL_RATED8);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNonItemDtos = mapCreditRevers
				.get(SECTION_NON_GST_SCH3);
		if (overNonItemDtos != null && !overNonItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overNonItemDto : overNonItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(SECTION_NON_GST_9);
				itemDto.setDigigstAutoComp(overNonItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(SECTION_NON_GST_9);
			itemDtos.add(itemDto);
		}
		CreditTurnOverItemUiDto nsyItemDto = new CreditTurnOverItemUiDto();
		nsyItemDto.setTurnoverComp(NSY_SEC_17);
		itemDtos.add(nsyItemDto);

		List<CreditTurnOverItemDto> overTtlItemDtos = mapCreditRevers
				.get(SECTION_TTL_RCS);
		if (overTtlItemDtos != null && !overTtlItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overTtlItemDto : overTtlItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
				itemDto.setDigigstAutoComp(overTtlItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
			itemDtos.add(itemDto);
		}
		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDto.setDigigstAutoComp(null);
		itemDtos.add(itemDto);

		List<CreditTurnOverItemDto> overTotalGstinItemDtos = mapCreditRevers
				.get(TOTAL_GSTIN_SUPPLIES2);
		if (overTotalGstinItemDtos != null
				&& !overTotalGstinItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overTotalGstinItemDto : overTotalGstinItemDtos) {
				CreditTurnOverItemUiDto overItemDto = new CreditTurnOverItemUiDto();
				overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
				overItemDto.setDigigstAutoComp(
						overTotalGstinItemDto.getDigigstAutoComp());
				itemDtos.add(overItemDto);
			}
		} else {
			CreditTurnOverItemUiDto overItemDto = new CreditTurnOverItemUiDto();
			overItemDto.setSno("29");
			overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
			itemDtos.add(overItemDto);
		}
		
		processDto.setUserInputRatio(ratio2);
		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}

	private void defultValueForRatio2ForTurnOver(
			List<CreditTurnOverDto> processDtoList, String ratio2) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();

		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();
		processDto.setTurnoverComp(RATIO2_PERC);
		processDto.setDigigstAutoComp(String.valueOf(ZERO));

		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setTurnoverComp(EXEMPT7);
		itemDtos.add(itemDto);

		CreditTurnOverItemUiDto nilRated8ItemDto = new CreditTurnOverItemUiDto();
		nilRated8ItemDto.setTurnoverComp(NIL_RATED8);
		itemDtos.add(nilRated8ItemDto);

		CreditTurnOverItemUiDto sectionNonGstItemDto = new CreditTurnOverItemUiDto();
		sectionNonGstItemDto.setTurnoverComp(SECTION_NON_GST_9);
		itemDtos.add(sectionNonGstItemDto);

		CreditTurnOverItemUiDto nsyItemDto = new CreditTurnOverItemUiDto();
		nsyItemDto.setTurnoverComp(NSY_SEC_17);
		itemDtos.add(nsyItemDto);

		CreditTurnOverItemUiDto secOutwardItemDto = new CreditTurnOverItemUiDto();
		secOutwardItemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
		itemDtos.add(secOutwardItemDto);

		CreditTurnOverItemUiDto explaToRuleItemDto = new CreditTurnOverItemUiDto();
		explaToRuleItemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(explaToRuleItemDto);

		CreditTurnOverItemUiDto overItemDto = new CreditTurnOverItemUiDto();
		overItemDto.setSno("29");
		overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
		itemDtos.add(overItemDto);

		processDto.setUserInputRatio(ratio2);
		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}

	private void ratio3ForTurnOver(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();

		List<CreditTurnOverItemDto> overItcRevItemDtos = mapCreditRevers
				.get(ITC_REVERSAL_RATIO);
		if (overItcRevItemDtos != null && !overItcRevItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overItcRevItemDto : overItcRevItemDtos) {
				processDto.setTurnoverComp(RATIO311);
				processDto.setDigigstAutoComp(
						String.valueOf(overItcRevItemDto.getRatio3()) + " %");
			}
		} else {
			processDto.setTurnoverComp(RATIO311);
			processDto.setDigigstAutoComp(String.valueOf(" 0.0 %"));
		}

		List<CreditTurnOverItemDto> overExtItemDtos = mapCreditRevers.get(EXT);
		if (overExtItemDtos != null && !overExtItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overExtItemDto : overExtItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(EXEMPT7);
				itemDto.setDigigstAutoComp(overExtItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(EXEMPT7);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overNilItemDtos = mapCreditRevers.get(NIL);
		if (overNilItemDtos != null && !overNilItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overNilItemDto : overNilItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(NIL_RATED8);
				itemDto.setDigigstAutoComp(overNilItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(NIL_RATED8);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNonItemDtos = mapCreditRevers
				.get(SECTION_NON_GST_SCH3);
		if (overNonItemDtos != null && !overNonItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overNonItemDto : overNonItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(SECTION_NON_GST_9);
				itemDto.setDigigstAutoComp(overNonItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(SECTION_NON_GST_9);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overTtlItemDtos = mapCreditRevers
				.get(SECTION_TTL_RCS);
		if (overTtlItemDtos != null && !overTtlItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overTtlItemDto : overTtlItemDtos) {
				CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
				itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
				itemDto.setDigigstAutoComp(overTtlItemDto.getDigigstAutoComp());
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
			itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
			itemDtos.add(itemDto);
		}
		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(itemDto);

		List<CreditTurnOverItemDto> overTotalGstinItemDtos = mapCreditRevers
				.get(TOTAL_GSTIN_SUPPLIES2);
		if (overTotalGstinItemDtos != null
				&& !overTotalGstinItemDtos.isEmpty()) {
			for (CreditTurnOverItemDto overTotalGstinItemDto : overTotalGstinItemDtos) {
				CreditTurnOverItemUiDto overItemDto = new CreditTurnOverItemUiDto();
				overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
				overItemDto.setDigigstAutoComp(
						overTotalGstinItemDto.getDigigstAutoComp());
				itemDtos.add(overItemDto);
			}
		} else {
			CreditTurnOverItemUiDto overItemDto = new CreditTurnOverItemUiDto();
			overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
			itemDtos.add(overItemDto);
		}
		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}

	private void defultValueForRatio3ForTurnOver(
			List<CreditTurnOverDto> processDtoList) {
		CreditTurnOverDto processDto = new CreditTurnOverDto();
		List<CreditTurnOverItemUiDto> itemDtos = new ArrayList<>();

		processDto.setTurnoverComp(RATIO311);
		processDto.setDigigstAutoComp(String.valueOf(" 0.0 %"));

		CreditTurnOverItemUiDto itemDto = new CreditTurnOverItemUiDto();
		itemDto.setTurnoverComp(EXEMPT7);
		itemDtos.add(itemDto);

		CreditTurnOverItemUiDto nilRated8itemDto = new CreditTurnOverItemUiDto();
		nilRated8itemDto.setTurnoverComp(NIL_RATED8);
		itemDtos.add(nilRated8itemDto);

		CreditTurnOverItemUiDto secNonGstItemDto = new CreditTurnOverItemUiDto();
		secNonGstItemDto.setTurnoverComp(SECTION_NON_GST_9);
		itemDtos.add(secNonGstItemDto);

		CreditTurnOverItemUiDto secOutwardItemDto = new CreditTurnOverItemUiDto();
		secOutwardItemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
		itemDtos.add(secOutwardItemDto);

		CreditTurnOverItemUiDto explaToRuleItemDto = new CreditTurnOverItemUiDto();
		explaToRuleItemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(explaToRuleItemDto);

		CreditTurnOverItemUiDto overItemDto = new CreditTurnOverItemUiDto();
		overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
		itemDtos.add(overItemDto);

		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}

	private Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOver(
			List<CreditTurnOverItemDto> itemDtos) {
		Map<String, List<CreditTurnOverItemDto>> mapCredTurnItemDto = new HashMap<>();
		itemDtos.forEach(itemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(itemDto.getTurnoverComp());
			String docKey = key.toString();
			if (mapCredTurnItemDto.containsKey(docKey)) {
				List<CreditTurnOverItemDto> turnOverItems = mapCredTurnItemDto
						.get(docKey);
				turnOverItems.add(itemDto);
				mapCredTurnItemDto.put(docKey, turnOverItems);
			} else {
				List<CreditTurnOverItemDto> turnOverItems = new ArrayList<>();
				turnOverItems.add(itemDto);
				mapCredTurnItemDto.put(docKey, turnOverItems);
			}
		});
		return mapCredTurnItemDto;
	}

	private List<CreditTurnOverItemDto> convertObjToCredTurnOver(
			final Annexure1SummaryReqDto reqDto) {
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();

		String gstin = setGstin(dataSecAttrs);

		List<CreditTurnOverItemDto> turnOverItemDtos = new ArrayList<>();
		int derivedRetPer = 0;
		if (reqDto.getTaxPeriod() != null) {
			derivedRetPer = GenUtil
					.convertTaxPeriodToInt(reqDto.getTaxPeriod());
		}

		List<Object[]> reversalObjs = daoImpl
				.getCreditReversalAndTurnvolProcess("turnOver", gstin,
						derivedRetPer);
		for (Object[] obj : reversalObjs) {
			CreditTurnOverItemDto processDto = new CreditTurnOverItemDto();
			processDto.setTurnoverComp(
					obj[0] != null ? String.valueOf(obj[0]) : null);
			processDto.setDigigstAutoComp(obj[1] != null
					? new BigDecimal(String.valueOf(obj[1])) : BigDecimal.ZERO);

			Double ratio1Double = Double
					.parseDouble(obj[2] != null ? String.valueOf(obj[2]) : "0");
			Double ratio2Double = Double
					.parseDouble(obj[3] != null ? String.valueOf(obj[3]) : "0");
			Double ratio3Double = Double
					.parseDouble(obj[4] != null ? String.valueOf(obj[4]) : "0");

			BigDecimal ratio1 = new BigDecimal(ratio1Double);
			BigDecimal ratio2 = new BigDecimal(ratio2Double);
			BigDecimal ratio3 = new BigDecimal(ratio3Double);

			if (ratio1.scale() > 3) {
				ratio1 = ratio1.setScale(3, RoundingMode.DOWN);
			}

			if (ratio2.scale() > 3) {
				ratio2 = ratio2.setScale(3, RoundingMode.DOWN);
			}

			if (ratio3.scale() > 3) {
				ratio3 = ratio3.setScale(3, RoundingMode.DOWN);
			}

			processDto.setRatio1(ratio1);
			processDto.setRatio2(ratio2);
			processDto.setRatio3(ratio3);
			turnOverItemDtos.add(processDto);
		}
		return turnOverItemDtos;
	}

	private String setGstin(Map<String, List<String>> dataSecAttrs) {
		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			gstin = gstinList.get(0);
		}
		return gstin;
	}

	public CreditReveSummaryDto getCredRevSummary(
			final Annexure1SummaryReqDto reqDto) {
		CreditReveSummaryDto summaryDto = new CreditReveSummaryDto();

		List<CreditReversalSummaryDto> revSumDtos = getConvertObject(reqDto);
		Map<String, List<CreditReversalSummaryDto>> mapRevSum = mapReveSummary(
				revSumDtos);
		List<CreditReversalSummaryDto> reveSumDtos = mapRevSum.get("Reversal");
		List<CreditReversalSummaryDto> turnOverSumDtos = mapRevSum
				.get("Turn_over");
		List<CreditReversalSummaryDto> ratioSumDtos = mapRevSum.get("Ratios");

		if (reveSumDtos != null && !reveSumDtos.isEmpty()) {
			reveSumDtos.forEach(reveSumDto -> {
				summaryDto.setReversalRatio1(reveSumDto.getRatio1());
				summaryDto.setReversalRatio2(reveSumDto.getRatio2());
				summaryDto.setReversalRatio3(reveSumDto.getRatio3());
			});
		}
		if (turnOverSumDtos != null && !turnOverSumDtos.isEmpty()) {
			turnOverSumDtos.forEach(turnOverSumDto -> {
				summaryDto.setTurnoverRatio1(turnOverSumDto.getRatio1());
				summaryDto.setTurnoverRatio2(turnOverSumDto.getRatio2());
				summaryDto.setTurnoverRatio3(turnOverSumDto.getRatio3());
			});
		}
		if (ratioSumDtos != null && !ratioSumDtos.isEmpty()) {
			ratioSumDtos.forEach(ratioSumDto -> {
				summaryDto.setRatio1(
						String.valueOf(ratioSumDto.getRatio1() + " %"));
				summaryDto.setRatio2(
						String.valueOf(ratioSumDto.getRatio2() + " %"));
				summaryDto.setRatio3(
						String.valueOf(ratioSumDto.getRatio3() + " %"));
			});
		}
		return summaryDto;
	}

	private List<CreditReversalSummaryDto> getConvertObject(
			final Annexure1SummaryReqDto reqDto) {
		List<CreditReversalSummaryDto> reversalSumDtos = new ArrayList<>();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

		int derivedRetPer = GenUtil
				.convertTaxPeriodToInt(reqDto.getTaxPeriod());
		List<Object[]> objs = daoImpl.getCreditReverseSummary(gstinList,
				derivedRetPer);
		if (objs != null && !objs.isEmpty()) {
			objs.forEach(obj -> {
				CreditReversalSummaryDto reversalSumDto = new CreditReversalSummaryDto();

				Double ratio1Double = Double.parseDouble(
						obj[1] != null ? String.valueOf(obj[1]) : "0");
				Double ratio2Double = Double.parseDouble(
						obj[2] != null ? String.valueOf(obj[2]) : "0");
				Double ratio3Double = Double.parseDouble(
						obj[3] != null ? String.valueOf(obj[3]) : "0");

				BigDecimal ratio1 = new BigDecimal(ratio1Double);
				BigDecimal ratio2 = new BigDecimal(ratio2Double);
				BigDecimal ratio3 = new BigDecimal(ratio3Double);

				if (ratio1.scale() > 3) {
					ratio1 = ratio1.setScale(3, RoundingMode.DOWN);
				}

				if (ratio2.scale() > 3) {
					ratio2 = ratio2.setScale(3, RoundingMode.DOWN);
				}

				if (ratio3.scale() > 3) {
					ratio3 = ratio3.setScale(3, RoundingMode.DOWN);
				}

				reversalSumDto.setRatio1(ratio1);
				reversalSumDto.setRatio2(ratio2);
				reversalSumDto.setRatio3(ratio3);

				reversalSumDto.setSectionName(
						obj[0] != null ? String.valueOf(obj[0]) : null);
				reversalSumDtos.add(reversalSumDto);
			});
		}
		return reversalSumDtos;
	}

	private Map<String, List<CreditReversalSummaryDto>> mapReveSummary(
			final List<CreditReversalSummaryDto> revSumDtos) {
		Map<String, List<CreditReversalSummaryDto>> sumDtos = new HashMap<>();

		revSumDtos.forEach(revSumDto -> {
			StringBuilder key = new StringBuilder();
			key.append(revSumDto.getSectionName());
			String sumKey = key.toString();
			if (sumDtos.containsKey(sumKey)) {
				List<CreditReversalSummaryDto> reviewSumDtos = sumDtos
						.get(sumKey);
				reviewSumDtos.add(revSumDto);
				sumDtos.put(sumKey, reviewSumDtos);
			} else {
				List<CreditReversalSummaryDto> reviewSumDtos = new ArrayList<>();
				reviewSumDtos.add(revSumDto);
				sumDtos.put(sumKey, reviewSumDtos);
			}
		});
		return sumDtos;
	}

	@Override
	public String pushToGstr3BCredRevRatio(Annexure1SummaryReqDto reqDto) {
		String msg = null;
		try {
			List<Gstr3BRuleComputeRespDto> respDtoList = convertObjGstr3b(
					reqDto);
			List<Gstr3BGstinAspComputeEntity> computeEntities = new ArrayList<>();
			respDtoList.forEach(respDto -> {
				Gstr3BGstinAspComputeEntity computeEntity = new Gstr3BGstinAspComputeEntity();
				repository.updateIdGstr3BAspCompute(respDto.getTaxPeriod(),
						respDto.getGstin(), respDto.getSubSectionName());
				computeEntity.setGstin(respDto.getGstin());
				computeEntity.setTaxPeriod(respDto.getTaxPeriod());
				computeEntity.setSectionName(respDto.getSectionName());
				computeEntity.setSubSectionName(respDto.getSubSectionName());
				computeEntity.setTaxableVal(respDto.getTaxableValue());
				computeEntity.setIgst(respDto.getIgstAmt());
				computeEntity.setCgst(respDto.getCgstAmt());
				computeEntity.setSgst(respDto.getSgstAmt());
				computeEntity.setCess(respDto.getCessAmt());
				computeEntity.setInterState(BigDecimal.ZERO);
				computeEntity.setIntraState(BigDecimal.ZERO);
				computeEntity
						.setDerivedRetPeriod(respDto.getDerivedRetPeriod());
				computeEntity.setBatchId(respDto.getBatchId());
				computeEntity.setIsActive(true);
				User user = SecurityContext.getUser();
				computeEntity.setCreatedBy(user.getUserPrincipalName());
				computeEntity.setCreateDate(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				computeEntities.add(computeEntity);
			});
			if (!computeEntities.isEmpty()) {
				repository.saveAll(computeEntities);
			}
			msg = "Updated Successfully";
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
			msg = "Updated failed";
		}
		return msg;
	}

	private List<Gstr3BRuleComputeRespDto> convertObjGstr3b(
			Annexure1SummaryReqDto reqDto) {
		int derivedRetPer = GenUtil
				.convertTaxPeriodToInt(reqDto.getTaxPeriod());

		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

		List<Object[]> objs = new ArrayList<>();
		if ("Ratio1".equalsIgnoreCase(reqDto.getRatio())) {
			objs = daoImpl.getGstr3BRuleCompute(gstinList, derivedRetPer,
					"Total Reversal_RATIO_1");
		} else if ("Ratio2".equalsIgnoreCase(reqDto.getRatio())) {
			objs = daoImpl.getGstr3BRuleCompute(gstinList, derivedRetPer,
					"Total Reversal_RATIO_2");
		} else if ("Ratio3".equalsIgnoreCase(reqDto.getRatio())) {
			objs = daoImpl.getGstr3BRuleCompute(gstinList, derivedRetPer,
					"Total Reversal_RATIO_3");
		}
		return convertObjToGstr3B(objs);
	}

	private List<Gstr3BRuleComputeRespDto> convertObjToGstr3B(
			List<Object[]> objs) {
		List<Gstr3BRuleComputeRespDto> respDtoList = new ArrayList<>();
		if (objs != null && !objs.isEmpty()) {
			objs.forEach(obj -> {
				Gstr3BRuleComputeRespDto respDto = new Gstr3BRuleComputeRespDto();

				respDto.setTaxPeriod(
						obj[0] != null ? String.valueOf(obj[0]) : null);
				respDto.setGstin(
						obj[1] != null ? String.valueOf(obj[1]) : null);
				respDto.setSectionName(
						obj[2] != null ? String.valueOf(obj[2]) : null);
				respDto.setSubSectionName(
						obj[3] != null ? String.valueOf(obj[3]) : null);
				respDto.setTaxableValue(
						obj[4] != null && !obj[4].toString().trim().isEmpty()
								? new BigDecimal(String.valueOf(obj[4]))
								: BigDecimal.ZERO);
				respDto.setIgstAmt(
						obj[5] != null && !obj[5].toString().trim().isEmpty()
								? new BigDecimal(String.valueOf(obj[5]))
								: BigDecimal.ZERO);
				respDto.setCgstAmt(
						obj[6] != null && !obj[6].toString().trim().isEmpty()
								? new BigDecimal(String.valueOf(obj[6]))
								: BigDecimal.ZERO);
				respDto.setSgstAmt(
						obj[7] != null && !obj[7].toString().trim().isEmpty()
								? new BigDecimal(String.valueOf(obj[7]))
								: BigDecimal.ZERO);
				respDto.setCessAmt(
						obj[8] != null && !obj[8].toString().trim().isEmpty()
								? new BigDecimal(String.valueOf(obj[8]))
								: BigDecimal.ZERO);

				respDto.setDerivedRetPeriod(
						obj[9] != null && !obj[9].toString().trim().isEmpty()
								? new Integer(String.valueOf(obj[9])) : 0);
				respDto.setBatchId(
						obj[10] != null && !obj[10].toString().trim().isEmpty()
								? new Long(String.valueOf(obj[10])) : 0);
				respDtoList.add(respDto);
			});
		}
		return respDtoList;
	}
}
