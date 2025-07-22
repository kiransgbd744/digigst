
package com.ey.advisory.app.services.daos.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.Gstr6DigiComputeDistributionEntity;
import com.ey.advisory.app.data.entities.gstr6.Gstr6DigiComputeEntity;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6DistChannelRevSumRespDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6DistChannelRevSumRespItemDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseItemDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseStringItemDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryStringResponseDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.collect.Lists;

@Service("Gstr6ReviewSummaryServiceImpl")
public class Gstr6ReviewSummaryServiceImpl
		implements Gstr6ReviewSummaryService {

	@Autowired
	@Qualifier("Gstr6ReviewSummaryDaoImpl")
	private Gstr6ReviewSummaryDaoImpl gstr6RevSumDaoImpl;

	@Autowired
	@Qualifier("Gstr6DistChannelRevSumDaoImpl")
	private Gstr6DistChannelRevSumDaoImpl gstr6DistChannelRevSumDao;

	private static final String IS_ELIGI = "IS_ELG";
	private static final String NOT_ELG = "NOT_ELG";
	private static final String ELIGIBLE = "Eligible";
	private static final String IN_ELIGIBLE = "Ineligible";
	private static final String TOTAL = "TOTAL";

	private static final String B2B_SECTION = "B2B (Section 3)";
	private static final String CDN_SECTION = "CDN (Section 6B)";
	private static final String B2BA_SECTION = "B2BA (Section 6A)";
	private static final String CDNA_SECTION = "CDNA (Section 6C)";

	private static final String INV_DISTRIBUTION = "Distribution - Invoices (Section 5)";
	private static final String CR_DISTRIBUTION = "Distribution - Credit Notes (Section 8)";
	private static final String INV_REDISTRIBUTION = "Redistribution - Invoices (Section 9)";
	private static final String CR_REDISTRIBUTION = "Redistribution - Credit Notes (Section 9)";
	private static final String ITC_SECTION = "Eligible / Ineligible ITC (Section 4)";

	public List<Gstr6ReviewSummaryResponseDto> getGstr6RevSummary(
			final Annexure1SummaryReqDto reqDto) {
		List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos = new ArrayList<>();
//		List<Gstr6ReviewSummaryResponseItemDto> respItemDtos = getGstr6InwardRespItem(
//				reqDto);
		List<Gstr6ReviewSummaryResponseItemDto> respItemDtos = getGstr6InwardResponse(
				reqDto);

		if (respItemDtos != null && !respItemDtos.isEmpty()) {

			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItem = getGstr6InwardRespItem(
					respItemDtos);

			getGstr6InwardB2BSection(mapGstr6InwardRespItem,
					gstr6RevSumRespDtos);

			getGstr6InwardCDNSection(mapGstr6InwardRespItem,
					gstr6RevSumRespDtos);

			getGstr6InwardB2BASection(mapGstr6InwardRespItem,
					gstr6RevSumRespDtos);

			getGstr6InwardCDNASection(mapGstr6InwardRespItem,
					gstr6RevSumRespDtos);

			getGstr6InwardITCSection(mapGstr6InwardRespItem,
					gstr6RevSumRespDtos);

			getGstr6InwardDINVSection(mapGstr6InwardRespItem,
					gstr6RevSumRespDtos);

			getGstr6InwardDCRSection(mapGstr6InwardRespItem,
					gstr6RevSumRespDtos);

			getGstr6InwardRDINCSection(mapGstr6InwardRespItem,
					gstr6RevSumRespDtos);

			getGstr6InwardRDCRSection(mapGstr6InwardRespItem,
					gstr6RevSumRespDtos);
		} else {
			Gstr6ReviewSummaryResponseDto b2bRespDto = new Gstr6ReviewSummaryResponseDto();
			b2bRespDto.setDocType(B2B_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> b2bItems = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto eligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			eligibleItem.setDocType(ELIGIBLE);
			b2bItems.add(eligibleItem);
			Gstr6ReviewSummaryResponseItemDto inEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			inEligibleItem.setDocType(IN_ELIGIBLE);
			b2bItems.add(inEligibleItem);
			b2bRespDto.setItems(b2bItems);
			gstr6RevSumRespDtos.add(b2bRespDto);

			Gstr6ReviewSummaryResponseDto cdnRespDto = new Gstr6ReviewSummaryResponseDto();
			cdnRespDto.setDocType(CDN_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> cdnItems = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto cdnEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			cdnEligibleItem.setDocType(ELIGIBLE);
			cdnItems.add(cdnEligibleItem);
			Gstr6ReviewSummaryResponseItemDto cdnInEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			cdnInEligibleItem.setDocType(IN_ELIGIBLE);
			cdnItems.add(cdnInEligibleItem);
			cdnRespDto.setItems(cdnItems);
			gstr6RevSumRespDtos.add(cdnRespDto);

			Gstr6ReviewSummaryResponseDto b2baRespDto = new Gstr6ReviewSummaryResponseDto();
			b2baRespDto.setDocType(B2BA_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> b2baItems = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto b2baEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			b2baEligibleItem.setDocType(ELIGIBLE);
			b2baItems.add(b2baEligibleItem);
			Gstr6ReviewSummaryResponseItemDto b2baInEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			b2baInEligibleItem.setDocType(IN_ELIGIBLE);
			b2baItems.add(b2baInEligibleItem);
			b2baRespDto.setItems(b2baItems);
			gstr6RevSumRespDtos.add(b2baRespDto);

			Gstr6ReviewSummaryResponseDto cdnaRespDto = new Gstr6ReviewSummaryResponseDto();
			cdnaRespDto.setDocType(CDNA_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> cdnaItems = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto cdnaEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			cdnaEligibleItem.setDocType(ELIGIBLE);
			cdnaItems.add(cdnaEligibleItem);
			Gstr6ReviewSummaryResponseItemDto cdnaInEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			cdnaInEligibleItem.setDocType(IN_ELIGIBLE);
			cdnaItems.add(cdnaInEligibleItem);
			cdnaRespDto.setItems(cdnaItems);
			gstr6RevSumRespDtos.add(cdnaRespDto);

			Gstr6ReviewSummaryResponseDto elInEligRespDto = new Gstr6ReviewSummaryResponseDto();
			elInEligRespDto.setDocType(ITC_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> elInEligItems = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto elInEligEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			elInEligEligibleItem.setDocType(ELIGIBLE);
			elInEligItems.add(elInEligEligibleItem);
			Gstr6ReviewSummaryResponseItemDto elInEligInEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			elInEligInEligibleItem.setDocType(IN_ELIGIBLE);
			elInEligItems.add(elInEligInEligibleItem);
			elInEligRespDto.setItems(elInEligItems);
			gstr6RevSumRespDtos.add(elInEligRespDto);

			Gstr6ReviewSummaryResponseDto deInvRespDto = new Gstr6ReviewSummaryResponseDto();
			deInvRespDto.setDocType(INV_DISTRIBUTION);
			List<Gstr6ReviewSummaryResponseItemDto> deInvItems = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto deInvEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			deInvEligibleItem.setDocType(ELIGIBLE);
			deInvItems.add(deInvEligibleItem);
			Gstr6ReviewSummaryResponseItemDto deInvInEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			deInvInEligibleItem.setDocType(IN_ELIGIBLE);
			deInvItems.add(inEligibleItem);
			deInvRespDto.setItems(deInvItems);
			gstr6RevSumRespDtos.add(deInvRespDto);

			Gstr6ReviewSummaryResponseDto crDiRespDto = new Gstr6ReviewSummaryResponseDto();
			crDiRespDto.setDocType(CR_DISTRIBUTION);
			List<Gstr6ReviewSummaryResponseItemDto> crDiItems = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto crDiEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			crDiEligibleItem.setDocType(ELIGIBLE);
			crDiItems.add(crDiEligibleItem);
			Gstr6ReviewSummaryResponseItemDto crDiInEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			crDiInEligibleItem.setDocType(IN_ELIGIBLE);
			crDiItems.add(crDiInEligibleItem);
			crDiRespDto.setItems(crDiItems);
			gstr6RevSumRespDtos.add(crDiRespDto);

			Gstr6ReviewSummaryResponseDto invReDiRespDto = new Gstr6ReviewSummaryResponseDto();
			invReDiRespDto.setDocType(INV_REDISTRIBUTION);
			List<Gstr6ReviewSummaryResponseItemDto> invReDiItems = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto invReDiEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			invReDiEligibleItem.setDocType(ELIGIBLE);
			invReDiItems.add(invReDiEligibleItem);
			Gstr6ReviewSummaryResponseItemDto invReDiInEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			invReDiInEligibleItem.setDocType(IN_ELIGIBLE);
			invReDiItems.add(invReDiInEligibleItem);
			invReDiRespDto.setItems(invReDiItems);
			gstr6RevSumRespDtos.add(invReDiRespDto);

			Gstr6ReviewSummaryResponseDto crDistRespDto = new Gstr6ReviewSummaryResponseDto();
			crDistRespDto.setDocType(CR_REDISTRIBUTION);
			List<Gstr6ReviewSummaryResponseItemDto> crDistItems = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto crDistEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			crDistEligibleItem.setDocType(ELIGIBLE);
			crDistItems.add(crDistEligibleItem);
			Gstr6ReviewSummaryResponseItemDto crDistInEligibleItem = new Gstr6ReviewSummaryResponseItemDto();
			crDistInEligibleItem.setDocType(IN_ELIGIBLE);
			crDistItems.add(crDistInEligibleItem);
			crDistRespDto.setItems(crDistItems);
			gstr6RevSumRespDtos.add(crDistRespDto);
		}
		return gstr6RevSumRespDtos;
	}

	private void getGstr6InwardB2BSection(
			final Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos) {

		List<Gstr6ReviewSummaryResponseItemDto> respB2BDtos = mapGstr6InwardRespItems
				.get("B2B");
		if (respB2BDtos != null) {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();

			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> eligebleItem = getGstr6InwardRespEligibleItem(
					respB2BDtos);
			List<Gstr6ReviewSummaryResponseItemDto> listEligibleRevSum = eligebleItem
					.get(IS_ELIGI);
			// Eligible
			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {
				listEligibleRevSum.forEach(respDto -> {
					Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemEliDto.setDocType(ELIGIBLE);
					respItemEliDto.setAspTotTax(respDto.getAspTotTax());
					respItemEliDto.setAspIgst(respDto.getAspIgst());
					respItemEliDto.setAspSgst(respDto.getAspSgst());
					respItemEliDto.setAspCgst(respDto.getAspCgst());
					respItemEliDto.setAspCess(respDto.getAspCess());

					respItemEliDto.setGstnTotTax(respDto.getGstnTotTax());
					respItemEliDto.setGstnIgst(respDto.getGstnIgst());
					respItemEliDto.setGstnSgst(respDto.getGstnSgst());
					respItemEliDto.setGstnCgst(respDto.getGstnCgst());
					respItemEliDto.setGstnCess(respDto.getGstnCess());

					respItemEliDto.setDiffTotTax(null);
					respItemEliDto.setDiffIgst(null);
					respItemEliDto.setDiffSgst(null);
					respItemEliDto.setDiffCgst(null);
					respItemEliDto.setDiffCess(null);
					revSumRespItemDtos.add(respItemEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}
			List<Gstr6ReviewSummaryResponseItemDto> listInEligibleRevSum = eligebleItem
					.get(NOT_ELG);
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {
				listInEligibleRevSum.forEach(respDto -> {
					Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemInEliDto.setDocType(IN_ELIGIBLE);
					respItemInEliDto.setAspTotTax(respDto.getAspTotTax());
					respItemInEliDto.setAspIgst(respDto.getAspIgst());
					respItemInEliDto.setAspSgst(respDto.getAspSgst());
					respItemInEliDto.setAspCgst(respDto.getAspCgst());
					respItemInEliDto.setAspCess(respDto.getAspCess());

					respItemInEliDto.setGstnTotTax(respDto.getGstnTotTax());
					respItemInEliDto.setGstnIgst(respDto.getGstnIgst());
					respItemInEliDto.setGstnSgst(respDto.getGstnSgst());
					respItemInEliDto.setGstnCgst(respDto.getGstnCgst());
					respItemInEliDto.setGstnCess(respDto.getGstnCess());

					respItemInEliDto.setDiffTotTax(null);
					respItemInEliDto.setDiffIgst(null);
					respItemInEliDto.setDiffSgst(null);
					respItemInEliDto.setDiffCgst(null);
					respItemInEliDto.setDiffCess(null);
					revSumRespItemDtos.add(respItemInEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemInEliDto.setDocType(IN_ELIGIBLE);
				revSumRespItemDtos.add(respItemInEliDto);
			}
			// Total
			List<Gstr6ReviewSummaryResponseItemDto> listTotalRevSum = eligebleItem
					.get(TOTAL);
			if (listTotalRevSum != null && !listTotalRevSum.isEmpty()) {
				listTotalRevSum.forEach(respDto -> {
					revSumRespDto.setDocType(B2B_SECTION);
					revSumRespDto.setAspCount(respDto.getAspCount());
					revSumRespDto.setAspCess(respDto.getAspCess());
					revSumRespDto.setAspCgst(respDto.getAspCgst());
					revSumRespDto.setAspIgst(respDto.getAspIgst());
					revSumRespDto.setAspSgst(respDto.getAspSgst());
					revSumRespDto.setAspInvValue(respDto.getAspInvValue());
					revSumRespDto.setAspTaxbValue(respDto.getAspTaxbValue());
					revSumRespDto.setAspTotTax(respDto.getAspTotTax());

					revSumRespDto.setGstnCount(respDto.getGstnCount());
					revSumRespDto.setGstnInvValue(respDto.getGstnInvValue());
					revSumRespDto.setGstnTaxbValue(respDto.getGstnTaxbValue());
					revSumRespDto.setGstnTotTax(respDto.getGstnTotTax());
					revSumRespDto.setGstnIgst(respDto.getGstnIgst());
					revSumRespDto.setGstnSgst(respDto.getGstnSgst());
					revSumRespDto.setGstnCgst(respDto.getGstnCgst());
					revSumRespDto.setGstnCess(respDto.getGstnCess());

					revSumRespDto.setDiffCount(respDto.getDiffCount());
					revSumRespDto.setDiffInvValue(respDto.getDiffInvValue());
					revSumRespDto.setDiffTaxbValue(respDto.getDiffTaxbValue());
					revSumRespDto.setDiffTotTax(respDto.getDiffTotTax());
					revSumRespDto.setDiffIgst(respDto.getDiffIgst());
					revSumRespDto.setDiffSgst(respDto.getDiffSgst());
					revSumRespDto.setDiffCgst(respDto.getDiffCgst());
					revSumRespDto.setDiffCess(respDto.getDiffCess());
				});
			}
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		} else {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			revSumRespDto.setDocType(B2B_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemEliDto.setDocType(ELIGIBLE);
			revSumRespItemDtos.add(respItemEliDto);

			Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemInEliDto.setDocType(IN_ELIGIBLE);
			revSumRespItemDtos.add(respItemInEliDto);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		}
	}

	private void getGstr6InwardCDNSection(
			final Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos) {
		List<Gstr6ReviewSummaryResponseItemDto> respCDNDtos = mapGstr6InwardRespItems
				.get("CDN");
		if (respCDNDtos != null) {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();

			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> eligebleItem = getGstr6InwardRespEligibleItem(
					respCDNDtos);
			List<Gstr6ReviewSummaryResponseItemDto> listTotalRevSum = eligebleItem
					.get(TOTAL);
			// Total
			if (listTotalRevSum != null && !listTotalRevSum.isEmpty()) {
				listTotalRevSum.forEach(respCDNDto -> {
					revSumRespDto.setDocType(CDN_SECTION);
					revSumRespDto.setAspCount(respCDNDto.getAspCount());
					revSumRespDto.setAspCess(respCDNDto.getAspCess());
					revSumRespDto.setAspCgst(respCDNDto.getAspCgst());
					revSumRespDto.setAspIgst(respCDNDto.getAspIgst());
					revSumRespDto.setAspSgst(respCDNDto.getAspSgst());
					revSumRespDto.setAspInvValue(respCDNDto.getAspInvValue());
					revSumRespDto.setAspTaxbValue(respCDNDto.getAspTaxbValue());
					revSumRespDto.setAspTotTax(respCDNDto.getAspTotTax());

					revSumRespDto.setGstnCount(respCDNDto.getGstnCount());
					revSumRespDto.setGstnInvValue(respCDNDto.getGstnInvValue());
					revSumRespDto
							.setGstnTaxbValue(respCDNDto.getGstnTaxbValue());
					revSumRespDto.setGstnTotTax(respCDNDto.getGstnTotTax());
					revSumRespDto.setGstnIgst(respCDNDto.getGstnIgst());
					revSumRespDto.setGstnSgst(respCDNDto.getGstnSgst());
					revSumRespDto.setGstnCgst(respCDNDto.getGstnCgst());
					revSumRespDto.setGstnCess(respCDNDto.getGstnCess());

					revSumRespDto.setDiffCount(respCDNDto.getDiffCount());
					revSumRespDto.setDiffInvValue(respCDNDto.getDiffInvValue());
					revSumRespDto
							.setDiffTaxbValue(respCDNDto.getDiffTaxbValue());
					revSumRespDto.setDiffTotTax(respCDNDto.getDiffTotTax());
					revSumRespDto.setDiffIgst(respCDNDto.getDiffIgst());
					revSumRespDto.setDiffSgst(respCDNDto.getDiffSgst());
					revSumRespDto.setDiffCgst(respCDNDto.getDiffCgst());
					revSumRespDto.setDiffCess(respCDNDto.getDiffCess());
				});
			}
			// Eligible
			List<Gstr6ReviewSummaryResponseItemDto> listEligibleRevSum = eligebleItem
					.get(IS_ELIGI);
			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {
				listEligibleRevSum.forEach(respCDNDto -> {
					Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemEliDto.setDocType(ELIGIBLE);

					respItemEliDto.setAspTotTax(respCDNDto.getAspTotTax());
					respItemEliDto.setAspIgst(respCDNDto.getAspIgst());
					respItemEliDto.setAspSgst(respCDNDto.getAspSgst());
					respItemEliDto.setAspCgst(respCDNDto.getAspCgst());
					respItemEliDto.setAspCess(respCDNDto.getAspCess());

					respItemEliDto.setGstnTotTax(respCDNDto.getGstnTotTax());
					respItemEliDto.setGstnIgst(respCDNDto.getGstnIgst());
					respItemEliDto.setGstnSgst(respCDNDto.getGstnSgst());
					respItemEliDto.setGstnCgst(respCDNDto.getGstnCgst());
					respItemEliDto.setGstnCess(respCDNDto.getGstnCess());

					respItemEliDto.setDiffTotTax(null);
					respItemEliDto.setDiffIgst(null);
					respItemEliDto.setDiffSgst(null);
					respItemEliDto.setDiffCgst(null);
					respItemEliDto.setDiffCess(null);

					revSumRespItemDtos.add(respItemEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}

			List<Gstr6ReviewSummaryResponseItemDto> listInEligibleRevSum = eligebleItem
					.get(NOT_ELG);
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {
				listInEligibleRevSum.forEach(respCDNDto -> {
					Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemInEliDto.setDocType(IN_ELIGIBLE);

					respItemInEliDto.setAspTotTax(respCDNDto.getAspTotTax());
					respItemInEliDto.setAspIgst(respCDNDto.getAspIgst());
					respItemInEliDto.setAspSgst(respCDNDto.getAspSgst());
					respItemInEliDto.setAspCgst(respCDNDto.getAspCgst());
					respItemInEliDto.setAspCess(respCDNDto.getAspCess());

					respItemInEliDto.setGstnTotTax(respCDNDto.getGstnTotTax());
					respItemInEliDto.setGstnIgst(respCDNDto.getGstnIgst());
					respItemInEliDto.setGstnSgst(respCDNDto.getGstnSgst());
					respItemInEliDto.setGstnCgst(respCDNDto.getGstnCgst());
					respItemInEliDto.setGstnCess(respCDNDto.getGstnCess());

					respItemInEliDto.setDiffTotTax(null);
					respItemInEliDto.setDiffIgst(null);
					respItemInEliDto.setDiffSgst(null);
					respItemInEliDto.setDiffCgst(null);
					respItemInEliDto.setDiffCess(null);
					revSumRespItemDtos.add(respItemInEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(IN_ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		} else {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			revSumRespDto.setDocType(CDN_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemEliDto.setDocType(ELIGIBLE);
			revSumRespItemDtos.add(respItemEliDto);

			Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemInEliDto.setDocType(IN_ELIGIBLE);
			revSumRespItemDtos.add(respItemInEliDto);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		}
	}

	private void getGstr6InwardB2BASection(
			final Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos) {
		List<Gstr6ReviewSummaryResponseItemDto> respB2BADtos = mapGstr6InwardRespItems
				.get("B2BA");
		if (respB2BADtos != null) {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> eligebleItem = getGstr6InwardRespEligibleItem(
					respB2BADtos);
			List<Gstr6ReviewSummaryResponseItemDto> listTotalRevSum = eligebleItem
					.get(TOTAL);

			// Total
			if (listTotalRevSum != null && !listTotalRevSum.isEmpty()) {
				listTotalRevSum.forEach(respB2BADto -> {

					revSumRespDto.setDocType(B2BA_SECTION);
					revSumRespDto.setAspCount(respB2BADto.getAspCount());
					revSumRespDto.setAspCess(respB2BADto.getAspCess());
					revSumRespDto.setAspCgst(respB2BADto.getAspCgst());
					revSumRespDto.setAspIgst(respB2BADto.getAspIgst());
					revSumRespDto.setAspSgst(respB2BADto.getAspSgst());
					revSumRespDto.setAspInvValue(respB2BADto.getAspInvValue());
					revSumRespDto
							.setAspTaxbValue(respB2BADto.getAspTaxbValue());
					revSumRespDto.setAspTotTax(respB2BADto.getAspTotTax());

					revSumRespDto.setGstnCount(respB2BADto.getGstnCount());
					revSumRespDto
							.setGstnInvValue(respB2BADto.getGstnInvValue());
					revSumRespDto
							.setGstnTaxbValue(respB2BADto.getGstnTaxbValue());
					revSumRespDto.setGstnTotTax(respB2BADto.getGstnTotTax());
					revSumRespDto.setGstnIgst(respB2BADto.getGstnIgst());
					revSumRespDto.setGstnSgst(respB2BADto.getGstnSgst());
					revSumRespDto.setGstnCgst(respB2BADto.getGstnCgst());
					revSumRespDto.setGstnCess(respB2BADto.getGstnCess());

					revSumRespDto.setDiffCount(respB2BADto.getDiffCount());
					revSumRespDto
							.setDiffInvValue(respB2BADto.getDiffInvValue());
					revSumRespDto
							.setDiffTaxbValue(respB2BADto.getDiffTaxbValue());
					revSumRespDto.setDiffTotTax(respB2BADto.getDiffTotTax());
					revSumRespDto.setDiffIgst(respB2BADto.getDiffIgst());
					revSumRespDto.setDiffSgst(respB2BADto.getDiffSgst());
					revSumRespDto.setDiffCgst(respB2BADto.getDiffCgst());
					revSumRespDto.setDiffCess(respB2BADto.getDiffCess());
				});
			}
			List<Gstr6ReviewSummaryResponseItemDto> listEligibleRevSum = eligebleItem
					.get(IS_ELIGI);
			// Eligible
			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {
				listEligibleRevSum.forEach(respB2BADto -> {

					Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemEliDto.setDocType(ELIGIBLE);

					respItemEliDto.setAspTotTax(respB2BADto.getAspTotTax());
					respItemEliDto.setAspIgst(respB2BADto.getAspIgst());
					respItemEliDto.setAspSgst(respB2BADto.getAspSgst());
					respItemEliDto.setAspCgst(respB2BADto.getAspCgst());
					respItemEliDto.setAspCess(respB2BADto.getAspCess());

					respItemEliDto.setGstnTotTax(respB2BADto.getGstnTotTax());
					respItemEliDto.setGstnIgst(respB2BADto.getGstnIgst());
					respItemEliDto.setGstnSgst(respB2BADto.getGstnSgst());
					respItemEliDto.setGstnCgst(respB2BADto.getGstnCgst());
					respItemEliDto.setGstnCess(respB2BADto.getGstnCess());

					respItemEliDto.setDiffTotTax(null);
					respItemEliDto.setDiffIgst(null);
					respItemEliDto.setDiffSgst(null);
					respItemEliDto.setDiffCgst(null);
					respItemEliDto.setDiffCess(null);
					revSumRespItemDtos.add(respItemEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}

			List<Gstr6ReviewSummaryResponseItemDto> listInEligibleRevSum = eligebleItem
					.get(NOT_ELG);
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {
				listInEligibleRevSum.forEach(respB2BADto -> {
					Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemInEliDto.setDocType(IN_ELIGIBLE);

					respItemInEliDto.setAspTotTax(respB2BADto.getAspTotTax());
					respItemInEliDto.setAspIgst(respB2BADto.getAspIgst());
					respItemInEliDto.setAspSgst(respB2BADto.getAspSgst());
					respItemInEliDto.setAspCgst(respB2BADto.getAspCgst());
					respItemInEliDto.setAspCess(respB2BADto.getAspCess());

					respItemInEliDto.setGstnTotTax(respB2BADto.getGstnTotTax());
					respItemInEliDto.setGstnIgst(respB2BADto.getGstnIgst());
					respItemInEliDto.setGstnSgst(respB2BADto.getGstnSgst());
					respItemInEliDto.setGstnCgst(respB2BADto.getGstnCgst());
					respItemInEliDto.setGstnCess(respB2BADto.getGstnCess());

					respItemInEliDto.setDiffTotTax(null);
					respItemInEliDto.setDiffIgst(null);
					respItemInEliDto.setDiffSgst(null);
					respItemInEliDto.setDiffCgst(null);
					respItemInEliDto.setDiffCess(null);
					revSumRespItemDtos.add(respItemInEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(IN_ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		} else {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			revSumRespDto.setDocType(B2BA_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemEliDto.setDocType(ELIGIBLE);
			revSumRespItemDtos.add(respItemEliDto);

			Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemInEliDto.setDocType(IN_ELIGIBLE);
			revSumRespItemDtos.add(respItemInEliDto);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		}
	}

	private void getGstr6InwardCDNASection(
			final Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos) {
		List<Gstr6ReviewSummaryResponseItemDto> respCDNADtos = mapGstr6InwardRespItems
				.get("CDNA");
		if (respCDNADtos != null) {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> eligebleItem = getGstr6InwardRespEligibleItem(
					respCDNADtos);
			List<Gstr6ReviewSummaryResponseItemDto> listTotalRevSum = eligebleItem
					.get(TOTAL);

			// Total
			if (listTotalRevSum != null && !listTotalRevSum.isEmpty()) {
				listTotalRevSum.forEach(respCDNADto -> {
					revSumRespDto.setDocType(CDNA_SECTION);
					revSumRespDto.setAspCount(respCDNADto.getAspCount());
					revSumRespDto.setAspCess(respCDNADto.getAspCess());
					revSumRespDto.setAspCgst(respCDNADto.getAspCgst());
					revSumRespDto.setAspIgst(respCDNADto.getAspIgst());
					revSumRespDto.setAspSgst(respCDNADto.getAspSgst());
					revSumRespDto.setAspInvValue(respCDNADto.getAspInvValue());
					revSumRespDto
							.setAspTaxbValue(respCDNADto.getAspTaxbValue());
					revSumRespDto.setAspTotTax(respCDNADto.getAspTotTax());

					revSumRespDto.setGstnCount(respCDNADto.getGstnCount());
					revSumRespDto
							.setGstnInvValue(respCDNADto.getGstnInvValue());
					revSumRespDto
							.setGstnTaxbValue(respCDNADto.getGstnTaxbValue());
					revSumRespDto.setGstnTotTax(respCDNADto.getGstnTotTax());
					revSumRespDto.setGstnIgst(respCDNADto.getGstnIgst());
					revSumRespDto.setGstnSgst(respCDNADto.getGstnSgst());
					revSumRespDto.setGstnCgst(respCDNADto.getGstnCgst());
					revSumRespDto.setGstnCess(respCDNADto.getGstnCess());

					revSumRespDto.setDiffCount(respCDNADto.getDiffCount());
					revSumRespDto
							.setDiffInvValue(respCDNADto.getDiffInvValue());
					revSumRespDto
							.setDiffTaxbValue(respCDNADto.getDiffTaxbValue());
					revSumRespDto.setDiffTotTax(respCDNADto.getDiffTotTax());
					revSumRespDto.setDiffIgst(respCDNADto.getDiffIgst());
					revSumRespDto.setDiffSgst(respCDNADto.getDiffSgst());
					revSumRespDto.setDiffCgst(respCDNADto.getDiffCgst());
					revSumRespDto.setDiffCess(respCDNADto.getDiffCess());
				});
			}

			List<Gstr6ReviewSummaryResponseItemDto> listEligibleRevSum = eligebleItem
					.get(IS_ELIGI);
			// Eligible
			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {
				listEligibleRevSum.forEach(respCDNADto -> {

					Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemEliDto.setDocType(ELIGIBLE);

					respItemEliDto.setAspTotTax(respCDNADto.getAspTotTax());
					respItemEliDto.setAspIgst(respCDNADto.getAspIgst());
					respItemEliDto.setAspSgst(respCDNADto.getAspSgst());
					respItemEliDto.setAspCgst(respCDNADto.getAspCgst());
					respItemEliDto.setAspCess(respCDNADto.getAspCess());

					respItemEliDto.setGstnTotTax(respCDNADto.getGstnTotTax());
					respItemEliDto.setGstnIgst(respCDNADto.getGstnIgst());
					respItemEliDto.setGstnSgst(respCDNADto.getGstnSgst());
					respItemEliDto.setGstnCgst(respCDNADto.getGstnCgst());
					respItemEliDto.setGstnCess(respCDNADto.getGstnCess());

					respItemEliDto.setDiffTotTax(null);
					respItemEliDto.setDiffIgst(null);
					respItemEliDto.setDiffSgst(null);
					respItemEliDto.setDiffCgst(null);
					respItemEliDto.setDiffCess(null);

					revSumRespItemDtos.add(respItemEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}

			List<Gstr6ReviewSummaryResponseItemDto> listInEligibleRevSum = eligebleItem
					.get(NOT_ELG);
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {
				listInEligibleRevSum.forEach(respCDNADto -> {

					Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemInEliDto.setDocType(IN_ELIGIBLE);

					respItemInEliDto.setAspTotTax(respCDNADto.getAspTotTax());
					respItemInEliDto.setAspIgst(respCDNADto.getAspIgst());
					respItemInEliDto.setAspSgst(respCDNADto.getAspSgst());
					respItemInEliDto.setAspCgst(respCDNADto.getAspCgst());
					respItemInEliDto.setAspCess(respCDNADto.getAspCess());

					respItemInEliDto.setGstnTotTax(respCDNADto.getGstnTotTax());
					respItemInEliDto.setGstnIgst(respCDNADto.getGstnIgst());
					respItemInEliDto.setGstnSgst(respCDNADto.getGstnSgst());
					respItemInEliDto.setGstnCgst(respCDNADto.getGstnCgst());
					respItemInEliDto.setGstnCess(respCDNADto.getGstnCess());

					respItemInEliDto.setDiffTotTax(null);
					respItemInEliDto.setDiffIgst(null);
					respItemInEliDto.setDiffSgst(null);
					respItemInEliDto.setDiffCgst(null);
					respItemInEliDto.setDiffCess(null);
					revSumRespItemDtos.add(respItemInEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(IN_ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		} else {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			revSumRespDto.setDocType(CDNA_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemEliDto.setDocType(ELIGIBLE);
			revSumRespItemDtos.add(respItemEliDto);

			Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemInEliDto.setDocType(IN_ELIGIBLE);
			revSumRespItemDtos.add(respItemInEliDto);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		}
	}

	private void getGstr6InwardITCSection(
			final Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos) {
		List<Gstr6ReviewSummaryResponseItemDto> respItcCrossDtos = mapGstr6InwardRespItems
				.get("ITC_CROSS");
		if (respItcCrossDtos != null) {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> eligebleItem = getGstr6InwardRespEligibleItem(
					respItcCrossDtos);
			List<Gstr6ReviewSummaryResponseItemDto> listTotalRevSum = eligebleItem
					.get(TOTAL);

			// Total
			if (listTotalRevSum != null && !listTotalRevSum.isEmpty()) {
				listTotalRevSum.forEach(respCDNADto -> {
					revSumRespDto.setDocType(ITC_SECTION);
					revSumRespDto.setAspCount(respCDNADto.getAspCount());
					revSumRespDto.setAspCess(respCDNADto.getAspCess());
					revSumRespDto.setAspCgst(respCDNADto.getAspCgst());
					revSumRespDto.setAspIgst(respCDNADto.getAspIgst());
					revSumRespDto.setAspSgst(respCDNADto.getAspSgst());
					revSumRespDto.setAspInvValue(respCDNADto.getAspInvValue());
					revSumRespDto
							.setAspTaxbValue(respCDNADto.getAspTaxbValue());
					revSumRespDto.setAspTotTax(respCDNADto.getAspTotTax());

					revSumRespDto.setGstnCount(respCDNADto.getGstnCount());
					revSumRespDto
							.setGstnInvValue(respCDNADto.getGstnInvValue());
					revSumRespDto
							.setGstnTaxbValue(respCDNADto.getGstnTaxbValue());
					revSumRespDto.setGstnTotTax(respCDNADto.getGstnTotTax());
					revSumRespDto.setGstnIgst(respCDNADto.getGstnIgst());
					revSumRespDto.setGstnSgst(respCDNADto.getGstnSgst());
					revSumRespDto.setGstnCgst(respCDNADto.getGstnCgst());
					revSumRespDto.setGstnCess(respCDNADto.getGstnCess());

					revSumRespDto.setDiffCount(respCDNADto.getDiffCount());
					revSumRespDto
							.setDiffInvValue(respCDNADto.getDiffInvValue());
					revSumRespDto
							.setDiffTaxbValue(respCDNADto.getDiffTaxbValue());
					revSumRespDto.setDiffTotTax(respCDNADto.getDiffTotTax());
					revSumRespDto.setDiffIgst(respCDNADto.getDiffIgst());
					revSumRespDto.setDiffSgst(respCDNADto.getDiffSgst());
					revSumRespDto.setDiffCgst(respCDNADto.getDiffCgst());
					revSumRespDto.setDiffCess(respCDNADto.getDiffCess());
				});
			}

			List<Gstr6ReviewSummaryResponseItemDto> listEligibleRevSum = eligebleItem
					.get(IS_ELIGI);
			// Eligible
			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {
				listEligibleRevSum.forEach(respCDNADto -> {

					Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemEliDto.setDocType(ELIGIBLE);
					respItemEliDto.setAspTotTax(respCDNADto.getAspTotTax());
					respItemEliDto.setAspIgst(respCDNADto.getAspIgst());
					respItemEliDto.setAspSgst(respCDNADto.getAspSgst());
					respItemEliDto.setAspCgst(respCDNADto.getAspCgst());
					respItemEliDto.setAspCess(respCDNADto.getAspCess());

					respItemEliDto.setGstnTotTax(respCDNADto.getGstnTotTax());
					respItemEliDto.setGstnIgst(respCDNADto.getGstnIgst());
					respItemEliDto.setGstnSgst(respCDNADto.getGstnSgst());
					respItemEliDto.setGstnCgst(respCDNADto.getGstnCgst());
					respItemEliDto.setGstnCess(respCDNADto.getGstnCess());

					respItemEliDto.setDiffTotTax(respCDNADto.getDiffTotTax());
					respItemEliDto.setDiffIgst(respCDNADto.getDiffIgst());
					respItemEliDto.setDiffSgst(respCDNADto.getDiffSgst());
					respItemEliDto.setDiffCgst(respCDNADto.getDiffCgst());
					respItemEliDto.setDiffCess(respCDNADto.getDiffCess());

					revSumRespItemDtos.add(respItemEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}

			List<Gstr6ReviewSummaryResponseItemDto> listInEligibleRevSum = eligebleItem
					.get(NOT_ELG);
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {
				listInEligibleRevSum.forEach(respCDNADto -> {

					Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
					respItemInEliDto.setDocType(IN_ELIGIBLE);
					respItemInEliDto.setAspTotTax(respCDNADto.getAspTotTax());
					respItemInEliDto.setAspIgst(respCDNADto.getAspIgst());
					respItemInEliDto.setAspSgst(respCDNADto.getAspSgst());
					respItemInEliDto.setAspCgst(respCDNADto.getAspCgst());
					respItemInEliDto.setAspCess(respCDNADto.getAspCess());

					respItemInEliDto.setGstnTotTax(respCDNADto.getGstnTotTax());
					respItemInEliDto.setGstnIgst(respCDNADto.getGstnIgst());
					respItemInEliDto.setGstnSgst(respCDNADto.getGstnSgst());
					respItemInEliDto.setGstnCgst(respCDNADto.getGstnCgst());
					respItemInEliDto.setGstnCess(respCDNADto.getGstnCess());

					respItemInEliDto.setDiffTotTax(respCDNADto.getDiffTotTax());
					respItemInEliDto.setDiffIgst(respCDNADto.getDiffIgst());
					respItemInEliDto.setDiffSgst(respCDNADto.getDiffSgst());
					respItemInEliDto.setDiffCgst(respCDNADto.getDiffCgst());
					respItemInEliDto.setDiffCess(respCDNADto.getDiffCess());
					revSumRespItemDtos.add(respItemInEliDto);
				});
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(IN_ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		} else {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			revSumRespDto.setDocType(ITC_SECTION);
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemEliDto.setDocType(ELIGIBLE);
			revSumRespItemDtos.add(respItemEliDto);

			Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemInEliDto.setDocType(IN_ELIGIBLE);
			revSumRespItemDtos.add(respItemInEliDto);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		}
	}

	private void getGstr6InwardDINVSection(
			final Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos) {
		List<Gstr6ReviewSummaryResponseItemDto> respDInvDtos = mapGstr6InwardRespItems
				.get("D_INV");
		if (respDInvDtos != null && !respDInvDtos.isEmpty()) {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> eligebleItem = getGstr6InwardRespEligibleItem(
					respDInvDtos);
			List<Gstr6ReviewSummaryResponseItemDto> listEligibleRevSum = eligebleItem
					.get(IS_ELIGI);

			// Eligible for Review Summary screen
			BigInteger aspCount = BigInteger.ZERO;
			BigDecimal aspInvValue = BigDecimal.ZERO;
			BigDecimal aspTaxbValue = BigDecimal.ZERO;
			BigDecimal aspTotTax = BigDecimal.ZERO;
			BigDecimal aspIgst = BigDecimal.ZERO;
			BigDecimal aspCgst = BigDecimal.ZERO;
			BigDecimal aspSgst = BigDecimal.ZERO;
			BigDecimal aspCess = BigDecimal.ZERO;

			BigInteger gstnCount = BigInteger.ZERO;
			BigDecimal gstnInvValue = BigDecimal.ZERO;
			BigDecimal gstnTaxbValue = BigDecimal.ZERO;
			BigDecimal gstnTotTax = BigDecimal.ZERO;
			BigDecimal gstnIgst = BigDecimal.ZERO;
			BigDecimal gstnCgst = BigDecimal.ZERO;
			BigDecimal gstnSgst = BigDecimal.ZERO;
			BigDecimal gstnCess = BigDecimal.ZERO;

			BigInteger diffCount = BigInteger.ZERO;
			BigDecimal diffInvValue = BigDecimal.ZERO;
			BigDecimal diffTaxbValue = BigDecimal.ZERO;
			BigDecimal diffTotTax = BigDecimal.ZERO;
			BigDecimal diffIgst = BigDecimal.ZERO;
			BigDecimal diffCgst = BigDecimal.ZERO;
			BigDecimal diffSgst = BigDecimal.ZERO;
			BigDecimal diffCess = BigDecimal.ZERO;

			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {

				for (Gstr6ReviewSummaryResponseItemDto respDto : listEligibleRevSum) {
					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(ELIGIBLE);
					revSumRespItemDtos.add(respDto);
				}
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}
			List<Gstr6ReviewSummaryResponseItemDto> listInEligibleRevSum = eligebleItem
					.get(NOT_ELG);
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {

				for (Gstr6ReviewSummaryResponseItemDto respDto : listInEligibleRevSum) {
					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);
					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(IN_ELIGIBLE);
					revSumRespItemDtos.add(respDto);
				}
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemInEliDto.setDocType(IN_ELIGIBLE);
				revSumRespItemDtos.add(respItemInEliDto);
			}
			revSumRespDto.setDocType(INV_DISTRIBUTION);
			revSumRespDto.setAspCount(aspCount);
			revSumRespDto.setAspCess(aspCess);
			revSumRespDto.setAspCgst(aspCgst);
			revSumRespDto.setAspIgst(aspIgst);
			revSumRespDto.setAspSgst(aspSgst);
			revSumRespDto.setAspInvValue(aspInvValue);
			revSumRespDto.setAspTaxbValue(aspTaxbValue);
			revSumRespDto.setAspTotTax(aspTotTax);

			revSumRespDto.setGstnCount(gstnCount);
			revSumRespDto.setGstnInvValue(gstnInvValue);
			revSumRespDto.setGstnTaxbValue(gstnTaxbValue);
			revSumRespDto.setGstnTotTax(gstnTotTax);
			revSumRespDto.setGstnIgst(gstnIgst);
			revSumRespDto.setGstnSgst(gstnSgst);
			revSumRespDto.setGstnCgst(gstnCgst);
			revSumRespDto.setGstnCess(gstnCess);

			revSumRespDto.setDiffCount(diffCount);
			revSumRespDto.setDiffInvValue(diffInvValue);
			revSumRespDto.setDiffTaxbValue(diffTaxbValue);
			revSumRespDto.setDiffTotTax(diffTotTax);
			revSumRespDto.setDiffIgst(diffIgst);
			revSumRespDto.setDiffSgst(diffSgst);
			revSumRespDto.setDiffCgst(diffCgst);
			revSumRespDto.setDiffCess(diffCess);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		} else {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			revSumRespDto.setDocType(INV_DISTRIBUTION);
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemEliDto.setDocType(ELIGIBLE);
			revSumRespItemDtos.add(respItemEliDto);

			Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemInEliDto.setDocType(IN_ELIGIBLE);
			revSumRespItemDtos.add(respItemInEliDto);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		}
	}

	private void getGstr6InwardDCRSection(
			final Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos) {

		List<Gstr6ReviewSummaryResponseItemDto> respCCRDtos = mapGstr6InwardRespItems
				.get("D_CR");
		if (respCCRDtos != null && !respCCRDtos.isEmpty()) {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();

			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> eligebleItem = getGstr6InwardRespEligibleItem(
					respCCRDtos);

			// Eligible for Review Summary screen
			BigInteger aspCount = BigInteger.ZERO;
			BigDecimal aspInvValue = BigDecimal.ZERO;
			BigDecimal aspTaxbValue = BigDecimal.ZERO;
			BigDecimal aspTotTax = BigDecimal.ZERO;
			BigDecimal aspIgst = BigDecimal.ZERO;
			BigDecimal aspCgst = BigDecimal.ZERO;
			BigDecimal aspSgst = BigDecimal.ZERO;
			BigDecimal aspCess = BigDecimal.ZERO;

			BigInteger gstnCount = BigInteger.ZERO;
			BigDecimal gstnInvValue = BigDecimal.ZERO;
			BigDecimal gstnTaxbValue = BigDecimal.ZERO;
			BigDecimal gstnTotTax = BigDecimal.ZERO;
			BigDecimal gstnIgst = BigDecimal.ZERO;
			BigDecimal gstnCgst = BigDecimal.ZERO;
			BigDecimal gstnSgst = BigDecimal.ZERO;
			BigDecimal gstnCess = BigDecimal.ZERO;

			BigInteger diffCount = BigInteger.ZERO;
			BigDecimal diffInvValue = BigDecimal.ZERO;
			BigDecimal diffTaxbValue = BigDecimal.ZERO;
			BigDecimal diffTotTax = BigDecimal.ZERO;
			BigDecimal diffIgst = BigDecimal.ZERO;
			BigDecimal diffCgst = BigDecimal.ZERO;
			BigDecimal diffSgst = BigDecimal.ZERO;
			BigDecimal diffCess = BigDecimal.ZERO;
			// Eligible
			List<Gstr6ReviewSummaryResponseItemDto> listEligibleRevSum = eligebleItem
					.get(IS_ELIGI);
			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {
				for (Gstr6ReviewSummaryResponseItemDto respDto : listEligibleRevSum) {
					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(ELIGIBLE);
					revSumRespItemDtos.add(respDto);
				}
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}

			List<Gstr6ReviewSummaryResponseItemDto> listInEligibleRevSum = eligebleItem
					.get(NOT_ELG);
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {

				for (Gstr6ReviewSummaryResponseItemDto respDto : listInEligibleRevSum) {
					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(IN_ELIGIBLE);
					revSumRespItemDtos.add(respDto);
				}

			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(IN_ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}
			revSumRespDto.setDocType(CR_DISTRIBUTION);
			revSumRespDto.setAspCount(aspCount);
			revSumRespDto.setAspCess(aspCess);
			revSumRespDto.setAspCgst(aspCgst);
			revSumRespDto.setAspIgst(aspIgst);
			revSumRespDto.setAspSgst(aspSgst);
			revSumRespDto.setAspInvValue(aspInvValue);
			revSumRespDto.setAspTaxbValue(aspTaxbValue);
			revSumRespDto.setAspTotTax(aspTotTax);

			revSumRespDto.setGstnCount(gstnCount);
			revSumRespDto.setGstnInvValue(gstnInvValue);
			revSumRespDto.setGstnTaxbValue(gstnTaxbValue);
			revSumRespDto.setGstnTotTax(gstnTotTax);
			revSumRespDto.setGstnIgst(gstnIgst);
			revSumRespDto.setGstnSgst(gstnSgst);
			revSumRespDto.setGstnCgst(gstnCgst);
			revSumRespDto.setGstnCess(gstnCess);

			revSumRespDto.setDiffCount(diffCount);
			revSumRespDto.setDiffInvValue(diffInvValue);
			revSumRespDto.setDiffTaxbValue(diffTaxbValue);
			revSumRespDto.setDiffTotTax(diffTotTax);
			revSumRespDto.setDiffIgst(diffIgst);
			revSumRespDto.setDiffSgst(diffSgst);
			revSumRespDto.setDiffCgst(diffCgst);
			revSumRespDto.setDiffCess(diffCess);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		} else {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			revSumRespDto.setDocType(CR_DISTRIBUTION);
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemEliDto.setDocType(ELIGIBLE);
			revSumRespItemDtos.add(respItemEliDto);

			Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemInEliDto.setDocType(IN_ELIGIBLE);
			revSumRespItemDtos.add(respItemInEliDto);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		}
	}

	private void getGstr6InwardRDINCSection(
			final Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos) {
		List<Gstr6ReviewSummaryResponseItemDto> respB2BADtos = mapGstr6InwardRespItems
				.get("RD_INC");
		if (respB2BADtos != null) {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			// Eligible for Review Summary screen
			BigInteger aspCount = BigInteger.ZERO;
			BigDecimal aspInvValue = BigDecimal.ZERO;
			BigDecimal aspTaxbValue = BigDecimal.ZERO;
			BigDecimal aspTotTax = BigDecimal.ZERO;
			BigDecimal aspIgst = BigDecimal.ZERO;
			BigDecimal aspCgst = BigDecimal.ZERO;
			BigDecimal aspSgst = BigDecimal.ZERO;
			BigDecimal aspCess = BigDecimal.ZERO;

			BigInteger gstnCount = BigInteger.ZERO;
			BigDecimal gstnInvValue = BigDecimal.ZERO;
			BigDecimal gstnTaxbValue = BigDecimal.ZERO;
			BigDecimal gstnTotTax = BigDecimal.ZERO;
			BigDecimal gstnIgst = BigDecimal.ZERO;
			BigDecimal gstnCgst = BigDecimal.ZERO;
			BigDecimal gstnSgst = BigDecimal.ZERO;
			BigDecimal gstnCess = BigDecimal.ZERO;

			BigInteger diffCount = BigInteger.ZERO;
			BigDecimal diffInvValue = BigDecimal.ZERO;
			BigDecimal diffTaxbValue = BigDecimal.ZERO;
			BigDecimal diffTotTax = BigDecimal.ZERO;
			BigDecimal diffIgst = BigDecimal.ZERO;
			BigDecimal diffCgst = BigDecimal.ZERO;
			BigDecimal diffSgst = BigDecimal.ZERO;
			BigDecimal diffCess = BigDecimal.ZERO;
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> eligebleItem = getGstr6InwardRespEligibleItem(
					respB2BADtos);
			List<Gstr6ReviewSummaryResponseItemDto> listEligibleRevSum = eligebleItem
					.get(IS_ELIGI);
			// Eligible
			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {
				for (Gstr6ReviewSummaryResponseItemDto respDto : listEligibleRevSum) {

					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(ELIGIBLE);
					revSumRespItemDtos.add(respDto);
				}
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}

			List<Gstr6ReviewSummaryResponseItemDto> listInEligibleRevSum = eligebleItem
					.get(NOT_ELG);
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {
				for (Gstr6ReviewSummaryResponseItemDto respDto : listInEligibleRevSum) {

					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(IN_ELIGIBLE);
					revSumRespItemDtos.add(respDto);
				}
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(IN_ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}
			revSumRespDto.setDocType(INV_REDISTRIBUTION);
			revSumRespDto.setAspCount(aspCount);
			revSumRespDto.setAspCess(aspCess);
			revSumRespDto.setAspCgst(aspCgst);
			revSumRespDto.setAspIgst(aspIgst);
			revSumRespDto.setAspSgst(aspSgst);
			revSumRespDto.setAspInvValue(aspInvValue);
			revSumRespDto.setAspTaxbValue(aspTaxbValue);
			revSumRespDto.setAspTotTax(aspTotTax);

			revSumRespDto.setGstnCount(gstnCount);
			revSumRespDto.setGstnInvValue(gstnInvValue);
			revSumRespDto.setGstnTaxbValue(gstnTaxbValue);
			revSumRespDto.setGstnTotTax(gstnTotTax);
			revSumRespDto.setGstnIgst(gstnIgst);
			revSumRespDto.setGstnSgst(gstnSgst);
			revSumRespDto.setGstnCgst(gstnCgst);
			revSumRespDto.setGstnCess(gstnCess);

			revSumRespDto.setDiffCount(diffCount);
			revSumRespDto.setDiffInvValue(diffInvValue);
			revSumRespDto.setDiffTaxbValue(diffTaxbValue);
			revSumRespDto.setDiffTotTax(diffTotTax);
			revSumRespDto.setDiffIgst(diffIgst);
			revSumRespDto.setDiffSgst(diffSgst);
			revSumRespDto.setDiffCgst(diffCgst);
			revSumRespDto.setDiffCess(diffCess);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		} else {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			revSumRespDto.setDocType(INV_REDISTRIBUTION);
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemEliDto.setDocType(ELIGIBLE);
			revSumRespItemDtos.add(respItemEliDto);

			Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemInEliDto.setDocType(IN_ELIGIBLE);
			revSumRespItemDtos.add(respItemInEliDto);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		}
	}

	private void getGstr6InwardRDCRSection(
			final Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseDto> gstr6RevSumRespDtos) {
		List<Gstr6ReviewSummaryResponseItemDto> respCDNADtos = mapGstr6InwardRespItems
				.get("RD_CR");
		if (respCDNADtos != null) {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			// Eligible for Review Summary screen
			BigInteger aspCount = BigInteger.ZERO;
			BigDecimal aspInvValue = BigDecimal.ZERO;
			BigDecimal aspTaxbValue = BigDecimal.ZERO;
			BigDecimal aspTotTax = BigDecimal.ZERO;
			BigDecimal aspIgst = BigDecimal.ZERO;
			BigDecimal aspCgst = BigDecimal.ZERO;
			BigDecimal aspSgst = BigDecimal.ZERO;
			BigDecimal aspCess = BigDecimal.ZERO;

			BigInteger gstnCount = BigInteger.ZERO;
			BigDecimal gstnInvValue = BigDecimal.ZERO;
			BigDecimal gstnTaxbValue = BigDecimal.ZERO;
			BigDecimal gstnTotTax = BigDecimal.ZERO;
			BigDecimal gstnIgst = BigDecimal.ZERO;
			BigDecimal gstnCgst = BigDecimal.ZERO;
			BigDecimal gstnSgst = BigDecimal.ZERO;
			BigDecimal gstnCess = BigDecimal.ZERO;

			BigInteger diffCount = BigInteger.ZERO;
			BigDecimal diffInvValue = BigDecimal.ZERO;
			BigDecimal diffTaxbValue = BigDecimal.ZERO;
			BigDecimal diffTotTax = BigDecimal.ZERO;
			BigDecimal diffIgst = BigDecimal.ZERO;
			BigDecimal diffCgst = BigDecimal.ZERO;
			BigDecimal diffSgst = BigDecimal.ZERO;
			BigDecimal diffCess = BigDecimal.ZERO;
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Map<String, List<Gstr6ReviewSummaryResponseItemDto>> eligebleItem = getGstr6InwardRespEligibleItem(
					respCDNADtos);

			List<Gstr6ReviewSummaryResponseItemDto> listEligibleRevSum = eligebleItem
					.get(IS_ELIGI);
			// Eligible
			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {
				for (Gstr6ReviewSummaryResponseItemDto respDto : listEligibleRevSum) {

					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(ELIGIBLE);

					revSumRespItemDtos.add(respDto);
				}
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}

			List<Gstr6ReviewSummaryResponseItemDto> listInEligibleRevSum = eligebleItem
					.get(NOT_ELG);
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {
				for (Gstr6ReviewSummaryResponseItemDto respDto : listInEligibleRevSum) {
					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(IN_ELIGIBLE);
					revSumRespItemDtos.add(respDto);
				}
			} else {
				Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemEliDto.setDocType(IN_ELIGIBLE);
				revSumRespItemDtos.add(respItemEliDto);
			}
			revSumRespDto.setDocType(CR_REDISTRIBUTION);
			revSumRespDto.setAspCount(aspCount);
			revSumRespDto.setAspCess(aspCess);
			revSumRespDto.setAspCgst(aspCgst);
			revSumRespDto.setAspIgst(aspIgst);
			revSumRespDto.setAspSgst(aspSgst);
			revSumRespDto.setAspInvValue(aspInvValue);
			revSumRespDto.setAspTaxbValue(aspTaxbValue);
			revSumRespDto.setAspTotTax(aspTotTax);

			revSumRespDto.setGstnCount(gstnCount);
			revSumRespDto.setGstnInvValue(gstnInvValue);
			revSumRespDto.setGstnTaxbValue(gstnTaxbValue);
			revSumRespDto.setGstnTotTax(gstnTotTax);
			revSumRespDto.setGstnIgst(gstnIgst);
			revSumRespDto.setGstnSgst(gstnSgst);
			revSumRespDto.setGstnCgst(gstnCgst);
			revSumRespDto.setGstnCess(gstnCess);

			revSumRespDto.setDiffCount(diffCount);
			revSumRespDto.setDiffInvValue(diffInvValue);
			revSumRespDto.setDiffTaxbValue(diffTaxbValue);
			revSumRespDto.setDiffTotTax(diffTotTax);
			revSumRespDto.setDiffIgst(diffIgst);
			revSumRespDto.setDiffSgst(diffSgst);
			revSumRespDto.setDiffCgst(diffCgst);
			revSumRespDto.setDiffCess(diffCess);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		} else {
			Gstr6ReviewSummaryResponseDto revSumRespDto = new Gstr6ReviewSummaryResponseDto();
			revSumRespDto.setDocType(CR_REDISTRIBUTION);
			List<Gstr6ReviewSummaryResponseItemDto> revSumRespItemDtos = new ArrayList<>();
			Gstr6ReviewSummaryResponseItemDto respItemEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemEliDto.setDocType(ELIGIBLE);
			revSumRespItemDtos.add(respItemEliDto);

			Gstr6ReviewSummaryResponseItemDto respItemInEliDto = new Gstr6ReviewSummaryResponseItemDto();
			respItemInEliDto.setDocType(IN_ELIGIBLE);
			revSumRespItemDtos.add(respItemInEliDto);
			revSumRespDto.setItems(revSumRespItemDtos);
			gstr6RevSumRespDtos.add(revSumRespDto);
		}
	}

	private Map<String, List<Gstr6ReviewSummaryResponseItemDto>> getGstr6InwardRespEligibleItem(
			List<Gstr6ReviewSummaryResponseItemDto> respItemDtos) {
		Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapRevSumResItemDtos = new HashMap<>();
		respItemDtos.forEach(respItemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(respItemDto.getEligInd());
			String docKey = key.toString();
			if (mapRevSumResItemDtos.containsKey(docKey)) {
				List<Gstr6ReviewSummaryResponseItemDto> revSumItemDtos = mapRevSumResItemDtos
						.get(docKey);
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			} else {
				List<Gstr6ReviewSummaryResponseItemDto> revSumItemDtos = new ArrayList<>();
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			}
		});
		return mapRevSumResItemDtos;
	}

	public List<Gstr6ReviewSummaryResponseItemDto> getGstr6InwardRespItem(
			final Annexure1SummaryReqDto reqDto) {
		List<Object[]> inwardSumDataObjs = gstr6RevSumDaoImpl
				.getSummaryDetails(reqDto);
		List<Gstr6ReviewSummaryResponseItemDto> respItemDtos = new ArrayList<>();
		if (inwardSumDataObjs != null && !inwardSumDataObjs.isEmpty()) {
			inwardSumDataObjs.forEach(inwardSumDataObj -> {
				Gstr6ReviewSummaryResponseItemDto respItemDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemDto.setAspCount(inwardSumDataObj[0] != null
						? new BigInteger(String.valueOf(inwardSumDataObj[0]))
						: BigInteger.ZERO);
				respItemDto.setAspInvValue(inwardSumDataObj[1] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[1]))
						: BigDecimal.ZERO);
				respItemDto.setAspTaxbValue(inwardSumDataObj[2] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[2]))
						: BigDecimal.ZERO);
				respItemDto.setAspTotTax(inwardSumDataObj[3] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[3]))
						: BigDecimal.ZERO);
				respItemDto.setAspIgst(inwardSumDataObj[4] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[4]))
						: BigDecimal.ZERO);
				respItemDto.setAspCgst(inwardSumDataObj[5] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[5]))
						: BigDecimal.ZERO);
				respItemDto.setAspSgst(inwardSumDataObj[6] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[6]))
						: BigDecimal.ZERO);
				respItemDto.setAspCess(inwardSumDataObj[7] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[7]))
						: BigDecimal.ZERO);
				respItemDto.setEligInd(inwardSumDataObj[8] != null
						? String.valueOf(inwardSumDataObj[8]) : null);
				respItemDto.setDocType(inwardSumDataObj[9] != null
						? String.valueOf(inwardSumDataObj[9]) : null);
				respItemDto.setGstnCount(inwardSumDataObj[12] != null
						? new BigInteger(String.valueOf(inwardSumDataObj[12]))
						: BigInteger.ZERO);
				respItemDto.setGstnInvValue(inwardSumDataObj[13] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[13]))
						: BigDecimal.ZERO);
				respItemDto.setGstnTaxbValue(inwardSumDataObj[14] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[14]))
						: BigDecimal.ZERO);
				respItemDto.setGstnTotTax(inwardSumDataObj[15] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[15]))
						: BigDecimal.ZERO);
				respItemDto.setGstnIgst(inwardSumDataObj[16] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[16]))
						: BigDecimal.ZERO);
				respItemDto.setGstnCgst(inwardSumDataObj[17] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[17]))
						: BigDecimal.ZERO);
				respItemDto.setGstnSgst(inwardSumDataObj[18] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[18]))
						: BigDecimal.ZERO);
				respItemDto.setGstnCess(inwardSumDataObj[19] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[19]))
						: BigDecimal.ZERO);
				respItemDto.setDiffCount(respItemDto.getAspCount()
						.subtract(respItemDto.getGstnCount()));
				respItemDto.setDiffInvValue(respItemDto.getAspInvValue()
						.subtract(respItemDto.getGstnInvValue()));
				respItemDto.setDiffTaxbValue(respItemDto.getAspTaxbValue()
						.subtract(respItemDto.getGstnTaxbValue()));
				respItemDto.setDiffTotTax(respItemDto.getAspTotTax()
						.subtract(respItemDto.getGstnTotTax()));
				respItemDto.setDiffIgst(respItemDto.getAspIgst()
						.subtract(respItemDto.getGstnIgst()));
				respItemDto.setDiffCgst(respItemDto.getAspCgst()
						.subtract(respItemDto.getGstnCgst()));
				respItemDto.setDiffSgst(respItemDto.getAspSgst()
						.subtract(respItemDto.getGstnSgst()));
				respItemDto.setDiffCess(respItemDto.getAspCess()
						.subtract(respItemDto.getGstnCess()));
				respItemDtos.add(respItemDto);
			});
		}
		return respItemDtos;
	}

	public List<Gstr6ReviewSummaryResponseItemDto> getGstr6InwardResponse(
			final Annexure1SummaryReqDto reqDto) {
		List<Gstr6DigiComputeEntity> inwardSumDataObjs = gstr6RevSumDaoImpl
				.getGstinSummaryDetails(reqDto);
		List<Gstr6ReviewSummaryResponseItemDto> respItemDtos = new ArrayList<>();
		if (inwardSumDataObjs != null && !inwardSumDataObjs.isEmpty()) {
			inwardSumDataObjs.forEach(inwardSumDataObj -> {
				Gstr6ReviewSummaryResponseItemDto respItemDto = new Gstr6ReviewSummaryResponseItemDto();
				respItemDto.setAspCount(BigInteger.valueOf(inwardSumDataObj.getAspId()));
				respItemDto.setAspInvValue(inwardSumDataObj.getInvoiceValueAsp());
				respItemDto.setAspTaxbValue(inwardSumDataObj.getTaxableValueAsp());
				respItemDto.setAspTotTax(inwardSumDataObj.getTotalTaxAsp());
				respItemDto.setAspIgst(inwardSumDataObj.getIgstAmountAsp());
				respItemDto.setAspCgst(inwardSumDataObj.getCgstAmountAsp());
				respItemDto.setAspSgst(inwardSumDataObj.getSgstAmountAsp());
				respItemDto.setAspCess(inwardSumDataObj.getCessAmountAsp());
				respItemDto.setEligInd(inwardSumDataObj.getEligibiltyIndicatorAsp());
				respItemDto.setDocType(inwardSumDataObj.getTaxDocumentType());
				respItemDto.setGstnCount(BigInteger.valueOf(inwardSumDataObj.getRecordCountGst()));
				respItemDto.setGstnInvValue(inwardSumDataObj.getInvoiceValueGst());
				respItemDto.setGstnTaxbValue(inwardSumDataObj.getTaxableValueGst());
				respItemDto.setGstnTotTax(inwardSumDataObj.getTotalTaxGst());
				respItemDto.setGstnIgst(inwardSumDataObj.getIgstAmountGst());
				respItemDto.setGstnCgst(inwardSumDataObj.getCgstAmountGst());
				respItemDto.setGstnSgst(inwardSumDataObj.getSgstAmountGst());
				respItemDto.setGstnCess(inwardSumDataObj.getCessAmountGst());
				respItemDto.setDiffCount(respItemDto.getAspCount()
						.subtract(respItemDto.getGstnCount()));
				respItemDto.setDiffInvValue(respItemDto.getAspInvValue()
						.subtract(respItemDto.getGstnInvValue()));
				respItemDto.setDiffTaxbValue(respItemDto.getAspTaxbValue()
						.subtract(respItemDto.getGstnTaxbValue()));
				respItemDto.setDiffTotTax(respItemDto.getAspTotTax()
						.subtract(respItemDto.getGstnTotTax()));
				respItemDto.setDiffIgst(respItemDto.getAspIgst()
						.subtract(respItemDto.getGstnIgst()));
				respItemDto.setDiffCgst(respItemDto.getAspCgst()
						.subtract(respItemDto.getGstnCgst()));
				respItemDto.setDiffSgst(respItemDto.getAspSgst()
						.subtract(respItemDto.getGstnSgst()));
				respItemDto.setDiffCess(respItemDto.getAspCess()
						.subtract(respItemDto.getGstnCess()));
				respItemDtos.add(respItemDto);
			});
		}
		return respItemDtos;
	}
	
	private Map<String, List<Gstr6ReviewSummaryResponseItemDto>> getGstr6InwardRespItem(
			List<Gstr6ReviewSummaryResponseItemDto> respItemDtos) {
		Map<String, List<Gstr6ReviewSummaryResponseItemDto>> mapRevSumResItemDtos = new HashMap<>();
		respItemDtos.forEach(respItemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(respItemDto.getDocType());
			String docKey = key.toString();
			if (mapRevSumResItemDtos.containsKey(docKey)) {
				List<Gstr6ReviewSummaryResponseItemDto> revSumItemDtos = mapRevSumResItemDtos
						.get(docKey);
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			} else {
				List<Gstr6ReviewSummaryResponseItemDto> revSumItemDtos = new ArrayList<>();
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			}
		});
		return mapRevSumResItemDtos;
	}

	@Override
	public List<Gstr6DistChannelRevSumRespDto> getGstr6DistChannelRevSum(
			Annexure1SummaryReqDto reqDto) {
		List<Gstr6DistChannelRevSumRespDto> distChanRevSumRespDtos = new ArrayList<>();

		List<Gstr6DistChannelRevSumRespItemDto> revSumRespItemDtos = convertObjToResponseDtos(
				reqDto);

		if (revSumRespItemDtos != null && !revSumRespItemDtos.isEmpty()) {
			Map<String, List<Gstr6DistChannelRevSumRespItemDto>> mapRevSumRespItems = mapRevSumRespItem(
					revSumRespItemDtos);
			getGstr6DistChanRev(mapRevSumRespItems, distChanRevSumRespDtos);
		} else {
			Gstr6DistChannelRevSumRespDto distInvRespDto = new Gstr6DistChannelRevSumRespDto();
			distInvRespDto.setDistribution(INV_DISTRIBUTION);
			List<Gstr6DistChannelRevSumRespItemDto> distInvItems = new ArrayList<>();
			Gstr6DistChannelRevSumRespItemDto distInvElItem = new Gstr6DistChannelRevSumRespItemDto();
			distInvElItem.setDistribution(ELIGIBLE);
			distInvItems.add(distInvElItem);
			Gstr6DistChannelRevSumRespItemDto distInElInvItem = new Gstr6DistChannelRevSumRespItemDto();
			distInElInvItem.setDistribution(IN_ELIGIBLE);
			distInvItems.add(distInElInvItem);
			distInvRespDto.setItems(distInvItems);
			distChanRevSumRespDtos.add(distInvRespDto);

			Gstr6DistChannelRevSumRespDto distCRRespDto = new Gstr6DistChannelRevSumRespDto();
			distCRRespDto.setDistribution(CR_DISTRIBUTION);
			List<Gstr6DistChannelRevSumRespItemDto> distCRItems = new ArrayList<>();
			Gstr6DistChannelRevSumRespItemDto distCRElItem = new Gstr6DistChannelRevSumRespItemDto();
			distCRElItem.setDistribution(ELIGIBLE);
			distCRItems.add(distCRElItem);
			Gstr6DistChannelRevSumRespItemDto distCRInElInvItem = new Gstr6DistChannelRevSumRespItemDto();
			distCRInElInvItem.setDistribution(IN_ELIGIBLE);
			distCRItems.add(distCRInElInvItem);
			distCRRespDto.setItems(distCRItems);
			distChanRevSumRespDtos.add(distCRRespDto);

			Gstr6DistChannelRevSumRespDto reDistInvRespDto = new Gstr6DistChannelRevSumRespDto();
			reDistInvRespDto.setDistribution(INV_REDISTRIBUTION);
			List<Gstr6DistChannelRevSumRespItemDto> reDistInvItems = new ArrayList<>();
			Gstr6DistChannelRevSumRespItemDto reDistInvElItem = new Gstr6DistChannelRevSumRespItemDto();
			reDistInvElItem.setDistribution(ELIGIBLE);
			reDistInvItems.add(reDistInvElItem);
			Gstr6DistChannelRevSumRespItemDto reDistInElInvItem = new Gstr6DistChannelRevSumRespItemDto();
			reDistInElInvItem.setDistribution(IN_ELIGIBLE);
			reDistInvItems.add(reDistInElInvItem);
			reDistInvRespDto.setItems(reDistInvItems);
			distChanRevSumRespDtos.add(reDistInvRespDto);

			Gstr6DistChannelRevSumRespDto reDistCRRespDto = new Gstr6DistChannelRevSumRespDto();
			reDistCRRespDto.setDistribution(CR_REDISTRIBUTION);
			List<Gstr6DistChannelRevSumRespItemDto> reDistCRItems = new ArrayList<>();
			Gstr6DistChannelRevSumRespItemDto reDistCRElItem = new Gstr6DistChannelRevSumRespItemDto();
			reDistCRElItem.setDistribution(ELIGIBLE);
			reDistCRItems.add(reDistCRElItem);
			Gstr6DistChannelRevSumRespItemDto reDistCRInElInvItem = new Gstr6DistChannelRevSumRespItemDto();
			reDistCRInElInvItem.setDistribution(IN_ELIGIBLE);
			reDistCRItems.add(reDistCRInElInvItem);
			reDistCRRespDto.setItems(reDistCRItems);
			distChanRevSumRespDtos.add(reDistCRRespDto);

		}
		return distChanRevSumRespDtos;

	}

	private void getGstr6DistChanRev(
			Map<String, List<Gstr6DistChannelRevSumRespItemDto>> mapRevSumRespItems,
			List<Gstr6DistChannelRevSumRespDto> distChanRevSumDtos) {

		List<Gstr6DistChannelRevSumRespItemDto> revInvSumRespDtos = mapRevSumRespItems
				.get("INV");
		if (revInvSumRespDtos != null && !revInvSumRespDtos.isEmpty()) {
			getGstr6DistInvChanReveiw(mapRevSumRespItems, revInvSumRespDtos,
					distChanRevSumDtos);
		} else {
			Gstr6DistChannelRevSumRespDto distInvRespDto = new Gstr6DistChannelRevSumRespDto();
			distInvRespDto.setDistribution(INV_DISTRIBUTION);
			List<Gstr6DistChannelRevSumRespItemDto> distInvItems = new ArrayList<>();
			Gstr6DistChannelRevSumRespItemDto distInvElItem = new Gstr6DistChannelRevSumRespItemDto();
			distInvElItem.setDistribution(ELIGIBLE);
			distInvItems.add(distInvElItem);
			Gstr6DistChannelRevSumRespItemDto distInElInvItem = new Gstr6DistChannelRevSumRespItemDto();
			distInElInvItem.setDistribution(IN_ELIGIBLE);
			distInvItems.add(distInElInvItem);
			distInvRespDto.setItems(distInvItems);
			distChanRevSumDtos.add(distInvRespDto);

		}
		List<Gstr6DistChannelRevSumRespItemDto> revCRSumRespDtos = mapRevSumRespItems
				.get("CR");
		List<Gstr6DistChannelRevSumRespItemDto> revDRSumRespDtos = mapRevSumRespItems
				.get("DR");
		if ((revCRSumRespDtos != null && !revCRSumRespDtos.isEmpty())
				|| (revDRSumRespDtos != null && !revDRSumRespDtos.isEmpty())) {
			getGstr6DistCRorDRandRCRorRDRChanReveiw(mapRevSumRespItems,
					revCRSumRespDtos, revDRSumRespDtos, distChanRevSumDtos);
		} else {
			Gstr6DistChannelRevSumRespDto distCRRespDto = new Gstr6DistChannelRevSumRespDto();
			distCRRespDto.setDistribution(CR_DISTRIBUTION);
			List<Gstr6DistChannelRevSumRespItemDto> distCRItems = new ArrayList<>();
			Gstr6DistChannelRevSumRespItemDto distCRElItem = new Gstr6DistChannelRevSumRespItemDto();
			distCRElItem.setDistribution(ELIGIBLE);
			distCRItems.add(distCRElItem);
			Gstr6DistChannelRevSumRespItemDto distCRInElInvItem = new Gstr6DistChannelRevSumRespItemDto();
			distCRInElInvItem.setDistribution(IN_ELIGIBLE);
			distCRItems.add(distCRInElInvItem);
			distCRRespDto.setItems(distCRItems);
			distChanRevSumDtos.add(distCRRespDto);
		}

		List<Gstr6DistChannelRevSumRespItemDto> revRnvSumRespDtos = mapRevSumRespItems
				.get("RNV");
		if (revRnvSumRespDtos != null && !revRnvSumRespDtos.isEmpty()) {
			getGstr6DistInvChanReveiw(mapRevSumRespItems, revRnvSumRespDtos,
					distChanRevSumDtos);
		} else {
			Gstr6DistChannelRevSumRespDto reDistInvRespDto = new Gstr6DistChannelRevSumRespDto();
			reDistInvRespDto.setDistribution(INV_REDISTRIBUTION);
			List<Gstr6DistChannelRevSumRespItemDto> reDistInvItems = new ArrayList<>();
			Gstr6DistChannelRevSumRespItemDto reDistInvElItem = new Gstr6DistChannelRevSumRespItemDto();
			reDistInvElItem.setDistribution(ELIGIBLE);
			reDistInvItems.add(reDistInvElItem);
			Gstr6DistChannelRevSumRespItemDto reDistInElInvItem = new Gstr6DistChannelRevSumRespItemDto();
			reDistInElInvItem.setDistribution(IN_ELIGIBLE);
			reDistInvItems.add(reDistInElInvItem);
			reDistInvRespDto.setItems(reDistInvItems);
			distChanRevSumDtos.add(reDistInvRespDto);

		}

		List<Gstr6DistChannelRevSumRespItemDto> revRCRSumRespDtos = mapRevSumRespItems
				.get("RCR");
		List<Gstr6DistChannelRevSumRespItemDto> revRDRSumRespDtos = mapRevSumRespItems
				.get("RDR");
		if ((revRCRSumRespDtos != null && !revRCRSumRespDtos.isEmpty())
				|| (revRDRSumRespDtos != null
						&& !revRDRSumRespDtos.isEmpty())) {
			getGstr6DistCRorDRandRCRorRDRChanReveiw(mapRevSumRespItems,
					revRCRSumRespDtos, revRDRSumRespDtos, distChanRevSumDtos);
		} else {
			Gstr6DistChannelRevSumRespDto reDistCRRespDto = new Gstr6DistChannelRevSumRespDto();
			reDistCRRespDto.setDistribution(CR_REDISTRIBUTION);
			List<Gstr6DistChannelRevSumRespItemDto> reDistCRItems = new ArrayList<>();
			Gstr6DistChannelRevSumRespItemDto reDistCRElItem = new Gstr6DistChannelRevSumRespItemDto();
			reDistCRElItem.setDistribution(ELIGIBLE);
			reDistCRItems.add(reDistCRElItem);
			Gstr6DistChannelRevSumRespItemDto reDistCRInElInvItem = new Gstr6DistChannelRevSumRespItemDto();
			reDistCRInElInvItem.setDistribution(IN_ELIGIBLE);
			reDistCRItems.add(reDistCRInElInvItem);
			reDistCRRespDto.setItems(reDistCRItems);
			distChanRevSumDtos.add(reDistCRRespDto);
		}
	}

	private void getGstr6DistCRorDRandRCRorRDRChanReveiw(
			Map<String, List<Gstr6DistChannelRevSumRespItemDto>> mapRevSumRespItems,
			List<Gstr6DistChannelRevSumRespItemDto> revCRSumRespDtos,
			List<Gstr6DistChannelRevSumRespItemDto> revDRSumRespDtos,
			List<Gstr6DistChannelRevSumRespDto> distChanRevSumDtos) {

		if ((revCRSumRespDtos != null && !revCRSumRespDtos.isEmpty())
				|| (revDRSumRespDtos != null && !revDRSumRespDtos.isEmpty())) {
			Map<String, List<Gstr6DistChannelRevSumRespItemDto>> mapDistResps = null;
			String docType = null;
			if (revCRSumRespDtos != null && !revCRSumRespDtos.isEmpty()) {
				mapDistResps = mapDistributionRevSumRespItem(revCRSumRespDtos);
			}
			if (revDRSumRespDtos != null && !revDRSumRespDtos.isEmpty()) {
				mapDistResps = mapDistributionRevSumRespItem(revDRSumRespDtos);
			}
			List<Gstr6DistChannelRevSumRespItemDto> itemIsEligiDtos = mapDistResps
					.get("ELIGIBLE");

			List<Gstr6DistChannelRevSumRespItemDto> itemInEligiDtos = mapDistResps
					.get("INELIGIBLE");

			Gstr6DistChannelRevSumRespDto revSumRespDto = new Gstr6DistChannelRevSumRespDto();
			BigInteger aspCount = BigInteger.ZERO;
			BigDecimal aspIgstasIgst = BigDecimal.ZERO;
			BigDecimal aspIgstasSgst = BigDecimal.ZERO;
			BigDecimal aspIgstasCgst = BigDecimal.ZERO;
			BigDecimal aspSgstasSgst = BigDecimal.ZERO;
			BigDecimal aspSgstasIgst = BigDecimal.ZERO;
			BigDecimal aspCgstasCgst = BigDecimal.ZERO;
			BigDecimal aspCgstasIgst = BigDecimal.ZERO;
			BigDecimal aspCessAmount = BigDecimal.ZERO;

			BigInteger gstnCount = BigInteger.ZERO;
			BigDecimal gstnIgstasIgst = BigDecimal.ZERO;
			BigDecimal gstnIgstasSgst = BigDecimal.ZERO;
			BigDecimal gstnIgstasCgst = BigDecimal.ZERO;
			BigDecimal gstnSgstasSgst = BigDecimal.ZERO;
			BigDecimal gstnSgstasIgst = BigDecimal.ZERO;
			BigDecimal gstnCgstasCgst = BigDecimal.ZERO;
			BigDecimal gstnCgstasIgst = BigDecimal.ZERO;
			BigDecimal gstnCessAmount = BigDecimal.ZERO;

			BigInteger diffCount = BigInteger.ZERO;
			BigDecimal diffIgstasIgst = BigDecimal.ZERO;
			BigDecimal diffIgstasSgst = BigDecimal.ZERO;
			BigDecimal diffIgstasCgst = BigDecimal.ZERO;
			BigDecimal diffSgstasSgst = BigDecimal.ZERO;
			BigDecimal diffSgstasIgst = BigDecimal.ZERO;
			BigDecimal diffCgstasCgst = BigDecimal.ZERO;
			BigDecimal diffCgstasIgst = BigDecimal.ZERO;
			BigDecimal diffCessAmount = BigDecimal.ZERO;

			List<Gstr6DistChannelRevSumRespItemDto> items = new ArrayList<>();
			if (itemIsEligiDtos != null && !itemIsEligiDtos.isEmpty()) {
				for (Gstr6DistChannelRevSumRespItemDto mapRevSumRespItem : itemIsEligiDtos) {
					docType = mapRevSumRespItem.getDocType();
					aspCount = aspCount.add(mapRevSumRespItem.getAspCount());
					aspIgstasIgst = aspIgstasIgst
							.add(mapRevSumRespItem.getAspIgstasIgst());
					aspIgstasSgst = aspIgstasSgst
							.add(mapRevSumRespItem.getAspIgstasSgst());
					aspIgstasCgst = aspIgstasCgst
							.add(mapRevSumRespItem.getAspIgstasCgst());
					aspSgstasSgst = aspSgstasSgst
							.add(mapRevSumRespItem.getAspSgstasSgst());
					aspSgstasIgst = aspSgstasIgst
							.add(mapRevSumRespItem.getAspSgstasIgst());
					aspCgstasCgst = aspCgstasCgst
							.add(mapRevSumRespItem.getAspCgstasCgst());
					aspCgstasIgst = aspCgstasIgst
							.add(mapRevSumRespItem.getAspCgstasIgst());
					aspCessAmount = aspCessAmount
							.add(mapRevSumRespItem.getAspCessAmount());

					gstnCount = gstnCount.add(mapRevSumRespItem.getGstnCount());
					gstnIgstasIgst = gstnIgstasIgst
							.add(mapRevSumRespItem.getGstnIgstasIgst());
					gstnIgstasSgst = gstnIgstasSgst
							.add(mapRevSumRespItem.getGstnIgstasSgst());
					gstnIgstasCgst = gstnIgstasCgst
							.add(mapRevSumRespItem.getGstnIgstasCgst());
					gstnSgstasSgst = gstnSgstasSgst
							.add(mapRevSumRespItem.getGstnSgstasSgst());
					gstnSgstasIgst = gstnSgstasIgst
							.add(mapRevSumRespItem.getGstnSgstasIgst());
					gstnCgstasCgst = gstnCgstasCgst
							.add(mapRevSumRespItem.getGstnCgstasCgst());
					gstnCgstasIgst = gstnCgstasIgst
							.add(mapRevSumRespItem.getGstnCgstasIgst());
					gstnCessAmount = gstnCessAmount
							.add(mapRevSumRespItem.getGstnCessAmount());

					diffCount = diffCount.add(mapRevSumRespItem.getDiffCount());
					diffIgstasIgst = diffIgstasIgst
							.add(mapRevSumRespItem.getDiffIgstasIgst());
					diffIgstasSgst = diffIgstasSgst
							.add(mapRevSumRespItem.getDiffIgstasSgst());
					diffIgstasCgst = diffIgstasCgst
							.add(mapRevSumRespItem.getDiffIgstasCgst());
					diffSgstasSgst = diffSgstasSgst
							.add(mapRevSumRespItem.getDiffSgstasSgst());
					diffSgstasIgst = diffSgstasIgst
							.add(mapRevSumRespItem.getDiffSgstasIgst());
					diffCgstasCgst = diffCgstasCgst
							.add(mapRevSumRespItem.getDiffCgstasCgst());
					diffCgstasIgst = diffCgstasIgst
							.add(mapRevSumRespItem.getDiffCgstasIgst());
					diffCessAmount = diffCessAmount
							.add(mapRevSumRespItem.getDiffCessAmount());
					mapRevSumRespItem.setDistribution(ELIGIBLE);
					items.add(mapRevSumRespItem);
				}
			} else {
				Gstr6DistChannelRevSumRespItemDto mapRevSumRespItem = new Gstr6DistChannelRevSumRespItemDto();
				mapRevSumRespItem.setDistribution(ELIGIBLE);
				items.add(mapRevSumRespItem);
			}

			if (itemInEligiDtos != null && !itemInEligiDtos.isEmpty()) {
				for (Gstr6DistChannelRevSumRespItemDto mapRevSumRespItem : itemInEligiDtos) {
					docType = mapRevSumRespItem.getDocType();
					aspCount = aspCount.add(mapRevSumRespItem.getAspCount());
					aspIgstasIgst = aspIgstasIgst
							.add(mapRevSumRespItem.getAspIgstasIgst());
					aspIgstasSgst = aspIgstasSgst
							.add(mapRevSumRespItem.getAspIgstasSgst());
					aspIgstasCgst = aspIgstasCgst
							.add(mapRevSumRespItem.getAspIgstasCgst());
					aspSgstasSgst = aspSgstasSgst
							.add(mapRevSumRespItem.getAspSgstasSgst());
					aspSgstasIgst = aspSgstasIgst
							.add(mapRevSumRespItem.getAspSgstasIgst());
					aspCgstasCgst = aspCgstasCgst
							.add(mapRevSumRespItem.getAspCgstasCgst());
					aspCgstasIgst = aspCgstasIgst
							.add(mapRevSumRespItem.getAspCgstasIgst());
					aspCessAmount = aspCessAmount
							.add(mapRevSumRespItem.getAspCessAmount());

					gstnCount = gstnCount.add(mapRevSumRespItem.getGstnCount());
					gstnIgstasIgst = gstnIgstasIgst
							.add(mapRevSumRespItem.getGstnIgstasIgst());
					gstnIgstasSgst = gstnIgstasSgst
							.add(mapRevSumRespItem.getGstnIgstasSgst());
					gstnIgstasCgst = gstnIgstasCgst
							.add(mapRevSumRespItem.getGstnIgstasCgst());
					gstnSgstasSgst = gstnSgstasSgst
							.add(mapRevSumRespItem.getGstnSgstasSgst());
					gstnSgstasIgst = gstnSgstasIgst
							.add(mapRevSumRespItem.getGstnSgstasIgst());
					gstnCgstasCgst = gstnCgstasCgst
							.add(mapRevSumRespItem.getGstnCgstasCgst());
					gstnCgstasIgst = gstnCgstasIgst
							.add(mapRevSumRespItem.getGstnCgstasIgst());
					gstnCessAmount = gstnCessAmount
							.add(mapRevSumRespItem.getGstnCessAmount());

					diffCount = diffCount.add(mapRevSumRespItem.getDiffCount());
					diffIgstasIgst = diffIgstasIgst
							.add(mapRevSumRespItem.getDiffIgstasIgst());
					diffIgstasSgst = diffIgstasSgst
							.add(mapRevSumRespItem.getDiffIgstasSgst());
					diffIgstasCgst = diffIgstasCgst
							.add(mapRevSumRespItem.getDiffIgstasCgst());
					diffSgstasSgst = diffSgstasSgst
							.add(mapRevSumRespItem.getDiffSgstasSgst());
					diffSgstasIgst = diffSgstasIgst
							.add(mapRevSumRespItem.getDiffSgstasIgst());
					diffCgstasCgst = diffCgstasCgst
							.add(mapRevSumRespItem.getDiffCgstasCgst());
					diffCgstasIgst = diffCgstasIgst
							.add(mapRevSumRespItem.getDiffCgstasIgst());
					diffCessAmount = diffCessAmount
							.add(mapRevSumRespItem.getDiffCessAmount());
					mapRevSumRespItem.setDistribution(IN_ELIGIBLE);
					items.add(mapRevSumRespItem);
				}
			} else {
				Gstr6DistChannelRevSumRespItemDto mapRevSumRespItem = new Gstr6DistChannelRevSumRespItemDto();
				mapRevSumRespItem.setDistribution(IN_ELIGIBLE);
				items.add(mapRevSumRespItem);
			}
			if ("CR".equalsIgnoreCase(docType)
					|| "DR".equalsIgnoreCase(docType)) {
				revSumRespDto.setDistribution(CR_DISTRIBUTION);
			}
			if ("RCR".equalsIgnoreCase(docType)
					|| "RDR".equalsIgnoreCase(docType)) {
				revSumRespDto.setDistribution(CR_REDISTRIBUTION);
			}
			revSumRespDto.setAspCount(aspCount);
			revSumRespDto.setAspIgstasIgst(aspIgstasIgst);
			revSumRespDto.setAspIgstasSgst(aspIgstasSgst);
			revSumRespDto.setAspIgstasCgst(aspIgstasCgst);
			revSumRespDto.setAspSgstasSgst(aspSgstasSgst);
			revSumRespDto.setAspSgstasIgst(aspSgstasIgst);
			revSumRespDto.setAspCgstasCgst(aspCgstasCgst);
			revSumRespDto.setAspCgstasIgst(aspCgstasIgst);
			revSumRespDto.setAspCessAmount(aspCessAmount);

			revSumRespDto.setGstnCount(gstnCount);
			revSumRespDto.setGstnIgstasIgst(gstnIgstasIgst);
			revSumRespDto.setGstnIgstasSgst(gstnIgstasSgst);
			revSumRespDto.setGstnIgstasCgst(gstnIgstasCgst);
			revSumRespDto.setGstnSgstasSgst(gstnSgstasSgst);
			revSumRespDto.setGstnSgstasIgst(gstnSgstasIgst);
			revSumRespDto.setGstnCgstasCgst(gstnCgstasCgst);
			revSumRespDto.setGstnCgstasIgst(gstnCgstasIgst);
			revSumRespDto.setGstnCessAmount(gstnCessAmount);

			revSumRespDto.setDiffCount(diffCount);
			revSumRespDto.setDiffIgstasIgst(diffIgstasIgst);
			revSumRespDto.setDiffIgstasSgst(diffIgstasSgst);
			revSumRespDto.setDiffIgstasCgst(diffIgstasCgst);
			revSumRespDto.setDiffSgstasSgst(diffSgstasSgst);
			revSumRespDto.setDiffSgstasIgst(diffSgstasIgst);
			revSumRespDto.setDiffCgstasCgst(diffCgstasCgst);
			revSumRespDto.setDiffCgstasIgst(diffCgstasIgst);
			revSumRespDto.setDiffCessAmount(diffCessAmount);

			revSumRespDto.setItems(items);
			distChanRevSumDtos.add(revSumRespDto);
		}

	}

	private Map<String, List<Gstr6DistChannelRevSumRespItemDto>> mapRevSumRespItem(
			List<Gstr6DistChannelRevSumRespItemDto> revSumRespItemDtos) {
		Map<String, List<Gstr6DistChannelRevSumRespItemDto>> revSumRespItemMap = new HashMap<>();
		revSumRespItemDtos.forEach(revSumRespItemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(revSumRespItemDto.getDocType());
			String docKey = key.toString();
			if (revSumRespItemMap.containsKey(docKey)) {
				List<Gstr6DistChannelRevSumRespItemDto> revSumItemDtos = revSumRespItemMap
						.get(docKey);
				revSumItemDtos.add(revSumRespItemDto);
				revSumRespItemMap.put(docKey, revSumItemDtos);
			} else {
				List<Gstr6DistChannelRevSumRespItemDto> revSumItemDtos = new ArrayList<>();
				revSumItemDtos.add(revSumRespItemDto);
				revSumRespItemMap.put(docKey, revSumItemDtos);
			}
		});
		return revSumRespItemMap;
	}

	private void getGstr6DistInvChanReveiw(
			Map<String, List<Gstr6DistChannelRevSumRespItemDto>> mapRevSumRespItems,
			List<Gstr6DistChannelRevSumRespItemDto> revInvSumRespDtos,
			List<Gstr6DistChannelRevSumRespDto> distChanRevSumDtos) {

		Map<String, List<Gstr6DistChannelRevSumRespItemDto>> mapDistResps = mapDistributionRevSumRespItem(
				revInvSumRespDtos);
		List<Gstr6DistChannelRevSumRespItemDto> itemIsEligiDtos = mapDistResps
				.get("ELIGIBLE");

		List<Gstr6DistChannelRevSumRespItemDto> itemInEligiDtos = mapDistResps
				.get("INELIGIBLE");

		Gstr6DistChannelRevSumRespDto revSumRespDto = new Gstr6DistChannelRevSumRespDto();
		BigInteger aspCount = BigInteger.ZERO;
		BigDecimal aspIgstasIgst = BigDecimal.ZERO;
		BigDecimal aspIgstasSgst = BigDecimal.ZERO;
		BigDecimal aspIgstasCgst = BigDecimal.ZERO;
		BigDecimal aspSgstasSgst = BigDecimal.ZERO;
		BigDecimal aspSgstasIgst = BigDecimal.ZERO;
		BigDecimal aspCgstasCgst = BigDecimal.ZERO;
		BigDecimal aspCgstasIgst = BigDecimal.ZERO;
		BigDecimal aspCessAmount = BigDecimal.ZERO;

		BigInteger gstnCount = BigInteger.ZERO;
		BigDecimal gstnIgstasIgst = BigDecimal.ZERO;
		BigDecimal gstnIgstasSgst = BigDecimal.ZERO;
		BigDecimal gstnIgstasCgst = BigDecimal.ZERO;
		BigDecimal gstnSgstasSgst = BigDecimal.ZERO;
		BigDecimal gstnSgstasIgst = BigDecimal.ZERO;
		BigDecimal gstnCgstasCgst = BigDecimal.ZERO;
		BigDecimal gstnCgstasIgst = BigDecimal.ZERO;
		BigDecimal gstnCessAmount = BigDecimal.ZERO;

		BigInteger diffCount = BigInteger.ZERO;
		BigDecimal diffIgstasIgst = BigDecimal.ZERO;
		BigDecimal diffIgstasSgst = BigDecimal.ZERO;
		BigDecimal diffIgstasCgst = BigDecimal.ZERO;
		BigDecimal diffSgstasSgst = BigDecimal.ZERO;
		BigDecimal diffSgstasIgst = BigDecimal.ZERO;
		BigDecimal diffCgstasCgst = BigDecimal.ZERO;
		BigDecimal diffCgstasIgst = BigDecimal.ZERO;
		BigDecimal diffCessAmount = BigDecimal.ZERO;
		String docType = null;
		List<Gstr6DistChannelRevSumRespItemDto> items = new ArrayList<>();
		if (itemIsEligiDtos != null && !itemIsEligiDtos.isEmpty()) {
			for (Gstr6DistChannelRevSumRespItemDto mapRevSumRespItem : itemIsEligiDtos) {
				docType = mapRevSumRespItem.getDocType();
				aspCount = aspCount.add(mapRevSumRespItem.getAspCount());
				aspIgstasIgst = aspIgstasIgst
						.add(mapRevSumRespItem.getAspIgstasIgst());
				aspIgstasSgst = aspIgstasSgst
						.add(mapRevSumRespItem.getAspIgstasSgst());
				aspIgstasCgst = aspIgstasCgst
						.add(mapRevSumRespItem.getAspIgstasCgst());
				aspSgstasSgst = aspSgstasSgst
						.add(mapRevSumRespItem.getAspSgstasSgst());
				aspSgstasIgst = aspSgstasIgst
						.add(mapRevSumRespItem.getAspSgstasIgst());
				aspCgstasCgst = aspCgstasCgst
						.add(mapRevSumRespItem.getAspCgstasCgst());
				aspCgstasIgst = aspCgstasIgst
						.add(mapRevSumRespItem.getAspCgstasIgst());
				aspCessAmount = aspCessAmount
						.add(mapRevSumRespItem.getAspCessAmount());

				gstnCount = gstnCount.add(mapRevSumRespItem.getGstnCount());
				gstnIgstasIgst = gstnIgstasIgst
						.add(mapRevSumRespItem.getGstnIgstasIgst());
				gstnIgstasSgst = gstnIgstasSgst
						.add(mapRevSumRespItem.getGstnIgstasSgst());
				gstnIgstasCgst = gstnIgstasCgst
						.add(mapRevSumRespItem.getGstnIgstasCgst());
				gstnSgstasSgst = gstnSgstasSgst
						.add(mapRevSumRespItem.getGstnSgstasSgst());
				gstnSgstasIgst = gstnSgstasIgst
						.add(mapRevSumRespItem.getGstnSgstasIgst());
				gstnCgstasCgst = gstnCgstasCgst
						.add(mapRevSumRespItem.getGstnCgstasCgst());
				gstnCgstasIgst = gstnCgstasIgst
						.add(mapRevSumRespItem.getGstnCgstasIgst());
				gstnCessAmount = gstnCessAmount
						.add(mapRevSumRespItem.getGstnCessAmount());

				diffCount = diffCount.add(mapRevSumRespItem.getDiffCount());
				diffIgstasIgst = diffIgstasIgst
						.add(mapRevSumRespItem.getDiffIgstasIgst());
				diffIgstasSgst = diffIgstasSgst
						.add(mapRevSumRespItem.getDiffIgstasSgst());
				diffIgstasCgst = diffIgstasCgst
						.add(mapRevSumRespItem.getDiffIgstasCgst());
				diffSgstasSgst = diffSgstasSgst
						.add(mapRevSumRespItem.getDiffSgstasSgst());
				diffSgstasIgst = diffSgstasIgst
						.add(mapRevSumRespItem.getDiffSgstasIgst());
				diffCgstasCgst = diffCgstasCgst
						.add(mapRevSumRespItem.getDiffCgstasCgst());
				diffCgstasIgst = diffCgstasIgst
						.add(mapRevSumRespItem.getDiffCgstasIgst());
				diffCessAmount = diffCessAmount
						.add(mapRevSumRespItem.getDiffCessAmount());
				mapRevSumRespItem.setDocType(ELIGIBLE);
				mapRevSumRespItem.setDistribution(ELIGIBLE);
				items.add(mapRevSumRespItem);
			}
		} else {
			Gstr6DistChannelRevSumRespItemDto mapRevSumRespItem = new Gstr6DistChannelRevSumRespItemDto();
			mapRevSumRespItem.setDistribution(ELIGIBLE);
			items.add(mapRevSumRespItem);
		}

		if (itemInEligiDtos != null && !itemInEligiDtos.isEmpty()) {
			for (Gstr6DistChannelRevSumRespItemDto mapRevSumRespItem : itemInEligiDtos) {
				docType = mapRevSumRespItem.getDocType();
				aspCount = aspCount.add(mapRevSumRespItem.getAspCount());
				aspIgstasIgst = aspIgstasIgst
						.add(mapRevSumRespItem.getAspIgstasIgst());
				aspIgstasSgst = aspIgstasSgst
						.add(mapRevSumRespItem.getAspIgstasSgst());
				aspIgstasCgst = aspIgstasCgst
						.add(mapRevSumRespItem.getAspIgstasCgst());
				aspSgstasSgst = aspSgstasSgst
						.add(mapRevSumRespItem.getAspSgstasSgst());
				aspSgstasIgst = aspSgstasIgst
						.add(mapRevSumRespItem.getAspSgstasIgst());
				aspCgstasCgst = aspCgstasCgst
						.add(mapRevSumRespItem.getAspCgstasCgst());
				aspCgstasIgst = aspCgstasIgst
						.add(mapRevSumRespItem.getAspCgstasIgst());
				aspCessAmount = aspCessAmount
						.add(mapRevSumRespItem.getAspCessAmount());

				gstnCount = gstnCount.add(mapRevSumRespItem.getGstnCount());
				gstnIgstasIgst = gstnIgstasIgst
						.add(mapRevSumRespItem.getGstnIgstasIgst());
				gstnIgstasSgst = gstnIgstasSgst
						.add(mapRevSumRespItem.getGstnIgstasSgst());
				gstnIgstasCgst = gstnIgstasCgst
						.add(mapRevSumRespItem.getGstnIgstasCgst());
				gstnSgstasSgst = gstnSgstasSgst
						.add(mapRevSumRespItem.getGstnSgstasSgst());
				gstnSgstasIgst = gstnSgstasIgst
						.add(mapRevSumRespItem.getGstnSgstasIgst());
				gstnCgstasCgst = gstnCgstasCgst
						.add(mapRevSumRespItem.getGstnCgstasCgst());
				gstnCgstasIgst = gstnCgstasIgst
						.add(mapRevSumRespItem.getGstnCgstasIgst());
				gstnCessAmount = gstnCessAmount
						.add(mapRevSumRespItem.getGstnCessAmount());

				diffCount = diffCount.add(mapRevSumRespItem.getDiffCount());
				diffIgstasIgst = diffIgstasIgst
						.add(mapRevSumRespItem.getDiffIgstasIgst());
				diffIgstasSgst = diffIgstasSgst
						.add(mapRevSumRespItem.getDiffIgstasSgst());
				diffIgstasCgst = diffIgstasCgst
						.add(mapRevSumRespItem.getDiffIgstasCgst());
				diffSgstasSgst = diffSgstasSgst
						.add(mapRevSumRespItem.getDiffSgstasSgst());
				diffSgstasIgst = diffSgstasIgst
						.add(mapRevSumRespItem.getDiffSgstasIgst());
				diffCgstasCgst = diffCgstasCgst
						.add(mapRevSumRespItem.getDiffCgstasCgst());
				diffCgstasIgst = diffCgstasIgst
						.add(mapRevSumRespItem.getDiffCgstasIgst());
				diffCessAmount = diffCessAmount
						.add(mapRevSumRespItem.getDiffCessAmount());
				mapRevSumRespItem.setDistribution(IN_ELIGIBLE);
				items.add(mapRevSumRespItem);
			}
		} else {
			Gstr6DistChannelRevSumRespItemDto mapRevSumRespItem = new Gstr6DistChannelRevSumRespItemDto();
			mapRevSumRespItem.setDistribution(IN_ELIGIBLE);
			items.add(mapRevSumRespItem);
		}
		if ("INV".equalsIgnoreCase(docType)) {
			revSumRespDto.setDistribution(INV_DISTRIBUTION);
		}
		if ("RNV".equalsIgnoreCase(docType)) {
			revSumRespDto.setDistribution(INV_REDISTRIBUTION);
		}
		revSumRespDto.setAspCount(aspCount);
		revSumRespDto.setAspIgstasIgst(aspIgstasIgst);
		revSumRespDto.setAspIgstasSgst(aspIgstasSgst);
		revSumRespDto.setAspIgstasCgst(aspIgstasCgst);
		revSumRespDto.setAspSgstasSgst(aspSgstasSgst);
		revSumRespDto.setAspSgstasIgst(aspSgstasIgst);
		revSumRespDto.setAspCgstasCgst(aspCgstasCgst);
		revSumRespDto.setAspCgstasIgst(aspCgstasIgst);
		revSumRespDto.setAspCessAmount(aspCessAmount);

		revSumRespDto.setGstnCount(gstnCount);
		revSumRespDto.setGstnIgstasIgst(gstnIgstasIgst);
		revSumRespDto.setGstnIgstasSgst(gstnIgstasSgst);
		revSumRespDto.setGstnIgstasCgst(gstnIgstasCgst);
		revSumRespDto.setGstnSgstasSgst(gstnSgstasSgst);
		revSumRespDto.setGstnSgstasIgst(gstnSgstasIgst);
		revSumRespDto.setGstnCgstasCgst(gstnCgstasCgst);
		revSumRespDto.setGstnCgstasIgst(gstnCgstasIgst);
		revSumRespDto.setGstnCessAmount(gstnCessAmount);

		revSumRespDto.setDiffCount(diffCount);
		revSumRespDto.setDiffIgstasIgst(diffIgstasIgst);
		revSumRespDto.setDiffIgstasSgst(diffIgstasSgst);
		revSumRespDto.setDiffIgstasCgst(diffIgstasCgst);
		revSumRespDto.setDiffSgstasSgst(diffSgstasSgst);
		revSumRespDto.setDiffSgstasIgst(diffSgstasIgst);
		revSumRespDto.setDiffCgstasCgst(diffCgstasCgst);
		revSumRespDto.setDiffCgstasIgst(diffCgstasIgst);
		revSumRespDto.setDiffCessAmount(diffCessAmount);
		revSumRespDto.setItems(items);
		distChanRevSumDtos.add(revSumRespDto);
	}

	private Map<String, List<Gstr6DistChannelRevSumRespItemDto>> mapDistributionRevSumRespItem(
			List<Gstr6DistChannelRevSumRespItemDto> revSumRespItemDtos) {
		Map<String, List<Gstr6DistChannelRevSumRespItemDto>> revSumRespItemMap = new HashMap<>();
		revSumRespItemDtos.forEach(revSumRespItemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(revSumRespItemDto.getDistribution());
			String docKey = key.toString();
			if (revSumRespItemMap.containsKey(docKey)) {
				List<Gstr6DistChannelRevSumRespItemDto> revSumItemDtos = revSumRespItemMap
						.get(docKey);
				revSumItemDtos.add(revSumRespItemDto);
				revSumRespItemMap.put(docKey, revSumItemDtos);
			} else {
				List<Gstr6DistChannelRevSumRespItemDto> revSumItemDtos = new ArrayList<>();
				revSumItemDtos.add(revSumRespItemDto);
				revSumRespItemMap.put(docKey, revSumItemDtos);
			}
		});
		return revSumRespItemMap;
	}

	public List<Gstr6DistChannelRevSumRespItemDto> convertObjToRespDtos(
			Annexure1SummaryReqDto reqDto) {
		List<Gstr6DistChannelRevSumRespItemDto> revSumItemDtos = new ArrayList<>();
		List<Object[]> objs = gstr6DistChannelRevSumDao
				.getSummaryDetails(reqDto);
		if (objs != null) {
			for (Object[] obj : objs) {
				Gstr6DistChannelRevSumRespItemDto revSumItemDto = new Gstr6DistChannelRevSumRespItemDto();
				revSumItemDto.setAspCount(
						obj[0] != null ? new BigInteger(String.valueOf(obj[0]))
								: BigInteger.ZERO);
				revSumItemDto.setAspIgstasIgst(
						obj[1] != null ? new BigDecimal(String.valueOf(obj[1]))
								: BigDecimal.ZERO);
				revSumItemDto.setAspIgstasSgst(
						obj[2] != null ? new BigDecimal(String.valueOf(obj[2]))
								: BigDecimal.ZERO);
				revSumItemDto.setAspIgstasCgst(
						obj[3] != null ? new BigDecimal(String.valueOf(obj[3]))
								: BigDecimal.ZERO);
				revSumItemDto.setAspSgstasSgst(
						obj[4] != null ? new BigDecimal(String.valueOf(obj[4]))
								: BigDecimal.ZERO);
				revSumItemDto.setAspSgstasIgst(
						obj[5] != null ? new BigDecimal(String.valueOf(obj[5]))
								: BigDecimal.ZERO);
				revSumItemDto.setAspCgstasCgst(
						obj[6] != null ? new BigDecimal(String.valueOf(obj[6]))
								: BigDecimal.ZERO);
				revSumItemDto.setAspCgstasIgst(
						obj[7] != null ? new BigDecimal(String.valueOf(obj[7]))
								: BigDecimal.ZERO);
				revSumItemDto.setAspCessAmount(
						obj[8] != null ? new BigDecimal(String.valueOf(obj[8]))
								: BigDecimal.ZERO);
				revSumItemDto.setDistribution(
						obj[9] != null ? String.valueOf(obj[9]) : null);
				revSumItemDto.setDocType(
						obj[10] != null ? String.valueOf(obj[10]) : null);

				revSumItemDto.setGstnCount(obj[13] != null
						? new BigInteger(String.valueOf(obj[13]))
						: BigInteger.ZERO);
				revSumItemDto.setGstnIgstasIgst(obj[14] != null
						? new BigDecimal(String.valueOf(obj[14]))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnIgstasCgst(obj[15] != null
						? new BigDecimal(String.valueOf(obj[15]))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnIgstasSgst(obj[16] != null
						? new BigDecimal(String.valueOf(obj[16]))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnSgstasSgst(obj[17] != null
						? new BigDecimal(String.valueOf(obj[17]))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnSgstasIgst(obj[18] != null
						? new BigDecimal(String.valueOf(obj[18]))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnCgstasCgst(obj[19] != null
						? new BigDecimal(String.valueOf(obj[19]))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnCgstasIgst(obj[20] != null
						? new BigDecimal(String.valueOf(obj[20]))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnCessAmount(obj[21] != null
						? new BigDecimal(String.valueOf(obj[21]))
						: BigDecimal.ZERO);
				revSumItemDto.setDiffCount(revSumItemDto.getAspCount()
						.subtract(revSumItemDto.getGstnCount()));
				revSumItemDto.setDiffIgstasIgst(revSumItemDto.getAspIgstasIgst()
						.subtract(revSumItemDto.getGstnIgstasIgst()));
				revSumItemDto.setDiffIgstasSgst(revSumItemDto.getAspIgstasSgst()
						.subtract(revSumItemDto.getGstnIgstasSgst()));
				revSumItemDto.setDiffIgstasCgst(revSumItemDto.getAspIgstasCgst()
						.subtract(revSumItemDto.getGstnIgstasCgst()));
				revSumItemDto.setDiffSgstasSgst(revSumItemDto.getAspSgstasSgst()
						.subtract(revSumItemDto.getGstnSgstasSgst()));
				revSumItemDto.setDiffSgstasIgst(revSumItemDto.getAspSgstasIgst()
						.subtract(revSumItemDto.getGstnSgstasIgst()));
				revSumItemDto.setDiffCgstasCgst(revSumItemDto.getAspCgstasCgst()
						.subtract(revSumItemDto.getGstnCgstasCgst()));
				revSumItemDto.setDiffCgstasIgst(revSumItemDto.getAspCgstasIgst()
						.subtract(revSumItemDto.getGstnCgstasIgst()));
				revSumItemDto.setDiffCessAmount(revSumItemDto.getAspCessAmount()
						.subtract(revSumItemDto.getGstnCessAmount()));
				revSumItemDtos.add(revSumItemDto);
			}
		}
		return revSumItemDtos;
	}

	public List<Gstr6DistChannelRevSumRespItemDto> convertObjToResponseDtos(
			Annexure1SummaryReqDto reqDto) {
		List<Gstr6DistChannelRevSumRespItemDto> revSumItemDtos = new ArrayList<>();
		List<Gstr6DigiComputeDistributionEntity> objs = gstr6DistChannelRevSumDao
				.getGstinSummaryDetails(reqDto);
		if (objs != null) {
			for (Gstr6DigiComputeDistributionEntity obj : objs) {
				Gstr6DistChannelRevSumRespItemDto revSumItemDto = new Gstr6DistChannelRevSumRespItemDto();
				revSumItemDto.setAspCount(obj.getAspId() != null
						? new BigInteger(String.valueOf(obj.getAspId()))
						: BigInteger.ZERO);
				revSumItemDto.setAspIgstasIgst(obj.getIgstAsIgstAps() != null
						? new BigDecimal(String.valueOf(obj.getIgstAsIgstAps()))
						: BigDecimal.ZERO);
				revSumItemDto.setAspIgstasSgst(obj.getIgstAsSgstAps() != null
						? new BigDecimal(String.valueOf(obj.getIgstAsSgstAps()))
						: BigDecimal.ZERO);
				revSumItemDto.setAspIgstasCgst(obj.getIgstAsCgstAps() != null
						? new BigDecimal(String.valueOf(obj.getIgstAsCgstAps()))
						: BigDecimal.ZERO);
				revSumItemDto.setAspSgstasSgst(obj.getSgstAsSgstAps() != null
						? new BigDecimal(String.valueOf(obj.getSgstAsSgstAps()))
						: BigDecimal.ZERO);
				revSumItemDto.setAspSgstasIgst(obj.getSgstAsIgstAps() != null
						? new BigDecimal(String.valueOf(obj.getSgstAsIgstAps()))
						: BigDecimal.ZERO);
				revSumItemDto.setAspCgstasCgst(obj.getCgstAsCgstAps() != null
						? new BigDecimal(String.valueOf(obj.getCgstAsCgstAps()))
						: BigDecimal.ZERO);
				revSumItemDto.setAspCgstasIgst(obj.getCgstAsIgstAps() != null
						? new BigDecimal(String.valueOf(obj.getCgstAsIgstAps()))
						: BigDecimal.ZERO);
				revSumItemDto.setAspCessAmount(obj.getCessAps() != null
						? new BigDecimal(String.valueOf(obj.getCessAps()))
						: BigDecimal.ZERO);
				revSumItemDto
						.setDistribution(obj.getEligibiltyIndicator() != null
								? obj.getEligibiltyIndicator() : null);
				revSumItemDto.setDocType(obj.getDocTypeAsp() != null
						? obj.getDocTypeAsp() : null);

				revSumItemDto.setGstnCount(obj.getIdGstn() != null
						? new BigInteger(String.valueOf(obj.getIdGstn()))
						: BigInteger.ZERO);
				revSumItemDto.setGstnIgstasIgst(obj.getIgstAsIgstGstn() != null
						? new BigDecimal(
								String.valueOf(obj.getIgstAsIgstGstn()))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnIgstasCgst(obj.getIgstAsCgstGstn() != null
						? new BigDecimal(
								String.valueOf(obj.getIgstAsCgstGstn()))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnIgstasSgst(obj.getIgstAsSgstGstn() != null
						? new BigDecimal(
								String.valueOf(obj.getIgstAsSgstGstn()))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnSgstasSgst(obj.getSgstAsSgstGstn() != null
						? new BigDecimal(
								String.valueOf(obj.getSgstAsSgstGstn()))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnSgstasIgst(obj.getSgstAsIgstGstn() != null
						? new BigDecimal(
								String.valueOf(obj.getSgstAsIgstGstn()))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnCgstasCgst(obj.getCgstAsCgstGstn() != null
						? new BigDecimal(
								String.valueOf(obj.getCgstAsCgstGstn()))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnCgstasIgst(obj.getCgstAsIgstGstn() != null
						? new BigDecimal(
								String.valueOf(obj.getCgstAsIgstGstn()))
						: BigDecimal.ZERO);
				revSumItemDto.setGstnCessAmount(obj.getCessGSTN() != null
						? new BigDecimal(String.valueOf(obj.getCessGSTN()))
						: BigDecimal.ZERO);
				revSumItemDto.setDiffCount(revSumItemDto.getAspCount()
						.subtract(revSumItemDto.getGstnCount()));
				revSumItemDto.setDiffIgstasIgst(revSumItemDto.getAspIgstasIgst()
						.subtract(revSumItemDto.getGstnIgstasIgst()));
				revSumItemDto.setDiffIgstasSgst(revSumItemDto.getAspIgstasSgst()
						.subtract(revSumItemDto.getGstnIgstasSgst()));
				revSumItemDto.setDiffIgstasCgst(revSumItemDto.getAspIgstasCgst()
						.subtract(revSumItemDto.getGstnIgstasCgst()));
				revSumItemDto.setDiffSgstasSgst(revSumItemDto.getAspSgstasSgst()
						.subtract(revSumItemDto.getGstnSgstasSgst()));
				revSumItemDto.setDiffSgstasIgst(revSumItemDto.getAspSgstasIgst()
						.subtract(revSumItemDto.getGstnSgstasIgst()));
				revSumItemDto.setDiffCgstasCgst(revSumItemDto.getAspCgstasCgst()
						.subtract(revSumItemDto.getGstnCgstasCgst()));
				revSumItemDto.setDiffCgstasIgst(revSumItemDto.getAspCgstasIgst()
						.subtract(revSumItemDto.getGstnCgstasIgst()));
				revSumItemDto.setDiffCessAmount(revSumItemDto.getAspCessAmount()
						.subtract(revSumItemDto.getGstnCessAmount()));
				revSumItemDtos.add(revSumItemDto);
			}
		}
		return revSumItemDtos;
	}
	
	public List<Gstr6ReviewSummaryStringResponseDto> getGstr6SectionsSummary(
			List<Gstr6ReviewSummaryResponseDto> respDtos) {
		List<Gstr6ReviewSummaryStringResponseDto> respSumryDto = Lists
				.newLinkedList();

		respDtos.stream().forEach(dto -> {
			Gstr6ReviewSummaryStringResponseDto dtos = new Gstr6ReviewSummaryStringResponseDto();

			boolean flag = false;
			dtos.setGstin(dto.getGstin());
			dtos.setDocType(dto.getDocType());

			dtos.setAspCount(dto.getAspCount().toString());
			dtos.setAspInvValue(dto.getAspInvValue().toString());
			dtos.setAspTaxbValue(dto.getAspTaxbValue().toString());
			dtos.setAspTotTax(dto.getAspTotTax().toString());
			dtos.setAspIgst(dto.getAspIgst().toString());
			dtos.setAspSgst(dto.getAspSgst().toString());
			dtos.setAspCgst(dto.getAspCgst().toString());
			dtos.setAspCess(dto.getAspCess().toString());

			dtos.setGstnCount(dto.getGstnCount().toString());
			dtos.setGstnTotTax(dto.getGstnTotTax().toString());
			dtos.setGstnIgst(dto.getGstnIgst().toString());
			dtos.setGstnSgst(dto.getGstnSgst().toString());
			dtos.setGstnCgst(dto.getGstnCgst().toString());
			dtos.setGstnCess(dto.getGstnCess().toString());

			dtos.setDiffCount(dto.getDiffCount().toString());

			if ("Eligible / Ineligible ITC (Section 4)"
					.equalsIgnoreCase(dto.getDocType())) {
				dtos.setDiffCount("NA");
				dtos.setGstnInvValue("NA");
				dtos.setGstnTaxbValue("NA");
				dtos.setDiffInvValue("NA");
				dtos.setDiffTaxbValue("NA");
			} else {
				dtos.setGstnInvValue(dto.getGstnInvValue().toString());
				dtos.setGstnTaxbValue(dto.getGstnTaxbValue().toString());
				dtos.setDiffInvValue(dto.getDiffInvValue().toString());
				dtos.setDiffTaxbValue(dto.getDiffTaxbValue().toString());
			}
			dtos.setDiffTotTax(dto.getDiffTotTax().toString());
			dtos.setDiffIgst(dto.getDiffIgst().toString());
			dtos.setDiffSgst(dto.getDiffSgst().toString());
			dtos.setDiffCgst(dto.getDiffCgst().toString());
			dtos.setDiffCess(dto.getDiffCess().toString());

			if ("B2B (Section 3)".equalsIgnoreCase(dto.getDocType())
					|| "CDN (Section 6B)".equalsIgnoreCase(dto.getDocType())
					|| "B2BA (Section 6A)".equalsIgnoreCase(dto.getDocType())
					|| "CDNA (Section 6C)".equalsIgnoreCase(dto.getDocType())) {
				if ((dto.getAspCess().equals(dto.getGstnCess()))
						&& (dto.getAspCgst().equals(dto.getGstnCgst()))
						&& (dto.getAspCount().equals(dto.getGstnCount()))
						&& (dto.getAspIgst().equals(dto.getGstnIgst()))
						&& (dto.getAspInvValue().equals(dto.getGstnInvValue()))
						&& (dto.getAspSgst().equals(dto.getGstnSgst()))
						&& (dto.getAspTaxbValue().equals(dto.getGstnTaxbValue()))
						&& (dto.getAspTotTax().equals(dto.getGstnTotTax()))) {
					flag = true;
				}
			}

			dtos.setItems(buildItemsForStringSection(respSumryDto, dto, flag));
			respSumryDto.add(dtos);

		});

		return respSumryDto;

	}

	public List<Gstr6ReviewSummaryResponseStringItemDto> buildItemsForStringSection(
			List<Gstr6ReviewSummaryStringResponseDto> dtos,
			Gstr6ReviewSummaryResponseDto dto, boolean flag) {
		List<Gstr6ReviewSummaryResponseItemDto> items = dto.getItems();
		List<Gstr6ReviewSummaryResponseStringItemDto> respItemDto = Lists
				.newLinkedList();
		if (CollectionUtils.isNotEmpty(items)) {
			for (Gstr6ReviewSummaryResponseItemDto item : items) {
				Gstr6ReviewSummaryResponseStringItemDto itmDto = new Gstr6ReviewSummaryResponseStringItemDto();

				itmDto.setGstin(item.getGstin());
				itmDto.setRetPeriod(item.getRetPeriod());
				itmDto.setDocType(item.getDocType());
				itmDto.setEligInd(item.getEligInd());

				itmDto.setAspCount((item.getAspCount() == null)
						? null
						: item.getAspCount().toString());
				itmDto.setAspInvValue((item.getAspInvValue() == null)
						? null
						: item.getAspInvValue().toString());
				itmDto.setAspTaxbValue((item.getAspTaxbValue() == null)
						? null
						: item.getAspTaxbValue().toString());
				itmDto.setAspTotTax((item.getAspTotTax() == null)
						? BigDecimal.ZERO.toString()+".00"
						: item.getAspTotTax().toString());
				itmDto.setAspIgst(item.getAspIgst().toString());
				itmDto.setAspSgst(item.getAspSgst().toString());
				itmDto.setAspCgst(item.getAspCgst().toString());
				itmDto.setAspCess(item.getAspCess().toString());
				itmDto.setGstnCount((item.getGstnCount() == null)
						? null
						: item.getGstnCount().toString());
				
				
				itmDto.setGstnInvValue((item.getGstnInvValue() == null)
				? null
				: item.getGstnInvValue().toString());
				
				
				itmDto.setGstnTaxbValue((item.getGstnTaxbValue() == null)
						? null
								: item.getGstnTaxbValue().toString());
				itmDto.setGstnTotTax((item.getGstnTotTax() == null)
						? null
								: item.getGstnTotTax().toString());
				itmDto.setGstnIgst(item.getGstnIgst().toString());
				itmDto.setGstnSgst(item.getGstnSgst().toString());
				itmDto.setGstnCgst(item.getGstnCgst().toString());
				itmDto.setGstnCess(item.getGstnCess().toString());

				
				itmDto.setDiffCount((item.getDiffCount() == null)
						? null
						: item.getDiffCount().toString());
				itmDto.setDiffInvValue((item.getDiffInvValue() == null)
						? null
								: item.getDiffInvValue().toString());
				itmDto.setDiffTaxbValue((item.getDiffTaxbValue() == null)
						? null
								: item.getDiffTaxbValue().toString());
				itmDto.setDiffTotTax((item.getDiffTotTax() == null)
						? "N/A"
								: item.getDiffTotTax().toString());
				itmDto.setDiffIgst((item.getDiffIgst() == null) ? "N/A" :item.getDiffIgst().toString());
				itmDto.setDiffSgst((item.getDiffSgst() == null) ? "N/A" :item.getDiffSgst().toString());
				itmDto.setDiffCgst((item.getDiffCgst() == null) ? "N/A" :item.getDiffCgst().toString());
				itmDto.setDiffCess((item.getDiffCess() == null) ? "N/A" :item.getDiffCess().toString());

				if (flag == true) {
					itmDto.setDiffCount(BigInteger.ZERO.toString());
					itmDto.setDiffInvValue(BigDecimal.ZERO.toString()+".00");
					itmDto.setDiffTaxbValue(BigDecimal.ZERO.toString()+".00");
					itmDto.setDiffTotTax(BigDecimal.ZERO.toString()+".00");
					itmDto.setDiffIgst(BigDecimal.ZERO.toString()+".00");
					itmDto.setDiffSgst(BigDecimal.ZERO.toString()+".00");
					itmDto.setDiffCgst(BigDecimal.ZERO.toString()+".00");
					itmDto.setDiffCess(BigDecimal.ZERO.toString()+".00");
				}

				respItemDto.add(itmDto);
			}
			;
		}
		return respItemDto;
	}
}
