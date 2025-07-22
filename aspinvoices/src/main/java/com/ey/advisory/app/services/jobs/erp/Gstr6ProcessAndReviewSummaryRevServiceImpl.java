package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.Gstr6DistributionRepository;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntDistributionDocItemSumDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntDistributionDocSumDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntProcAndReviewSumDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntProcAndReviewSumFinalDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntProcSumDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntProcSumItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntProcSumRespDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntReDistributionDocSumDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntRevSumDistributionDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntRevSumDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntRevSumIDistributiontemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntRevSumInwardDto;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntRevSumInwardItemDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DistributedSummaryScreenResponseDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ProcessedSummResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6DistChannelRevSumRespItemDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseItemDto;
import com.ey.advisory.app.docs.service.gstr6.Gstr6DistributedSummaryServiceImpl;
import com.ey.advisory.app.docs.services.gstr6.Gstr6ProcessedDataServiceImpl;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6ReviewSummaryServiceImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr6ProcessAndReviewSummaryRevServiceImpl")
public class Gstr6ProcessAndReviewSummaryRevServiceImpl
		implements Gstr6ProcessAndReviewSummaryRevService {
	
	@Autowired
	@Qualifier("Gstr6ProcessedDataServiceImpl")
	private Gstr6ProcessedDataServiceImpl gstr6ProcessedDataService;

	@Autowired
	@Qualifier("Gstr6ReviewSummaryServiceImpl")
	private Gstr6ReviewSummaryServiceImpl gstr6RevSumServImpl;

	@Autowired
	@Qualifier("Gstr6DistributedSummaryServiceImpl")
	private Gstr6DistributedSummaryServiceImpl distSumSevImpl;

	private static final String ITC_CROSS = "ITC_CROSS";
	private static final String TOTAL = "TOTAL";

	@Autowired
	@Qualifier("Gstr6DistributionRepository")
	private Gstr6DistributionRepository repository;

	@Override
	public Gstr6RevIntProcAndReviewSumFinalDto getGstr6RevIntProcAnReview(
			String gstin, String entityPan, String stateName, String entityName,
			String companyCode) {
		Gstr6RevIntProcAndReviewSumFinalDto gstr6RevIntProcAndRevSumfinalDto = new Gstr6RevIntProcAndReviewSumFinalDto();

		List<Gstr6RevIntProcAndReviewSumDto> gstr6RevIntProcAndReviewSumDtos = getGstr6RevIntProc(
				gstin, entityPan, stateName, entityName, companyCode);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR 6 Item List details %s for GSTIN %s",
					gstr6RevIntProcAndReviewSumDtos, gstin);
			LOGGER.debug(msg);
		}

		gstr6RevIntProcAndRevSumfinalDto.setGstr6RevIntProcAndReviewSumDtos(
				gstr6RevIntProcAndReviewSumDtos);
		return gstr6RevIntProcAndRevSumfinalDto;
	}

	private List<Gstr6RevIntProcAndReviewSumDto> getGstr6RevIntProc(
			String gstin, String entityPan, String stateName, String entityName,
			String companyCode) {
		List<Gstr6RevIntProcAndReviewSumDto> procAndRevSumDtos = new ArrayList<>();
		List<Gstr6RevIntProcSumRespDto> gstr6RevIntProcSumRespDtos = getGstr6ProcessSummary(
				gstin);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("GSTR 6  Processed details %s",
					gstr6RevIntProcSumRespDtos);
			LOGGER.debug(msg);
		}

		for (Gstr6RevIntProcSumRespDto gstr6RevIntProcSumRespDto : gstr6RevIntProcSumRespDtos) {
			Gstr6RevIntProcAndReviewSumDto procAndRevSumdto = null;
			if (gstr6RevIntProcSumRespDto.getGstin() != null
					&& gstr6RevIntProcSumRespDto.getRetPeriod() != null) {
				procAndRevSumdto = new Gstr6RevIntProcAndReviewSumDto();
				procAndRevSumdto.setGstinNum(gstin);
				procAndRevSumdto
						.setRetPer(gstr6RevIntProcSumRespDto.getRetPeriod());
				procAndRevSumdto.setCompanyCode(companyCode);
				if (entityName != null) {
					procAndRevSumdto
							.setEntityName(StringUtils.upperCase(entityName));
				}
				procAndRevSumdto.setEntityPan(entityPan);

				// Process Summary
				Gstr6RevIntProcSumDto gstr6RevIntProcSumDto = getProcSumItem(
						gstr6RevIntProcSumRespDto);
				procAndRevSumdto
						.setGetGstr6RevIntProcSumDto(gstr6RevIntProcSumDto);

				// Review Summary
				Gstr6RevIntRevSumDto gstr6RevIntRevSumDto = convertReviewSummary(
						gstr6RevIntProcSumRespDto);
				procAndRevSumdto.setGstr6RevIntRevSumDto(gstr6RevIntRevSumDto);

				// Review Summary Distribution
				Gstr6RevIntDistributionDocSumDto gstr6RevIntDistDocSumDto = getDistriuteReviewSum(
						gstr6RevIntProcSumRespDto);
				procAndRevSumdto
						.setGstr6RevIntDistDocSumDto(gstr6RevIntDistDocSumDto);

				// Review Summary Re Distribution
				Gstr6RevIntReDistributionDocSumDto gstr6RevIntReDistDocSumDto = getReDistriuteReviewSum(
						gstr6RevIntProcSumRespDto);

				procAndRevSumdto.setGstr6RevIntReDistDocSumDto(
						gstr6RevIntReDistDocSumDto);
			}
			procAndRevSumDtos.add(procAndRevSumdto);
		}
		return procAndRevSumDtos;
	}

	private Gstr6RevIntProcSumDto getProcSumItem(
			Gstr6RevIntProcSumRespDto gstr6RevIntProcSumRespDto) {
		Gstr6RevIntProcSumDto getGstr6RevIntProcSumDto = new Gstr6RevIntProcSumDto();
		List<Gstr6RevIntProcSumItemDto> intProcSumItemDtos = new ArrayList<>();
		Gstr6RevIntProcSumItemDto intProcSumItemDto = new Gstr6RevIntProcSumItemDto();
		intProcSumItemDto.setState(gstr6RevIntProcSumRespDto.getState());
		intProcSumItemDto
				.setEyRegType(gstr6RevIntProcSumRespDto.getEyRegType());
		intProcSumItemDto.setEyDate(gstr6RevIntProcSumRespDto.getEyDate());
		intProcSumItemDto.setEyTime(gstr6RevIntProcSumRespDto.getEyTime());
		intProcSumItemDto.setEyStatus(gstr6RevIntProcSumRespDto.getEyStatus());
		intProcSumItemDto.setTotCreditElg(
				gstr6RevIntProcSumRespDto.getTotCreditEligable());

		intProcSumItemDto
				.setTaxTotDoc(gstr6RevIntProcSumRespDto.getTaxTotDoc());
		intProcSumItemDto
				.setTaxInvval(gstr6RevIntProcSumRespDto.getTaxInvval());
		intProcSumItemDto
				.setTaxTotval(gstr6RevIntProcSumRespDto.getTaxTotval());
		intProcSumItemDto
				.setTaxTaxval(gstr6RevIntProcSumRespDto.getTaxTaxval());

		intProcSumItemDto
				.setTaxIgstval(gstr6RevIntProcSumRespDto.getTaxIgstval());
		intProcSumItemDto
				.setTaxCgstval(gstr6RevIntProcSumRespDto.getTaxCgstval());
		intProcSumItemDto
				.setTaxSgstval(gstr6RevIntProcSumRespDto.getTaxSgstval());
		intProcSumItemDto
				.setTaxCessval(gstr6RevIntProcSumRespDto.getTaxCessval());

		intProcSumItemDto
				.setCreditIgst(gstr6RevIntProcSumRespDto.getCreditIgst());
		intProcSumItemDto
				.setCreditCgst(gstr6RevIntProcSumRespDto.getCreditCgst());
		intProcSumItemDto
				.setCreditSgst(gstr6RevIntProcSumRespDto.getCreditSgst());
		intProcSumItemDto
				.setCreditCess(gstr6RevIntProcSumRespDto.getCreditCess());
		intProcSumItemDtos.add(intProcSumItemDto);
		getGstr6RevIntProcSumDto
				.setGstr6RevIntProcSumItemDto(intProcSumItemDtos);
		return getGstr6RevIntProcSumDto;
	}

	private Gstr6RevIntRevSumDto convertReviewSummary(
			Gstr6RevIntProcSumRespDto gstr6RevIntProcSumRespDto) {
		Gstr6RevIntRevSumDto gstr6RevIntRevSumDto = new Gstr6RevIntRevSumDto();

		// Inward Part
		Gstr6RevIntRevSumInwardDto inwardDto = new Gstr6RevIntRevSumInwardDto();
		List<Gstr6RevIntRevSumInwardItemDto> inwardSum = convertReviewSummaryInward(
				gstr6RevIntProcSumRespDto);
		inwardDto.setItems(inwardSum);
		gstr6RevIntRevSumDto.setInwardDto(inwardDto);

		// Distribution part
		Gstr6RevIntRevSumDistributionDto distributionDto = new Gstr6RevIntRevSumDistributionDto();

		List<Gstr6RevIntRevSumIDistributiontemDto> distSum = convertReviewSummaryDistribution(
				gstr6RevIntProcSumRespDto);
		distributionDto.setItems(distSum);
		gstr6RevIntRevSumDto.setDistributionDto(distributionDto);

		return gstr6RevIntRevSumDto;
	}

	private List<Gstr6RevIntRevSumInwardItemDto> convertReviewSummaryInward(
			Gstr6RevIntProcSumRespDto gstr6RevIntProcSumRespDto) {
		List<Gstr6RevIntRevSumInwardItemDto> revItems = new ArrayList<>();
		Annexure1SummaryReqDto reqDto = new Annexure1SummaryReqDto();
		reqDto.setTaxPeriod(gstr6RevIntProcSumRespDto.getRetPeriod());
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstr6RevIntProcSumRespDto.getGstin());
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		reqDto.setDataSecAttrs(dataSecAttrs);

		List<Gstr6ReviewSummaryResponseItemDto> revSumRespDtos = gstr6RevSumServImpl
				.getGstr6InwardRespItem(reqDto);
		if (revSumRespDtos != null && !revSumRespDtos.isEmpty()) {
			for (Gstr6ReviewSummaryResponseItemDto revSumRespDto : revSumRespDtos) {
				Gstr6RevIntRevSumInwardItemDto itemDto = new Gstr6RevIntRevSumInwardItemDto();
				itemDto.setTableType("INWARD");

				if (ITC_CROSS.equalsIgnoreCase(revSumRespDto.getDocType())) {
					itemDto.setDocType(revSumRespDto.getDocType());
					itemDto.setEligInd(revSumRespDto.getEligInd());

					if (TOTAL.equalsIgnoreCase(revSumRespDto.getEligInd())) {
						itemDto.setACount(revSumRespDto.getAspCount());
						itemDto.setAInvVal(revSumRespDto.getAspInvValue());
						itemDto.setATaxVal(revSumRespDto.getAspTaxbValue());
					}
					itemDto.setATotalTax(revSumRespDto.getAspTotTax());
					itemDto.setAIgst(revSumRespDto.getAspIgst());
					itemDto.setASgst(revSumRespDto.getAspSgst());
					itemDto.setACgst(revSumRespDto.getAspCgst());
					itemDto.setACess(revSumRespDto.getAspCess());

					if (TOTAL.equalsIgnoreCase(revSumRespDto.getEligInd())) {
						itemDto.setGCount(revSumRespDto.getGstnCount());
						itemDto.setGInvVal(revSumRespDto.getGstnInvValue());
						itemDto.setGTaxVal(revSumRespDto.getGstnTaxbValue());
					}
					itemDto.setGTotalTax(revSumRespDto.getGstnTotTax());
					itemDto.setGIgst(revSumRespDto.getGstnIgst());
					itemDto.setGSgst(revSumRespDto.getGstnSgst());
					itemDto.setGCgst(revSumRespDto.getGstnCgst());
					itemDto.setGCess(revSumRespDto.getGstnCess());

					if (TOTAL.equalsIgnoreCase(revSumRespDto.getEligInd())) {
						itemDto.setDCount(revSumRespDto.getDiffCount());
						itemDto.setDInvVal(revSumRespDto.getDiffInvValue());
						itemDto.setDTaxVal(revSumRespDto.getDiffTaxbValue());
					}
					itemDto.setDTotalTax(revSumRespDto.getDiffTotTax());
					itemDto.setDIgst(revSumRespDto.getDiffIgst());
					itemDto.setDSgst(revSumRespDto.getDiffSgst());
					itemDto.setDCgst(revSumRespDto.getDiffCgst());
					itemDto.setDCess(revSumRespDto.getDiffCess());
				} else {
					itemDto.setDocType(revSumRespDto.getDocType());
					itemDto.setEligInd(revSumRespDto.getEligInd());

					itemDto.setACount(revSumRespDto.getAspCount());
					itemDto.setAInvVal(revSumRespDto.getAspInvValue());
					itemDto.setATaxVal(revSumRespDto.getAspTaxbValue());
					itemDto.setATotalTax(revSumRespDto.getAspTotTax());
					itemDto.setAIgst(revSumRespDto.getAspIgst());
					itemDto.setASgst(revSumRespDto.getAspSgst());
					itemDto.setACgst(revSumRespDto.getAspCgst());
					itemDto.setACess(revSumRespDto.getAspCess());

					itemDto.setGCount(revSumRespDto.getGstnCount());
					itemDto.setGInvVal(revSumRespDto.getGstnInvValue());
					itemDto.setGTaxVal(revSumRespDto.getGstnTaxbValue());
					itemDto.setGTotalTax(revSumRespDto.getGstnTotTax());
					itemDto.setGIgst(revSumRespDto.getGstnIgst());
					itemDto.setGSgst(revSumRespDto.getGstnSgst());
					itemDto.setGCgst(revSumRespDto.getGstnCgst());
					itemDto.setGCess(revSumRespDto.getGstnCess());

					itemDto.setDCount(revSumRespDto.getDiffCount());
					itemDto.setDInvVal(revSumRespDto.getDiffInvValue());
					itemDto.setDTaxVal(revSumRespDto.getDiffTaxbValue());
					itemDto.setDTotalTax(revSumRespDto.getDiffTotTax());
					itemDto.setDIgst(revSumRespDto.getDiffIgst());
					itemDto.setDSgst(revSumRespDto.getDiffSgst());
					itemDto.setDCgst(revSumRespDto.getDiffCgst());
					itemDto.setDCess(revSumRespDto.getDiffCess());
				}
				revItems.add(itemDto);
			}

			getSumTotalRevSumInwardDist("D_INV", revSumRespDtos, revItems);
			getSumTotalRevSumInwardDist("D_CR", revSumRespDtos, revItems);
			getSumTotalRevSumInwardDist("RD_INC", revSumRespDtos, revItems);
			getSumTotalRevSumInwardDist("RD_CR", revSumRespDtos, revItems);
		}
		return revItems;
	}

	private void getSumTotalRevSumInwardDist(String docType,
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespDtos,
			List<Gstr6RevIntRevSumInwardItemDto> revItems) {

		BigInteger aCount = BigInteger.ZERO;
		BigDecimal aInvVal = BigDecimal.ZERO;
		BigDecimal aTaxVal = BigDecimal.ZERO;
		BigDecimal aTotalTax = BigDecimal.ZERO;
		BigDecimal aIgst = BigDecimal.ZERO;
		BigDecimal aCgst = BigDecimal.ZERO;
		BigDecimal aSgst = BigDecimal.ZERO;
		BigDecimal aCess = BigDecimal.ZERO;
		BigInteger gCount = BigInteger.ZERO;
		BigDecimal gInvVal = BigDecimal.ZERO;
		BigDecimal gTaxVal = BigDecimal.ZERO;
		BigDecimal gTotalTax = BigDecimal.ZERO;
		BigDecimal gIgst = BigDecimal.ZERO;
		BigDecimal gCgst = BigDecimal.ZERO;
		BigDecimal gSgst = BigDecimal.ZERO;
		BigDecimal gCess = BigDecimal.ZERO;
		BigInteger dCount = BigInteger.ZERO;
		BigDecimal dTaxVal = BigDecimal.ZERO;
		BigDecimal dInvVal = BigDecimal.ZERO;
		BigDecimal dTotalTax = BigDecimal.ZERO;
		BigDecimal dIgst = BigDecimal.ZERO;
		BigDecimal dCgst = BigDecimal.ZERO;
		BigDecimal dSgst = BigDecimal.ZERO;
		BigDecimal dCess = BigDecimal.ZERO;

		for (Gstr6ReviewSummaryResponseItemDto revSumRespDto : revSumRespDtos) {
			if (docType.equalsIgnoreCase(revSumRespDto.getDocType())
					&& ("IS_ELG".equalsIgnoreCase(revSumRespDto.getEligInd())
							|| "NOT_ELG".equalsIgnoreCase(
									revSumRespDto.getEligInd()))) {
				docType = revSumRespDto.getDocType();
				aCount = aCount.add(revSumRespDto.getAspCount());
				aInvVal = aInvVal.add(revSumRespDto.getAspInvValue());
				aTaxVal = aTaxVal.add(revSumRespDto.getAspTaxbValue());
				aTotalTax = aTotalTax.add(revSumRespDto.getAspTotTax());
				aIgst = aIgst.add(revSumRespDto.getAspIgst());
				aSgst = aSgst.add(revSumRespDto.getAspSgst());
				aCgst = aCgst.add(revSumRespDto.getAspCgst());
				aCess = aCess.add(revSumRespDto.getAspCess());

				gCount = gCount.add(revSumRespDto.getGstnCount());
				gInvVal = gInvVal.add(revSumRespDto.getGstnInvValue());
				gTaxVal = gTaxVal.add(revSumRespDto.getGstnTaxbValue());
				gTotalTax = gTotalTax.add(revSumRespDto.getGstnTotTax());
				gIgst = gIgst.add(revSumRespDto.getGstnIgst());
				gSgst = gSgst.add(revSumRespDto.getGstnSgst());
				gCgst = gCgst.add(revSumRespDto.getGstnCgst());
				gCess = gCess.add(revSumRespDto.getGstnCess());

				dCount = dCount.add(revSumRespDto.getDiffCount());
				dInvVal = dInvVal.add(revSumRespDto.getDiffInvValue());
				dTaxVal = dTaxVal.add(revSumRespDto.getDiffTaxbValue());
				dTotalTax = dTotalTax.add(revSumRespDto.getDiffTotTax());
				dIgst = dIgst.add(revSumRespDto.getDiffIgst());
				dSgst = dSgst.add(revSumRespDto.getDiffSgst());
				dCgst = dCgst.add(revSumRespDto.getDiffCgst());
				dCess = dCess.add(revSumRespDto.getDiffCess());
			}
		}
		Gstr6RevIntRevSumInwardItemDto itemDto = new Gstr6RevIntRevSumInwardItemDto();
		itemDto.setTableType("INWARD");
		itemDto.setDocType(docType);
		itemDto.setEligInd("TOTAL");
		itemDto.setACount(aCount);
		itemDto.setAInvVal(aInvVal);
		itemDto.setATaxVal(aTaxVal);
		itemDto.setATotalTax(aTotalTax);
		itemDto.setAIgst(aIgst);
		itemDto.setASgst(aSgst);
		itemDto.setACgst(aCgst);
		itemDto.setACess(aCess);

		itemDto.setGCount(gCount);
		itemDto.setGInvVal(gInvVal);
		itemDto.setGTaxVal(gTaxVal);
		itemDto.setGTotalTax(gTotalTax);
		itemDto.setGIgst(gIgst);
		itemDto.setGSgst(gSgst);
		itemDto.setGCgst(gCgst);
		itemDto.setGCess(gCess);

		itemDto.setDCount(dCount);
		itemDto.setDInvVal(dInvVal);
		itemDto.setDTaxVal(dTaxVal);
		itemDto.setDTotalTax(dTotalTax);
		itemDto.setDIgst(dIgst);
		itemDto.setDSgst(dSgst);
		itemDto.setDCgst(dCgst);
		itemDto.setDCess(dCess);
		revItems.add(itemDto);
	}

	private List<Gstr6RevIntRevSumIDistributiontemDto> convertReviewSummaryDistribution(
			Gstr6RevIntProcSumRespDto gstr6RevIntProcSumRespDto) {
		List<Gstr6RevIntRevSumIDistributiontemDto> revItems = new ArrayList<>();
		Annexure1SummaryReqDto reqDto = new Annexure1SummaryReqDto();
		reqDto.setTaxPeriod(gstr6RevIntProcSumRespDto.getRetPeriod());
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstr6RevIntProcSumRespDto.getGstin());
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		reqDto.setDataSecAttrs(dataSecAttrs);

		List<Gstr6DistChannelRevSumRespItemDto> distribuRevSumList = gstr6RevSumServImpl
				.convertObjToRespDtos(reqDto);

		if (distribuRevSumList != null && !distribuRevSumList.isEmpty()) {
			getDistInvRevTotal("INV", distribuRevSumList, revItems);
			getDistInvRevTotal("CR", distribuRevSumList, revItems);
			getDistInvRevTotal("RNV", distribuRevSumList, revItems);
			getDistInvRevTotal("RCR", distribuRevSumList, revItems);
			distribuRevSumList.forEach(distRevSum -> {
				Gstr6RevIntRevSumIDistributiontemDto itemDto = new Gstr6RevIntRevSumIDistributiontemDto();
				itemDto.setTableType("DISTRIBUTION");
				itemDto.setDocType(distRevSum.getDocType());
				if ("ELIGIBLE".equalsIgnoreCase(distRevSum.getDistribution())) {
					itemDto.setEligInd("IS_ELG");
				}
				if ("INELIGIBLE"
						.equalsIgnoreCase(distRevSum.getDistribution())) {
					itemDto.setEligInd("NOT_ELG");
				}

				itemDto.setACount(distRevSum.getAspCount());
				itemDto.setAIgstAsIgst(distRevSum.getAspIgstasIgst());
				itemDto.setAIgstAsSgst(distRevSum.getAspIgstasSgst());
				itemDto.setAIgstAsCgst(distRevSum.getAspIgstasCgst());
				itemDto.setASgstAsSgst(distRevSum.getAspSgstasSgst());
				itemDto.setASgstAsIgst(distRevSum.getAspSgstasIgst());
				itemDto.setACgstAsCgst(distRevSum.getAspCgstasCgst());
				itemDto.setACgstAsIgst(distRevSum.getAspCgstasIgst());
				itemDto.setACessAmt(distRevSum.getAspCessAmount());

				itemDto.setGCount(distRevSum.getGstnCount());
				itemDto.setGIgstAsIgst(distRevSum.getGstnIgstasIgst());
				itemDto.setGIgstAsSgst(distRevSum.getGstnIgstasSgst());
				itemDto.setGIgstAsCgst(distRevSum.getGstnIgstasCgst());
				itemDto.setGSgstAsSgst(distRevSum.getGstnSgstasSgst());
				itemDto.setGSgstAsIgst(distRevSum.getGstnSgstasIgst());
				itemDto.setGCgstAsCgst(distRevSum.getGstnCgstasCgst());
				itemDto.setGCgstAsIgst(distRevSum.getGstnCgstasIgst());
				itemDto.setGCessAmt(distRevSum.getGstnCessAmount());

				itemDto.setDCount(distRevSum.getDiffCount());
				itemDto.setDIgstAsIgst(distRevSum.getDiffIgstasIgst());
				itemDto.setDIgstAsSgst(distRevSum.getDiffIgstasSgst());
				itemDto.setDIgstAsCgst(distRevSum.getDiffIgstasCgst());
				itemDto.setDSgstAsSgst(distRevSum.getDiffSgstasSgst());
				itemDto.setDSgstAsIgst(distRevSum.getDiffSgstasIgst());
				itemDto.setDCgstAsCgst(distRevSum.getDiffCgstasCgst());
				itemDto.setDCgstAsIgst(distRevSum.getDiffCgstasIgst());
				itemDto.setDCessAmt(distRevSum.getDiffCessAmount());

				revItems.add(itemDto);
			});
		}
		return revItems;
	}

	private void getDistInvRevTotal(String docType,
			List<Gstr6DistChannelRevSumRespItemDto> distribuRevSumList,
			List<Gstr6RevIntRevSumIDistributiontemDto> revItems) {

		BigInteger aspCount = BigInteger.ZERO;

		BigDecimal aIgstAsIgst = BigDecimal.ZERO;

		BigDecimal aIgstAsSgst = BigDecimal.ZERO;

		BigDecimal aIgstAsCgst = BigDecimal.ZERO;

		BigDecimal aSgstAsSgst = BigDecimal.ZERO;

		BigDecimal aSgstAsIgst = BigDecimal.ZERO;

		BigDecimal aCgstAsCgst = BigDecimal.ZERO;

		BigDecimal aCgstAsIgst = BigDecimal.ZERO;

		BigDecimal aCessAmt = BigDecimal.ZERO;

		BigInteger gCount = BigInteger.ZERO;

		BigDecimal gIgstAsIgst = BigDecimal.ZERO;

		BigDecimal gIgstAsSgst = BigDecimal.ZERO;

		BigDecimal gIgstAsCgst = BigDecimal.ZERO;

		BigDecimal gSgstAsSgst = BigDecimal.ZERO;

		BigDecimal gSgstAsIgst = BigDecimal.ZERO;

		BigDecimal gCgstAsCgst = BigDecimal.ZERO;

		BigDecimal gCgstAsIgst = BigDecimal.ZERO;

		BigDecimal gCessAmt = BigDecimal.ZERO;

		BigInteger dCount = BigInteger.ZERO;

		BigDecimal dIgstAsIgst = BigDecimal.ZERO;

		BigDecimal dIgstAsSgst = BigDecimal.ZERO;

		BigDecimal dIgstAsCgst = BigDecimal.ZERO;

		BigDecimal dSgstAsSgst = BigDecimal.ZERO;

		BigDecimal dSgstAsIgst = BigDecimal.ZERO;

		BigDecimal dCgstAsCgst = BigDecimal.ZERO;

		BigDecimal dCgstAsIgst = BigDecimal.ZERO;

		BigDecimal dCessAmt = BigDecimal.ZERO;

		for (Gstr6DistChannelRevSumRespItemDto distRevSum : distribuRevSumList) {
			if (docType.equalsIgnoreCase(distRevSum.getDocType()) && ("ELIGIBLE"
					.equalsIgnoreCase(distRevSum.getDistribution())
					|| "INELIGIBLE"
							.equalsIgnoreCase(distRevSum.getDistribution()))) {
				aspCount = aspCount.add(distRevSum.getAspCount() != null
						? distRevSum.getAspCount() : BigInteger.ZERO);
				aIgstAsIgst = aIgstAsIgst
						.add(distRevSum.getAspIgstasIgst() != null
								? distRevSum.getAspIgstasIgst()
								: BigDecimal.ZERO);
				aIgstAsSgst = aIgstAsSgst
						.add(distRevSum.getAspIgstasSgst() != null
								? distRevSum.getAspIgstasSgst()
								: BigDecimal.ZERO);
				aIgstAsCgst = aIgstAsCgst
						.add(distRevSum.getAspIgstasCgst() != null
								? distRevSum.getAspIgstasCgst()
								: BigDecimal.ZERO);
				aSgstAsSgst = aSgstAsSgst
						.add(distRevSum.getAspSgstasSgst() != null
								? distRevSum.getAspSgstasSgst()
								: BigDecimal.ZERO);
				aSgstAsIgst = aSgstAsIgst
						.add(distRevSum.getAspSgstasIgst() != null
								? distRevSum.getAspSgstasIgst()
								: BigDecimal.ZERO);
				aCgstAsCgst = aCgstAsCgst
						.add(distRevSum.getAspCgstasCgst() != null
								? distRevSum.getAspCgstasCgst()
								: BigDecimal.ZERO);

				aCgstAsIgst = aCgstAsIgst
						.add(distRevSum.getAspCgstasIgst() != null
								? distRevSum.getAspCgstasIgst()
								: BigDecimal.ZERO);

				aCessAmt = aCessAmt.add(distRevSum.getAspCessAmount() != null
						? distRevSum.getAspCessAmount() : BigDecimal.ZERO);

				gCount = gCount.add(distRevSum.getGstnCount() != null
						? distRevSum.getGstnCount() : BigInteger.ZERO);
				gIgstAsIgst = gIgstAsIgst
						.add(distRevSum.getGstnIgstasIgst() != null
								? distRevSum.getGstnIgstasIgst()
								: BigDecimal.ZERO);
				gIgstAsSgst = gIgstAsSgst
						.add(distRevSum.getGstnIgstasSgst() != null
								? distRevSum.getGstnIgstasSgst()
								: BigDecimal.ZERO);
				gIgstAsCgst = gIgstAsCgst
						.add(distRevSum.getGstnIgstasCgst() != null
								? distRevSum.getGstnIgstasCgst()
								: BigDecimal.ZERO);
				gSgstAsSgst = gSgstAsSgst
						.add(distRevSum.getGstnSgstasSgst() != null
								? distRevSum.getGstnSgstasSgst()
								: BigDecimal.ZERO);
				gSgstAsIgst = gSgstAsIgst
						.add(distRevSum.getGstnSgstasIgst() != null
								? distRevSum.getGstnSgstasIgst()
								: BigDecimal.ZERO);
				gCgstAsCgst = gCgstAsCgst
						.add(distRevSum.getGstnCgstasCgst() != null
								? distRevSum.getGstnCgstasCgst()
								: BigDecimal.ZERO);

				gCgstAsIgst = gCgstAsIgst
						.add(distRevSum.getGstnCgstasIgst() != null
								? distRevSum.getGstnCgstasIgst()
								: BigDecimal.ZERO);

				gCessAmt = gCessAmt.add(distRevSum.getGstnCessAmount() != null
						? distRevSum.getGstnCessAmount() : BigDecimal.ZERO);

				dCount = dCount.add(distRevSum.getDiffCount() != null
						? distRevSum.getDiffCount() : BigInteger.ZERO);
				dIgstAsIgst = dIgstAsIgst
						.add(distRevSum.getDiffIgstasIgst() != null
								? distRevSum.getDiffIgstasIgst()
								: BigDecimal.ZERO);
				dIgstAsSgst = dIgstAsSgst
						.add(distRevSum.getDiffIgstasSgst() != null
								? distRevSum.getDiffIgstasSgst()
								: BigDecimal.ZERO);
				dIgstAsCgst = dIgstAsCgst
						.add(distRevSum.getDiffIgstasCgst() != null
								? distRevSum.getDiffIgstasCgst()
								: BigDecimal.ZERO);
				dSgstAsSgst = dSgstAsSgst
						.add(distRevSum.getDiffSgstasSgst() != null
								? distRevSum.getDiffSgstasSgst()
								: BigDecimal.ZERO);
				dSgstAsIgst = dSgstAsIgst
						.add(distRevSum.getDiffSgstasIgst() != null
								? distRevSum.getDiffSgstasIgst()
								: BigDecimal.ZERO);
				dCgstAsCgst = dCgstAsCgst
						.add(distRevSum.getDiffCgstasCgst() != null
								? distRevSum.getDiffCgstasCgst()
								: BigDecimal.ZERO);

				dCgstAsIgst = dCgstAsIgst
						.add(distRevSum.getDiffCgstasIgst() != null
								? distRevSum.getDiffCgstasIgst()
								: BigDecimal.ZERO);

				dCessAmt = dCessAmt.add(distRevSum.getDiffCessAmount() != null
						? distRevSum.getDiffCessAmount() : BigDecimal.ZERO);
			}
		}

		Gstr6RevIntRevSumIDistributiontemDto itemDto = new Gstr6RevIntRevSumIDistributiontemDto();
		itemDto.setTableType("DISTRIBUTION");
		itemDto.setDocType(docType);
		itemDto.setEligInd("TOTAL");

		itemDto.setACount(aspCount);
		itemDto.setAIgstAsIgst(aIgstAsIgst);
		itemDto.setAIgstAsSgst(aIgstAsSgst);
		itemDto.setAIgstAsCgst(aIgstAsCgst);
		itemDto.setASgstAsSgst(aSgstAsSgst);
		itemDto.setASgstAsIgst(aSgstAsIgst);
		itemDto.setACgstAsCgst(aCgstAsCgst);
		itemDto.setACgstAsIgst(aCgstAsIgst);
		itemDto.setACessAmt(aCessAmt);

		itemDto.setGCount(gCount);
		itemDto.setGIgstAsIgst(gIgstAsIgst);
		itemDto.setGIgstAsSgst(gIgstAsSgst);
		itemDto.setGIgstAsCgst(gIgstAsCgst);
		itemDto.setGSgstAsSgst(gSgstAsSgst);
		itemDto.setGSgstAsIgst(gSgstAsIgst);
		itemDto.setGCgstAsCgst(gCgstAsCgst);
		itemDto.setGCgstAsIgst(gCgstAsIgst);
		itemDto.setGCessAmt(gCessAmt);

		itemDto.setDCount(dCount);
		itemDto.setDIgstAsIgst(dIgstAsIgst);
		itemDto.setDIgstAsSgst(dIgstAsSgst);
		itemDto.setDIgstAsCgst(dIgstAsCgst);
		itemDto.setDSgstAsSgst(dSgstAsSgst);
		itemDto.setDSgstAsIgst(dSgstAsIgst);
		itemDto.setDCgstAsCgst(dCgstAsCgst);
		itemDto.setDCgstAsIgst(dCgstAsIgst);
		itemDto.setDCessAmt(dCessAmt);

		revItems.add(itemDto);
	}

	private Gstr6RevIntDistributionDocSumDto getDistriuteReviewSum(
			Gstr6RevIntProcSumRespDto gstr6RevIntProcSumRespDto) {
		Gstr6RevIntDistributionDocSumDto gstr6RevIntDistDocSumDto = new Gstr6RevIntDistributionDocSumDto();
		Annexure1SummaryReqDto reqDto = new Annexure1SummaryReqDto();
		reqDto.setTaxPeriod(gstr6RevIntProcSumRespDto.getRetPeriod());
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstr6RevIntProcSumRespDto.getGstin());
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		reqDto.setDataSecAttrs(dataSecAttrs);

		List<Gstr6RevIntDistributionDocItemSumDto> distDocsItemSumDtos = new ArrayList<>();
		List<Gstr6DistributedSummaryScreenResponseDto> distEliSumList = distSumSevImpl
				.getGstr6DistributedEliSummaryList(reqDto);
		if (distEliSumList != null && !distEliSumList.isEmpty()) {
			distEliSumList.forEach(distEliSum -> {
				sumDistEligibleScreen(distDocsItemSumDtos, distEliSum);
			});
		}
		List<Gstr6DistributedSummaryScreenResponseDto> distInEliSumList = distSumSevImpl
				.getGstr6DistributedInEliSummaryList(reqDto);
		if (distInEliSumList != null && !distInEliSumList.isEmpty()) {
			distInEliSumList.forEach(distInEliSum -> {
				sumDistEligibleScreen(distDocsItemSumDtos, distInEliSum);
			});
		}
		gstr6RevIntDistDocSumDto.setItems(distDocsItemSumDtos);
		return gstr6RevIntDistDocSumDto;
	}

	private Gstr6RevIntReDistributionDocSumDto getReDistriuteReviewSum(
			Gstr6RevIntProcSumRespDto gstr6RevIntProcSumRespDto) {
		Gstr6RevIntReDistributionDocSumDto gstr6RevIntReDistDocSumDto = new Gstr6RevIntReDistributionDocSumDto();
		Annexure1SummaryReqDto reqDto = new Annexure1SummaryReqDto();
		reqDto.setTaxPeriod(gstr6RevIntProcSumRespDto.getRetPeriod());
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstr6RevIntProcSumRespDto.getGstin());
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		reqDto.setDataSecAttrs(dataSecAttrs);

		List<Gstr6RevIntDistributionDocItemSumDto> distDocsItemSumDtos = new ArrayList<>();

		List<Gstr6DistributedSummaryScreenResponseDto> reDistEliSumList = distSumSevImpl
				.getGstr6ReDistributedSummaryList(reqDto);
		if (reDistEliSumList != null && !reDistEliSumList.isEmpty()) {
			reDistEliSumList.forEach(reDistEliSum -> {
				sumDistEligibleScreen(distDocsItemSumDtos, reDistEliSum);
			});
		}
		List<Gstr6DistributedSummaryScreenResponseDto> reDistInEliSumList = distSumSevImpl
				.getGstr6ReDistributedInEligibleSummaryList(reqDto);
		if (reDistInEliSumList != null && !reDistInEliSumList.isEmpty()) {
			reDistInEliSumList.forEach(reDistInEliSum -> {
				sumDistEligibleScreen(distDocsItemSumDtos, reDistInEliSum);
			});
		}
		gstr6RevIntReDistDocSumDto.setItems(distDocsItemSumDtos);
		return gstr6RevIntReDistDocSumDto;
	}

	private void sumDistEligibleScreen(
			List<Gstr6RevIntDistributionDocItemSumDto> distDocsItemSumDtos,
			Gstr6DistributedSummaryScreenResponseDto distEliSum) {
		Gstr6RevIntDistributionDocItemSumDto distDocsItemSumDto = new Gstr6RevIntDistributionDocItemSumDto();
		distDocsItemSumDto.setDocumentType(distEliSum.getDocumentType());
		distDocsItemSumDto.setEligInd(distEliSum.getEligibleIndicator());
		distDocsItemSumDto.setDocDate(distEliSum.getDocDate() != null
				? String.valueOf(distEliSum.getDocDate()) : null);
		distDocsItemSumDto.setDocNumber(distEliSum.getDocNum());
		distDocsItemSumDto.setOrgDocNumber(distEliSum.getOrigDocNumber());
		distDocsItemSumDto.setOrgDocdate(distEliSum.getOrigDocDate() != null
				? String.valueOf(distEliSum.getOrigDocDate()) : null);
		distDocsItemSumDto.setIsdgstin(distEliSum.getIsdGstin());
		distDocsItemSumDto.setStatecode(distEliSum.getStateCode());
		distDocsItemSumDto.setRecpGstin(distEliSum.getRecipientGSTIN());
		distDocsItemSumDto.setSupplytype(distEliSum.getSupplyType());

		distDocsItemSumDto.setIgstAsIgst(distEliSum.getIgstAsIgst());
		distDocsItemSumDto.setIgstAsSgst(distEliSum.getIgstAsSgst());
		distDocsItemSumDto.setIgstAsCgst(distEliSum.getIgstAsCgst());

		distDocsItemSumDto.setSgstAsSgst(distEliSum.getSgstAsSgst());
		distDocsItemSumDto.setSgstAsIgst(distEliSum.getSgstAsIgst());

		distDocsItemSumDto.setCgstAsCgst(distEliSum.getCgstAsCgst());
		distDocsItemSumDto.setCgstAsIgst(distEliSum.getCgstAsIgst());
		distDocsItemSumDto.setCessAmount(distEliSum.getCessAmount());

		distDocsItemSumDtos.add(distDocsItemSumDto);
	}

	private List<Gstr6RevIntProcSumRespDto> getGstr6ProcessSummary(
			String gstin) {

		Gstr6SummaryRequestDto dto = new Gstr6SummaryRequestDto();
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		dto.setDataSecAttrs(dataSecAttrs);
		List<Gstr6RevIntProcSumRespDto> gstr6RevIntProcSumRespDtos = new ArrayList<>();

		List<String> listOfTaxPeriods = new ArrayList<>();
		listOfTaxPeriods.addAll(GenUtil.listOfPrevtaxPeriod(2));

		dto.setListOfretPer(listOfTaxPeriods);

		List<Gstr6ProcessedSummResponseDto> getGstr6ProcessedRecs = gstr6ProcessedDataService
				.getGstr6ProcessedRecForRevIntg(dto);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("GSTR 6  Processed details %s",
					getGstr6ProcessedRecs);
			LOGGER.debug(msg);
		}
		List<String> returnPeriods = repository.getRetPeriodsByGstin(gstin);
		DateTimeFormatter dateTimeFormmatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		DateTimeFormatter secondDateTimeForm = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss");

		Set<String> setRetPeriod = new HashSet<>(returnPeriods);
		defaultValueForCan(gstin, setRetPeriod, getGstr6ProcessedRecs);
		for (Gstr6ProcessedSummResponseDto resDto : getGstr6ProcessedRecs) {
			Gstr6RevIntProcSumRespDto itemDto = new Gstr6RevIntProcSumRespDto();
			itemDto.setGstin(gstin);
			itemDto.setRetPeriod(resDto.getRetPer());
			itemDto.setState(resDto.getState());
			itemDto.setEyStatus(resDto.getStatus());
			itemDto.setEyRegType(resDto.getRegType());
			String timeStamp = resDto.getTimeStamp();
			if (timeStamp != null && !timeStamp.isEmpty()) {
				LocalDateTime localDateTime = LocalDateTime.parse(timeStamp,
						dateTimeFormmatter);
				String currentDateTime = localDateTime
						.format(secondDateTimeForm);
				if (currentDateTime != null && !currentDateTime.isEmpty()) {
					String[] currentDateTimeArr = currentDateTime.split(" ");
					String date = currentDateTimeArr[0];
					String time = currentDateTimeArr[1];
					itemDto.setEyDate(date);
					itemDto.setEyTime(time);
				}
			}
			itemDto.setTaxTotDoc(resDto.getCount() > 0
					? BigInteger.valueOf(resDto.getCount()) : BigInteger.ZERO);
			itemDto.setTaxInvval(resDto.getInvoiceValue());
			itemDto.setTaxTotval(resDto.getTotalTax());
			itemDto.setTaxTaxval(resDto.getTaxableValue());

			itemDto.setTaxIgstval(resDto.getTpIgst());
			itemDto.setTaxSgstval(resDto.getTpSgst());
			itemDto.setTaxCgstval(resDto.getTpCgst());
			itemDto.setTaxCessval(resDto.getTpCess());

			itemDto.setCreditIgst(resDto.getCeIgst());
			itemDto.setCreditSgst(resDto.getCeSgst());
			itemDto.setCreditCgst(resDto.getCeCgst());
			itemDto.setCreditCess(resDto.getCeCess());

			itemDto.setTotCreditEligable(resDto.getTotCreElig());
			gstr6RevIntProcSumRespDtos.add(itemDto);
		}
		return gstr6RevIntProcSumRespDtos;
	}

	private void defaultValueForCan(String gstin, Set<String> retPeriodSet,
			List<Gstr6ProcessedSummResponseDto> getGstr6ProcessedRecs) {
		List<String> retPeriods = new ArrayList<>();
		getGstr6ProcessedRecs.forEach(getGstr6ProcessedRec -> {
			retPeriods.add(getGstr6ProcessedRec.getRetPer());
		});
		for (String retPeriod : retPeriodSet) {
			if (retPeriods.contains(retPeriod))
				continue;
			Gstr6ProcessedSummResponseDto respDto = new Gstr6ProcessedSummResponseDto();
			respDto.setGstin(gstin);
			respDto.setRetPer(retPeriod);
			getGstr6ProcessedRecs.add(respDto);
		}
	}
}
