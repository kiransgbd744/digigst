package com.ey.advisory.app.data.daos.ewb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.repositories.client.EwbMultiVehicleDetailsRepository;
import com.ey.advisory.app.data.repositories.client.EwbMultiVehicleRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIParams;
import com.ey.advisory.ewb.app.api.APIReqParamConstants;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.client.domain.EwbMultiVehicleDetailsEntity;
import com.ey.advisory.ewb.client.domain.EwbMultiVehicleEntity;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsDtoReq;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleRequestDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleGetVehicleRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListGroupResp;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListVehicleResp;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRequestDto;
import com.ey.advisory.ewb.dto.VehicleDetailsRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */
@Slf4j
@Service("EwbMultiVehicleDaoImpl")
public class EwbMultiVehicleDaoImpl implements EwbMultiVehicleDao {

	private static final String EWB_GROUP_NOT_FOUND = "Group id not found";
	private static final String EWB_VEHICLE_NOT_FOUND = "Vehicle number not found";

	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("EwbMultiVehicleRepository")
	private EwbMultiVehicleRepository eWbrepoMultiVehicleRepo;

	@Autowired
	@Qualifier("EwbMultiVehicleDetailsRepository")
	private EwbMultiVehicleDetailsRepository ewbMultiVehicleDetailsRepo;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepo;

