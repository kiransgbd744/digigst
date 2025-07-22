package com.ey.advisory.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.Anx1NewDataStatusEntity;
import com.ey.advisory.app.docs.dto.anx1.EInvoiceDataStatusDto;
import com.ey.advisory.app.services.docs.einvoice.EInvoiceDataStatusServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * 
 * @author Umesha.M
 *
 *         This method represents Data Status Screen
 */
@RestController
public class EInvoiceDataStatusControlller {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvoiceDataStatusControlller.class);

	@Autowired
	@Qualifier("EInvoiceDataStatusServiceImpl")
	private EInvoiceDataStatusServiceImpl einvoiceDataStatusServiceImpl;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	private BasicCommonSecParam basicCommonSecParam;

	/**
	 * 
	 * @param reqObj
	 * @return Get Data Status Screen
	 */
	@PostMapping(value = "/ui/getEInvoceDataStatusScreen", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEInvoceDataStatusScreen(
			@RequestBody String reqObj) {
		JsonObject requestObject = (new JsonParser()).parse(reqObj)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		// Execute the service method and get the result.
		try {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"/getEInvoceDataStatusScreen Request : "+reqObj);
			}

			DataStatusSearchReqDto searchParams = gson.fromJson(json,
					DataStatusSearchReqDto.class);
			/**
			 * Start - Set Data Security Attributes
			 */
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"DataStatus Adapter Filters Setting to Request BEGIN");
			}
			DataStatusSearchReqDto setDataSecurity = basicCommonSecParam
					.setDataSecuritySearchParamsApi(searchParams);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"DataStatus Adapter Filters Setting to Request END");
			}
			/**
			 * End - Set Data Security Attributes
			 */

			SearchResult<Anx1NewDataStatusEntity> searchResult = einvoiceDataStatusServiceImpl
					.find(setDataSecurity, null, Anx1NewDataStatusEntity.class);
			List<EInvoiceDataStatusDto> statusDtos = new ArrayList<>();
			if (searchResult.getResult() != null) {

				BigInteger totalRecords = BigInteger.ZERO;
				BigInteger processActive = BigInteger.ZERO;
				BigInteger processInActive = BigInteger.ZERO;
				BigInteger errorActive = BigInteger.ZERO;
				BigInteger errorInActive = BigInteger.ZERO;

				BigInteger invNA = BigInteger.ZERO;
				BigInteger invAspError = BigInteger.ZERO;
				BigInteger invAspProcessed = BigInteger.ZERO;
				BigInteger invIRNInProgress = BigInteger.ZERO;
				BigInteger invIrnError = BigInteger.ZERO;
				BigInteger invIrnProcessed = BigInteger.ZERO;
				BigInteger invIrnCancelled = BigInteger.ZERO;
				BigInteger einvVInfoError = BigInteger.ZERO;
				BigInteger einvNotOpted = BigInteger.ZERO;
				BigInteger einvId = BigInteger.ZERO;

				BigInteger eWbna = BigInteger.ZERO;
				BigInteger ewbErrorGigiGST = BigInteger.ZERO;
				BigInteger ewbProcessed = BigInteger.ZERO;
				BigInteger ewbInitiated = BigInteger.ZERO;
				BigInteger ewbError = BigInteger.ZERO;
				BigInteger ewbGenerated = BigInteger.ZERO;
				BigInteger ewbCancelled = BigInteger.ZERO;
				BigInteger ewbGeneratedOnErp = BigInteger.ZERO;
				BigInteger ewbNotGeneratedOnErp = BigInteger.ZERO;
				BigInteger ewbId = BigInteger.ZERO;

				BigInteger aspNA = BigInteger.ZERO;
				BigInteger aspError = BigInteger.ZERO;
				BigInteger aspProcess = BigInteger.ZERO;
				BigInteger aspSaveInitiated = BigInteger.ZERO;
				BigInteger aspSavedGstin = BigInteger.ZERO;
				BigInteger aspErrorsGstin = BigInteger.ZERO;
				BigInteger ewbNotOpted = BigInteger.ZERO;

				for (Anx1NewDataStatusEntity result : searchResult
						.getResult()) {
					totalRecords = totalRecords
							.add(result.getTotalRecords() != null
									? new BigInteger(String
											.valueOf(result.getTotalRecords()))
									: BigInteger.ZERO);
					processActive = processActive
							.add(result.getProcessedActive() != null
									? new BigInteger(String.valueOf(
											result.getProcessedActive()))
									: BigInteger.ZERO);
					processInActive = processInActive
							.add(result.getProcessedInactive() != null
									? new BigInteger(String.valueOf(
											result.getProcessedInactive()))
									: BigInteger.ZERO);
					errorActive = errorActive
							.add(result.getErrorActive() != null
									? new BigInteger(String
											.valueOf(result.getErrorActive()))
									: BigInteger.ZERO);
					errorInActive = errorInActive
							.add(result.getErrorInactive() != null
									? new BigInteger(String
											.valueOf(result.getErrorInactive()))
									: BigInteger.ZERO);

					invNA = invNA.add(result.getInvNotApplicable() != null
							? new BigInteger(String
									.valueOf(result.getInvNotApplicable()))
							: BigInteger.ZERO);
					invAspError = invAspError
							.add(result.getInvAspError() != null
									? new BigInteger(String
											.valueOf(result.getInvAspError()))
									: BigInteger.ZERO);
					invAspProcessed = invAspProcessed
							.add(result.getInvAspProcessed() != null
									? new BigInteger(String.valueOf(
											result.getInvAspProcessed()))
									: BigInteger.ZERO);
					invIRNInProgress = invIRNInProgress
							.add(result.getInvIrnInProgress() != null
									? new BigInteger(String.valueOf(
											result.getInvIrnInProgress()))
									: BigInteger.ZERO);
					invIrnError = invIrnError
							.add(result.getInvIrnInError() != null
									? new BigInteger(String
											.valueOf(result.getInvIrnInError()))
									: BigInteger.ZERO);
					invIrnProcessed = invIrnProcessed
							.add(result.getInvIrnProcessed() != null
									? new BigInteger(String.valueOf(
											result.getInvIrnProcessed()))
									: BigInteger.ZERO);

					invIrnCancelled = invIrnCancelled
							.add(result.getInvIrnCancelled() != null
									? new BigInteger(String.valueOf(
											result.getInvIrnCancelled()))
									: BigInteger.ZERO);

					einvVInfoError = einvVInfoError
							.add(result.getEinvVInfoError() != null
									? new BigInteger(String.valueOf(
											result.getEinvVInfoError()))
									: BigInteger.ZERO);

					einvNotOpted = einvNotOpted
							.add(result.getEinvNotOpted() != null
									? new BigInteger(String
											.valueOf(result.getEinvNotOpted()))
									: BigInteger.ZERO);

					einvId = einvId.add(result.getEinvId() != null
							? new BigInteger(String.valueOf(result.getEinvId()))
							: BigInteger.ZERO);

					eWbna = eWbna.add(result.getEwbNotApplicable() != null
							? new BigInteger(String
									.valueOf(result.getEwbNotApplicable()))
							: BigInteger.ZERO);

					ewbErrorGigiGST = ewbErrorGigiGST
							.add(result.getEwbAspError() != null
									? new BigInteger(String
											.valueOf(result.getEwbAspError()))
									: BigInteger.ZERO);

					ewbProcessed = ewbProcessed
							.add(result.getEwbAspProcessed() != null
									? new BigInteger(String.valueOf(
											result.getEwbAspProcessed()))
									: BigInteger.ZERO);
					ewbInitiated = ewbInitiated
							.add(result.getEwbGenInProgress() != null
									? new BigInteger(String.valueOf(
											result.getEwbGenInProgress()))
									: BigInteger.ZERO);
					ewbError = ewbError.add(result.getEwbNicError() != null
							? new BigInteger(
									String.valueOf(result.getEwbNicError()))
							: BigInteger.ZERO);
					ewbGenerated = ewbGenerated
							.add(result.getEwbPartAGenerated() != null
									? new BigInteger(String.valueOf(
											result.getEwbPartAGenerated()))
									: BigInteger.ZERO);
					ewbCancelled = ewbCancelled
							.add(result.getEwbCancelled() != null
									? new BigInteger(String
											.valueOf(result.getEwbCancelled()))
									: BigInteger.ZERO);
					ewbGeneratedOnErp = ewbGeneratedOnErp
							.add(result.getEwbGeneratedOnErp() != null
									? new BigInteger(String.valueOf(
											result.getEwbGeneratedOnErp()))
									: BigInteger.ZERO);
					ewbNotGeneratedOnErp = ewbNotGeneratedOnErp
							.add(result.getEwbNotGeneratedOnErp() != null
									? new BigInteger(String.valueOf(
											result.getEwbNotGeneratedOnErp()))
									: BigInteger.ZERO);

					ewbNotOpted = ewbNotOpted
							.add(result.getEwbNotOpted() != null
									? new BigInteger(String
											.valueOf(result.getEwbNotOpted()))
									: BigInteger.ZERO);
					ewbId = ewbId.add(result.getEwbId() != null
							? new BigInteger(String.valueOf(result.getEwbId()))
							: BigInteger.ZERO);

					aspNA = aspNA.add(result.getAspNA() != null
							? new BigInteger(String.valueOf(result.getAspNA()))
							: BigInteger.ZERO);

					aspError = aspError.add(result.getAspError() != null
							? new BigInteger(
									String.valueOf(result.getAspError()))
							: BigInteger.ZERO);

					aspProcess = aspProcess.add(result.getAspProcess() != null
							? new BigInteger(
									String.valueOf(result.getAspProcess()))
							: BigInteger.ZERO);

					aspSaveInitiated = aspSaveInitiated
							.add(result.getAspSaveInitiated() != null
									? new BigInteger(String.valueOf(
											result.getAspSaveInitiated()))
									: BigInteger.ZERO);

					aspSavedGstin = aspSavedGstin
							.add(result.getAspSavedGstin() != null
									? new BigInteger(String
											.valueOf(result.getAspSavedGstin()))
									: BigInteger.ZERO);

					aspErrorsGstin = aspErrorsGstin
							.add(result.getAspErrorsGstin() != null
									? new BigInteger(String.valueOf(
											result.getAspErrorsGstin()))
									: BigInteger.ZERO);
				}
				EInvoiceDataStatusDto statusDto1 = new EInvoiceDataStatusDto();
				statusDto1.setTotalRecords(totalRecords);
				statusDto1.setProcesseActive(processActive);
				statusDto1.setProcesseInactive(processInActive);
				statusDto1.setErrorActive(errorActive);
				statusDto1.setErrorInactive(errorInActive);

				statusDto1.setEInvNA(invNA);
				statusDto1.setEInvErrorDigiGST(invAspError);
				statusDto1.setEInvProcessed(invAspProcessed);
				statusDto1.setEinvINRInitiated(invIRNInProgress);
				statusDto1.setEInvGenerated(invIrnProcessed);
				statusDto1.setEInvError(invIrnError);
				statusDto1.setEinvCancelled(invIrnCancelled);
				statusDto1.setEinvInfoError(einvVInfoError);
				statusDto1.setEinvNotOpted(einvNotOpted);
				statusDto1.setEinvId(einvId);

				statusDto1.setEWbna(eWbna);
				statusDto1.setEwbErrorDigiGST(ewbErrorGigiGST);
				statusDto1.setEwbProcessd(ewbProcessed);
				statusDto1.setEwbInitiated(ewbInitiated);
				statusDto1.setEwbGenerated(ewbGenerated);
				statusDto1.setEwbError(ewbError);
				statusDto1.setEwbCancelled(ewbCancelled);
				statusDto1.setEwbGeneratedOnErp(ewbGeneratedOnErp);
				statusDto1.setEwbGeneratedOnErp(ewbNotGeneratedOnErp);
				statusDto1.setEwbNotOpted(ewbNotOpted);
				statusDto1.setEwbId(ewbId);

				statusDto1.setAspNA(aspNA);
				statusDto1.setAspError(aspError);
				statusDto1.setAspProcess(aspProcess);
				statusDto1.setAspErrorsGstin(aspErrorsGstin);
				statusDto1.setAspSavedGstin(aspSavedGstin);
				statusDto1.setAspSaveInitiated(aspSaveInitiated);

				statusDtos.add(statusDto1);

				for (Anx1NewDataStatusEntity result : searchResult
						.getResult()) {
					EInvoiceDataStatusDto statusDto = new EInvoiceDataStatusDto();
					statusDto.setReceivedDate(result.getReceivedDate() != null
							? String.valueOf(result.getReceivedDate()) : null);
					statusDto.setTotalRecords(result.getTotalRecords() != null
							? new BigInteger(
									String.valueOf(result.getTotalRecords()))
							: BigInteger.ZERO);
					statusDto.setProcesseActive(
							result.getProcessedActive() != null
									? new BigInteger(String.valueOf(
											result.getProcessedActive()))
									: BigInteger.ZERO);
					statusDto.setProcesseInactive(
							result.getProcessedInactive() != null
									? new BigInteger(String.valueOf(
											result.getProcessedInactive()))
									: BigInteger.ZERO);
					statusDto.setErrorActive(result.getErrorActive() != null
							? new BigInteger(
									String.valueOf(result.getErrorActive()))
							: BigInteger.ZERO);
					statusDto.setErrorInactive(result.getErrorInactive() != null
							? new BigInteger(
									String.valueOf(result.getErrorInactive()))
							: BigInteger.ZERO);

					statusDto.setEInvNA(result.getInvNotApplicable() != null
							? new BigInteger(String
									.valueOf(result.getInvNotApplicable()))
							: BigInteger.ZERO);
					statusDto
							.setEInvErrorDigiGST(result.getInvAspError() != null
									? new BigInteger(String
											.valueOf(result.getInvAspError()))
									: BigInteger.ZERO);
					statusDto.setEInvProcessed(
							result.getInvAspProcessed() != null
									? new BigInteger(String.valueOf(
											result.getInvAspProcessed()))
									: BigInteger.ZERO);
					statusDto.setEinvINRInitiated(
							result.getInvIrnInProgress() != null
									? new BigInteger(String.valueOf(
											result.getInvIrnInProgress()))
									: BigInteger.ZERO);
					statusDto.setEInvError(result.getInvIrnInError() != null
							? new BigInteger(
									String.valueOf(result.getInvIrnInError()))
							: BigInteger.ZERO);
					statusDto.setEInvGenerated(
							result.getInvIrnProcessed() != null
									? new BigInteger(String.valueOf(
											result.getInvIrnProcessed()))
									: BigInteger.ZERO);
					statusDto
							.setEinvInfoError(result.getEinvVInfoError() != null
									? new BigInteger(String.valueOf(
											result.getEinvVInfoError()))
									: BigInteger.ZERO);

					statusDto.setEinvCancelled(
							result.getInvIrnCancelled() != null
									? new BigInteger(String.valueOf(
											result.getInvIrnCancelled()))
									: BigInteger.ZERO);
					statusDto.setEinvNotOpted(result.getEinvNotOpted() != null
							? new BigInteger(
									String.valueOf(result.getEinvNotOpted()))
							: BigInteger.ZERO);

					statusDto.setEinvId(result.getEinvId() != null
							? new BigInteger(String.valueOf(result.getEinvId()))
							: BigInteger.ZERO);

					statusDto.setEWbna(result.getEwbNotApplicable() != null
							? new BigInteger(String
									.valueOf(result.getEwbNotApplicable()))
							: BigInteger.ZERO);
					statusDto.setEwbErrorDigiGST(result.getEwbAspError() != null
							? new BigInteger(
									String.valueOf(result.getEwbAspError()))
							: BigInteger.ZERO);
					statusDto.setEwbProcessd(result.getEwbAspProcessed() != null
							? new BigInteger(
									String.valueOf(result.getEwbAspProcessed()))
							: BigInteger.ZERO);
					statusDto.setEwbInitiated(
							result.getEwbGenInProgress() != null
									? new BigInteger(String.valueOf(
											result.getEwbGenInProgress()))
									: BigInteger.ZERO);
					statusDto.setEwbError(result.getEwbNicError() != null
							? new BigInteger(
									String.valueOf(result.getEwbNicError()))
							: BigInteger.ZERO);
					statusDto.setEinvCancelled(result.getEwbCancelled() != null
							? new BigInteger(
									String.valueOf(result.getEwbCancelled()))
							: BigInteger.ZERO);
					statusDto.setEwbGenerated(
							result.getEwbPartAGenerated() != null
									? new BigInteger(String.valueOf(
											result.getEwbPartAGenerated()))
									: BigInteger.ZERO);
					statusDto.setEwbGeneratedOnErp(
							result.getEwbGeneratedOnErp() != null
									? new BigInteger(String.valueOf(
											result.getEwbGeneratedOnErp()))
									: BigInteger.ZERO);
					statusDto.setEwbNotGeneratedOnErp(
							result.getEwbNotGeneratedOnErp() != null
									? new BigInteger(String.valueOf(
											result.getEwbNotGeneratedOnErp()))
									: BigInteger.ZERO);

					statusDto.setEwbNotOpted(result.getEwbNotOpted() != null
							? new BigInteger(
									String.valueOf(result.getEwbNotOpted()))
							: BigInteger.ZERO);
					
					statusDto.setEwbId(result.getEwbId() != null
							? new BigInteger(
									String.valueOf(result.getEwbId()))
							: BigInteger.ZERO);

					statusDto.setAspNA(result.getAspNA() != null
							? new BigInteger(String.valueOf(result.getAspNA()))
							: BigInteger.ZERO);
					statusDto.setAspError(result.getAspError() != null
							? new BigInteger(
									String.valueOf(result.getAspError()))
							: BigInteger.ZERO);

					statusDto.setAspProcess(result.getAspProcess() != null
							? new BigInteger(
									String.valueOf(result.getAspProcess()))
							: BigInteger.ZERO);
					statusDto.setAspErrorsGstin(
							result.getAspErrorsGstin() != null
									? new BigInteger(String.valueOf(
											result.getAspErrorsGstin()))
									: BigInteger.ZERO);
					statusDto.setAspSavedGstin(result.getAspSavedGstin() != null
							? new BigInteger(
									String.valueOf(result.getAspSavedGstin()))
							: BigInteger.ZERO);
					statusDto.setAspSaveInitiated(
							result.getAspSaveInitiated() != null
									? new BigInteger(String.valueOf(
											result.getAspSaveInitiated()))
									: BigInteger.ZERO);
					statusDtos.add(statusDto);
				}
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(statusDtos));

			} else {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree("No Record Found"));

			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
