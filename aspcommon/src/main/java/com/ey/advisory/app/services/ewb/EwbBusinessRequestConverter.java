/**
 * 
 */
package com.ey.advisory.app.services.ewb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Quintet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.OutwardTransDocLineItem;
import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbItemDto;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("EwbBusinessRequestConverter")
public class EwbBusinessRequestConverter {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	public EwayBillRequestDto convert(OutwardTransDocument hdr) {

		try {
			EwayBillRequestDto ewbRequestDto = new EwayBillRequestDto();

			if (Strings.isNullOrEmpty(hdr.getDocType())) {
				String errMsg = "Document type is missing";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			
			if (Strings.isNullOrEmpty(hdr.getTransactionType())) {
				String errMsg = "Transaction Type is Mandatory";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			reqLogHelper.logAppMessage(hdr.getDocNo(), null, null,
					"Converting Generate Eway bill Request");
			ewbRequestDto
					.setDocType(EyEwbCommonUtil.getDocType(hdr.getDocType()));
			ewbRequestDto.setDocNo(hdr.getDocNo());
			ewbRequestDto.setDocDate(hdr.getDocDate());

			ewbRequestDto.setFromGstin(EyEwbCommonUtil
					.getFromGstin(hdr.getSgstin(), hdr.getTransactionType()));
			ewbRequestDto.setFromTrdName(hdr.getSupplierTradeName());
			ewbRequestDto.setFromStateCode(hdr.getSupplierStateCode() != null
					? hdr.getSupplierStateCode() : null);

			boolean isAddrPrefeReq = isAddressPreferenceRequired(hdr);
			if (isAddrPrefeReq) {
				Quintet<String, String, String, String, Integer> fromValues = fromEWBAddresses(
						hdr);
				String fromAddr2 = fromValues.getValue1();
				String fromAddr1 = fromValues.getValue0();
				String fromPlace = fromValues.getValue2();
				String actFromStateCode = fromValues.getValue3();
				Integer fromPincode = fromValues.getValue4();

				ewbRequestDto.setFromAddr1(fromAddr1);
				ewbRequestDto.setFromAddr2(fromAddr2);
				ewbRequestDto.setFromPlace(fromPlace);
				ewbRequestDto.setActFromStateCode(actFromStateCode);
				ewbRequestDto.setFromPincode(fromPincode);

				Quintet<String, String, String, String, Integer> toValues = toEWBAddresses(
						hdr);

				String toAddr1 = toValues.getValue0();
				String toAddr2 = toValues.getValue1();
				String toPlace = toValues.getValue2();
				String acttoStateCode = toValues.getValue3();
				Integer toPincode = toValues.getValue4();

				ewbRequestDto.setToAddr1(toAddr1);
				ewbRequestDto.setToAddr2(toAddr2);
				ewbRequestDto.setToPlace(toPlace);
				ewbRequestDto.setActToStateCode(acttoStateCode);
				ewbRequestDto.setToPincode(toPincode);

			} else {
				ewbRequestDto.setFromAddr1(EyEwbCommonUtil.getFromAdd1(
						hdr.getDispatcherBuildingNumber(),
						hdr.getSupplierBuildingNumber(), hdr.getDocCategory()));
				ewbRequestDto.setFromAddr2(EyEwbCommonUtil.getFromAdd2(
						hdr.getDispatcherBuildingName(),
						hdr.getSupplierBuildingName(), hdr.getDocCategory()));
				ewbRequestDto.setFromPlace(EyEwbCommonUtil.getFromPlace(
						hdr.getDispatcherLocation(), hdr.getSupplierLocation(),
						hdr.getDocCategory()));
				ewbRequestDto.setActFromStateCode(EyEwbCommonUtil
						.getActFromStateCode(hdr.getDispatcherStateCode(),
								hdr.getSupplierStateCode(),
								hdr.getDocCategory()));
				ewbRequestDto.setFromPincode(EyEwbCommonUtil.getFromPinocode(
						hdr.getDispatcherPincode(), hdr.getSupplierPincode(),
						hdr.getDocCategory()));

				ewbRequestDto.setToAddr1(EyEwbCommonUtil.getToAdd1(
						hdr.getShipToBuildingNumber(),
						hdr.getCustOrSuppAddress1(), hdr.getDocCategory()));
				ewbRequestDto.setToAddr2(EyEwbCommonUtil.getToAdd2(
						hdr.getShipToBuildingName(),
						hdr.getCustOrSuppAddress2(), hdr.getDocCategory()));
				ewbRequestDto.setToPlace(EyEwbCommonUtil.getToPlace(
						hdr.getShipToLocation(), hdr.getCustOrSuppAddress4(),
						hdr.getDocCategory()));
				ewbRequestDto.setActToStateCode(
						EyEwbCommonUtil.getActToStateCode(hdr.getShipToState(),
								hdr.getBillToState(), hdr.getDocCategory()));
				ewbRequestDto.setToPincode(EyEwbCommonUtil.getToPinocode(
						hdr.getShipToPincode(), hdr.getCustomerPincode(),
						hdr.getDocCategory()));

			}

			ewbRequestDto.setToGstin(EyEwbCommonUtil.getToGstin(hdr.getCgstin(),
					hdr.getTransactionType()));
			ewbRequestDto.setToTrdName(hdr.getCustomerTradeName());
			ewbRequestDto.setToStateCode(hdr.getBillToState());
			ewbRequestDto.setTotalValue(removeTrailingZeros(hdr.getInvoiceAssessableAmount()));

			ewbRequestDto.setSupplyType(hdr.getTransactionType());
			if (Strings.isNullOrEmpty(hdr.getSubSupplyType())) {
				String errMsg = "Sub supply type is missing";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			ewbRequestDto.setSubSupplyType(
					EyEwbCommonUtil.getSubSupplyType(hdr.getSubSupplyType()));
			ewbRequestDto.setSubSupplyDesc(hdr.getOtherSupplyTypeDescription());

			if (Strings.isNullOrEmpty(hdr.getDocCategory())) {
				String errMsg = "Doc category is missing";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			ewbRequestDto.setTransactionType(
					EyEwbCommonUtil.getTransactionType(hdr.getDocCategory()));

			ewbRequestDto.setTransDistance(hdr.getDistance());
			ewbRequestDto.setTransporterId(hdr.getTransporterID());
			ewbRequestDto.setTransporterName(hdr.getTransporterName());
			boolean isPartBEligible = isEligibleForPartB(hdr);

			if (isPartBEligible) {
				ewbRequestDto.setTransDocDate(hdr.getTransportDocDate());
				ewbRequestDto.setTransDocNo(hdr.getTransportDocNo());
				ewbRequestDto.setTransMode(
						EyEwbCommonUtil.getTransMode(hdr.getTransportMode()));
				ewbRequestDto.setVehicleNo(hdr.getVehicleNo());
				ewbRequestDto.setVehicleType(hdr.getVehicleType());
			}
			ewbRequestDto.setCessNonAdvolValue(
					removeTrailingZeros(hdr.getInvoiceCessSpecificAmount()));
			ewbRequestDto.setCessValue(
					removeTrailingZeros(hdr.getInvoiceCessAdvaloremAmount()));
			ewbRequestDto.setCgstValue(
					removeTrailingZeros(hdr.getInvoiceCgstAmount()));
			ewbRequestDto.setIgstValue(
					removeTrailingZeros(hdr.getInvoiceIgstAmount()));
			ewbRequestDto.setOtherValue(
					removeTrailingZeros(hdr.getInvoiceOtherCharges()));
			ewbRequestDto.setSgstValue(
					removeTrailingZeros(hdr.getInvoiceSgstAmount()));
			ewbRequestDto.setTotalValue(
					removeTrailingZeros(hdr.getInvoiceAssessableAmount()));
			ewbRequestDto
					.setTotInvValue(removeTrailingZeros(hdr.getDocAmount()));

			List<EwbItemDto> itemList = new ArrayList<>();
			List<OutwardTransDocLineItem> lineItems = hdr.getLineItems();

			for (OutwardTransDocLineItem lineItem : lineItems) {
				EwbItemDto item = new EwbItemDto();
				item.setCessNonadvol(
						removeTrailingZeros(lineItem.getCessRateSpecific()));
				item.setCessRate(
						removeTrailingZeros(lineItem.getCessRateAdvalorem()));
				item.setCgstRate(removeTrailingZeros(lineItem.getCgstRate()));
				item.setHsnCode(lineItem.getHsnSac());
				item.setIgstRate(removeTrailingZeros(lineItem.getIgstRate()));
				item.setProductDesc(lineItem.getItemDescription());
				item.setProductName(lineItem.getProductName());
				item.setQtyUnit(lineItem.getUom());
				item.setQuantity(removeTrailingZerosforQty(lineItem.getQty()));
				item.setSgstRate(removeTrailingZeros(lineItem.getSgstRate()));
				item.setTaxableAmount(
						removeTrailingZeros(lineItem.getTaxableValue()));
				itemList.add(item);
			}
			ewbRequestDto.setItemList(itemList);

			return ewbRequestDto;
		} catch (Exception e) {
			String errMsg = "Exception while Converting the outward doc to EWB json";
			LOGGER.error(errMsg, e);
			throw new AppException(e.getMessage());
		}
	}

	private boolean isEligibleForPartB(OutwardTransDocument hdr) {

		Map<String, Config> configMap = configManager.getConfigs("EWB",
				"ewb.partb", TenantContext.getTenantId());

		boolean partBSuppresReq = configMap.get("ewb.partb.suppresreq") == null
				? Boolean.FALSE
				: Boolean.valueOf(
						configMap.get("ewb.partb.suppresreq").getValue());

		if (!partBSuppresReq) {
			return true;
		}

		if (hdr.getTransportMode() == null
				|| Strings.isNullOrEmpty(hdr.getTransportMode())) {
			return false;
		}

		if ("Road".equalsIgnoreCase(hdr.getTransportMode())) {
			return !Strings.isNullOrEmpty(hdr.getVehicleNo())
					&& !Strings.isNullOrEmpty(hdr.getVehicleType());
		} else if ("InTransit".equalsIgnoreCase(hdr.getTransportMode())) {
			return true;
		} else {
			return !Strings.isNullOrEmpty(hdr.getTransportDocNo())
					&& hdr.getTransportDocDate() != null;
		}
	}

	private boolean isAddressPreferenceRequired(OutwardTransDocument hdr) {

		if ("EXP".equalsIgnoreCase(hdr.getSubSupplyType())) {
			return false;
		}

		Map<String, Config> configMap = configManager.getConfigs("EWB",
				"ewb.address", TenantContext.getTenantId());

		return configMap.get("ewb.address.perference") == null ? Boolean.FALSE
				: Boolean.valueOf(
						configMap.get("ewb.address.perference").getValue());
	}

	public static Quintet<String, String, String, String, Integer> fromEWBAddresses(
			OutwardTransDocument hdr) {

		if ((!(hdr.getDispatcherPincode() == null
				|| hdr.getDispatcherPincode() == 0))
				&& !Strings.isNullOrEmpty(hdr.getDispatcherStateCode())) {
			return new Quintet<>(hdr.getDispatcherBuildingNumber(),
					hdr.getDispatcherBuildingName(),
					hdr.getDispatcherLocation(), hdr.getDispatcherStateCode(),
					hdr.getDispatcherPincode());
		} else {
			return new Quintet<>(hdr.getSupplierBuildingNumber(),
					hdr.getSupplierBuildingName(), hdr.getSupplierLocation(),
					hdr.getSupplierStateCode(), hdr.getSupplierPincode());
		}
	}

	public static Quintet<String, String, String, String, Integer> toEWBAddresses(
			OutwardTransDocument hdr) {

		if ((!(hdr.getShipToPincode() == null || hdr.getShipToPincode() == 0))
				&& !Strings.isNullOrEmpty(hdr.getShipToState())) {
			return new Quintet<>(hdr.getShipToBuildingNumber(),
					hdr.getShipToBuildingName(), hdr.getShipToLocation(),
					hdr.getShipToState(), hdr.getShipToPincode());
		} else {
			return new Quintet<>(hdr.getCustOrSuppAddress1(),
					hdr.getCustOrSuppAddress2(), hdr.getCustOrSuppAddress4(),
					hdr.getBillToState(), hdr.getCustomerPincode());
		}
	}

	private BigDecimal removeTrailingZeros(BigDecimal amt) {

		return amt != null
				? new BigDecimal(amt.stripTrailingZeros().toPlainString())
				: BigDecimal.ZERO;
	}

	private BigDecimal removeTrailingZerosforQty(BigDecimal amt) {

		return amt != null
				? new BigDecimal(amt.stripTrailingZeros().toPlainString())
				: null;
	}
}
