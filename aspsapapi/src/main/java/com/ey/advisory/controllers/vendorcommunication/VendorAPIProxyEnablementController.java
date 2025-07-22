package com.ey.advisory.controllers.vendorcommunication;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorApiUploadStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterErrorReportEntityRepository;
import com.ey.advisory.app.itcmatching.vendorupload.ItcMatchingVendorDataFileUpld;
import com.ey.advisory.app.itcmatching.vendorupload.VendorAPIListDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorAPIPushDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorAPIPushListDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorApiUploadStatusEntity;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterErrorReportEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran s
 *
 */
@RestController
@Slf4j
public class VendorAPIProxyEnablementController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("VendorApiUploadStatusRepository")
	VendorApiUploadStatusRepository apiStatusRepository;

	@Autowired
	@Qualifier("ItcMatchingVendorDataFileUpld")
	private ItcMatchingVendorDataFileUpld vendorDataFileUpload;

	@Autowired
	@Qualifier("VendorMasterErrorReportEntityRepository")
	private VendorMasterErrorReportEntityRepository vendorMasterErrorReportEntityRepository;

	@PostMapping(value = "/api/saveVendorMasterXml", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> saveVendorMaster(@RequestBody String xmlString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		VendorApiUploadStatusEntity apiStatus = null;

		 String xmlResponse=null;
		    VendorAPIListDto reqDto = new VendorAPIListDto();
		    int errorRecords = 0;
		try {

		

		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		         factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		        		        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		        		        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		        		        factory.setXIncludeAware(false);
		        		        factory.setExpandEntityReferences(false);
		        DocumentBuilder builder = factory.newDocumentBuilder();
		        Document document = builder.parse(new InputSource(new StringReader(xmlString)));

		        Element root = document.getDocumentElement();
		        NodeList vendorDetailsList = root.getElementsByTagName("vendorDetails");


		        if (vendorDetailsList.getLength() > 0) {
		            Element vendorDetails = (Element) vendorDetailsList.item(0);

		            // Create a new instance of VendorAPIPushListDto
		            VendorAPIPushListDto pushListDto = new VendorAPIPushListDto();

		            // Set values for each field of pushListDto
		            pushListDto.setRecipientPAN(getElementValue(vendorDetails, "recipientPAN"));
		            pushListDto.setVendorGstin(getElementValue(vendorDetails, "vendorGstin"));
		            pushListDto.setVendorPAN(getElementValue(vendorDetails, "vendorPAN"));
		            pushListDto.setSupplierCode(getElementValue(vendorDetails, "supplierCode"));
		            pushListDto.setVendorName(getElementValue(vendorDetails, "vendorName"));
		            pushListDto.setVendPrimEmailId(getElementValue(vendorDetails, "vendPrimEmailId"));
		            pushListDto.setVendorContactNumber(getElementValue(vendorDetails, "vendorContactNo"));
		            pushListDto.setVendorEmailId1(getElementValue(vendorDetails, "vendorEmailId1"));
		            pushListDto.setVendorEmailId2(getElementValue(vendorDetails, "vendorEmailId2"));
		            pushListDto.setVendorEmailId3(getElementValue(vendorDetails, "vendorEmailId3"));
		            pushListDto.setVendorEmailId4(getElementValue(vendorDetails, "vendorEmailId4"));
		            pushListDto.setRecipientEmailId1(getElementValue(vendorDetails, "recipientEmailId1"));
		            pushListDto.setRecipientEmailId2(getElementValue(vendorDetails, "recipientEmailId2"));
		            pushListDto.setRecipientEmailId3(getElementValue(vendorDetails, "recipientEmailId3"));
		            pushListDto.setRecipientEmailId4(getElementValue(vendorDetails, "recipientEmailId4"));
		            pushListDto.setRecipientEmailId5(getElementValue(vendorDetails, "recipientEmailId5"));
		            pushListDto.setVendorType(getElementValue(vendorDetails, "vendorType"));
		            pushListDto.setHsn(getElementValue(vendorDetails, "hsn"));
		            pushListDto.setVendorRiskCategory(getElementValue(vendorDetails, "vendRiskCategory"));
		            pushListDto.setVendorPaymentTerms(getElementValue(vendorDetails, "vendPaymentTerms"));
		            pushListDto.setVendorRemarks(getElementValue(vendorDetails, "vendorRemarks"));
		            pushListDto.setApprovalStatus(getElementValue(vendorDetails, "approvalStatus"));
		            pushListDto.setExcludeVendorRemarks(getElementValue(vendorDetails, "excludVendRemarks"));
		            pushListDto.setIsVendorCom(getElementValue(vendorDetails, "isVendorCom"));
		            pushListDto.setIsExcludeVendor(getElementValue(vendorDetails, "isExcludeVendor"));
		            pushListDto.setIsNonComplaintCom(getElementValue(vendorDetails, "isNonComplaintCom"));
		            pushListDto.setIsCreditEligibility(getElementValue(vendorDetails, "isCredEligibility"));
		            pushListDto.setIsDelete(getElementValue(vendorDetails, "isDelete"));

		            // Add the populated pushListDto to the vendorData list
		            reqDto.getVendorData().add(pushListDto);
		        }
		    
			/* catch (Exception ex) {
				LOGGER.error("Error while parsing the request-{}",
						ex);
				throw new AppException("Invalid request payload");
			}*/

			
			if (reqDto.getVendorData() == null
					|| reqDto.getVendorData().isEmpty()) {
				String msg = "Vendor Data cannot be null";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			apiStatus = logRequestPayload(xmlString, true);

			List<VendorAPIPushListDto> vendorData = reqDto.getVendorData();
			String refId = apiStatus.getRefId();

			List<Object[]> req = new ArrayList<>();

			for (VendorAPIPushListDto dto : vendorData) {

				Object[] obj = new Object[28];
				convertDtoToObj(obj, dto);
				req.add(obj);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("List of objects created -{}", req);
			}

			vendorDataFileUpload.validation(req, null, refId,null);

			Optional<VendorApiUploadStatusEntity> errorCount = apiStatusRepository
					.findByRefId(refId);
			if(errorCount.isPresent()){
			 errorRecords = errorCount.get().getError();
			}
			if (errorRecords > 0) {

				List<VendorMasterErrorReportEntity> errorList = vendorMasterErrorReportEntityRepository
						.findByRefId(apiStatus.getRefId());

				List<VendorMasterErrorReportEntity> errors = errorList.stream()
						.filter(eachObject -> Objects
								.nonNull(eachObject.getError())
								&& !Objects.equals("null",
										eachObject.getError()))
						.collect(Collectors.toList());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("error records are --> {}", errors);
				}

				List<VendorAPIPushDto> errorDataList = errors.stream()
						.map(o -> convert(o))
						.collect(Collectors.toCollection(ArrayList::new));
				//respBody = gson.toJsonTree(errorDataList);
	             xmlResponse = convertErrorDataListToXml(errorDataList);

			} else {
				
				StringBuilder xmlBuilder = new StringBuilder();
				xmlBuilder.append("<response>");
				xmlBuilder.append("<refId>").append(apiStatus.getRefId()).append("</refId>");
				xmlBuilder.append("<msg>Vendor Data has been processed successfully</msg>");
				xmlBuilder.append("</response>");

				// Convert StringBuilder to String
				 xmlResponse = xmlBuilder.toString();
			}

			
			StringBuilder xmlBuilder = new StringBuilder();
			xmlBuilder.append("<response>");
			xmlBuilder.append("<hdr>");
			xmlBuilder.append(APIRespDto.createSuccessResp().getStatus());
			xmlBuilder.append("</hdr>");
			if (xmlResponse != null) {
			    xmlBuilder.append("<resp>");
			    xmlBuilder.append(xmlResponse); // Add the XML response here
			    xmlBuilder.append("</resp>");
			} else {
			    xmlBuilder.append("<resp>");
			    // Assuming resp is already in XML format
			    xmlBuilder.append(resp);
			    xmlBuilder.append("</resp>");
			}
			xmlBuilder.append("</response>");

			// Convert StringBuilder to String
			 xmlResponse = xmlBuilder.toString();

			// Save the XML response to the database
			if(errorCount.isPresent()){
			errorCount.get().setRespPayload(GenUtil.convertStringToClob(xmlResponse));
			apiStatusRepository.save(errorCount.get());
			}

			// Return the XML response directly
			return new ResponseEntity<>(xmlResponse, HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Fetching vendor api Status ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("errMsg", gson.toJsonTree(ex.getMessage()));

			apiStatus = logRequestPayload(xmlString, false);
			apiStatus.setError(0);
			apiStatus.setInformation(0);
			apiStatus.setProcessed(0);
			apiStatus.setTotal(0);
			apiStatus.setRespPayload(
					GenUtil.convertStringToClob(resp.toString()));
			apiStatusRepository.save(apiStatus);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

		
	
		
	}


	public String convertErrorDataListToXml(List<VendorAPIPushDto> errorDataList) {
	    StringBuilder xmlBuilder = new StringBuilder();
	    xmlBuilder.append("<errorDataList>");

	    for (VendorAPIPushDto dto : errorDataList) {
	        xmlBuilder.append("<VendorAPIPushDto>");
	        xmlBuilder.append("<error>").append(dto.getError()).append("</error>");
	        xmlBuilder.append("<refId>").append(dto.getRefId()).append("</refId>");
	        xmlBuilder.append("<recipientPAN>").append(dto.getRecipientPAN()).append("</recipientPAN>");
	        xmlBuilder.append("<vendorGstin>").append(dto.getVendorGstin()).append("</vendorGstin>");
	        xmlBuilder.append("<vendorPAN>").append(dto.getVendorPAN()).append("</vendorPAN>");
	        xmlBuilder.append("<SupplierCode>").append(dto.getSupplierCode()).append("</SupplierCode>");
	        xmlBuilder.append("<vendorName>").append(dto.getVendorName()).append("</vendorName>");
	        xmlBuilder.append("<vendPrimEmailId>").append(dto.getVendPrimEmailId()).append("</vendPrimEmailId>");
	        xmlBuilder.append("<vendorContactNo>").append(dto.getVendorContactNumber()).append("</vendorContactNo>");
	        xmlBuilder.append("<vendorEmailId1>").append(dto.getVendorEmailId1()).append("</vendorEmailId1>");
	        xmlBuilder.append("<vendorEmailId2>").append(dto.getVendorEmailId2()).append("</vendorEmailId2>");
	        xmlBuilder.append("<vendorEmailId3>").append(dto.getVendorEmailId3()).append("</vendorEmailId3>");
	        xmlBuilder.append("<vendorEmailId4>").append(dto.getVendorEmailId4()).append("</vendorEmailId4>");
	        xmlBuilder.append("<recipientEmailId1>").append(dto.getRecipientEmailId1()).append("</recipientEmailId1>");
	        xmlBuilder.append("<recipientEmailId2>").append(dto.getRecipientEmailId2()).append("</recipientEmailId2>");
	        xmlBuilder.append("<recipientEmailId3>").append(dto.getRecipientEmailId3()).append("</recipientEmailId3>");
	        xmlBuilder.append("<recipientEmailId4>").append(dto.getRecipientEmailId4()).append("</recipientEmailId4>");
	        xmlBuilder.append("<recipientEmailId5>").append(dto.getRecipientEmailId5()).append("</recipientEmailId5>");
	        xmlBuilder.append("<vendorType>").append(dto.getVendorType()).append("</vendorType>");
	        xmlBuilder.append("<hsn>").append(dto.getHsn()).append("</hsn>");
	        xmlBuilder.append("<vendRiskCategory>").append(dto.getVendorRiskCategory()).append("</vendRiskCategory>");
	        xmlBuilder.append("<vendPaymentTerms>").append(dto.getVendorPaymentTerms()).append("</vendPaymentTerms>");
	        xmlBuilder.append("<vendorRemarks>").append(dto.getVendorRemarks()).append("</vendorRemarks>");
	        xmlBuilder.append("<approvalStatus>").append(dto.getApprovalStatus()).append("</approvalStatus>");
	        xmlBuilder.append("<excludVendRemarks>").append(dto.getExcludeVendorRemarks()).append("</excludVendRemarks>");
	        xmlBuilder.append("<isVendorCom>").append(dto.getIsVendorCom()).append("</isVendorCom>");
	        xmlBuilder.append("<isExcludeVendor>").append(dto.getIsExcludeVendor()).append("</isExcludeVendor>");
	        xmlBuilder.append("<isNonComplaintCom>").append(dto.getIsNonComplaintCom()).append("</isNonComplaintCom>");
	        xmlBuilder.append("<isCredEligibility>").append(dto.getIsCreditEligibility()).append("</isCredEligibility>");
	        xmlBuilder.append("<isDelete>").append(dto.getIsDelete()).append("</isDelete>");
	        xmlBuilder.append("</VendorAPIPushDto>");
	    }

	    xmlBuilder.append("</errorDataList>");
	    return xmlBuilder.toString();
	}


	// Helper method to get element value from the XML
	private String getElementValue(Element parentElement, String tagName) {
	    NodeList nodeList = parentElement.getElementsByTagName(tagName);
	    if (nodeList.getLength() > 0) {
	        return nodeList.item(0).getTextContent();
	    }
	    return null;
	}


	private static void convertDtoToObj(Object[] obj,
			VendorAPIPushListDto reqDto) {
		obj[0] = reqDto.getRecipientPAN();
		obj[1] = reqDto.getVendorPAN();
		obj[2] = reqDto.getVendorGstin();

		obj[3] = reqDto.getSupplierCode();
		obj[4] = reqDto.getVendorName();
		obj[5] = reqDto.getVendPrimEmailId();
		obj[6] = reqDto.getVendorContactNumber();
		obj[7] = reqDto.getVendorEmailId1();
		obj[8] = reqDto.getVendorEmailId2();
		obj[9] = reqDto.getVendorEmailId3();
		obj[10] = reqDto.getVendorEmailId4();
		obj[11] = reqDto.getRecipientEmailId1();
		obj[12] = reqDto.getRecipientEmailId2();
		obj[13] = reqDto.getRecipientEmailId3();
		obj[14] = reqDto.getRecipientEmailId4();
		obj[15] = reqDto.getRecipientEmailId5();
		obj[16] = reqDto.getVendorType();
		obj[17] = reqDto.getHsn();
		obj[18] = reqDto.getVendorRiskCategory();
		obj[19] = reqDto.getVendorPaymentTerms();
		obj[20] = reqDto.getVendorRemarks();
		obj[21] = reqDto.getApprovalStatus();
		obj[22] = reqDto.getExcludeVendorRemarks();
		obj[23] = reqDto.getIsVendorCom();
		obj[24] = reqDto.getIsExcludeVendor();
		obj[25] = reqDto.getIsNonComplaintCom();
		obj[26] = reqDto.getIsCreditEligibility();
		obj[27] = reqDto.getIsDelete();
	}

	private VendorAPIPushDto convert(VendorMasterErrorReportEntity entity) {

		VendorAPIPushDto obj = new VendorAPIPushDto();

		obj.setError(entity.getError());
		obj.setRefId(entity.getRefId());
		obj.setRecipientPAN(entity.getRecipientPAN());
		obj.setVendorGstin(entity.getVendorGstin());
		obj.setVendorPAN(entity.getVendorPAN());
		obj.setSupplierCode(entity.getVendorCode());
		obj.setVendorName(entity.getVendorName());
		obj.setVendPrimEmailId(entity.getVendPrimEmailId());
		obj.setVendorContactNumber(entity.getVendorContactNumber());
		obj.setVendorEmailId1(entity.getVendorEmailId1());
		obj.setVendorEmailId2(entity.getVendorEmailId2());
		obj.setVendorEmailId3(entity.getVendorEmailId3());
		obj.setVendorEmailId4(entity.getVendorEmailId4());
		obj.setRecipientEmailId1(entity.getRecipientEmailId1());
		obj.setRecipientEmailId2(entity.getRecipientEmailId2());
		obj.setRecipientEmailId3(entity.getRecipientEmailId3());
		obj.setRecipientEmailId4(entity.getRecipientEmailId4());
		obj.setRecipientEmailId5(entity.getRecipientEmailId5());
		obj.setVendorType(entity.getVendorType());
		obj.setHsn(entity.getHsn());
		obj.setVendorRiskCategory(entity.getVendorRiskCategory());
		obj.setVendorPaymentTerms(entity.getVendorPaymentTerms());
		obj.setVendorRemarks(entity.getVendorRemarks());
		obj.setApprovalStatus(entity.getApprovalStatus());
		obj.setExcludeVendorRemarks(entity.getExcludeVendorRemarks());
		obj.setIsVendorCom(entity.getIsVendorCom());
		obj.setIsExcludeVendor(entity.getIsExcludeVendor());
		obj.setIsNonComplaintCom(entity.getIsNonComplaintCom());
		obj.setIsCreditEligibility(entity.getIsCreditEligibility());
		obj.setIsDelete(entity.getIsDelete());
		return obj;

	}



	

	private VendorApiUploadStatusEntity logRequestPayload(String xmlString,
			boolean value) {
		VendorApiUploadStatusEntity apiStatus = new VendorApiUploadStatusEntity();

		UUID uuid = UUID.randomUUID();
		apiStatus.setRefId(uuid.toString());
		apiStatus.setCreatedBy("API");
		apiStatus.setCreatedOn(LocalDateTime.now());
		apiStatus.setReqPayload(
				GenUtil.convertStringToClob(xmlString.toString()));
		if (value) {
			apiStatus.setApiStatus(JobStatusConstants.UPLOADED);
		} else {
			apiStatus.setApiStatus("FAILED");
		}
		return apiStatusRepository.save(apiStatus);

	}

}
