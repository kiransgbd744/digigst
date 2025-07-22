package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;

@Service("anx1SEZWTEYFinalStructure")
public class Anx1SEZWTEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getSezwtEyList(
			List<Annexure1SummaryResp1Dto> sezwtEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// For Total
		List<Annexure1SummaryResp1Dto> view3FTotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For INV
		List<Annexure1SummaryResp1Dto> view3FInvFiltered = eySummaryListFromView
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> view3FCrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> view3FDrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RNV
		List<Annexure1SummaryResp1Dto> view3FRnvFiltered = eySummaryListFromView
				.stream().filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RDR
		List<Annexure1SummaryResp1Dto> view3FRdrFiltered = eySummaryListFromView
				.stream().filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RCR
		List<Annexure1SummaryResp1Dto> view3FRcrFiltered = eySummaryListFromView
				.stream().filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// If Total filtered list is not null
		if (view3FTotalFiltered != null & view3FTotalFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3FTotalFiltered = sezwtEYList
					.stream()
					.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3FTotalFiltered.forEach(default3FTotal -> {
				// then remove it from List
				sezwtEYList.remove(default3FTotal);
			});

			view3FTotalFiltered.forEach(view3FInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3FInv.getTableSection());
				summaryRespDto.setIndex(0);
				summaryRespDto.setDocType(view3FInv.getDocType());
				summaryRespDto.setEyCount(view3FInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3FInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3FInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3FInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3FInv.getEyIgst());
				summaryRespDto.setEySgst(view3FInv.getEySgst());
				summaryRespDto.setEyCgst(view3FInv.getEyCgst());
				summaryRespDto.setEyCess(view3FInv.getEyCess());
				summaryRespDto.setGstnCount(view3FInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3FInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3FInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3FInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3FInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3FInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3FInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3FInv.getGstnCess());
				summaryRespDto.setDiffCount(view3FInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3FInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3FInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3FInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3FInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3FInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3FInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3FInv.getDiffCess());
				summaryRespDto.setMemoCount(view3FInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3FInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3FInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3FInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3FInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3FInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3FInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3FInv.getMemoCess());

				sezwtEYList.add(summaryRespDto);
			});
		}

		// If INV filtered list is not null
		if (view3FInvFiltered != null & view3FInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3FInvFiltered = sezwtEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3FInvFiltered.forEach(default3FInv -> {
				// then remove it from List
				sezwtEYList.remove(default3FInv);
			});

			view3FInvFiltered.forEach(view3FInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3FInv.getTableSection());
				summaryRespDto.setIndex(1);
				summaryRespDto.setDocType(view3FInv.getDocType());
				summaryRespDto.setEyCount(view3FInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3FInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3FInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3FInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3FInv.getEyIgst());
				summaryRespDto.setEySgst(view3FInv.getEySgst());
				summaryRespDto.setEyCgst(view3FInv.getEyCgst());
				summaryRespDto.setEyCess(view3FInv.getEyCess());
				summaryRespDto.setGstnCount(view3FInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3FInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3FInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3FInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3FInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3FInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3FInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3FInv.getGstnCess());
				summaryRespDto.setDiffCount(view3FInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3FInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3FInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3FInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3FInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3FInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3FInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3FInv.getDiffCess());
				summaryRespDto.setMemoCount(view3FInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3FInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3FInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3FInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3FInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3FInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3FInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3FInv.getMemoCess());

				sezwtEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (view3FCrFiltered != null & view3FCrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3FCrFiltered = sezwtEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3FCrFiltered.forEach(default3FCr -> {
				// then remove it from List
				sezwtEYList.remove(default3FCr);
			});

			// Iterate view list
			view3FCrFiltered.forEach(view3FCr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3FCr.getTableSection());
				summaryRespDto.setIndex(3);
				summaryRespDto.setDocType(view3FCr.getDocType());
				summaryRespDto.setEyCount(view3FCr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3FCr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3FCr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3FCr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3FCr.getEyIgst());
				summaryRespDto.setEySgst(view3FCr.getEySgst());
				summaryRespDto.setEyCgst(view3FCr.getEyCgst());
				summaryRespDto.setEyCess(view3FCr.getEyCess());
				summaryRespDto.setGstnCount(view3FCr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3FCr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3FCr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3FCr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3FCr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3FCr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3FCr.getGstnCgst());
				summaryRespDto.setGstnCess(view3FCr.getGstnCess());
				summaryRespDto.setDiffCount(view3FCr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3FCr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3FCr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3FCr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3FCr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3FCr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3FCr.getDiffCgst());
				summaryRespDto.setDiffCess(view3FCr.getDiffCess());
				summaryRespDto.setMemoCount(view3FCr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3FCr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3FCr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3FCr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3FCr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3FCr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3FCr.getMemoCgst());
				summaryRespDto.setMemoCess(view3FCr.getMemoCess());

				sezwtEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (view3FDrFiltered != null & view3FDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3EDrFiltered = sezwtEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3EDrFiltered.forEach(default3EDr -> {
				// then remove it from List
				sezwtEYList.remove(default3EDr);
			});

			view3FDrFiltered.forEach(view3FDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3FDr.getTableSection());
				summaryRespDto.setIndex(2);
				summaryRespDto.setDocType(view3FDr.getDocType());
				summaryRespDto.setEyCount(view3FDr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3FDr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3FDr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3FDr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3FDr.getEyIgst());
				summaryRespDto.setEySgst(view3FDr.getEySgst());
				summaryRespDto.setEyCgst(view3FDr.getEyCgst());
				summaryRespDto.setEyCess(view3FDr.getEyCess());
				summaryRespDto.setGstnCount(view3FDr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3FDr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3FDr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3FDr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3FDr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3FDr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3FDr.getGstnCgst());
				summaryRespDto.setGstnCess(view3FDr.getGstnCess());
				summaryRespDto.setDiffCount(view3FDr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3FDr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3FDr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3FDr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3FDr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3FDr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3FDr.getDiffCgst());
				summaryRespDto.setDiffCess(view3FDr.getDiffCess());
				summaryRespDto.setMemoCount(view3FDr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3FDr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3FDr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3FDr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3FDr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3FDr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3FDr.getMemoCgst());
				summaryRespDto.setMemoCess(view3FDr.getMemoCess());

				sezwtEYList.add(summaryRespDto);
			});
		}

		// If RNV filtered list is not null
		if (view3FRnvFiltered != null & view3FRnvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3FRnvFiltered = sezwtEYList
					.stream()
					.filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3FRnvFiltered.forEach(default3FRnv -> {
				// then remove it from List
				sezwtEYList.remove(default3FRnv);
			});

			view3FRnvFiltered.forEach(view3FRnv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3FRnv.getTableSection());
				summaryRespDto.setIndex(4);
				summaryRespDto.setDocType(view3FRnv.getDocType());
				summaryRespDto.setEyCount(view3FRnv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3FRnv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3FRnv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3FRnv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3FRnv.getEyIgst());
				summaryRespDto.setEySgst(view3FRnv.getEySgst());
				summaryRespDto.setEyCgst(view3FRnv.getEyCgst());
				summaryRespDto.setEyCess(view3FRnv.getEyCess());
				summaryRespDto.setGstnCount(view3FRnv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3FRnv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3FRnv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3FRnv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3FRnv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3FRnv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3FRnv.getGstnCgst());
				summaryRespDto.setGstnCess(view3FRnv.getGstnCess());
				summaryRespDto.setDiffCount(view3FRnv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3FRnv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3FRnv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3FRnv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3FRnv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3FRnv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3FRnv.getDiffCgst());
				summaryRespDto.setDiffCess(view3FRnv.getDiffCess());
				summaryRespDto.setMemoCount(view3FRnv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3FRnv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3FRnv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3FRnv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3FRnv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3FRnv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3FRnv.getMemoCgst());
				summaryRespDto.setMemoCess(view3FRnv.getMemoCess());

				sezwtEYList.add(summaryRespDto);
			});
		}

		// If RDR filtered list is not null
		if (view3FRdrFiltered != null & view3FRdrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ERdrFiltered = sezwtEYList
					.stream()
					.filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ERdrFiltered.forEach(default3FRdr -> {
				// then remove it from List
				sezwtEYList.remove(default3FRdr);
			});

			view3FRdrFiltered.forEach(view3FRdr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3FRdr.getTableSection());
				summaryRespDto.setIndex(5);
				summaryRespDto.setDocType(view3FRdr.getDocType());
				summaryRespDto.setEyCount(view3FRdr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3FRdr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3FRdr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3FRdr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3FRdr.getEyIgst());
				summaryRespDto.setEySgst(view3FRdr.getEySgst());
				summaryRespDto.setEyCgst(view3FRdr.getEyCgst());
				summaryRespDto.setEyCess(view3FRdr.getEyCess());
				summaryRespDto.setGstnCount(view3FRdr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3FRdr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3FRdr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3FRdr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3FRdr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3FRdr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3FRdr.getGstnCgst());
				summaryRespDto.setGstnCess(view3FRdr.getGstnCess());
				summaryRespDto.setDiffCount(view3FRdr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3FRdr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3FRdr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3FRdr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3FRdr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3FRdr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3FRdr.getDiffCgst());
				summaryRespDto.setDiffCess(view3FRdr.getDiffCess());
				summaryRespDto.setMemoCount(view3FRdr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3FRdr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3FRdr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3FRdr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3FRdr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3FRdr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3FRdr.getMemoCgst());
				summaryRespDto.setMemoCess(view3FRdr.getMemoCess());

				sezwtEYList.add(summaryRespDto);
			});
		}

		// If RCR filtered list is not null
		if (view3FRcrFiltered != null & view3FRcrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ERcrFiltered = sezwtEYList
					.stream()
					.filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ERcrFiltered.forEach(default3FRcr -> {
				// then remove it from List
				sezwtEYList.remove(default3FRcr);
			});

			view3FRcrFiltered.forEach(view3FRcr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3FRcr.getTableSection());
				summaryRespDto.setIndex(6);
				summaryRespDto.setDocType(view3FRcr.getDocType());
				summaryRespDto.setEyCount(view3FRcr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3FRcr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3FRcr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3FRcr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3FRcr.getEyIgst());
				summaryRespDto.setEySgst(view3FRcr.getEySgst());
				summaryRespDto.setEyCgst(view3FRcr.getEyCgst());
				summaryRespDto.setEyCess(view3FRcr.getEyCess());
				summaryRespDto.setGstnCount(view3FRcr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3FRcr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3FRcr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3FRcr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3FRcr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3FRcr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3FRcr.getGstnCgst());
				summaryRespDto.setGstnCess(view3FRcr.getGstnCess());
				summaryRespDto.setDiffCount(view3FRcr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3FRcr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3FRcr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3FRcr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3FRcr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3FRcr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3FRcr.getDiffCgst());
				summaryRespDto.setDiffCess(view3FRcr.getDiffCess());
				summaryRespDto.setMemoCount(view3FRcr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3FRcr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3FRcr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3FRcr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3FRcr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3FRcr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3FRcr.getMemoCgst());
				summaryRespDto.setMemoCess(view3FRcr.getMemoCess());

				sezwtEYList.add(summaryRespDto);
			});
		}
		Collections.sort(sezwtEYList,
				new Comparator<Annexure1SummaryResp1Dto>() {
					@Override
					public int compare(Annexure1SummaryResp1Dto respDto1,
							Annexure1SummaryResp1Dto respDto2) {
						return respDto1.getIndex()
								.compareTo(respDto2.getIndex());
					}
				});

		return sezwtEYList;
	}

}
