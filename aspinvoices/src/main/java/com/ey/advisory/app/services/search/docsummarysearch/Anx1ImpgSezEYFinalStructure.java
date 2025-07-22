package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("anx1ImpgSezEYFinalStructure")
public class Anx1ImpgSezEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getImpgSezEyList(
			List<Annexure1SummaryResp1Dto> impgSezEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// total
		List<Annexure1SummaryResp1Dto> view3KTotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());
		// Inv
		List<Annexure1SummaryResp1Dto> view3KInvFiltered = eySummaryListFromView
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> view3KCrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> view3KDrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		
		// If Total filtered list is not null
				if (view3KTotalFiltered != null & view3KTotalFiltered.size() > 0) {
					// then filter default List for 4A
					List<Annexure1SummaryResp1Dto> default3KTotalFiltered = impgSezEYList
							.stream()
							.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
							.collect(Collectors.toList());

					// If the default filtered list is not null
					default3KTotalFiltered.forEach(default3KTotal -> {
						// then remove it from List
						impgSezEYList.remove(default3KTotal);
					});

					view3KTotalFiltered.forEach(view3KInv -> {
						Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
						summaryRespDto.setTableSection(view3KInv.getTableSection());
						summaryRespDto.setIndex(0);
						summaryRespDto.setDocType(view3KInv.getDocType());
						summaryRespDto.setEyCount(view3KInv.getEyCount());
						summaryRespDto.setEyInvoiceValue(view3KInv.getEyInvoiceValue());
						summaryRespDto.setEyTaxableValue(view3KInv.getEyTaxableValue());
						summaryRespDto.setEyTaxPayble(view3KInv.getEyTaxPayble());
						summaryRespDto.setEyIgst(view3KInv.getEyIgst());
						summaryRespDto.setEySgst(view3KInv.getEySgst());
						summaryRespDto.setEyCgst(view3KInv.getEyCgst());
						summaryRespDto.setEyCess(view3KInv.getEyCess());
						summaryRespDto.setGstnCount(view3KInv.getGstnCount());
						summaryRespDto
								.setGstnInvoiceValue(view3KInv.getGstnInvoiceValue());
						summaryRespDto
								.setGstnTaxableValue(view3KInv.getGstnTaxableValue());
						summaryRespDto.setGstnTaxPayble(view3KInv.getGstnTaxPayble());
						summaryRespDto.setGstnIgst(view3KInv.getGstnIgst());
						summaryRespDto.setGstnSgst(view3KInv.getGstnSgst());
						summaryRespDto.setGstnCgst(view3KInv.getGstnCgst());
						summaryRespDto.setGstnCess(view3KInv.getGstnCess());
						summaryRespDto.setDiffCount(view3KInv.getDiffCount());
						summaryRespDto
								.setDiffInvoiceValue(view3KInv.getDiffInvoiceValue());
						summaryRespDto
								.setDiffTaxableValue(view3KInv.getDiffTaxableValue());
						summaryRespDto.setDiffTaxPayble(view3KInv.getDiffTaxPayble());
						summaryRespDto.setDiffIgst(view3KInv.getDiffIgst());
						summaryRespDto.setDiffSgst(view3KInv.getDiffSgst());
						summaryRespDto.setDiffCgst(view3KInv.getDiffCgst());
						summaryRespDto.setDiffCess(view3KInv.getDiffCess());
						summaryRespDto.setMemoCount(view3KInv.getMemoCount());
						summaryRespDto
								.setMemoInvoiceValue(view3KInv.getMemoInvoiceValue());
						summaryRespDto
								.setMemoTaxableValue(view3KInv.getMemoTaxableValue());
						summaryRespDto.setMemoTaxPayble(view3KInv.getMemoTaxPayble());
						summaryRespDto.setMemoIgst(view3KInv.getMemoIgst());
						summaryRespDto.setMemoSgst(view3KInv.getMemoSgst());
						summaryRespDto.setMemoCgst(view3KInv.getMemoCgst());
						summaryRespDto.setMemoCess(view3KInv.getMemoCess());

						impgSezEYList.add(summaryRespDto);
					});
				}

		
		
		
		// If INV filtered list is not null
		if (view3KInvFiltered != null & view3KInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3KInvFiltered = impgSezEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3KInvFiltered.forEach(default3KInv -> {
				// then remove it from List
				impgSezEYList.remove(default3KInv);
			});

			view3KInvFiltered.forEach(view3KInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3KInv.getTableSection());
				summaryRespDto.setIndex(1);
				summaryRespDto.setDocType(view3KInv.getDocType());
				summaryRespDto.setEyCount(view3KInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3KInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3KInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3KInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3KInv.getEyIgst());
				summaryRespDto.setEySgst(view3KInv.getEySgst());
				summaryRespDto.setEyCgst(view3KInv.getEyCgst());
				summaryRespDto.setEyCess(view3KInv.getEyCess());
				summaryRespDto.setGstnCount(view3KInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3KInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3KInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3KInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3KInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3KInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3KInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3KInv.getGstnCess());
				summaryRespDto.setDiffCount(view3KInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3KInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3KInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3KInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3KInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3KInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3KInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3KInv.getDiffCess());
				summaryRespDto.setMemoCount(view3KInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3KInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3KInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3KInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3KInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3KInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3KInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3KInv.getMemoCess());

				impgSezEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (view3KCrFiltered != null & view3KCrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ACrFiltered = impgSezEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ACrFiltered.forEach(default3KCr -> {
				// then remove it from List
				impgSezEYList.remove(default3KCr);
			});

			// Iterate view list
			view3KCrFiltered.forEach(view3KCr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3KCr.getTableSection());
				summaryRespDto.setDocType(view3KCr.getDocType());
				summaryRespDto.setIndex(3);
				summaryRespDto.setEyCount(view3KCr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3KCr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3KCr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3KCr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3KCr.getEyIgst());
				summaryRespDto.setEySgst(view3KCr.getEySgst());
				summaryRespDto.setEyCgst(view3KCr.getEyCgst());
				summaryRespDto.setEyCess(view3KCr.getEyCess());
				summaryRespDto.setGstnCount(view3KCr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3KCr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3KCr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3KCr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3KCr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3KCr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3KCr.getGstnCgst());
				summaryRespDto.setGstnCess(view3KCr.getGstnCess());
				summaryRespDto.setDiffCount(view3KCr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3KCr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3KCr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3KCr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3KCr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3KCr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3KCr.getDiffCgst());
				summaryRespDto.setDiffCess(view3KCr.getDiffCess());
				summaryRespDto.setMemoCount(view3KCr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3KCr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3KCr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3KCr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3KCr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3KCr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3KCr.getMemoCgst());
				summaryRespDto.setMemoCess(view3KCr.getMemoCess());

				impgSezEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (view3KDrFiltered != null & view3KDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> defaultDrFiltered = impgSezEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultDrFiltered.forEach(default3KDr -> {
				// then remove it from List
				impgSezEYList.remove(default3KDr);
			});

			view3KDrFiltered.forEach(view3KDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3KDr.getTableSection());
				summaryRespDto.setIndex(2);
				summaryRespDto.setDocType(view3KDr.getDocType());
				summaryRespDto.setEyCount(view3KDr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3KDr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3KDr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3KDr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3KDr.getEyIgst());
				summaryRespDto.setEySgst(view3KDr.getEySgst());
				summaryRespDto.setEyCgst(view3KDr.getEyCgst());
				summaryRespDto.setEyCess(view3KDr.getEyCess());
				summaryRespDto.setGstnCount(view3KDr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3KDr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3KDr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3KDr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3KDr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3KDr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3KDr.getGstnCgst());
				summaryRespDto.setGstnCess(view3KDr.getGstnCess());
				summaryRespDto.setDiffCount(view3KDr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3KDr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3KDr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3KDr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3KDr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3KDr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3KDr.getDiffCgst());
				summaryRespDto.setDiffCess(view3KDr.getDiffCess());
				summaryRespDto.setMemoCount(view3KDr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3KDr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3KDr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3KDr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3KDr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3KDr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3KDr.getMemoCgst());
				summaryRespDto.setMemoCess(view3KDr.getMemoCess());

				impgSezEYList.add(summaryRespDto);
			});
		}
		Collections.sort(impgSezEYList, new Comparator<Annexure1SummaryResp1Dto>() {
			@Override
			public int compare(Annexure1SummaryResp1Dto respDto1,
					Annexure1SummaryResp1Dto respDto2) {
				return respDto1.getIndex()
						.compareTo(respDto2.getIndex());
			}
		});


		return impgSezEYList;

	}

}
