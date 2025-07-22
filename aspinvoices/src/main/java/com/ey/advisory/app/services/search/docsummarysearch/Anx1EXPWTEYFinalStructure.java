package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;

@Service("anx1EXPWTEYFinalStructure")
public class Anx1EXPWTEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getEXPWTEyList(
			List<Annexure1SummaryResp1Dto> expwtEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// For Total
		List<Annexure1SummaryResp1Dto> view3DTotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For INV
		List<Annexure1SummaryResp1Dto> view3DInvFiltered = eySummaryListFromView
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> view3DCrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> view3DDrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// If Total filtered list is not null
		if (view3DTotalFiltered != null & view3DTotalFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3DTotalFiltered = expwtEYList
					.stream()
					.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3DTotalFiltered.forEach(default3DTotal -> {
				// then remove it from List
				expwtEYList.remove(default3DTotal);
			});

			view3DTotalFiltered.forEach(view3DInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3DInv.getTableSection());
				summaryRespDto.setIndex(0);
				summaryRespDto.setDocType(view3DInv.getDocType());
				summaryRespDto.setEyCount(view3DInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3DInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3DInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3DInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3DInv.getEyIgst());
				summaryRespDto.setEySgst(view3DInv.getEySgst());
				summaryRespDto.setEyCgst(view3DInv.getEyCgst());
				summaryRespDto.setEyCess(view3DInv.getEyCess());
				summaryRespDto.setGstnCount(view3DInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3DInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3DInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3DInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3DInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3DInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3DInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3DInv.getGstnCess());
				summaryRespDto.setDiffCount(view3DInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3DInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3DInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3DInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3DInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3DInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3DInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3DInv.getDiffCess());
				summaryRespDto.setMemoCount(view3DInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3DInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3DInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3DInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3DInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3DInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3DInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3DInv.getMemoCess());

				expwtEYList.add(summaryRespDto);
			});
		}

		// If INV filtered list is not null
		if (view3DInvFiltered != null & view3DInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3DInvFiltered = expwtEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3DInvFiltered.forEach(default3DInv -> {
				// then remove it from List
				expwtEYList.remove(default3DInv);
			});

			view3DInvFiltered.forEach(view3DInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3DInv.getTableSection());
				summaryRespDto.setIndex(1);
				summaryRespDto.setDocType(view3DInv.getDocType());
				summaryRespDto.setEyCount(view3DInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3DInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3DInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3DInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3DInv.getEyIgst());
				summaryRespDto.setEySgst(view3DInv.getEySgst());
				summaryRespDto.setEyCgst(view3DInv.getEyCgst());
				summaryRespDto.setEyCess(view3DInv.getEyCess());
				summaryRespDto.setGstnCount(view3DInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3DInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3DInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3DInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3DInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3DInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3DInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3DInv.getGstnCess());
				summaryRespDto.setDiffCount(view3DInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3DInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3DInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3DInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3DInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3DInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3DInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3DInv.getDiffCess());
				summaryRespDto.setMemoCount(view3DInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3DInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3DInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3DInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3DInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3DInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3DInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3DInv.getMemoCess());

				expwtEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (view3DCrFiltered != null & view3DCrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3CCrFiltered = expwtEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3CCrFiltered.forEach(default3DCr -> {
				// then remove it from List
				expwtEYList.remove(default3DCr);
			});

			// Iterate view list
			view3DCrFiltered.forEach(view3DCr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3DCr.getTableSection());
				summaryRespDto.setIndex(3);
				summaryRespDto.setDocType(view3DCr.getDocType());
				summaryRespDto.setEyCount(view3DCr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3DCr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3DCr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3DCr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3DCr.getEyIgst());
				summaryRespDto.setEySgst(view3DCr.getEySgst());
				summaryRespDto.setEyCgst(view3DCr.getEyCgst());
				summaryRespDto.setEyCess(view3DCr.getEyCess());
				summaryRespDto.setGstnCount(view3DCr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3DCr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3DCr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3DCr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3DCr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3DCr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3DCr.getGstnCgst());
				summaryRespDto.setGstnCess(view3DCr.getGstnCess());
				summaryRespDto.setDiffCount(view3DCr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3DCr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3DCr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3DCr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3DCr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3DCr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3DCr.getDiffCgst());
				summaryRespDto.setDiffCess(view3DCr.getDiffCess());
				summaryRespDto.setMemoCount(view3DCr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3DCr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3DCr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3DCr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3DCr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3DCr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3DCr.getMemoCgst());
				summaryRespDto.setMemoCess(view3DCr.getMemoCess());

				expwtEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (view3DDrFiltered != null & view3DDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3CDrFiltered = expwtEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3CDrFiltered.forEach(default3CDr -> {
				// then remove it from List
				expwtEYList.remove(default3CDr);
			});

			view3DDrFiltered.forEach(view3DDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3DDr.getTableSection());
				summaryRespDto.setIndex(2);
				summaryRespDto.setDocType(view3DDr.getDocType());
				summaryRespDto.setEyCount(view3DDr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3DDr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3DDr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3DDr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3DDr.getEyIgst());
				summaryRespDto.setEySgst(view3DDr.getEySgst());
				summaryRespDto.setEyCgst(view3DDr.getEyCgst());
				summaryRespDto.setEyCess(view3DDr.getEyCess());
				summaryRespDto.setGstnCount(view3DDr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3DDr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3DDr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3DDr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3DDr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3DDr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3DDr.getGstnCgst());
				summaryRespDto.setGstnCess(view3DDr.getGstnCess());
				summaryRespDto.setDiffCount(view3DDr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3DDr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3DDr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3DDr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3DDr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3DDr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3DDr.getDiffCgst());
				summaryRespDto.setDiffCess(view3DDr.getDiffCess());
				summaryRespDto.setMemoCount(view3DDr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3DDr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3DDr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3DDr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3DDr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3DDr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3DDr.getMemoCgst());
				summaryRespDto.setMemoCess(view3DDr.getMemoCess());

				expwtEYList.add(summaryRespDto);
			});
		}
		Collections.sort(expwtEYList,
				new Comparator<Annexure1SummaryResp1Dto>() {
					@Override
					public int compare(Annexure1SummaryResp1Dto respDto1,
							Annexure1SummaryResp1Dto respDto2) {
						return respDto1.getIndex()
								.compareTo(respDto2.getIndex());
					}
				});
		return expwtEYList;

	}

}
