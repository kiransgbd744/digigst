package com.ey.advisory.common;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.springframework.stereotype.Service;
import org.w3c.dom.NodeList;

import com.ey.advisory.app.data.business.dto.OtrdTransDocSoapHdrRq;
import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnSAPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnSoapRespDto;
import com.ey.advisory.einv.dto.GenerateEWBByIRNSoapReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnERPReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnSoapRespDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateIrnSAPResponseDto;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.EwbSAPResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SoapInvoiceDataExtractor implements InvoiceDataExecutor {

	@Override
	public JsonObject createCloudJson(List<ERPRequestLogEntity> soapPayloads) {

		JsonParser jsonParser = new JsonParser();
		JsonArray reqarr = new JsonArray();
		JsonObject finalObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			for (ERPRequestLogEntity entityData : soapPayloads) {
				JsonObject reqBodyObj = new JsonObject();
				JsonObject nicRespObj = new JsonObject();
				JsonObject nicReqObj = new JsonObject();
				String nicReqpData = null;
				String nicRespData = null;
				String reqBody = null;

				if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.GENEWB_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					nicReqpData = entityData.getNicReqPayload();
					SOAPMessage message = MessageFactory.newInstance()
							.createMessage(new MimeHeaders(),
									new ByteArrayInputStream(reqBody.getBytes(
											Charset.forName("UTF-8"))));
					SOAPEnvelope soapEnv = message.getSOAPPart().getEnvelope();
					SOAPBody soapBody = soapEnv.getBody();
					NodeList returnList = soapBody.getChildNodes();
					OutwardTransDocument hdrObj = convertXMLtoObj(returnList,
							"ewb-req");
					setPreDocDtls(hdrObj);
					reqBodyObj = (JsonObject) jsonParser
							.parse(gson.toJson(hdrObj));
					nicRespObj = (JsonObject) jsonParser.parse(nicRespData);
					nicReqObj = (JsonObject) jsonParser.parse(nicReqpData);
					EwbResponseDto nicResp = gson.fromJson(nicRespObj,
							EwbResponseDto.class);
					EwbSAPResponseDto cloudResp = BusinessCommonUtil
							.convertEWBNICResptoCloud(nicResp, hdrObj,
									nicReqObj);
					reqBodyObj.add("ewbDtls", gson.toJsonTree(cloudResp));
				} else if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.GENEWB_IRN_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					nicReqpData = entityData.getNicReqPayload();
					SOAPMessage message = MessageFactory.newInstance()
							.createMessage(new MimeHeaders(),
									new ByteArrayInputStream(reqBody.getBytes(
											Charset.forName("UTF-8"))));
					SOAPEnvelope soapEnv = message.getSOAPPart().getEnvelope();
					SOAPBody soapBody = soapEnv.getBody();
					NodeList returnList = soapBody.getChildNodes();

					GenerateEWBByIrnERPReqDto reqDto = convertGenIrnXMLtoObj(
							returnList, "genewbbyirn-req");
					reqBodyObj = (JsonObject) jsonParser
							.parse(gson.toJson(reqDto));
					nicRespObj = (JsonObject) jsonParser.parse(nicRespData);
					nicReqObj = (JsonObject) jsonParser.parse(nicReqpData);

					GenerateEWBByIrnSoapRespDto nicResp = gson.fromJson(
							nicRespData, GenerateEWBByIrnSoapRespDto.class);

					GenerateEWBByIrnResponseDto cnlERPRespDto = cnvtGenIrnSoaptoRestResp(
							nicResp);
					EwbSAPResponseDto cloudResp = BusinessCommonUtil
							.convertGENEWBIRNResptoCloud(cnlERPRespDto,
									nicReqObj);

					reqBodyObj.add("ewbDtls", gson.toJsonTree(cloudResp));
				} else if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.CANEWB_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					nicReqpData = entityData.getNicReqPayload();
					SOAPMessage message = MessageFactory.newInstance()
							.createMessage(new MimeHeaders(),
									new ByteArrayInputStream(reqBody.getBytes(
											Charset.forName("UTF-8"))));
					SOAPEnvelope soapEnv = message.getSOAPPart().getEnvelope();
					SOAPBody soapBody = soapEnv.getBody();
					NodeList returnList = soapBody.getChildNodes();

					CancelEwbReqDto hdrObj = convertCanEwbXMLtoObj(returnList,
							"ewb-req-list");
					reqBodyObj = (JsonObject) jsonParser
							.parse(gson.toJson(hdrObj));
					nicRespObj = (JsonObject) jsonParser.parse(nicRespData);
					nicReqObj = (JsonObject) jsonParser.parse(nicReqpData);
					CancelEwbResponseDto nicResp = gson.fromJson(
							nicRespObj.get("resp"), CancelEwbResponseDto.class);
					EwbSAPResponseDto cloudResp = BusinessCommonUtil
							.convertCANEWBNICResptoCloud(nicResp, nicReqObj);
					reqBodyObj.add("ewbDtls", gson.toJsonTree(cloudResp));

				} else if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.GENEINV_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					SOAPMessage message = MessageFactory.newInstance()
							.createMessage(new MimeHeaders(),
									new ByteArrayInputStream(reqBody.getBytes(
											Charset.forName("UTF-8"))));
					SOAPEnvelope soapEnv = message.getSOAPPart().getEnvelope();
					SOAPBody soapBody = soapEnv.getBody();
					NodeList returnList = soapBody.getChildNodes();
					OutwardTransDocument hdrObj = convertXMLtoObj(returnList,
							"einv-req");
					setPreDocDtls(hdrObj);

					reqBodyObj = (JsonObject) jsonParser
							.parse(gson.toJson(hdrObj));
					GenerateIrnResponseDto nicResp = gson.fromJson(nicRespData,
							GenerateIrnResponseDto.class);
					GenerateIrnSAPResponseDto cloudResp = BusinessCommonUtil
							.convertNICResptoCloud(nicResp);
					if (!Strings.isNullOrEmpty(nicResp.getEwbNo())) {
						EwbSAPResponseDto ewayBillDto = new EwbSAPResponseDto();
						ewayBillDto.setEwayBillNo(nicResp.getEwbNo());
						ewayBillDto.setEwayBillDate(nicResp.getEwbDt());
						ewayBillDto.setValidUpto(nicResp.getEwbValidTill());
						ewayBillDto.setTransporterID(hdrObj.getTransporterID());
						ewayBillDto.setTransportMode(hdrObj.getTransportMode());
						ewayBillDto
								.setTransportDocNo(hdrObj.getTransportDocNo());
						ewayBillDto.setTransportDocDate(
								hdrObj.getTransportDocDate());
						ewayBillDto.setVehicleNo(hdrObj.getVehicleNo());
						ewayBillDto.setVehicleType(hdrObj.getVehicleType());
						ewayBillDto.setAspDistance(
								Integer.valueOf(nicResp.getNicDistance()));
						ewayBillDto.setFromPlace(EyEwbCommonUtil.getFromPlace(
								hdrObj.getDispatcherLocation(),
								hdrObj.getSupplierLocation(),
								hdrObj.getDocCategory()));
						ewayBillDto.setFromPincode(EyEwbCommonUtil
								.getFromPinocode(hdrObj.getDispatcherPincode(),
										hdrObj.getSupplierPincode(),
										hdrObj.getDocCategory()));
						ewayBillDto.setFromState(
								hdrObj.getSupplierStateCode() != null
										? hdrObj.getSupplierStateCode() : null);

						reqBodyObj.add("ewbDtls", gson.toJsonTree(ewayBillDto));
					}
					reqBodyObj.add("eInvDtls", gson.toJsonTree(cloudResp));
				} else if (entityData.getApiType().equalsIgnoreCase(
						BusinessCriticalConstants.CANEINV_V3)) {
					nicRespData = entityData.getNicResPayload();
					reqBody = entityData.getReqPayload();
					nicReqpData = entityData.getNicReqPayload();

					SOAPMessage message = MessageFactory.newInstance()
							.createMessage(new MimeHeaders(),
									new ByteArrayInputStream(reqBody.getBytes(
											Charset.forName("UTF-8"))));
					SOAPEnvelope soapEnv = message.getSOAPPart().getEnvelope();
					SOAPBody soapBody = soapEnv.getBody();
					NodeList returnList = soapBody.getChildNodes();
					OutwardTransDocument hdrObj = convertXMLtoObj(returnList,
							"einv-req-list");
					reqBodyObj = (JsonObject) jsonParser
							.parse(gson.toJson(hdrObj));
					nicRespObj = (JsonObject) jsonParser.parse(nicRespData);
					nicReqObj = (JsonObject) jsonParser.parse(nicReqpData);

					CancelIrnSoapRespDto nicResp = gson.fromJson(nicRespData,
							CancelIrnSoapRespDto.class);
					CancelIrnERPResponseDto cnlERPRespDto = cnvtCnlEinvSoapToRestResp(
							nicResp);
					CancelIrnSAPResponseDto cloudResp = BusinessCommonUtil
							.convertCanNICResptoCloud(cnlERPRespDto,
									reqBodyObj);
					reqBodyObj.add("eInvDtls", gson.toJsonTree(cloudResp));
				}
				reqBodyObj.addProperty("dataOriginTypeCode", "B");
				reqarr.add(reqBodyObj);
			}
		} catch (Exception e) {
			String msg = "Exception occured while SOAP request";
			LOGGER.error(msg, e);
		}
		finalObj.add("req", reqarr);
		return finalObj;
	}

	private OutwardTransDocument convertXMLtoObj(NodeList list,
			String apiType) {
		OutwardTransDocument otTransDoc = null;
		try {
			for (int k = 0; k < list.getLength(); k++) {
				NodeList innerResultList = list.item(k).getChildNodes();
				for (int l = 0; l < innerResultList.getLength(); l++) {
					if (innerResultList.item(l).getNodeName()
							.equalsIgnoreCase(apiType)) {
						Source xmlSource = new DOMSource(
								innerResultList.item(l));
						Unmarshaller unmarshaller = JAXBContext
								.newInstance(OtrdTransDocSoapHdrRq.class)
								.createUnmarshaller();
						OtrdTransDocSoapHdrRq soRq = unmarshaller
								.unmarshal(xmlSource,
										OtrdTransDocSoapHdrRq.class)
								.getValue();
						otTransDoc = BusinessCommonUtil
								.convertERPSoapReqToOutwardTransDoc(soRq);
					}
				}
			}
		} catch (Exception ex) {
			String msg = "Exception occured while converting xml to object";
			LOGGER.error(msg, ex);
		}
		return otTransDoc;
	}

	private GenerateEWBByIrnERPReqDto convertGenIrnXMLtoObj(NodeList list,
			String apiType) {
		GenerateEWBByIrnERPReqDto genIrnReqDto = null;
		try {
			for (int k = 0; k < list.getLength(); k++) {
				NodeList innerResultList = list.item(k).getChildNodes();
				for (int l = 0; l < innerResultList.getLength(); l++) {
					if (innerResultList.item(l).getNodeName()
							.equalsIgnoreCase(apiType)) {
						Source xmlSource = new DOMSource(
								innerResultList.item(l));
						Unmarshaller unmarshaller = JAXBContext
								.newInstance(GenerateEWBByIRNSoapReqDto.class)
								.createUnmarshaller();
						GenerateEWBByIRNSoapReqDto soRq = unmarshaller
								.unmarshal(xmlSource,
										GenerateEWBByIRNSoapReqDto.class)
								.getValue();
						genIrnReqDto = BusinessCommonUtil
								.convertERPSoapReqToGenIrn(soRq);
					}
				}
			}
		} catch (Exception ex) {
			String msg = "Exception occured while converting xml to object for Generate Ewb By Irn";
			LOGGER.error(msg, ex);
		}
		return genIrnReqDto;
	}

	private static CancelEwbReqDto convertCanEwbXMLtoObj(NodeList list,
			String apiType) {
		CancelEwbReqDto canReqDto = null;
		try {
			for (int k = 0; k < list.getLength(); k++) {
				NodeList innerResultList = list.item(k).getChildNodes();
				for (int l = 0; l < innerResultList.getLength(); l++) {
					if (innerResultList.item(l).getNodeName()
							.equalsIgnoreCase(apiType)) {
						NodeList innerResultList2 = innerResultList.item(l)
								.getChildNodes();
						for (int m = 0; m < innerResultList2.getLength(); m++) {
							String innerNode = innerResultList2.item(m)
									.getNodeName();
							if (innerNode.equalsIgnoreCase("cancel-ewb-req")) {
								Source xmlSource = new DOMSource(
										innerResultList2.item(m));
								Unmarshaller unmarshaller = JAXBContext
										.newInstance(CancelEwbReqDto.class)
										.createUnmarshaller();
								CancelEwbReqDto soRq = unmarshaller
										.unmarshal(xmlSource,
												CancelEwbReqDto.class)
										.getValue();
								canReqDto = soRq;
							}
						}

					}
				}
			}
		} catch (Exception ex) {
			String msg = "Exception occured while converting xml to object for Cancel"
					+ " Ewb";
			LOGGER.error(msg, ex);
		}
		return canReqDto;
	}

	private CancelIrnERPResponseDto cnvtCnlEinvSoapToRestResp(
			CancelIrnSoapRespDto cnlSoapResp) {
		CancelIrnERPResponseDto cnlERPRespDto = new CancelIrnERPResponseDto();
		cnlERPRespDto.setCancelDate(cnlSoapResp.getCancelDate());
		cnlERPRespDto.setIrn(cnlSoapResp.getIrn());

		return cnlERPRespDto;
	}

	private GenerateEWBByIrnResponseDto cnvtGenIrnSoaptoRestResp(
			GenerateEWBByIrnSoapRespDto genEwbSoapRespDto) {
		GenerateEWBByIrnResponseDto respDto = new GenerateEWBByIrnResponseDto();
		respDto.setEwbDt(genEwbSoapRespDto.getEwbDt());
		respDto.setEwbNo(genEwbSoapRespDto.getEwbNo());
		respDto.setEwbValidTill(genEwbSoapRespDto.getEwbValidTill() != null
				? genEwbSoapRespDto.getEwbValidTill() : null);
		respDto.setNicDistance(genEwbSoapRespDto.getNicDistance());
		return respDto;
	}
	
	private static void setPreDocDtls(OutwardTransDocument hdr) {
		try {
			if (hdr.getPreDocDtls() != null && !hdr.getPreDocDtls().isEmpty()) {
				int preDocSize = hdr.getPreDocDtls().size();
				int lineItemSize = hdr.getLineItems().size();
				if (preDocSize >= lineItemSize) {
					for (int i = 0; i < lineItemSize; i++) {
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceDate(hdr.getPreDocDtls()
										.get(i).getPreceedingInvoiceDate());
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceNumber(hdr.getPreDocDtls()
										.get(i).getPreceedingInvoiceNumber());
					}
				} else if (preDocSize < lineItemSize && preDocSize != 1) {
					for (int i = 0; i < preDocSize; i++) {
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceDate(hdr.getPreDocDtls()
										.get(i).getPreceedingInvoiceDate());
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceNumber(hdr.getPreDocDtls()
										.get(i).getPreceedingInvoiceNumber());
					}
				} else if (preDocSize == 1) {
					for (int i = 0; i < lineItemSize; i++) {

						hdr.getLineItems().get(i)
								.setPreceedingInvoiceDate(hdr.getPreDocDtls()
										.get(0).getPreceedingInvoiceDate());
						hdr.getLineItems().get(i)
								.setPreceedingInvoiceNumber(hdr.getPreDocDtls()
										.get(0).getPreceedingInvoiceNumber());

					}
				} else {
					String msg = String.format(
							"PreDocDtls Condition didn't match, So skipping PreDocDtls Insertion");
					LOGGER.debug(msg);
				}
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while setting the preDocDtls for DocNo %s",
					hdr.getDocNo());
			LOGGER.error(msg, e);
		}

	}
}
