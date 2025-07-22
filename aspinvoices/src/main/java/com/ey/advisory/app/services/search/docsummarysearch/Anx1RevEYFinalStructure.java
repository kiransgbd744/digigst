package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("anx1RevEYFinalStructure")
public class Anx1RevEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getRevhEyList(
			List<Annexure1SummaryResp1Dto> revhEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// Total
		List<Annexure1SummaryResp1Dto> view3HTotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// SLF
		List<Annexure1SummaryResp1Dto> view3HSLFFiltered = eySummaryListFromView
				.stream().filter(p -> "SLF".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> view3HCrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> view3HDrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// If Total filtered list is not null
				if (view3HTotalFiltered != null & view3HTotalFiltered.size() > 0) {
					// then filter default List for 4A
					List<Annexure1SummaryResp1Dto> default3HTotalFiltered = revhEYList
							.stream()
							.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
							.collect(Collectors.toList());

					// If the default filtered list is not null
					default3HTotalFiltered.forEach(default3HTotal -> {
						// then remove it from List
						revhEYList.remove(default3HTotal);
					});

					view3HTotalFiltered.forEach(view3HSLF -> {
						Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
						summaryRespDto.setTableSection(view3HSLF.getTableSection());
						summaryRespDto.setIndex(0);
						summaryRespDto.setDocType(view3HSLF.getDocType());
						summaryRespDto.setEyCount(view3HSLF.getEyCount());
						summaryRespDto.setEyInvoiceValue(view3HSLF.getEyInvoiceValue());
						summaryRespDto.setEyTaxableValue(view3HSLF.getEyTaxableValue());
						summaryRespDto.setEyTaxPayble(view3HSLF.getEyTaxPayble());
						summaryRespDto.setEyIgst(view3HSLF.getEyIgst());
						summaryRespDto.setEySgst(view3HSLF.getEySgst());
						summaryRespDto.setEyCgst(view3HSLF.getEyCgst());
						summaryRespDto.setEyCess(view3HSLF.getEyCess());
						summaryRespDto.setGstnCount(view3HSLF.getGstnCount());
						summaryRespDto
								.setGstnInvoiceValue(view3HSLF.getGstnInvoiceValue());
						summaryRespDto
								.setGstnTaxableValue(view3HSLF.getGstnTaxableValue());
						summaryRespDto.setGstnTaxPayble(view3HSLF.getGstnTaxPayble());
						summaryRespDto.setGstnIgst(view3HSLF.getGstnIgst());
						summaryRespDto.setGstnSgst(view3HSLF.getGstnSgst());
						summaryRespDto.setGstnCgst(view3HSLF.getGstnCgst());
						summaryRespDto.setGstnCess(view3HSLF.getGstnCess());
						summaryRespDto.setDiffCount(view3HSLF.getDiffCount());
						summaryRespDto
								.setDiffInvoiceValue(view3HSLF.getDiffInvoiceValue());
						summaryRespDto
								.setDiffTaxableValue(view3HSLF.getDiffTaxableValue());
						summaryRespDto.setDiffTaxPayble(view3HSLF.getDiffTaxPayble());
						summaryRespDto.setDiffIgst(view3HSLF.getDiffIgst());
						summaryRespDto.setDiffSgst(view3HSLF.getDiffSgst());
						summaryRespDto.setDiffCgst(view3HSLF.getDiffCgst());
						summaryRespDto.setDiffCess(view3HSLF.getDiffCess());
						summaryRespDto.setMemoCount(view3HSLF.getMemoCount());
						summaryRespDto
								.setMemoInvoiceValue(view3HSLF.getMemoInvoiceValue());
						summaryRespDto
								.setMemoTaxableValue(view3HSLF.getMemoTaxableValue());
						summaryRespDto.setMemoTaxPayble(view3HSLF.getMemoTaxPayble());
						summaryRespDto.setMemoIgst(view3HSLF.getMemoIgst());
						summaryRespDto.setMemoSgst(view3HSLF.getMemoSgst());
						summaryRespDto.setMemoCgst(view3HSLF.getMemoCgst());
						summaryRespDto.setMemoCess(view3HSLF.getMemoCess());

						revhEYList.add(summaryRespDto);
					});
				}
		
		// If INV filtered list is not null
		if (view3HSLFFiltered != null & view3HSLFFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3HSLFFiltered = revhEYList
					.stream()
					.filter(p -> "SLF".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3HSLFFiltered.forEach(default3HSLF -> {
				// then remove it from List
				revhEYList.remove(default3HSLF);
			});

			view3HSLFFiltered.forEach(view3HSLF -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3HSLF.getTableSection());
				summaryRespDto.setIndex(1);
				summaryRespDto.setDocType(view3HSLF.getDocType());
				summaryRespDto.setEyCount(view3HSLF.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3HSLF.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3HSLF.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3HSLF.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3HSLF.getEyIgst());
				summaryRespDto.setEySgst(view3HSLF.getEySgst());
				summaryRespDto.setEyCgst(view3HSLF.getEyCgst());
				summaryRespDto.setEyCess(view3HSLF.getEyCess());
				summaryRespDto.setGstnCount(view3HSLF.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3HSLF.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3HSLF.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3HSLF.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3HSLF.getGstnIgst());
				summaryRespDto.setGstnSgst(view3HSLF.getGstnSgst());
				summaryRespDto.setGstnCgst(view3HSLF.getGstnCgst());
				summaryRespDto.setGstnCess(view3HSLF.getGstnCess());
				summaryRespDto.setDiffCount(view3HSLF.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3HSLF.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3HSLF.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3HSLF.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3HSLF.getDiffIgst());
				summaryRespDto.setDiffSgst(view3HSLF.getDiffSgst());
				summaryRespDto.setDiffCgst(view3HSLF.getDiffCgst());
				summaryRespDto.setDiffCess(view3HSLF.getDiffCess());
				summaryRespDto.setMemoCount(view3HSLF.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3HSLF.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3HSLF.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3HSLF.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3HSLF.getMemoIgst());
				summaryRespDto.setMemoSgst(view3HSLF.getMemoSgst());
				summaryRespDto.setMemoCgst(view3HSLF.getMemoCgst());
				summaryRespDto.setMemoCess(view3HSLF.getMemoCess());

				revhEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (view3HCrFiltered != null & view3HCrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3HCrFiltered = revhEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3HCrFiltered.forEach(default3HCr -> {
				// then remove it from List
				revhEYList.remove(default3HCr);
			});

			// Iterate view list
			view3HCrFiltered.forEach(view3HCr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3HCr.getTableSection());
				summaryRespDto.setIndex(3);
				summaryRespDto.setDocType(view3HCr.getDocType());
				summaryRespDto.setEyCount(view3HCr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3HCr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3HCr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3HCr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3HCr.getEyIgst());
				summaryRespDto.setEySgst(view3HCr.getEySgst());
				summaryRespDto.setEyCgst(view3HCr.getEyCgst());
				summaryRespDto.setEyCess(view3HCr.getEyCess());
				summaryRespDto.setGstnCount(view3HCr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3HCr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3HCr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3HCr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3HCr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3HCr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3HCr.getGstnCgst());
				summaryRespDto.setGstnCess(view3HCr.getGstnCess());
				summaryRespDto.setDiffCount(view3HCr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3HCr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3HCr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3HCr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3HCr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3HCr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3HCr.getDiffCgst());
				summaryRespDto.setDiffCess(view3HCr.getDiffCess());
				summaryRespDto.setMemoCount(view3HCr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3HCr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3HCr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3HCr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3HCr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3HCr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3HCr.getMemoCgst());
				summaryRespDto.setMemoCess(view3HCr.getMemoCess());

				revhEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (view3HDrFiltered != null & view3HDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3EDrFiltered = revhEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3EDrFiltered.forEach(default3HDr -> {
				// then remove it from List
				revhEYList.remove(default3HDr);
			});

			view3HDrFiltered.forEach(view3HDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3HDr.getTableSection());
				summaryRespDto.setIndex(2);
				summaryRespDto.setDocType(view3HDr.getDocType());
				summaryRespDto.setEyCount(view3HDr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3HDr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3HDr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3HDr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3HDr.getEyIgst());
				summaryRespDto.setEySgst(view3HDr.getEySgst());
				summaryRespDto.setEyCgst(view3HDr.getEyCgst());
				summaryRespDto.setEyCess(view3HDr.getEyCess());
				summaryRespDto.setGstnCount(view3HDr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3HDr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3HDr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3HDr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3HDr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3HDr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3HDr.getGstnCgst());
				summaryRespDto.setGstnCess(view3HDr.getGstnCess());
				summaryRespDto.setDiffCount(view3HDr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3HDr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3HDr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3HDr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3HDr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3HDr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3HDr.getDiffCgst());
				summaryRespDto.setDiffCess(view3HDr.getDiffCess());
				summaryRespDto.setMemoCount(view3HDr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3HDr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3HDr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3HDr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3HDr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3HDr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3HDr.getMemoCgst());
				summaryRespDto.setMemoCess(view3HDr.getMemoCess());

				revhEYList.add(summaryRespDto);
			});
		}

		Collections.sort(revhEYList,
				new Comparator<Annexure1SummaryResp1Dto>() {
					@Override
					public int compare(Annexure1SummaryResp1Dto respDto1,
							Annexure1SummaryResp1Dto respDto2) {
						return respDto1.getIndex()
								.compareTo(respDto2.getIndex());
					}
				});

		return revhEYList;

	}

}
