package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;

@Service("anx1DExpEYFinalStructure")
public class Anx1DExpEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getDExpEyList(
			List<Annexure1SummaryResp1Dto> dexpEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// For Total
		List<Annexure1SummaryResp1Dto> view3GTotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For INV
		List<Annexure1SummaryResp1Dto> view3GInvFiltered = eySummaryListFromView
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> view3GCrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> view3GDrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RNV
		List<Annexure1SummaryResp1Dto> view3GRnvFiltered = eySummaryListFromView
				.stream().filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RDR
		List<Annexure1SummaryResp1Dto> view3GRdrFiltered = eySummaryListFromView
				.stream().filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RCR
		List<Annexure1SummaryResp1Dto> view3GRcrFiltered = eySummaryListFromView
				.stream().filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// If Total filtered list is not null
		if (view3GTotalFiltered != null & view3GTotalFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3GTotalFiltered = dexpEYList
					.stream()
					.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3GTotalFiltered.forEach(default3GTotal -> {
				// then remove it from List
				dexpEYList.remove(default3GTotal);
			});

			view3GTotalFiltered.forEach(view3GInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3GInv.getTableSection());
				summaryRespDto.setIndex(0);
				summaryRespDto.setDocType(view3GInv.getDocType());
				summaryRespDto.setEyCount(view3GInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3GInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3GInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3GInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3GInv.getEyIgst());
				summaryRespDto.setEySgst(view3GInv.getEySgst());
				summaryRespDto.setEyCgst(view3GInv.getEyCgst());
				summaryRespDto.setEyCess(view3GInv.getEyCess());
				summaryRespDto.setGstnCount(view3GInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3GInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3GInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3GInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3GInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3GInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3GInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3GInv.getGstnCess());
				summaryRespDto.setDiffCount(view3GInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3GInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3GInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3GInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3GInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3GInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3GInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3GInv.getDiffCess());
				summaryRespDto.setMemoCount(view3GInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3GInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3GInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3GInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3GInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3GInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3GInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3GInv.getMemoCess());

				dexpEYList.add(summaryRespDto);
			});
		}

		// If INV filtered list is not null
		if (view3GInvFiltered != null & view3GInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3GInvFiltered = dexpEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3GInvFiltered.forEach(default3GInv -> {
				// then remove it from List
				dexpEYList.remove(default3GInv);
			});

			view3GInvFiltered.forEach(view3GInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3GInv.getTableSection());
				summaryRespDto.setIndex(1);
				summaryRespDto.setDocType(view3GInv.getDocType());
				summaryRespDto.setEyCount(view3GInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3GInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3GInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3GInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3GInv.getEyIgst());
				summaryRespDto.setEySgst(view3GInv.getEySgst());
				summaryRespDto.setEyCgst(view3GInv.getEyCgst());
				summaryRespDto.setEyCess(view3GInv.getEyCess());
				summaryRespDto.setGstnCount(view3GInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3GInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3GInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3GInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3GInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3GInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3GInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3GInv.getGstnCess());
				summaryRespDto.setDiffCount(view3GInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3GInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3GInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3GInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3GInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3GInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3GInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3GInv.getDiffCess());
				summaryRespDto.setMemoCount(view3GInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3GInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3GInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3GInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3GInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3GInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3GInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3GInv.getMemoCess());

				dexpEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (view3GCrFiltered != null & view3GCrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3FCrFiltered = dexpEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3FCrFiltered.forEach(default3GCr -> {
				// then remove it from List
				dexpEYList.remove(default3GCr);
			});

			// Iterate view list
			view3GCrFiltered.forEach(view3GCr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3GCr.getTableSection());
				summaryRespDto.setIndex(3);
				summaryRespDto.setDocType(view3GCr.getDocType());
				summaryRespDto.setEyCount(view3GCr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3GCr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3GCr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3GCr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3GCr.getEyIgst());
				summaryRespDto.setEySgst(view3GCr.getEySgst());
				summaryRespDto.setEyCgst(view3GCr.getEyCgst());
				summaryRespDto.setEyCess(view3GCr.getEyCess());
				summaryRespDto.setGstnCount(view3GCr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3GCr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3GCr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3GCr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3GCr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3GCr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3GCr.getGstnCgst());
				summaryRespDto.setGstnCess(view3GCr.getGstnCess());
				summaryRespDto.setDiffCount(view3GCr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3GCr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3GCr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3GCr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3GCr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3GCr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3GCr.getDiffCgst());
				summaryRespDto.setDiffCess(view3GCr.getDiffCess());
				summaryRespDto.setMemoCount(view3GCr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3GCr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3GCr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3GCr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3GCr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3GCr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3GCr.getMemoCgst());
				summaryRespDto.setMemoCess(view3GCr.getMemoCess());

				dexpEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (view3GDrFiltered != null & view3GDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3EDrFiltered = dexpEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3EDrFiltered.forEach(default3GDr -> {
				// then remove it from List
				dexpEYList.remove(default3GDr);
			});

			view3GDrFiltered.forEach(view3GDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3GDr.getTableSection());
				summaryRespDto.setIndex(2);
				summaryRespDto.setDocType(view3GDr.getDocType());
				summaryRespDto.setEyCount(view3GDr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3GDr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3GDr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3GDr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3GDr.getEyIgst());
				summaryRespDto.setEySgst(view3GDr.getEySgst());
				summaryRespDto.setEyCgst(view3GDr.getEyCgst());
				summaryRespDto.setEyCess(view3GDr.getEyCess());
				summaryRespDto.setGstnCount(view3GDr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3GDr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3GDr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3GDr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3GDr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3GDr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3GDr.getGstnCgst());
				summaryRespDto.setGstnCess(view3GDr.getGstnCess());
				summaryRespDto.setDiffCount(view3GDr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3GDr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3GDr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3GDr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3GDr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3GDr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3GDr.getDiffCgst());
				summaryRespDto.setDiffCess(view3GDr.getDiffCess());
				summaryRespDto.setMemoCount(view3GDr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3GDr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3GDr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3GDr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3GDr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3GDr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3GDr.getMemoCgst());
				summaryRespDto.setMemoCess(view3GDr.getMemoCess());

				dexpEYList.add(summaryRespDto);
			});
		}

		// If RNV filtered list is not null
		if (view3GRnvFiltered != null & view3GRnvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3FRnvFiltered = dexpEYList
					.stream()
					.filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3FRnvFiltered.forEach(default3GRnv -> {
				// then remove it from List
				dexpEYList.remove(default3GRnv);
			});

			view3GRnvFiltered.forEach(view3GRnv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3GRnv.getTableSection());
				summaryRespDto.setIndex(4);
				summaryRespDto.setDocType(view3GRnv.getDocType());
				summaryRespDto.setEyCount(view3GRnv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3GRnv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3GRnv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3GRnv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3GRnv.getEyIgst());
				summaryRespDto.setEySgst(view3GRnv.getEySgst());
				summaryRespDto.setEyCgst(view3GRnv.getEyCgst());
				summaryRespDto.setEyCess(view3GRnv.getEyCess());
				summaryRespDto.setGstnCount(view3GRnv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3GRnv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3GRnv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3GRnv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3GRnv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3GRnv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3GRnv.getGstnCgst());
				summaryRespDto.setGstnCess(view3GRnv.getGstnCess());
				summaryRespDto.setDiffCount(view3GRnv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3GRnv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3GRnv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3GRnv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3GRnv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3GRnv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3GRnv.getDiffCgst());
				summaryRespDto.setDiffCess(view3GRnv.getDiffCess());
				summaryRespDto.setMemoCount(view3GRnv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3GRnv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3GRnv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3GRnv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3GRnv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3GRnv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3GRnv.getMemoCgst());
				summaryRespDto.setMemoCess(view3GRnv.getMemoCess());

				dexpEYList.add(summaryRespDto);
			});
		}

		// If RDR filtered list is not null
		if (view3GRdrFiltered != null & view3GRdrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ERdrFiltered = dexpEYList
					.stream()
					.filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ERdrFiltered.forEach(default3GRdr -> {
				// then remove it from List
				dexpEYList.remove(default3GRdr);
			});

			view3GRdrFiltered.forEach(view3GRdr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3GRdr.getTableSection());
				summaryRespDto.setIndex(5);
				summaryRespDto.setDocType(view3GRdr.getDocType());
				summaryRespDto.setEyCount(view3GRdr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3GRdr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3GRdr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3GRdr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3GRdr.getEyIgst());
				summaryRespDto.setEySgst(view3GRdr.getEySgst());
				summaryRespDto.setEyCgst(view3GRdr.getEyCgst());
				summaryRespDto.setEyCess(view3GRdr.getEyCess());
				summaryRespDto.setGstnCount(view3GRdr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3GRdr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3GRdr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3GRdr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3GRdr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3GRdr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3GRdr.getGstnCgst());
				summaryRespDto.setGstnCess(view3GRdr.getGstnCess());
				summaryRespDto.setDiffCount(view3GRdr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3GRdr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3GRdr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3GRdr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3GRdr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3GRdr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3GRdr.getDiffCgst());
				summaryRespDto.setDiffCess(view3GRdr.getDiffCess());
				summaryRespDto.setMemoCount(view3GRdr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3GRdr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3GRdr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3GRdr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3GRdr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3GRdr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3GRdr.getMemoCgst());
				summaryRespDto.setMemoCess(view3GRdr.getMemoCess());

				dexpEYList.add(summaryRespDto);
			});
		}

		// If RCR filtered list is not null
		if (view3GRcrFiltered != null & view3GRcrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ERcrFiltered = dexpEYList
					.stream()
					.filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ERcrFiltered.forEach(default3GRcr -> {
				// then remove it from List
				dexpEYList.remove(default3GRcr);
			});

			view3GRcrFiltered.forEach(view3GRcr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3GRcr.getTableSection());
				summaryRespDto.setIndex(6);
				summaryRespDto.setDocType(view3GRcr.getDocType());
				summaryRespDto.setEyCount(view3GRcr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3GRcr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3GRcr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3GRcr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3GRcr.getEyIgst());
				summaryRespDto.setEySgst(view3GRcr.getEySgst());
				summaryRespDto.setEyCgst(view3GRcr.getEyCgst());
				summaryRespDto.setEyCess(view3GRcr.getEyCess());
				summaryRespDto.setGstnCount(view3GRcr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3GRcr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3GRcr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3GRcr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3GRcr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3GRcr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3GRcr.getGstnCgst());
				summaryRespDto.setGstnCess(view3GRcr.getGstnCess());
				summaryRespDto.setDiffCount(view3GRcr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3GRcr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3GRcr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3GRcr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3GRcr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3GRcr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3GRcr.getDiffCgst());
				summaryRespDto.setDiffCess(view3GRcr.getDiffCess());
				summaryRespDto.setMemoCount(view3GRcr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3GRcr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3GRcr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3GRcr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3GRcr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3GRcr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3GRcr.getMemoCgst());
				summaryRespDto.setMemoCess(view3GRcr.getMemoCess());

				dexpEYList.add(summaryRespDto);
			});
		}

		Collections.sort(dexpEYList,
				new Comparator<Annexure1SummaryResp1Dto>() {
					@Override
					public int compare(Annexure1SummaryResp1Dto respDto1,
							Annexure1SummaryResp1Dto respDto2) {
						return respDto1.getIndex()
								.compareTo(respDto2.getIndex());
					}
				});

		return dexpEYList;
	}

}
