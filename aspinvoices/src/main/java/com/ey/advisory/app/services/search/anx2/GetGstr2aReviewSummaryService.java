package com.ey.advisory.app.services.search.anx2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.anx2.GetGstr2aReviewSummaryDao;
import com.ey.advisory.app.docs.dto.Gstr2PRSummarySectionDto;
import com.ey.advisory.app.docs.dto.anx1a.GetGstr2aReviewSummaryFinalRespDto;
import com.ey.advisory.app.docs.dto.anx1a.GetGstr2aReviewSummaryRespDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.google.common.collect.Lists;

@Service("GetGstr2aReviewSummaryService")
public class GetGstr2aReviewSummaryService {

	@Autowired
	@Qualifier("GetGstr2aReviewSummaryDao")
	private GetGstr2aReviewSummaryDao getGstr2aReviewSummaryDao;

	public static final String B2B = "B2B";
	public static final String B2BA = "B2BA";
	public static final String CDN = "CDN";
	public static final String CDNA = "CDNA";
	public static final String ISD = "ISD";
	public static final String ISDA = "ISDA";
	public static final String IMPG = "IMPG";
	public static final String IMPGSEZ = "IMPGSEZ";
	public static final String AMDIMPG = "AMDIMPG";
	public static final String TOTAL = "TOTAL";
	public static final String ECOM = "ECOM";
	public static final String ECOMA = "ECOMA";

	public static final String CR = "CR";
	public static final String DR = "DR";
	public static final String RCR = "RCR";
	public static final String RDR = "RDR";
	public static final String INV = "INV";
	public static final String RNV = "RNV";

	private LinkedList<String> buildTableTypes() {
		LinkedList<String> tableTypes = Lists.newLinkedList();
		tableTypes.add(B2B);
		tableTypes.add(B2BA);
		tableTypes.add(CDN);
		tableTypes.add(CDNA);
		tableTypes.add(ISD);
		tableTypes.add(ISDA);
		tableTypes.add(IMPG);
		tableTypes.add(IMPGSEZ);
		tableTypes.add(AMDIMPG);
		tableTypes.add(ECOM);
		tableTypes.add(ECOMA);
		return tableTypes;
	}

	public GetGstr2aReviewSummaryFinalRespDto loadSummaryDetails(
			Gstr2AProcessedRecordsReqDto request) {
		GetGstr2aReviewSummaryFinalRespDto finalRespDto = new GetGstr2aReviewSummaryFinalRespDto();
		List<GetGstr2aReviewSummaryRespDto> respDtos = getGstr2aReviewSummaryDao
				.loadSummaryDetails(request);
		Map<String, List<GetGstr2aReviewSummaryRespDto>> tablesMap = respDtos
				.stream().collect(Collectors.groupingBy(e -> e.getTable()));

		buildTableTypes().forEach(tableType -> {
			List<GetGstr2aReviewSummaryRespDto> tableList = tablesMap
					.get(tableType);
			convertTableListByTableType(tableList, finalRespDto, tableType,
					request);
		});

		// GetGstr2aReviewSummaryFinalRespDto calcOrderRespDto =
		// GetGstr2aReviewSummaryUtil
		// .calculateTotalByTaxDocType(finalRespDto);
		GetGstr2aReviewSummaryFinalRespDto orderRespDto = orderByTaxDocType(
				finalRespDto);
		return orderRespDto;
	}

