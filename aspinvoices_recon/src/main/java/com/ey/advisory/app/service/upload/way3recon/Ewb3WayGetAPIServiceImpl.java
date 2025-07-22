/**
 * 
 */
package com.ey.advisory.app.service.upload.way3recon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedHeaderEntity;
import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedItemEntity;
import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedVehicleEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbUploadProcessedHeaderRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.einv.common.EyEInvCommonUtil;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.ey.advisory.ewb.dto.ItemList;
import com.ey.advisory.ewb.dto.VehiclListDetail;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Slf4j
@Component("Ewb3WayGetAPIServiceImpl")
public class Ewb3WayGetAPIServiceImpl implements Ewb3WayGetAPIService {

	private static final String errCode = "ER-101";
	private static final String errDesc = "DROPOUT";

	@Autowired
	@Qualifier("EwbUploadProcessedHeaderRepository")
	private EwbUploadProcessedHeaderRepository psdHeaderRepo;

	@Override
	public void prepareGetEwayBillData(List<GetEwbResponseDto> respList) {
		try {

			List<EwbUploadProcessedHeaderEntity> headerEntityList = new ArrayList<>();
			for (GetEwbResponseDto respDto : respList) {

				LOGGER.debug("Started the Processes for EWB No {} ",
						respDto.getEwbNo());
				EwbUploadProcessedHeaderEntity listOfEnt = populateGetEwayDtls(
						respDto);
				headerEntityList.add(listOfEnt);
				LOGGER.debug("End the Processes for EWB No {} ",
						respDto.getEwbNo());
			}

			if (!headerEntityList.isEmpty()) {
				markDuplicateDocuments(headerEntityList);

				for (EwbUploadProcessedHeaderEntity hdr : headerEntityList) {
					if (hdr.getIsDelete()) {
						continue;
					}
					String docKey = hdr.getDocKey();
					String ewbNo = hdr.getEwbNo();

					Optional<EwbUploadProcessedHeaderEntity> hdrDocKey = psdHeaderRepo
							.findByDocKeyAndIsDeleteFalse(docKey);
					if (hdrDocKey.isPresent()) {
						EwbUploadProcessedHeaderEntity existHdrObj = hdrDocKey
								.get();
						String existEwbNo = existHdrObj.getEwbNo();
						if (!existEwbNo.equalsIgnoreCase(ewbNo)) {
							existHdrObj.setErrorDesc(errDesc);
							existHdrObj.setErrorCode(errCode);
						} else {
							existHdrObj.setIsDelete(true);
						}
						psdHeaderRepo.save(existHdrObj);
					}
				}
				psdHeaderRepo.saveAll(headerEntityList);
			}

		} catch (Exception e) {
			String errMsg = String
					.format("Exception occured converting the data ");
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	private EwbUploadProcessedHeaderEntity populateGetEwayDtls(
			GetEwbResponseDto resp) {

		String createdBy = "SYSTEM";

		try {
			EwbUploadProcessedHeaderEntity header = new EwbUploadProcessedHeaderEntity();
			LocalDate docDate = resp.getDocDate();
			String docType = EyEwbCommonUtil.convertDocType(resp.getDocType());
			String docNum = resp.getDocNo();
			String transType = resp.getTransactionType();
			String sGstin = setFromAdd(transType, resp, header);
			setToAdd(transType, resp, header);
			header.setEwbNo(resp.getEwbNo());
			header.setEwbDate(resp.getEwayBillDate() != null
					? resp.getEwayBillDate().toLocalDate() : null);
			header.setTime(resp.getEwayBillDate() != null
					? resp.getEwayBillDate().toLocalTime() : null);
			header.setDerviedTaxPeriod(GenUtil.convertTaxPeriodToInt(
					setRetPeriod(DateUtil.parseObjToDate(docDate.toString()))));
			header.setReturnPeriod(
					setRetPeriod(DateUtil.parseObjToDate(docDate.toString())));
			header.setSupplyType(resp.getSupplyType().equalsIgnoreCase("I")
					? "Inward" : "Outward");
			header.setSubSupplyType(Ewb3WayReconCommUtility
					.getSubSupplyDesc(resp.getSubSupplyType().trim()));
			header.setDocType(docType);
			header.setEInvAppl("INV".equalsIgnoreCase(docType) ? true : false);
			header.setComplianceAppl("INV".equalsIgnoreCase(docType)
					|| "BOS".equalsIgnoreCase(docType) ? true : false);
			header.setDocNum(resp.getDocNo());
			header.setDocDate(resp.getDocDate());
			header.setInvoiceValue(resp.getTotInvValue());
			header.setCgstAmount(resp.getCgstValue());
			header.setSgstAmount(resp.getSgstValue());
			header.setIgstAmount(resp.getIgstValue());
			header.setCessAmountAdv(resp.getCessValue());
			header.setCessAmountSpec(resp.getCessNonAdvolValue());
			header.setEwbStatus(resp.getStatus());
			header.setNoValidDays(resp.getNoValidDays());
			header.setValidTillDate(resp.getValidUpto() != null
					? resp.getValidUpto().toLocalDate().toString() : null);
			header.setExtendedTime((resp.getExtendedTimes()));
			header.setRejectStatus(resp.getRejectStatus());
			header.setTransType(
					EyEwbCommonUtil.getDocCateDesc(resp.getTransactionType()));
			header.setOtherVal(resp.getOtherValue());
			header.setIsProcessed(true);
			header.setIsError(false);
			header.setIsDelete(false);
			header.setTransactionId(resp.getTransporterId());
			header.setTransactionName(resp.getTransporterName());
			String docKey = Ewb3WayReconCommUtility.generateDocKey(docDate,
					sGstin, docType, docNum);
			header.setDocKey(docKey);
			header.setDataOrigin("A");
			header.setCreatedBy(createdBy);
			header.setCreatedDate(LocalDateTime.now());
			Pair<List<EwbUploadProcessedItemEntity>, BigDecimal> lineDtls = prepItemDtls(
					resp, docKey);
			//lineDtls.getValue1()
			header.setAssessableVal(resp.getTotalValue());
			header.setLineItems(lineDtls.getValue0());
			header.setVehDtls(prepVehDtls(resp).getValue0());

			EwbUploadProcessedVehicleEntity maxDateVehicle = prepVehDtls(resp)
					.getValue1();

			header.getVehDtls().forEach(vehDtls -> {
				vehDtls.setEwbHeaderId(header);
				vehDtls.setCreatedBy(createdBy);
				vehDtls.setCreatedDate(LocalDateTime.now());
				vehDtls.setTransMode(EyEInvCommonUtil
						.getTransModeDesc(vehDtls.getTransMode()));
			});
			header.getLineItems().forEach(item -> {
				item.setEwbHeaderId(header);
				item.setCreatedBy(createdBy);
				item.setCreatedDate(LocalDateTime.now());
				// item.setModeOfTrans(EyEInvCommonUtil.getTransMode(transMode));
				// item.setTransactionDocDate(transDocDt.toString());
				// }
				// item.setVehicleNo(vehNo);
			});
			LocalDate transDocDt = maxDateVehicle.getTransportDocDate();
			String vehNo = maxDateVehicle.getVehicleNo();
			header.setModeOfTrans(EyEInvCommonUtil
					.getTransModeDesc(maxDateVehicle.getTransMode()));
			header.setTransactionDocNo(maxDateVehicle.getTransportDocNo());
			header.setTransactionDocDate(
					transDocDt != null ? transDocDt.toString() : null);
			header.setVehicleNo(vehNo);
			header.setVehicleType(resp.getVehicleType());
			header.setDistance(Long.valueOf(resp.getActualDist()));
			header.setEwbDateTime(resp.getEwayBillDate() != null
					? resp.getEwayBillDate() : null);

			return header;
		} catch (Exception e) {

			String errMsg = String.format(
					"Exception occured while converting the data for Get Eway Header for Ewb Bill No %s",
					resp.getEwbNo());
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	private Pair<List<EwbUploadProcessedItemEntity>, BigDecimal> prepItemDtls(
			GetEwbResponseDto resp, String docKey) {

		List<EwbUploadProcessedItemEntity> items = new ArrayList<>();
		List<ItemList> itemList = resp.getItemList();
		BigDecimal assesValHeader = BigDecimal.ZERO;

		for (ItemList item : itemList) {
			EwbUploadProcessedItemEntity obj = new EwbUploadProcessedItemEntity();
			obj.setEwbNo(Long.valueOf(resp.getEwbNo()));
			obj.setEwbDate(resp.getEwayBillDate() != null
					? resp.getEwayBillDate().toLocalDate() : null);
			obj.setEwbTime(resp.getEwayBillDate() != null
					? resp.getEwayBillDate().toLocalTime() : null);
			obj.setEwbStatus(resp.getStatus());
			obj.setValidTillDate(resp.getValidUpto() != null
					? resp.getValidUpto().toLocalDate().toString() : null);

			obj.setTransType(
					EyEwbCommonUtil.getDocCateDesc(resp.getTransactionType()));
			obj.setDocType(EyEwbCommonUtil.convertDocType(resp.getDocType()));
			obj.setDocNumber(resp.getDocNo());
			obj.setDocDate(resp.getDocDate());
			obj.setSupplyType(resp.getSupplyType().equalsIgnoreCase("I")
					? "Inward" : "Outward");
			obj.setSubSupplyType(Ewb3WayReconCommUtility
					.getSubSupplyDesc(resp.getSubSupplyType().trim()));
			obj.setModeofGeneration(resp.getGenMode());
			setFromAddForLine(resp.getTransactionType(), resp, obj);
			setToAddForLine(resp.getTransactionType(), resp, obj);
			obj.setItemSerialNo(String.valueOf(item.getItemNo()));
			obj.setProductName(item.getProductName());
			obj.setProductDesc(item.getProductDesc());
			obj.setHsn(item.getHsnCode());
			obj.setUqc(item.getQtyUnit());
			obj.setQuantity(item.getQuantity());
			obj.setItemAsseAmount(item.getTaxableAmount());
			
			if (item.getIgstRate().compareTo(BigDecimal.ZERO) > 0) {
				obj.setIgstRate(item.getIgstRate());
				obj.setIgstAmount(
						calcLineValue(item.getTaxableAmount(), item.getIgstRate()));
			} else {
				obj.setIgstRate(BigDecimal.ZERO);
				obj.setIgstAmount(BigDecimal.ZERO);
			}

			if (item.getCgstRate().compareTo(BigDecimal.ZERO) > 0) {
				obj.setCgstRate(item.getCgstRate());
				obj.setCgstAmount(
						calcLineValue(item.getTaxableAmount(), item.getCgstRate()));
			} else {
				obj.setCgstRate(BigDecimal.ZERO);
				obj.setCgstAmount(BigDecimal.ZERO);
			}

			if (item.getSgstRate().compareTo(BigDecimal.ZERO) > 0) {
				obj.setSgstRate(item.getSgstRate());
				obj.setSgstAmount(
						calcLineValue(item.getTaxableAmount(), item.getSgstRate()));
			} else {
				obj.setSgstRate(BigDecimal.ZERO);
				obj.setSgstAmount(BigDecimal.ZERO);
			}
			
			if (item.getCessRate().compareTo(BigDecimal.ZERO) > 0) {
				obj.setCessRateAdv(item.getCessRate());
				obj.setCessAmountAdv(
						calcLineValue(item.getTaxableAmount(), item.getCessRate()));
			} else {
				obj.setCessRateAdv(BigDecimal.ZERO);
				obj.setCessAmountAdv(BigDecimal.ZERO);
			}
			
			if (item.getCessNonAdvol().compareTo(BigDecimal.ZERO) > 0) {
				obj.setCessRateSpec(item.getCessNonAdvol());
				obj.setCessAmountSpec(calcLineValue(item.getTaxableAmount(),
						item.getCessNonAdvol()));
			} else {
				obj.setCessRateSpec(BigDecimal.ZERO);
				obj.setCessAmountSpec(BigDecimal.ZERO);
			}

			obj.setTransactionId(resp.getTransporterId());

			obj.setInvoiceValue(resp.getTotInvValue());

			// changes required

			obj.setTransactionName(resp.getTransporterName());
			obj.setDistance(Long.valueOf(resp.getActualDist()));
			// obj.setVehicleType(resp.getVehicleType());
			obj.setDocKey(docKey);
			obj.setDataOrigin("A");
//			assesValHeader = assesValHeader.add(item.getTaxableAmount());
			items.add(obj);
		}

		return new Pair<List<EwbUploadProcessedItemEntity>, BigDecimal>(items,
				assesValHeader);
	}

	private Pair<List<EwbUploadProcessedVehicleEntity>, EwbUploadProcessedVehicleEntity> prepVehDtls(
			GetEwbResponseDto resp) {

		List<VehiclListDetail> vehicleList = resp.getVehiclListDetails();

		List<EwbUploadProcessedVehicleEntity> vehDtlsList = new ArrayList<>();

		for (VehiclListDetail veh : vehicleList) {
			EwbUploadProcessedVehicleEntity obj = new EwbUploadProcessedVehicleEntity();
			obj.setUpdMode(veh.getUpdMode());
			obj.setVehicleNo(veh.getVehicleNo());
			obj.setFromePlace(veh.getFromPlace());
			obj.setFromState(veh.getFromState());
			obj.setTripshtNo(veh.getTripshtNo());
			obj.setUserGstinTransin(veh.getUserGSTINTransin());
			obj.setEnteredDate(veh.getEnteredDate());
			obj.setTransportDocNo(veh.getTransDocNo());
			obj.setTransportDocDate(veh.getTransDocDate());
			obj.setVehicleNo(veh.getVehicleNo());
			obj.setGroupNo(veh.getGroupNo());
			obj.setVehicleNo(veh.getVehicleNo());
			obj.setTransMode(veh.getTransMode());
			vehDtlsList.add(obj);
		}

		Comparator<EwbUploadProcessedVehicleEntity> comparator = Comparator
				.comparing(EwbUploadProcessedVehicleEntity::getEnteredDate);

		EwbUploadProcessedVehicleEntity maxDateVehicle = vehDtlsList.stream()
				.filter(emp -> emp.getEnteredDate() != null).max(comparator)
				.orElse(new EwbUploadProcessedVehicleEntity());

		return new Pair<List<EwbUploadProcessedVehicleEntity>, EwbUploadProcessedVehicleEntity>(
				vehDtlsList, maxDateVehicle);
	}

	private String setFromAdd(String transType, GetEwbResponseDto resp,
			EwbUploadProcessedHeaderEntity header) {

		String sGstin = resp.getFromGstin();
		header.setSupplierGstin(sGstin);
		header.setSupplierTradeName((resp.getFromTrdName()));
		header.setSupplierAddress1(resp.getFromAddr1());
		header.setSupplyAdd2(resp.getFromAddr2());
		header.setSupplyLocation(resp.getFromPlace());
		header.setSupplyPincode(String.valueOf(resp.getFromPincode()));
		header.setSupplyStatecode(resp.getFromStateCode());

		header.setDispGstin(sGstin);
		header.setDispTradename((resp.getFromTrdName()));
		header.setDispAddr1(resp.getFromAddr1());
		header.setDispAddr2(resp.getFromAddr2());
		header.setDispLocation(resp.getFromPlace());
		header.setDispPincode(String.valueOf(resp.getFromPincode()));
		header.setDispStatecode(resp.getFromStateCode());
		header.setFromGstinInfo(sGstin);
		return sGstin;

	}

	private String setToAdd(String transType, GetEwbResponseDto resp,
			EwbUploadProcessedHeaderEntity header) {

		String bGstin = resp.getToGstin();
		header.setCustomerGstin(resp.getToGstin() != null ?
				resp.getToGstin().trim() : null);		
		header.setCustomerTradename(resp.getToTrdName());
		header.setCustomerAddr1(resp.getToAddr1());
		header.setCustomerAddr2(resp.getToAddr2());
		header.setCustomerLocation(resp.getToPlace());
		header.setCustomerPincode(String.valueOf(resp.getToPincode()));
		header.setCustomerStatecode(resp.getToStateCode());

		header.setShipToGstin(bGstin);
		header.setShipToTradename(resp.getToTrdName());
		header.setShipToAaddr1(resp.getToAddr1());
		header.setShipT0Aaddr2(resp.getToAddr2());
		header.setShipToLocation(resp.getToPlace());
		header.setShipToPincode(String.valueOf(resp.getToPincode()));
		header.setShipToStatecode(resp.getToStateCode());
		header.setToGstinInfo(bGstin);

		return bGstin;
	}

	private String setFromAddForLine(String transType, GetEwbResponseDto resp,
			EwbUploadProcessedItemEntity item) {

		String sGstin = resp.getFromGstin();
		item.setSupplierGstin(sGstin);
		item.setSupplierTradeName((resp.getFromTrdName()));
		item.setSupplierAddress1(resp.getFromAddr1());
		item.setSupplyAdd2(resp.getFromAddr2());
		item.setSupplyLocation(resp.getFromPlace());
		item.setSupplyPincode(String.valueOf(resp.getFromPincode()));
		item.setSupplyStatecode(resp.getFromStateCode());

		item.setDispGstin(sGstin);
		item.setDispTradename((resp.getFromTrdName()));
		item.setDispAddr1(resp.getFromAddr1());
		item.setDispAddr2(resp.getFromAddr2());
		item.setDispLocation(resp.getFromPlace());
		item.setDispPincode(String.valueOf(resp.getFromPincode()));
		item.setDispStatecode(resp.getFromStateCode());
		item.setFromGstinInfo(sGstin);

		return sGstin;
	}

	private String setToAddForLine(String transType, GetEwbResponseDto resp,
			EwbUploadProcessedItemEntity item) {

		String bGstin = resp.getToGstin();
		item.setCustomerGstin(
				resp.getToGstin() != null ? resp.getToGstin().trim() : null);		
		item.setCustomerTradename(resp.getToTrdName());
		item.setCustomerAddr1(resp.getToAddr1());
		item.setCustomerAddr2(resp.getToAddr2());
		item.setCustomerLocation(resp.getToPlace());
		item.setCustomerPincode(String.valueOf(resp.getToPincode()));
		item.setCustomerStatecode(resp.getToStateCode());

		item.setShipToGstin(bGstin);
		item.setShipToTradename(resp.getToTrdName());
		item.setShipToAaddr1(resp.getToAddr1());
		item.setShipT0Aaddr2(resp.getToAddr2());
		item.setShipToLocation(resp.getToPlace());
		item.setShipToPincode(String.valueOf(resp.getToPincode()));
		item.setShipToStatecode(resp.getToStateCode());
		item.setToGstinInfo(bGstin);
		return bGstin;
	}

	private String setRetPeriod(LocalDate docDate) {

		int month = docDate.getMonth().getValue();
		int year = docDate.getYear();

		String taxPeriod = month < 10 ? "0" + month + year : "" + month + year;
		return taxPeriod;

	}

	private BigDecimal calcLineValue(BigDecimal totVal, BigDecimal gstVal) {

		if (totVal != null && gstVal != null)
			return new BigDecimal(
					totVal.doubleValue() * gstVal.doubleValue() / 100);
		else {
			return new BigDecimal(0);
		}
	}

	private static void markDuplicateDocuments(
			List<EwbUploadProcessedHeaderEntity> docs) {

		// Get the map of documents will the keys and values as the list of
		// documents.
		Map<String, List<EwbUploadProcessedHeaderEntity>> allDocsMap = docs
				.stream()
				.sorted(Comparator
						.comparing(
								EwbUploadProcessedHeaderEntity::getEwbDateTime)
						.reversed())
				.collect(Collectors.groupingBy(doc -> doc.getDocKey()));

		// Filter out the documents that have more than one element in the value
		// list.
		Map<String, List<EwbUploadProcessedHeaderEntity>> duplicatesMap = allDocsMap
				.entrySet().stream().filter(e -> e.getValue().size() > 1)
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));

		duplicatesMap.entrySet().forEach(entry -> {
			// String key = entry.getKey();
			List<EwbUploadProcessedHeaderEntity> value = entry.getValue();
			int lastIndex = value.size() - 1;
			IntStream.range(0, value.size()).forEach(idx -> {
				EwbUploadProcessedHeaderEntity hdr = value.get(idx);
				if (idx != lastIndex) {
					hdr.setIsDelete(true);
				}
			});
		});
	}

}
