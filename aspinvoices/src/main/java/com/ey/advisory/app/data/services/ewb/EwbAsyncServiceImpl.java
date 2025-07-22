/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.EwbStatus;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.ewb.api.GenerateEwayBill;
import com.ey.advisory.ewb.app.api.APIError;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.EwbResponseXmlDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("EwbAsyncServiceImpl")
public class EwbAsyncServiceImpl implements EwbAsyncService {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("EwbRequestConverter")
	private EwbRequestConverter ewbConverter;

	@Autowired
	@Qualifier("GenerateEwayBillImpl")
	private GenerateEwayBill generateEwb;

	@Autowired
	private DestinationConnectivity destinationConn;

	@Override
	public void generateEwayBill(Long id) {
		APIResponse response = null;
		OutwardTransDocument doc = null;
		EwbResponseDto resp = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("generateEwBill Method Begin ,fetching the"
						+ " doc header details from db for id " + id);
			}
			Optional<OutwardTransDocument> transDocuments = docrepo
					.findById(id);

			if (!transDocuments.isPresent()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("generateEwBill  ,no invoice available");
				}
				throw new AppException("No Invoice Found For Requested Params");
			}
			doc = transDocuments.get();
			resp = processGenerateEwb(doc);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			APIError err = new APIError();
			if (e instanceof SocketException) {
				err.setErrorCode("SOCTOUT");
			}
			err.setErrorCode("OTHEXP");
			err.setErrorDesc(e.getMessage());
			response = new APIResponse();
			response.addError(err);
			EwbResponseXmlDto errResp = createErrorResponse(Arrays.asList(err), doc);
			resp.setErrorCode(errResp.getErrorCode()); 
			resp.setErrorCode(errResp.getErrorDesc()); 
			LOGGER.error(e.getMessage(), e);
			throw new AppException(e);
		} finally {
			callErp(resp, doc);
		}

	}

	private EwbResponseXmlDto convertToXml(EwbResponseDto resp,
			OutwardTransDocument doc) {
		EwbResponseXmlDto xmlDto = new EwbResponseXmlDto();
		xmlDto.setAlert(resp.getAlert());
		xmlDto.setEwayBillDate(resp.getEwayBillDate());
		xmlDto.setEwayBillNo(resp.getEwayBillNo());
		xmlDto.setValidUpto(resp.getValidUpto());
		xmlDto.setErrorCode(resp.getErrorCode());
		xmlDto.setErrorDesc(resp.getErrorDesc());
		xmlDto.setDocDate(doc.getDocDate());
		xmlDto.setDocNum(doc.getDocNo());
		xmlDto.setDocType(doc.getDocType());
		return xmlDto;
	}

	private void updateEwayBillNo(Long id, String ewbNo, LocalDateTime ewbDate,
			Integer status, Integer processingStatus) {
		docrepo.updateEwayBillNo(id, Long.valueOf(ewbNo), ewbDate, status,processingStatus);

	}

	private APIResponse callEwayBillApi(EwayBillRequestDto ewbReqDto,
			String sgstin) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info(ewbReqDto.toString());
			}
			return generateEwb.generateEwbill(ewbReqDto, sgstin);
		} catch (Exception e) {
			LOGGER.error("Exception Occured in generateEwbill", e);
			throw new AppException("Exception Occured in generateEwbill",e);
		}

	}

	private void pushToErp(EwbResponseXmlDto erpResp) {
		try {
			ByteArrayOutputStream itemOut = new ByteArrayOutputStream();
			JAXBContext itemContext = JAXBContext
					.newInstance(EwbResponseXmlDto.class);
			Marshaller itemMarshr = itemContext.createMarshaller();
			itemMarshr.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			itemMarshr.marshal(erpResp, itemOut);
			String xml = itemOut.toString();

			if (xml != null && xml.length() > 0) {
				xml = xml.substring(xml.indexOf('\n') + 1);
			}

			String header = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
					+ "xmlns:urn='urn:sap-com:document:sap:rfc:functions'><soapenv:Header/>"
					+ "<soapenv:Body><urn:ZEY_EWAY_RES><GT_DATA>";
			String footer = "</GT_DATA></urn:ZEY_EWAY_RES></soapenv:Body></soapenv:Envelope>";
			if (xml != null) {
				xml = header + xml + footer;
			}

			String destinationName = "Ewb_ReverseIntegration";
			if (xml != null) {
				LOGGER.debug(
						"Before Posting the  Ewb Details {} to ERP with xml {}",
						erpResp, xml);
				//destinationConn.post(destinationName, xml,null);
				LOGGER.debug("Ewb Details {}  has been posted Successfully ",
						erpResp);
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured while pushing Ewb Details to ERP",
					e);
			throw new AppException(e);
		}
	}

	private void callErp(EwbResponseDto response, OutwardTransDocument doc) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Response :" + response.toString());
		}
		EwbResponseXmlDto erpResp = convertToXml(response, doc);
		pushToErp(erpResp);

	}

	private EwbResponseXmlDto createErrorResponse(List<APIError> apiErrorList,
			OutwardTransDocument doc) {

		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < apiErrorList.size(); i++) {
			if (!Strings.isNullOrEmpty(apiErrorList.get(i).getErrorCode()))
				errorCode = errorCode + (i + 1) + ") "
						+ apiErrorList.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(apiErrorList.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ apiErrorList.get(i).getErrorDesc() + " ";
		}
		EwbResponseXmlDto errResp = new EwbResponseXmlDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorDesc(errorDesc);
		if (doc != null) {
			errResp.setDocNum(doc.getDocNo());
			errResp.setDocType(doc.getDocType());
			errResp.setDocDate(doc.getDocDate());
		}

		return errResp;
	}

	public EwbResponseDto processGenerateEwb(OutwardTransDocument doc) {

		APIResponse response = null;
		EwbResponseDto resp = new EwbResponseDto();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("generateEwBill  ,convert the doc header detail "
						+ "into ewaybill request dto");
			}
			EwayBillRequestDto ewbRequestDto = ewbConverter.convert(doc);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("generateEwBill  ,converted  the doc header detail "
						+ "into ewaybill request dto");
			}
			String sgstin = doc.getSgstin();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info(
						"generateEwBill  ,generating irn for given request");
			}
			response = callEwayBillApi(ewbRequestDto, sgstin);

			if (response.isSuccess()) {
				String jsonResp = "";
				Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
				jsonResp = response.getResponse();
				resp = gson.fromJson(jsonResp, EwbResponseDto.class);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info(
							"Ewaybill Generation successfull now about to update "
									+ "the db with new irn {} and ackDate {}",
							resp.getEwayBillNo(), resp.getEwayBillDate());
				}
			} else {
				List<APIError> apiErrorList = response.getErrors();
				EwbResponseXmlDto errResp = createErrorResponse(apiErrorList,
						doc);
				resp.setErrorCode(errResp.getErrorCode());
				resp.setErrorDesc(errResp.getErrorDesc());
			}
			executeDbUpdates(resp, doc);
			return resp;
		} catch (Exception e) {
			LOGGER.error("Exception occured while executng API", e);
			throw new AppException("Exception occured while executng API", e);
		}
	}

	private void executeDbUpdates(EwbResponseDto resp,
			OutwardTransDocument doc) {
		if (!Strings.isNullOrEmpty(resp.getEwayBillNo())) {
			updateEwayBillNo(doc.getId(), resp.getEwayBillNo(),
					resp.getEwayBillDate(), EwbStatus.EWB_ACTIVE
					.getEwbStatusCode(),
					EwbProcessingStatus.UPDATED_PARTB
					.getEwbProcessingStatusCode());
			saveEwbEntity(resp, doc.getId());
		} else{
			updateFailureResp(doc.getId(), resp.getErrorCode(),
					resp.getErrorDesc(), 
					EwbStatus.GENERATION_ERROR.getEwbStatusCode(),
					EwbProcessingStatus.GENERATION_ERROR
					.getEwbProcessingStatusCode());
		}
	}

	private void updateFailureResp(Long id, String errorCode, String errorDesc,
			Integer status, Integer processingStatus) {
		
		boolean isDupEwb = false;
		if(errorCode.contains("604") || 
				errorCode.contains("312")){
			LOGGER.debug("Ewb is already generated or Ewb is already cancelled");
			isDupEwb = true;
		}

		docrepo.updateErrorEwbResponseById(id, errorCode, errorDesc, status,
				isDupEwb ? EwbProcessingStatus.ERROR_CANCELLATION
						.getEwbProcessingStatusCode() : 
						   EwbProcessingStatus.GENERATION_ERROR
						.getEwbProcessingStatusCode(),
				AspInvoiceStatus.ASP_ERROR.getAspInvoiceStatusCode());
	}

	private void saveEwbEntity(EwbResponseDto resp, Long id) {
		EwbEntity ewbEntity = new EwbEntity();

		ewbEntity.setAlert(resp.getAlert());
		ewbEntity.setCreatedBy("SYSTEM");
		ewbEntity.setCreatedOn(LocalDateTime.now());
		ewbEntity.setDocHeaderId(id);
		ewbEntity.setEwbDate(resp.getEwayBillDate());
		ewbEntity.setEwbNum(resp.getEwayBillNo());
		ewbEntity.setModifiedBy("SYSTEM");
		ewbEntity.setModifiedOn(LocalDateTime.now());
		ewbEntity.setValidUpto(resp.getValidUpto());
		ewbRepository.save(ewbEntity);

	}
}
