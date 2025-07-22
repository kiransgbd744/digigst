package com.ey.advisory.controllers.anexure1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.Gstr1EinvSeriesCompEntity;
import com.ey.advisory.app.data.entities.client.Gstr1InvoiceFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AEinvSeriesCompEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AInvoiceFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AEinvSeriesCompRepo;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EinvSeriesCompRepo;
import com.ey.advisory.app.data.repositories.client.Gstr1InvoiceRepository;
import com.ey.advisory.app.data.services.anx1.Gstr1ProcessedRecordsFetchService;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Sasidhar Reddy
 *
 */
@RestController
public class Gstr1ProcessedController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ProcessedController.class);

	@Autowired
	@Qualifier("Gstr1ProcessedRecordsFetchService")
	Gstr1ProcessedRecordsFetchService gstr1ProcessedRecordsFetchService;

	@Autowired
	@Qualifier("Gstr1InvoiceRepository")
	private Gstr1InvoiceRepository gstr1InvoiceRepository;

	@Autowired
	private Gstr1EinvSeriesCompRepo gstr1EinvSeriesCompRepo;

	@Autowired
	@Qualifier("Gstr1AInvoiceRepository")
	private Gstr1AInvoiceRepository gstr1AInvoiceRepository;

	@Autowired
	private Gstr1AEinvSeriesCompRepo gstr1AEinvSeriesCompRepo;

	
	@RequestMapping(value = "/ui/gstr1ProcessedRecords", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The selected criteria for processed records is:->"
					+ requestObject.get("req"));
		}
		// Execute the service method and get the result.
		try {

			Gstr1ProcessedRecordsReqDto gstr1ProcessedRecordsReqDto = gson
					.fromJson(json, Gstr1ProcessedRecordsReqDto.class);

			SearchResult<Gstr1ProcessedRecordsRespDto> respDtos = gstr1ProcessedRecordsFetchService
					.find(gstr1ProcessedRecordsReqDto, null,
							Gstr1ProcessedRecordsRespDto.class);
			setInvSeriesDtls(respDtos.getResult(),gstr1ProcessedRecordsReqDto.getRetunPeriod(),
					gstr1ProcessedRecordsReqDto.getReturnType());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respDtos.getResult()));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Response data for given criteria for processed records is :->"
								+ resp.toString());
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr1 processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private void setInvSeriesDtls(
			List<? extends Gstr1ProcessedRecordsRespDto> list,
			String retPeriod, String returnType) {

		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put(APIConstants.SUCCESS, "Completed");
		statusMap.put(APIConstants.INPROGRESS, "InProgress");
		statusMap.put(APIConstants.FAILED, "Failed");
		statusMap.put(APIConstants.INITIATED, "Initiated");
		statusMap.put(APIConstants.SUCCESS_WITH_NO_DATA, "SUCCESS_WITH_NO_DATA");
		statusMap.put("JOB_POSTED", "JOB_POSTED");
		
		if (!Strings.isNullOrEmpty(returnType)
				&& APIConstants.GSTR1A.equalsIgnoreCase(returnType)) {
			for (Gstr1ProcessedRecordsRespDto dto : list) {
				Optional<Gstr1AInvoiceFileUploadEntity> isAvailable = gstr1AInvoiceRepository
						.findTop1BySgstinAndReturnPeriodAndIsDeleteFalseOrderByCreatedOnDesc(
								dto.getGstin(), retPeriod);
				if (isAvailable.isPresent()) {
					if ("C".equalsIgnoreCase(
							isAvailable.get().getDataOriginType())) {
						Optional<Gstr1AEinvSeriesCompEntity> isCompDtlsAva = gstr1AEinvSeriesCompRepo
								.findByGstinAndReturnPeriodAndIsActiveTrue(
										dto.getGstin(), retPeriod);
						if (isCompDtlsAva.isPresent()) {
							dto.setInvTimeStamp(EYDateUtil.fmt(EYDateUtil
									.toISTDateTimeFromUTC(isCompDtlsAva.get()
											.getModifiedOn())));
							dto.setInvSerStatus("DigiGST compute -"
									+ statusMap.get(isCompDtlsAva.get()
											.getRequestStatus()));
						} else {
							dto.setInvSerStatus(APIConstants.NOT_INITIATED);
						}
					} else {
						dto.setInvSerStatus("File Upload");
						dto.setInvTimeStamp(
								EYDateUtil.fmt(EYDateUtil.toISTDateTimeFromUTC(
										isAvailable.get().getCreatedOn())));
					}
				} else {
					dto.setInvSerStatus(APIConstants.NOT_INITIATED);
				}
			}

		} else {
		for (Gstr1ProcessedRecordsRespDto dto : list) {
			Optional<Gstr1InvoiceFileUploadEntity> isAvailable = gstr1InvoiceRepository
					.findTop1BySgstinAndReturnPeriodAndIsDeleteFalseOrderByCreatedOnDesc(
							dto.getGstin(), retPeriod);
			if (isAvailable.isPresent()) {
				if ("C".equalsIgnoreCase(
						isAvailable.get().getDataOriginType())) {
					Optional<Gstr1EinvSeriesCompEntity> isCompDtlsAva = gstr1EinvSeriesCompRepo
							.findByGstinAndReturnPeriodAndIsActiveTrue(
									dto.getGstin(), retPeriod);
					if (isCompDtlsAva.isPresent()) {
						dto.setInvTimeStamp(
								EYDateUtil.fmt(EYDateUtil.toISTDateTimeFromUTC(
										isCompDtlsAva.get().getModifiedOn())));
						dto.setInvSerStatus("DigiGST compute -" + statusMap
								.get(isCompDtlsAva.get().getRequestStatus()));
					} else {
						dto.setInvSerStatus(APIConstants.NOT_INITIATED);
					}
				} else {
					dto.setInvSerStatus("File Upload");
					dto.setInvTimeStamp(
							EYDateUtil.fmt(EYDateUtil.toISTDateTimeFromUTC(
									isAvailable.get().getCreatedOn())));
				}
			} else {
				dto.setInvSerStatus(APIConstants.NOT_INITIATED);
			}
		}
		}
	}
}