	@Override
	public APIResponse initiateMultiVehicle(String reqBody, String gstin) {
		try {
			KeyValuePair<String, String> apiParam = new KeyValuePair<>(
					APIReqParamConstants.GSTIN, gstin);
			APIParams apiParams = new APIParams(APIProviderEnum.EWB,
					APIIdentifiers.MultiVehMovement, apiParam);
			return apiExecutor.execute(apiParams, reqBody);
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking '%s', GSTIN - '%s'",
					APIIdentifiers.MultiVehMovement, gstin);
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}
	}

	@Override
	public void saveInitiateMultivehicleResp(String jsonResp,
			InitiateMultiVehicleRequestDto req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("EwbMultiVehicleDaoImpl.saveInitiateMultivehicleResp "
					+ "parsing the json response");
		}
		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject parseResponse = (JsonObject) jsonParser.parse(jsonResp)
					.getAsJsonObject();
			String groupNo = parseResponse.get("groupNo").getAsString();
			String dateTime = parseResponse.get("createdDate").getAsString();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd/MM/yyyy hh:mm:ss a");
			LocalDateTime createdDate = LocalDateTime.parse(dateTime,
					formatter);
			EwbMultiVehicleEntity entity = new EwbMultiVehicleEntity();
			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";
			// Saving Response to DB Table.
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("EwbMultiVehicleDaoImpl"
						+ ".saveInitiateMultivehicleResp persisting Initiate "
						+ "Multi Vehicles response to EWB_MULTIVEHICLE table");
			}
			EwbMultiVehicleEntity findGroupNo = eWbrepoMultiVehicleRepo
					.findByEwbNoAndGroupNoAndErrorCodeIsNullAndErrorMessageIsNull(
							req.getEwbNo(), Long.parseLong(groupNo));
			if (findGroupNo == null) {
				entity.setEwbNo(req.getEwbNo());
				entity.setDocHeaderId(req.getDocHeaderId());
				entity.setGroupNo(Long.parseLong(groupNo));
				entity.setTransMode(req.getTransMode());
				entity.setFromPlace(req.getFromPlace());
				entity.setToPlace(req.getToPlace());
				entity.setTotalQty(req.getTotalQuantity());
				entity.setUnit(req.getUnitCode());
				entity.setFromState(req.getFromState());
				entity.setToState(req.getToState());
				entity.setReason(req.getReasonCode());
				entity.setRemarks(req.getReasonRem());
				entity.setCreatedDate(createdDate);
				entity.setCreatedBy(userName);
				eWbrepoMultiVehicleRepo.save(entity);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info(
							" Initiate Multi Vehicles response successfully saved to "
									+ " EWB_MULTIVEHICLE table");
				}
			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting Initiate Multi "
					+ "Vehicles response";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public APIResponse addMultiVehicle(AddMultiVehicleDetailsDtoReq req,
			String gstin) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
			String reqBody = gson.toJson(req);
			KeyValuePair<String, String> apiParam = new KeyValuePair<>(
					APIReqParamConstants.GSTIN, gstin);
			APIParams apiParams = new APIParams(APIProviderEnum.EWB,
					APIIdentifiers.MultiVehAdd, apiParam);
			return apiExecutor.execute(apiParams, reqBody);
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking '%s', GSTIN - '%s'",
					APIIdentifiers.MultiVehAdd, gstin);
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}
	}

	@Override
	public String saveAddMultivehicleRes(String jsonResp,
			AddMultiVehicleDetailsDtoReq req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"EwbMultiVehicleDaoImpl.saveAddMultivehicleRes parsing the "
							+ "json response");
		}
		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject parseResponse = (JsonObject) jsonParser.parse(jsonResp)
					.getAsJsonObject();

			String groupNumber = parseResponse.get("groupNo").getAsString();
			Long ewbNo = req.getEwbNo();
			String groupNo = req.getGroupNo();
			String message = "Vehicle Added SuccessFully";
			String dateTime = parseResponse.get("vehAddedDate").getAsString();

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd/MM/yyyy hh:mm:ss a");
			LocalDateTime addedDate = LocalDateTime.parse(dateTime, formatter);

			DateTimeFormatter formatt = DateTimeFormatter
					.ofPattern("d/MM/yyyy");
			LocalDate localDate = null;
			if(req.getTransDocDate()!=null && !req.getTransDocDate().isEmpty()){
			 localDate = LocalDate.parse(req.getTransDocDate(),
					formatt);
			}

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";
			Long groupNumb = Long.parseLong(groupNo);

			EwbMultiVehicleEntity initiateVehicle = eWbrepoMultiVehicleRepo
					.findByEwbNoAndGroupNoAndErrorCodeIsNullAndErrorMessageIsNull(
							ewbNo, groupNumb);

			// Saving Response to DB Table.
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("EwbMultiVehicleDaoImpl.saveAddMultivehicleRes"
						+ " persisting Add Multi Vehicles response"
						+ " to EWB_MULTIVEHICLE_DETAILS table");
			}
			if (initiateVehicle != null) {
				EwbMultiVehicleDetailsEntity entity = new EwbMultiVehicleDetailsEntity();
				entity.setVehicleNum(req.getVehicleNo());
				entity.setMultiVehicleId(initiateVehicle.getId());
				entity.setTransDocDate(localDate);
				entity.setTransDocNo(req.getTransDocNo());
				entity.setVehicleQty(req.getQuantity());
				entity.setGroupNo(Long.parseLong(groupNumber));
				entity.setCreatedDate(LocalDateTime.now());
				entity.setVehicleAddedDate(addedDate);
				entity.setCreatedBy(userName);
				entity.setFromState(initiateVehicle.getFromState());
				entity.setFromPlace(initiateVehicle.getFromPlace());
				entity.setReason(initiateVehicle.getReason());
				entity.setRemarks(initiateVehicle.getRemarks());
				entity.setError(false);
				entity.setFunction("A");
				ewbMultiVehicleDetailsRepo.save(entity);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info(
						" Add Multi Vehicles response successfully saved to "
								+ " EWB_MULTIVEHICLE_DETAILS table");
			}
			return message;
		} catch (Exception e) {
			String msg = "Exception occured while persisting Add Multi "
					+ "Vehicles response";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public List<EwbMultiVehicleListGroupResp> getListOfGroup(Long eWbno) {
		List<Long> listGroups = eWbrepoMultiVehicleRepo.findByEwbNumber(eWbno);
		if (CollectionUtils.isEmpty(listGroups))
			throw new AppException(EWB_GROUP_NOT_FOUND);
		List<EwbMultiVehicleListGroupResp> list = listGroups.stream()
				.map(e -> ewbListGroups(e)).collect(Collectors.toList());
		return list;
	}

	private EwbMultiVehicleListGroupResp ewbListGroups(Long groupNumber) {
		EwbMultiVehicleListGroupResp dto = new EwbMultiVehicleListGroupResp();
		dto.setGroupNo(groupNumber);
		return dto;
	}

	@Override
	public List<EwbMultiVehicleListVehicleResp> getListOfVehicleNumber(
			Long eWbno, String groupNo) {
		EwbMultiVehicleEntity initiateVehicle = eWbrepoMultiVehicleRepo
				.findByEwbNoAndGroupNoAndErrorCodeIsNullAndErrorMessageIsNull(
						eWbno, Long.parseLong(groupNo));
		Long id = initiateVehicle.getId();
		List<Object[]> listVehicles = ewbMultiVehicleDetailsRepo
				.findActiveVehicleNumberForMultiVehicle(id,
						Long.parseLong(groupNo));
		
		LOGGER.debug("Converting Query And converting to List BEGIN");
		List<EwbMultiVehicleListVehicleResp> list = listVehicles.stream()
				.map(vehicleNo -> ewbMultiVehicleDetails(vehicleNo))
				.collect(Collectors.toCollection(ArrayList::new));

		LOGGER.debug("Converting Query And converting to List End");
		return list;
	}

	private EwbMultiVehicleListVehicleResp ewbMultiVehicleDetails(
			Object[] arr) {
		EwbMultiVehicleListVehicleResp entity = new EwbMultiVehicleListVehicleResp();
		
		if(arr[0] != null && !arr[0].toString().isEmpty()){
		entity.setVehicleNo(arr[0].toString());
		}
		if(arr[0] == null || arr[0].toString().isEmpty()){
		return entity;
		}
		if(arr[1] != null && !arr[1].toString().isEmpty()){
		entity.setTransDocNo(arr[1].toString());
		}
		return entity;
		
	}

	@Override
	public APIResponse changeMultiVehicle(ChangeMultiVehicleRequestDto req,
			String gstin) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
			String reqBody = gson.toJson(req);
			KeyValuePair<String, String> apiParam = new KeyValuePair<>(
					APIReqParamConstants.GSTIN, gstin);
			APIParams apiParams = new APIParams(APIProviderEnum.EWB,
					APIIdentifiers.MultiVehUpdate, apiParam);
			return apiExecutor.execute(apiParams, reqBody);
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking '%s', GSTIN - '%s'",
					APIIdentifiers.MultiVehAdd, gstin);
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}
	}

	@SuppressWarnings("unused")
	@Override
	public String changeMultiVehicle(String jsonResp,
			ChangeMultiVehicleRequestDto req) {
		
		final String message = "Vehicle Updated SuccessFully";
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"EwbMultiVehicleDaoImpl.saveAddMultivehicleRes parsing the "
							+ "json response");
		}
		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject parseResponse = (JsonObject) jsonParser.parse(jsonResp)
					.getAsJsonObject();
			String groupNumber = parseResponse.get("groupNo").getAsString();
			Long ewbNo = req.getEwbNo();
			String groupNo = req.getGroupNo();
			String vehicleNo = req.getOldvehicleNo();
			String transDocNo = req.getOldTranNo();
			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";
			String dateTime = parseResponse.get("vehUpdDate").getAsString();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd/MM/yyyy hh:mm:ss a");
			LocalDateTime updatedDate = LocalDateTime.parse(dateTime,
					formatter);

			EwbMultiVehicleEntity ewbMultiVehicle = eWbrepoMultiVehicleRepo
					.findByEwbNoAndGroupNoAndErrorCodeIsNullAndErrorMessageIsNull(
							ewbNo, Long.parseLong(groupNo));

			Long multiVehicleId = ewbMultiVehicle.getId();
			EwbMultiVehicleDetailsEntity findMultiVehcile = ewbMultiVehicleDetailsRepo
					.findByMultiVehicleIdAndVehicleNumAndTransDocNoAndIsDeleteFalseAndErrorCodeIsNullAndErrorMessageIsNull(
							multiVehicleId, vehicleNo, transDocNo);
			Long vechicleQty = findMultiVehcile.getVehicleQty();
			if (findMultiVehcile != null) {
				findMultiVehcile.setDelete(true);
				findMultiVehcile.setUpdatedBy(userName);
				findMultiVehcile.setUpdatedDate(LocalDateTime.now());
				ewbMultiVehicleDetailsRepo.save(findMultiVehcile);
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Inside EwbMultiVehicleDaoImpl"
									+ ".changeMultiVehicle() method {} soft "
									+ "delete failed for Multi VehicleId: %d, "
									+ "Vehicle Number: %s " + "TransDocNo: %s",
							multiVehicleId, vehicleNo, transDocNo);
					LOGGER.debug(msg);
				}
			}

			EwbMultiVehicleDetailsEntity entity = new EwbMultiVehicleDetailsEntity();
			LOGGER.info(
					"Updating new vehicle record : " + req.getNewVehicleNo());
			entity.setGroupNo(Long.parseLong(groupNo));
			entity.setMultiVehicleId(multiVehicleId);
			entity.setFromState(String.valueOf(req.getFromState()));
			entity.setFromPlace(req.getFromPlace());
			entity.setReason(req.getReasonCode());
			entity.setRemarks(req.getReasonRem());
			entity.setVehicleNum(req.getNewVehicleNo());
			entity.setOldVehicleNum(req.getOldvehicleNo());
			entity.setTransDocNo(req.getNewTranNo());
			entity.setOldTransDocNum(req.getOldTranNo());
			entity.setVehicleQty(vechicleQty);
			entity.setVehicleAddedDate(updatedDate);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setCreatedBy(userName);
			entity.setError(false);
			entity.setFunction("U");
			ewbMultiVehicleDetailsRepo.save(entity);
			LOGGER.info("New vehicle updated successfully for vehicle numb: "
					+ req.getNewVehicleNo());
			return message;

		} catch (Exception e) {
			String msg = "Exception occured while persisting Initiate Multi"
					+ "Vehicles response";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	@Override
	public List<EwbMultiVehicleDetailsRespDto> getMultiVehicleDetails(
			Long ewbNo, String docNo, String supplierGstin) {
		List<EwbMultiVehicleEntity> ewbMultiVehicle = eWbrepoMultiVehicleRepo
				.findByEwbNo(ewbNo);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Ewb multi vehicle details : " + ewbMultiVehicle);
		}

		List<EwbMultiVehicleDetailsRespDto> list = listMulieVechiDetails(
				ewbMultiVehicle, ewbNo, supplierGstin, docNo);

		return list;
	}

	private List<EwbMultiVehicleDetailsRespDto> listMulieVechiDetails(
			List<EwbMultiVehicleEntity> ewbMultiVehicle, Long ewbNo,
			String supplierGstin, String docNo) {

		List<EwbMultiVehicleDetailsRespDto> listResp = new ArrayList<>();

		EwbEntity getEwbStatus = ewbRepo.findByEwbNum(String.valueOf(ewbNo));

		ewbMultiVehicle.forEach(ewb -> {

			List<EwbMultiVehicleDetailsEntity> findMultiVehcile = ewbMultiVehicleDetailsRepo
					.findByMultiVehicleId(ewb.getId());

			if (findMultiVehcile == null || findMultiVehcile.isEmpty()) {

				EwbMultiVehicleDetailsRespDto dto = new EwbMultiVehicleDetailsRespDto();

				dto.setReason(ewb.getReason() != null ? ewb.getReason() : null);
				dto.setRemarks(
						ewb.getRemarks() != null ? ewb.getRemarks() : null);
				dto.setToPlacecode(
						ewb.getToState() != null ? ewb.getToState() : null);
				dto.setFromStateCode(
						ewb.getFromState() != null ? ewb.getFromState() : null);
				dto.setFromState(
						ewb.getFromPlace() != null ? ewb.getFromPlace() : null);
				dto.setToPlace(
						ewb.getToPlace() != null ? ewb.getToPlace() : null);
				dto.setGroupNo(ewb.getGroupNo());
				dto.setSuppGstin(supplierGstin);
				dto.setEwbNo(ewbNo);
				dto.setErrorMessage(ewb.getErrorMessage());
				dto.setErrorCode(ewb.getErrorCode());
				dto.setTotalQty(ewb.getTotalQty());
				dto.setUnit(ewb.getUnit());
				dto.setTransMode(ewb.getTransMode());
				if (getEwbStatus.getEwbDate() != null) {
					DateTimeFormatter format = DateTimeFormatter
							.ofPattern("dd/MM/yyyy HH:mm:ss");
					String formatDateTime = getEwbStatus.getEwbDate()
							.format(format);
					dto.setEwbDate(formatDateTime);
				}
				dto.setEwbStatus("Active");
				dto.setDocNo(docNo);
				listResp.add(dto);
			} else {
				findMultiVehcile.forEach(ewbDeatils -> {

					EwbMultiVehicleDetailsRespDto dto = new EwbMultiVehicleDetailsRespDto();
					dto.setVehicleNo(ewbDeatils.getVehicleNum() != null
							? ewbDeatils.getVehicleNum() : null);
					dto.setTransDocNum(ewbDeatils.getTransDocNo() != null
							? ewbDeatils.getTransDocNo() : null);

					if (ewbDeatils.getTransDocDate() != null) {
						LocalDate date = ewbDeatils.getTransDocDate();

						DateTimeFormatter format = DateTimeFormatter
								.ofPattern("dd/MM/yyyy");

						String transDocDate = date.format(format);

						dto.setTransDocDate(transDocDate);
					}
					dto.setReason(ewbDeatils.getReason() != null
							? ewbDeatils.getReason() : null);
					dto.setRemarks(ewbDeatils.getRemarks() != null
							? ewbDeatils.getRemarks() : null);
					dto.setToPlacecode(
							ewb.getToState() != null ? ewb.getToState() : null);
					dto.setFromStateCode(ewb.getFromState() != null
							? ewbDeatils.getFromState() : null);
					dto.setFromState(ewb.getFromPlace() != null
							? ewbDeatils.getFromPlace() : null);
					dto.setToPlace(
							ewb.getToPlace() != null ? ewb.getToPlace() : null);
					dto.setGroupNo(ewb.getGroupNo());
					dto.setSuppGstin(supplierGstin);
					dto.setEwbNo(ewbNo);
					dto.setTotalQty(ewb.getTotalQty());
					dto.setVehicleQty(ewbDeatils.getVehicleQty());
					dto.setUnit(ewb.getUnit());
					dto.setTransMode(ewb.getTransMode());
					dto.setErrorMessage(ewbDeatils.getErrorMessage());
					dto.setErrorCode(ewbDeatils.getErrorCode());
					if (getEwbStatus.getEwbDate() != null) {
						DateTimeFormatter dateTimeFormat = DateTimeFormatter
								.ofPattern("dd/MM/yyyy HH:mm:ss");
						String formatDateTime = getEwbStatus.getEwbDate()
								.format(dateTimeFormat);
						dto.setEwbDate(formatDateTime);
					}
					dto.setEwbStatus("Active");
					dto.setIsDelete(ewbDeatils.isDelete());
					dto.setFunction(ewbDeatils.getFunction());
					dto.setDocNo(docNo);			
					listResp.add(dto);
				});
			}

		});

		return listResp;
	}

	@Override
	public EwbMultiVehicleGetVehicleRespDto getVehiclesforGroup(Long ewbNo,
			String groupNo) {
		EwbMultiVehicleEntity ewbData = eWbrepoMultiVehicleRepo
				.findByEwbNoAndGroupNoAndErrorCodeIsNullAndErrorMessageIsNull(
						ewbNo, Long.parseLong(groupNo));
		EwbMultiVehicleGetVehicleRespDto resDto = new EwbMultiVehicleGetVehicleRespDto();
		resDto.setEwbNo(ewbNo);
		resDto.setReasonCode(ewbData.getReason());
		resDto.setReasonRem(ewbData.getRemarks());
		resDto.setFromPlace(ewbData.getFromPlace());
		resDto.setFromState(ewbData.getFromState());
		resDto.setToPlace(ewbData.getToPlace());
		resDto.setToState(ewbData.getToState());
		resDto.setTransMode(ewbData.getTransMode());
		resDto.setTotalQty(ewbData.getTotalQty());
		resDto.setUnitCode(ewbData.getUnit());
		resDto.setDocNo(ewbData.getDocHeaderId());
		Long id = ewbData.getId();
		List<Object[]> listVehicles = ewbMultiVehicleDetailsRepo
				.findActiveVehicleNumber(id, Long.parseLong(groupNo));
		LOGGER.debug("Converting to List Begin to get vehicle details ");
		List<VehicleDetailsRespDto> list = listVehicles.stream()
				.map(e -> listVehicleList(e))
				.collect(Collectors.toCollection(ArrayList::new));
		resDto.setListVehicles(list);
		return resDto;
	}

	private VehicleDetailsRespDto listVehicleList(Object[] arr) {
		VehicleDetailsRespDto vehicleDto = new VehicleDetailsRespDto();
		vehicleDto.setVehicleNo(arr[0] != null ? arr[0].toString() : null);
		vehicleDto.setTransDocNo(arr[1] != null ? arr[1].toString() : null);

		if (arr[2] != null) {
			LocalDate date = LocalDate.parse(String.valueOf(arr[2]));

			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("dd/MM/yyyy");

			String transDocDate = date.format(format);

			vehicleDto.setTransDocDate(transDocDate);
		}
		vehicleDto.setQuantity((Long) arr[3] != null ? (Long) arr[3] : null);
		return vehicleDto;
	}
}