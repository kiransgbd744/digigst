package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
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

@Service("anx1B2CEYFinalStructure")
public class Anx1B2CEYFinalStructure {

	public List<Annexure1SummaryResp1Dto> getB2CEyList(
			List<Annexure1SummaryResp1Dto> b2cEYList,
			List<Annexure1SummaryResp1Dto> eySummaryListFromView) {

		// total
		List<Annexure1SummaryResp1Dto> view3ATotalFiltered = eySummaryListFromView
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// Inv

		List<Annexure1SummaryResp1Dto> view3AInvFiltered = eySummaryListFromView
				.stream().filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For CR
		List<Annexure1SummaryResp1Dto> view3ACrFiltered = eySummaryListFromView
				.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// For DR
		List<Annexure1SummaryResp1Dto> view3ADrFiltered = eySummaryListFromView
				.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		// total

		if (view3ATotalFiltered != null & view3ATotalFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3AtotalFiltered = b2cEYList
					.stream()
					.filter(p -> "total".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3AtotalFiltered.forEach(defaulttotal -> {
				// then remove it from List
				b2cEYList.remove(defaulttotal);
			});

			view3ATotalFiltered.forEach(viewTotal -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(viewTotal.getTableSection());
				summaryRespDto.setIndex(0);
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

				b2cEYList.add(summaryRespDto);
			});
		}

		// If INV filtered list is not null
		if (view3AInvFiltered != null & view3AInvFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3AInvFiltered = b2cEYList
					.stream()
					.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3AInvFiltered.forEach(defaultInv -> {
				// then remove it from List
				b2cEYList.remove(defaultInv);
			});

			view3AInvFiltered.forEach(viewInv -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(viewInv.getTableSection());
				summaryRespDto.setIndex(1);
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

				b2cEYList.add(summaryRespDto);
			});
		}

		// If CR filtered list is not null
		if (view3ACrFiltered != null & view3ACrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> default3ACrFiltered = b2cEYList
					.stream().filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3ACrFiltered.forEach(default3ACr -> {
				// then remove it from List
				b2cEYList.remove(default3ACr);
			});

			// Iterate view list
			view3ACrFiltered.forEach(view3ACr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(view3ACr.getTableSection());
				summaryRespDto.setDocType(view3ACr.getDocType());
				summaryRespDto.setIndex(3);
				summaryRespDto.setEyCount(view3ACr.getEyCount());
				summaryRespDto.setEyInvoiceValue(view3ACr.getEyInvoiceValue());
				summaryRespDto.setEyTaxableValue(view3ACr.getEyTaxableValue());
				summaryRespDto.setEyTaxPayble(view3ACr.getEyTaxPayble());
				summaryRespDto.setEyIgst(view3ACr.getEyIgst());
				summaryRespDto.setEySgst(view3ACr.getEySgst());
				summaryRespDto.setEyCgst(view3ACr.getEyCgst());
				summaryRespDto.setEyCess(view3ACr.getEyCess());
				summaryRespDto.setGstnCount(view3ACr.getGstnCount());
				summaryRespDto
						.setGstnInvoiceValue(view3ACr.getGstnInvoiceValue());
				summaryRespDto
						.setGstnTaxableValue(view3ACr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(view3ACr.getGstnTaxPayble());
				summaryRespDto.setGstnIgst(view3ACr.getGstnIgst());
				summaryRespDto.setGstnSgst(view3ACr.getGstnSgst());
				summaryRespDto.setGstnCgst(view3ACr.getGstnCgst());
				summaryRespDto.setGstnCess(view3ACr.getGstnCess());
				summaryRespDto.setDiffCount(view3ACr.getDiffCount());
				summaryRespDto
						.setDiffInvoiceValue(view3ACr.getDiffInvoiceValue());
				summaryRespDto
						.setDiffTaxableValue(view3ACr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(view3ACr.getDiffTaxPayble());
				summaryRespDto.setDiffIgst(view3ACr.getDiffIgst());
				summaryRespDto.setDiffSgst(view3ACr.getDiffSgst());
				summaryRespDto.setDiffCgst(view3ACr.getDiffCgst());
				summaryRespDto.setDiffCess(view3ACr.getDiffCess());
				summaryRespDto.setMemoCount(view3ACr.getMemoCount());
				summaryRespDto
						.setMemoInvoiceValue(view3ACr.getMemoInvoiceValue());
				summaryRespDto
						.setMemoTaxableValue(view3ACr.getMemoTaxableValue());
				summaryRespDto.setMemoTaxPayble(view3ACr.getMemoTaxPayble());
				summaryRespDto.setMemoIgst(view3ACr.getMemoIgst());
				summaryRespDto.setMemoSgst(view3ACr.getMemoSgst());
				summaryRespDto.setMemoCgst(view3ACr.getMemoCgst());
				summaryRespDto.setMemoCess(view3ACr.getMemoCess());

				b2cEYList.add(summaryRespDto);
			});
		}

		// If DR filtered list is not null
		if (view3ADrFiltered != null & view3ADrFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryResp1Dto> defaultDrFiltered = b2cEYList
					.stream().filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultDrFiltered.forEach(defaultDr -> {
				// then remove it from List
				b2cEYList.remove(defaultDr);
			});

			view3ADrFiltered.forEach(viewDr -> {
				Annexure1SummaryResp1Dto summaryRespDto = new Annexure1SummaryResp1Dto();
				summaryRespDto.setTableSection(viewDr.getTableSection());
				summaryRespDto.setDocType(viewDr.getDocType());
				summaryRespDto.setIndex(2);
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

				b2cEYList.add(summaryRespDto);
			});
		}

		Collections.sort(b2cEYList, new Comparator<Annexure1SummaryResp1Dto>() {
			@Override
			public int compare(Annexure1SummaryResp1Dto respDto1,
					Annexure1SummaryResp1Dto respDto2) {
				return respDto1.getIndex().compareTo(respDto2.getIndex());
			}
		});

		return b2cEYList;

	}

}
