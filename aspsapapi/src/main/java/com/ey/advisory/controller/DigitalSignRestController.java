/*package com.ey.advisory.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.apache.bcel.classfile.Constant;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.asp.client.dao.Gstr3Dao;
import com.ey.advisory.asp.client.domain.FIleSubmissionStatusDetails;
import com.ey.advisory.asp.client.domain.TblDashboardSummary;
import com.ey.advisory.asp.client.dto.DownloadedData;
import com.ey.advisory.asp.client.dto.SummaryDto;
import com.ey.advisory.asp.client.service.DigitalSignService;
import com.ey.advisory.asp.client.service.GSTINDetailsService;
import com.ey.advisory.asp.client.service.MailService;
import com.ey.advisory.asp.client.service.SignAuthService;
import com.ey.advisory.asp.client.service.TblGstinRetutnFilingStatusService;
import com.ey.advisory.asp.client.service.gstr1.Gstr1Service;
import com.ey.advisory.asp.client.service.gstr2.Gstr2Service;
import com.ey.advisory.asp.client.service.gstr3.Gstr3Service;
import com.ey.advisory.asp.client.service.gstr6.Gstr6Service;
import com.ey.advisory.asp.client.util.CommonUtillity;
import com.ey.advisory.asp.dto.DigitalSigModel;
import com.ey.advisory.asp.dto.DigitalSigSubmit;
import com.ey.advisory.asp.dto.YearMonthDto;
import com.ey.advisory.asp.gstn.common.AuthDetailsDto;
import com.ey.advisory.asp.gstn.exception.RestClientUtilException;
import com.ey.advisory.asp.gstn.service.second.IAPIService;
import com.ey.advisory.asp.gstn.util.CryptoUtil;
import com.ey.advisory.asp.gstn.util.RestClientUtility;
import com.ey.advisory.asp.master.service.UserService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.einv.dto.DocumentDetails;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@RestController
public class DigitalSignRestController {

	@Autowired
	Gstr1Service gstr1Service;

	@Autowired
	Gstr2Service gstr2Service;

	@Autowired
	Gstr3Service gstr3Service;

	@Autowired
	Gstr6Service gstr6Service;

	@Autowired
	MailService mailService;

	@Autowired
	Gstr3Dao gstr3Dao;

	@Autowired
	DigitalSignService digitalSignService;

	@Autowired
	@Qualifier("GSPService")
	IAPIService gstnServiceImpl;

	@Autowired
	GSTINDetailsService gstinService;

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Autowired
	UserService userService;

	@Autowired
	SignAuthService signAuthService;

	@Autowired
	TblGstinRetutnFilingStatusService returnFilingService;

	@Autowired
	private Environment env;

	private static final Logger LOGGER = Logger
			.getLogger(DigitalSignRestController.class);
	private static final String CLASS_NAME = DigitalSignRestController.class
			.getName();

	*//******* Dongle sign Methods *********//*
	*//**
	 * @param summaryDto
	 * @param headers
	 * @return
	 *//*
	@RequestMapping(value = "/getSignedDataGstr1", method = RequestMethod.POST,
			produces = {MediaType.APPLICATION_JSON_UTF8_VALUE })
	public String getSignedDataGstr1(
			@RequestHeader("X-TENANT-ID") String groupCode,
			@RequestBody SummaryDto summaryDto) {
		AuthDetailsDto dto = new AuthDetailsDto();
		String response = "";
		try {
			User user = userService.findByUserName(summaryDto.getUserName());
			String pan = user.getPan();
			String gstnUserName = "";
			if (!(CommonUtillity.isEmpty(summaryDto.getGstinId()))) {
				dto = RestClientUtility
						.getRedisData(summaryDto.getGstinId()) != null
								? RestClientUtility
										.getRedisData(summaryDto.getGstinId())
								: new AuthDetailsDto();
			}
			if (!(CommonUtillity
					.isEmpty(summaryDto.getGroupCode().toLowerCase()))) {
				Map<Object, Object> map = RestClientUtility
						.getRedisDataForGSP(summaryDto.getGroupCode());
				dto.setDigigstUserName(
						(String) map.get(Constant.GSP_DIGIGST_USERNAME));
				dto.setAccessToken((String) map.get(Constant.ACCESS_TOKEN));
				dto.setApiKey(((String) map.get(Constant.API_KEY)).getBytes());
				dto.setApiSecret((String) map.get(Constant.API_SECRET));
			}

			byte[] authEK = null;

			if (!(CommonUtillity.isEmpty(dto.getSek()))) {
				authEK = CryptoUtil.decodeBase64StringTOByte(dto.getSek());
				dto.setAuthEK(authEK);
			}
			if (!(CommonUtillity.isEmpty(dto.getUserName()))) {
				gstnUserName = dto.getUserName();
			}

			String host = env.getProperty("gsp.restapi.host");
			String endpointRet = env
					.getProperty("gsp.restapi.gstr1.version.retFile");

			response = gstnServiceImpl.fileGSTR(dto, summaryDto.getData(),
					summaryDto.getSigneddata(), summaryDto.getSignatureType(),
					pan, gstnUserName, summaryDto.getGstinId(),
					summaryDto.getTaxPeriod(), host, endpointRet,
					Constant.GSTR1, Constant.RETFILE, true);

			if (response != null && response != "") {
				if (response.contains("ack_num")) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(response).getAsJsonObject();
					gstr1Service.updateAckNum(summaryDto.getGstinId(),
							summaryDto.getTaxPeriod(),
							json.get("ack_num").getAsString());
				} else {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(response).getAsJsonObject();
					JsonObject errorObj = json.get("error").getAsJsonObject();
					response = errorObj.get("message").getAsString();
				}
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException", e);
		} catch (RestClientUtilException e) {
			LOGGER.error("RestClientUtilException", e);
		}
		return response;

	}

	*//**
	 * @param summaryDto
	 * @param headers
	 * @return
	 *//*
	@RequestMapping(value = "/getSignedDataGstr2", method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_UTF8_VALUE })
	public String getSignedDataGstr2(@RequestBody SummaryDto summaryDto,
			@RequestHeader("X-TENANT-ID") String groupCode) {
		AuthDetailsDto dto = new AuthDetailsDto();
		String response = "";
		try {
			User user = userService.findByUserName(summaryDto.getUserName());
			String pan = user.getPan();
			String gstnUserName = "";
			if (!(CommonUtillity.isEmpty(summaryDto.getGstinId()))) {
				dto = RestClientUtility
						.getRedisData(summaryDto.getGstinId()) != null
								? RestClientUtility
										.getRedisData(summaryDto.getGstinId())
								: new AuthDetailsDto();
			}
			if (!(CommonUtillity
					.isEmpty(summaryDto.getGroupCode().toLowerCase()))) {
				Map<Object, Object> map = RestClientUtility
						.getRedisDataForGSP(summaryDto.getGroupCode());
				dto.setDigigstUserName(
						(String) map.get(Constant.GSP_DIGIGST_USERNAME));
				dto.setAccessToken((String) map.get(Constant.ACCESS_TOKEN));
				dto.setApiKey(((String) map.get(Constant.API_KEY)).getBytes());
				dto.setApiSecret((String) map.get(Constant.API_SECRET));
			}

			byte[] authEK = null;

			if (!(CommonUtillity.isEmpty(dto.getSek()))) {
				authEK = CryptoUtil.decodeBase64StringTOByte(dto.getSek());
				dto.setAuthEK(authEK);
			}
			if (!(CommonUtillity.isEmpty(dto.getUserName()))) {
				gstnUserName = dto.getUserName();
			}

			response = gstnServiceImpl.fileGSTR(dto, summaryDto.getData(),
					summaryDto.getSigneddata(), summaryDto.getSignatureType(),
					pan, gstnUserName, summaryDto.getGstinId(),
					summaryDto.getTaxPeriod(), Constant.GSPGW_RESTAPI_HOST,
					Constant.GSPGW_RESTAPI_RETURN, Constant.GSTR2,
					Constant.RETFILE, true);

			if (response != null && response != "") {
				if (response.contains("ack_num")) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(response).getAsJsonObject();
					gstr2Service.updateAckNum(summaryDto.getGstinId(),
							summaryDto.getTaxPeriod(),
							json.get("ack_num").getAsString());
				} else {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(response).getAsJsonObject();
					JsonObject errorObj = json.get("error").getAsJsonObject();
					response = errorObj.get("message").getAsString();
				}
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException", e);
		} catch (RestClientUtilException e) {
			LOGGER.error("RestClientUtilException", e);
		}
		return response;
	}

	*//**
	 * @param summaryDto
	 * @param headers
	 * @return
	 *//*
	@RequestMapping(value = "/getSignedDataGstr6", method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_UTF8_VALUE })
	public String getSignedDataGstr6(@RequestBody SummaryDto summaryDto,
			@RequestHeader("X-TENANT-ID") String groupCode) {
		AuthDetailsDto dto = new AuthDetailsDto();
		String response = "";
		try {
			User user = userService.findByUserName(summaryDto.getUserName());
			String pan = user.getPan();
			String gstnUserName = "";
			if (!(CommonUtillity.isEmpty(summaryDto.getGstinId()))) {
				dto = RestClientUtility
						.getRedisData(summaryDto.getGstinId()) != null
								? RestClientUtility
										.getRedisData(summaryDto.getGstinId())
								: new AuthDetailsDto();
			}
			if (!(CommonUtillity
					.isEmpty(summaryDto.getGroupCode().toLowerCase()))) {
				Map<Object, Object> map = RestClientUtility
						.getRedisDataForGSP(summaryDto.getGroupCode());
				dto.setDigigstUserName(
						(String) map.get(Constant.GSP_DIGIGST_USERNAME));
				dto.setAccessToken((String) map.get(Constant.ACCESS_TOKEN));
				dto.setApiKey(((String) map.get(Constant.API_KEY)).getBytes());
				dto.setApiSecret((String) map.get(Constant.API_SECRET));
			}

			byte[] authEK = null;

			if (!(CommonUtillity.isEmpty(dto.getSek()))) {
				authEK = CryptoUtil.decodeBase64StringTOByte(dto.getSek());
				dto.setAuthEK(authEK);
			}
			if (!(CommonUtillity.isEmpty(dto.getUserName()))) {
				gstnUserName = dto.getUserName();
			}

			response = gstnServiceImpl.fileGSTR(dto, summaryDto.getData(),
					summaryDto.getSigneddata(), summaryDto.getSignatureType(),
					pan, gstnUserName, summaryDto.getGstinId(),
					summaryDto.getTaxPeriod(), Constant.GSPGW_RESTAPI_HOST,
					Constant.GSPGW_RESTAPI_RETURN, Constant.GSTR6,
					Constant.RETFILE, true);

			if (response != null && response != "") {
				if (response.contains("ack_num")) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(response).getAsJsonObject();
					gstr2Service.updateAckNum(summaryDto.getGstinId(),
							summaryDto.getTaxPeriod(),
							json.get("ack_num").getAsString());
				} else {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(response).getAsJsonObject();
					JsonObject errorObj = json.get("error").getAsJsonObject();
					response = errorObj.get("message").getAsString();
				}
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException", e);
		} catch (RestClientUtilException e) {
			LOGGER.error("RestClientUtilException", e);
		}
		return response;
	}

	*//**
	 * @param ymObj
	 * @param headers
	 *//*
	@RequestMapping(value = "/sendMailGstr1", method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_UTF8_VALUE })
	public void sendMailGstr1(@RequestBody DocumentDetails docObj) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " sendMailGstr1");
		}
		try {
			mailService.fileGSTR1(docObj.getYearMonthDto().getUserEmailId(),
					docObj.getYearMonthDto().getGstinId(),
					docObj.getYearMonthDto().getRtPeriod());
		} catch (Exception e) {
			LOGGER.error(Constant.LOGGER_ERROR + CLASS_NAME
					+ Constant.LOGGER_METHOD + " sendMailGstr1", e);
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " sendMailGstr1");
		}
	}

	*//**
	 * @param ymObj
	 * @param headers
	 *//*
	@RequestMapping(value = "/sendMailGstr2", method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_UTF8_VALUE })
	public void sendMailGstr2(@RequestBody DocumentDetails docObj) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " sendMailGstr2");
		}
		try {
			mailService.fileGSTR2(docObj.getYearMonthDto().getUserEmailId(),
					docObj.getYearMonthDto().getGstinId(),
					docObj.getYearMonthDto().getRtPeriod());
		} catch (Exception e) {
			LOGGER.error(Constant.LOGGER_ERROR + CLASS_NAME
					+ Constant.LOGGER_METHOD + " sendMailGstr2", e);
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " sendMailGstr2");
		}
	}

	*//**
	 * @param ymObj
	 * @param headers
	 *//*
	@RequestMapping(value = "/sendMailGstr6", method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_UTF8_VALUE })
	public void sendMailGstr6(@RequestBody DocumentDetails docObj) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " sendMailGstr6");
		}
		try {
			mailService.fileGSTR6(docObj.getYearMonthDto().getUserEmailId(),
					docObj.getYearMonthDto().getGstinId(),
					docObj.getYearMonthDto().getRtPeriod());
		} catch (Exception e) {
			LOGGER.error(Constant.LOGGER_ERROR + CLASS_NAME
					+ Constant.LOGGER_METHOD + " sendMailGstr6", e);
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " sendMailGstr6");
		}
	}

	*//******* Dongle sign Methods ends *********//*

	*//******* Digio sign Methods *********//*
	*//**
	 * 
	 * @param ymObj
	 * @param response
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 *//*
	@RequestMapping(value = "/digitalsignature", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigModel> getDocumentIDfromDigio(
			@RequestBody YearMonthDto ymObj)
			throws NoSuchAlgorithmException, IOException, URISyntaxException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getDocumentIDfromDigio");
		}
		String docId;
		String content;
		if (ymObj.isDbFlag()) {
			content = gstr1Service.gstr1Summary(ymObj.getGstinId(),
					ymObj.getMonth(), ymObj.getYear());
		} else {
			content = (String) gstr1Service.getGSTR1SummaryfromDB(
					ymObj.getGstinId(), ymObj.getRtPeriod());
		}
		docId = digitalSignService.uploadData(ymObj.getUserEmailId(), content);

		DigitalSigModel model = new DigitalSigModel();
		model.setDocumentId(docId);
		model.setEmailId(ymObj.getUserEmailId());
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getDocumentIDfromDigio");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	*//**
	 * 
	 * @param docObj
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	@RequestMapping(value = "/digSigSubmit2GSTN", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigSubmit> downloadHashFromDigio(
			@RequestBody DocumentDetails docObj) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio");
		}
		DownloadedData downloaded = digitalSignService
				.downloadSignedData(docObj.getDocId());

		String status = null;
		DigitalSigSubmit model = new DigitalSigSubmit(status,
				downloaded.getPkcs7());
		String gstn = docObj.getYearMonthDto().getGstinId();
		String taxPeriod = docObj.getYearMonthDto().getRtPeriod();
		String resp = null;
		try {
			SummaryDto summaryDto = new SummaryDto();
			summaryDto.setPayLoad(downloaded.getContent());
			summaryDto.setSigneddata(downloaded.getPkcs7());
			summaryDto.setSignatureType(Constant.ESIGN);
			summaryDto.setSid(gstn);
			summaryDto.setGstinId(gstn);
			summaryDto.setTaxPeriod(taxPeriod);
			resp = gstr1Service.fileGSTR1(summaryDto);

			JSONObject jsonObjectVal = (JSONObject) new JSONParser()
					.parse(resp);
			model.setStatus(String.valueOf(jsonObjectVal.get(Constant.STATUS)));
			model.setAcknowledmentId(
					String.valueOf(jsonObjectVal.get(Constant.ACK_NUM)));

			mailService.fileGSTR1(docObj.getYearMonthDto().getUserEmailId(),
					gstn, taxPeriod);

		} catch (Exception e) {

			model.setStatus(Constant.ONE);
			model.setAcknowledmentId(Constant.ACKNOWLEDGE_ID);
			LOGGER.error(Constant.LOGGER_ERROR + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio", e);

		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	*//**
	 * 
	 * @param ymObj
	 * @param response
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 *//*
	@RequestMapping(value = "/digitalsignature2", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigModel> getDocumentIDfromDigio2(
			@RequestBody YearMonthDto ymObj)
			throws NoSuchAlgorithmException, IOException, URISyntaxException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getDocumentIDfromDigio2");
		}
		String docId;
		String content;
		if (ymObj.isDbFlag()) {
			content = gstr2Service.gstr2Summary(ymObj.getGstinId(),
					ymObj.getMonth(), ymObj.getYear());
		} else {
			content = (String) gstr2Service.getGSTR2SummaryfromDB(
					ymObj.getGstinId(), ymObj.getRtPeriod(),
					ymObj.getSumtype() != null ? ymObj.getSumtype() : "PR");
		}
		docId = digitalSignService.uploadData(ymObj.getUserEmailId(), content);

		DigitalSigModel model = new DigitalSigModel();
		model.setDocumentId(docId);
		model.setEmailId(ymObj.getUserEmailId());
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getDocumentIDfromDigio2");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	*//**
	 * 
	 * @param docObj
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	@RequestMapping(value = "/digSigSubmit2GSTN2", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigSubmit> downloadHashFromDigio2(
			@RequestBody DocumentDetails docObj) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio2");
		}
		DownloadedData downloaded = digitalSignService
				.downloadSignedData(docObj.getDocId());

		String status = null;
		DigitalSigSubmit model = new DigitalSigSubmit(status,
				downloaded.getPkcs7());
		String gstn = docObj.getYearMonthDto().getGstinId();
		String taxPeriod = docObj.getYearMonthDto().getRtPeriod();
		String resp = null;
		try {
			SummaryDto summaryDto = new SummaryDto();
			summaryDto.setPayLoad(downloaded.getContent());
			summaryDto.setSigneddata(downloaded.getPkcs7());
			summaryDto.setSignatureType(Constant.ESIGN);
			summaryDto.setSid(gstn);
			summaryDto.setGstinId(gstn);
			summaryDto.setTaxPeriod(taxPeriod);
			resp = gstr2Service.fileGSTR2(summaryDto);

			JSONObject jsonObjectVal = (JSONObject) new JSONParser()
					.parse(resp);
			model.setStatus(String.valueOf(jsonObjectVal.get(Constant.STATUS)));
			model.setAcknowledmentId(
					String.valueOf(jsonObjectVal.get(Constant.ACK_NUM)));
			mailService.fileGSTR2(docObj.getYearMonthDto().getUserEmailId(),
					gstn, taxPeriod);
		} catch (Exception e) {
			model.setStatus(Constant.ONE);
			model.setAcknowledmentId(Constant.ACKNOWLEDGE_ID);
			LOGGER.error(Constant.LOGGER_ERROR + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio2", e);
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio2");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	*//******* Digio sign Methods *********//*

	*//**
	 * 
	 * @param ymObj
	 * @param response
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 *//*
	@RequestMapping(value = "/digitalsignature3", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigModel> getDocumentIDfromDigio3(
			@RequestBody YearMonthDto ymObj)
			throws NoSuchAlgorithmException, IOException, URISyntaxException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getDocumentIDfromDigio3");
		}
		String docId;
		String content = gstr3Service.signfileGstr3(ymObj.getGstinId(),
				ymObj.getMonth(), ymObj.getYear());
		docId = digitalSignService.uploadData(ymObj.getUserEmailId(), content);

		DigitalSigModel model = new DigitalSigModel();
		model.setDocumentId(docId);
		model.setEmailId(ymObj.getUserEmailId());
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getDocumentIDfromDigio3");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	*//**
	 * 
	 * @param docObj
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	@RequestMapping(value = "/digSigSubmitGSTN3", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigSubmit> downloadHashFromDigio3(
			@RequestBody DocumentDetails docObj) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio3");
		}
		DownloadedData downloaded = digitalSignService
				.downloadSignedData(docObj.getDocId());

		String status = "";
		DigitalSigSubmit model = new DigitalSigSubmit(status,
				downloaded.getPkcs7());
		String gstn = docObj.getYearMonthDto().getGstinId();
		String selMonth = docObj.getYearMonthDto().getMonth();
		String selYear = docObj.getYearMonthDto().getYear();
		String resp = "";
		try {
			SummaryDto summaryDto = new SummaryDto();
			summaryDto.setPayLoad(downloaded.getContent());
			summaryDto.setSigneddata(downloaded.getPkcs7());
			summaryDto.setSignatureType(Constant.ESIGN);
			summaryDto.setSid(gstn);
			resp = gstr3Service.fileGSTR3(summaryDto);

			JSONObject jsonObjectVal = (JSONObject) new JSONParser()
					.parse(resp);
			model.setStatus(String.valueOf(jsonObjectVal.get(Constant.STATUS)));
			model.setAcknowledmentId(
					String.valueOf(jsonObjectVal.get(Constant.ACK_NUM)));

			FIleSubmissionStatusDetails fIleSubmissionStatus = new FIleSubmissionStatusDetails();
			fIleSubmissionStatus.setRefNo(model.getAcknowledmentId());
			fIleSubmissionStatus
					.setGstin(docObj.getYearMonthDto().getGstinId());
			fIleSubmissionStatus
					.setReturnPeriod(docObj.getYearMonthDto().getMonth()
							+ docObj.getYearMonthDto().getYear());
			fIleSubmissionStatus.setCreatedOn(new Date());
			fIleSubmissionStatus.setFiled(true);
			gstr3Service.saveGstr3FileSubmStatus(fIleSubmissionStatus);
			mailService.fileGSTR3(docObj.getYearMonthDto().getUserEmailId(),
					gstn, selMonth, selYear);
		} catch (Exception e) {
			model.setStatus(Constant.ONE);
			model.setAcknowledmentId(Constant.ACKNOWLEDGE_ID);
			LOGGER.error(Constant.LOGGER_ERROR + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio2", e);
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio2");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	*//**
	 * 
	 * @param ymObj
	 * @param response
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 *//*
	@RequestMapping(value = "/digitalsignature6", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigModel> getDocumentIDfromDigio6(
			@RequestBody YearMonthDto ymObj)
			throws NoSuchAlgorithmException, IOException, URISyntaxException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getDocumentIDfromDigio6");
		}
		String docId;
		String content;
		ymObj.setDbFlag(true);
		if (ymObj.isDbFlag()) {
			content = gstr6Service.GSTR6Summary(ymObj.getGstinId(),
					ymObj.getMonth(), ymObj.getYear());
		} else {
			content = (String) gstr6Service.getGSTR6SummaryfromDB(
					ymObj.getGstinId(), ymObj.getRtPeriod());
		}
		docId = digitalSignService.uploadData(ymObj.getUserEmailId(), content);

		DigitalSigModel model = new DigitalSigModel();
		model.setDocumentId(docId);
		model.setEmailId(ymObj.getUserEmailId());
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getDocumentIDfromDigio6");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	*//**
	 * 
	 * @param docObj
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	@RequestMapping(value = "/digSigSubmitGSTN6", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigSubmit> downloadHashFromDigio6(
			@RequestBody DocumentDetails docObj) throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_ENTERING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio6");
		}
		DownloadedData downloaded = digitalSignService
				.downloadSignedData(docObj.getDocId());

		String status = null;
		DigitalSigSubmit model = new DigitalSigSubmit(status,
				downloaded.getPkcs7());
		String gstn = docObj.getYearMonthDto().getGstinId();
		String taxPeriod = docObj.getYearMonthDto().getRtPeriod();
		String resp = null;
		try {
			SummaryDto summaryDto = new SummaryDto();
			summaryDto.setPayLoad(downloaded.getContent());
			summaryDto.setSigneddata(downloaded.getPkcs7());
			summaryDto.setSignatureType(Constant.ESIGN);
			summaryDto.setSid(gstn);
			summaryDto.setGstinId(gstn);
			summaryDto.setTaxPeriod(taxPeriod);
			resp = gstr6Service.fileGSTR6(summaryDto);

			JSONObject jsonObjectVal = (JSONObject) new JSONParser()
					.parse(resp);
			model.setStatus(String.valueOf(jsonObjectVal.get(Constant.STATUS)));
			model.setAcknowledmentId(
					String.valueOf(jsonObjectVal.get(Constant.ACK_NUM)));
			mailService.fileGSTR6(docObj.getYearMonthDto().getUserEmailId(),
					gstn, taxPeriod);

		} catch (Exception e) {
			model.setStatus(Constant.ONE);
			model.setAcknowledmentId(Constant.ACKNOWLEDGE_ID);
			LOGGER.error(Constant.LOGGER_ERROR + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio6", e);
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " downloadHashFromDigio6");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	@RequestMapping(value = "/requestSignFileOtp")
	public String requestSignFileOtp(@RequestBody String jsonObject) {
		String response = "";
		try {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(jsonObject).getAsJsonObject();
			String groupCode = json.get("groupCode").getAsString()
					.toLowerCase();
			String apiKey = "";
			String apiSecret = "";
			response = gstnServiceImpl.getOTP(
					json.get("mobile_number").getAsString(),
					json.get("app_key").getAsString(),
					json.get("digigst_username").getAsString(),
					Constant.APISIGN_GSTR1_OTP_ENDPOINT, apiKey, apiSecret);
		} catch (RestClientUtilException e) {
			LOGGER.error("RestClientUtilException", e);
		}
		return response;
	}

	@RequestMapping(value = "/getEncryptedApiKeySecret")
	public String getEncryptedApiKeySecret(@RequestBody String jsonData) {
		String apiData = "";
		try {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(jsonData).getAsJsonObject();
			String groupCode = json.get("groupCode").getAsString()
					.toLowerCase();
			String apiKey = "";
			String apiSecret = "";

			apiData = gstnServiceImpl.getEncryptedApiKeySecret(
					json.get("appkey").getAsString(), apiKey, apiSecret);
		} catch (Exception e) {
			LOGGER.error("exception in getEncryptedApiKeySecret", e);
		}
		return apiData;
	}

	@RequestMapping(value = "/signData")
	public String signData(@RequestBody SummaryDto signDto) {
		String response = "";
		try {
			response = gstnServiceImpl.verifyOTP(signDto.getUserName(),
					signDto.getApiKey(), signDto.getApiSecret(),
					signDto.getOtp(), signDto.getSigneddata(),
					Constant.APISIGN_GSTR1_OTP_VERIFY_ENDPOINT);

		} catch (RestClientUtilException e) {
			LOGGER.error("RestClientUtilException", e);
		}
		return response;
	}

	@RequestMapping(value = "/AuthSignatoryCheck", method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_UTF8_VALUE })
	public Boolean AuthSignatoryCheck(
			@RequestHeader("X-TENANT-ID") String groupCode,
			@RequestBody String jsonObject) {
		LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME
				+ Constant.LOGGER_METHOD + " AuthSignatoryCheck");
		Boolean isAuthSignatory = null;
		try {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(jsonObject).getAsJsonObject();

			String userId = json.get("user").getAsString();

			isAuthSignatory = signAuthService.checkUserAuthSignatory(userId);

		} catch (Exception e) {
			LOGGER.error(Constant.LOGGER_ERROR + CLASS_NAME
					+ Constant.LOGGER_METHOD + " AuthSignatoryCheck", e);
		}
		LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME
				+ Constant.LOGGER_METHOD + " AuthSignatoryCheck");
		return isAuthSignatory;
	}

	@RequestMapping(value = "/getSubmitStatusForEntity", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public Object getSubmitStatusForEntity(
			@RequestHeader("X-TENANT-ID") String groupCode,
			@RequestBody String jsonObject) {
		LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME
				+ Constant.LOGGER_METHOD + " getSubmitStatusForEntity");
		String finalResponse = null;
		try {

			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(jsonObject).getAsJsonObject();

			Long userId = json.get("userId").getAsLong();
			String entity = json.get("entity").getAsString();
			String returnType = json.get("returnType").getAsString();
			String taxPeriod = json.get("taxPeriod").getAsString();

			List<String> gstins = gstinService.fetchAllGstinsByEntityID(
					Arrays.asList(Integer.valueOf(entity)));

			List<String> applicableGstins = signAuthService
					.checkApplicableAuthSignatoryforGstin(gstins, userId);
			if (applicableGstins != null && !applicableGstins.isEmpty()) {
				Map<String, Boolean> dataCheck = null;
				List<TblDashboardSummary> dataAvailable = null;
				if (returnType != null
						&& returnType.equalsIgnoreCase(Constant.GSTR_1))
					dataAvailable = gstr1Service.fetchDashboardSummary(
							applicableGstins, "HSN1", true, taxPeriod);

				if (dataAvailable != null && !dataAvailable.isEmpty()) {
					dataCheck = new LinkedHashMap<String, Boolean>();
					for (TblDashboardSummary dashBoard : dataAvailable) {
						Boolean isAvailable = (dashBoard.getTaxVal() != null
								&& dashBoard.getTaxVal()
										.compareTo(BigDecimal.ZERO) > 0) ? true
												: false;
						dataCheck.put(dashBoard.getsGstin(), isAvailable);
					}
				}

				JsonArray gridArray = new JsonArray();
				for (String gstin : applicableGstins) {
					JsonObject gridObj = new JsonObject();
					gridObj.addProperty("gstin", gstin);
					gridObj.addProperty("tokenStatus", checkGstinStatus(gstin));
					if (dataCheck != null && dataCheck.get(gstin) != null)
						gridObj.addProperty("dataAvailable",
								dataCheck.get(gstin));
					else
						gridObj.addProperty("dataAvailable", false);

					gridArray.add(gridObj);
				}

				finalResponse = gridArray.toString();
			}
		} catch (Exception e) {
			LOGGER.error(Constant.LOGGER_ERROR + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getSubmitStatusForEntity", e);
		}
		LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME
				+ Constant.LOGGER_METHOD + " getSubmitStatusForEntity");
		return finalResponse;
	}

	@RequestMapping(value = "/getChunkStatusforSubmit", 
			method = RequestMethod.POST)
	@ResponseBody
	public Object getChunkStatusforSubmit(@RequestBody String data) {
		Object returnUploadList = null;

		try {
			LOGGER.info("Inside getChunkStatusforSubmit of " + CLASS_NAME);

			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(data).getAsJsonObject();
			String taxPeriod = json.get("taxPeriod").getAsString();
			String gstin = json.get("GSTIN").getAsString();
			String returnType = json.get("returnTypes").getAsString();

			LOGGER.info("Caliing getChunkStatusforSubmit with parameters :"
					+ "GSTIN: " + gstin + ", TaxPeriod: " + taxPeriod
					+ ", Return Type: " + returnType);

			returnUploadList = gstinService.getChunkStatusforSubmit(gstin,
					taxPeriod, returnType);

		} catch (JsonParseException e) {
			LOGGER.error("Unable to parse json :" + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(Constant.LOGGER_EXITING + CLASS_NAME
					+ Constant.LOGGER_METHOD + " getChunkStatusforSubmit", e);
		}
		return returnUploadList;
	}

	private String checkGstinStatus(String gstinId) {

		String status = "";
		try {

			String dateModified = RestClientUtility
					.getDateModifiedFromRedisCache(gstinId, redisTemplate);

			String logMsg = String.format(
					"Fetching details from redis " + "for gstin : %s for"
							+ "checking auth token. The date modified is : %s",
					gstinId, dateModified);
			LOGGER.info(logMsg);

			if (null != dateModified && !dateModified.isEmpty()) {

				Date dateModifiedFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss").parse(dateModified);
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateModifiedFormat);
				cal.add(Calendar.HOUR_OF_DAY,
						Integer.parseInt(env.getProperty("expiryTimeGSTIN")));
				Date expiryTime = cal.getTime();
				Date currentDate = new Date();

				if (expiryTime.compareTo(currentDate) <= 0) {
					status = "Not Active";
				} else {
					status = "Active";
				}

			} else {
				status = "Not available";
			}
			// }
		} catch (Exception ex) {

			String erroMmsg = String
					.format("An exception occured while fetching the auth token "
							+ "details for gstin : %s .", ex);
			throw new AppException(erroMmsg);
		}

		String logMsg = String.format(
				"The auth token status for " + "a gstn : %s is %s", gstinId,
				status);
		LOGGER.info(logMsg);
		return status;
	}

	*//**
	 * @param summaryDto
	 * @param headers
	 * @return
	 *//*
	@RequestMapping(value = "/getSignedDataGstr3B", method = RequestMethod.POST,
			produces = {MediaType.APPLICATION_JSON_UTF8_VALUE })
	public String getSignedDataGstr3B(
			@RequestHeader("X-TENANT-ID") String groupCode,
			@RequestBody SummaryDto summaryDto) {
		AuthDetailsDto dto = new AuthDetailsDto();
		String response = "";
		try {
			User user = userService.findByUserName(summaryDto.getUserName());
			String pan = user.getPan();
			String gstnUserName = "";
			if (!(CommonUtillity.isEmpty(summaryDto.getGstinId()))) {
				dto = RestClientUtility
						.getRedisData(summaryDto.getGstinId()) != null
								? RestClientUtility
										.getRedisData(summaryDto.getGstinId())
								: new AuthDetailsDto();
			}
			if (!(CommonUtillity
					.isEmpty(summaryDto.getGroupCode().toLowerCase()))) {
				Map<Object, Object> map = RestClientUtility
						.getRedisDataForGSP(summaryDto.getGroupCode());
				dto.setDigigstUserName(
						(String) map.get(Constant.GSP_DIGIGST_USERNAME));
				dto.setAccessToken((String) map.get(Constant.ACCESS_TOKEN));
				dto.setApiKey(((String) map.get(Constant.API_KEY)).getBytes());
				dto.setApiSecret((String) map.get(Constant.API_SECRET));
			}

			byte[] authEK = null;

			if (!(CommonUtillity.isEmpty(dto.getSek()))) {
				authEK = CryptoUtil.decodeBase64StringTOByte(dto.getSek());
				dto.setAuthEK(authEK);
			}
			if (!(CommonUtillity.isEmpty(dto.getUserName()))) {
				gstnUserName = dto.getUserName();
			}

			String host = env.getProperty("gsp.restapi.host");
			String endpointRet = env
					.getProperty("gsp.restapi.gstr3B.version.retFile");

			response = gstnServiceImpl.fileGSTR(dto, summaryDto.getData(),
					summaryDto.getSigneddata(), summaryDto.getSignatureType(),
					pan, gstnUserName, summaryDto.getGstinId(),
					summaryDto.getTaxPeriod(), host, endpointRet, "/gstr3b",
					Constant.RETFILE, true);

			if (response != null && response != "") {
				if (response.contains("ack_num")) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(response).getAsJsonObject();
					gstr1Service.updateAckNum(summaryDto.getGstinId(),
							summaryDto.getTaxPeriod(),
							json.get("ack_num").getAsString());
				} else {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(response).getAsJsonObject();
					JsonObject errorObj = json.get("error").getAsJsonObject();
					response = errorObj.get("message").getAsString();
				}
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException", e);
		} catch (RestClientUtilException e) {
			LOGGER.error("RestClientUtilException", e);
		}
		return response;

	}
}
*/