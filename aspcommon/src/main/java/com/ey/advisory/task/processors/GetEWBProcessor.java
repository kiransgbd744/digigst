/**
 * 
 */
package com.ey.advisory.task.processors;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.clientBusiness.CounterPartyEwbBillRepository;
import com.ey.advisory.app.services.ewb.EwbBusinessService;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.ewb.client.domain.EWBHeader;
import com.ey.advisory.ewb.client.domain.EWBItem;
import com.ey.advisory.ewb.client.domain.EWBVehicle;
import com.ey.advisory.ewb.client.repositories.EWBHeaderRepository;
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.ey.advisory.ewb.dto.ItemList;
import com.ey.advisory.ewb.dto.VehiclListDetail;
import com.ey.advisory.processing.message.GetCounterPartyMsg;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 * 
 */
@Slf4j
@Component("GetEWBProcessor")
public class GetEWBProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("EwbBusinessServiceImpl")
	EwbBusinessService ewbService;

	@Autowired
	CounterPartyEwbBillRepository counterPartyEwbBillRepo;

	@Autowired
	EWBHeaderRepository ewbHeaderRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		@SuppressWarnings("serial")
		Type typeList = new TypeToken<List<GetCounterPartyMsg>>() {
		}.getType();
		List<GetCounterPartyMsg> msgList = gson.fromJson(jsonString, typeList);

		for (GetCounterPartyMsg msg : msgList) {

			String ewbNo = msg.getEwbNo();
			String gstin = msg.getGstin();

			try {
				GetEwbResponseDto resp = ewbService.getEWB(ewbNo, gstin);

				if (Strings.isNullOrEmpty(resp.getErrorCode())) {

					EWBHeader header = convertToHeader(resp, gstin);

					List<EWBItem> items = convertToItem(resp, header);
					header.setItems(items);

					List<EWBVehicle> vehicles = convertToVehicles(resp, header);
					header.setTransportationList(vehicles);

					ewbHeaderRepo.save(header);

					counterPartyEwbBillRepo.updateStatus(ewbNo, "SUCCESS",
							msg.getDate());

				} else {
					LOGGER.error(
							"NIC GET EWB API call has returned error response {}",
							resp.getErrorMessage());
					counterPartyEwbBillRepo.updateStatus(ewbNo, "FAILED",
							msg.getDate());
				}
			} catch (Exception ex) {
				String errMsg = "Error occured while invoking GET eway bill call";

				counterPartyEwbBillRepo.updateStatus(ewbNo, "FAILED",
						msg.getDate());

				LOGGER.error(errMsg, ex);

			}

		}

	}

	private EWBHeader convertToHeader(GetEwbResponseDto resp,
			String clientGstin) {

		EWBHeader header = new EWBHeader();

		header.setEwbNo(resp.getEwbNo());
		header.setEwbBillDate(resp.getEwayBillDate());
		header.setGenMode(resp.getGenMode());
		header.setUserGstin(resp.getUserGstin());
		header.setSupplyType(resp.getSupplyType());
		header.setSubSupplyType(resp.getSubSupplyType());
		header.setDocType(resp.getDocType());
		header.setDocNo(resp.getDocNo());
		header.setDocDate(resp.getDocDate());
		header.setFromGstin(resp.getFromGstin());
		header.setFromTradeName(resp.getFromTrdName());
		header.setFromAddr1(resp.getFromAddr1());
		header.setFromAddr2(resp.getFromAddr2());
		header.setFromPlace(resp.getFromPlace());
		header.setFromPincode(resp.getFromPincode());
		header.setFromStateCode(resp.getFromStateCode());
		header.setToGstin(resp.getToGstin());
		header.setToTrdName(resp.getToTrdName());
		header.setToAddr1(resp.getToAddr1());
		header.setToAddr2(resp.getToAddr2());
		header.setToPlace(resp.getToPlace());
		header.setToPincode(resp.getToPincode());
		header.setToStateCode(resp.getToStateCode());
		header.setTotalVal(resp.getTotalValue());
		header.setTotInvoiceVal(resp.getTotInvValue());
		header.setCgst(resp.getCgstValue());
		header.setSgst(resp.getSgstValue());
		header.setIgst(resp.getIgstValue());
		header.setCess(resp.getCessValue());
		header.setTranspoterId(resp.getTransporterId());
		header.setTranspoterName(resp.getTransporterName());
		header.setStatus(resp.getStatus());
		header.setActualDist(resp.getActualDist());
		header.setNoValidDays(resp.getNoValidDays());
		header.setValidUpto(resp.getValidUpto());
		header.setExtendedTimes(resp.getExtendedTimes());
		header.setRejectStatus(resp.getRejectStatus());
		header.setActFromStateCode(resp.getActFromStateCode());
		header.setActToStateCode(resp.getActToStateCode());
		header.setVehicleType(resp.getVehicleType());
		header.setTransactionType(resp.getTransactionType());
		header.setOtherValue(resp.getOtherValue());
		header.setCessNonAdvolValue(resp.getCessNonAdvolValue());
		header.setSubSupplyDesc(resp.getSubSupplyDesc());
		header.setClientGstin(clientGstin);

		return header;
	}

	private List<EWBItem> convertToItem(GetEwbResponseDto resp,
			EWBHeader header) {

		List<EWBItem> items = new ArrayList<>();
		List<ItemList> itemList = resp.getItemList();

		for (ItemList item : itemList) {
			EWBItem obj = new EWBItem();
			obj.setItemNo(item.getItemNo());
			obj.setProductId(item.getProductId());
			obj.setProductName(item.getProductName());
			obj.setProductDescription(item.getProductDesc());
			obj.setHsnCode(item.getHsnCode());
			obj.setQuantity(item.getQuantity());
			obj.setQtyUnit(item.getQtyUnit());
			obj.setCGSTRate(item.getCgstRate());
			obj.setSGSTRate(item.getSgstRate());
			obj.setIgstRate(item.getIgstRate());
			obj.setCessRate(item.getCessRate());
			obj.setCessNonAdvol(item.getCessNonAdvol());
			obj.setTaxableAmount(item.getTaxableAmount());
			obj.setEWayBill(header);

			items.add(obj);

		}

		return items;
	}

	private List<EWBVehicle> convertToVehicles(GetEwbResponseDto resp,
			EWBHeader header) {

		List<EWBVehicle> vehicles = new ArrayList<>();
		List<VehiclListDetail> vehicleList = resp.getVehiclListDetails();

		for (VehiclListDetail vehicle : vehicleList) {
			EWBVehicle obj = new EWBVehicle();

			obj.setUpdMode(vehicle.getUpdMode());
			obj.setVehicleNo(vehicle.getVehicleNo());
			obj.setFromePlace(vehicle.getFromPlace());
			obj.setFromState(vehicle.getFromState());
			obj.setTripshtNo(vehicle.getTripshtNo());
			obj.setUserGstinTransin(vehicle.getUserGSTINTransin());
			obj.setEnteredDate(vehicle.getEnteredDate());
			obj.setTransMode(vehicle.getTransMode());
			obj.setTransportDocNo(vehicle.getTransDocNo());
			obj.setTransportDocDate(vehicle.getTransDocDate());
			obj.setGroupNo(vehicle.getGroupNo());
			obj.setEWayBill(header);

			vehicles.add(obj);
		}
		return vehicles;
	}
}
