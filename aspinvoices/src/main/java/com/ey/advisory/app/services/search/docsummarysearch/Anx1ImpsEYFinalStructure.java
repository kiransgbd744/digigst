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
@Service("anx1ImpsEYFinalStructure")
public class Anx1ImpsEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getImpsEyList(
			List<Annexure1SummaryResp1Dto> impsIEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// total 
		List<Annexure1SummaryResp1Dto> view3KTotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());
		
		// SLF
		List<Annexure1SummaryResp1Dto> view3KSLFFiltered = eySummaryListFromView
				.stream().filter(p -> "SLF".equalsIgnoreCase(p.getDocType()))
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
					List<Annexure1SummaryResp1Dto> default3ITotalFiltered = impsIEYList
							.stream()
							.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
							.collect(Collectors.toList());

					// If the default filtered list is not null
					default3ITotalFiltered.forEach(default3ISLF -> {
						// then remove it from List
						impsIEYList.remove(default3ISLF);
					});

					view3KTotalFiltered.forEach(view3ISLF -> {
						Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
						summaryRespDto.setTableSection(view3ISLF.getTableSection());
						summaryRespDto.setIndex(0);
						summaryRespDto.setDocType(view3ISLF.getDocType());
						summaryRespDto.setEyCount(view3ISLF.getEyCount());
						summaryRespDto.setEyInvoiceValue(view3ISLF.getEyInvoiceValue());
						summaryRespDto.setEyTaxableValue(view3ISLF.getEyTaxableValue());
						summaryRespDto.setEyTaxPayble(view3ISLF.getEyTaxPayble());
						summaryRespDto.setEyIgst(view3ISLF.getEyIgst());
						summaryRespDto.setEySgst(view3ISLF.getEySgst());
						summaryRespDto.setEyCgst(view3ISLF.getEyCgst());
						summaryRespDto.setEyCess(view3ISLF.getEyCess());
						summaryRespDto.setGstnCount(view3ISLF.getGstnCount());
						summaryRespDto
								.setGstnInvoiceValue(view3ISLF.getGstnInvoiceValue());
						summaryRespDto
								.setGstnTaxableValue(view3ISLF.getGstnTaxableValue());
						summaryRespDto.setGstnTaxPayble(view3ISLF.getGstnTaxPayble());
						summaryRespDto.setGstnIgst(view3ISLF.getGstnIgst());
						summaryRespDto.setGstnSgst(view3ISLF.getGstnSgst());
						summaryRespDto.setGstnCgst(view3ISLF.getGstnCgst());
						summaryRespDto.setGstnCess(view3ISLF.getGstnCess());
						summaryRespDto.setDiffCount(view3ISLF.getDiffCount());
						summaryRespDto
								.setDiffInvoiceValue(view3ISLF.getDiffInvoiceValue());
						summaryRespDto
								.setDiffTaxableValue(view3ISLF.getDiffTaxableValue());
						summaryRespDto.setDiffTaxPayble(view3ISLF.getDiffTaxPayble());
						summaryRespDto.setDiffIgst(view3ISLF.getDiffIgst());
						summaryRespDto.setDiffSgst(view3ISLF.getDiffSgst());
						summaryRespDto.setDiffCgst(view3ISLF.getDiffCgst());
						summaryRespDto.setDiffCess(view3ISLF.getDiffCess());
						summaryRespDto.setMemoCount(view3ISLF.getMemoCount());
						summaryRespDto
								.setMemoInvoiceValue(view3ISLF.getMemoInvoiceValue());
						summaryRespDto
								.setMemoTaxableValue(view3ISLF.getMemoTaxableValue());
						summaryRespDto.setMemoTaxPayble(view3ISLF.getMemoTaxPayble());
						summaryRespDto.setMemoIgst(view3ISLF.getMemoIgst());
						summaryRespDto.setMemoSgst(view3ISLF.getMemoSgst());
						summaryRespDto.setMemoCgst(view3ISLF.getMemoCgst());
						summaryRespDto.setMemoCess(view3ISLF.getMemoCess());

						impsIEYList.add(summaryRespDto);
					});
				}
		
		// If INV filtered list is not null
		if (view3KSLFFiltered != null & view3KSLFFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ISLFFiltered = impsIEYList
					.stream()
					.filter(p -> "SLF".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ISLFFiltered.forEach(default3ISLF -> {
				// then remove it from List
				impsIEYList.remove(default3ISLF);
			});

			view3KSLFFiltered.forEach(view3ISLF -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3ISLF.getTableSection());
				summaryRespDto.setIndex(1);
				summaryRespDto.setDocType(view3ISLF.getDocType());
				summaryRespDto.setEyCount(view3ISLF.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3ISLF.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3ISLF.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3ISLF.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3ISLF.getEyIgst());
				summaryRespDto.setEySgst(view3ISLF.getEySgst());
				summaryRespDto.setEyCgst(view3ISLF.getEyCgst());
				summaryRespDto.setEyCess(view3ISLF.getEyCess());
				summaryRespDto.setGstnCount(view3ISLF.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3ISLF.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3ISLF.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3ISLF.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3ISLF.getGstnIgst());
				summaryRespDto.setGstnSgst(view3ISLF.getGstnSgst());
				summaryRespDto.setGstnCgst(view3ISLF.getGstnCgst());
				summaryRespDto.setGstnCess(view3ISLF.getGstnCess());
				summaryRespDto.setDiffCount(view3ISLF.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3ISLF.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3ISLF.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3ISLF.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3ISLF.getDiffIgst());
				summaryRespDto.setDiffSgst(view3ISLF.getDiffSgst());
				summaryRespDto.setDiffCgst(view3ISLF.getDiffCgst());
				summaryRespDto.setDiffCess(view3ISLF.getDiffCess());
				summaryRespDto.setMemoCount(view3ISLF.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3ISLF.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3ISLF.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3ISLF.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3ISLF.getMemoIgst());
				summaryRespDto.setMemoSgst(view3ISLF.getMemoSgst());
				summaryRespDto.setMemoCgst(view3ISLF.getMemoCgst());
				summaryRespDto.setMemoCess(view3ISLF.getMemoCess());

				impsIEYList.add(summaryRespDto);
			});
		}
		// If Cr filtered list is not null
				if (view3KCrFiltered != null & view3KCrFiltered.size() > 0) {
					// then filter default List for 4A
					List<Annexure1SummaryResp1Dto> default3ICRFiltered = impsIEYList
							.stream()
							.filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
							.collect(Collectors.toList());

					// If the default filtered list is not null
					default3ICRFiltered.forEach(default3ICR -> {
						// then remove it from List
						impsIEYList.remove(default3ICR);
					});

					view3KCrFiltered.forEach(view3ICR -> {
						Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
						summaryRespDto.setTableSection(view3ICR.getTableSection());
						summaryRespDto.setIndex(3);
						summaryRespDto.setDocType(view3ICR.getDocType());
						summaryRespDto.setEyCount(view3ICR.getEyCount());
						summaryRespDto.setEyInvoiceValue(view3ICR.getEyInvoiceValue());
						summaryRespDto.setEyTaxableValue(view3ICR.getEyTaxableValue());
						summaryRespDto.setEyTaxPayble(view3ICR.getEyTaxPayble());
						summaryRespDto.setEyIgst(view3ICR.getEyIgst());
						summaryRespDto.setEySgst(view3ICR.getEySgst());
						summaryRespDto.setEyCgst(view3ICR.getEyCgst());
						summaryRespDto.setEyCess(view3ICR.getEyCess());
						summaryRespDto.setGstnCount(view3ICR.getGstnCount());
						summaryRespDto
								.setGstnInvoiceValue(view3ICR.getGstnInvoiceValue());
						summaryRespDto
								.setGstnTaxableValue(view3ICR.getGstnTaxableValue());
						summaryRespDto.setGstnTaxPayble(view3ICR.getGstnTaxPayble());
						summaryRespDto.setGstnIgst(view3ICR.getGstnIgst());
						summaryRespDto.setGstnSgst(view3ICR.getGstnSgst());
						summaryRespDto.setGstnCgst(view3ICR.getGstnCgst());
						summaryRespDto.setGstnCess(view3ICR.getGstnCess());
						summaryRespDto.setDiffCount(view3ICR.getDiffCount());
						summaryRespDto
								.setDiffInvoiceValue(view3ICR.getDiffInvoiceValue());
						summaryRespDto
								.setDiffTaxableValue(view3ICR.getDiffTaxableValue());
						summaryRespDto.setDiffTaxPayble(view3ICR.getDiffTaxPayble());
						summaryRespDto.setDiffIgst(view3ICR.getDiffIgst());
						summaryRespDto.setDiffSgst(view3ICR.getDiffSgst());
						summaryRespDto.setDiffCgst(view3ICR.getDiffCgst());
						summaryRespDto.setDiffCess(view3ICR.getDiffCess());
						summaryRespDto.setMemoCount(view3ICR.getMemoCount());
						summaryRespDto
								.setMemoInvoiceValue(view3ICR.getMemoInvoiceValue());
						summaryRespDto
								.setMemoTaxableValue(view3ICR.getMemoTaxableValue());
						summaryRespDto.setMemoTaxPayble(view3ICR.getMemoTaxPayble());
						summaryRespDto.setMemoIgst(view3ICR.getMemoIgst());
						summaryRespDto.setMemoSgst(view3ICR.getMemoSgst());
						summaryRespDto.setMemoCgst(view3ICR.getMemoCgst());
						summaryRespDto.setMemoCess(view3ICR.getMemoCess());

						impsIEYList.add(summaryRespDto);
					});
				}
				// If DR filtered list is not null
				if (view3KDrFiltered != null & view3KDrFiltered.size() > 0) {
					// then filter default List for 4A
					List<Annexure1SummaryResp1Dto> default3IDRFiltered = impsIEYList
							.stream()
							.filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
							.collect(Collectors.toList());

					// If the default filtered list is not null
					default3IDRFiltered.forEach(default3IDR -> {
						// then remove it from List
						impsIEYList.remove(default3IDR);
					});

					view3KDrFiltered.forEach(view3IDR -> {
						Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
						summaryRespDto.setTableSection(view3IDR.getTableSection());
						summaryRespDto.setIndex(2);
						summaryRespDto.setDocType(view3IDR.getDocType());
						summaryRespDto.setEyCount(view3IDR.getEyCount());
						summaryRespDto.setEyInvoiceValue(view3IDR.getEyInvoiceValue());
						summaryRespDto.setEyTaxableValue(view3IDR.getEyTaxableValue());
						summaryRespDto.setEyTaxPayble(view3IDR.getEyTaxPayble());
						summaryRespDto.setEyIgst(view3IDR.getEyIgst());
						summaryRespDto.setEySgst(view3IDR.getEySgst());
						summaryRespDto.setEyCgst(view3IDR.getEyCgst());
						summaryRespDto.setEyCess(view3IDR.getEyCess());
						summaryRespDto.setGstnCount(view3IDR.getGstnCount());
						summaryRespDto
								.setGstnInvoiceValue(view3IDR.getGstnInvoiceValue());
						summaryRespDto
								.setGstnTaxableValue(view3IDR.getGstnTaxableValue());
						summaryRespDto.setGstnTaxPayble(view3IDR.getGstnTaxPayble());
						summaryRespDto.setGstnIgst(view3IDR.getGstnIgst());
						summaryRespDto.setGstnSgst(view3IDR.getGstnSgst());
						summaryRespDto.setGstnCgst(view3IDR.getGstnCgst());
						summaryRespDto.setGstnCess(view3IDR.getGstnCess());
						summaryRespDto.setDiffCount(view3IDR.getDiffCount());
						summaryRespDto
								.setDiffInvoiceValue(view3IDR.getDiffInvoiceValue());
						summaryRespDto
								.setDiffTaxableValue(view3IDR.getDiffTaxableValue());
						summaryRespDto.setDiffTaxPayble(view3IDR.getDiffTaxPayble());
						summaryRespDto.setDiffIgst(view3IDR.getDiffIgst());
						summaryRespDto.setDiffSgst(view3IDR.getDiffSgst());
						summaryRespDto.setDiffCgst(view3IDR.getDiffCgst());
						summaryRespDto.setDiffCess(view3IDR.getDiffCess());
						summaryRespDto.setMemoCount(view3IDR.getMemoCount());
						summaryRespDto
								.setMemoInvoiceValue(view3IDR.getMemoInvoiceValue());
						summaryRespDto
								.setMemoTaxableValue(view3IDR.getMemoTaxableValue());
						summaryRespDto.setMemoTaxPayble(view3IDR.getMemoTaxPayble());
						summaryRespDto.setMemoIgst(view3IDR.getMemoIgst());
						summaryRespDto.setMemoSgst(view3IDR.getMemoSgst());
						summaryRespDto.setMemoCgst(view3IDR.getMemoCgst());
						summaryRespDto.setMemoCess(view3IDR.getMemoCess());

						impsIEYList.add(summaryRespDto);
					});
				}
				Collections.sort(impsIEYList, new Comparator<Annexure1SummaryResp1Dto>() {
					@Override
					public int compare(Annexure1SummaryResp1Dto respDto1,
							Annexure1SummaryResp1Dto respDto2) {
						return respDto1.getIndex()
								.compareTo(respDto2.getIndex());
					}
				});

		
		return impsIEYList;

	}

}
