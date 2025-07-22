/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.common.AppException;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbItemDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("EwbRequestConverter")
public class EwbRequestConverter {

	public EwayBillRequestDto convert(OutwardTransDocument hdr) {
		try {
			EwayBillRequestDto ewbRequestDto = new EwayBillRequestDto();

			ewbRequestDto
					.setDocType(EyEwbCommonUtil.getDocType(hdr.getDocType()));
			ewbRequestDto.setDocNo(hdr.getDocNo());
			ewbRequestDto.setDocDate(hdr.getDocDate());

			ewbRequestDto.setFromTrdName(hdr.getSupplierTradeName());
			ewbRequestDto.setFromAddr1(EyEwbCommonUtil.getFromAdd1(
					hdr.getDispatcherBuildingNumber(),
					hdr.getSupplierBuildingNumber(), hdr.getDocCategory()));
			ewbRequestDto.setFromAddr2(EyEwbCommonUtil.getFromAdd2(
					hdr.getDispatcherBuildingName(),
					hdr.getSupplierBuildingName(), hdr.getDocCategory()));
			ewbRequestDto.setFromGstin(EyEwbCommonUtil.getFromGstin(hdr.getSgstin(),
					hdr.getTransactionType()));
			ewbRequestDto.setFromPincode(
					EyEwbCommonUtil.getFromPinocode(hdr.getDispatcherPincode(),
							hdr.getSupplierPincode(), hdr.getDocCategory()));
			ewbRequestDto.setFromPlace(
					EyEwbCommonUtil.getFromPlace(hdr.getDispatcherLocation(),
							hdr.getSupplierLocation(), hdr.getDocCategory()));
			ewbRequestDto.setFromStateCode(hdr.getSupplierStateCode() != null
					? hdr.getSupplierStateCode().toString() : null);
			ewbRequestDto.setActFromStateCode(EyEwbCommonUtil
					.getActFromStateCode(hdr.getDispatcherStateCode(),
							hdr.getSupplierStateCode(), hdr.getDocCategory())); //

			ewbRequestDto.setToAddr1(
					EyEwbCommonUtil.getToAdd1(hdr.getShipToBuildingNumber(),
							hdr.getCustOrSuppAddress1(), hdr.getDocCategory()));
			ewbRequestDto.setToAddr2(
					EyEwbCommonUtil.getToAdd2(hdr.getShipToBuildingName(),
							hdr.getCustOrSuppAddress2(), hdr.getDocCategory()));
			ewbRequestDto.setToGstin(EyEwbCommonUtil.getToGstin(hdr.getCgstin(), hdr.getTransactionType()));
			ewbRequestDto.setToPincode(
					EyEwbCommonUtil.getToPinocode(hdr.getShipToPincode(),
							hdr.getCustomerPincode(), hdr.getDocCategory()));
			ewbRequestDto.setToPlace(
					EyEwbCommonUtil.getToPlace(hdr.getShipToLocation(),
							hdr.getCustOrSuppAddress4(), hdr.getDocCategory()));
			ewbRequestDto.setToStateCode(hdr.getBillToState());
			ewbRequestDto.setTotalValue(hdr.getInvoiceAssessableAmount() != null
					? new BigDecimal(hdr.getInvoiceAssessableAmount().stripTrailingZeros().toPlainString())
					: BigDecimal.ZERO);
			ewbRequestDto.setTotInvValue(hdr.getDocAmount() != null
					? new BigDecimal(hdr.getDocAmount().stripTrailingZeros().toPlainString()) : BigDecimal.ZERO);
			ewbRequestDto.setToTrdName(hdr.getCustomerTradeName());
			ewbRequestDto.setActToStateCode(
					EyEwbCommonUtil.getActToStateCode(hdr.getShipToState(),
							hdr.getBillToState(), hdr.getDocCategory()));

			ewbRequestDto.setSupplyType(hdr.getTransactionType());
			ewbRequestDto.setSubSupplyType(
					EyEwbCommonUtil.getSubSupplyType(hdr.getSubSupplyType()));
			ewbRequestDto.setSubSupplyDesc(hdr.getOtherSupplyTypeDescription());

			ewbRequestDto.setTransactionType(
					EyEwbCommonUtil.getTransactionType(hdr.getDocCategory()));

			ewbRequestDto.setTransDistance(hdr.getDistance() != null ? 
					hdr.getDistance().setScale
					(0, RoundingMode.HALF_UP).intValue() : null);
			ewbRequestDto.setTransDocDate(hdr.getTransportDocDate());
			ewbRequestDto.setTransDocNo(hdr.getTransportDocNo());
			ewbRequestDto.setTransMode(
					EyEwbCommonUtil.getTransMode(hdr.getTransportMode()));
			ewbRequestDto.setTransporterId(hdr.getTransporterID());
			ewbRequestDto.setTransporterName(hdr.getTransporterName());

			ewbRequestDto.setVehicleNo(hdr.getVehicleNo());
			ewbRequestDto.setVehicleType(hdr.getVehicleType());

			ewbRequestDto.setCessNonAdvolValue(
					hdr.getInvoiceCessSpecificAmount() != null ? new BigDecimal(hdr
							.getInvoiceCessSpecificAmount().stripTrailingZeros().toPlainString())
							: BigDecimal.ZERO);
			ewbRequestDto
					.setCessValue(hdr.getInvoiceCessAdvaloremAmount() != null
							? new BigDecimal(hdr.getInvoiceCessAdvaloremAmount()
									.stripTrailingZeros().toPlainString())
							: BigDecimal.ZERO);
			ewbRequestDto.setCgstValue(hdr.getInvoiceCgstAmount() != null
					? new BigDecimal(hdr.getInvoiceCgstAmount().stripTrailingZeros().toPlainString()) : BigDecimal.ZERO);
			ewbRequestDto.setIgstValue(hdr.getInvoiceIgstAmount() != null
					? new BigDecimal(hdr.getInvoiceIgstAmount().stripTrailingZeros().toPlainString()) : BigDecimal.ZERO);
			ewbRequestDto.setOtherValue(hdr.getInvoiceOtherCharges() != null
					? new BigDecimal(hdr.getInvoiceOtherCharges().stripTrailingZeros().toPlainString()) : BigDecimal.ZERO);// hardcoded
			// verify
			ewbRequestDto.setSgstValue(hdr.getInvoiceSgstAmount() != null
					? new BigDecimal(hdr.getInvoiceSgstAmount().stripTrailingZeros().toPlainString()) : BigDecimal.ZERO);
			ewbRequestDto.setTotalValue(hdr.getInvoiceAssessableAmount() != null
					? new BigDecimal(hdr.getInvoiceAssessableAmount().stripTrailingZeros().toPlainString())
					: BigDecimal.ZERO);
			ewbRequestDto.setTotInvValue(hdr.getDocAmount() != null
					? new BigDecimal(hdr.getDocAmount().stripTrailingZeros().toPlainString()) : null);

			List<EwbItemDto> itemList = new ArrayList<>();
			List<OutwardTransDocLineItem> lineItems = hdr.getLineItems();

			for (OutwardTransDocLineItem lineItem : lineItems) {
				EwbItemDto item = new EwbItemDto();
				item.setCessNonadvol(lineItem.getCessRateSpecific() != null
						? new BigDecimal(lineItem.getCessRateSpecific().stripTrailingZeros().toPlainString())
						: BigDecimal.ZERO);
				item.setCessRate(lineItem.getCessRateAdvalorem() != null
						? new BigDecimal(lineItem.getCessRateAdvalorem().stripTrailingZeros().toPlainString())
						: BigDecimal.ZERO);
				item.setCgstRate(lineItem.getCgstRate() != null
						? new BigDecimal(lineItem.getCgstRate().stripTrailingZeros().toPlainString()) : BigDecimal.ZERO);
				item.setHsnCode(lineItem.getHsnSac());
				item.setIgstRate(lineItem.getIgstRate() != null
						? new BigDecimal(lineItem.getIgstRate().stripTrailingZeros().toPlainString()) : BigDecimal.ZERO);
				item.setProductDesc(lineItem.getItemDescription());
				item.setProductName(lineItem.getProductName());
				item.setQtyUnit(lineItem.getItemUqcUser());
				item.setQuantity(
						lineItem.getItemQtyUser() != null
								? new BigDecimal(lineItem.getItemQtyUser()
										.stripTrailingZeros().toPlainString())
								: null);
				item.setSgstRate(lineItem.getSgstRate() != null
						? new BigDecimal(lineItem.getSgstRate().stripTrailingZeros().toPlainString()) : BigDecimal.ZERO);
				item.setTaxableAmount(lineItem.getTaxableValue() != null
						? new BigDecimal(lineItem.getTaxableValue().stripTrailingZeros().toPlainString())
						: BigDecimal.ZERO);
				itemList.add(item);
			}
			ewbRequestDto.setItemList(itemList);

			return ewbRequestDto;
		} catch (Exception e) {
			String errMsg = "Exception while Converting the outward doc to EWB json";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}
	}

}