	private GetGstr2aReviewSummaryFinalRespDto orderByTaxDocType(
			GetGstr2aReviewSummaryFinalRespDto finalRespDto) {
		GetGstr2aReviewSummaryFinalRespDto orderRespDto = new GetGstr2aReviewSummaryFinalRespDto();
		Map<String, List<String>> taxOrder = buildDocTypesMap();

		List<GetGstr2aReviewSummaryRespDto> b2bDtosList = Lists.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> b2bDtos = finalRespDto.getB2b();
		List<String> b2bSections = taxOrder.get("B2B");
		b2bSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = b2bDtos.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			b2bDtosList.addAll(sectionData);
		});

		List<GetGstr2aReviewSummaryRespDto> b2baDtosList = Lists
				.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> b2baDtos = finalRespDto.getB2ba();
		List<String> b2baSections = taxOrder.get("B2BA");
		b2baSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = b2baDtos.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			b2baDtosList.addAll(sectionData);
		});

		List<GetGstr2aReviewSummaryRespDto> cdnDtosList = Lists.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> cdnDtos = finalRespDto.getCdn();
		List<String> cdnSections = taxOrder.get("CDN");
		cdnSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = cdnDtos.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			cdnDtosList.addAll(sectionData);
		});

		List<GetGstr2aReviewSummaryRespDto> cdnaDtosList = Lists
				.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> cdnaDtos = finalRespDto.getCdna();
		List<String> cdnaSections = taxOrder.get("CDNA");
		cdnaSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = cdnaDtos.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			cdnaDtosList.addAll(sectionData);
		});

		List<GetGstr2aReviewSummaryRespDto> isdDtosList = Lists.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> isdDtos = finalRespDto.getIsd();
		List<String> isdSections = taxOrder.get("ISD");
		isdSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = isdDtos.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			isdDtosList.addAll(sectionData);
		});

		List<GetGstr2aReviewSummaryRespDto> isdaDtosList = Lists
				.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> isdaDtos = finalRespDto.getIsda();
		List<String> isdaSections = taxOrder.get("ISDA");
		isdaSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = isdaDtos.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			isdaDtosList.addAll(sectionData);
		});

		List<GetGstr2aReviewSummaryRespDto> impgDtosList = Lists
				.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> impgDtos = finalRespDto.getImpg();
		List<String> impgSections = taxOrder.get("IMPG");
		impgSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = impgDtos.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			impgDtosList.addAll(sectionData);
		});

		List<GetGstr2aReviewSummaryRespDto> impgSezDtosList = Lists
				.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> impgSezDtos = finalRespDto
				.getImpgsez();
		List<String> impgSezSections = taxOrder.get("IMPGSEZ");
		impgSezSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = impgSezDtos
					.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			impgSezDtosList.addAll(sectionData);
		});
		List<GetGstr2aReviewSummaryRespDto> amdimpgDtosList = Lists
				.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> amdimpgDtos = finalRespDto
				.getAmdImpg();
		List<String> amdimpgSections = taxOrder.get("AMDIMPG");
		amdimpgSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = amdimpgDtos
					.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			amdimpgDtosList.addAll(sectionData);
		});
		
		List<GetGstr2aReviewSummaryRespDto> ecomDtosList = Lists.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> ecomDtos = finalRespDto.getEcom();
		List<String> ecomSections = taxOrder.get("ECOM");
		ecomSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = ecomDtos.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			ecomDtosList.addAll(sectionData);
		});

		List<GetGstr2aReviewSummaryRespDto> ecomaDtosList = Lists
				.newLinkedList();
		List<GetGstr2aReviewSummaryRespDto> ecomaDtos = finalRespDto.getEcoma();
		List<String> ecomaSections = taxOrder.get("ECOMA");
		ecomaSections.forEach(section -> {
			List<GetGstr2aReviewSummaryRespDto> sectionData = ecomaDtos.stream()
					.filter(dto -> section.toLowerCase()
							.equals(dto.getTaxDocType()))
					.collect(Collectors.toList());
			ecomaDtosList.addAll(sectionData);
		});

		List<List<GetGstr2aReviewSummaryRespDto>> gstr2aReviewSummaryRespDtoList = new ArrayList<>();
		gstr2aReviewSummaryRespDtoList.add(b2bDtosList);
		gstr2aReviewSummaryRespDtoList.add(b2baDtosList);
		gstr2aReviewSummaryRespDtoList.add(cdnDtosList);
		gstr2aReviewSummaryRespDtoList.add(cdnaDtosList);
		gstr2aReviewSummaryRespDtoList.add(isdDtosList);
		gstr2aReviewSummaryRespDtoList.add(isdaDtosList);
		gstr2aReviewSummaryRespDtoList.add(impgDtosList);
		gstr2aReviewSummaryRespDtoList.add(impgSezDtosList);
		gstr2aReviewSummaryRespDtoList.add(amdimpgDtosList);
		gstr2aReviewSummaryRespDtoList.add(ecomDtosList);
		gstr2aReviewSummaryRespDtoList.add(ecomaDtosList);

		for (List<GetGstr2aReviewSummaryRespDto> gstr2aReviewSummaryRespDto : gstr2aReviewSummaryRespDtoList) {
			for (GetGstr2aReviewSummaryRespDto doc : gstr2aReviewSummaryRespDto) {
				String docType = doc.getTaxDocType();
				if (docType != null && (docType.equalsIgnoreCase("CR")
						|| docType.equalsIgnoreCase("C")
						|| docType.equalsIgnoreCase("RCR"))) {
					doc.setCess(CheckForNegativeValue(doc.getCess()));
					doc.setCgst(CheckForNegativeValue(doc.getCgst()));
					doc.setIgst(CheckForNegativeValue(doc.getIgst()));
					doc.setInvoiceValue(
							CheckForNegativeValue(doc.getInvoiceValue()));
					doc.setSgst(CheckForNegativeValue(doc.getSgst()));
					;
					doc.setTaxableValue(
							CheckForNegativeValue(doc.getTaxableValue()));
					doc.setTaxPayble(CheckForNegativeValue(doc.getTaxPayble()));
				}
			}
		}

		orderRespDto.setB2b(b2bDtosList);
		orderRespDto.setB2ba(b2baDtosList);
		orderRespDto.setCdn(cdnDtosList);
		orderRespDto.setCdna(cdnaDtosList);
		orderRespDto.setIsd(isdDtosList);
		orderRespDto.setIsda(isdaDtosList);
		orderRespDto.setImpg(impgDtosList);
		orderRespDto.setImpgsez(impgSezDtosList);
		orderRespDto.setAmdImpg(amdimpgDtosList);
		orderRespDto.setEcom(ecomDtosList);
		orderRespDto.setEcoma(ecomaDtosList);
		return orderRespDto;
	}

	private void convertTableListByTableType(
			List<GetGstr2aReviewSummaryRespDto> tableList,
			GetGstr2aReviewSummaryFinalRespDto finalRespDto, String tableType,
			Gstr2AProcessedRecordsReqDto request) {
		List<GetGstr2aReviewSummaryRespDto> finalList = checkDataByTableTypeAndFilterTheNonRequiredDocTypes(
				tableList, tableType, request);
		finalList.forEach(data -> {
			data.setTable(data.getTable().toLowerCase());
			data.setTaxDocType(data.getTaxDocType().toLowerCase());
		});

		switch (tableType) {
		case B2B:
			finalRespDto.setB2b(finalList);
			break;
		case B2BA:
			finalRespDto.setB2ba(finalList);
			break;
		case CDN:
			finalRespDto.setCdn(finalList);
			break;
		case CDNA:
			finalRespDto.setCdna(finalList);
			break;
		case ISD:
			finalRespDto.setIsd(finalList);
			break;
		case ISDA:
			finalRespDto.setIsda(finalList);
			break;
		case IMPG:
			finalRespDto.setImpg(finalList);
			break;
		case IMPGSEZ:
			finalRespDto.setImpgsez(finalList);
			break;
		case AMDIMPG:
			finalRespDto.setAmdImpg(finalList);
			break;
		case ECOM:
			finalRespDto.setEcom(finalList);
			break;
		case ECOMA:
			finalRespDto.setEcoma(finalList);
			break;
		}
	}

	private List<GetGstr2aReviewSummaryRespDto> checkDataByTableTypeAndFilterTheNonRequiredDocTypes(
			List<GetGstr2aReviewSummaryRespDto> tableList, String tableType,
			Gstr2AProcessedRecordsReqDto request) {

		List<GetGstr2aReviewSummaryRespDto> filterdList = Lists.newArrayList();

		if (CollectionUtils.isEmpty(tableList)) {
			createTableTypeWithDefaultData(tableType, filterdList);
		} else {
			List<String> finalDocTypes = buildDocTypesMap().get(tableType);
			List<String> dbDocTypes = tableList.stream()
					.map(GetGstr2aReviewSummaryRespDto::getTaxDocType)
					.collect(Collectors.toList());
			List<String> commonElements = finalDocTypes.stream()
					.filter(docType -> dbDocTypes.contains(docType))
					.collect(Collectors.toList());

			commonElements.forEach(docType -> {
				filterdList.addAll(tableList.stream()
						.filter(data -> data.getTaxDocType().equals(docType)
								&& data.getTable().equals(tableType))
						.collect(Collectors.toList()));
			});
			// missingDocTypes
			List<String> missingDocTypes = Lists.newArrayList();
			int finalDocSize = finalDocTypes.size();
			int dbDocSize = dbDocTypes.size();
			if (finalDocSize > dbDocSize) {
				if (CollectionUtils.isNotEmpty(dbDocTypes)) {
					dbDocTypes
							.forEach(docType -> finalDocTypes.remove(docType));
					missingDocTypes.addAll(finalDocTypes);
				}
			} else {
				dbDocTypes.removeAll(finalDocTypes);
			}
			if (CollectionUtils.isNotEmpty(missingDocTypes)) { // TOTAL, DR
				List<String> reqDocTypes = request.getDocType();
				missingDocTypes.forEach(docType -> {
					if (docType.equals("TOTAL")
							&& !reqDocTypes.contains(docType)
							&& !reqDocTypes.isEmpty()) {
						filterdList.add(
								calculateTotalFromCommonElements(commonElements,
										docType, filterdList, tableType));
					} else {
						filterdList.add(createDefaultSummaryDataObj(tableType,
								docType));
					}
				});
			}
		}
		return filterdList;
	}

	private GetGstr2aReviewSummaryRespDto calculateTotalFromCommonElements(
			List<String> commonElements, String docType,
			List<GetGstr2aReviewSummaryRespDto> filterdList, String tableType) { // CR
		GetGstr2aReviewSummaryRespDto totalDto = new GetGstr2aReviewSummaryRespDto();
		final BigInteger count[] = { BigInteger.ZERO };
		BigDecimal invoiceValue[] = { BigDecimal.ZERO };
		BigDecimal taxableValue[] = { BigDecimal.ZERO };
		BigDecimal taxPayble[] = { BigDecimal.ZERO };
		BigDecimal igst[] = { BigDecimal.ZERO };
		BigDecimal cgst[] = { BigDecimal.ZERO };
		BigDecimal sgst[] = { BigDecimal.ZERO };
		BigDecimal cess[] = { BigDecimal.ZERO };
		if (docType.equals("TOTAL")) {
			commonElements.stream().filter(type -> !type.equals("TOTAL"))
					.forEach(commonDocType -> {
						GetGstr2aReviewSummaryRespDto commonDto = filterdList
								.stream()
								.filter(dto -> dto.getTaxDocType()
										.equals(commonDocType))
								.collect(Collectors.toList()).stream()
								.findFirst().get();
						count[0] = count[0].add(commonDto.getCount());
						invoiceValue[0] = invoiceValue[0]
								.add(commonDto.getInvoiceValue());
						taxableValue[0] = taxableValue[0]
								.add(commonDto.getTaxableValue());
						taxPayble[0] = taxPayble[0]
								.add(commonDto.getTaxPayble());
						igst[0] = igst[0].add(commonDto.getIgst());
						cgst[0] = cgst[0].add(commonDto.getCgst());
						sgst[0] = sgst[0].add(commonDto.getSgst());
						cess[0] = cess[0].add(commonDto.getCess());

					});
		}

		totalDto.setTable(tableType);
		totalDto.setTaxDocType(docType);
		totalDto.setCount(count[0]);
		totalDto.setInvoiceValue(invoiceValue[0]);
		totalDto.setTaxableValue(taxableValue[0]);
		totalDto.setTaxPayble(taxPayble[0]);
		totalDto.setIgst(igst[0]);
		totalDto.setCgst(cgst[0]);
		totalDto.setSgst(sgst[0]);
		totalDto.setCess(cess[0]);

		return totalDto;
	}

	private void createTableTypeWithDefaultData(String tableType,
			List<GetGstr2aReviewSummaryRespDto> filterdList) {
		List<String> docTypes = buildDocTypesMap().get(tableType);
		docTypes.forEach(docType -> {
			filterdList.add(createDefaultSummaryDataObj(tableType, docType));
		});
	}

	public static Map<String, List<String>> buildDocTypesMap() {
		Map<String, List<String>> docTypesMap = new LinkedHashMap<>();
		docTypesMap.put(B2B, Lists.newArrayList(TOTAL, INV));
		docTypesMap.put(B2BA, Lists.newArrayList(TOTAL, RNV));
		docTypesMap.put(CDN, Lists.newArrayList(TOTAL, DR, CR));
		docTypesMap.put(CDNA, Lists.newArrayList(TOTAL, RDR, RCR));
		docTypesMap.put(ISD, Lists.newArrayList(TOTAL, INV, CR));
		docTypesMap.put(ISDA, Lists.newArrayList(TOTAL, RNV, RCR));
		docTypesMap.put(IMPG, Lists.newArrayList(TOTAL, INV));
		docTypesMap.put(IMPGSEZ, Lists.newArrayList(TOTAL, INV));
		docTypesMap.put(AMDIMPG, Lists.newArrayList(TOTAL, RNV));
		docTypesMap.put(ECOM, Lists.newArrayList(TOTAL, INV));
		docTypesMap.put(ECOMA, Lists.newArrayList(TOTAL, RNV));
		return docTypesMap;
	}

	private GetGstr2aReviewSummaryRespDto createDefaultSummaryDataObj(
			String tableType, String docType) {
		GetGstr2aReviewSummaryRespDto dto = new GetGstr2aReviewSummaryRespDto();
		dto.setTable(tableType);
		dto.setTaxDocType(docType);
		dto.setTaxableValue(BigDecimal.ZERO);
		dto.setTaxPayble(BigDecimal.ZERO);
		dto.setCount(BigInteger.ZERO);
		dto.setInvoiceValue(BigDecimal.ZERO);
		dto.setIgst(BigDecimal.ZERO);
		dto.setCgst(BigDecimal.ZERO);
		dto.setSgst(BigDecimal.ZERO);
		dto.setCess(BigDecimal.ZERO);

		return dto;
	}

	public static void main(String[] args) {
		List<String> finalList = Lists.newArrayList("TOTAL", "RDR", "RCR");
		List<String> dbList = Arrays.asList("RCR", "TOTAL");
		System.out.println(finalList);
		System.out.println(dbList);

		List<String> missingDocTypes = Lists.newArrayList();
		if (finalList.size() > dbList.size()) {
			dbList.forEach(docType -> finalList.remove(docType));
			missingDocTypes.addAll(finalList);
		} else {
			dbList.removeAll(finalList);
		}
		System.out.println(missingDocTypes);

	}

	private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));
			}
		}
		return null;
	}
}
