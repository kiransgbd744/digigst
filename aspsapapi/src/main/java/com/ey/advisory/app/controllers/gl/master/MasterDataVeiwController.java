/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.asprecon.GlBusinessPlaceMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlCodeMappingMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlCodeMasterRepo;
import com.ey.advisory.app.data.repositories.client.asprecon.GlDocTypeMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlMasterSupplyTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlTaxCodeMasterRepo;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MasterDataVeiwController {

	@Autowired
	@Qualifier("GlBusinessPlaceMasterRepository")
	private GlBusinessPlaceMasterRepository glBusinessPlaceMasterRepository;

	@Autowired
	@Qualifier("GlCodeMasterRepo")
	private GlCodeMasterRepo glCodeMasterRepo;

	@Autowired
	@Qualifier("GlDocTypeMasterRepository")
	private GlDocTypeMasterRepository glDocTypeMasterRepository;

	@Autowired
	@Qualifier("GlMasterSupplyTypeRepository")
	private GlMasterSupplyTypeRepository glMasterSupplyTypeRepository;

	@Autowired
	@Qualifier("GlTaxCodeMasterRepo")
	GlTaxCodeMasterRepo glTaxCodeMasterRepo;
	
	@Autowired
	@Qualifier("GlCodeMappingMasterRepository")
	GlCodeMappingMasterRepository glCodeMappingMasterRepository;

	// @PostMapping(value = "/ui/getMasterFileData", produces =
	// MediaType.APPLICATION_JSON_VALUE)
	@PostMapping(value = "/ui/getMasterFileData", produces = {
			MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> getMasterFileData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject response = new JsonObject();

		try {
			JsonObject request = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String fileType = request.get("fileType").getAsString();
			if ("Business Unit".equalsIgnoreCase(fileType)) {
			    fileType = "Business_Unit_code";
			} else if ("Document Type".equalsIgnoreCase(fileType)) {
			    fileType = "Document_type";
			} else if ("Supply Type".equalsIgnoreCase(fileType)) {
			    fileType = "Supply_Type";
			} else if ("GL Code Master".equalsIgnoreCase(fileType)) {
			    fileType = "GL_Code_Mapping_Master_GL";
			} else if ("Tax Code Master".equalsIgnoreCase(fileType)) {
			    fileType = "Tax_code";
			}

			Long entityId = request.get("entityId").getAsLong();

			JsonElement resultData = null;

			switch (fileType.trim()) {
				case "GL_Code_Mapping_Master_GL" :
					List<GlCodeMasterDto> glCodeData = glCodeMasterRepo
							.findByFileTypeAndIsActiveTrue(fileType).stream()
							.map(e -> {
								GlCodeMasterDto dto = new GlCodeMasterDto();
								dto.setId(e.getId());
								dto.setCgstTaxGlCode(e.getCgstTaxGlCode());
								dto.setSgstTaxGlCode(e.getSgstTaxGlCode());
								dto.setIgstTaxGlCode(e.getIgstTaxGlCode());
								dto.setUgstTaxGlCode(e.getUgstTaxGlCode());
								dto.setCompensationCessGlCode(
										e.getCompensationCessGlCode());
								dto.setKeralaCessGlCode(
										e.getKeralaCessGlCode());
								dto.setRevenueGls(e.getRevenueGls());
								dto.setExpenceGls(e.getExpenceGls());
								dto.setExchangeRate(e.getExchangeRate());
								//dto.setDiffGl(e.getDiffGl());
								dto.setExportGl(e.getExportGl());
								dto.setForexGlsPor(e.getForexGlsPor());
								dto.setTaxableAdvanceLiabilityGls(
										e.getTaxableAdvanceLiabilityGls());
								dto.setNonTaxableAdvanceLiabilityGls(
										e.getNonTaxableAdvanceLiabilityGls());
								dto.setCcAndStGls(e.getCcAndStGls());
								dto.setUnbilledRevenueGls(
										e.getUnbilledRevenueGls());
								dto.setBankAccGls(e.getBankAccGls());
								dto.setInputTaxGls(e.getInputTaxGls());
								dto.setFixedAssetGls(e.getFixedAssetGls());
								return dto;
							}).collect(Collectors.toList());
					resultData = gson.toJsonTree(glCodeData);
					break;

				case "Document_type" :
					List<GlDocTypeMasterDto> docTypeData = glDocTypeMasterRepository
							.findByFileTypeAndIsActiveTrue(fileType).stream()
							.map(e -> {
								GlDocTypeMasterDto dto = new GlDocTypeMasterDto();
								dto.setId(e.getId());
								dto.setDocType(e.getDocType());
								dto.setDocTypeMs(e.getDocTypeMs());
								return dto;
							}).collect(Collectors.toList());
					resultData = gson.toJsonTree(docTypeData);
					break;

				case "Supply_Type" :
					List<GlMasterSupplyTypeDto> supplyTypeData = glMasterSupplyTypeRepository
							.findByFileTypeAndIsActiveTrue(fileType).stream()
							.map(e -> {
								GlMasterSupplyTypeDto dto = new GlMasterSupplyTypeDto();
								dto.setId(e.getId());
								dto.setSupplyTypeReg(e.getSupplyTypeReg());
								dto.setSupplyTypeMs(e.getSupplyTypeMs());
								return dto;
							}).collect(Collectors.toList());
					resultData = gson.toJsonTree(supplyTypeData);
					break;

				case "Tax_code" :
					List<GlTaxCodeMasterDto> taxCodeData = glTaxCodeMasterRepo
							.findByFileTypeAndIsActiveTrue(fileType).stream()
							.map(e -> {
								GlTaxCodeMasterDto dto = new GlTaxCodeMasterDto();
								dto.setId(e.getId());
								dto.setTransactionTypeGl(
										e.getTransactionTypeGl());
								dto.setTaxCodeDescriptionMs(
										e.getTaxCodeDescriptionMs());
								dto.setTaxTypeMs(e.getTaxTypeMs());
								dto.setEligibilityMs(e.getEligibilityMs());
								dto.setTaxRateMs(e.getTaxRateMs());
								return dto;
							}).collect(Collectors.toList());
					resultData = gson.toJsonTree(taxCodeData);
					break;

				case "Business_Unit_code" :
					List<GlBusinessPlaceMasterDto> businessUnitData = glBusinessPlaceMasterRepository
							.findByFileTypeAndIsActiveTrue(fileType).stream()
							.map(e -> {
								GlBusinessPlaceMasterDto dto = new GlBusinessPlaceMasterDto();
								dto.setId(e.getId());
								dto.setBusinessPlace(e.getBusinessPlace());
								dto.setBusinessArea(e.getBusinessArea());
								dto.setPlantCode(e.getPlantCode());
								dto.setProfitCentre(e.getProfitCentre());
								dto.setCostCentre(e.getCostCentre());
								dto.setGstin(e.getGstin());
								return dto;
							}).collect(Collectors.toList());
					resultData = gson.toJsonTree(businessUnitData);
					break;
					
				 case "GL Mapping":
		                List<GlCodeMappingMasterDto> mappingData = glCodeMappingMasterRepository
		                        .findByFileTypeAndIsActiveTrue(fileType).stream()
		                        .map(e -> {
		                            GlCodeMappingMasterDto dto = new GlCodeMappingMasterDto();
		                            dto.setId(e.getId());
		                            dto.setBaseHeader(e.getBaseHeaders());
		                            dto.setInputFileHeaders(e.getInputFileHeaders());
		                            return dto;
		                        }).collect(Collectors.toList());
		                resultData = gson.toJsonTree(mappingData);
		                break;

				default :
					throw new IllegalArgumentException(
							"Invalid fileType provided.");
			}

			// Handle No Data Found case
			if (resultData == null || (resultData.isJsonArray()
					&& resultData.getAsJsonArray().size() == 0)) {
				APIRespDto noDataDto = new APIRespDto("No Data Found",
						"No data available for the selected file type.");
				response.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				response.add("resp", gson.toJsonTree(noDataDto));
				return new ResponseEntity<>(response.toString(), HttpStatus.OK);
			}

			response.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			response.add("resp", resultData);
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto errorDto = new APIRespDto("Failed",
					"Error processing request: " + e.getMessage());
			response.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			response.add("resp", gson.toJsonTree(errorDto));
			LOGGER.error("Error retrieving master data", e);
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);
		}
	}

}
