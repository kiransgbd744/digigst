package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("anx1B2BEYFinalStructure")
public class Anx1B2BEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getB2BEyList(
			List<Annexure1SummaryResp1Dto> b2bEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// For Total
		List<Annexure1SummaryResp1Dto> viewTotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For INV
		List<Annexure1SummaryResp1Dto> viewInvFiltered = eySummaryListFromView
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> viewCrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> viewDrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RNV
		List<Annexure1SummaryResp1Dto> viewRnvFiltered = eySummaryListFromView
				.stream().filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RDR
		List<Annexure1SummaryResp1Dto> viewRdrFiltered = eySummaryListFromView
				.stream().filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For RCR
		List<Annexure1SummaryResp1Dto> viewRcrFiltered = eySummaryListFromView
				.stream().filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// If Total filtered list is not null
		if (viewTotalFiltered != null & viewTotalFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> defaultTotalFiltered = b2bEYList
					.stream()
					.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultTotalFiltered.forEach(defaultTotal -> {
				// then remove it from List
				b2bEYList.remove(defaultTotal);
			});

			viewTotalFiltered.forEach(viewTotal -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setIndex(0);
				summaryRespDto.setTableSection(viewTotal.getTableSection());
				summaryRespDto.setDocType(viewTotal.getDocType());
				summaryRespDto.setEyCount(viewTotal.getEyCount());
				summaryRespDto.setEyInvoiceValue(viewTotal.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(viewTotal.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(viewTotal.getEyTaxPayble());
				summaryRespDto.setEyIgst(viewTotal.getEyIgst());
				summaryRespDto.setEySgst(viewTotal.getEySgst());
				summaryRespDto.setEyCgst(viewTotal.getEyCgst());
				summaryRespDto.setEyCess(viewTotal.getEyCess());
				summaryRespDto.setGstnCount(viewTotal.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(viewTotal.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(viewTotal.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewTotal.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(viewTotal.getGstnIgst());
				summaryRespDto.setGstnSgst(viewTotal.getGstnSgst());
				summaryRespDto.setGstnCgst(viewTotal.getGstnCgst());
				summaryRespDto.setGstnCess(viewTotal.getGstnCess());
				summaryRespDto.setDiffCount(viewTotal.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(viewTotal.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(viewTotal.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewTotal.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(viewTotal.getDiffIgst());
				summaryRespDto.setDiffSgst(viewTotal.getDiffSgst());
				summaryRespDto.setDiffCgst(viewTotal.getDiffCgst());
				summaryRespDto.setDiffCess(viewTotal.getDiffCess());
				summaryRespDto.setMemoCount(viewTotal.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(viewTotal.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(viewTotal.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(viewTotal.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(viewTotal.getMemoIgst());
				summaryRespDto.setMemoSgst(viewTotal.getMemoSgst());
				summaryRespDto.setMemoCgst(viewTotal.getMemoCgst());
				summaryRespDto.setMemoCess(viewTotal.getMemoCess());

				b2bEYList.add(summaryRespDto);
			});
		}

		// If INV filtered list is not null
		if (viewInvFiltered != null & viewInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> defaultInvFiltered = b2bEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultInvFiltered.forEach(defaultInv -> {
				// then remove it from List
				b2bEYList.remove(defaultInv);
			});

			viewInvFiltered.forEach(viewInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setIndex(1);
				summaryRespDto.setTableSection(viewInv.getTableSection());
				summaryRespDto.setDocType(viewInv.getDocType());
				summaryRespDto.setEyCount(viewInv.getEyCount());
				summaryRespDto.setEyInvoiceValue(viewInv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(viewInv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(viewInv.getEyTaxPayble());
				summaryRespDto.setEyIgst(viewInv.getEyIgst());
				summaryRespDto.setEySgst(viewInv.getEySgst());
				summaryRespDto.setEyCgst(viewInv.getEyCgst());
				summaryRespDto.setEyCess(viewInv.getEyCess());
				summaryRespDto.setGstnCount(viewInv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(viewInv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(viewInv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewInv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(viewInv.getGstnIgst());
				summaryRespDto.setGstnSgst(viewInv.getGstnSgst());
				summaryRespDto.setGstnCgst(viewInv.getGstnCgst());
				summaryRespDto.setGstnCess(viewInv.getGstnCess());
				summaryRespDto.setDiffCount(viewInv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(viewInv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(viewInv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewInv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(viewInv.getDiffIgst());
				summaryRespDto.setDiffSgst(viewInv.getDiffSgst());
				summaryRespDto.setDiffCgst(viewInv.getDiffCgst());
				summaryRespDto.setDiffCess(viewInv.getDiffCess());
				summaryRespDto.setMemoCount(viewInv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(viewInv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(viewInv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(viewInv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(viewInv.getMemoIgst());
				summaryRespDto.setMemoSgst(viewInv.getMemoSgst());
				summaryRespDto.setMemoCgst(viewInv.getMemoCgst());
				summaryRespDto.setMemoCess(viewInv.getMemoCess());

				b2bEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (viewDrFiltered != null & viewDrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> defaultDrFiltered = b2bEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultDrFiltered.forEach(defaultDr -> {
				// then remove it from List
				b2bEYList.remove(defaultDr);
			});

			viewDrFiltered.forEach(viewDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setIndex(2);
				summaryRespDto.setTableSection(viewDr.getTableSection());
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

				b2bEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (viewCrFiltered != null & viewCrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> defaultCrFiltered = b2bEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultCrFiltered.forEach(defaultCr -> {
				// then remove it from List
				b2bEYList.remove(defaultCr);
			});

			// Iterate view list
			viewCrFiltered.forEach(viewCr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setIndex(3);
				summaryRespDto.setTableSection(viewCr.getTableSection());
				summaryRespDto.setDocType(viewCr.getDocType());
				summaryRespDto.setEyCount(viewCr.getEyCount());
				summaryRespDto.setEyInvoiceValue(viewCr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(viewCr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(viewCr.getEyTaxPayble());
				summaryRespDto.setEyIgst(viewCr.getEyIgst());
				summaryRespDto.setEySgst(viewCr.getEySgst());
				summaryRespDto.setEyCgst(viewCr.getEyCgst());
				summaryRespDto.setEyCess(viewCr.getEyCess());
				summaryRespDto.setGstnCount(viewCr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(viewCr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(viewCr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewCr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(viewCr.getGstnIgst());
				summaryRespDto.setGstnSgst(viewCr.getGstnSgst());
				summaryRespDto.setGstnCgst(viewCr.getGstnCgst());
				summaryRespDto.setGstnCess(viewCr.getGstnCess());
				summaryRespDto.setDiffCount(viewCr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(viewCr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(viewCr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewCr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(viewCr.getDiffIgst());
				summaryRespDto.setDiffSgst(viewCr.getDiffSgst());
				summaryRespDto.setDiffCgst(viewCr.getDiffCgst());
				summaryRespDto.setDiffCess(viewCr.getDiffCess());
				summaryRespDto.setMemoCount(viewCr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(viewCr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(viewCr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(viewCr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(viewCr.getMemoIgst());
				summaryRespDto.setMemoSgst(viewCr.getMemoSgst());
				summaryRespDto.setMemoCgst(viewCr.getMemoCgst());
				summaryRespDto.setMemoCess(viewCr.getMemoCess());

				b2bEYList.add(summaryRespDto);
			});
		}

		// If RNV filtered list is not null
		if (viewRnvFiltered != null & viewRnvFiltered.size() > 0) {
			// then filter default List for RNV
			List<Annexure1SummaryResp1Dto> defaultRnvFiltered = b2bEYList
					.stream()
					.filter(p -> "RNV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultRnvFiltered.forEach(defaultRnv -> {
				// then remove it from List
				b2bEYList.remove(defaultRnv);
			});

			viewRnvFiltered.forEach(viewRnv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setIndex(4);
				summaryRespDto.setTableSection(viewRnv.getTableSection());
				summaryRespDto.setDocType(viewRnv.getDocType());
				summaryRespDto.setEyCount(viewRnv.getEyCount());
				summaryRespDto.setEyInvoiceValue(viewRnv.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(viewRnv.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(viewRnv.getEyTaxPayble());
				summaryRespDto.setEyIgst(viewRnv.getEyIgst());
				summaryRespDto.setEySgst(viewRnv.getEySgst());
				summaryRespDto.setEyCgst(viewRnv.getEyCgst());
				summaryRespDto.setEyCess(viewRnv.getEyCess());
				summaryRespDto.setGstnCount(viewRnv.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(viewRnv.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(viewRnv.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewRnv.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(viewRnv.getGstnIgst());
				summaryRespDto.setGstnSgst(viewRnv.getGstnSgst());
				summaryRespDto.setGstnCgst(viewRnv.getGstnCgst());
				summaryRespDto.setGstnCess(viewRnv.getGstnCess());
				summaryRespDto.setDiffCount(viewRnv.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(viewRnv.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(viewRnv.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewRnv.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(viewRnv.getDiffIgst());
				summaryRespDto.setDiffSgst(viewRnv.getDiffSgst());
				summaryRespDto.setDiffCgst(viewRnv.getDiffCgst());
				summaryRespDto.setDiffCess(viewRnv.getDiffCess());
				summaryRespDto.setMemoCount(viewRnv.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(viewRnv.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(viewRnv.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(viewRnv.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(viewRnv.getMemoIgst());
				summaryRespDto.setMemoSgst(viewRnv.getMemoSgst());
				summaryRespDto.setMemoCgst(viewRnv.getMemoCgst());
				summaryRespDto.setMemoCess(viewRnv.getMemoCess());

				b2bEYList.add(summaryRespDto);
			});
		}

		// If RDR filtered list is not null
		if (viewRdrFiltered != null & viewRdrFiltered.size() > 0) {
			// then filter default List for RNV
			List<Annexure1SummaryResp1Dto> defaultRdrFiltered = b2bEYList
					.stream()
					.filter(p -> "RDR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultRdrFiltered.forEach(defaultRdr -> {
				// then remove it from List
				b2bEYList.remove(defaultRdr);
			});

			viewRdrFiltered.forEach(viewRdr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setIndex(5);
				summaryRespDto.setTableSection(viewRdr.getTableSection());
				summaryRespDto.setDocType(viewRdr.getDocType());
				summaryRespDto.setEyCount(viewRdr.getEyCount());
				summaryRespDto.setEyInvoiceValue(viewRdr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(viewRdr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(viewRdr.getEyTaxPayble());
				summaryRespDto.setEyIgst(viewRdr.getEyIgst());
				summaryRespDto.setEySgst(viewRdr.getEySgst());
				summaryRespDto.setEyCgst(viewRdr.getEyCgst());
				summaryRespDto.setEyCess(viewRdr.getEyCess());
				summaryRespDto.setGstnCount(viewRdr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(viewRdr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(viewRdr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewRdr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(viewRdr.getGstnIgst());
				summaryRespDto.setGstnSgst(viewRdr.getGstnSgst());
				summaryRespDto.setGstnCgst(viewRdr.getGstnCgst());
				summaryRespDto.setGstnCess(viewRdr.getGstnCess());
				summaryRespDto.setDiffCount(viewRdr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(viewRdr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(viewRdr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewRdr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(viewRdr.getDiffIgst());
				summaryRespDto.setDiffSgst(viewRdr.getDiffSgst());
				summaryRespDto.setDiffCgst(viewRdr.getDiffCgst());
				summaryRespDto.setDiffCess(viewRdr.getDiffCess());
				summaryRespDto.setMemoCount(viewRdr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(viewRdr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(viewRdr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(viewRdr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(viewRdr.getMemoIgst());
				summaryRespDto.setMemoSgst(viewRdr.getMemoSgst());
				summaryRespDto.setMemoCgst(viewRdr.getMemoCgst());
				summaryRespDto.setMemoCess(viewRdr.getMemoCess());

				b2bEYList.add(summaryRespDto);
			});
		}

		// If RCR filtered list is not null
		if (viewRcrFiltered != null & viewRcrFiltered.size() > 0) {
			// then filter default List for RNV
			List<Annexure1SummaryResp1Dto> defaultRcrFiltered = b2bEYList
					.stream()
					.filter(p -> "RCR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultRcrFiltered.forEach(defaultRcr -> {
				// then remove it from List
				b2bEYList.remove(defaultRcr);
			});

			viewRcrFiltered.forEach(viewRcr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setIndex(6);
				summaryRespDto.setTableSection(viewRcr.getTableSection());
				summaryRespDto.setDocType(viewRcr.getDocType());
				summaryRespDto.setEyCount(viewRcr.getEyCount());
				summaryRespDto.setEyInvoiceValue(viewRcr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(viewRcr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(viewRcr.getEyTaxPayble());
				summaryRespDto.setEyIgst(viewRcr.getEyIgst());
				summaryRespDto.setEySgst(viewRcr.getEySgst());
				summaryRespDto.setEyCgst(viewRcr.getEyCgst());
				summaryRespDto.setEyCess(viewRcr.getEyCess());
				summaryRespDto.setGstnCount(viewRcr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(viewRcr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(viewRcr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewRcr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(viewRcr.getGstnIgst());
				summaryRespDto.setGstnSgst(viewRcr.getGstnSgst());
				summaryRespDto.setGstnCgst(viewRcr.getGstnCgst());
				summaryRespDto.setGstnCess(viewRcr.getGstnCess());
				summaryRespDto.setDiffCount(viewRcr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(viewRcr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(viewRcr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewRcr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(viewRcr.getDiffIgst());
				summaryRespDto.setDiffSgst(viewRcr.getDiffSgst());
				summaryRespDto.setDiffCgst(viewRcr.getDiffCgst());
				summaryRespDto.setDiffCess(viewRcr.getDiffCess());
				summaryRespDto.setMemoCount(viewRcr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(viewRcr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(viewRcr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(viewRcr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(viewRcr.getMemoIgst());
				summaryRespDto.setMemoSgst(viewRcr.getMemoSgst());
				summaryRespDto.setMemoCgst(viewRcr.getMemoCgst());
				summaryRespDto.setMemoCess(viewRcr.getMemoCess());

				b2bEYList.add(summaryRespDto);
			});
		}
		Collections.sort(b2bEYList, new Comparator<Annexure1SummaryResp1Dto>() {
			@Override
			public int compare(Annexure1SummaryResp1Dto respDto1,
					Annexure1SummaryResp1Dto respDto2) {
				return respDto1.getIndex().compareTo(respDto2.getIndex());
			}
		});

		return b2bEYList;
	}

}
