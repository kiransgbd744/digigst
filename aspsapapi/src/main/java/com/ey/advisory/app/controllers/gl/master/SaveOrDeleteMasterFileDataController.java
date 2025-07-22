/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
@RestController
@Slf4j
public class SaveOrDeleteMasterFileDataController {

	@Autowired
	@Qualifier("GlCodeMasterDateSaveOrDeleteServiceImpl")
	private GlCodeMasterDateSaveOrDeleteService glSaveOrDeleteMasterData;

	@PostMapping(value = "/ui/saveMasterFileData", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveMasterFileData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject response = new JsonObject();

		try {
			JsonArray requestArray = JsonParser.parseString(jsonString)
					.getAsJsonArray(); // Expecting an array at root

			for (JsonElement element : requestArray) {
				JsonObject request = element.getAsJsonObject();
				String fileType = request.get("fileType").getAsString();
				Long entityId = request.get("entityId").getAsLong();
				//Long id = request.get("id").getAsLong();
				JsonArray dataArray = request.get("data").getAsJsonArray();

				switch (fileType) {
					case "GL Code Master" : {
						java.lang.reflect.Type listType = new TypeToken<List<GlCodeMasterDto>>() {
						}.getType();
						List<GlCodeMasterDto> glList = gson.fromJson(dataArray,
								listType);
						
						glSaveOrDeleteMasterData.saveGlCodeMasterList(glList,
								entityId);
						
						break;
					}
					case "Document Type" : {
						java.lang.reflect.Type listType = new TypeToken<List<GlMasterDocTypeDto>>() {
						}.getType();
						List<GlMasterDocTypeDto> docList = gson
								.fromJson(dataArray, listType);

						boolean invalid = docList.stream()
								.anyMatch(dto -> dto.getDocType() == null
										|| dto.getDocType().trim().isEmpty()
										|| dto.getDocTypeMs() == null
										|| dto.getDocTypeMs().trim().isEmpty());

						if (invalid) {
							return buildValidationError(gson,
									"Data/Value in both the columns is mandatory");
						}

						glSaveOrDeleteMasterData.saveDocTypeMasterList(docList,
								entityId);
						break;
					}
					case "Supply Type" : {
						java.lang.reflect.Type listType = new TypeToken<List<GlMasterSupplyTypeDto>>() {
						}.getType();
						List<GlMasterSupplyTypeDto> supplyList = gson
								.fromJson(dataArray, listType);

						boolean invalid = supplyList.stream()
								.anyMatch(dto -> dto.getSupplyTypeReg() == null
										|| dto.getSupplyTypeReg().trim()
												.isEmpty()
										|| dto.getSupplyTypeMs() == null
										|| dto.getSupplyTypeMs().trim()
												.isEmpty());

						if (invalid) {
							return buildValidationError(gson,
									"Data/Value in both the columns is mandatory");
						}

						glSaveOrDeleteMasterData
								.saveSupplyTypeMasterList(supplyList, entityId);
						break;
					}
					case "Business Unit" : {
						java.lang.reflect.Type listType = new TypeToken<List<GlBusinessPlaceMasterDto>>() {
						}.getType();
						List<GlBusinessPlaceMasterDto> businessList = gson
								.fromJson(dataArray, listType);

						for (GlBusinessPlaceMasterDto dto : businessList) {
							boolean isGstinBlank = dto.getGstin() == null
									|| dto.getGstin().trim().isEmpty();
							boolean areAllOthersBlank = (dto
									.getBusinessPlace() == null
									|| dto.getBusinessPlace().trim().isEmpty())
									&& (dto.getBusinessArea() == null || dto
											.getBusinessArea().trim().isEmpty())
									&& (dto.getPlantCode() == null || dto
											.getPlantCode().trim().isEmpty())
									&& (dto.getProfitCentre() == null || dto
											.getProfitCentre().trim().isEmpty())
									&& (dto.getCostCentre() == null || dto
											.getCostCentre().trim().isEmpty());

							if (isGstinBlank || areAllOthersBlank) {
								return buildValidationError(gson,
										"Data/Value in both the columns is mandatory");
							}
						}

						glSaveOrDeleteMasterData.saveBusinessPlaceMasterList(
								businessList, entityId);
						break;
					}
					case "Tax Code Master" : {
						java.lang.reflect.Type listType = new TypeToken<List<GlTaxCodeMasterDto>>() {
						}.getType();
						List<GlTaxCodeMasterDto> taxCodeList = gson
								.fromJson(dataArray, listType);

						boolean invalid = taxCodeList.stream().anyMatch(
							    dto -> dto.getTransactionTypeGl() == null
							        || dto.getTransactionTypeGl().trim().isEmpty()
							        || dto.getTaxRateMs() == null
							);


						if (invalid) {
							return buildValidationError(gson,
									"Data/Value in both the columns is mandatory");
						}

						glSaveOrDeleteMasterData
								.saveTaxCodeMasterList(taxCodeList, entityId);
						break;
					}
					case "GL Mapping": {
					    java.lang.reflect.Type listType = new TypeToken<List<GlCodeMappingMasterDto>>() {}.getType();
					    List<GlCodeMappingMasterDto> mappingList = gson.fromJson(dataArray, listType);

					   /* boolean invalid = mappingList.stream()
					        .anyMatch(dto -> dto.getBaseHeader() == null || dto.getBaseHeader().trim().isEmpty()
					            || dto.getInputFileHeaders() == null || dto.getInputFileHeaders().trim().isEmpty());

					    if (invalid) {
					        return buildValidationError(gson, "Base Header and Input File Header are mandatory");
					        } */
					    

					    glSaveOrDeleteMasterData.saveGlMappingMasterList(mappingList, entityId);
					    break;
					}

					
					default :
						return buildValidationError(gson,
								"Invalid file type: " + fileType);
				}
			}

			// Success response if everything saved correctly
			APIRespDto successDto = new APIRespDto("Success",
					"Saved Successfully");
			response.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			response.add("resp", gson.toJsonTree(successDto));
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error("Error saving master data", e);
			APIRespDto errorDto = new APIRespDto("Failed",
					"Error: " + e.getMessage());
			response.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			response.add("resp", gson.toJsonTree(errorDto));
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);
		}
	}

	// Helper method for validation errors
	private ResponseEntity<String> buildValidationError(Gson gson,
			String message) {
		JsonObject response = new JsonObject();
		APIRespDto errorDto = new APIRespDto("Failed", message);
		response.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
		response.add("resp", gson.toJsonTree(errorDto));
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/ui/deleteMasterFileData", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteMasterFileData(@RequestBody String jsonString) {
	    Gson gson = GsonUtil.newSAPGsonInstance();
	    JsonObject response = new JsonObject();

	    try {
	        JsonObject request = JsonParser.parseString(jsonString).getAsJsonObject();
	        String fileType = request.get("fileType").getAsString();
	        Long entityId = request.get("entityId").getAsLong();
	        JsonArray deleteArray = request.getAsJsonArray("delete");

	        List<Long> idsToDelete = new ArrayList<>();
	        for (JsonElement element : deleteArray) {
	            JsonObject idObject = element.getAsJsonObject();
	            idsToDelete.add(idObject.get("id").getAsLong());
	        }

	        switch (fileType) {
	            case "GL Code Master": {
	                glSaveOrDeleteMasterData.softDeleteGlCodeMaster(idsToDelete);
	                break;
	            }
	            case "Document Type": {
	                glSaveOrDeleteMasterData.softDeleteDocTypeMaster(idsToDelete);
	                break;
	            }
	            case "Supply Type": {
	                glSaveOrDeleteMasterData.softDeleteSupplyTypeMaster(idsToDelete);
	                break;
	            }
	            case "Business Unit": {
	                glSaveOrDeleteMasterData.softDeleteBusinessPlaceMaster(idsToDelete);
	                break;
	            }
	            case "Tax Code Master": {
	                glSaveOrDeleteMasterData.softDeleteTaxCodeMaster(idsToDelete);
	                break;
	            }
	            default:
	                return buildValidationError(gson, "Invalid file type: " + fileType);
	        }

	        APIRespDto successDto = new APIRespDto("Success", "Deleted Successfully");
	        response.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
	        response.add("resp", gson.toJsonTree(successDto));
	        return new ResponseEntity<>(response.toString(), HttpStatus.OK);

	    } catch (Exception e) {
	        LOGGER.error("Error deleting master data", e);
	        APIRespDto errorDto = new APIRespDto("Failed", "Error: " + e.getMessage());
	        response.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
	        response.add("resp", gson.toJsonTree(errorDto));
	        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	    }
	}
	
	
}
