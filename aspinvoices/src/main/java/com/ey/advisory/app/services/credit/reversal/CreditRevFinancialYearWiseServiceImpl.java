package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bItcStatusRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

@Service("CreditRevFinancialYearWiseServiceImpl")
public class CreditRevFinancialYearWiseServiceImpl
		implements CreditRevFinancialYearWiseService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreditRevFinancialYearWiseServiceImpl.class);
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
	private static final String TOTAL_GSTIN_SUPPLIES2 = "TOTAL_GSTIN_SUPPLIES";
	private static final String DEEMED_EXPORTS = "Deemed Exports";
	private static final String TAXABLE_NORMAL = "Taxable (Normal)";
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

	private static final String NIL_RATED = "Nil Rated";
	private static final String EXPWT = "EXPWT";
	private static final String GRAND_TOTAL_FOR_CONTROLS = "Grand Total for Controls";
	private static final String TOTAL_GSTIN_SUPPLIES = "Total GSTIN Supplies (A+B) - 10";
	private static final String NON_GST_SCHEDULE = "Non GST /Schedule III";
	private static final String NSY_SEC_17 = "NSY- Sec 17(5) (17)";

	private static final String TAXABLE_B2CS_VERTICAL = "Taxable (Normal) B2CS Vertical Upload";

	private static final String ACTIVE = "Active";
	private static final String INACTIVE = "Inactive";

	private static final String RATIO311 = "Ratio 3 (%) (7+8+8A+9-10+16) / 11";

	private static final String LESS_INELIGIBLE_CREDIT = "Less: Ineligible credit based on ITC Reversal Identifier - D";

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("CreditRevForFinancialYearWiseDaoImpl")
	private CreditRevForFinancialYearWiseDaoImpl finaYearWiseDaoImpl;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinDetailRepository;

	@Autowired
	@Qualifier("Gstr3bItcStatusRepository")
	private Gstr3bItcStatusRepository gstr3bItcStatusRepository;

	public String proceCallForFinancialYear(Annexure1SummaryReqDto reqDto) {
		String msg = null;
		List<String> gstinList = getDataSecurity(reqDto);
		int countValue = 0;
		if (gstinList != null && !gstinList.isEmpty()) {
			for (String gstinvalue : gstinList) {
				int fromDerRetPer = GenUtil
						.convertTaxPeriodToInt(reqDto.getFromRetPeriod());

				int toDerRetPer = GenUtil
						.convertTaxPeriodToInt(reqDto.getToRetPeriod());
				int count = finaYearWiseDaoImpl.proceCallForFinancialYear(
						gstinvalue, fromDerRetPer, toDerRetPer);
				countValue = count + 1;
			}
		}
		if (countValue > 0) {
			msg = "Updated Successfully";
		} else {
			msg = "Invalid Gstin";
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

	private List<String> getDataSecurityForFinancial(
			final Annexure1SummaryReqDto reqDto) {
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

	@Override
	public FinancialYearFinalRespDto getFinYearForProceSummary(
			Annexure1SummaryReqDto reqDto) {

		FinancialYearFinalRespDto finYearFinalRespDto = new FinancialYearFinalRespDto();
		List<FinancialYearRespDto> finYearRatio1 = new ArrayList<>();
		List<FinancialYearRespDto> finYearRatio2 = new ArrayList<>();
		List<FinancialYearRespDto> finYearRatio3 = new ArrayList<>();

		List<String> gstinList = getDataSecurityForFinancial(reqDto);
		if (gstinList != null && !gstinList.isEmpty()) {
			for (String gstinvalue : gstinList) {
				int fromRetPeriod = GenUtil
						.convertTaxPeriodToInt(reqDto.getTaxPeriodFrom());

				int toRetPeriod = GenUtil
						.convertTaxPeriodToInt(reqDto.getTaxPeriodTo());

				FinancialYearRespDto finYearRespRatio1Dto = new FinancialYearRespDto();
				FinancialYearRespDto finYearRespRatio2Dto = new FinancialYearRespDto();
				FinancialYearRespDto finYearRespRatio3Dto = new FinancialYearRespDto();
				BigDecimal totalTaxAmountRatio1 = BigDecimal.ZERO;
				BigDecimal totalTaxAmountRatio2 = BigDecimal.ZERO;
				BigDecimal totalTaxAmountRatio3 = BigDecimal.ZERO;
				List<Object[]> objs = finaYearWiseDaoImpl.getProcSumRevFinaYear(
						gstinvalue, fromRetPeriod, toRetPeriod);
				finYearRespRatio1Dto.setGstin(gstinvalue);
				finYearRespRatio2Dto.setGstin(gstinvalue);
				finYearRespRatio3Dto.setGstin(gstinvalue);
				String stateCode = gstinvalue.substring(0, 2);

				// Getting State Name based on state code
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);

				finYearRespRatio1Dto.setState(stateName);
				finYearRespRatio2Dto.setState(stateName);
				finYearRespRatio3Dto.setState(stateName);

				GSTNDetailEntity gstnEntity = gstinDetailRepository
						.findRegDates(gstinvalue);

				finYearRespRatio1Dto
						.setRegType(gstnEntity.getRegistrationType());
				finYearRespRatio2Dto
						.setRegType(gstnEntity.getRegistrationType());
				finYearRespRatio3Dto
						.setRegType(gstnEntity.getRegistrationType());

				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstnEntity.getGstin());

				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						finYearRespRatio1Dto.setAuthToken(ACTIVE);
						finYearRespRatio2Dto.setAuthToken(ACTIVE);
						finYearRespRatio3Dto.setAuthToken(ACTIVE);
					} else {
						finYearRespRatio1Dto.setAuthToken(INACTIVE);
						finYearRespRatio2Dto.setAuthToken(INACTIVE);
						finYearRespRatio3Dto.setAuthToken(INACTIVE);
					}
				} else {
					finYearRespRatio1Dto.setAuthToken(INACTIVE);
					finYearRespRatio2Dto.setAuthToken(INACTIVE);
					finYearRespRatio3Dto.setAuthToken(INACTIVE);
				}
				finYearRespRatio1Dto.setStatus("NOT INITIATED");
				finYearRespRatio2Dto.setStatus("NOT INITIATED");
				finYearRespRatio3Dto.setStatus("NOT INITIATED");
				List<CreditRevFinancialRespDto> finYearCredRespDtos = getfinYearProcSumAllRatio(
						objs);
				if (finYearCredRespDtos != null
						&& !finYearCredRespDtos.isEmpty()) {
					Map<String, List<CreditRevFinancialRespDto>> mapCreditRevFinRatioRespDto = mapCreditRevFinancialRatio(
							finYearCredRespDtos);
					List<CreditRevFinancialRespDto> finYearJanRespDtos = mapCreditRevFinRatioRespDto
							.get("01");
					if (finYearJanRespDtos != null
							&& !finYearJanRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearJanRespDto : finYearJanRespDtos) {
							finYearRespRatio1Dto.setJanRevRatio(
									finYearJanRespDto.getTaxbleValueRatio1());
							BigDecimal janTaxAmountRatio1 = finYearJanRespDto
									.getCgstARaio1()
									.add(finYearJanRespDto.getCessARaio1())
									.add(finYearJanRespDto.getIgstARaio1())
									.add(finYearJanRespDto.getSgstARaio1());
							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearJanRespDto.getCgstARaio1())
									.add(finYearJanRespDto.getCessARaio1())
									.add(finYearJanRespDto.getIgstARaio1())
									.add(finYearJanRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setJanTaxamount(janTaxAmountRatio1);

							finYearRespRatio2Dto.setJanRevRatio(
									finYearJanRespDto.getTaxbleValueRatio2());
							BigDecimal janTaxAmountRatio2 = finYearJanRespDto
									.getCgstARaio2()
									.add(finYearJanRespDto.getCessARaio2())
									.add(finYearJanRespDto.getIgstARaio2())
									.add(finYearJanRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setJanTaxamount(janTaxAmountRatio2);
							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearJanRespDto.getCgstARaio2())
									.add(finYearJanRespDto.getCessARaio2())
									.add(finYearJanRespDto.getIgstARaio2())
									.add(finYearJanRespDto.getSgstARaio2());
							finYearRespRatio3Dto.setJanRevRatio(
									finYearJanRespDto.getTaxbleValueRatio3());
							BigDecimal janTaxAmountRatio3 = finYearJanRespDto
									.getCgstARaio3()
									.add(finYearJanRespDto.getCessARaio3())
									.add(finYearJanRespDto.getIgstARaio3())
									.add(finYearJanRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setJanTaxamount(janTaxAmountRatio3);

							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearJanRespDto.getCgstARaio3())
									.add(finYearJanRespDto.getCessARaio3())
									.add(finYearJanRespDto.getIgstARaio3())
									.add(finYearJanRespDto.getSgstARaio3());

							finYearRespRatio1Dto
									.setStatus(finYearJanRespDto.getStatus());
							finYearRespRatio2Dto
									.setStatus(finYearJanRespDto.getStatus());
							finYearRespRatio3Dto
									.setStatus(finYearJanRespDto.getStatus());
						}
					}

					List<CreditRevFinancialRespDto> finYearFebRespDtos = mapCreditRevFinRatioRespDto
							.get("02");
					if (finYearFebRespDtos != null
							&& !finYearFebRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearFebRespDto : finYearFebRespDtos) {
							finYearRespRatio1Dto.setFebRevRatio(
									finYearFebRespDto.getTaxbleValueRatio1());
							BigDecimal febTaxAmountRatio1 = finYearFebRespDto
									.getCgstARaio1()
									.add(finYearFebRespDto.getCessARaio1())
									.add(finYearFebRespDto.getIgstARaio1())
									.add(finYearFebRespDto.getSgstARaio1());
							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearFebRespDto.getCgstARaio1())
									.add(finYearFebRespDto.getCessARaio1())
									.add(finYearFebRespDto.getIgstARaio1())
									.add(finYearFebRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setFebTaxamount(febTaxAmountRatio1);

							finYearRespRatio2Dto.setFebRevRatio(
									finYearFebRespDto.getTaxbleValueRatio2());
							BigDecimal febTaxAmountRatio2 = finYearFebRespDto
									.getCgstARaio2()
									.add(finYearFebRespDto.getCessARaio2())
									.add(finYearFebRespDto.getIgstARaio2())
									.add(finYearFebRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setFebTaxamount(febTaxAmountRatio2);

							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearFebRespDto.getCgstARaio2())
									.add(finYearFebRespDto.getCessARaio2())
									.add(finYearFebRespDto.getIgstARaio2())
									.add(finYearFebRespDto.getSgstARaio2());

							finYearRespRatio3Dto.setFebRevRatio(
									finYearFebRespDto.getTaxbleValueRatio3());
							BigDecimal febTaxAmountRatio3 = finYearFebRespDto
									.getCgstARaio3()
									.add(finYearFebRespDto.getCessARaio3())
									.add(finYearFebRespDto.getIgstARaio3())
									.add(finYearFebRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setFebTaxamount(febTaxAmountRatio3);
							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearFebRespDto.getCgstARaio3())
									.add(finYearFebRespDto.getCessARaio3())
									.add(finYearFebRespDto.getIgstARaio3())
									.add(finYearFebRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearMarchRespDtos = mapCreditRevFinRatioRespDto
							.get("03");
					if (finYearMarchRespDtos != null
							&& !finYearMarchRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearMarchRespDto : finYearMarchRespDtos) {
							finYearRespRatio1Dto.setMarchRevRatio(
									finYearMarchRespDto.getTaxbleValueRatio1());
							BigDecimal marchTaxAmountRatio1 = finYearMarchRespDto
									.getCgstARaio1()
									.add(finYearMarchRespDto.getCessARaio1())
									.add(finYearMarchRespDto.getIgstARaio1())
									.add(finYearMarchRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setMarchTaxamount(marchTaxAmountRatio1);

							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearMarchRespDto.getCgstARaio1())
									.add(finYearMarchRespDto.getCessARaio1())
									.add(finYearMarchRespDto.getIgstARaio1())
									.add(finYearMarchRespDto.getSgstARaio1());

							finYearRespRatio2Dto.setMarchRevRatio(
									finYearMarchRespDto.getTaxbleValueRatio2());
							BigDecimal febTaxAmountRatio2 = finYearMarchRespDto
									.getCgstARaio2()
									.add(finYearMarchRespDto.getCessARaio2())
									.add(finYearMarchRespDto.getIgstARaio2())
									.add(finYearMarchRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setMarchTaxamount(febTaxAmountRatio2);

							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearMarchRespDto.getCgstARaio2())
									.add(finYearMarchRespDto.getCessARaio2())
									.add(finYearMarchRespDto.getIgstARaio2())
									.add(finYearMarchRespDto.getSgstARaio2());

							finYearRespRatio3Dto.setMarchRevRatio(
									finYearMarchRespDto.getTaxbleValueRatio3());
							BigDecimal febTaxAmountRatio3 = finYearMarchRespDto
									.getCgstARaio3()
									.add(finYearMarchRespDto.getCessARaio3())
									.add(finYearMarchRespDto.getIgstARaio3())
									.add(finYearMarchRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setMarchTaxamount(febTaxAmountRatio3);

							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearMarchRespDto.getCgstARaio3())
									.add(finYearMarchRespDto.getCessARaio3())
									.add(finYearMarchRespDto.getIgstARaio3())
									.add(finYearMarchRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearAprilRespDtos = mapCreditRevFinRatioRespDto
							.get("04");
					if (finYearAprilRespDtos != null
							&& !finYearAprilRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearAprilRespDto : finYearAprilRespDtos) {
							finYearRespRatio1Dto.setAprilRevRatio(
									finYearAprilRespDto.getTaxbleValueRatio1());
							BigDecimal aprilTaxAmountRatio1 = finYearAprilRespDto
									.getCgstARaio1()
									.add(finYearAprilRespDto.getCessARaio1())
									.add(finYearAprilRespDto.getIgstARaio1())
									.add(finYearAprilRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setAprilTaxamount(aprilTaxAmountRatio1);
							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearAprilRespDto.getCgstARaio1())
									.add(finYearAprilRespDto.getCessARaio1())
									.add(finYearAprilRespDto.getIgstARaio1())
									.add(finYearAprilRespDto.getSgstARaio1());

							finYearRespRatio2Dto.setAprilRevRatio(
									finYearAprilRespDto.getTaxbleValueRatio2());
							BigDecimal aprilTaxAmountRatio2 = finYearAprilRespDto
									.getCgstARaio2()
									.add(finYearAprilRespDto.getCessARaio2())
									.add(finYearAprilRespDto.getIgstARaio2())
									.add(finYearAprilRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setAprilTaxamount(aprilTaxAmountRatio2);
							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearAprilRespDto.getCgstARaio2())
									.add(finYearAprilRespDto.getCessARaio2())
									.add(finYearAprilRespDto.getIgstARaio2())
									.add(finYearAprilRespDto.getSgstARaio2());
							finYearRespRatio3Dto.setAprilRevRatio(
									finYearAprilRespDto.getTaxbleValueRatio3());
							BigDecimal aprilTaxAmountRatio3 = finYearAprilRespDto
									.getCgstARaio3()
									.add(finYearAprilRespDto.getCessARaio3())
									.add(finYearAprilRespDto.getIgstARaio3())
									.add(finYearAprilRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setAprilTaxamount(aprilTaxAmountRatio3);

							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearAprilRespDto.getCgstARaio3())
									.add(finYearAprilRespDto.getCessARaio3())
									.add(finYearAprilRespDto.getIgstARaio3())
									.add(finYearAprilRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearMayRespDtos = mapCreditRevFinRatioRespDto
							.get("05");
					if (finYearMayRespDtos != null
							&& !finYearMayRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearMayRespDto : finYearMayRespDtos) {
							finYearRespRatio1Dto.setMayRevRatio(
									finYearMayRespDto.getTaxbleValueRatio1());
							BigDecimal mayTaxAmountRatio1 = finYearMayRespDto
									.getCgstARaio1()
									.add(finYearMayRespDto.getCessARaio1())
									.add(finYearMayRespDto.getIgstARaio1())
									.add(finYearMayRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setMayTaxamount(mayTaxAmountRatio1);

							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearMayRespDto.getCgstARaio1())
									.add(finYearMayRespDto.getCessARaio1())
									.add(finYearMayRespDto.getIgstARaio1())
									.add(finYearMayRespDto.getSgstARaio1());

							finYearRespRatio2Dto.setMayRevRatio(
									finYearMayRespDto.getTaxbleValueRatio2());
							BigDecimal mayTaxAmountRatio2 = finYearMayRespDto
									.getCgstARaio2()
									.add(finYearMayRespDto.getCessARaio2())
									.add(finYearMayRespDto.getIgstARaio2())
									.add(finYearMayRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setMayTaxamount(mayTaxAmountRatio2);

							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearMayRespDto.getCgstARaio2())
									.add(finYearMayRespDto.getCessARaio2())
									.add(finYearMayRespDto.getIgstARaio2())
									.add(finYearMayRespDto.getSgstARaio2());
							finYearRespRatio3Dto.setMayRevRatio(
									finYearMayRespDto.getTaxbleValueRatio3());
							BigDecimal mayTaxAmountRatio3 = finYearMayRespDto
									.getCgstARaio3()
									.add(finYearMayRespDto.getCessARaio3())
									.add(finYearMayRespDto.getIgstARaio3())
									.add(finYearMayRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setMayTaxamount(mayTaxAmountRatio3);

							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearMayRespDto.getCgstARaio3())
									.add(finYearMayRespDto.getCessARaio3())
									.add(finYearMayRespDto.getIgstARaio3())
									.add(finYearMayRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearJuneRespDtos = mapCreditRevFinRatioRespDto
							.get("06");
					if (finYearJuneRespDtos != null
							&& !finYearJuneRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearJuneRespDto : finYearJuneRespDtos) {
							finYearRespRatio1Dto.setJuneRevRatio(
									finYearJuneRespDto.getTaxbleValueRatio1());
							BigDecimal juneTaxAmountRatio1 = finYearJuneRespDto
									.getCgstARaio1()
									.add(finYearJuneRespDto.getCessARaio1())
									.add(finYearJuneRespDto.getIgstARaio1())
									.add(finYearJuneRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setJuneTaxamount(juneTaxAmountRatio1);

							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearJuneRespDto.getCgstARaio1())
									.add(finYearJuneRespDto.getCessARaio1())
									.add(finYearJuneRespDto.getIgstARaio1())
									.add(finYearJuneRespDto.getSgstARaio1());

							finYearRespRatio2Dto.setJuneRevRatio(
									finYearJuneRespDto.getTaxbleValueRatio2());
							BigDecimal juneTaxAmountRatio2 = finYearJuneRespDto
									.getCgstARaio2()
									.add(finYearJuneRespDto.getCessARaio2())
									.add(finYearJuneRespDto.getIgstARaio2())
									.add(finYearJuneRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setJuneTaxamount(juneTaxAmountRatio2);

							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearJuneRespDto.getCgstARaio2())
									.add(finYearJuneRespDto.getCessARaio2())
									.add(finYearJuneRespDto.getIgstARaio2())
									.add(finYearJuneRespDto.getSgstARaio2());

							finYearRespRatio3Dto.setJuneRevRatio(
									finYearJuneRespDto.getTaxbleValueRatio3());
							BigDecimal juneTaxAmountRatio3 = finYearJuneRespDto
									.getCgstARaio3()
									.add(finYearJuneRespDto.getCessARaio3())
									.add(finYearJuneRespDto.getIgstARaio3())
									.add(finYearJuneRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setJuneTaxamount(juneTaxAmountRatio3);
							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearJuneRespDto.getCgstARaio3())
									.add(finYearJuneRespDto.getCessARaio3())
									.add(finYearJuneRespDto.getIgstARaio3())
									.add(finYearJuneRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearJulyRespDtos = mapCreditRevFinRatioRespDto
							.get("07");
					if (finYearJulyRespDtos != null
							&& !finYearJulyRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearJulyRespDto : finYearJulyRespDtos) {
							finYearRespRatio1Dto.setJulyRevRatio(
									finYearJulyRespDto.getTaxbleValueRatio1());
							BigDecimal julyTaxAmountRatio1 = finYearJulyRespDto
									.getCgstARaio1()
									.add(finYearJulyRespDto.getCessARaio1())
									.add(finYearJulyRespDto.getIgstARaio1())
									.add(finYearJulyRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setJulyTaxamount(julyTaxAmountRatio1);

							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearJulyRespDto.getCgstARaio1())
									.add(finYearJulyRespDto.getCessARaio1())
									.add(finYearJulyRespDto.getIgstARaio1())
									.add(finYearJulyRespDto.getSgstARaio1());

							finYearRespRatio2Dto.setJulyRevRatio(
									finYearJulyRespDto.getTaxbleValueRatio2());
							BigDecimal julyTaxAmountRatio2 = finYearJulyRespDto
									.getCgstARaio2()
									.add(finYearJulyRespDto.getCessARaio2())
									.add(finYearJulyRespDto.getIgstARaio2())
									.add(finYearJulyRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setJulyTaxamount(julyTaxAmountRatio2);
							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearJulyRespDto.getCgstARaio2())
									.add(finYearJulyRespDto.getCessARaio2())
									.add(finYearJulyRespDto.getIgstARaio2())
									.add(finYearJulyRespDto.getSgstARaio2());

							finYearRespRatio3Dto.setJulyRevRatio(
									finYearJulyRespDto.getTaxbleValueRatio3());
							BigDecimal julyTaxAmountRatio3 = finYearJulyRespDto
									.getCgstARaio3()
									.add(finYearJulyRespDto.getCessARaio3())
									.add(finYearJulyRespDto.getIgstARaio3())
									.add(finYearJulyRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setJulyTaxamount(julyTaxAmountRatio3);
							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearJulyRespDto.getCgstARaio3())
									.add(finYearJulyRespDto.getCessARaio3())
									.add(finYearJulyRespDto.getIgstARaio3())
									.add(finYearJulyRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearAugRespDtos = mapCreditRevFinRatioRespDto
							.get("08");
					if (finYearAugRespDtos != null
							&& !finYearAugRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearAugRespDto : finYearAugRespDtos) {
							finYearRespRatio1Dto.setAugRevRatio(
									finYearAugRespDto.getTaxbleValueRatio1());
							BigDecimal augTaxAmountRatio1 = finYearAugRespDto
									.getCgstARaio1()
									.add(finYearAugRespDto.getCessARaio1())
									.add(finYearAugRespDto.getIgstARaio1())
									.add(finYearAugRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setAugTaxamount(augTaxAmountRatio1);

							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearAugRespDto.getCgstARaio1())
									.add(finYearAugRespDto.getCessARaio1())
									.add(finYearAugRespDto.getIgstARaio1())
									.add(finYearAugRespDto.getSgstARaio1());

							finYearRespRatio2Dto.setAugRevRatio(
									finYearAugRespDto.getTaxbleValueRatio2());
							BigDecimal augTaxAmountRatio2 = finYearAugRespDto
									.getCgstARaio2()
									.add(finYearAugRespDto.getCessARaio2())
									.add(finYearAugRespDto.getIgstARaio2())
									.add(finYearAugRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setAugTaxamount(augTaxAmountRatio2);
							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearAugRespDto.getCgstARaio2())
									.add(finYearAugRespDto.getCessARaio2())
									.add(finYearAugRespDto.getIgstARaio2())
									.add(finYearAugRespDto.getSgstARaio2());
							finYearRespRatio3Dto.setAugRevRatio(
									finYearAugRespDto.getTaxbleValueRatio3());
							BigDecimal augTaxAmountRatio3 = finYearAugRespDto
									.getCgstARaio3()
									.add(finYearAugRespDto.getCessARaio3())
									.add(finYearAugRespDto.getIgstARaio3())
									.add(finYearAugRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setAugTaxamount(augTaxAmountRatio3);
							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearAugRespDto.getCgstARaio3())
									.add(finYearAugRespDto.getCessARaio3())
									.add(finYearAugRespDto.getIgstARaio3())
									.add(finYearAugRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearSepRespDtos = mapCreditRevFinRatioRespDto
							.get("09");
					if (finYearSepRespDtos != null
							&& !finYearSepRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearSepRespDto : finYearSepRespDtos) {
							finYearRespRatio1Dto.setSepRevRatio(
									finYearSepRespDto.getTaxbleValueRatio1());
							BigDecimal sepTaxAmountRatio1 = finYearSepRespDto
									.getCgstARaio1()
									.add(finYearSepRespDto.getCessARaio1())
									.add(finYearSepRespDto.getIgstARaio1())
									.add(finYearSepRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setSepTaxamount(sepTaxAmountRatio1);

							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearSepRespDto.getCgstARaio1())
									.add(finYearSepRespDto.getCessARaio1())
									.add(finYearSepRespDto.getIgstARaio1())
									.add(finYearSepRespDto.getSgstARaio1());
							finYearRespRatio2Dto.setSepRevRatio(
									finYearSepRespDto.getTaxbleValueRatio2());
							BigDecimal sepTaxAmountRatio2 = finYearSepRespDto
									.getCgstARaio2()
									.add(finYearSepRespDto.getCessARaio2())
									.add(finYearSepRespDto.getIgstARaio2())
									.add(finYearSepRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setSepTaxamount(sepTaxAmountRatio2);

							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearSepRespDto.getCgstARaio2())
									.add(finYearSepRespDto.getCessARaio2())
									.add(finYearSepRespDto.getIgstARaio2())
									.add(finYearSepRespDto.getSgstARaio2());
							finYearRespRatio3Dto.setSepRevRatio(
									finYearSepRespDto.getTaxbleValueRatio3());
							BigDecimal sepTaxAmountRatio3 = finYearSepRespDto
									.getCgstARaio3()
									.add(finYearSepRespDto.getCessARaio3())
									.add(finYearSepRespDto.getIgstARaio3())
									.add(finYearSepRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setSepTaxamount(sepTaxAmountRatio3);
							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearSepRespDto.getCgstARaio3())
									.add(finYearSepRespDto.getCessARaio3())
									.add(finYearSepRespDto.getIgstARaio3())
									.add(finYearSepRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearOctRespDtos = mapCreditRevFinRatioRespDto
							.get("10");
					if (finYearOctRespDtos != null
							&& !finYearOctRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearOctRespDto : finYearOctRespDtos) {
							finYearRespRatio1Dto.setOctRevRatio(
									finYearOctRespDto.getTaxbleValueRatio1());
							BigDecimal octTaxAmountRatio1 = finYearOctRespDto
									.getCgstARaio1()
									.add(finYearOctRespDto.getCessARaio1())
									.add(finYearOctRespDto.getIgstARaio1())
									.add(finYearOctRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setOctTaxamount(octTaxAmountRatio1);

							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearOctRespDto.getCgstARaio1())
									.add(finYearOctRespDto.getCessARaio1())
									.add(finYearOctRespDto.getIgstARaio1())
									.add(finYearOctRespDto.getSgstARaio1());

							finYearRespRatio2Dto.setOctRevRatio(
									finYearOctRespDto.getTaxbleValueRatio2());
							BigDecimal octTaxAmountRatio2 = finYearOctRespDto
									.getCgstARaio2()
									.add(finYearOctRespDto.getCessARaio2())
									.add(finYearOctRespDto.getIgstARaio2())
									.add(finYearOctRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setOctTaxamount(octTaxAmountRatio2);

							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearOctRespDto.getCgstARaio2())
									.add(finYearOctRespDto.getCessARaio2())
									.add(finYearOctRespDto.getIgstARaio2())
									.add(finYearOctRespDto.getSgstARaio2());

							finYearRespRatio3Dto.setOctRevRatio(
									finYearOctRespDto.getTaxbleValueRatio3());
							BigDecimal octTaxAmountRatio3 = finYearOctRespDto
									.getCgstARaio3()
									.add(finYearOctRespDto.getCessARaio3())
									.add(finYearOctRespDto.getIgstARaio3())
									.add(finYearOctRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setOctTaxamount(octTaxAmountRatio3);

							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearOctRespDto.getCgstARaio3())
									.add(finYearOctRespDto.getCessARaio3())
									.add(finYearOctRespDto.getIgstARaio3())
									.add(finYearOctRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearNovRespDtos = mapCreditRevFinRatioRespDto
							.get("11");
					if (finYearNovRespDtos != null
							&& !finYearNovRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearNovRespDto : finYearNovRespDtos) {
							finYearRespRatio1Dto.setNovRevRatio(
									finYearNovRespDto.getTaxbleValueRatio1());
							BigDecimal novTaxamountRatio1 = finYearNovRespDto
									.getCgstARaio1()
									.add(finYearNovRespDto.getCessARaio1())
									.add(finYearNovRespDto.getIgstARaio1())
									.add(finYearNovRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setNovTaxamount(novTaxamountRatio1);

							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearNovRespDto.getCgstARaio1())
									.add(finYearNovRespDto.getCessARaio1())
									.add(finYearNovRespDto.getIgstARaio1())
									.add(finYearNovRespDto.getSgstARaio1());

							finYearRespRatio2Dto.setNovRevRatio(
									finYearNovRespDto.getTaxbleValueRatio2());
							BigDecimal novTaxamountRatio2 = finYearNovRespDto
									.getCgstARaio2()
									.add(finYearNovRespDto.getCessARaio2())
									.add(finYearNovRespDto.getIgstARaio2())
									.add(finYearNovRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setNovTaxamount(novTaxamountRatio2);
							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearNovRespDto.getCgstARaio2())
									.add(finYearNovRespDto.getCessARaio2())
									.add(finYearNovRespDto.getIgstARaio2())
									.add(finYearNovRespDto.getSgstARaio2());
							finYearRespRatio3Dto.setNovRevRatio(
									finYearNovRespDto.getTaxbleValueRatio3());
							BigDecimal novTaxamountRatio3 = finYearNovRespDto
									.getCgstARaio3()
									.add(finYearNovRespDto.getCessARaio3())
									.add(finYearNovRespDto.getIgstARaio3())
									.add(finYearNovRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setNovTaxamount(novTaxamountRatio3);

							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearNovRespDto.getCgstARaio3())
									.add(finYearNovRespDto.getCessARaio3())
									.add(finYearNovRespDto.getIgstARaio3())
									.add(finYearNovRespDto.getSgstARaio3());
						}
					}

					List<CreditRevFinancialRespDto> finYearDecRespDtos = mapCreditRevFinRatioRespDto
							.get("12");
					if (finYearDecRespDtos != null
							&& !finYearDecRespDtos.isEmpty()) {
						for (CreditRevFinancialRespDto finYearDecRespDto : finYearDecRespDtos) {
							finYearRespRatio1Dto.setDecRevRatio(
									finYearDecRespDto.getTaxbleValueRatio1());
							BigDecimal decTaxAmountRaio1 = finYearDecRespDto
									.getCgstARaio1()
									.add(finYearDecRespDto.getCessARaio1())
									.add(finYearDecRespDto.getIgstARaio1())
									.add(finYearDecRespDto.getSgstARaio1());
							finYearRespRatio1Dto
									.setDecTaxamount(decTaxAmountRaio1);

							totalTaxAmountRatio1 = totalTaxAmountRatio1
									.add(finYearDecRespDto.getCgstARaio1())
									.add(finYearDecRespDto.getCessARaio1())
									.add(finYearDecRespDto.getIgstARaio1())
									.add(finYearDecRespDto.getSgstARaio1());

							finYearRespRatio2Dto.setDecRevRatio(
									finYearDecRespDto.getTaxbleValueRatio2());
							BigDecimal decTaxAmountRaio2 = finYearDecRespDto
									.getCgstARaio2()
									.add(finYearDecRespDto.getCessARaio2())
									.add(finYearDecRespDto.getIgstARaio2())
									.add(finYearDecRespDto.getSgstARaio2());
							finYearRespRatio2Dto
									.setDecTaxamount(decTaxAmountRaio2);
							totalTaxAmountRatio2 = totalTaxAmountRatio2
									.add(finYearDecRespDto.getCgstARaio2())
									.add(finYearDecRespDto.getCessARaio2())
									.add(finYearDecRespDto.getIgstARaio2())
									.add(finYearDecRespDto.getSgstARaio2());
							finYearRespRatio3Dto.setDecRevRatio(
									finYearDecRespDto.getTaxbleValueRatio3());
							BigDecimal decTaxAmountRaio3 = finYearDecRespDto
									.getCgstARaio3()
									.add(finYearDecRespDto.getCessARaio3())
									.add(finYearDecRespDto.getIgstARaio3())
									.add(finYearDecRespDto.getSgstARaio3());
							finYearRespRatio3Dto
									.setDecTaxamount(decTaxAmountRaio3);
							totalTaxAmountRatio3 = totalTaxAmountRatio3
									.add(finYearDecRespDto.getCgstARaio3())
									.add(finYearDecRespDto.getCessARaio3())
									.add(finYearDecRespDto.getIgstARaio3())
									.add(finYearDecRespDto.getSgstARaio3());
						}
					}
				}

				finYearRespRatio1Dto.setTotalTaxAmount(totalTaxAmountRatio1);
				finYearRespRatio2Dto.setTotalTaxAmount(totalTaxAmountRatio2);
				finYearRespRatio3Dto.setTotalTaxAmount(totalTaxAmountRatio3);
				finYearRatio1.add(finYearRespRatio1Dto);
				finYearRatio2.add(finYearRespRatio2Dto);
				finYearRatio3.add(finYearRespRatio3Dto);
			}
		}
		finYearFinalRespDto.setRatio1(finYearRatio1);
		finYearFinalRespDto.setRatio2(finYearRatio2);
		finYearFinalRespDto.setRatio3(finYearRatio3);
		return finYearFinalRespDto;
	}

	private List<CreditRevFinancialRespDto> getfinYearProcSumAllRatio(
			List<Object[]> objs) {
		List<CreditRevFinancialRespDto> finaResoDtos = new ArrayList<>();
		if (objs != null && !objs.isEmpty()) {
			for (Object[] obj : objs) {
				CreditRevFinancialRespDto finaResoDto = new CreditRevFinancialRespDto();
				finaResoDto.setTaxPeriod(
						obj[0] != null ? String.valueOf(obj[0]) : null);
				finaResoDto.setGstin(
						obj[1] != null ? String.valueOf(obj[1]) : null);

				finaResoDto.setTaxbleValueRatio1(
						obj[2] != null ? new BigDecimal(String.valueOf(obj[2]))
								: BigDecimal.ZERO);
				finaResoDto.setCgstARaio1(
						obj[3] != null ? new BigDecimal(String.valueOf(obj[3]))
								: BigDecimal.ZERO);
				finaResoDto.setSgstARaio1(
						obj[4] != null ? new BigDecimal(String.valueOf(obj[4]))
								: BigDecimal.ZERO);
				finaResoDto.setIgstARaio1(
						obj[5] != null ? new BigDecimal(String.valueOf(obj[5]))
								: BigDecimal.ZERO);
				finaResoDto.setCessARaio1(
						obj[6] != null ? new BigDecimal(String.valueOf(obj[6]))
								: BigDecimal.ZERO);

				finaResoDto.setTaxbleValueRatio2(
						obj[7] != null ? new BigDecimal(String.valueOf(obj[7]))
								: BigDecimal.ZERO);
				finaResoDto.setCgstARaio2(
						obj[8] != null ? new BigDecimal(String.valueOf(obj[8]))
								: BigDecimal.ZERO);
				finaResoDto.setSgstARaio2(
						obj[9] != null ? new BigDecimal(String.valueOf(obj[9]))
								: BigDecimal.ZERO);
				finaResoDto.setIgstARaio2(obj[10] != null
						? new BigDecimal(String.valueOf(obj[10]))
						: BigDecimal.ZERO);
				finaResoDto.setCessARaio2(obj[11] != null
						? new BigDecimal(String.valueOf(obj[11]))
						: BigDecimal.ZERO);

				finaResoDto.setTaxbleValueRatio3(obj[12] != null
						? new BigDecimal(String.valueOf(obj[12]))
						: BigDecimal.ZERO);
				finaResoDto.setCgstARaio3(obj[13] != null
						? new BigDecimal(String.valueOf(obj[13]))
						: BigDecimal.ZERO);
				finaResoDto.setSgstARaio3(obj[14] != null
						? new BigDecimal(String.valueOf(obj[14]))
						: BigDecimal.ZERO);
				finaResoDto.setIgstARaio3(obj[15] != null
						? new BigDecimal(String.valueOf(obj[15]))
						: BigDecimal.ZERO);
				finaResoDto.setCessARaio3(obj[16] != null
						? new BigDecimal(String.valueOf(obj[16]))
						: BigDecimal.ZERO);

				finaResoDtos.add(finaResoDto);
			}
		}
		return finaResoDtos;
	}

	private Map<String, List<CreditRevFinancialRespDto>> mapCreditRevFinancialRatio(
			List<CreditRevFinancialRespDto> credRevFinRespDtos) {
		Map<String, List<CreditRevFinancialRespDto>> mapCredRevFinancial = new HashMap<>();
		credRevFinRespDtos.forEach(credRevFinRespDto -> {
			String taxPer = credRevFinRespDto.getTaxPeriod();
			if (mapCredRevFinancial.containsKey(taxPer)) {
				List<CreditRevFinancialRespDto> credRevFinancialRespDtos = mapCredRevFinancial
						.get(taxPer);
				credRevFinancialRespDtos.add(credRevFinRespDto);
				mapCredRevFinancial.put(taxPer, credRevFinancialRespDtos);
			} else {
				List<CreditRevFinancialRespDto> credRevFinancialRespDtos = new ArrayList<>();
				credRevFinancialRespDtos.add(credRevFinRespDto);
				mapCredRevFinancial.put(taxPer, credRevFinancialRespDtos);
			}
		});
		return mapCredRevFinancial;
	}

	public List<CreditReversalForFinancialYearDto> getCredReviewReversalSummary(
			final Annexure1SummaryReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessServiceImpl getCredReversal begin");
		}
		List<CreditReversalForFinancialYearDto> creditReversalForFinancialYearDtos = new ArrayList<>();

		List<CreditReversalItemDto> convertObj = convertObjToCredRevrProcess(
				reqDto);
		if (convertObj != null && !convertObj.isEmpty()) {
			Map<String, List<CreditReversalItemDto>> mapCreditRevers = mapCreditReversProcess(
					convertObj);

			totalTaxAmount(mapCreditRevers, creditReversalForFinancialYearDtos);
			commonCreditForInputGoodsA(mapCreditRevers,
					creditReversalForFinancialYearDtos);

			commonCreditForInputServicesB(mapCreditRevers,
					creditReversalForFinancialYearDtos);
			commonCreditC(mapCreditRevers, creditReversalForFinancialYearDtos);

			inEligibleCreditOnITCReversalD(mapCreditRevers,
					creditReversalForFinancialYearDtos);

			netElgCommonCreditE(mapCreditRevers,
					creditReversalForFinancialYearDtos);

			itcReversalRatio(mapCreditRevers,
					creditReversalForFinancialYearDtos);

			ratio1ReversalAmountRule42(mapCreditRevers,
					creditReversalForFinancialYearDtos);
			ratio2ReversalAmountRule42(mapCreditRevers,
					creditReversalForFinancialYearDtos);
			ratio3ReversalAmountRule42(mapCreditRevers,
					creditReversalForFinancialYearDtos);
			creditUsedPartlyforBusiness(mapCreditRevers,
					creditReversalForFinancialYearDtos);
			totalReversalRatio1(mapCreditRevers,
					creditReversalForFinancialYearDtos);
			totalReversalRatio2(mapCreditRevers,
					creditReversalForFinancialYearDtos);
			totalReversalRatio3(mapCreditRevers,
					creditReversalForFinancialYearDtos);

		} else {
			CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();
			processDto.setParticulars(TOTAL_TAX_AMOUNT);
			creditReversalForFinancialYearDtos.add(processDto);

			CreditReversalForFinancialYearDto creditAProcessDto = new CreditReversalForFinancialYearDto();
			creditAProcessDto.setParticulars(COMMON_CREDIT_A);
			creditReversalForFinancialYearDtos.add(creditAProcessDto);

			CreditReversalForFinancialYearDto credAProcessDto = new CreditReversalForFinancialYearDto();
			credAProcessDto.setParticulars(COMMON_CREDIT_B);
			creditReversalForFinancialYearDtos.add(credAProcessDto);

			CreditReversalForFinancialYearDto commCredtProcessDto = new CreditReversalForFinancialYearDto();
			commCredtProcessDto.setParticulars(TOTAL_COMMON_CREDIT);
			creditReversalForFinancialYearDtos.add(commCredtProcessDto);

			CreditReversalForFinancialYearDto useExecluProcessDto = new CreditReversalForFinancialYearDto();
			List<CreditReversalForFinancialYearDto> credRevFinaYearDtos = new ArrayList<>();

			CreditReversalForFinancialYearDto itemDto = new CreditReversalForFinancialYearDto();
			itemDto.setParticulars(T1_USED_EXECLUSIVELY);
			credRevFinaYearDtos.add(itemDto);

			CreditReversalForFinancialYearDto t2UserItemDto = new CreditReversalForFinancialYearDto();
			t2UserItemDto.setParticulars(T2_USED_EXECLUSIVELY);
			credRevFinaYearDtos.add(t2UserItemDto);

			CreditReversalForFinancialYearDto t3ItemDto = new CreditReversalForFinancialYearDto();
			t3ItemDto.setParticulars(T3_CREDIT_IS_NOT);
			credRevFinaYearDtos.add(t3ItemDto);

			CreditReversalForFinancialYearDto t4ItemDto = new CreditReversalForFinancialYearDto();
			t4ItemDto.setParticulars(T4_USED_EXCLUSIVE);
			credRevFinaYearDtos.add(t4ItemDto);

			useExecluProcessDto.setCredRevFinaYearDtos(credRevFinaYearDtos);
			useExecluProcessDto.setParticulars(LESS_INELIGIBLE_CREDIT);
			credRevFinaYearDtos.add(useExecluProcessDto);

			CreditReversalForFinancialYearDto nerEligProcessDto = new CreditReversalForFinancialYearDto();
			nerEligProcessDto.setParticulars(NET_ELIGIGLE_COMMON);
			credRevFinaYearDtos.add(nerEligProcessDto);

			CreditReversalForFinancialYearDto ratio1ProcessDto = new CreditReversalForFinancialYearDto();
			ratio1ProcessDto.setParticulars(RATIO1);
			credRevFinaYearDtos.add(ratio1ProcessDto);
			CreditReversalForFinancialYearDto ratio2ProcessDto = new CreditReversalForFinancialYearDto();
			ratio2ProcessDto.setParticulars(RATIO2);
			credRevFinaYearDtos.add(ratio2ProcessDto);
			CreditReversalForFinancialYearDto ratio3ProcessDto = new CreditReversalForFinancialYearDto();
			ratio3ProcessDto.setParticulars(RATIO3);
			credRevFinaYearDtos.add(ratio3ProcessDto);

			CreditReversalForFinancialYearDto ratiRevProcessDto = new CreditReversalForFinancialYearDto();
			ratiRevProcessDto.setParticulars(RATIO1_REVERSAL_AMOUNT);
			credRevFinaYearDtos.add(ratiRevProcessDto);

			CreditReversalForFinancialYearDto ratiRev2ProcessDto = new CreditReversalForFinancialYearDto();
			ratiRev2ProcessDto.setParticulars(RATIO2_REVERSAL_AMOUNT);
			credRevFinaYearDtos.add(ratiRev2ProcessDto);

			CreditReversalForFinancialYearDto ratiRev3ProcessDto = new CreditReversalForFinancialYearDto();
			ratiRev3ProcessDto.setParticulars(RATIO3_REVERSAL_AMOUNT);
			credRevFinaYearDtos.add(ratiRev3ProcessDto);

			CreditReversalForFinancialYearDto credUsedPartlyProcessDto = new CreditReversalForFinancialYearDto();
			credUsedPartlyProcessDto.setParticulars(CREDIT_USED_PARTLY);
			credRevFinaYearDtos.add(credUsedPartlyProcessDto);

			CreditReversalForFinancialYearDto totalRevAsPerRatio1 = new CreditReversalForFinancialYearDto();
			totalRevAsPerRatio1.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO1);
			credRevFinaYearDtos.add(totalRevAsPerRatio1);

			CreditReversalForFinancialYearDto totalRevAsPerRatio2 = new CreditReversalForFinancialYearDto();
			totalRevAsPerRatio2.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO2);
			credRevFinaYearDtos.add(totalRevAsPerRatio2);

			CreditReversalForFinancialYearDto totalRevAsPerRatio3 = new CreditReversalForFinancialYearDto();
			totalRevAsPerRatio3.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO3);
			credRevFinaYearDtos.add(totalRevAsPerRatio3);

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessServiceImpl getCredReversal end");
		}
		return creditReversalForFinancialYearDtos;
	}

	private void totalTaxAmount(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Total Tax Amount");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);

			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(TOTAL_TAX_AMOUNT);

			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);

			processDtoList.add(credRevForFinYearDto);
		} else {
			CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();
			processDto.setParticulars(TOTAL_TAX_AMOUNT);
			processDtoList.add(processDto);
		}
	}

	private void totalTaxAmount1(BigDecimal totalIgst, BigDecimal totalCgst,
			BigDecimal totalSgst, BigDecimal totalCess,
			BigDecimal totalTotalTax,
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers,
			CreditReversalForFinancialYearDto credRevForFinYearDto) {
		List<CreditReversalItemDto> janRevers = mapReturnPeriodRevers.get("01");
		if (janRevers != null && !janRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : janRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				janTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
		List<CreditReversalItemDto> febRevers = mapReturnPeriodRevers.get("02");
		if (febRevers != null && !febRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : febRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				febTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
		List<CreditReversalItemDto> marchRevers = mapReturnPeriodRevers
				.get("03");
		if (marchRevers != null && !marchRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : marchRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				marchTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
	}

	private void totalTaxAmount2(BigDecimal totalIgst, BigDecimal totalCgst,
			BigDecimal totalSgst, BigDecimal totalCess,
			BigDecimal totalTotalTax,
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers,
			CreditReversalForFinancialYearDto credRevForFinYearDto) {
		List<CreditReversalItemDto> mayRevers = mapReturnPeriodRevers.get("04");
		if (mayRevers != null && !mayRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : mayRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				mayTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
		List<CreditReversalItemDto> sixthRevers = mapReturnPeriodRevers
				.get("05");
		if (sixthRevers != null && !sixthRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : sixthRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				juneTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
		List<CreditReversalItemDto> sevenRevers = mapReturnPeriodRevers
				.get("06");
		if (sevenRevers != null && !sevenRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : sevenRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				julyTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
	}

	private void totalTaxAmount3(BigDecimal totalIgst, BigDecimal totalCgst,
			BigDecimal totalSgst, BigDecimal totalCess,
			BigDecimal totalTotalTax,
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers,
			CreditReversalForFinancialYearDto credRevForFinYearDto) {

		List<CreditReversalItemDto> nineRevers = mapReturnPeriodRevers
				.get("07");
		if (nineRevers != null && !nineRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : nineRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				sepTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
		List<CreditReversalItemDto> tenthRevers = mapReturnPeriodRevers
				.get("08");
		if (tenthRevers != null && !tenthRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : tenthRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				octTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
		List<CreditReversalItemDto> eleventhRevers = mapReturnPeriodRevers
				.get("09");
		if (eleventhRevers != null && !eleventhRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : eleventhRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				novTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}

	}

	private void totalTaxAmount4(BigDecimal totalIgst, BigDecimal totalCgst,
			BigDecimal totalSgst, BigDecimal totalCess,
			BigDecimal totalTotalTax,
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers,
			CreditReversalForFinancialYearDto credRevForFinYearDto) {
		List<CreditReversalItemDto> tenthRevers = mapReturnPeriodRevers
				.get("10");
		if (tenthRevers != null && !tenthRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : tenthRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				octTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
		List<CreditReversalItemDto> eleventhRevers = mapReturnPeriodRevers
				.get("11");
		if (eleventhRevers != null && !eleventhRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : eleventhRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				novTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}

		List<CreditReversalItemDto> twelthRevers = mapReturnPeriodRevers
				.get("12");
		if (twelthRevers != null && !twelthRevers.isEmpty()) {
			for (CreditReversalItemDto itcReverRatioItemDto : twelthRevers) {
				totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
				totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
				totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
				totalCess = totalCess.add(itcReverRatioItemDto.getCess());
				totalTotalTax = totalTotalTax
						.add(itcReverRatioItemDto.getTotalTax());
				decTotalTaxAmount(credRevForFinYearDto, itcReverRatioItemDto);
			}
		}
	}

	private void commonCreditForInputGoodsA(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("A");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(COMMON_CREDIT_A);
			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			processDtoList.add(credRevForFinYearDto);

		} else {
			CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();
			processDto.setParticulars(COMMON_CREDIT_A);
			processDtoList.add(processDto);
		}
	}

	private void commonCreditForInputServicesB(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("B");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(COMMON_CREDIT_B);
			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			processDtoList.add(credRevForFinYearDto);
		} else {
			CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();
			processDto.setParticulars(COMMON_CREDIT_B);
			processDtoList.add(processDto);
		}
	}

	private void commonCreditC(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("C");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(TOTAL_COMMON_CREDIT);
			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			processDtoList.add(credRevForFinYearDto);
		} else {
			CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();
			processDto.setParticulars(TOTAL_COMMON_CREDIT);
			processDtoList.add(processDto);
		}
	}

	private void janTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setJanIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setJanCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setJanSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setJanCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto.setJanTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void febTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setFebIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setFebCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setFebSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setFebCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto.setFebTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void marchTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setMarchIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setMarchCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setMarchSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setMarchCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto
				.setMarchTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void aprilTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setAprilIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setAprilCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setAprilSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setAprilCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto
				.setAprilTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void mayTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setMayIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setMayCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setMaySgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setMayCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto.setMayTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void juneTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setJuneIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setJuneCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setJuneSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setJuneCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto
				.setJuneTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void julyTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setJulyIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setJulyCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setJulySgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setJulyCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto
				.setJulyTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void augTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setAugIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setAugCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setAugSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setAugCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto.setAugTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void sepTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setSepIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setSepCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setSepSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setSepCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto.setSepTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void octTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setOctIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setOctCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setOctSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setOctCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto.setOctTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void novTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setNovIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setNovCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setNovSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setNovCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto.setNovTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void decTotalTaxAmount(
			CreditReversalForFinancialYearDto credRevForFinYearDto,
			CreditReversalItemDto itcReverRatioItemDto) {
		credRevForFinYearDto.setDecIgst(itcReverRatioItemDto.getIgst());
		credRevForFinYearDto.setDecCgst(itcReverRatioItemDto.getCgst());
		credRevForFinYearDto.setDecSgst(itcReverRatioItemDto.getSgst());
		credRevForFinYearDto.setDecCess(itcReverRatioItemDto.getCess());
		credRevForFinYearDto.setDecTotalTax(itcReverRatioItemDto.getTotalTax());
	}

	private void inEligibleCreditOnITCReversalD(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> processDtoList) {

		CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();

		BigDecimal totalIgstJan = BigDecimal.ZERO;
		BigDecimal totalCgstJan = BigDecimal.ZERO;
		BigDecimal totalSgstJan = BigDecimal.ZERO;
		BigDecimal totalCessJan = BigDecimal.ZERO;
		BigDecimal totalTotalTaxJan = BigDecimal.ZERO;

		BigDecimal totalIgstFeb = BigDecimal.ZERO;
		BigDecimal totalCgstFeb = BigDecimal.ZERO;
		BigDecimal totalSgstFeb = BigDecimal.ZERO;
		BigDecimal totalCessFeb = BigDecimal.ZERO;
		BigDecimal totalTotalTaxFeb = BigDecimal.ZERO;

		BigDecimal totalIgstMarch = BigDecimal.ZERO;
		BigDecimal totalCgstMarch = BigDecimal.ZERO;
		BigDecimal totalSgstMarch = BigDecimal.ZERO;
		BigDecimal totalCessMarch = BigDecimal.ZERO;
		BigDecimal totalTotalTaxMarch = BigDecimal.ZERO;

		BigDecimal totalIgstApril = BigDecimal.ZERO;
		BigDecimal totalCgstApril = BigDecimal.ZERO;
		BigDecimal totalSgstApril = BigDecimal.ZERO;
		BigDecimal totalCessApril = BigDecimal.ZERO;
		BigDecimal totalTotalTaxApril = BigDecimal.ZERO;

		BigDecimal totalIgstMay = BigDecimal.ZERO;
		BigDecimal totalCgstMay = BigDecimal.ZERO;
		BigDecimal totalSgstMay = BigDecimal.ZERO;
		BigDecimal totalCessMay = BigDecimal.ZERO;
		BigDecimal totalTotalTaxMay = BigDecimal.ZERO;

		BigDecimal totalIgstJune = BigDecimal.ZERO;
		BigDecimal totalCgstJune = BigDecimal.ZERO;
		BigDecimal totalSgstJune = BigDecimal.ZERO;
		BigDecimal totalCessJune = BigDecimal.ZERO;
		BigDecimal totalTotalTaxJune = BigDecimal.ZERO;

		BigDecimal totalIgstJuly = BigDecimal.ZERO;
		BigDecimal totalCgstJuly = BigDecimal.ZERO;
		BigDecimal totalSgstJuly = BigDecimal.ZERO;
		BigDecimal totalCessJuly = BigDecimal.ZERO;
		BigDecimal totalTotalTaxJuly = BigDecimal.ZERO;

		BigDecimal totalIgstAug = BigDecimal.ZERO;
		BigDecimal totalCgstAug = BigDecimal.ZERO;
		BigDecimal totalSgstAug = BigDecimal.ZERO;
		BigDecimal totalCessAug = BigDecimal.ZERO;
		BigDecimal totalTotalTaxAug = BigDecimal.ZERO;

		BigDecimal totalIgstSep = BigDecimal.ZERO;
		BigDecimal totalCgstSep = BigDecimal.ZERO;
		BigDecimal totalSgstSep = BigDecimal.ZERO;
		BigDecimal totalCessSep = BigDecimal.ZERO;
		BigDecimal totalTotalTaxSep = BigDecimal.ZERO;

		BigDecimal totalIgstOct = BigDecimal.ZERO;
		BigDecimal totalCgstOct = BigDecimal.ZERO;
		BigDecimal totalSgstOct = BigDecimal.ZERO;
		BigDecimal totalCessOct = BigDecimal.ZERO;
		BigDecimal totalTotalTaxOct = BigDecimal.ZERO;

		BigDecimal totalIgstNov = BigDecimal.ZERO;
		BigDecimal totalCgstNov = BigDecimal.ZERO;
		BigDecimal totalSgstNov = BigDecimal.ZERO;
		BigDecimal totalCessNov = BigDecimal.ZERO;
		BigDecimal totalTotalTaxNov = BigDecimal.ZERO;

		BigDecimal totalIgstDec = BigDecimal.ZERO;
		BigDecimal totalCgstDec = BigDecimal.ZERO;
		BigDecimal totalSgstDec = BigDecimal.ZERO;
		BigDecimal totalCessDec = BigDecimal.ZERO;
		BigDecimal totalTotalTaxDec = BigDecimal.ZERO;

		List<CreditReversalForFinancialYearDto> itemDtos = new ArrayList<>();

		List<CreditReversalItemDto> itcReversalDT1Dtos = mapCreditRevers
				.get("D_T1");
		if (itcReversalDT1Dtos != null && !itcReversalDT1Dtos.isEmpty()) {
			CreditReversalForFinancialYearDto itemDto = new CreditReversalForFinancialYearDto();
			itemDto.setParticulars(T1_USED_EXECLUSIVELY);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReversalDT1Dtos);
			List<CreditReversalItemDto> janRevers = mapReturnPeriodRevers
					.get("01");
			BigDecimal totalIgstT1 = BigDecimal.ZERO;
			BigDecimal totalCgstT1 = BigDecimal.ZERO;
			BigDecimal totalSgstT1 = BigDecimal.ZERO;
			BigDecimal totalCessT1 = BigDecimal.ZERO;
			BigDecimal totalTotalTaxT1 = BigDecimal.ZERO;
			if (janRevers != null && !janRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : janRevers) {
					totalIgstJan = totalIgstJan
							.add(itcReverRatioItemDto.getIgst());
					totalCgstJan = totalCgstJan
							.add(itcReverRatioItemDto.getCgst());
					totalSgstJan = totalSgstJan
							.add(itcReverRatioItemDto.getSgst());
					totalCessJan = totalCessJan
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxJan = totalTotalTaxJan
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					janTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> febRevers = mapReturnPeriodRevers
					.get("02");
			if (febRevers != null && !febRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : febRevers) {
					totalIgstFeb = totalIgstFeb
							.add(itcReverRatioItemDto.getIgst());
					totalCgstFeb = totalCgstFeb
							.add(itcReverRatioItemDto.getCgst());
					totalSgstFeb = totalSgstFeb
							.add(itcReverRatioItemDto.getSgst());
					totalCessFeb = totalCessFeb
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxFeb = totalTotalTaxFeb
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					febTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> marchRevers = mapReturnPeriodRevers
					.get("03");
			if (marchRevers != null && !marchRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : marchRevers) {
					totalIgstMarch = totalIgstMarch
							.add(itcReverRatioItemDto.getIgst());
					totalCgstMarch = totalCgstMarch
							.add(itcReverRatioItemDto.getCgst());
					totalSgstMarch = totalSgstMarch
							.add(itcReverRatioItemDto.getSgst());
					totalCessMarch = totalCessMarch
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxMarch = totalTotalTaxMarch
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					marchTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> aprilRevers = mapReturnPeriodRevers
					.get("04");
			if (aprilRevers != null && !aprilRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : aprilRevers) {
					totalIgstApril = totalIgstApril
							.add(itcReverRatioItemDto.getIgst());
					totalCgstApril = totalCgstApril
							.add(itcReverRatioItemDto.getCgst());
					totalSgstApril = totalSgstApril
							.add(itcReverRatioItemDto.getSgst());
					totalCessApril = totalCessApril
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxApril = totalTotalTaxApril
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					aprilTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> mayRevers = mapReturnPeriodRevers
					.get("05");
			if (mayRevers != null && !mayRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : mayRevers) {
					totalIgstMay = totalIgstMay
							.add(itcReverRatioItemDto.getIgst());
					totalCgstMay = totalCgstMay
							.add(itcReverRatioItemDto.getCgst());
					totalSgstMay = totalSgstMay
							.add(itcReverRatioItemDto.getSgst());
					totalCessMay = totalCessMay
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxMay = totalTotalTaxMay
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					mayTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}

			}
			List<CreditReversalItemDto> sixthRevers = mapReturnPeriodRevers
					.get("06");
			if (sixthRevers != null && !sixthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sixthRevers) {
					totalIgstJune = totalIgstJune
							.add(itcReverRatioItemDto.getIgst());
					totalCgstJune = totalCgstJune
							.add(itcReverRatioItemDto.getCgst());
					totalSgstJune = totalSgstJune
							.add(itcReverRatioItemDto.getSgst());
					totalCessJune = totalCessJune
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxJune = totalTotalTaxJune
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					juneTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> sevenRevers = mapReturnPeriodRevers
					.get("07");
			if (sevenRevers != null && !sevenRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sevenRevers) {
					totalIgstJuly = totalIgstJuly
							.add(itcReverRatioItemDto.getIgst());
					totalCgstJuly = totalCgstJuly
							.add(itcReverRatioItemDto.getCgst());
					totalSgstJuly = totalSgstJuly
							.add(itcReverRatioItemDto.getSgst());
					totalCessJuly = totalCessJuly
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxJuly = totalTotalTaxJuly
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					julyTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eightRevers = mapReturnPeriodRevers
					.get("08");
			if (eightRevers != null && !eightRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eightRevers) {
					totalIgstAug = totalIgstAug
							.add(itcReverRatioItemDto.getIgst());
					totalCgstAug = totalCgstAug
							.add(itcReverRatioItemDto.getCgst());
					totalSgstAug = totalSgstAug
							.add(itcReverRatioItemDto.getSgst());
					totalCessAug = totalCessAug
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxAug = totalTotalTaxAug
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					augTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> nineRevers = mapReturnPeriodRevers
					.get("09");
			if (nineRevers != null && !nineRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : nineRevers) {
					totalIgstSep = totalIgstSep
							.add(itcReverRatioItemDto.getIgst());
					totalCgstSep = totalCgstSep
							.add(itcReverRatioItemDto.getCgst());
					totalSgstSep = totalSgstSep
							.add(itcReverRatioItemDto.getSgst());
					totalCessSep = totalCessSep
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxSep = totalTotalTaxSep
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					sepTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> tenthRevers = mapReturnPeriodRevers
					.get("10");
			if (tenthRevers != null && !tenthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : tenthRevers) {
					totalIgstOct = totalIgstOct
							.add(itcReverRatioItemDto.getIgst());
					totalCgstOct = totalCgstOct
							.add(itcReverRatioItemDto.getCgst());
					totalSgstOct = totalSgstOct
							.add(itcReverRatioItemDto.getSgst());
					totalCessOct = totalCessOct
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxOct = totalTotalTaxOct
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					octTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eleventhRevers = mapReturnPeriodRevers
					.get("11");
			if (eleventhRevers != null && !eleventhRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eleventhRevers) {
					totalIgstNov = totalIgstNov
							.add(itcReverRatioItemDto.getIgst());
					totalCgstNov = totalCgstNov
							.add(itcReverRatioItemDto.getCgst());
					totalSgstNov = totalSgstNov
							.add(itcReverRatioItemDto.getSgst());
					totalCessNov = totalCessNov
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxNov = totalTotalTaxNov
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					novTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> twelthRevers = mapReturnPeriodRevers
					.get("12");
			if (twelthRevers != null && !twelthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : twelthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT1 = totalIgstT1
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT1 = totalCgstT1
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT1 = totalSgstT1
							.add(itcReverRatioItemDto.getSgst());
					totalCessT1 = totalCessT1
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT1 = totalTotalTaxT1
							.add(itcReverRatioItemDto.getTotalTax());
					decTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			itemDto.setTotalTotalTax(totalTotalTaxT1);
			itemDto.setTotalIgst(totalIgstT1);
			itemDto.setTotalSgst(totalSgstT1);
			itemDto.setTotalCgst(totalCgstT1);
			itemDto.setTotalCess(totalCessT1);
			itemDtos.add(itemDto);
		} else {
			CreditReversalForFinancialYearDto itemDto = new CreditReversalForFinancialYearDto();
			itemDto.setParticulars(T1_USED_EXECLUSIVELY);
			itemDtos.add(itemDto);
		}

		List<CreditReversalItemDto> itcReversalDT2Dtos = mapCreditRevers
				.get("D_T2");
		if (itcReversalDT2Dtos != null && !itcReversalDT2Dtos.isEmpty()) {
			CreditReversalForFinancialYearDto itemDto = new CreditReversalForFinancialYearDto();
			itemDto.setParticulars(T2_USED_EXECLUSIVELY);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReversalDT2Dtos);
			List<CreditReversalItemDto> janRevers = mapReturnPeriodRevers
					.get("01");
			BigDecimal totalIgstT2 = BigDecimal.ZERO;
			BigDecimal totalCgstT2 = BigDecimal.ZERO;
			BigDecimal totalSgstT2 = BigDecimal.ZERO;
			BigDecimal totalCessT2 = BigDecimal.ZERO;
			BigDecimal totalTotalTaxT2 = BigDecimal.ZERO;
			if (janRevers != null && !janRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : janRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());

					janTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> febRevers = mapReturnPeriodRevers
					.get("02");
			if (febRevers != null && !febRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : febRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					febTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> marchRevers = mapReturnPeriodRevers
					.get("03");
			if (marchRevers != null && !marchRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : marchRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					marchTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> aprilRevers = mapReturnPeriodRevers
					.get("04");
			if (aprilRevers != null && !aprilRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : aprilRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					aprilTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> mayRevers = mapReturnPeriodRevers
					.get("05");
			if (mayRevers != null && !mayRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : mayRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					mayTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}

			}
			List<CreditReversalItemDto> sixthRevers = mapReturnPeriodRevers
					.get("06");
			if (sixthRevers != null && !sixthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sixthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					juneTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> sevenRevers = mapReturnPeriodRevers
					.get("07");
			if (sevenRevers != null && !sevenRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sevenRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					julyTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eightRevers = mapReturnPeriodRevers
					.get("08");
			if (eightRevers != null && !eightRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eightRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					augTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> nineRevers = mapReturnPeriodRevers
					.get("09");
			if (nineRevers != null && !nineRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : nineRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					sepTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> tenthRevers = mapReturnPeriodRevers
					.get("10");
			if (tenthRevers != null && !tenthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : tenthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					octTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eleventhRevers = mapReturnPeriodRevers
					.get("11");
			if (eleventhRevers != null && !eleventhRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eleventhRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					novTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> twelthRevers = mapReturnPeriodRevers
					.get("12");
			if (twelthRevers != null && !twelthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : twelthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT2 = totalIgstT2
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT2 = totalCgstT2
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT2 = totalSgstT2
							.add(itcReverRatioItemDto.getSgst());
					totalCessT2 = totalCessT2
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT2 = totalTotalTaxT2
							.add(itcReverRatioItemDto.getTotalTax());
					decTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			itemDto.setTotalTotalTax(totalTotalTaxT2);
			itemDto.setTotalIgst(totalIgstT2);
			itemDto.setTotalSgst(totalSgstT2);
			itemDto.setTotalCgst(totalCgstT2);
			itemDto.setTotalCess(totalCessT2);
			itemDtos.add(itemDto);
		} else {
			CreditReversalForFinancialYearDto itemDto = new CreditReversalForFinancialYearDto();
			itemDto.setParticulars(T2_USED_EXECLUSIVELY);
			itemDtos.add(itemDto);
		}

		List<CreditReversalItemDto> itcReversalDT3Dtos = mapCreditRevers
				.get("D_T3");
		if (itcReversalDT3Dtos != null && !itcReversalDT3Dtos.isEmpty()) {
			CreditReversalForFinancialYearDto itemDto = new CreditReversalForFinancialYearDto();
			itemDto.setParticulars(T3_CREDIT_IS_NOT);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReversalDT3Dtos);
			List<CreditReversalItemDto> janRevers = mapReturnPeriodRevers
					.get("01");
			BigDecimal totalIgstT3 = BigDecimal.ZERO;
			BigDecimal totalCgstT3 = BigDecimal.ZERO;
			BigDecimal totalSgstT3 = BigDecimal.ZERO;
			BigDecimal totalCessT3 = BigDecimal.ZERO;
			BigDecimal totalTotalTaxT3 = BigDecimal.ZERO;
			if (janRevers != null && !janRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : janRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					janTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> febRevers = mapReturnPeriodRevers
					.get("02");
			if (febRevers != null && !febRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : febRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					febTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> marchRevers = mapReturnPeriodRevers
					.get("03");
			if (marchRevers != null && !marchRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : marchRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					marchTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> aprilRevers = mapReturnPeriodRevers
					.get("04");
			if (aprilRevers != null && !aprilRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : aprilRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					aprilTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> mayRevers = mapReturnPeriodRevers
					.get("05");
			if (mayRevers != null && !mayRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : mayRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					mayTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}

			}
			List<CreditReversalItemDto> sixthRevers = mapReturnPeriodRevers
					.get("06");
			if (sixthRevers != null && !sixthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sixthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					juneTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> sevenRevers = mapReturnPeriodRevers
					.get("07");
			if (sevenRevers != null && !sevenRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sevenRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					julyTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eightRevers = mapReturnPeriodRevers
					.get("08");
			if (eightRevers != null && !eightRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eightRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					augTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> nineRevers = mapReturnPeriodRevers
					.get("09");
			if (nineRevers != null && !nineRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : nineRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					sepTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> tenthRevers = mapReturnPeriodRevers
					.get("10");
			if (tenthRevers != null && !tenthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : tenthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					octTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eleventhRevers = mapReturnPeriodRevers
					.get("11");
			if (eleventhRevers != null && !eleventhRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eleventhRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					novTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> twelthRevers = mapReturnPeriodRevers
					.get("12");
			if (twelthRevers != null && !twelthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : twelthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT3 = totalIgstT3
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT3 = totalCgstT3
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT3 = totalSgstT3
							.add(itcReverRatioItemDto.getSgst());
					totalCessT3 = totalCessT3
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT3 = totalTotalTaxT3
							.add(itcReverRatioItemDto.getTotalTax());
					decTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			itemDto.setTotalTotalTax(totalTotalTaxT3);
			itemDto.setTotalIgst(totalIgstT3);
			itemDto.setTotalSgst(totalSgstT3);
			itemDto.setTotalCgst(totalCgstT3);
			itemDto.setTotalCess(totalCessT3);
			itemDtos.add(itemDto);
		} else {
			CreditReversalForFinancialYearDto itemDto = new CreditReversalForFinancialYearDto();
			itemDto.setParticulars(T3_CREDIT_IS_NOT);
			itemDtos.add(itemDto);
		}

		List<CreditReversalItemDto> itcReversalDT4Dtos = mapCreditRevers
				.get("D_T4");
		if (itcReversalDT4Dtos != null && !itcReversalDT4Dtos.isEmpty()) {
			CreditReversalForFinancialYearDto itemDto = new CreditReversalForFinancialYearDto();
			itemDto.setParticulars(T4_USED_EXCLUSIVE);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReversalDT4Dtos);
			List<CreditReversalItemDto> janRevers = mapReturnPeriodRevers
					.get("01");
			BigDecimal totalIgstT4 = BigDecimal.ZERO;
			BigDecimal totalCgstT4 = BigDecimal.ZERO;
			BigDecimal totalSgstT4 = BigDecimal.ZERO;
			BigDecimal totalCessT4 = BigDecimal.ZERO;
			BigDecimal totalTotalTaxT4 = BigDecimal.ZERO;
			if (janRevers != null && !janRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : janRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					janTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> febRevers = mapReturnPeriodRevers
					.get("02");
			if (febRevers != null && !febRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : febRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					febTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> marchRevers = mapReturnPeriodRevers
					.get("03");
			if (marchRevers != null && !marchRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : marchRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					marchTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> aprilRevers = mapReturnPeriodRevers
					.get("04");
			if (aprilRevers != null && !aprilRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : aprilRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					aprilTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> mayRevers = mapReturnPeriodRevers
					.get("05");
			if (mayRevers != null && !mayRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : mayRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					mayTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}

			}
			List<CreditReversalItemDto> sixthRevers = mapReturnPeriodRevers
					.get("06");
			if (sixthRevers != null && !sixthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sixthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					juneTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> sevenRevers = mapReturnPeriodRevers
					.get("07");
			if (sevenRevers != null && !sevenRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sevenRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					julyTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eightRevers = mapReturnPeriodRevers
					.get("08");
			if (eightRevers != null && !eightRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eightRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					augTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> nineRevers = mapReturnPeriodRevers
					.get("09");
			if (nineRevers != null && !nineRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : nineRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					sepTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> tenthRevers = mapReturnPeriodRevers
					.get("10");
			if (tenthRevers != null && !tenthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : tenthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					octTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eleventhRevers = mapReturnPeriodRevers
					.get("11");
			if (eleventhRevers != null && !eleventhRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eleventhRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					novTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> twelthRevers = mapReturnPeriodRevers
					.get("12");
			if (twelthRevers != null && !twelthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : twelthRevers) {
					totalIgstDec = totalIgstDec
							.add(itcReverRatioItemDto.getIgst());
					totalCgstDec = totalCgstDec
							.add(itcReverRatioItemDto.getCgst());
					totalSgstDec = totalSgstDec
							.add(itcReverRatioItemDto.getSgst());
					totalCessDec = totalCessDec
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxDec = totalTotalTaxDec
							.add(itcReverRatioItemDto.getTotalTax());
					totalIgstT4 = totalIgstT4
							.add(itcReverRatioItemDto.getIgst());
					totalCgstT4 = totalCgstT4
							.add(itcReverRatioItemDto.getCgst());
					totalSgstT4 = totalSgstT4
							.add(itcReverRatioItemDto.getSgst());
					totalCessT4 = totalCessT4
							.add(itcReverRatioItemDto.getCess());
					totalTotalTaxT4 = totalTotalTaxT4
							.add(itcReverRatioItemDto.getTotalTax());
					decTotalTaxAmount(itemDto, itcReverRatioItemDto);
				}
			}
			itemDto.setTotalTotalTax(totalTotalTaxT4);
			itemDto.setTotalIgst(totalIgstT4);
			itemDto.setTotalSgst(totalSgstT4);
			itemDto.setTotalCgst(totalCgstT4);
			itemDto.setTotalCess(totalCessT4);
			itemDtos.add(itemDto);

		} else {
			CreditReversalForFinancialYearDto itemDto = new CreditReversalForFinancialYearDto();
			itemDto.setParticulars(T4_USED_EXCLUSIVE);
			itemDtos.add(itemDto);
		}
		processDto.setCredRevFinaYearDtos(itemDtos);
		processDto.setParticulars(LESS_INELIGIBLE_CREDIT);
		processDto.setJanIgst(totalIgstJan);
		processDto.setJanCgst(totalCgstJan);
		processDto.setJanSgst(totalSgstJan);
		processDto.setJanCess(totalCessJan);
		processDto.setJanTotalTax(totalTotalTaxJan);

		processDto.setFebIgst(totalIgstFeb);
		processDto.setFebCgst(totalCgstFeb);
		processDto.setFebSgst(totalSgstFeb);
		processDto.setFebCess(totalCessFeb);
		processDto.setFebTotalTax(totalTotalTaxFeb);

		processDto.setMarchIgst(totalIgstMarch);
		processDto.setMarchCgst(totalCgstMarch);
		processDto.setMarchSgst(totalSgstMarch);
		processDto.setMarchCess(totalCessMarch);
		processDto.setMarchTotalTax(totalTotalTaxMarch);

		processDto.setAprilIgst(totalIgstApril);
		processDto.setAprilCgst(totalCgstApril);
		processDto.setAprilSgst(totalSgstApril);
		processDto.setAprilCess(totalCessApril);
		processDto.setAprilTotalTax(totalTotalTaxApril);

		processDto.setMayIgst(totalIgstMay);
		processDto.setMayCgst(totalCgstMay);
		processDto.setMaySgst(totalSgstMay);
		processDto.setMayCess(totalCessMay);
		processDto.setMayTotalTax(totalTotalTaxMay);

		processDto.setJuneIgst(totalIgstJune);
		processDto.setJuneCgst(totalCgstJune);
		processDto.setJuneSgst(totalSgstJune);
		processDto.setJuneCess(totalCessJune);
		processDto.setJuneTotalTax(totalTotalTaxJune);

		processDto.setJulyIgst(totalIgstJuly);
		processDto.setJulyCgst(totalCgstJuly);
		processDto.setJulySgst(totalSgstJuly);
		processDto.setJulyCess(totalCessJuly);
		processDto.setJulyTotalTax(totalTotalTaxJuly);

		processDto.setAugIgst(totalIgstAug);
		processDto.setAugCgst(totalCgstAug);
		processDto.setAugSgst(totalSgstAug);
		processDto.setAugCess(totalCessAug);
		processDto.setAugTotalTax(totalTotalTaxAug);

		processDto.setSepIgst(totalIgstSep);
		processDto.setSepCgst(totalCgstSep);
		processDto.setSepSgst(totalSgstSep);
		processDto.setSepCess(totalCessSep);
		processDto.setSepTotalTax(totalTotalTaxSep);

		processDto.setOctIgst(totalIgstOct);
		processDto.setOctCgst(totalCgstOct);
		processDto.setOctSgst(totalSgstOct);
		processDto.setOctCess(totalCessOct);
		processDto.setOctTotalTax(totalTotalTaxOct);

		processDto.setNovIgst(totalIgstNov);
		processDto.setNovCgst(totalCgstNov);
		processDto.setNovSgst(totalSgstNov);
		processDto.setNovCess(totalCessNov);
		processDto.setNovTotalTax(totalTotalTaxNov);

		processDto.setDecIgst(totalIgstDec);
		processDto.setDecCgst(totalCgstDec);
		processDto.setDecSgst(totalSgstDec);
		processDto.setDecCess(totalCessDec);
		processDto.setDecTotalTax(totalTotalTaxDec);

		processDtoList.add(processDto);
	}

	private void netElgCommonCreditE(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> processDtoList) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("E");
		if (itcReverRatioItemDtos != null) {
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(NET_ELIGIGLE_COMMON);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);
			List<CreditReversalItemDto> janRevers = mapReturnPeriodRevers
					.get("01");
			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			if (janRevers != null && !janRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : janRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					janTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> febRevers = mapReturnPeriodRevers
					.get("02");
			if (febRevers != null && !febRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : febRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					febTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> marchRevers = mapReturnPeriodRevers
					.get("03");
			if (marchRevers != null && !marchRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : marchRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					marchTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> aprilRevers = mapReturnPeriodRevers
					.get("04");
			if (aprilRevers != null && !aprilRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : aprilRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					aprilTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> mayRevers = mapReturnPeriodRevers
					.get("05");
			if (mayRevers != null && !mayRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : mayRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					mayTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}

			}
			List<CreditReversalItemDto> sixthRevers = mapReturnPeriodRevers
					.get("06");
			if (sixthRevers != null && !sixthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sixthRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					juneTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> sevenRevers = mapReturnPeriodRevers
					.get("07");
			if (sevenRevers != null && !sevenRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : sevenRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					julyTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eightRevers = mapReturnPeriodRevers
					.get("08");
			if (eightRevers != null && !eightRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eightRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					augTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> nineRevers = mapReturnPeriodRevers
					.get("09");
			if (nineRevers != null && !nineRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : nineRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					sepTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> tenthRevers = mapReturnPeriodRevers
					.get("10");
			if (tenthRevers != null && !tenthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : tenthRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					octTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}
			List<CreditReversalItemDto> eleventhRevers = mapReturnPeriodRevers
					.get("11");
			if (eleventhRevers != null && !eleventhRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : eleventhRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					novTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}

			List<CreditReversalItemDto> twelthRevers = mapReturnPeriodRevers
					.get("12");
			if (twelthRevers != null && !twelthRevers.isEmpty()) {
				for (CreditReversalItemDto itcReverRatioItemDto : twelthRevers) {
					totalIgst = totalIgst.add(itcReverRatioItemDto.getIgst());
					totalCgst = totalCgst.add(itcReverRatioItemDto.getCgst());
					totalSgst = totalSgst.add(itcReverRatioItemDto.getSgst());
					totalCess = totalCess.add(itcReverRatioItemDto.getCess());
					totalTotalTax = totalTotalTax
							.add(itcReverRatioItemDto.getTotalTax());
					decTotalTaxAmount(credRevForFinYearDto,
							itcReverRatioItemDto);
				}
			}
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			processDtoList.add(credRevForFinYearDto);

		} else {
			CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();
			processDto.setParticulars(NET_ELIGIGLE_COMMON);
			processDtoList.add(processDto);
		}
	}

	private void ratio1ReversalAmountRule42(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> creditReversalForFinancialYearDtos) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Ratio_1_R42");
		if (itcReverRatioItemDtos != null) {
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(RATIO1_REVERSAL_AMOUNT);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);
			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			creditReversalForFinancialYearDtos.add(credRevForFinYearDto);

		} else {
			CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();
			processDto.setParticulars(RATIO1_REVERSAL_AMOUNT);
			creditReversalForFinancialYearDtos.add(processDto);
		}
	}

	private void ratio2ReversalAmountRule42(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> creditReversalForFinancialYearDtos) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Ratio_2_R42");

		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(RATIO2_REVERSAL_AMOUNT);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);
			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			creditReversalForFinancialYearDtos.add(credRevForFinYearDto);
		} else {
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(RATIO2_REVERSAL_AMOUNT);
			creditReversalForFinancialYearDtos.add(credRevForFinYearDto);
		}
	}

	private void ratio3ReversalAmountRule42(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> creditReversalForFinancialYearDtos) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Ratio_3_R42");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(RATIO3_REVERSAL_AMOUNT);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);
			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			creditReversalForFinancialYearDtos.add(credRevForFinYearDto);

		} else {
			CreditReversalForFinancialYearDto creditReversalForFinancialYearDto = new CreditReversalForFinancialYearDto();
			creditReversalForFinancialYearDto
					.setParticulars(RATIO3_REVERSAL_AMOUNT);
			creditReversalForFinancialYearDtos
					.add(creditReversalForFinancialYearDto);
		}
	}

	private void totalReversalRatio1(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> creditReversalForFinancialYearDtos) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Total Reversal_RATIO_1");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO1);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);
			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			creditReversalForFinancialYearDtos.add(credRevForFinYearDto);
		} else {
			CreditReversalForFinancialYearDto creditRevForFinYearDto = new CreditReversalForFinancialYearDto();
			creditRevForFinYearDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO1);
			creditReversalForFinancialYearDtos.add(creditRevForFinYearDto);
		}
	}

	private void itcReversalRatio(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> creditReversalForFinancialYearDtos) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get(ITC_REVERSAL_RATIO);
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			CreditReversalForFinancialYearDto processDto1 = new CreditReversalForFinancialYearDto();
			BigDecimal ratio1 = BigDecimal.ZERO;
			BigDecimal ratio2 = BigDecimal.ZERO;
			BigDecimal ratio3 = BigDecimal.ZERO;
			for (CreditReversalItemDto itcReverRatioItemDto : itcReverRatioItemDtos) {
				ratio1 = ratio1.add(itcReverRatioItemDto.getRatio1());
				ratio2 = ratio2.add(itcReverRatioItemDto.getRatio2());
				ratio3 = ratio3.add(itcReverRatioItemDto.getRatio3());
			}
			processDto1
					.setParticulars("Ratio 1 = " + ratio1.longValue() + " %");
			creditReversalForFinancialYearDtos.add(processDto1);
			CreditReversalForFinancialYearDto processDto2 = new CreditReversalForFinancialYearDto();
			processDto2
					.setParticulars("Ratio 2 = " + ratio2.longValue() + " %");
			creditReversalForFinancialYearDtos.add(processDto2);
			CreditReversalForFinancialYearDto processDto3 = new CreditReversalForFinancialYearDto();
			processDto3
					.setParticulars("Ratio 3 = " + ratio3.longValue() + " %");
			creditReversalForFinancialYearDtos.add(processDto3);
		} else {
			CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();
			processDto.setParticulars(RATIO1);
			creditReversalForFinancialYearDtos.add(processDto);
			CreditReversalForFinancialYearDto processDto2 = new CreditReversalForFinancialYearDto();
			processDto2.setParticulars(RATIO2);
			creditReversalForFinancialYearDtos.add(processDto2);
			CreditReversalForFinancialYearDto processDto3 = new CreditReversalForFinancialYearDto();
			processDto3.setParticulars(RATIO3);
			creditReversalForFinancialYearDtos.add(processDto3);
		}
	}

	private void totalReversalRatio2(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> creditReversalForFinancialYearDtos) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Total Reversal_RATIO_2");
		if (itcReverRatioItemDtos != null && !itcReverRatioItemDtos.isEmpty()) {
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO2);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);

			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			creditReversalForFinancialYearDtos.add(credRevForFinYearDto);
		} else {
			CreditReversalForFinancialYearDto creditReveForFinYearDto = new CreditReversalForFinancialYearDto();
			creditReveForFinYearDto
					.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO2);
			creditReversalForFinancialYearDtos.add(creditReveForFinYearDto);
		}
	}

	private void totalReversalRatio3(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> creditReversalForFinancialYearDtos) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("Total Reversal_RATIO_3");
		if (itcReverRatioItemDtos != null) {
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO3);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);
			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			creditReversalForFinancialYearDtos.add(credRevForFinYearDto);

		} else {
			CreditReversalForFinancialYearDto creditRevForFinaYearDto = new CreditReversalForFinancialYearDto();
			creditRevForFinaYearDto
					.setParticulars(TOTAL_REVERSAL_AS_PER_RATIO3);
			creditReversalForFinancialYearDtos.add(creditRevForFinaYearDto);
		}
	}

	private void creditUsedPartlyforBusiness(
			Map<String, List<CreditReversalItemDto>> mapCreditRevers,
			List<CreditReversalForFinancialYearDto> creditReversalForFinancialYearDtos) {
		List<CreditReversalItemDto> itcReverRatioItemDtos = mapCreditRevers
				.get("D2");
		if (itcReverRatioItemDtos != null) {
			CreditReversalForFinancialYearDto credRevForFinYearDto = new CreditReversalForFinancialYearDto();
			credRevForFinYearDto.setParticulars(CREDIT_USED_PARTLY);
			Map<String, List<CreditReversalItemDto>> mapReturnPeriodRevers = mapReturnPeriodReversProcess(
					itcReverRatioItemDtos);

			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;
			BigDecimal totalTotalTax = BigDecimal.ZERO;
			totalTaxAmount1(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount2(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount3(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			totalTaxAmount4(totalIgst, totalCgst, totalSgst, totalCess,
					totalTotalTax, mapReturnPeriodRevers, credRevForFinYearDto);
			credRevForFinYearDto.setTotalIgst(totalIgst);
			credRevForFinYearDto.setTotalCgst(totalCgst);
			credRevForFinYearDto.setTotalSgst(totalSgst);
			credRevForFinYearDto.setTotalCess(totalCess);
			credRevForFinYearDto.setTotalTotalTax(totalTotalTax);
			creditReversalForFinancialYearDtos.add(credRevForFinYearDto);

		} else {
			CreditReversalForFinancialYearDto processDto = new CreditReversalForFinancialYearDto();
			processDto.setParticulars(CREDIT_USED_PARTLY);
			creditReversalForFinancialYearDtos.add(processDto);
		}
	}

	private List<CreditReversalItemDto> convertObjToCredRevrProcess(
			final Annexure1SummaryReqDto reqDto) {
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<CreditReversalItemDto> processItemDtos = new ArrayList<>();
		String gstin = setGstin(dataSecAttrs);
		int fromRetPeriod = GenUtil
				.convertTaxPeriodToInt(reqDto.getFromRetPeriod());
		int toRetPeriod = GenUtil
				.convertTaxPeriodToInt(reqDto.getToRetPeriod());
		List<Object[]> objs = finaYearWiseDaoImpl
				.getReviewReversalFinancialYear(gstin, fromRetPeriod,
						toRetPeriod);
		for (Object[] obj : objs) {
			CreditReversalItemDto processDto = new CreditReversalItemDto();
			processDto
					.setTaxPer(obj[0] != null ? String.valueOf(obj[0]) : null);
			processDto.setParticulars(
					obj[1] != null ? String.valueOf(obj[1]) : null);
			processDto.setTotalTax(obj[2] != null
					? new BigDecimal(String.valueOf(obj[2])) : BigDecimal.ZERO);
			processDto.setIgst(obj[3] != null
					? new BigDecimal(String.valueOf(obj[3])) : BigDecimal.ZERO);
			processDto.setCgst(obj[4] != null
					? new BigDecimal(String.valueOf(obj[4])) : BigDecimal.ZERO);
			processDto.setSgst(obj[5] != null
					? new BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
			processDto.setCess(obj[6] != null
					? new BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);
			processDto.setRatio1(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
			processDto.setRatio2(obj[8] != null
					? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
			processDto.setRatio3(obj[9] != null
					? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
			processItemDtos.add(processDto);
		}
		return processItemDtos;
	}

	private Map<String, List<CreditReversalItemDto>> mapReturnPeriodReversProcess(
			List<CreditReversalItemDto> itemDtos) {
		Map<String, List<CreditReversalItemDto>> mapCredRevTurnItemDto = new HashMap<>();

		itemDtos.forEach(itemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(itemDto.getTaxPer());
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

	public List<CreditTurnOverFinancialItemDto> getFinancialYearCredTurnOverPart1(
			final Annexure1SummaryReqDto reqDto) {

		List<CreditTurnOverFinancialItemDto> processDtoList = new ArrayList<>();

		List<CreditTurnOverItemDto> convertObj = convertObjToCredTurnOver(
				reqDto);
		if (convertObj != null && !convertObj.isEmpty()) {
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers = mapCreditTurnOver(
					convertObj);
			taxableSuppliesTax(mapCreditRevers, processDtoList);
			exmptSupplies(mapCreditRevers, processDtoList);
			lessExpRule42(processDtoList);
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
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		List<CreditTurnOverFinancialItemDto> itemDtos = new ArrayList<>();
		CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
		itemDto.setSno("1");
		itemDto.setTurnoverComp(TAXABLE_NORMAL);
		itemDtos.add(itemDto);

		CreditTurnOverFinancialItemDto b2csVertUpladItemDto = new CreditTurnOverFinancialItemDto();
		b2csVertUpladItemDto.setSno("1A");
		b2csVertUpladItemDto.setTurnoverComp(TAXABLE_B2CS_VERTICAL);
		itemDtos.add(b2csVertUpladItemDto);

		CreditTurnOverFinancialItemDto sezDtaItemDto = new CreditTurnOverFinancialItemDto();
		sezDtaItemDto.setSno("2");
		sezDtaItemDto.setTurnoverComp(SEZ_DTA);
		itemDtos.add(sezDtaItemDto);

		CreditTurnOverFinancialItemDto dxpItemDto = new CreditTurnOverFinancialItemDto();
		dxpItemDto.setSno("3");
		dxpItemDto.setTurnoverComp("DXP");
		itemDtos.add(dxpItemDto);

		CreditTurnOverFinancialItemDto exptItemDto = new CreditTurnOverFinancialItemDto();
		exptItemDto.setSno("4");
		exptItemDto.setTurnoverComp("EXPT");
		itemDtos.add(exptItemDto);

		CreditTurnOverFinancialItemDto expwtItemDto = new CreditTurnOverFinancialItemDto();
		expwtItemDto.setSno("5");
		expwtItemDto.setTurnoverComp(EXPWT);
		itemDtos.add(expwtItemDto);

		CreditTurnOverFinancialItemDto dtaSezDtaItemDto = new CreditTurnOverFinancialItemDto();
		dtaSezDtaItemDto.setSno("6");
		dtaSezDtaItemDto.setTurnoverComp(DTA_SEZ_DTA);
		itemDtos.add(dtaSezDtaItemDto);
		processDto.setItems(itemDtos);

		processDtoList.add(processDto);
	}

	private void defultValuesForExmptSupplies(
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		List<CreditTurnOverFinancialItemDto> itemDtos = new ArrayList<>();

		CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
		itemDto.setSno("7");
		itemDto.setTurnoverComp(EXEMPT);
		itemDtos.add(itemDto);

		CreditTurnOverFinancialItemDto nilRatedItemDto = new CreditTurnOverFinancialItemDto();
		nilRatedItemDto.setSno("8");
		nilRatedItemDto.setTurnoverComp(NIL_RATED);
		itemDtos.add(nilRatedItemDto);

		CreditTurnOverFinancialItemDto nilRatedB2CItemDto = new CreditTurnOverFinancialItemDto();
		nilRatedB2CItemDto.setSno("8A");
		nilRatedB2CItemDto.setTurnoverComp(NIL_RATED_B2CS_VERTICLE_UPLOAD);
		itemDtos.add(nilRatedB2CItemDto);

		CreditTurnOverFinancialItemDto nonGstSceItemDto = new CreditTurnOverFinancialItemDto();
		nonGstSceItemDto.setSno("9");
		nonGstSceItemDto.setTurnoverComp(NON_GST_SCHEDULE);
		itemDtos.add(itemDto);

		processDto.setItems(itemDtos);
		processDto.setTurnoverComp("Exempt Supplies (B)");
		processDtoList.add(processDto);
	}

	private void taxableSuppliesTax(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		List<CreditTurnOverFinancialItemDto> itemDtos = new ArrayList<>();
		BigDecimal jan = BigDecimal.ZERO;
		BigDecimal feb = BigDecimal.ZERO;
		BigDecimal march = BigDecimal.ZERO;
		BigDecimal april = BigDecimal.ZERO;
		BigDecimal may = BigDecimal.ZERO;
		BigDecimal june = BigDecimal.ZERO;
		BigDecimal july = BigDecimal.ZERO;
		BigDecimal aug = BigDecimal.ZERO;
		BigDecimal sep = BigDecimal.ZERO;
		BigDecimal oct = BigDecimal.ZERO;
		BigDecimal nov = BigDecimal.ZERO;
		BigDecimal dec = BigDecimal.ZERO;
		if (mapCreditRevers != null && !mapCreditRevers.isEmpty()) {
			List<CreditTurnOverItemDto> overTaxItemDtos = mapCreditRevers
					.get("TAX");
			if (overTaxItemDtos != null && !overTaxItemDtos.isEmpty()) {
				CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
				itemDto.setSno("1");
				itemDto.setTurnoverComp(TAXABLE_NORMAL);
				Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
						overTaxItemDtos);
				List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
						.get("01");
				if (turnOverItemJanDtos != null
						&& !turnOverItemJanDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
						itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
						jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
					}
				}
				List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
						.get("02");
				if (turnOverItemFebDtos != null
						&& !turnOverItemFebDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
						itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
						feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
						.get("03");
				if (turnOverItemMarchDtos != null
						&& !turnOverItemMarchDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
						itemDto.setMarch(
								turnOverItemMarchDto.getDigigstAutoComp());
						march = march
								.add(turnOverItemMarchDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
						.get("04");
				if (turnOverItemAprilDtos != null
						&& !turnOverItemAprilDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
						itemDto.setApril(
								turnOverItemAprilDto.getDigigstAutoComp());
						april = april
								.add(turnOverItemAprilDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
						.get("05");
				if (turnOverItemMayDtos != null
						&& !turnOverItemMayDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
						itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
						may = may.add(turnOverItemMayDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
						.get("06");
				if (turnOverItemJuneDtos != null
						&& !turnOverItemJuneDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
						itemDto.setJune(
								turnOverItemJuneDto.getDigigstAutoComp());
						june = june
								.add(turnOverItemJuneDto.getDigigstAutoComp());
					}
				}
				List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
						.get("07");
				if (turnOverItemJulyDtos != null
						&& !turnOverItemJulyDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
						itemDto.setJuly(
								turnOverItemJulyDto.getDigigstAutoComp());
						july = july
								.add(turnOverItemJulyDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
						.get("08");
				if (turnOverItemAugDtos != null
						&& !turnOverItemAugDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
						itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
						aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
						.get("09");
				if (turnOverItemSepDtos != null
						&& !turnOverItemSepDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
						itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
						sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
						.get("10");
				if (turnOverItemOctDtos != null
						&& !turnOverItemOctDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
						itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
						oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
						.get("11");
				if (turnOverItemNovDtos != null
						&& !turnOverItemNovDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
						itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
						nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
						.get("12");
				if (turnOverItemDecDtos != null
						&& !turnOverItemDecDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
						itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
						dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
					}
				}
				itemDtos.add(itemDto);
			} else {
				CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
				itemDto.setSno("1");
				itemDto.setTurnoverComp(TAXABLE_NORMAL);
				itemDtos.add(itemDto);
			}
			List<CreditTurnOverItemDto> overB2csItemDtos = mapCreditRevers
					.get("B2CS <>0");
			if (overB2csItemDtos != null && !overB2csItemDtos.isEmpty()) {
				CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
				itemDto.setSno("1A");
				itemDto.setTurnoverComp(TAXABLE_B2CS_VERTICAL);
				Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
						overTaxItemDtos);
				List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
						.get("01");
				if (turnOverItemJanDtos != null
						&& !turnOverItemJanDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
						itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
						jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
					}
				}
				List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
						.get("02");
				if (turnOverItemFebDtos != null
						&& !turnOverItemFebDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
						itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
						feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
						.get("03");
				if (turnOverItemMarchDtos != null
						&& !turnOverItemMarchDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
						itemDto.setMarch(
								turnOverItemMarchDto.getDigigstAutoComp());
						march = march
								.add(turnOverItemMarchDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
						.get("04");
				if (turnOverItemAprilDtos != null
						&& !turnOverItemAprilDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
						itemDto.setApril(
								turnOverItemAprilDto.getDigigstAutoComp());
						april = april
								.add(turnOverItemAprilDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
						.get("05");
				if (turnOverItemMayDtos != null
						&& !turnOverItemMayDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
						itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
						may = may.add(turnOverItemMayDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
						.get("06");
				if (turnOverItemJuneDtos != null
						&& !turnOverItemJuneDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
						itemDto.setJune(
								turnOverItemJuneDto.getDigigstAutoComp());
						june = june
								.add(turnOverItemJuneDto.getDigigstAutoComp());
					}
				}
				List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
						.get("07");
				if (turnOverItemJulyDtos != null
						&& !turnOverItemJulyDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
						itemDto.setJuly(
								turnOverItemJulyDto.getDigigstAutoComp());
						july = july
								.add(turnOverItemJulyDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
						.get("08");
				if (turnOverItemAugDtos != null
						&& !turnOverItemAugDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
						itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
						aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
						.get("09");
				if (turnOverItemSepDtos != null
						&& !turnOverItemSepDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
						itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
						sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
						.get("10");
				if (turnOverItemOctDtos != null
						&& !turnOverItemOctDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
						itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
						oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
						.get("11");
				if (turnOverItemNovDtos != null
						&& !turnOverItemNovDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
						itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
						nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
					}
				}

				List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
						.get("12");
				if (turnOverItemDecDtos != null
						&& !turnOverItemDecDtos.isEmpty()) {
					for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemNovDtos) {
						itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
						dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
					}
				}
				itemDtos.add(itemDto);
			}
		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("1A");
			itemDto.setTurnoverComp(TAXABLE_B2CS_VERTICAL);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overSezItemDtos = mapCreditRevers
				.get("SEZ");
		if (overSezItemDtos != null && !overSezItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("2");
			itemDto.setTurnoverComp(SEZ_DTA);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overSezItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemNovDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);

		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("2");
			itemDto.setTurnoverComp(SEZ_DTA);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overDxpItemDtos = mapCreditRevers
				.get("DXP");
		if (overDxpItemDtos != null && !overDxpItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("3");
			itemDto.setTurnoverComp("DXP");
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overDxpItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemNovDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);

		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("3");
			itemDto.setTurnoverComp("DXP");
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overExptItemDtos = mapCreditRevers
				.get("EXPT");
		if (overExptItemDtos != null && !overExptItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("4");
			itemDto.setTurnoverComp("EXPT");
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overExptItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("4");
			itemDto.setTurnoverComp("EXPT");
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overExpwtItemDtos = mapCreditRevers
				.get(EXPWT);
		if (overExpwtItemDtos != null && !overExpwtItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("5");
			itemDto.setTurnoverComp(EXPWT);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overExpwtItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("5");
			itemDto.setTurnoverComp(EXPWT);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overDtaItemDtos = mapCreditRevers
				.get("DTA");
		if (overDtaItemDtos != null && !overDtaItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("6");
			itemDto.setTurnoverComp(DTA_SEZ_DTA);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overDtaItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);

		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("6");
			itemDto.setTurnoverComp(DTA_SEZ_DTA);
			itemDtos.add(itemDto);
		}
		processDto.setItems(itemDtos);
		processDto.setTurnoverComp("Taxable Supplies (A)");
		processDto.setJan(jan);
		processDto.setFeb(feb);
		processDto.setMarch(march);
		processDto.setApril(april);
		processDto.setMay(may);
		processDto.setJune(june);
		processDto.setJuly(july);
		processDto.setAug(aug);
		processDto.setSep(sep);
		processDto.setOct(oct);
		processDto.setNov(nov);
		processDto.setDec(dec);
		processDtoList.add(processDto);
	}

	private void exmptSupplies(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		List<CreditTurnOverFinancialItemDto> itemDtos = new ArrayList<>();

		BigDecimal jan = BigDecimal.ZERO;
		BigDecimal feb = BigDecimal.ZERO;
		BigDecimal march = BigDecimal.ZERO;
		BigDecimal april = BigDecimal.ZERO;
		BigDecimal may = BigDecimal.ZERO;
		BigDecimal june = BigDecimal.ZERO;
		BigDecimal july = BigDecimal.ZERO;
		BigDecimal aug = BigDecimal.ZERO;
		BigDecimal sep = BigDecimal.ZERO;
		BigDecimal oct = BigDecimal.ZERO;
		BigDecimal nov = BigDecimal.ZERO;
		BigDecimal dec = BigDecimal.ZERO;
		List<CreditTurnOverItemDto> overExtItemDtos = mapCreditRevers.get(EXT);
		if (overExtItemDtos != null && !overExtItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("7");
			itemDto.setTurnoverComp(EXEMPT);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overExtItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("7");
			itemDto.setTurnoverComp(EXEMPT);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overNilItemDtos = mapCreditRevers.get(NIL);
		if (overNilItemDtos != null && !overNilItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("8");
			itemDto.setTurnoverComp(NIL_RATED);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overNilItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);

		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("8");
			itemDto.setTurnoverComp(NIL_RATED);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNilRatedB2CSItemDtos = mapCreditRevers
				.get(SECTION_B2CS);
		if (overNilRatedB2CSItemDtos != null
				&& !overNilRatedB2CSItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("8A");
			itemDto.setTurnoverComp(NIL_RATED_B2CS_VERTICLE_UPLOAD);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overNilItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);

		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("8A");
			itemDto.setTurnoverComp(NIL_RATED_B2CS_VERTICLE_UPLOAD);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overNonItemDtos = mapCreditRevers
				.get(SECTION_NON_GST_SCH3);
		if (overNonItemDtos != null && !overNonItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("9");
			itemDto.setTurnoverComp(NON_GST_SCHEDULE);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overNilItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("9");
			itemDto.setTurnoverComp(NON_GST_SCHEDULE);
			itemDtos.add(itemDto);
		}
		processDto.setItems(itemDtos);
		processDto.setTurnoverComp("Exempt Supplies (B)");
		processDto.setJan(jan);
		processDto.setFeb(feb);
		processDto.setMarch(march);
		processDto.setApril(april);
		processDto.setMay(may);
		processDto.setJune(june);
		processDto.setJuly(july);
		processDto.setAug(aug);
		processDto.setSep(sep);
		processDto.setOct(oct);
		processDto.setNov(nov);
		processDto.setDec(dec);
		processDtoList.add(processDto);
	}

	private void lessExpRule42(
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		processDto.setSno("10");
		processDto.setTurnoverComp("Less : (Explanation to Rule 42)");
		processDtoList.add(processDto);
	}

	private void defultValuesForLessExpRule42(
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		processDto.setSno("10");
		processDto.setTurnoverComp("Less : (Explanation to Rule 42)");
		processDtoList.add(processDto);
	}

	private void totalGstinSupp10(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		List<CreditTurnOverItemDto> itemDtos = mapCreditRevers
				.get(TOTAL_GSTIN_SUPPLIES2);

		if (itemDtos != null && !itemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("11");
			itemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					itemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			processDtoList.add(itemDto);

		} else {
			CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
			processDto.setSno("11");
			processDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES);
			processDtoList.add(processDto);
		}
	}

	private void defultValuesForTotalGstinSupp10(
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		processDto.setSno("11");
		processDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES);
		processDtoList.add(processDto);

	}

	private void reverseChargeSupplies(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		List<CreditTurnOverFinancialItemDto> itemDtos = new ArrayList<>();

		BigDecimal jan = BigDecimal.ZERO;
		BigDecimal feb = BigDecimal.ZERO;
		BigDecimal march = BigDecimal.ZERO;
		BigDecimal april = BigDecimal.ZERO;
		BigDecimal may = BigDecimal.ZERO;
		BigDecimal june = BigDecimal.ZERO;
		BigDecimal july = BigDecimal.ZERO;
		BigDecimal aug = BigDecimal.ZERO;
		BigDecimal sep = BigDecimal.ZERO;
		BigDecimal oct = BigDecimal.ZERO;
		BigDecimal nov = BigDecimal.ZERO;
		BigDecimal dec = BigDecimal.ZERO;
		List<CreditTurnOverItemDto> overTaxReveCharItemDtos = mapCreditRevers
				.get("TAX_REVERSE_CHARGE");
		if (overTaxReveCharItemDtos != null
				&& !overTaxReveCharItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("12");
			itemDto.setTurnoverComp(TAXABLE_NORMAL_REVERSE_CHARGE);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overTaxReveCharItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("12");
			itemDto.setTurnoverComp(TAXABLE_NORMAL_REVERSE_CHARGE);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overDataRevChargeItemDtos = mapCreditRevers
				.get("DTA_REVERSE_CHARGE");
		if (overDataRevChargeItemDtos != null
				&& !overDataRevChargeItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("13");
			itemDto.setTurnoverComp(DTA_SEZ_DTA);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overDataRevChargeItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("13");
			itemDto.setTurnoverComp(DTA_SEZ_DTA);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overDeemedExportsItemDtos = mapCreditRevers
				.get("DXP_REVERSE_CHARGE");
		if (overDeemedExportsItemDtos != null
				&& !overDeemedExportsItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("14");
			itemDto.setTurnoverComp(DEEMED_EXPORTS);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overDeemedExportsItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("14");
			itemDto.setTurnoverComp(DEEMED_EXPORTS);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> oversSezRevChargeItemDtos = mapCreditRevers
				.get("SEZ_REVERSE_CHARGE");
		if (oversSezRevChargeItemDtos != null
				&& !oversSezRevChargeItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("15");
			itemDto.setTurnoverComp("SEZ (DTA to SEZ)");
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					oversSezRevChargeItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					jan = jan.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					feb = feb.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					march = march
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					april = april
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					may = may.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					june = june.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					july = july.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					aug = aug.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					sep = sep.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					oct = oct.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					nov = nov.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					dec = dec.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
			itemDto.setSno("15");
			itemDto.setTurnoverComp("SEZ (DTA to SEZ)");
			itemDtos.add(itemDto);
		}
		processDto.setItems(itemDtos);
		processDto.setTurnoverComp("Reverse Charge Supplies (C )");
		processDto.setJan(jan);
		processDto.setFeb(feb);
		processDto.setMarch(march);
		processDto.setApril(april);
		processDto.setMay(may);
		processDto.setJune(june);
		processDto.setJuly(july);
		processDto.setAug(aug);
		processDto.setSep(sep);
		processDto.setOct(oct);
		processDto.setNov(nov);
		processDto.setDec(dec);
		processDtoList.add(processDto);
	}

	private void defultValuesForReverseChargeSupplies(
			List<CreditTurnOverFinancialItemDto> processDtoList) {
		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		List<CreditTurnOverFinancialItemDto> itemDtos = new ArrayList<>();

		CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
		itemDto.setSno("12");
		itemDto.setTurnoverComp(TAXABLE_NORMAL_REVERSE_CHARGE);
		itemDtos.add(itemDto);

		CreditTurnOverFinancialItemDto dtaSezItemDto = new CreditTurnOverFinancialItemDto();
		dtaSezItemDto.setSno("13");
		dtaSezItemDto.setTurnoverComp(DTA_SEZ_DTA);
		itemDtos.add(dtaSezItemDto);

		CreditTurnOverFinancialItemDto deemedExpoItemDto = new CreditTurnOverFinancialItemDto();
		deemedExpoItemDto.setSno("14");
		deemedExpoItemDto.setTurnoverComp(DEEMED_EXPORTS);
		itemDtos.add(deemedExpoItemDto);

		CreditTurnOverFinancialItemDto sezDtaItemDto = new CreditTurnOverFinancialItemDto();
		sezDtaItemDto.setSno("15");
		sezDtaItemDto.setTurnoverComp("SEZ (DTA to SEZ)");
		itemDtos.add(sezDtaItemDto);

		processDto.setItems(itemDtos);
		processDto.setTurnoverComp("Reverse Charge Supplies (C )");
		processDtoList.add(processDto);
	}

	private void overOthersD(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverFinancialItemDto> processDtoList) {

		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		List<CreditTurnOverFinancialItemDto> otherDitemDtos = new ArrayList<>();

		CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
		itemDto.setSno("17");
		itemDto.setTurnoverComp("NSY - Non Supply Transactions");
		otherDitemDtos.add(itemDto);

		processDto.setItems(otherDitemDtos);
		processDto.setTurnoverComp("Others (D)");
		processDtoList.add(processDto);
		List<CreditTurnOverItemDto> overTotalGstinItemDtos = mapCreditRevers
				.get(TOTAL_GSTIN_SUPPLIES2);
		if (overTotalGstinItemDtos != null
				&& !overTotalGstinItemDtos.isEmpty()) {
			CreditTurnOverFinancialItemDto creditTurnOverDto = new CreditTurnOverFinancialItemDto();
			creditTurnOverDto.setSno("18");
			creditTurnOverDto.setTurnoverComp(GRAND_TOTAL_FOR_CONTROLS);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overTotalGstinItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			processDtoList.add(creditTurnOverDto);
		} else {
			CreditTurnOverFinancialItemDto creditTurnOverDto = new CreditTurnOverFinancialItemDto();
			creditTurnOverDto.setSno("18");
			creditTurnOverDto.setTurnoverComp(GRAND_TOTAL_FOR_CONTROLS);
			processDtoList.add(creditTurnOverDto);
		}

	}

	private void defultValuesForOverOthersD(
			List<CreditTurnOverFinancialItemDto> processDtoList) {

		CreditTurnOverFinancialItemDto processDto = new CreditTurnOverFinancialItemDto();
		List<CreditTurnOverFinancialItemDto> otherDitemDtos = new ArrayList<>();

		CreditTurnOverFinancialItemDto itemDto = new CreditTurnOverFinancialItemDto();
		itemDto.setSno("17");
		itemDto.setTurnoverComp("NSY - Non Supply Transactions");
		otherDitemDtos.add(itemDto);
		processDto.setItems(otherDitemDtos);
		processDto.setTurnoverComp("Others (D)");
		processDtoList.add(processDto);

		CreditTurnOverFinancialItemDto creditTurnOverDto = new CreditTurnOverFinancialItemDto();
		creditTurnOverDto.setSno("18");
		creditTurnOverDto.setTurnoverComp(GRAND_TOTAL_FOR_CONTROLS);
		processDtoList.add(creditTurnOverDto);

	}

	private void itcReversalRatioForTurnOver(
			List<CreditTurnOverFinancialProcess2Dto> processDtoList) {
		CreditTurnOverFinancialProcess2Dto processDto = new CreditTurnOverFinancialProcess2Dto();
		processDto.setTurnoverComp(ITC_REVERSAL_RATIO);
		processDtoList.add(processDto);
	}

	private void ratio1ForTurnOver(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverFinancialProcess2Dto> processDtoList) {

		CreditTurnOverFinancialProcess2Dto processDto = new CreditTurnOverFinancialProcess2Dto();

		List<CreditTurnOverItemDto> overItcRevItemDtos = mapCreditRevers
				.get(ITC_REVERSAL_RATIO);

		if (overItcRevItemDtos != null && !overItcRevItemDtos.isEmpty()) {
			processDto.setTurnoverComp(RATIO1_PERC);
			BigDecimal total = BigDecimal.ZERO;
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overItcRevItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					processDto.setJan(String.valueOf(
							turnOverItemJanDto.getRatio1()) + " %");
					total = total.add(turnOverItemJanDto.getRatio1());
				}
			} else {
				processDto.setJan(String.valueOf(BigDecimal.ZERO) + " %");
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					processDto.setFeb(String.valueOf(
							turnOverItemFebDto.getRatio1()) + " %");
					total = total.add(turnOverItemFebDto.getRatio1());
				}
			} else {
				processDto.setFeb(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					processDto.setMarch(String.valueOf(
							turnOverItemMarchDto.getRatio1()) + " %");
					total = total
							.add(turnOverItemMarchDto.getRatio1());
				}
			} else {
				processDto.setMarch(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					processDto.setApril(String.valueOf(
							turnOverItemAprilDto.getRatio1()) + " %");
					total = total
							.add(turnOverItemAprilDto.getRatio1());
				}
			} else {
				processDto.setApril(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					processDto.setMay(String.valueOf(
							turnOverItemMayDto.getRatio1()) + " %");
					total = total.add(turnOverItemMayDto.getRatio1());
				}
			} else {
				processDto.setMay(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					processDto.setJune(String.valueOf(
							turnOverItemJuneDto.getRatio1()) + " %");
					total = total.add(turnOverItemJuneDto.getRatio1());
				}
			} else {
				processDto.setJune(String.valueOf(BigDecimal.ZERO) + " %");
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					processDto.setJuly(String.valueOf(
							turnOverItemJulyDto.getRatio1()) + " %");
					total = total.add(turnOverItemJulyDto.getRatio1());
				}
			} else {
				processDto.setJuly(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					processDto.setAug(String.valueOf(
							turnOverItemAugDto.getRatio1()) + " %");
					total = total.add(turnOverItemAugDto.getRatio1());
				}
			} else {
				processDto.setAug(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					processDto.setSep(String.valueOf(
							turnOverItemSepDto.getRatio1()) + " %");
					total = total.add(turnOverItemSepDto.getRatio1());
				}
			} else {
				processDto.setSep(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					processDto.setOct(String.valueOf(
							turnOverItemOctDto.getRatio1()) + " %");
					total = total.add(turnOverItemOctDto.getRatio1());
				}
			} else {
				processDto.setOct(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					processDto.setNov(String.valueOf(
							turnOverItemNovDto.getRatio1()) + " %");
					total = total.add(turnOverItemNovDto.getRatio1());
				}
			} else {
				processDto.setNov(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					processDto.setDec(String.valueOf(
							turnOverItemDecDto.getRatio1()) + " %");
					total = total.add(turnOverItemDecDto.getRatio1());
				}
			} else {
				processDto.setDec(String.valueOf(BigDecimal.ZERO) + " %");
			}
			processDto.setTotal(String.valueOf(total) + " %");
		} else {
			processDto.setTurnoverComp(RATIO1_PERC);
			processDto.setTotal(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setJan(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setFeb(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setMarch(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setApril(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setMay(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setJune(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setJuly(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setAug(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setSep(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setOct(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setNov(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setDec(String.valueOf(BigDecimal.ZERO) + " %");
		}

		List<CreditTurnOverFinancialItem2Dto> itemDtos = new ArrayList<>();
		List<CreditTurnOverItemDto> overExtItemDtos = mapCreditRevers.get(EXT);
		if (overExtItemDtos != null && !overExtItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(EXEMPT7);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overExtItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(EXEMPT7);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNilItemDtos = mapCreditRevers.get(NIL);
		if (overNilItemDtos != null && !overNilItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(NIL_RATED8);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overNilItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);

		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(NIL_RATED8);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overTtlItemDtos = mapCreditRevers
				.get(SECTION_TTL_RCS);
		if (overTtlItemDtos != null && !overTtlItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overTtlItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);

		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
			itemDtos.add(itemDto);
		}
		CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
		itemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(itemDto);

		List<CreditTurnOverItemDto> overTotalGstinItemDtos = mapCreditRevers
				.get("T_GSTIN_SUPPLIES");
		if (overTotalGstinItemDtos != null
				&& !overTotalGstinItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto overItemDto = new CreditTurnOverFinancialItem2Dto();
			overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11_9);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overTotalGstinItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItem2Dto overItemDto = new CreditTurnOverFinancialItem2Dto();
			overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11_9);
			itemDtos.add(overItemDto);
		}
		processDto.setItems(itemDtos);

		processDtoList.add(processDto);
	}

	private void defultValueForRatio1ForTurnOver(
			List<CreditTurnOverFinancialProcess2Dto> processDtoList) {

		CreditTurnOverFinancialProcess2Dto processDto = new CreditTurnOverFinancialProcess2Dto();
		List<CreditTurnOverFinancialItem2Dto> itemDtos = new ArrayList<>();
		processDto.setTurnoverComp(RATIO1_PERC);

		CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
		itemDto.setTurnoverComp(EXEMPT7);
		itemDtos.add(itemDto);

		CreditTurnOverFinancialItem2Dto nilRated8ItemDto = new CreditTurnOverFinancialItem2Dto();
		nilRated8ItemDto.setTurnoverComp(NIL_RATED8);
		itemDtos.add(nilRated8ItemDto);

		CreditTurnOverFinancialItem2Dto secOutwardRevItemDto = new CreditTurnOverFinancialItem2Dto();
		secOutwardRevItemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
		itemDtos.add(secOutwardRevItemDto);

		CreditTurnOverFinancialItem2Dto explaToRuleItemDto = new CreditTurnOverFinancialItem2Dto();
		explaToRuleItemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(explaToRuleItemDto);

		CreditTurnOverFinancialItem2Dto overItemDto = new CreditTurnOverFinancialItem2Dto();
		overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11_9);
		itemDtos.add(overItemDto);

		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}

	private void ratio2ForTurnOver(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverFinancialProcess2Dto> processDtoList) {
		CreditTurnOverFinancialProcess2Dto processDto = new CreditTurnOverFinancialProcess2Dto();
		List<CreditTurnOverItemDto> overItcRevItemDtos = mapCreditRevers
				.get(ITC_REVERSAL_RATIO);
		if (overItcRevItemDtos != null && !overItcRevItemDtos.isEmpty()) {
			processDto.setTurnoverComp(RATIO2_PERC);
			BigDecimal total = BigDecimal.ZERO;
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overItcRevItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					processDto.setJan(String.valueOf(
							turnOverItemJanDto.getRatio2()) + " %");
					total = total.add(turnOverItemJanDto.getRatio2());
				}
			} else {
				processDto.setJan(String.valueOf(BigDecimal.ZERO) + " %");
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					processDto.setFeb(String.valueOf(
							turnOverItemFebDto.getRatio2()) + " %");
					total = total.add(turnOverItemFebDto.getRatio2());
				}
			} else {
				processDto.setFeb(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					processDto.setMarch(String.valueOf(
							turnOverItemMarchDto.getRatio2()) + " %");
					total = total
							.add(turnOverItemMarchDto.getRatio2());
				}
			} else {
				processDto.setMarch(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					processDto.setApril(String.valueOf(
							turnOverItemAprilDto.getRatio2()) + " %");
					total = total
							.add(turnOverItemAprilDto.getRatio2());
				}
			} else {
				processDto.setApril(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					processDto.setMay(String.valueOf(
							turnOverItemMayDto.getRatio2()) + " %");
					total = total.add(turnOverItemMayDto.getRatio2());
				}
			} else {
				processDto.setMay(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					processDto.setJune(String.valueOf(
							turnOverItemJuneDto.getRatio2()) + " %");
					total = total.add(turnOverItemJuneDto.getRatio2());
				}
			} else {
				processDto.setJune(String.valueOf(BigDecimal.ZERO) + " %");
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					processDto.setJuly(String.valueOf(
							turnOverItemJulyDto.getRatio2()) + " %");
					total = total.add(turnOverItemJulyDto.getRatio2());
				}
			} else {
				processDto.setJuly(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					processDto.setAug(String.valueOf(
							turnOverItemAugDto.getRatio2()) + " %");
					total = total.add(turnOverItemAugDto.getRatio2());
				}
			} else {
				processDto.setAug(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					processDto.setSep(String.valueOf(
							turnOverItemSepDto.getRatio2()) + " %");
					total = total.add(turnOverItemSepDto.getRatio2());
				}
			} else {
				processDto.setSep(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					processDto.setOct(String.valueOf(
							turnOverItemOctDto.getRatio2()) + " %");
					total = total.add(turnOverItemOctDto.getRatio2());
				}
			} else {
				processDto.setOct(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					processDto.setNov(String.valueOf(
							turnOverItemNovDto.getRatio2()) + " %");
					total = total.add(turnOverItemNovDto.getRatio2());
				}
			} else {
				processDto.setNov(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					processDto.setDec(String.valueOf(
							turnOverItemDecDto.getRatio2()) + " %");
					total = total.add(turnOverItemDecDto.getRatio2());
				}
			} else {
				processDto.setDec(String.valueOf(BigDecimal.ZERO) + " %");
			}
			processDto.setTotal(String.valueOf(total) + " %");
		} else {
			processDto.setTurnoverComp(RATIO2_PERC);
			processDto.setTotal(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setJan(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setFeb(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setMarch(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setApril(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setMay(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setJune(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setJuly(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setAug(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setSep(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setOct(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setNov(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setDec(String.valueOf(BigDecimal.ZERO) + " %");
		}

		List<CreditTurnOverFinancialItem2Dto> itemDtos = new ArrayList<>();
		List<CreditTurnOverItemDto> overExtItemDtos = mapCreditRevers.get(EXT);
		if (overExtItemDtos != null && !overExtItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(EXEMPT7);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overExtItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);

		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(EXEMPT7);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNilItemDtos = mapCreditRevers.get(NIL);
		if (overNilItemDtos != null && !overNilItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(NIL_RATED8);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overNilItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(NIL_RATED8);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNonItemDtos = mapCreditRevers
				.get(SECTION_NON_GST_SCH3);
		if (overNonItemDtos != null && !overNonItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_NON_GST_9);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overNonItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_NON_GST_9);
			itemDtos.add(itemDto);
		}
		CreditTurnOverFinancialItem2Dto nsyItemDto = new CreditTurnOverFinancialItem2Dto();
		nsyItemDto.setTurnoverComp(NSY_SEC_17);
		itemDtos.add(nsyItemDto);

		List<CreditTurnOverItemDto> overTtlItemDtos = mapCreditRevers
				.get(SECTION_TTL_RCS);
		if (overTtlItemDtos != null && !overTtlItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overTtlItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
			itemDtos.add(itemDto);
		}
		CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
		itemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(itemDto);

		List<CreditTurnOverItemDto> overTotalGstinItemDtos = mapCreditRevers
				.get(TOTAL_GSTIN_SUPPLIES2);
		if (overTotalGstinItemDtos != null
				&& !overTotalGstinItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto overItemDto = new CreditTurnOverFinancialItem2Dto();
			overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overTotalGstinItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(overItemDto);
		} else {
			CreditTurnOverFinancialItem2Dto overItemDto = new CreditTurnOverFinancialItem2Dto();
			overItemDto.setSno("29");
			overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
			itemDtos.add(overItemDto);
		}
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

	private Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod(
			List<CreditTurnOverItemDto> itemDtos) {
		Map<String, List<CreditTurnOverItemDto>> mapCredTurnItemDto = new HashMap<>();
		itemDtos.forEach(itemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(itemDto.getTaxPeriod());
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

		List<CreditTurnOverItemDto> turnOverItemDtos = new ArrayList<>();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		String gstin = setGstin(dataSecAttrs);
		int fromRetPeriod = GenUtil
				.convertTaxPeriodToInt(reqDto.getFromRetPeriod());

		int toRetPeriod = GenUtil
				.convertTaxPeriodToInt(reqDto.getToRetPeriod());

		List<Object[]> reversalObjs = finaYearWiseDaoImpl
				.getTurnOverFinaYear(gstin, fromRetPeriod, toRetPeriod);
		for (Object[] obj : reversalObjs) {
			CreditTurnOverItemDto processDto = new CreditTurnOverItemDto();
			processDto.setTaxPeriod(
					obj[0] != null ? String.valueOf(obj[0]) : null);
			processDto.setTurnoverComp(
					obj[1] != null ? String.valueOf(obj[1]) : null);
			processDto.setDigigstAutoComp(obj[2] != null
					? new BigDecimal(String.valueOf(obj[2])) : BigDecimal.ZERO);
			processDto.setRatio1(obj[3] != null
					? new BigDecimal(String.valueOf(obj[3])) : BigDecimal.ZERO);
			processDto.setRatio2(obj[4] != null
					? new BigDecimal(String.valueOf(obj[4])) : BigDecimal.ZERO);
			processDto.setRatio3(obj[5] != null
					? new BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
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

	private List<Gstr3BRuleComputeRespDto> convertObjGstr3b(
			Annexure1SummaryReqDto reqDto) {
		int derivedRetPer = GenUtil
				.convertTaxPeriodToInt(reqDto.getTaxPeriod());

		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

		List<Object[]> objs = new ArrayList<>();
		if ("Ratio1".equalsIgnoreCase(reqDto.getRatio())) {
			objs = /*
					 * daoImpl.getGstr3BRuleCompute(gstinList, derivedRetPer,
					 * "Total Reversal_RATIO_1")
					 */null;
		} else if ("Ratio2".equalsIgnoreCase(reqDto.getRatio())) {
			objs = /*
					 * daoImpl.getGstr3BRuleCompute(gstinList, derivedRetPer,
					 * "Total Reversal_RATIO_2")
					 */null;
		} else if ("Ratio3".equalsIgnoreCase(reqDto.getRatio())) {
			objs = /*
					 * daoImpl.getGstr3BRuleCompute(gstinList, derivedRetPer,
					 * "Total Reversal_RATIO_3")
					 */null;
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

	@Override
	public List<CreditTurnOverFinancialProcess2Dto> getFinancialYearCredTurnOverPart2(
			Annexure1SummaryReqDto reqDto) {
		List<CreditTurnOverFinancialProcess2Dto> itemDtos = new ArrayList<>();
		List<CreditTurnOverItemDto> convertObj = convertObjToCredTurnOver(
				reqDto);
		if (convertObj != null && !convertObj.isEmpty()) {
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers = mapCreditTurnOver(
					convertObj);
			itcReversalRatioForTurnOver(itemDtos);
			ratio1ForTurnOver(mapCreditRevers, itemDtos);
			ratio2ForTurnOver(mapCreditRevers, itemDtos);
			ratio3ForTurnOver(mapCreditRevers, itemDtos);

		} else {
			CreditTurnOverFinancialProcess2Dto processDto = new CreditTurnOverFinancialProcess2Dto();
			processDto.setTurnoverComp(ITC_REVERSAL_RATIO);
			defultValueForRatio1ForTurnOver(itemDtos);
			defultValueForRatio2ForTurnOver(itemDtos);
			defultValueForRatio3ForTurnOver(itemDtos);
			itemDtos.add(processDto);
		}
		return itemDtos;
	}

	private void ratio3ForTurnOver(
			Map<String, List<CreditTurnOverItemDto>> mapCreditRevers,
			List<CreditTurnOverFinancialProcess2Dto> processDtoList) {
		CreditTurnOverFinancialProcess2Dto processDto = new CreditTurnOverFinancialProcess2Dto();
		List<CreditTurnOverFinancialItem2Dto> itemDtos = new ArrayList<>();

		List<CreditTurnOverItemDto> overItcRevItemDtos = mapCreditRevers
				.get(ITC_REVERSAL_RATIO);
		if (overItcRevItemDtos != null && !overItcRevItemDtos.isEmpty()) {
			processDto.setTurnoverComp(RATIO311);
			BigDecimal total = BigDecimal.ZERO;
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overItcRevItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					processDto.setJan(String.valueOf(
							turnOverItemJanDto.getRatio3()) + " %");
					total = total.add(turnOverItemJanDto.getRatio3());
				}
			} else {
				processDto.setJan(String.valueOf(BigDecimal.ZERO) + " %");
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					processDto.setFeb(String.valueOf(
							turnOverItemFebDto.getRatio3()) + " %");
					total = total.add(turnOverItemFebDto.getRatio3());
				}
			} else {
				processDto.setFeb(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					processDto.setMarch(String.valueOf(
							turnOverItemMarchDto.getRatio3()) + " %");
					total = total
							.add(turnOverItemMarchDto.getRatio3());
				}
			} else {
				processDto.setMarch(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					processDto.setApril(String.valueOf(
							turnOverItemAprilDto.getRatio3()) + " %");
					total = total
							.add(turnOverItemAprilDto.getRatio3());
				}
			} else {
				processDto.setApril(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					processDto.setMay(String.valueOf(
							turnOverItemMayDto.getRatio3()) + " %");
					total = total.add(turnOverItemMayDto.getRatio3());
				}
			} else {
				processDto.setMay(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					processDto.setJune(String.valueOf(
							turnOverItemJuneDto.getRatio3()) + " %");
					total = total.add(turnOverItemJuneDto.getRatio3());
				}
			} else {
				processDto.setJune(String.valueOf(BigDecimal.ZERO) + " %");
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					processDto.setJuly(String.valueOf(
							turnOverItemJulyDto.getRatio3()) + " %");
					total = total.add(turnOverItemJulyDto.getRatio3());
				}
			} else {
				processDto.setJuly(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					processDto.setAug(String.valueOf(
							turnOverItemAugDto.getRatio3()) + " %");
					total = total.add(turnOverItemAugDto.getRatio3());
				}
			} else {
				processDto.setAug(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					processDto.setSep(String.valueOf(
							turnOverItemSepDto.getRatio3()) + " %");
					total = total.add(turnOverItemSepDto.getRatio3());
				}
			} else {
				processDto.setSep(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					processDto.setOct(String.valueOf(
							turnOverItemOctDto.getRatio3()) + " %");
					total = total.add(turnOverItemOctDto.getRatio3());
				}
			} else {
				processDto.setOct(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					processDto.setNov(String.valueOf(
							turnOverItemNovDto.getRatio3()) + " %");
					total = total.add(turnOverItemNovDto.getRatio3());
				}
			} else {
				processDto.setNov(String.valueOf(BigDecimal.ZERO) + " %");
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					processDto.setDec(String.valueOf(
							turnOverItemDecDto.getRatio3()) + " %");
					total = total.add(turnOverItemDecDto.getRatio3());
				}
			} else {
				processDto.setDec(String.valueOf(BigDecimal.ZERO) + " %");
			}
			processDto.setTotal(String.valueOf(total) + " %");

		} else {
			processDto.setTurnoverComp(RATIO311);
			processDto.setTotal(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setJan(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setFeb(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setMarch(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setApril(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setMay(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setJune(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setJuly(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setAug(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setSep(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setOct(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setNov(String.valueOf(BigDecimal.ZERO) + " %");
			processDto.setDec(String.valueOf(BigDecimal.ZERO) + " %");
		}

		List<CreditTurnOverItemDto> overExtItemDtos = mapCreditRevers.get(EXT);
		if (overExtItemDtos != null && !overExtItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(EXEMPT7);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overExtItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(EXEMPT7);
			itemDtos.add(itemDto);
		}
		List<CreditTurnOverItemDto> overNilItemDtos = mapCreditRevers.get(NIL);
		if (overNilItemDtos != null && !overNilItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(NIL_RATED8);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overNilItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(NIL_RATED8);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overNonItemDtos = mapCreditRevers
				.get(SECTION_NON_GST_SCH3);
		if (overNonItemDtos != null && !overNonItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_NON_GST_9);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overNonItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_NON_GST_9);
			itemDtos.add(itemDto);
		}

		List<CreditTurnOverItemDto> overTtlItemDtos = mapCreditRevers
				.get(SECTION_TTL_RCS);
		if (overTtlItemDtos != null && !overTtlItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overTtlItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(itemDto);
		} else {
			CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
			itemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
			itemDtos.add(itemDto);
		}
		CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
		itemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(itemDto);

		List<CreditTurnOverItemDto> overTotalGstinItemDtos = mapCreditRevers
				.get(TOTAL_GSTIN_SUPPLIES2);
		if (overTotalGstinItemDtos != null
				&& !overTotalGstinItemDtos.isEmpty()) {
			CreditTurnOverFinancialItem2Dto overItemDto = new CreditTurnOverFinancialItem2Dto();
			overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
			Map<String, List<CreditTurnOverItemDto>> mapCreditTurnOverForTaxPeriod = mapCreditTurnOverForTaxPeriod(
					overTotalGstinItemDtos);
			List<CreditTurnOverItemDto> turnOverItemJanDtos = mapCreditTurnOverForTaxPeriod
					.get("01");
			BigDecimal total = BigDecimal.ZERO;
			if (turnOverItemJanDtos != null && !turnOverItemJanDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJanDto : turnOverItemJanDtos) {
					itemDto.setJan(turnOverItemJanDto.getDigigstAutoComp());
					total = total.add(turnOverItemJanDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemFebDtos = mapCreditTurnOverForTaxPeriod
					.get("02");
			if (turnOverItemFebDtos != null && !turnOverItemFebDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemFebDto : turnOverItemFebDtos) {
					itemDto.setFeb(turnOverItemFebDto.getDigigstAutoComp());
					total = total.add(turnOverItemFebDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMarchDtos = mapCreditTurnOverForTaxPeriod
					.get("03");
			if (turnOverItemMarchDtos != null
					&& !turnOverItemMarchDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMarchDto : turnOverItemMarchDtos) {
					itemDto.setMarch(turnOverItemMarchDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemMarchDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAprilDtos = mapCreditTurnOverForTaxPeriod
					.get("04");
			if (turnOverItemAprilDtos != null
					&& !turnOverItemAprilDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAprilDto : turnOverItemAprilDtos) {
					itemDto.setApril(turnOverItemAprilDto.getDigigstAutoComp());
					total = total
							.add(turnOverItemAprilDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemMayDtos = mapCreditTurnOverForTaxPeriod
					.get("05");
			if (turnOverItemMayDtos != null && !turnOverItemMayDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemMayDto : turnOverItemMayDtos) {
					itemDto.setMay(turnOverItemMayDto.getDigigstAutoComp());
					total = total.add(turnOverItemMayDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemJuneDtos = mapCreditTurnOverForTaxPeriod
					.get("06");
			if (turnOverItemJuneDtos != null
					&& !turnOverItemJuneDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJuneDto : turnOverItemJuneDtos) {
					itemDto.setJune(turnOverItemJuneDto.getDigigstAutoComp());
					total = total.add(turnOverItemJuneDto.getDigigstAutoComp());
				}
			}
			List<CreditTurnOverItemDto> turnOverItemJulyDtos = mapCreditTurnOverForTaxPeriod
					.get("07");
			if (turnOverItemJulyDtos != null
					&& !turnOverItemJulyDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemJulyDto : turnOverItemJulyDtos) {
					itemDto.setJuly(turnOverItemJulyDto.getDigigstAutoComp());
					total = total.add(turnOverItemJulyDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemAugDtos = mapCreditTurnOverForTaxPeriod
					.get("08");
			if (turnOverItemAugDtos != null && !turnOverItemAugDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemAugDto : turnOverItemAugDtos) {
					itemDto.setAug(turnOverItemAugDto.getDigigstAutoComp());
					total = total.add(turnOverItemAugDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemSepDtos = mapCreditTurnOverForTaxPeriod
					.get("09");
			if (turnOverItemSepDtos != null && !turnOverItemSepDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemSepDto : turnOverItemSepDtos) {
					itemDto.setSep(turnOverItemSepDto.getDigigstAutoComp());
					total = total.add(turnOverItemSepDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemOctDtos = mapCreditTurnOverForTaxPeriod
					.get("10");
			if (turnOverItemOctDtos != null && !turnOverItemOctDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemOctDto : turnOverItemOctDtos) {
					itemDto.setOct(turnOverItemOctDto.getDigigstAutoComp());
					total = total.add(turnOverItemOctDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemNovDtos = mapCreditTurnOverForTaxPeriod
					.get("11");
			if (turnOverItemNovDtos != null && !turnOverItemNovDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemNovDto : turnOverItemNovDtos) {
					itemDto.setNov(turnOverItemNovDto.getDigigstAutoComp());
					total = total.add(turnOverItemNovDto.getDigigstAutoComp());
				}
			}

			List<CreditTurnOverItemDto> turnOverItemDecDtos = mapCreditTurnOverForTaxPeriod
					.get("12");
			if (turnOverItemDecDtos != null && !turnOverItemDecDtos.isEmpty()) {
				for (CreditTurnOverItemDto turnOverItemDecDto : turnOverItemDecDtos) {
					itemDto.setDec(turnOverItemDecDto.getDigigstAutoComp());
					total = total.add(turnOverItemDecDto.getDigigstAutoComp());
				}
			}
			itemDto.setTotal(total);
			itemDtos.add(overItemDto);
		} else {
			CreditTurnOverFinancialItem2Dto overItemDto = new CreditTurnOverFinancialItem2Dto();
			overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
			itemDtos.add(overItemDto);
		}
		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}

	private void defultValueForRatio2ForTurnOver(
			List<CreditTurnOverFinancialProcess2Dto> processDtoList) {
		CreditTurnOverFinancialProcess2Dto processDto = new CreditTurnOverFinancialProcess2Dto();

		List<CreditTurnOverFinancialItem2Dto> itemDtos = new ArrayList<>();
		processDto.setTurnoverComp(RATIO2_PERC);

		CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
		itemDto.setTurnoverComp(EXEMPT7);
		itemDtos.add(itemDto);

		CreditTurnOverFinancialItem2Dto nilRated8ItemDto = new CreditTurnOverFinancialItem2Dto();
		nilRated8ItemDto.setTurnoverComp(NIL_RATED8);
		itemDtos.add(nilRated8ItemDto);

		CreditTurnOverFinancialItem2Dto sectionNonGstItemDto = new CreditTurnOverFinancialItem2Dto();
		sectionNonGstItemDto.setTurnoverComp(SECTION_NON_GST_9);
		itemDtos.add(sectionNonGstItemDto);

		CreditTurnOverFinancialItem2Dto nsyItemDto = new CreditTurnOverFinancialItem2Dto();
		nsyItemDto.setTurnoverComp(NSY_SEC_17);
		itemDtos.add(nsyItemDto);

		CreditTurnOverFinancialItem2Dto secOutwardItemDto = new CreditTurnOverFinancialItem2Dto();
		secOutwardItemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
		itemDtos.add(secOutwardItemDto);

		CreditTurnOverFinancialItem2Dto explaToRuleItemDto = new CreditTurnOverFinancialItem2Dto();
		explaToRuleItemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(explaToRuleItemDto);

		CreditTurnOverFinancialItem2Dto overItemDto = new CreditTurnOverFinancialItem2Dto();
		overItemDto.setSno("29");
		overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
		itemDtos.add(overItemDto);

		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}

	private void defultValueForRatio3ForTurnOver(
			List<CreditTurnOverFinancialProcess2Dto> processDtoList) {
		CreditTurnOverFinancialProcess2Dto processDto = new CreditTurnOverFinancialProcess2Dto();
		List<CreditTurnOverFinancialItem2Dto> itemDtos = new ArrayList<>();

		processDto.setTurnoverComp(RATIO311);
		processDto.setTotal(String.valueOf(" 0.0 %"));
		processDto.setJan(String.valueOf(" 0.0 %"));
		processDto.setFeb(String.valueOf(" 0.0 %"));
		processDto.setMarch(String.valueOf(" 0.0 %"));
		processDto.setApril(String.valueOf(" 0.0 %"));
		processDto.setMay(String.valueOf(" 0.0 %"));
		processDto.setJune(String.valueOf(" 0.0 %"));
		processDto.setJuly(String.valueOf(" 0.0 %"));
		processDto.setAug(String.valueOf(" 0.0 %"));
		processDto.setSep(String.valueOf(" 0.0 %"));
		processDto.setOct(String.valueOf(" 0.0 %"));
		processDto.setNov(String.valueOf(" 0.0 %"));
		processDto.setDec(String.valueOf(" 0.0 %"));

		CreditTurnOverFinancialItem2Dto itemDto = new CreditTurnOverFinancialItem2Dto();
		itemDto.setTurnoverComp(EXEMPT7);
		itemDtos.add(itemDto);

		CreditTurnOverFinancialItem2Dto nilRated8itemDto = new CreditTurnOverFinancialItem2Dto();
		nilRated8itemDto.setTurnoverComp(NIL_RATED8);
		itemDtos.add(nilRated8itemDto);

		CreditTurnOverFinancialItem2Dto secNonGstItemDto = new CreditTurnOverFinancialItem2Dto();
		secNonGstItemDto.setTurnoverComp(SECTION_NON_GST_9);
		itemDtos.add(secNonGstItemDto);

		CreditTurnOverFinancialItem2Dto secOutwardItemDto = new CreditTurnOverFinancialItem2Dto();
		secOutwardItemDto.setTurnoverComp(SECTION_OUTWARD_REVERSE_CHARGE_16);
		itemDtos.add(secOutwardItemDto);

		CreditTurnOverFinancialItem2Dto explaToRuleItemDto = new CreditTurnOverFinancialItem2Dto();
		explaToRuleItemDto.setTurnoverComp(EXPLANATION_TO_RULE_42_10);
		itemDtos.add(explaToRuleItemDto);

		CreditTurnOverFinancialItem2Dto overItemDto = new CreditTurnOverFinancialItem2Dto();
		overItemDto.setTurnoverComp(TOTAL_GSTIN_SUPPLIES_11);
		itemDtos.add(overItemDto);

		processDto.setItems(itemDtos);
		processDtoList.add(processDto);
	}
}
