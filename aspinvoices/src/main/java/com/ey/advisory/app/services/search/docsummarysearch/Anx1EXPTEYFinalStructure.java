package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;

@Service("anx1EXPTEYFinalStructure")
public class Anx1EXPTEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getEXPTEyList(
			List<Annexure1SummaryResp1Dto> exptEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// For Total
		List<Annexure1SummaryResp1Dto> view3CTotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For INV
		List<Annexure1SummaryResp1Dto> view3CInvFiltered = eySummaryListFromView
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> view3CCrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> view3CDrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// If Total filtered list is not null
		if (view3CTotalFiltered != null & view3CTotalFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3CTotalFiltered = exptEYList
					.stream()
					.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3CTotalFiltered.forEach(default3CTotal -> {
				// then remove it from List
				exptEYList.remove(default3CTotal);
			});

			view3CTotalFiltered.forEach(view3CInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3CInv.getTableSection());
				summaryRespDto.setIndex(0);
				summaryRespDto.setDocType(view3CInv.getDocType());
				summaryRespDto.setEyCount(view3CInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3CInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3CInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3CInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3CInv.getEyIgst());
				summaryRespDto.setEySgst(view3CInv.getEySgst());
				summaryRespDto.setEyCgst(view3CInv.getEyCgst());
				summaryRespDto.setEyCess(view3CInv.getEyCess());
				summaryRespDto.setGstnCount(view3CInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3CInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3CInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3CInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3CInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3CInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3CInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3CInv.getGstnCess());
				summaryRespDto.setDiffCount(view3CInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3CInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3CInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3CInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3CInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3CInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3CInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3CInv.getDiffCess());
				summaryRespDto.setMemoCount(view3CInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3CInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3CInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3CInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3CInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3CInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3CInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3CInv.getMemoCess());

				exptEYList.add(summaryRespDto);
			});
		}

		// If INV filtered list is not null
		if (view3CInvFiltered != null & view3CInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3CInvFiltered = exptEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3CInvFiltered.forEach(default3CInv -> {
				// then remove it from List
				exptEYList.remove(default3CInv);
			});

			view3CInvFiltered.forEach(view3CInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3CInv.getTableSection());
				summaryRespDto.setIndex(1);
				summaryRespDto.setDocType(view3CInv.getDocType());
				summaryRespDto.setEyCount(view3CInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3CInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3CInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3CInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3CInv.getEyIgst());
				summaryRespDto.setEySgst(view3CInv.getEySgst());
				summaryRespDto.setEyCgst(view3CInv.getEyCgst());
				summaryRespDto.setEyCess(view3CInv.getEyCess());
				summaryRespDto.setGstnCount(view3CInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3CInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3CInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3CInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3CInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3CInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3CInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3CInv.getGstnCess());
				summaryRespDto.setDiffCount(view3CInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3CInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3CInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3CInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3CInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3CInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3CInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3CInv.getDiffCess());
				summaryRespDto.setMemoCount(view3CInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3CInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3CInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3CInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3CInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3CInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3CInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3CInv.getMemoCess());

				exptEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (view3CCrFiltered != null & view3CCrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3CCrFiltered = exptEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3CCrFiltered.forEach(default3CCr -> {
				// then remove it from List
				exptEYList.remove(default3CCr);
			});

			// Iterate view list
			view3CCrFiltered.forEach(view3CCr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3CCr.getTableSection());
				summaryRespDto.setIndex(3);
				summaryRespDto.setDocType(view3CCr.getDocType());
				summaryRespDto.setEyCount(view3CCr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3CCr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3CCr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3CCr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3CCr.getEyIgst());
				summaryRespDto.setEySgst(view3CCr.getEySgst());
				summaryRespDto.setEyCgst(view3CCr.getEyCgst());
				summaryRespDto.setEyCess(view3CCr.getEyCess());
				summaryRespDto.setGstnCount(view3CCr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3CCr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3CCr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3CCr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3CCr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3CCr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3CCr.getGstnCgst());
				summaryRespDto.setGstnCess(view3CCr.getGstnCess());
				summaryRespDto.setDiffCount(view3CCr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3CCr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3CCr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3CCr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3CCr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3CCr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3CCr.getDiffCgst());
				summaryRespDto.setDiffCess(view3CCr.getDiffCess());
				summaryRespDto.setMemoCount(view3CCr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3CCr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3CCr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3CCr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3CCr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3CCr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3CCr.getMemoCgst());
				summaryRespDto.setMemoCess(view3CCr.getMemoCess());

				exptEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (view3CDrFiltered != null & view3CDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3CDrFiltered = exptEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3CDrFiltered.forEach(default3CDr -> {
				// then remove it from List
				exptEYList.remove(default3CDr);
			});

			view3CDrFiltered.forEach(viewDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(viewDr.getTableSection());
				summaryRespDto.setIndex(2);
				summaryRespDto.setDocType(viewDr.getDocType());
				summaryRespDto.setEyCount(viewDr.getEyCount());
				summaryRespDto.setEyInvoiceValue(viewDr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(viewDr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(viewDr.getEyTaxPayble());
				summaryRespDto.setEyIgst(viewDr.getEyIgst());
				summaryRespDto.setEySgst(viewDr.getEySgst());
				summaryRespDto.setEyCgst(viewDr.getEyCgst());
				summaryRespDto.setEyCess(viewDr.getEyCess());
				summaryRespDto.setGstnCount(viewDr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(viewDr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(viewDr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewDr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(viewDr.getGstnIgst());
				summaryRespDto.setGstnSgst(viewDr.getGstnSgst());
				summaryRespDto.setGstnCgst(viewDr.getGstnCgst());
				summaryRespDto.setGstnCess(viewDr.getGstnCess());
				summaryRespDto.setDiffCount(viewDr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(viewDr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(viewDr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewDr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(viewDr.getDiffIgst());
				summaryRespDto.setDiffSgst(viewDr.getDiffSgst());
				summaryRespDto.setDiffCgst(viewDr.getDiffCgst());
				summaryRespDto.setDiffCess(viewDr.getDiffCess());
				summaryRespDto.setMemoCount(viewDr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(viewDr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(viewDr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(viewDr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(viewDr.getMemoIgst());
				summaryRespDto.setMemoSgst(viewDr.getMemoSgst());
				summaryRespDto.setMemoCgst(viewDr.getMemoCgst());
				summaryRespDto.setMemoCess(viewDr.getMemoCess());

				exptEYList.add(summaryRespDto);
			});
		}

		Collections.sort(exptEYList,
				new Comparator<Annexure1SummaryResp1Dto>() {
					@Override
					public int compare(Annexure1SummaryResp1Dto respDto1,
							Annexure1SummaryResp1Dto respDto2) {
						return respDto1.getIndex()
								.compareTo(respDto2.getIndex());
					}
				});
		return exptEYList;

	}

}
