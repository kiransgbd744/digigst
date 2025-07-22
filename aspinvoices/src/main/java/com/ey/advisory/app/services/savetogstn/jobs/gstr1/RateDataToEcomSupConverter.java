package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants;
import com.ey.advisory.app.docs.dto.B2BEcomInvoices;
import com.ey.advisory.app.docs.dto.B2BInvoiceData;
import com.ey.advisory.app.docs.dto.B2bLineItem;
import com.ey.advisory.app.docs.dto.B2bLineItemDetail;
import com.ey.advisory.app.docs.dto.Ecom;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service("RateDataToEcomSupConverter")
public class RateDataToEcomSupConverter implements RateDataToGstr1Converter {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	private static final List<String> REGYPE_IMPORTS = ImmutableList
			.of(GSTConstants.SEZD, GSTConstants.SEZU);

	@Override
	public SaveBatchProcessDto convertToGstr1Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType, int chunkSize) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAV_GST1_JSON_FORMATION_START,
				PerfamanceEventConstants.RateDataToB2bB2baConverter,
				PerfamanceEventConstants.convertToGstr1Object, null);

		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		Ecom ecomObj = new Ecom();
		List<Long> idsList = new ArrayList<>();
		List<B2BEcomInvoices> b2bList = new ArrayList<>();
		List<B2BEcomInvoices> urp2bList = new ArrayList<>();

		try {
			String supplierGstin = objects.get(0)[0] != null
					? String.valueOf(objects.get(0)[0]) : null;
			String taxPeriod = objects.get(0)[1] != null
					? String.valueOf(objects.get(0)[1]) : null;
			boolean isSEZSupplier = isSEZSupplier(groupCode, supplierGstin);
			List<EcomSectionDto> sectionDtoList = convertArrtoDto(objects);
			Map<String, Map<String, Map<Long, List<EcomSectionDto>>>> groupedByTableSection = sectionDtoList
					.stream()
					.collect(Collectors.groupingBy(
							EcomSectionDto::getTableSection,
							Collectors.groupingBy(
									obj -> combineKey(obj.getTableSection(),
											obj.getEcomTin(), obj.getCgstin()),
									Collectors.groupingBy(
											obj -> obj.getDocId()))));

			if (groupedByTableSection.containsKey(GSTConstants.GSTR1_15I)) {
				String tableSection = GSTConstants.GSTR1_15I;
				Map<String, Map<Long, List<EcomSectionDto>>> tableSectionMap = groupedByTableSection
						.get(GSTConstants.GSTR1_15I);
				for (Entry<String, Map<Long, List<EcomSectionDto>>> tableSectionEntry : tableSectionMap
						.entrySet()) {
					List<B2BInvoiceData> invList = new ArrayList<>();
					String combineKey = tableSectionEntry.getKey();
					String[] gstinParts = combineKey.split("\\|");
					String eTin = gstinParts[0];
					String cgstin = gstinParts[1];
					Map<Long, List<EcomSectionDto>> idsMap = tableSectionEntry
							.getValue();
					for (Entry<Long, List<EcomSectionDto>> idMapEntry : idsMap
							.entrySet()) {
						List<B2bLineItem> itmsList = new ArrayList<>();
						List<EcomSectionDto> sectionDtoDetails = idMapEntry
								.getValue();
						int itemNumber = 0;
						for (EcomSectionDto ecomSectionDto : sectionDtoDetails) {
							B2bLineItem itms = setItemDetail(ecomSectionDto,
									++itemNumber, isSEZSupplier);
							itmsList.add(itms);
						}
						idsList.add(idMapEntry.getKey());
						B2BInvoiceData inv = setInvData(
								sectionDtoDetails.get(0), section, itmsList,
								taxDocType);
						invList.add(inv);
					}
					B2BEcomInvoices b2b = setInv(eTin, cgstin, invList,
							tableSection);
					b2bList.add(b2b);
				}
			}
			if (groupedByTableSection.containsKey(GSTConstants.GSTR1_15III)) {
				String tableSection = GSTConstants.GSTR1_15III;
				Map<String, Map<Long, List<EcomSectionDto>>> tableSectionMap = groupedByTableSection
						.get(GSTConstants.GSTR1_15III);
				for (Entry<String, Map<Long, List<EcomSectionDto>>> tableSectionEntry : tableSectionMap
						.entrySet()) {
					List<B2BInvoiceData> invList = new ArrayList<>();
					String cgstin = tableSectionEntry.getKey();
					Map<Long, List<EcomSectionDto>> idsMap = tableSectionEntry
							.getValue();
					for (Entry<Long, List<EcomSectionDto>> idMapEntry : idsMap
							.entrySet()) {
						List<B2bLineItem> itmsList = new ArrayList<>();
						List<EcomSectionDto> sectionDtoDetails = idMapEntry
								.getValue();
						int itemNumber = 0;
						for (EcomSectionDto ecomSectionDto : sectionDtoDetails) {
							B2bLineItem itms = setItemDetail(ecomSectionDto,
									++itemNumber, isSEZSupplier);
							itmsList.add(itms);
						}
						idsList.add(idMapEntry.getKey());
						B2BInvoiceData inv = setInvData(
								sectionDtoDetails.get(0), section, itmsList,
								taxDocType);
						invList.add(inv);
					}
					B2BEcomInvoices b2b = setInv(null, cgstin, invList,
							tableSection);
					urp2bList.add(b2b);
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}
			if (!b2bList.isEmpty()) {
				ecomObj.setB2b(b2bList);
			}
			if (!urp2bList.isEmpty()) {
				ecomObj.setUrp2b(urp2bList);
			}
			SaveGstr1 gstr1 = setBatch(supplierGstin, taxPeriod, section,
					ecomObj);
			batchesList.add(gstr1);
			batchIdsList.add(idsList);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		batchDto.setGstr1(batchesList);
		batchDto.setIdsList(batchIdsList);
		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAV_GST1_JSON_FORMATION_END,
				"RateDataToEcomSupConverter",
				PerfamanceEventConstants.convertToGstr1Object, null);
		return batchDto;
		//
	}

	private B2bLineItem setItemDetail(EcomSectionDto sectionDto, int counter2,
			boolean isSEZSupplier) {

		B2bLineItemDetail itemData = new B2bLineItemDetail();
		B2bLineItem itms = new B2bLineItem();
		String sGstin = sectionDto.getSgstin();
		String supplyType = sectionDto.getSupplyType();
		String pos = sectionDto.getPos();

		if (isSEZSupplier
				|| DocAndSupplyTypeConstants.INV_TYPE_SEZWP.equals(supplyType)
				|| (sGstin != null
						&& !sGstin.substring(0, 2).equalsIgnoreCase(pos))) {
			itemData.setIgstAmount(sectionDto.getIgstAmt());
		} else {
			itemData.setSgstAmount(sectionDto.getSgstAmt());
			itemData.setCgstAmount(sectionDto.getCgstAmt());
		}
		itemData.setCessAmount(sectionDto.getCessAmt());
		itemData.setTaxableValue(sectionDto.getTaxValue());
		itemData.setRate(sectionDto.getTaxRate());
		itms.setLineNumber(counter2);
		itms.setItemDetail(itemData);
		return itms;
	}

	private B2BInvoiceData setInvData(EcomSectionDto ecomSection,
			String section, List<B2bLineItem> itmsList, String taxDocType) {

		B2BInvoiceData inv = new B2BInvoiceData();

		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			inv.setInvoiceStatus(APIConstants.D); // D-Delete, A-Accept,
													// R-Reject
		} else {
			inv.setInvoiceStatus(APIConstants.N);
		}
		/**
		 * R- Regular B2B Invoices, DE – Deemed Exports, SEWP – SEZ Exports with
		 * payment, SEWOP – SEZ exports without payment, CBW - Custom Bonded
		 * Warehouse
		 */
		String sGstin = ecomSection.getSgstin();
		String pos = ecomSection.getPos();
		inv.setInvoiceNumber(ecomSection.getDocNo());
		inv.setInvoiceDate(ecomSection.getDocDate());
		inv.setInvoiceValue(ecomSection.getDocAmt());
		inv.setPos(pos);
		inv.setInvoiceType(decideInvType(ecomSection.getDocType(),
				ecomSection.getSupplyType()));
		inv.setDocId(ecomSection.getDocId());
		if (sGstin != null && !sGstin.substring(0, 2).equalsIgnoreCase(pos)) {
			inv.setSupplyType(APIConstants.SUP_TYPE_INTER);
		} else {
			inv.setSupplyType(APIConstants.SUP_TYPE_INTRA);
		}
		inv.setLineItems(itmsList);
		return inv;
	}

	private String decideInvType(String docType, String supplyType) {

		if (DocAndSupplyTypeConstants.DXP.equals(supplyType)) {
			return APIConstants.INV_TYPE_DE;
		} else if (DocAndSupplyTypeConstants.INV_TYPE_SEZWP
				.equals(supplyType)) {
			return APIConstants.INV_TYPE_SEWP;
		} else if (DocAndSupplyTypeConstants.INV_TYPE_SEZWOP
				.equals(supplyType)) {
			return APIConstants.INV_TYPE_SEWOP;
		} else if (DocAndSupplyTypeConstants.INV.equals(docType)
				|| DocAndSupplyTypeConstants.TAX.concat(",")
						.concat(DocAndSupplyTypeConstants.DTA)
						.contains(supplyType)) {
			return APIConstants.INV_TYPE_R;
		}
		return null;
	}

	private B2BEcomInvoices setInv(String eTin, String cgstin,
			List<B2BInvoiceData> invList, String tableSection) {

		String cGstin = Strings.isNullOrEmpty(cgstin) ? APIConstants.URP
				: cgstin;
		if (cGstin.equalsIgnoreCase(APIConstants.URP)) {
			cGstin = null;
		}
		B2BEcomInvoices b2b = new B2BEcomInvoices();
		b2b.setRtin(cGstin);
		if (tableSection.equalsIgnoreCase(GSTConstants.GSTR1_15I)) {
			b2b.setStin(eTin);
		}
		b2b.setB2bInvoiceData(invList);
		return b2b;
	}

	private SaveGstr1 setBatch(String sGstin, String txPriod, String section,
			Ecom ecomObj) {

		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		gstr1.setEcom(ecomObj);
		return gstr1;
	}

	private List<EcomSectionDto> convertArrtoDto(List<Object[]> objects) {

		List<EcomSectionDto> sectionDtoList = new ArrayList<>();

		for (int counter = 0; counter < objects.size(); counter++) {
			EcomSectionDto sectionDto = new EcomSectionDto();
			Object[] arr1 = objects.get(counter);
			sectionDto.setSgstin(
					arr1[0] != null ? String.valueOf(arr1[0]) : null);
			sectionDto.setTaxPeriod(
					arr1[1] != null ? String.valueOf(arr1[1]) : null);
			sectionDto.setCgstin(
					arr1[2] != null ? String.valueOf(arr1[2]) : null);
			sectionDto
					.setDocNo(arr1[3] != null ? String.valueOf(arr1[3]) : null);
			BigDecimal invVal = arr1[4] != null
					? new BigDecimal(String.valueOf(arr1[4])) : BigDecimal.ZERO;
			sectionDto.setDocAmt(invVal);
			sectionDto.setPos(arr1[5] != null ? String.valueOf(arr1[5]) : null);
			sectionDto.setEcomTin(
					arr1[6] != null ? String.valueOf(arr1[6]) : null);
			Long id = arr1[7] != null ? (Long) arr1[7] : null;
			sectionDto.setDocId(id);
			sectionDto.setSupplyType(
					arr1[8] != null ? String.valueOf(arr1[8]) : null);
			sectionDto.setDocType(
					arr1[9] != null ? String.valueOf(arr1[9]) : null);

			String invDate = null;
			if (arr1[10] != null && arr1[10].toString().trim().length() > 0) {
				invDate = ((LocalDate) arr1[10])
						.format(DateUtil.SUPPORTED_DATE_FORMAT2);
			}
			sectionDto.setDocDate(invDate);

			sectionDto.setTableSection(
					arr1[11] != null ? String.valueOf(arr1[11]) : null);

			BigDecimal taxRate = arr1[12] != null
					? new BigDecimal(String.valueOf(arr1[12]))
					: BigDecimal.ZERO;

			BigDecimal taxValue = arr1[13] != null
					? new BigDecimal(String.valueOf(arr1[13]))
					: BigDecimal.ZERO;
			BigDecimal igstAmt = arr1[14] != null
					? new BigDecimal(String.valueOf(arr1[14]))
					: BigDecimal.ZERO;
			BigDecimal cgstAmt = arr1[15] != null
					? new BigDecimal(String.valueOf(arr1[15]))
					: BigDecimal.ZERO;
			BigDecimal sgstAmt = arr1[16] != null
					? new BigDecimal(String.valueOf(arr1[16]))
					: BigDecimal.ZERO;

			BigDecimal cessAmt = arr1[17] != null
					? new BigDecimal(String.valueOf(arr1[17]))
					: BigDecimal.ZERO;

			sectionDto.setSec7(
					arr1[18] != null ? String.valueOf(arr1[18]) : null);

			sectionDto.setTaxRate(taxRate);
			sectionDto.setTaxValue(taxValue);
			sectionDto.setIgstAmt(igstAmt);
			sectionDto.setCgstAmt(cgstAmt);
			sectionDto.setSgstAmt(sgstAmt);
			sectionDto.setCessAmt(cessAmt);
			sectionDtoList.add(sectionDto);
		}
		return sectionDtoList;
	}

	private boolean isSEZSupplier(String groupCode, String supplierGstin) {

		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				supplierGstin);
		if (gstin != null) {
			if (gstin.getRegistrationType() != null) {
				String regType = gstin.getRegistrationType();
				if (REGYPE_IMPORTS.contains(regType.toUpperCase())) {
					return true;
				}
			}
		}
		return false;
	}

	private String combineKey(String tableSection, String etin, String cgstin) {

		if (tableSection.equalsIgnoreCase(GSTConstants.GSTR1_15I)) {
			return String.format("%s|%s", etin, cgstin);
		} else {
			return String.format("%s", cgstin);
		}
	}

	@Data
	private class EcomSectionDto {

		String sgstin;
		String taxPeriod;
		String cgstin;
		String ecomTin;
		String docNo;
		BigDecimal docAmt;
		String pos;
		Long docId;
		String supplyType;
		String docType;
		String docDate;
		String tableSection;
		BigDecimal taxRate;
		BigDecimal taxValue;
		BigDecimal igstAmt;
		BigDecimal cgstAmt;
		BigDecimal sgstAmt;
		BigDecimal cessAmt;
		String sec7;

	}
}
