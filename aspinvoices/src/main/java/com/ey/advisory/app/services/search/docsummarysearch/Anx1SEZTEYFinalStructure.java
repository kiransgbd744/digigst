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

@Service("anx1SEZTEYFinalStructure")
public class Anx1SEZTEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getSeztEyList(
			List<Annexure1SummaryResp1Dto> seztEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// For total
		List<Annexure1SummaryResp1Dto> view3ETotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For INV
		List<Annexure1SummaryResp1Dto> view3EInvFiltered = eySummaryListFromView
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> view3ECrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> view3EDrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RNV
		List<Annexure1SummaryResp1Dto> view3ERnvFiltered = eySummaryListFromView
				.stream().filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RDR
		List<Annexure1SummaryResp1Dto> view3ERdrFiltered = eySummaryListFromView
				.stream().filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RCR
		List<Annexure1SummaryResp1Dto> view3ERcrFiltered = eySummaryListFromView
				.stream().filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// If Total filtered list is not null
		if (view3ETotalFiltered != null & view3ETotalFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ETotalFiltered = seztEYList
					.stream()
					.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ETotalFiltered.forEach(default3ETotal -> {
				// then remove it from List
				seztEYList.remove(default3ETotal);
			});

			view3ETotalFiltered.forEach(view3EInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();

				summaryRespDto.setTableSection(view3EInv.getTableSection());
				summaryRespDto.setIndex(0);
				summaryRespDto.setDocType(view3EInv.getDocType());
				summaryRespDto.setEyCount(view3EInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3EInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3EInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3EInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3EInv.getEyIgst());
				summaryRespDto.setEySgst(view3EInv.getEySgst());
				summaryRespDto.setEyCgst(view3EInv.getEyCgst());
				summaryRespDto.setEyCess(view3EInv.getEyCess());
				summaryRespDto.setGstnCount(view3EInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3EInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3EInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3EInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3EInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3EInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3EInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3EInv.getGstnCess());
				summaryRespDto.setDiffCount(view3EInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3EInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3EInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3EInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3EInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3EInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3EInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3EInv.getDiffCess());
				summaryRespDto.setMemoCount(view3EInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3EInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3EInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3EInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3EInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3EInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3EInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3EInv.getMemoCess());

				seztEYList.add(summaryRespDto);
			});
		}

		// If INV filtered list is not null
		if (view3EInvFiltered != null & view3EInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3EInvFiltered = seztEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3EInvFiltered.forEach(default3EInv -> {
				// then remove it from List
				seztEYList.remove(default3EInv);
			});

			view3EInvFiltered.forEach(view3EInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();

				summaryRespDto.setTableSection(view3EInv.getTableSection());
				summaryRespDto.setIndex(1);
				summaryRespDto.setDocType(view3EInv.getDocType());
				summaryRespDto.setEyCount(view3EInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3EInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3EInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3EInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3EInv.getEyIgst());
				summaryRespDto.setEySgst(view3EInv.getEySgst());
				summaryRespDto.setEyCgst(view3EInv.getEyCgst());
				summaryRespDto.setEyCess(view3EInv.getEyCess());
				summaryRespDto.setGstnCount(view3EInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3EInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3EInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3EInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3EInv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3EInv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3EInv.getGstnCgst());
				summaryRespDto.setGstnCess(view3EInv.getGstnCess());
				summaryRespDto.setDiffCount(view3EInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3EInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3EInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3EInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3EInv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3EInv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3EInv.getDiffCgst());
				summaryRespDto.setDiffCess(view3EInv.getDiffCess());
				summaryRespDto.setMemoCount(view3EInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3EInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3EInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3EInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3EInv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3EInv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3EInv.getMemoCgst());
				summaryRespDto.setMemoCess(view3EInv.getMemoCess());

				seztEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (view3ECrFiltered != null & view3ECrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3CCrFiltered = seztEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3CCrFiltered.forEach(default3ECr -> {
				// then remove it from List
				seztEYList.remove(default3ECr);
			});

			// Iterate view list
			view3ECrFiltered.forEach(view3ECr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3ECr.getTableSection());
				summaryRespDto.setIndex(3);
				summaryRespDto.setDocType(view3ECr.getDocType());
				summaryRespDto.setEyCount(view3ECr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3ECr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3ECr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3ECr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3ECr.getEyIgst());
				summaryRespDto.setEySgst(view3ECr.getEySgst());
				summaryRespDto.setEyCgst(view3ECr.getEyCgst());
				summaryRespDto.setEyCess(view3ECr.getEyCess());
				summaryRespDto.setGstnCount(view3ECr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3ECr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3ECr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3ECr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3ECr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3ECr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3ECr.getGstnCgst());
				summaryRespDto.setGstnCess(view3ECr.getGstnCess());
				summaryRespDto.setDiffCount(view3ECr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3ECr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3ECr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3ECr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3ECr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3ECr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3ECr.getDiffCgst());
				summaryRespDto.setDiffCess(view3ECr.getDiffCess());
				summaryRespDto.setMemoCount(view3ECr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3ECr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3ECr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3ECr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3ECr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3ECr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3ECr.getMemoCgst());
				summaryRespDto.setMemoCess(view3ECr.getMemoCess());

				seztEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (view3EDrFiltered != null & view3EDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3EDrFiltered = seztEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3EDrFiltered.forEach(default3EDr -> {
				// then remove it from List
				seztEYList.remove(default3EDr);
			});

			view3EDrFiltered.forEach(view3EDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3EDr.getTableSection());
				summaryRespDto.setIndex(2);
				summaryRespDto.setDocType(view3EDr.getDocType());
				summaryRespDto.setEyCount(view3EDr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3EDr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3EDr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3EDr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3EDr.getEyIgst());
				summaryRespDto.setEySgst(view3EDr.getEySgst());
				summaryRespDto.setEyCgst(view3EDr.getEyCgst());
				summaryRespDto.setEyCess(view3EDr.getEyCess());
				summaryRespDto.setGstnCount(view3EDr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3EDr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3EDr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3EDr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3EDr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3EDr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3EDr.getGstnCgst());
				summaryRespDto.setGstnCess(view3EDr.getGstnCess());
				summaryRespDto.setDiffCount(view3EDr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3EDr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3EDr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3EDr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3EDr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3EDr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3EDr.getDiffCgst());
				summaryRespDto.setDiffCess(view3EDr.getDiffCess());
				summaryRespDto.setMemoCount(view3EDr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3EDr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3EDr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3EDr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3EDr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3EDr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3EDr.getMemoCgst());
				summaryRespDto.setMemoCess(view3EDr.getMemoCess());

				seztEYList.add(summaryRespDto);
			});
		}

		// If RNV filtered list is not null
		if (view3ERnvFiltered != null & view3ERnvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ERnvFiltered = seztEYList
					.stream()
					.filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ERnvFiltered.forEach(default3ERnv -> {
				// then remove it from List
				seztEYList.remove(default3ERnv);
			});

			view3ERnvFiltered.forEach(view3ERnv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3ERnv.getTableSection());
				summaryRespDto.setIndex(4);
				summaryRespDto.setDocType(view3ERnv.getDocType());
				summaryRespDto.setEyCount(view3ERnv.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3ERnv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3ERnv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3ERnv.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3ERnv.getEyIgst());
				summaryRespDto.setEySgst(view3ERnv.getEySgst());
				summaryRespDto.setEyCgst(view3ERnv.getEyCgst());
				summaryRespDto.setEyCess(view3ERnv.getEyCess());
				summaryRespDto.setGstnCount(view3ERnv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3ERnv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3ERnv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3ERnv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3ERnv.getGstnIgst());
				summaryRespDto.setGstnSgst(view3ERnv.getGstnSgst());
				summaryRespDto.setGstnCgst(view3ERnv.getGstnCgst());
				summaryRespDto.setGstnCess(view3ERnv.getGstnCess());
				summaryRespDto.setDiffCount(view3ERnv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3ERnv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3ERnv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3ERnv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3ERnv.getDiffIgst());
				summaryRespDto.setDiffSgst(view3ERnv.getDiffSgst());
				summaryRespDto.setDiffCgst(view3ERnv.getDiffCgst());
				summaryRespDto.setDiffCess(view3ERnv.getDiffCess());
				summaryRespDto.setMemoCount(view3ERnv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3ERnv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3ERnv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3ERnv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3ERnv.getMemoIgst());
				summaryRespDto.setMemoSgst(view3ERnv.getMemoSgst());
				summaryRespDto.setMemoCgst(view3ERnv.getMemoCgst());
				summaryRespDto.setMemoCess(view3ERnv.getMemoCess());

				seztEYList.add(summaryRespDto);
			});
		}

		// If RDR filtered list is not null
		if (view3ERdrFiltered != null & view3ERdrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ERdrFiltered = seztEYList
					.stream()
					.filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ERdrFiltered.forEach(default3ERdr -> {
				// then remove it from List
				seztEYList.remove(default3ERdr);
			});

			view3ERdrFiltered.forEach(view3ERdr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3ERdr.getTableSection());
				summaryRespDto.setIndex(5);
				summaryRespDto.setDocType(view3ERdr.getDocType());
				summaryRespDto.setEyCount(view3ERdr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3ERdr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3ERdr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3ERdr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3ERdr.getEyIgst());
				summaryRespDto.setEySgst(view3ERdr.getEySgst());
				summaryRespDto.setEyCgst(view3ERdr.getEyCgst());
				summaryRespDto.setEyCess(view3ERdr.getEyCess());
				summaryRespDto.setGstnCount(view3ERdr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3ERdr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3ERdr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3ERdr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3ERdr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3ERdr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3ERdr.getGstnCgst());
				summaryRespDto.setGstnCess(view3ERdr.getGstnCess());
				summaryRespDto.setDiffCount(view3ERdr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3ERdr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3ERdr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3ERdr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3ERdr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3ERdr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3ERdr.getDiffCgst());
				summaryRespDto.setDiffCess(view3ERdr.getDiffCess());
				summaryRespDto.setMemoCount(view3ERdr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3ERdr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3ERdr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3ERdr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3ERdr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3ERdr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3ERdr.getMemoCgst());
				summaryRespDto.setMemoCess(view3ERdr.getMemoCess());

				seztEYList.add(summaryRespDto);
			});
		}

		// If RCR filtered list is not null
		if (view3ERcrFiltered != null & view3ERcrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ERcrFiltered = seztEYList
					.stream()
					.filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ERcrFiltered.forEach(default3ERcr -> {
				// then remove it from List
				seztEYList.remove(default3ERcr);
			});

			view3ERdrFiltered.forEach(view3ERcr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3ERcr.getTableSection());
				summaryRespDto.setIndex(6);
				summaryRespDto.setDocType(view3ERcr.getDocType());
				summaryRespDto.setEyCount(view3ERcr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3ERcr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3ERcr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3ERcr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3ERcr.getEyIgst());
				summaryRespDto.setEySgst(view3ERcr.getEySgst());
				summaryRespDto.setEyCgst(view3ERcr.getEyCgst());
				summaryRespDto.setEyCess(view3ERcr.getEyCess());
				summaryRespDto.setGstnCount(view3ERcr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3ERcr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3ERcr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3ERcr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3ERcr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3ERcr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3ERcr.getGstnCgst());
				summaryRespDto.setGstnCess(view3ERcr.getGstnCess());
				summaryRespDto.setDiffCount(view3ERcr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3ERcr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3ERcr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3ERcr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3ERcr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3ERcr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3ERcr.getDiffCgst());
				summaryRespDto.setDiffCess(view3ERcr.getDiffCess());
				summaryRespDto.setMemoCount(view3ERcr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3ERcr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3ERcr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3ERcr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3ERcr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3ERcr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3ERcr.getMemoCgst());
				summaryRespDto.setMemoCess(view3ERcr.getMemoCess());

				seztEYList.add(summaryRespDto);
			});
		}
		Collections.sort(seztEYList,
				new Comparator<Annexure1SummaryResp1Dto>() {
					@Override
					public int compare(Annexure1SummaryResp1Dto respDto1,
							Annexure1SummaryResp1Dto respDto2) {
						return respDto1.getIndex()
								.compareTo(respDto2.getIndex());
					}
				});

		return seztEYList;
	}
}
