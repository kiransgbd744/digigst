package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;

@Service("anx1ImpgEYFinalStructure")
public class Anx1ImpgEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getImpgEyList(
			List<Annexure1SummaryResp1Dto> impgEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		List<Annexure1SummaryResp1Dto> view3JTotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());
		
		List<Annexure1SummaryResp1Dto> view3JInvFiltered = eySummaryListFromView
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> view3JCrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> view3JDrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		
		// If Total filtered list is not null
				if (view3JTotalFiltered != null & view3JTotalFiltered.size() > 0) {
					// then filter default List for 4A
					List<Annexure1SummaryResp1Dto> default3JTotalFiltered = impgEYList
							.stream()
							.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
							.collect(Collectors.toList());

					// If the default filtered list is not null
					default3JTotalFiltered.forEach(default3JTotal -> {
						// then remove it from List
						impgEYList.remove(default3JTotal);
					});

					view3JTotalFiltered.forEach(view3JInv -> {
						Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
						summaryRespDto.setTableSection(view3JInv.getTableSection());
						summaryRespDto.setIndex(0);
						summaryRespDto.setDocType(view3JInv.getDocType());
						summaryRespDto.setEyCount(view3JInv.getEyCount());
						summaryRespDto.setEyInvoiceValue(view3JInv.getEyInvoiceValue());
						summaryRespDto.setEyTaxableValue(view3JInv.getEyTaxableValue());
						summaryRespDto.setEyTaxPayble(view3JInv.getEyTaxPayble());
						summaryRespDto.setEyIgst(view3JInv.getEyIgst());
						summaryRespDto.setEySgst(view3JInv.getEySgst());
						summaryRespDto.setEyCgst(view3JInv.getEyCgst());
						summaryRespDto.setEyCess(view3JInv.getEyCess());
						summaryRespDto.setGstnCount(view3JInv.getGstnCount());
						summaryRespDto
								.setGstnInvoiceValue(view3JInv.getGstnInvoiceValue());
						summaryRespDto
								.setGstnTaxableValue(view3JInv.getGstnTaxableValue());
						summaryRespDto.setGstnTaxPayble(view3JInv.getGstnTaxPayble());
						summaryRespDto.setGstnIgst(view3JInv.getGstnIgst());
						summaryRespDto.setGstnSgst(view3JInv.getGstnSgst());
						summaryRespDto.setGstnCgst(view3JInv.getGstnCgst());
						summaryRespDto.setGstnCess(view3JInv.getGstnCess());
						summaryRespDto.setDiffCount(view3JInv.getDiffCount());
						summaryRespDto
								.setDiffInvoiceValue(view3JInv.getDiffInvoiceValue());
						summaryRespDto
								.setDiffTaxableValue(view3JInv.getDiffTaxableValue());
						summaryRespDto.setDiffTaxPayble(view3JInv.getDiffTaxPayble());
						summaryRespDto.setDiffIgst(view3JInv.getDiffIgst());
						summaryRespDto.setDiffSgst(view3JInv.getDiffSgst());
						summaryRespDto.setDiffCgst(view3JInv.getDiffCgst());
						summaryRespDto.setDiffCess(view3JInv.getDiffCess());
						summaryRespDto.setMemoCount(view3JInv.getMemoCount());
						summaryRespDto
								.setMemoInvoiceValue(view3JInv.getMemoInvoiceValue());
						summaryRespDto
								.setMemoTaxableValue(view3JInv.getMemoTaxableValue());
						summaryRespDto.setMemoTaxPayble(view3JInv.getMemoTaxPayble());
						summaryRespDto.setMemoIgst(view3JInv.getMemoIgst());
						summaryRespDto.setMemoSgst(view3JInv.getMemoSgst());
						summaryRespDto.setMemoCgst(view3JInv.getMemoCgst());
						summaryRespDto.setMemoCess(view3JInv.getMemoCess());

						impgEYList.add(summaryRespDto);
					});
				}
		
		
		// If INV filtered list is not null
		if (view3JInvFiltered != null & view3JInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3JInvFiltered = impgEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3JInvFiltered.forEach(default3JInv -> {
				// then remove it from List
				impgEYList.remove(default3JInv);
			});

			view3JInvFiltered.forEach(view3JInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3JInv.getTableSection());
				summaryRespDto.setIndex(1);
				summaryRespDto.setDocType(view3JInv.getDocType());
				summaryRespDto.setEyCount(view3JInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3JInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3JInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3JInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3JInv.getEyIgst());
				summaryRespDto.setEySgst(view3JInv.getEySgst());
				summaryRespDto.setEyCgst(view3JInv.getEyCgst());
				summaryRespDto.setEyCess(view3JInv.getEyCess());
				summaryRespDto.setGstnCount(view3JInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3JInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3JInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3JInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3JInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3JInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3JInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3JInv.getGstnCess());
				summaryRespDto.setDiffCount(view3JInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3JInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3JInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3JInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3JInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3JInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3JInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3JInv.getDiffCess());
				summaryRespDto.setMemoCount(view3JInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3JInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3JInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3JInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3JInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3JInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3JInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3JInv.getMemoCess());

				impgEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (view3JCrFiltered != null & view3JCrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ACrFiltered = impgEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ACrFiltered.forEach(default3JCr -> {
				// then remove it from List
				impgEYList.remove(default3JCr);
			});

			// Iterate view list
			view3JCrFiltered.forEach(view3JCr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3JCr.getTableSection());
				summaryRespDto.setIndex(3);
				summaryRespDto.setDocType(view3JCr.getDocType());
				summaryRespDto.setEyCount(view3JCr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3JCr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3JCr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3JCr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3JCr.getEyIgst());
				summaryRespDto.setEySgst(view3JCr.getEySgst());
				summaryRespDto.setEyCgst(view3JCr.getEyCgst());
				summaryRespDto.setEyCess(view3JCr.getEyCess());
				summaryRespDto.setGstnCount(view3JCr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3JCr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3JCr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3JCr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3JCr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3JCr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3JCr.getGstnCgst());
				summaryRespDto.setGstnCess(view3JCr.getGstnCess());
				summaryRespDto.setDiffCount(view3JCr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3JCr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3JCr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3JCr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3JCr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3JCr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3JCr.getDiffCgst());
				summaryRespDto.setDiffCess(view3JCr.getDiffCess());
				summaryRespDto.setMemoCount(view3JCr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3JCr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3JCr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3JCr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3JCr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3JCr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3JCr.getMemoCgst());
				summaryRespDto.setMemoCess(view3JCr.getMemoCess());

				impgEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (view3JDrFiltered != null & view3JDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> defaultDrFiltered = impgEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultDrFiltered.forEach(default3jDr -> {
				// then remove it from List
				impgEYList.remove(default3jDr);
			});

			view3JDrFiltered.forEach(view3jDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3jDr.getTableSection());
				summaryRespDto.setIndex(2);
				summaryRespDto.setDocType(view3jDr.getDocType());
				summaryRespDto.setEyCount(view3jDr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3jDr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3jDr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3jDr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3jDr.getEyIgst());
				summaryRespDto.setEySgst(view3jDr.getEySgst());
				summaryRespDto.setEyCgst(view3jDr.getEyCgst());
				summaryRespDto.setEyCess(view3jDr.getEyCess());
				summaryRespDto.setGstnCount(view3jDr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3jDr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3jDr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3jDr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3jDr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3jDr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3jDr.getGstnCgst());
				summaryRespDto.setGstnCess(view3jDr.getGstnCess());
				summaryRespDto.setDiffCount(view3jDr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3jDr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3jDr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3jDr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3jDr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3jDr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3jDr.getDiffCgst());
				summaryRespDto.setDiffCess(view3jDr.getDiffCess());
				summaryRespDto.setMemoCount(view3jDr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3jDr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3jDr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3jDr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3jDr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3jDr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3jDr.getMemoCgst());
				summaryRespDto.setMemoCess(view3jDr.getMemoCess());

				impgEYList.add(summaryRespDto);
			});
		}
		Collections.sort(impgEYList, new Comparator<Annexure1SummaryResp1Dto>() {
			@Override
			public int compare(Annexure1SummaryResp1Dto respDto1,
					Annexure1SummaryResp1Dto respDto2) {
				return respDto1.getIndex()
						.compareTo(respDto2.getIndex());
			}
		});

		return impgEYList;

	}

}
