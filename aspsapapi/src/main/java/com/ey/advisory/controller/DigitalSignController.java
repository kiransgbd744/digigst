/*package com.ey.advisory.asp.controller;

import java.lang.reflect.Type;
import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.aspectj.apache.bcel.classfile.Constant;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ey.advisory.app.services.common.CommonUtility;
import com.ey.advisory.app.services.validation.b2cs.ReturnPeriod;
import com.ey.advisory.asp.domain.DigitalSigModel;
import com.ey.advisory.asp.domain.DigitalSigSubmit;
import com.ey.advisory.asp.domain.UserAccessMapping_dto;
import com.ey.advisory.asp.dto.GroupDto;
import com.ey.advisory.asp.dto.ReturnUpload;
import com.ey.advisory.asp.dto.StatusDto;
import com.ey.advisory.asp.dto.SummaryDto;
import com.ey.advisory.asp.exception.RestCustomException;
import com.ey.advisory.asp.util.CommonRestClientUtility;
import com.ey.advisory.asp.util.HashGenerator;
import com.ey.advisory.asp.util.RedisOperationForSessionObj;
import com.ey.advisory.asp.util.TokenValidationUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


@Controller
@PropertySource("classpath:ASPRestConfig.properties")
@PropertySource("classpath:AdFileter.properties")
public class DigitalSignController {

	private static final Logger LOGGER = Logger.getLogger(DigitalSignController.class);
	private static final String CLASS_NAME = DigitalSignController.class.getName();

	@Autowired
	private Environment env;

	@Autowired
	private CommonRestClientUtility commonRestClientUtility;

	@Value("${asp-restapi.host}")
	private String restHost;

	@Value("${redirectHost}")o
	private String httpsHost;

	@Autowired(required = false)
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private RedisOperationForSessionObj redisSessionUtility;
	
	@Autowired
	private TokenValidationUtility tokenValidationUtility;
	
	@Value("${asp-restbatchapi.host}")
	private String restBatchHost;
	
	@Autowired
	SignGeneratorService signGeneratorService;
	
	// Dongle related
		@RequestMapping(value = "/launchDigitalSignApp")
		public ModelAndView launchDigitalSignApp(HttpServletRequest request, @RequestParam String gstnid, @RequestParam String taxPeriod,
				@RequestParam String gstr, @RequestParam String appKey				
				) {
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "launchDigitalSignApp");
			}

			ModelAndView mv = new ModelAndView("BulkDigitalSignTemplate");//Constant.DIGITAL_SIGN_TEMPLATE);

			String gstr = request.getParameter(Constant.GSTR);
			String gstnid = request.getParameter("gstnid");
			String taxPeriod = request.getParameter("taxPeriod");
			String items = "";
			String appKey= request.getParameter("appKey");
			LOGGER.info("appkey = "+ appKey);
			Object userObj = redisSessionUtility.getValueFromRedis(Constant.CURRENTUSER, (HttpServletRequest) request);
			User user = (User) userObj;
			GroupDto group = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
			String groupCode = group.getGroupCode();
			
			String signValues = user.getUserName()+"_#_"+groupCode+"_#_"+gstnid+"_#_"+taxPeriod;
			
			mv.addObject("username", signValues);
			mv.addObject("appKey",appKey);
			mv.addObject("action", "/submitDigitalSign");
			try {
				JSONObject jObj = new JSONObject();
				jObj.put("appkey", appKey);
				jObj.put("groupCode", groupCode);
				
				//get encrypted apikey,apisecret
				String apiData = commonRestClientUtility.executeRestCall(
						restHost + env.getProperty("asp-getEncryptedApiKeySecret"), request,
						jObj.toString(), HttpMethod.POST);
				
				JSONObject jsonRes = new JSONObject(apiData);
				mv.addObject("apiKey", jsonRes.get("api_key"));
				mv.addObject("apiSecret", jsonRes.get("api_secret"));
			} catch (JSONException e1) {
				LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "launchDigitalSignApp" + e1);
			}
			
			try{
				items = (String) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,
						groupCode+"_"+user.getUserName()+"_"+gstnid+"_"+taxPeriod+"_"+appKey);
				if (gstr.equalsIgnoreCase(Constant.ONE)) {
					gstrType = Constant.GSTR1;
				}
				else if (gstr.equalsIgnoreCase(Constant.TWO)) {
					gstrType = Constant.GSTR2;
				}
			}catch(Exception e){
				if (LOGGER.isDebugEnabled()){
					LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "launchDigitalSignApp" + e);
				}
			}
			
			Map<String, String> gstinDetailsMap = new HashMap<>();
			gstinDetailsMap.put("gstin", gstnid);
			gstinDetailsMap.put("taxPeriod", taxPeriod);
			gstinDetailsMap.put("gstrType", gstrType);
			gstinDetailsMap.put("groupCode", groupCode);
			
			redisTemplate.opsForHash().put(Constant.REDIS_CACHE,groupCode+"_"+user.getUserName()+
					"_"+"APPKEY_GSTIN_DET"+"_"+gstnid+"_"+taxPeriod, gstinDetailsMap);
			else if(gstr.equalsIgnoreCase(Constant.SIX)){
				if (dbFlag != null && !"".equals(dbFlag)
						&& dbFlag.equalsIgnoreCase(Constant.DB))
					flag = false;
				else
					flag = true;
				try {
					ResponseEntity<Object> executeRestApiCall = commonRestClientUtility.executeRestApiCall(
							restHost + env.getProperty("asp-viewGSTR6Summary"),CommonUtility.setInputApi(CommonUtility
									.setYearMonth(request, flag, taxPeriod,gstnid)), request, HttpMethod.POST,Object.class);
			
					LinkedHashMap<String, Object> hashMap = (LinkedHashMap<String,
							Object>) executeRestApiCall.getBody();
					items =  (String) hashMap.get("salesJson");
					redisTemplate.opsForHash().put(Constant.REDIS_CACHE,
							Constant.SIGNING_CONTENT, items);
				} catch (Exception e) {
					
					if (LOGGER.isDebugEnabled()){
						LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "launchDigitalSignApp" + e);
					}

				}
			}else {
				if (dbFlag != null && !"".equals(dbFlag)
						&& dbFlag.equalsIgnoreCase(Constant.DB))
					flag = false;
				else
					flag = true;
				try {
					ResponseEntity<Object> executeRestApiCall = commonRestClientUtility.executeRestApiCall(
							restHost + env.getProperty("asp-viewGSTR2Summary"),CommonUtility.setInputApi(CommonUtility
									.setYearMonth(request, flag, taxPeriod,gstnid)), request, HttpMethod.POST,Object.class);
			
					LinkedHashMap<String, Object> hashMap = (LinkedHashMap<String,
							Object>) executeRestApiCall.getBody();
					items =  (String) hashMap.get("salesJson");
					redisTemplate.opsForHash().put(Constant.REDIS_CACHE,
							Constant.SIGNING_CONTENT, items);
				} catch (Exception e) {
					
					if (LOGGER.isDebugEnabled()){
						LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "launchDigitalSignApp" + e);
					}

				}
			}

			try {
				Security.addProvider(new BouncyCastleProvider());
				String hexString = create256HashString(items);
				mv.addObject("data", hexString);
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()){
					LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "launchDigitalSignApp" + e);
				}
			}
			String url;
			if (httpsHost != null && httpsHost.trim().length() > 0) {
				url = httpsHost;
			} else {
				url = request.getScheme() + "://" + request.getServerName() + ":"
						+ request.getServerPort();
			}
			mv.addObject("url", url);
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "launchDigitalSignApp");
			}

			return mv;
		}

		private String create256HashString(String items) {
			byte[] base64Str = Base64.encode(items.getBytes());

			// create hash of base64 json string
			HashGenerator hashGen = new HashGenerator();

			byte[] generateSha256Hash = hashGen.generateSha256Hash(base64Str);
			 StringBuilder builder = new StringBuilder();
			    for(byte b : generateSha256Hash) {
			           builder.append(String.format("%02x", b));
			    }
			return builder.toString();
		}
		
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getTheSignStatus")
	@ResponseBody
	public String signStatus(HttpServletRequest request,@RequestParam String appKey,@RequestParam String gstin,@RequestParam String taxperiod,@RequestParam String gstrType) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "signStatus");
		}
		GroupDto group = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
		String groupCode = group.getGroupCode();
		
		Object userObj = redisSessionUtility.getValueFromRedis(Constant.CURRENTUSER, (HttpServletRequest) request);
		User user = (User) userObj;
		
		boolean findSign = false;
		Date d = new Date();
		String jsonString = "";
		String signedData = "";
		String userName ="";
		String msg="";
		boolean findData = false;
		Map<String,String> appkeySignedDetails = new HashMap<String, String>();
		JSONObject responseObj = new JSONObject();
		String fileRedisKey= groupCode+"_"+user.getUserName()+"_"+gstin+"_"+taxperiod;
		try {
			
			while (!findSign) {
				//check if signature generated
				appkeySignedDetails = (Map<String,String>) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,fileRedisKey+"_"+"SIGNED_APPKEY_DATA"+"_"+appKey) 
							 != null ?(Map<String,String>) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,fileRedisKey+"_"+"SIGNED_APPKEY_DATA"+"_"+appKey)
									 : appkeySignedDetails;
							 
				signedData = appkeySignedDetails.get("signData");
				
				//check if any exception
							msg = (String) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,fileRedisKey+"_"+"FILE_MSG") 
						!= null ? (String) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,fileRedisKey+"_"+"FILE_MSG"):msg;
						
						
				Date d1 = new Date();
				long sec = (d1.getTime() - d.getTime()) / 1000;
				if (signedData != null && !signedData.equalsIgnoreCase("")) {
					findSign =true;
					findData = true;
					userName = appkeySignedDetails.get("userName");
					break;
				} else if (sec > 900) {

					findSign = true;
				}
				
				else if (msg!=null && !msg.equalsIgnoreCase("")) {
					findData = true;
					jsonString = msg;
					break;
				}

				Thread.sleep(7000);
			}
			// if findData = false: show timeout
			if(!findData){
				jsonString = "{\"status\":\"timeout\",\"msg\":\"Timeout, Please Sign again\"}";
			}
			else if(findSign){
				Map<String, String> gstinDetailsMap = (Map<String, String>) redisTemplate.opsForHash().
						get(Constant.REDIS_CACHE,
								groupCode+"_"+user.getUserName()+"_"+"APPKEY_GSTIN_DET"+appKey);
				
				String summary = (String) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,fileRedisKey+"_"+appKey);
				String gstin = gstinDetailsMap.get("gstin");
				String taxPeriod = gstinDetailsMap.get("taxPeriod");
				String gstrType = gstinDetailsMap.get("gstrType");
				
				SummaryDto dto = new SummaryDto();
				dto.setData(summary);
				dto.setGstinId(gstin);
				dto.setTaxPeriod(taxperiod);
				dto.setGroupCode(groupCode);
				dto.setGstrType(gstrType);
				dto.setUserName(userName);
				dto.setSigneddata(signedData);
				dto.setSignatureType("DSC");
				
				String fileResponse ="";
				if(gstrType.equalsIgnoreCase(Constant.ONE)){
					fileResponse = commonRestClientUtility
							.executeRestCall(
									restHost
											+ env.getProperty("asp-getSignedDataGstr1"),
									request, dto,
									HttpMethod.POST);
				}
				else if(gstrType.equalsIgnoreCase(Constant.GSTR2)){
					fileResponse = commonRestClientUtility
							.executeRestCall(
									restHost
											+ env.getProperty("asp-getSignedDataGstr2"),
									request, dto,
									HttpMethod.POST);
				}else if(gstrType.equalsIgnoreCase(Constant.THREEB)) {
					fileResponse = commonRestClientUtility
							.executeRestCall(
									restHost
											+ env.getProperty("asp-getSignedDataGstr3B"),
									request, dto,
									HttpMethod.POST);
				}
				if(fileResponse!=null && !fileResponse.equalsIgnoreCase("")){
					if(fileResponse.contains("ack_num")){
						JSONObject fileObj = new JSONObject(fileResponse);
						responseObj.put("status", "success");
						responseObj.put("msg", fileObj.get("ack_num"));
						jsonString = responseObj.toString();
						
					}
					else{
						responseObj.put("status", "failed");
						responseObj.put("msg",fileResponse);
						jsonString = responseObj.toString();
					}
				}
				else{
					
					responseObj.put("status", "failed");
					responseObj.put("msg", "An error Occured while Filing ! Please Try after sometime");
					jsonString = responseObj.toString();
				}
			}
			
		} catch (Exception e) {
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "signStatus" + e);
			}

			jsonString = "{\"status\":\"failed\",\"msg\":\"Something went wrong in validation OTP, Please try again after sometime\"}";
		}
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "signStatus");
		}
		//deleting the keys
		redisTemplate.opsForHash().delete(Constant.REDIS_CACHE,fileRedisKey+"_"+appKey);
		redisTemplate.opsForHash().delete(Constant.REDIS_CACHE,
				groupCode+"_"+user.getUserName()+"_"+"APPKEY_GSTIN_DET"+appKey);
		redisTemplate.opsForHash().delete(Constant.REDIS_CACHE,fileRedisKey+"_"+"FILE_MSG");
		redisTemplate.opsForHash().delete(Constant.REDIS_CACHE,fileRedisKey+"_"+"SIGNED_APPKEY_DATA"+"_"+appKey);
		return jsonString;
	}

	@RequestMapping(value = "/submitDigitalSign")
	@ResponseBody
	public String submitDigitalSign(String username, String apikey, 
			String apiSecret, String signedData, String otp, HttpServletRequest request) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "submitDigitalSign");
		}
		
		String message="";
		JSONObject responseObj = new JSONObject();
		String isSuccess = "true";
		
		String[] signValues = username.split("_#_");
		//String userName = signValues[0];
		String groupCode = signValues[1];
		String gstin = signValues[2];
		String taxperiod = signValues[3];
		String fileRedisKey= groupCode+"_"+signValues[0]+"_"+gstin+"_"+taxperiod;
			try {
				SummaryDto signDto = new SummaryDto();
				signDto.setUserName(signValues[0]);
				signDto.setSigneddata(signedData);
				signDto.setOtp(otp);
				signDto.setApiKey(apikey);
				signDto.setApiSecret(apiSecret);
				
				String jsonString = commonRestClientUtility
						.executeRestCall(
								restHost
										+ env.getProperty("asp-signData"),
								request, signDto,
								HttpMethod.POST);
				if(jsonString!=null && !jsonString.equalsIgnoreCase("")){
					JSONObject signObj = new JSONObject(jsonString);
					String appkey = (String) signObj.get("app_key");
					
					Map<String,String> appkeySignedDetails = new HashMap<String, String>();
					String userName = (String) signObj.get("digigst_username");
					String signData = (String) signObj.get("data");
					
					appkeySignedDetails.put("userName", userName);
					appkeySignedDetails.put("signData", signData);
					
					redisTemplate.opsForHash().put(Constant.REDIS_CACHE,fileRedisKey+"_"+"SIGNED_APPKEY_DATA"+"_"+appkey,appkeySignedDetails);
					isSuccess="true";
				}
				else{
					isSuccess = "false";
					responseObj.put("status", "failed");
					responseObj.put("msg", "Something went wrong in validation OTP, Please try again after sometime");
					message = responseObj.toString();
				}
					
			} catch (JSONException e) {
				
				isSuccess = "false";
				message = "{\"status\":\"failed\",\"msg\":\"Something went wrong in validation OTP, Please try again after sometime\"}";
			}

		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "submitDigitalSign");
		}
		redisTemplate.opsForHash().put(Constant.REDIS_CACHE,fileRedisKey+"_"+"FILE_MSG",message);
		return isSuccess;
	}
	
	@RequestMapping(value = "/submitDigitalSign")
	@ResponseBody
	public String submitDigitalSign(String username, String appkey, 
			String signedData,HttpServletRequest request) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "submitDigitalSign");
		}
		
		String message="";
		String isSuccess = "true";
		
		String[] signValues = username.split("_#_");
		String groupCode = signValues[1];
		String gstin = signValues[2];
		String taxperiod = signValues[3];
		String fileRedisKey= groupCode+"_"+signValues[0]+"_"+gstin+"_"+taxperiod;
			try {
				Map<String,String> appkeySignedDetails = new HashMap<String, String>();
				String userName = signValues[0];
				String signData = signedData;
				
				appkeySignedDetails.put("userName", userName);
				appkeySignedDetails.put("signData", signData);
				
				redisTemplate.opsForHash().put(Constant.REDIS_CACHE,fileRedisKey+"_"+"SIGNED_APPKEY_DATA"+"_"+appkey,appkeySignedDetails);
				isSuccess="true";
			} catch (Exception e) {
				isSuccess = "false";
				message = "{\"status\":\"failed\",\"msg\":\"Something went wrong in validating Signature, Please try again after sometime\"}";
			}

		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "submitDigitalSign");
		}
		redisTemplate.opsForHash().put(Constant.REDIS_CACHE,fileRedisKey+"_"+"FILE_MSG",message);
		return isSuccess;
	}


	@RequestMapping(value = "/testUrlSub")
	@ResponseBody
	public String testUrlSub(HttpServletRequest request) {
		String isSuccess = null;
		String gstin = request.getParameter("gstin");
		String key = request.getParameter("key");

		//isSuccess = submitDigitalSign("signed data", gstin, key);
		return isSuccess;
	}

	// Digio Related
	@RequestMapping(value = "/digitalsignature", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigModel> getDocumentIDfromDigio(
			HttpServletRequest request,String gstnid, String taxPeriod) {
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "getDocumentIDfromDigio");
		}
		DigitalSigModel model = null;
		ResponseEntity<DigitalSigModel> responseObject;

		try {
			responseObject = commonRestClientUtility.executeRestApiCall(
					restHost + env.getProperty("asp-digitalsignature"),
					CommonUtility.setYearMonth(request, false, taxPeriod,
							gstnid), HttpMethod.POST, DigitalSigModel.class);
			model = responseObject.getBody();
		} catch (RestCustomException e) {
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "getDocumentIDfromDigio" + e);
			}

		}
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "getDocumentIDfromDigio");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	@RequestMapping(value = "/digitalsignature2", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigModel> getDocumentIDfromDigio2(
			HttpServletRequest request,String gstnid, String taxPeriod) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "getDocumentIDfromDigio2");
		}
		DigitalSigModel model = null;
		ResponseEntity<DigitalSigModel> responseObject;

		try {
			responseObject = commonRestClientUtility.executeRestApiCall(
					restHost + env.getProperty("asp-digitalsignature2"),
					CommonUtility.setYearMonth(request, false, taxPeriod,
							gstnid), HttpMethod.POST, DigitalSigModel.class);
			model = responseObject.getBody();
		} catch (RestCustomException e) {
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "getDocumentIDfromDigio" + e);
			}

		}
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "getDocumentIDfromDigio2");
		}

		return new ResponseEntity<>(model, HttpStatus.OK);

	}

	
	
	@RequestMapping(value = "/digSigSubmit2GSTN", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigSubmit> downloadHashFromDigio(
			HttpServletRequest request,	@RequestParam String docid, @RequestParam String gstnId,
			@RequestParam String taxPeriod) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "downloadHashFromDigio");
		}

		DigitalSigSubmit model = null;
		ResponseEntity<DigitalSigSubmit> responseObject;
		try {
			responseObject = commonRestClientUtility.executeRestApiCall(
					restHost + env.getProperty("asp-digSigSubmit2GSTN"),
					CommonUtility.setYearMonth(request, gstnId, taxPeriod,
							docid), HttpMethod.POST, DigitalSigSubmit.class);
			model = responseObject.getBody();
		} catch (RestCustomException e) {
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "downloadHashFromDigio" + e);
			}

		}
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "downloadHashFromDigio");
		}

		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	@RequestMapping(value = "/digSigSubmit2GSTN2", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigSubmit> downloadHashFromDigio2(
			HttpServletRequest request,	@RequestParam String docid, String gstnId, String taxPeriod) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "downloadHashFromDigio2");
		}
		DigitalSigSubmit model = null;
		ResponseEntity<DigitalSigSubmit> responseObject;
		try {
			responseObject = commonRestClientUtility.executeRestApiCall(
					restHost + env.getProperty("asp-digSigSubmit2GSTN2"),
					CommonUtility.setYearMonth(request, gstnId, taxPeriod,
							docid), HttpMethod.POST, DigitalSigSubmit.class);
			model = responseObject.getBody();
		} catch (RestCustomException e) {
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "downloadHashFromDigio2" + e);
			}

		}
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "downloadHashFromDigio2");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/digSigSubmit7GSTN7", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigSubmit> downloadHashFromDigio7(
			HttpServletRequest request,	@RequestParam String docid, String gstnId, String taxPeriod) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "downloadHashFromDigio7");
		}

		DigitalSigSubmit model = null;
		ResponseEntity<DigitalSigSubmit> responseObject;
		try {
			responseObject = commonRestClientUtility.executeRestApiCall(
					restHost + env.getProperty("asp-digSigSubmit7GSTN7"),
					CommonUtility.setYearMonth(request, gstnId, taxPeriod,
							docid), HttpMethod.POST, DigitalSigSubmit.class);
			model = responseObject.getBody();
		} catch (RestCustomException e) {
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "downloadHashFromDigio7" + e);
			}

		}
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "downloadHashFromDigio7");
		}

		return new ResponseEntity<>(model, HttpStatus.OK);

	}

	@RequestMapping(value = "/digitalsignature7", produces = "application/json")
	@ResponseBody
	public ResponseEntity<DigitalSigModel> getDocumentIDfromDigio7(
			HttpServletRequest request,String gstnid, String taxPeriod) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "getDocumentIDfromDigio7");
		}

		DigitalSigModel model = null;
		ResponseEntity<DigitalSigModel> responseObject;

		try {
			responseObject = commonRestClientUtility.executeRestApiCall(
					restHost + env.getProperty("asp-digitalsignature7"),
					CommonUtility.setYearMonth(request, false, taxPeriod,
							gstnid), HttpMethod.POST, DigitalSigModel.class);
			model = responseObject.getBody();
		} catch (RestCustomException e) {
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "getDocumentIDfromDigio7");
			}

		}
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "getDocumentIDfromDigio7");
		}
		return new ResponseEntity<>(model, HttpStatus.OK);

	}

	*//**
	 * @param request
	 * @param response
	 * @param attributes
	 * @return
	 *//*
	@RequestMapping(value = "/requestSignFileOtp")
	@ResponseBody
	public String requestSignFileOtp(HttpServletRequest request, @RequestParam String gstnId, 
			@RequestParam String taxPeriod, @RequestParam String gstr) {

		LOGGER.info("Entering requestSignFileOtp() " + request);
		GroupDto group = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
		String groupCode = group.getGroupCode(); 
		Object userObj = redisSessionUtility.getValueFromRedis(Constant.CURRENTUSER, (HttpServletRequest) request);
		User user = (User) userObj;
		//JSONObject jsonObject = new JSONObject();
		JSONObject resultObj = new JSONObject();
		String uuid = UUID.randomUUID().toString();
		String appKey = uuid.replaceAll("-", "");
		String items="";
		String result="";
		String output="";
		try {
			jsonObject.put("mobile_number",Constant.ISD_CODE+user.getMobileNo());
			jsonObject.put("groupCode", groupCode);
			jsonObject.put("app_key", appKey);
			jsonObject.put("digigst_username",user.getUserName());
			String jsonSumData="";
			if(gstr.equalsIgnoreCase(Constant.ONE))
				jsonSumData =commonRestClientUtility
					.executeRestCall(restHost + env.getProperty("asp-viewGSTR1Summary"), request,
							CommonUtility
							.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstnId,groupCode)),
							HttpMethod.POST);
			else if(gstr.equalsIgnoreCase(Constant.TWO))
				jsonSumData =commonRestClientUtility
				.executeRestCall(restHost + env.getProperty("asp-viewGSTR2Summary"), request,
						CommonUtility
						.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstnId,groupCode)),
						HttpMethod.POST);
			else if(gstr.equalsIgnoreCase(Constant.THREEB))
				jsonSumData =commonRestClientUtility
				.executeRestCall(restHost + env.getProperty("asp-viewGSTR3BSummary"), request,
						CommonUtility
						.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstnId,groupCode)),
						HttpMethod.POST);
			else if(gstr.equalsIgnoreCase(Constant.SIX))
				jsonSumData =commonRestClientUtility
				.executeRestCall(restHost + env.getProperty("asp-signAndFileGstr6"), request,
						CommonUtility
						.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstnId,groupCode)),
						HttpMethod.POST);
			JSONObject jsonRes = new JSONObject(jsonSumData);
			items=jsonRes.getString(Constant.SUMMARY_DATA);
			
			String signRedisKey=groupCode+"_"+user.getUserName()+"_"+gstnId+"_"+taxPeriod;
			
			redisTemplate.opsForHash().put(Constant.REDIS_CACHE,signRedisKey+"_"+appKey,items);
	
			LOGGER.info("Calling method to get CA OTP");
			LOGGER.debug(restHost + env.getProperty("asp-requestSignFileOtp")+" JSon = "+jsonObject.toString());
			output =commonRestClientUtility.executeRestCall(restHost + env.getProperty("asp-requestSignFileOtp"), request,
					jsonObject.toString(), HttpMethod.POST);
			
			resultObj.put("otp_response", output);
			resultObj.put("app_key", appKey);
			result= resultObj.toString();
		} catch (JSONException e) {
			LOGGER.error("Error requestSignFileOtp() " + request);
		}
		
		return result;
	}
	
	@RequestMapping(value = "/bulkSubmitSign")
	public ModelAndView bulkSubmitSign(HttpServletRequest request,HttpServletResponse response				
			) {
	
		ModelAndView mv = new ModelAndView("bulkSubmitFile");
		LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "bulkSubmitSign");
		try {
			Map<String,String> entityNameMap = new LinkedHashMap<String,String>();
			
			ReturnPeriod retPeriod = (ReturnPeriod) redisSessionUtility.getValueFromRedis(Constant.RETURN_PERIOD, request);
			String taxPeriod = retPeriod == null ? "072017" : retPeriod.getTaxPeriodMMYYYY();
			
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			GroupDto groupdto = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
			String entityListJSON = commonRestClientUtility.executeRestCall
					(restHost + env.getProperty("asp-fetchEntityList"), request, 
							groupdto.getGroupId(),HttpMethod.POST);
			
			String userLevel = getLevel(request);
			
			if (entityListJSON != null) {
				TypeToken<List<Object[]>> token = new TypeToken<List<Object[]>>() {};
				List<Object[]> entityList = gson.fromJson(entityListJSON, token.getType());
				for(Object[] entity : entityList){
					Integer entityId = ((Double)entity[0]).intValue();
					if(userLevel != "L1" && userLevel != "L2"){
							entityNameMap.put(entityId.toString(), (String)entity[1]);
							break;
					}
					if(userLevel == "L1" || userLevel == "L2")
						entityNameMap.put(entityId.toString(), (String)entity[1]);
					}
				mv.addObject("entityNameMap", entityNameMap);
				mv.addObject("financialYear", CommonUtility
						.getYearsTillDateInYYYY(retPeriod.getTaxPeriodMMYYYY()));
				org.json.JSONObject entityNameMapObj = new org.json.JSONObject(entityNameMap);
				mv.addObject("entityNameList", entityNameMapObj);
				
				Map<String, List<String>> applicableYearMonths = CommonUtility.reconYTDMonths(taxPeriod);
				mv.addObject("applicableYearMonths", applicableYearMonths);
				mv.addObject("applicableYears", applicableYearMonths.keySet());
				mv.addObject("applicableMonths", CommonUtility.reconMonthsForYear(taxPeriod));
			}
		}catch (Exception e) {
			LOGGER.error("Error bulkSubmitSign() " + request);
		}
		LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "bulkSubmitSign");
		return mv;
		
	}
	
	private String getLevel(HttpServletRequest request){
		List<UserAccessMapping_dto> accessMapDto = redisSessionUtility.getUserAccessMapping(request);
		StringBuffer strBuf = new StringBuffer();
		for (UserAccessMapping_dto dto : accessMapDto) {

			strBuf.append(dto.getAccessLevel()).append("#");

		}

		String str = strBuf.toString();
		String level = "";
		if (str.contains("L1")) {
			level = "L1";
		} else if (str.contains("L2")) {
			level = "L2";
		} else if (str.contains("L3A")) {
			level = "L3A";
		} else if (str.contains("L3B")) {
			level = "L3B";
		} else if (str.contains("L4A")) {
			level = "L4A";
		} else if (str.contains("L4B")) {
			level = "L4B";
		} else if (str.contains("L4C")) {
			level = "L4C";
		} else if (str.contains("L5")) {
			level = "L5";
		}

		return level;

	}

	@RequestMapping(value = "/getSubmitStatusForEntity", method = RequestMethod.POST)
	@ResponseBody
	public Object getSubmitStatusForEntity(@RequestParam String entity,@RequestParam String returnType,
			@RequestParam String taxPeriod,HttpServletRequest request,HttpServletResponse response){
		LOGGER.debug("Entering " + CLASS_NAME + " Method : getSubmitStatusForEntity");
		Object result=null;
		try {
			GroupDto group = (GroupDto)redisSessionUtility
					.getValueFromRedis(Constant.USER_GROUP,  request);
			
			Object currentUser = redisSessionUtility.getValueFromRedis(Constant.CURRENTUSER, (HttpServletRequest) request);
			User loggedInUser = (User) currentUser;
			
			JsonObject json = new JsonObject();
			
			json.addProperty("entity", entity);
			json.addProperty("returnType", returnType);
			json.addProperty("taxPeriod", taxPeriod);
			json.addProperty("group", group.getGroupCode());
			json.addProperty("userId", loggedInUser.getUserId());
			
			ResponseEntity<Object> submitResponse = commonRestClientUtility.executeRestApiCall(restHost + env.getProperty("asp-getSubmitStatusForEntity"),
					json.toString(),request, HttpMethod.POST, Object.class);

			result = submitResponse.getBody();
		}catch(Exception e) {
			LOGGER.error("Error in getSubmitStatusForEntity() " + request);
		}
		LOGGER.debug("Exiting " + CLASS_NAME + " Method : getSubmitStatusForEntity");
		return result;
	}
	
	@RequestMapping(value = "/displayStatusSubmit")
	public @ResponseBody String displayStatusSubmit(@RequestParam String gstnid,
			@RequestParam String taxPeriod, @RequestParam String returnType,
			HttpServletRequest request) {
		
		LOGGER.info("Entering " + CLASS_NAME + " displayStatusSubmit method for return type " + returnType);

		String returnData = null;
		List<StatusDto> chunkStatusList = null;
		Gson gsonHelper = new GsonBuilder().setDateFormat(Constant.DATE_FORMAT_IN_HOURS).create();
		try {
			
			JsonObject json = new JsonObject();
			
			json.addProperty(Constant.GSTIN, gstnid);
			json.addProperty(Constant.TAX_PERIOD, taxPeriod);
			json.addProperty(Constant.RETURNTYPES, returnType);
			
			String returnUploadMsg = commonRestClientUtility.executeRestCall(
					restHost + env.getProperty("asp-getChunkStatusforSubmit"), request, json.toString(),
					HttpMethod.POST);

			
			if (returnUploadMsg != null && !Constant.EXCEPTION_AT_GSTN_ERROR.equals(returnUploadMsg)) {

				LOGGER.info("Inside getChunkStatusforSubmit obtained for return type" + returnType
						+ " and return upload message is :" + returnUploadMsg);
				
				TypeToken<List<ReturnUpload>> token = new TypeToken<List<ReturnUpload>>() {
				};
				List<ReturnUpload> returnUploadList = gsonHelper.fromJson(returnUploadMsg, token.getType());
				
				if (null != returnUploadList && !returnUploadList.isEmpty()) {

					for (ReturnUpload item : returnUploadList) {
						StatusDto statusDto = new StatusDto();

						if (null != item.getCreatedDt()) {
							// Split time and date
							
							Calendar cal = Calendar.getInstance();
							cal.setTime(item.getCreatedDt());
							cal.add(Calendar.HOUR, 11);
							
							DateFormat formatter = DateFormat.getInstance();
							
							formatter.format(cal.getTime());
							SimpleDateFormat df = new SimpleDateFormat(Constant.DATE_FORMAT_IN_HOURS);
							String[] timestamp = df.format(cal.getTime()).split(" ");
							
							String date = timestamp[0];
							String time = timestamp[1];
							statusDto.setDate(date);
							
							if(null!=time){
								statusDto.setTime(time.substring(0, time.indexOf(".")));
							}
							
						}
						statusDto.setStatus(item.getGstnStatus());
						statusDto.setRefId(item.getRefId());
						statusDto.setErrorReport("");
						statusDto.setErrorCount(item.getErrorCount());

						if (null != chunkStatusList) {
							chunkStatusList.add(statusDto);
						} else {
							chunkStatusList = new ArrayList<>();
							chunkStatusList.add(statusDto);
						}

					}
					returnData = gsonHelper.toJson(chunkStatusList);
				}

				LOGGER.info("chunkStatusList is formed for for return type " + returnType);
			}
		} catch (Exception e) {

			LOGGER.error("Exception in " + CLASS_NAME + " of method displayStatusSubmit for return type " + returnType, e);

			returnData = gsonHelper.toJson(Constant.EXCEPTION_AT_GSTN_ERROR);

			return returnData;
		}
		LOGGER.info("Exiting " + CLASS_NAME + " of method displayStatusSubmit for return type " + returnType);
		return returnData;
	}
	
	@RequestMapping(value = "/validateBulkSubmitGstrtoGstn")
	public @ResponseBody Object validateBulkSubmitGstrtoGstn(@RequestParam String gstnid, @RequestParam String taxPeriod,
			 @RequestParam String returnType,
			HttpServletRequest request) throws Exception {


		if (LOGGER.isDebugEnabled()){
			LOGGER.debug("Entering " + CLASS_NAME + " Method : validateBulkSubmitGstrtoGstn");
		}
		GroupDto group = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
		String groupCode = group.getGroupCode();
		Map<String,String> responseMap = null;
		try {
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>() {}.getType();
			List<String> gstins = gson.fromJson(gstnid, listType);
			
			if(gstins!=null && !gstins.isEmpty()) {
				responseMap = new LinkedHashMap<String, String>();
				for(String gstin: gstins) {
					String status = null;
					HashMap<String, String> statusMap = tokenValidationUtility.validateGspGstnAuthTokens(groupCode, gstin);
		
					if (statusMap.get("GSP").equalsIgnoreCase("Active")) {
						if (!statusMap.get("GSTN").equalsIgnoreCase("Active")) {
							status= "GSTNToken";
						}
					} else {
						status =  "GSPToken";
					}
					
					if(status==null) {
						if(returnType.equalsIgnoreCase("Gstr1"))
							status = commonRestClientUtility.executeRestCall(restHost + env.getProperty("asp-validationSubmitGstr1"),
									request, CommonUtility.setYearMonth(request, false, taxPeriod, gstin), HttpMethod.POST);
						else if(returnType.equalsIgnoreCase("Gstr3B"))
							status = commonRestClientUtility.executeRestCall(restHost + env.getProperty("asp-validationSubmitGstr3b"),
									request, CommonUtility.setYearMonth(request, false, taxPeriod, gstin), HttpMethod.POST);
					}
					
					responseMap.put(gstin, status);
				}

			}
		}
		catch (Exception exception) {

			if (LOGGER.isDebugEnabled()){
				LOGGER.debug("Entering " + CLASS_NAME + " Method : validateBulkSubmitGstrtoGstn Exception" + exception);
			}


		}


		if (LOGGER.isDebugEnabled()){
			LOGGER.debug("Exiting " + CLASS_NAME + " Method : validateBulkSubmitGstrtoGstn");
		}

		return responseMap;

	}
	
	@RequestMapping(value = "/checkSubmitStateBeforeFiling")
	public @ResponseBody Object checkSubmitStateBeforeFiling(HttpServletRequest request,@RequestParam String gstnid, @RequestParam String taxPeriod,
			@RequestParam String gstr) throws Exception {
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug("Entering " + CLASS_NAME + " Method : checkSubmitStateBeforeFiling");
		}
		GroupDto group = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
		String groupCode = group.getGroupCode();
		Map<String,String> responseMap = null;	
		
		try {
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>() {}.getType();
			List<String> gstins = gson.fromJson(gstnid, listType);
			
			if(gstins!=null && !gstins.isEmpty()) {
				responseMap = new LinkedHashMap<String, String>();
				for(String gstin: gstins) {
					String status = null;
					HashMap<String, String> statusMap = tokenValidationUtility.validateGspGstnAuthTokens(groupCode, gstin);
		
					if (statusMap.get("GSP").equalsIgnoreCase("Active")) {
						if (!statusMap.get("GSTN").equalsIgnoreCase("Active")) {
							status= "GSTNToken";
						}
					} else {
						status =  "GSPToken";
					}
					
					if(status==null) {
						if(gstr.equalsIgnoreCase("gstr1"))
							status = commonRestClientUtility.executeRestCall(restHost + env.getProperty("asp-gstr1SubmitCheck"), request,
									CommonUtility.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstin)),
									HttpMethod.POST);
						else if(gstr.equalsIgnoreCase("gstr3b"))
							status = commonRestClientUtility.executeRestCall(restHost + env.getProperty("asp-gstr3BSubmitCheck"), request,
									CommonUtility.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstin)),
									HttpMethod.POST);
					}
					
					responseMap.put(gstin, status);
				}

			}
		}
		catch (Exception exception) {

			if (LOGGER.isDebugEnabled()){
				LOGGER.debug("Entering " + CLASS_NAME + " Method : checkSubmitStateBeforeFiling Exception" + exception);
			}


		}


		if (LOGGER.isDebugEnabled()){
			LOGGER.debug("Exiting " + CLASS_NAME + " Method : checkSubmitStateBeforeFiling");
		}

		return responseMap;
		
	}
	
	@RequestMapping(value = "/bulkFile")
	public @ResponseBody Object bulkFile(HttpServletRequest request,@RequestParam String gstnid, @RequestParam String taxPeriod,
			@RequestParam String gstr) throws Exception {
		
		GroupDto group = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
		String groupCode = group.getGroupCode(); 
		Object userObj = redisSessionUtility.getValueFromRedis(Constant.CURRENTUSER, (HttpServletRequest) request);
		User user = (User) userObj;
		JSONObject resultObj = new JSONObject();
		String uuid = UUID.randomUUID().toString();
		String appKey = uuid.replaceAll("-", "");
		String result="";
		try {
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>() {}.getType();
			List<String> gstins = gson.fromJson(gstnid, listType);
			
			for(String gstin : gstins) {
				
				String jsonSumData="";
				if(gstr.equalsIgnoreCase("gstr1"))
					jsonSumData =commonRestClientUtility
						.executeRestCall(restHost + env.getProperty("asp-viewGSTR1Summary"), request,
								CommonUtility
								.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstin,groupCode)),
								HttpMethod.POST);
				else if(gstr.equalsIgnoreCase("gstr3b"))
					jsonSumData = commonRestClientUtility
						.executeRestCall(restHost + env.getProperty("asp-viewGSTR3BSummary"), request,
							CommonUtility
							.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstin,groupCode)),
							HttpMethod.POST);
				
				JSONObject summaryResponse = new JSONObject(jsonSumData);
				String summary=summaryResponse.getString(Constant.SUMMARY_DATA);
					String signRedisKey=groupCode+"_"+user.getUserName()+"_"+gstin+"_"+taxPeriod;
					redisTemplate.opsForHash().put(Constant.REDIS_CACHE,signRedisKey+"_"+appKey,summary);
			}
			
			resultObj.put("app_key", appKey);
			result= resultObj.toString();
		} catch (JSONException e) {
			LOGGER.error("Error requestSignFileOtp() " + request);
		}
	    return result;
	}
	
	
	@RequestMapping(value = "/launchBulkDigitalSignApp")
	public ModelAndView launchBulkDigitalSignApp(HttpServletRequest request, @RequestParam String gstnid, @RequestParam String taxPeriod,
			@RequestParam String gstr, @RequestParam String appKey				
			) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "launchBulkDigitalSignApp");
		}

		ModelAndView mv = new ModelAndView("BulkDigitalSignTemplate");
		try {
			List<String> hexStringList= new LinkedList<String>();
			User user = (User)redisSessionUtility.getValueFromRedis(Constant.CURRENTUSER, (HttpServletRequest) request);
			String useranme = user != null ? user.getUserName() : "UNKNOWN_USER";
			
			LOGGER.info("Digital Signature Info = "+ useranme+"_#_"+gstnid+"_#_"+taxPeriod);
			
			GroupDto group = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
			String groupCode = group != null? group.getGroupCode() :"NA";
			
			LOGGER.info("Digital Signature Info1 = "+ user.getUserName()+"_#_"+groupCode+"_#_"+gstnid+"_#_"+taxPeriod);
			LOGGER.info("appkey = "+ appKey);
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>() {}.getType();
			List<String> gstins = Arrays.asList(gstnid.split(","));
			LOGGER.info("Digital Signature Info2 = "+ user.getUserName()+"_#_"+groupCode+"_#_"+gstnid+"_#_"+taxPeriod +"_#_"+gstins);
			
			String signValues = user.getUserName()+"_#_"+groupCode+"_#_"+gstnid+"_#_"+taxPeriod;
			
			LOGGER.info("appkey = "+ appKey);
			
			mv.addObject("username", signValues);
		
			//get encrypted apikey,apisecret
			String apiData = commonRestClientUtility.executeRestCall(
					restHost + env.getProperty("asp-getEncryptedApiKeySecret"), request,
					jObj.toString(), HttpMethod.POST);
			
			JSONObject jsonRes = new JSONObject(apiData);
			mv.addObject("appKey", appKey);
			mv.addObject("action", "/bulkSubmitDigitalSign");
			
			if(gstr.equalsIgnoreCase("gstr1")) {
				
				Security.addProvider(new BouncyCastleProvider());
				
				for(String gstin : gstins) {
				
					String summary = (String) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,
							groupCode+"_"+user.getUserName()+"_"+gstin+"_"+taxPeriod+"_"+appKey);
					
					String hexString = create256HashString(summary);
					hexStringList.add(hexString);
				}
			}
			String hexStrings = String.join("####", hexStringList);
			mv.addObject("data", hexStrings);
			
		}catch (Exception e1) {
			LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "launchBulkDigitalSignApp" + e1);
		}
		String url;
		if (httpsHost != null && httpsHost.trim().length() > 0) {
			url = httpsHost;
		} else {
			url = request.getScheme() + "://" + request.getServerName() + ":"
					+ request.getServerPort();
		}
		mv.addObject("url", url);
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "launchBulkDigitalSignApp");
		}

		return mv;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getTheBulkSignStatus")
	@ResponseBody
	public String getTheBulkSignStatus(HttpServletRequest request,@RequestParam String appKey,@RequestParam String gstin,@RequestParam String taxperiod,@RequestParam String gstrType) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "getTheBulkSignStatus");
		}
		GroupDto group = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
		String groupCode = group.getGroupCode();
		
		Object userObj = redisSessionUtility.getValueFromRedis(Constant.CURRENTUSER, (HttpServletRequest) request);
		User user = (User) userObj;
		
		boolean findSign = false;
		Date d = new Date();
		String jsonString = "";
		String signedData = "";
		String userName ="";
		String msg="";
		boolean findData = false;
		Map<String,String> appkeySignedDetails = new HashMap<String, String>();
		String fileRedisKey= groupCode+"_"+user.getUserName()+"_"+gstin+"_"+taxperiod;
		
		JSONArray responseArray = null;
		try {
			
			while (!findSign) {
				//check if signature generated
				appkeySignedDetails = (Map<String,String>) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,fileRedisKey+"_"+"SIGNED_APPKEY_DATA"+"_"+appKey) 
							 != null ?(Map<String,String>) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,fileRedisKey+"_"+"SIGNED_APPKEY_DATA"+"_"+appKey)
									 : appkeySignedDetails;
							 
				signedData = appkeySignedDetails.get("signData");
				
				//check if any exception
							msg = (String) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,fileRedisKey+"_"+"FILE_MSG") 
						!= null ? (String) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,fileRedisKey+"_"+"FILE_MSG"):msg;
						
						
				Date d1 = new Date();
				long sec = (d1.getTime() - d.getTime()) / 1000;
				if (signedData != null && !signedData.equalsIgnoreCase("")) {
					findSign =true;
					findData = true;
					userName = appkeySignedDetails.get("userName");
					break;
				} else if (sec > 900) {

					findSign = true;
				}
				
				else if (msg!=null && !msg.equalsIgnoreCase("")) {
					findData = true;
					jsonString = msg;
					break;
				}

				Thread.sleep(7000);
			}
			// if findData = false: show timeout
			if(!findData){
				jsonString = "{\"status\":\"timeout\",\"msg\":\"Timeout, Please Sign again\"}";
			}
			else if(findSign){
				responseArray = new JSONArray();
				
				List<String> gstins = Arrays.asList(gstin.split(","));
				int index=0;
				String[] signatures = signedData.split("@@@@");
				for(String gstn : gstins) {
					
					JSONObject responseObj = new JSONObject();
					
					String summaryKey = groupCode+"_"+user.getUserName()+"_"+gstn+"_"+taxperiod;
					String summary = (String) redisTemplate.opsForHash().get(Constant.REDIS_CACHE,summaryKey+"_"+appKey);
					
					redisTemplate.opsForHash().delete(Constant.REDIS_CACHE,summaryKey+"_"+appKey);
					
					SummaryDto dto = new SummaryDto();
					dto.setData(summary);
					dto.setGstinId(gstn);
					dto.setTaxPeriod(taxperiod);
					dto.setGroupCode(groupCode);
					dto.setGstrType(gstrType);
					dto.setUserName(userName);
					dto.setSigneddata(signatures[index]);
					dto.setSignatureType("DSC");
				
					String fileResponse ="";
					if(gstrType.equalsIgnoreCase("gstr1")){
						fileResponse = commonRestClientUtility
								.executeRestCall(
										restHost
												+ env.getProperty("asp-getSignedDataGstr1"),
										request, dto,
										HttpMethod.POST);
					}
					else if(gstrType.equalsIgnoreCase("gstr3b")) {
						fileResponse = commonRestClientUtility
								.executeRestCall(
										restHost
												+ env.getProperty("asp-getSignedDataGstr3B"),
										request, dto,
										HttpMethod.POST);
					}
					
					if(fileResponse!=null && !fileResponse.equalsIgnoreCase("")){
						if(fileResponse.contains("ack_num")){
							JSONObject fileObj = new JSONObject(fileResponse);
							responseObj.put("gstin", gstn);
							responseObj.put("status", "success");
							responseObj.put("msg", fileObj.get("ack_num"));
							responseArray.put(responseObj.toString());
							
						}
						else{
							responseObj.put("gstin", gstn);
							responseObj.put("status", "failed");
							responseObj.put("msg",fileResponse);
							responseArray.put(responseObj.toString());
						}
					}
					else{
						responseObj.put("gstin", gstn);
						responseObj.put("status", "failed");
						responseObj.put("msg", "An error Occured while Filing ! Please Try after sometime");
						responseArray.put(responseObj.toString());
					}
					
					index++;
				}
				
				jsonString = responseArray.toString();
			}
			
		} catch (Exception e) {
			
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug(Constant.LOGGER_ERROR + CLASS_NAME + Constant.LOGGER_METHOD + "signStatus" + e);
			}

			jsonString = "{\"status\":\"failed\",\"msg\":\"Something went wrong in validation OTP, Please try again after sometime\"}";
		}
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "signStatus");
		}
		//deleting the keys
		redisTemplate.opsForHash().delete(Constant.REDIS_CACHE,fileRedisKey+"_"+appKey);
		redisTemplate.opsForHash().delete(Constant.REDIS_CACHE,
				groupCode+"_"+user.getUserName()+"_"+"APPKEY_GSTIN_DET"+appKey);
		redisTemplate.opsForHash().delete(Constant.REDIS_CACHE,fileRedisKey+"_"+"FILE_MSG");
		redisTemplate.opsForHash().delete(Constant.REDIS_CACHE,fileRedisKey+"_"+"SIGNED_APPKEY_DATA"+"_"+appKey);
		return jsonString;
	}
	
	@RequestMapping(value = "/launchBulkDigiSignAppTEST")
	@ResponseBody
	public String launchBulkDigiSignAppTEST(HttpServletRequest request,@RequestParam String appKey,@RequestParam String gstin,@RequestParam String taxperiod,@RequestParam String gstrType,
			@RequestParam String dscPassword) {
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "launchBulkDigitalSignApp");
		}

		String sign = signGeneratorService.generatePKCS7Sign(dscPassword, "C:\\Windows\\System32\\eps2003csp11.dll", "8d2b0bda056f677826697f82ff55dbc4039b846d8821d3cf18c773bac3723ec6");
		System.out.println(sign);
		
		return sign;
	}
	
	@RequestMapping(value = "/submitBulkGstrtoGstn")
	public @ResponseBody Map<String, Map<String, String>> submitBulkGstrtoGstn(@RequestParam String gstnid,
			@RequestParam String taxPeriod,@RequestParam String returnType, HttpServletRequest request)throws Exception {
		Map<String, Map<String, String>> finalMap = new HashMap<>();
		try {
			
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>() {}.getType();
			List<String> gstins = gson.fromJson(gstnid, listType);
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug("Entering " + CLASS_NAME + " Method : submitBulkGstrtoGstn");
			}
			
			
			GroupDto group = (GroupDto)redisSessionUtility.getValueFromRedis(Constant.USER_GROUP,  request);
			String groupCode = group.getGroupCode();
			
			for(String gstin: gstins) {
				Map<String, String> map = new HashMap<>();
				
				ResponseEntity<Object> executeRestApiCall = null;
				if(returnType.equalsIgnoreCase("gstr1"))
					executeRestApiCall = commonRestClientUtility.executeRestApiCall(
							restHost + env.getProperty("asp-submitGstr1"),
							CommonUtility.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstin,groupCode)), request,
							HttpMethod.POST, Object.class);
				else if(returnType.equalsIgnoreCase("gstr3b"))
					executeRestApiCall = commonRestClientUtility.executeRestApiCall(
							restHost + env.getProperty("asp-submitGstr3b"),
							CommonUtility.setInputApi(CommonUtility.setYearMonth(request, false, taxPeriod, gstin,groupCode)), request,
							HttpMethod.POST, Object.class);
				
				LinkedHashMap<String, Object> hashMap = (LinkedHashMap<String, Object>) executeRestApiCall.getBody();
				String submitStatus;
				
				submitStatus = (String) hashMap.get("submitStatus");
				String refId = (String) hashMap.get("refId");
				
				if (submitStatus.equalsIgnoreCase(Constant.SUCCESS)) {
	
					map.put("refId", refId);
					map.put("submitStatus", submitStatus);
					
					insertTransactionIdPollingSubmit(groupCode, Constant.TRANSACTIONIDPOLLINGSUBMIT, Constant.repeatInterval, 
							Constant.repeatCount); 
	
	
				} else {
					map.put("submitStatus", submitStatus);
				}
				
				finalMap.put(gstin,map);
			}
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug("Executed " + CLASS_NAME + " Method : submitGstr1toGstn");
			}
		}catch(Exception e) {
			
		}
		return finalMap;
	}
	
	
	@SuppressWarnings("unchecked")
	private void insertTransactionIdPollingSubmit(String groupCode,String jobName, int repeatinterval, int repeatcount) {
		try {
			LOGGER.info("Inside insertTransactionIdPollingSubmit method");
			org.json.simple.JSONObject json = new org.json.simple.JSONObject(); 
			json.put("groupEntityId", groupCode);
			json.put("jobName", jobName);
			json.put("repeatCount", repeatcount);
			json.put("timeInterval", repeatinterval);
			
			ResponseEntity<String> result = commonRestClientUtility.executeRestApiCall(restBatchHost+ env.getProperty("asp-returnSubmitFilingDetails"),
							json, HttpMethod.POST, String.class);
		} catch (Exception e) {
			LOGGER.error(
					"Exception in insertTransactionIdPollingSubmit method : "
							+ e, e);
		}
	}
	
	@RequestMapping(value = "/bulkSubmitDigitalSign")
	@ResponseBody
	public String bulkSubmitDigitalSign(String username, String appkey, 
			String signedData, HttpServletRequest request) {
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_ENTERING + CLASS_NAME + Constant.LOGGER_METHOD + "submitDigitalSign");
		}
		
		String message="";
		String isSuccess = "true";
		
		String[] signValues = username.split("_#_");
		String groupCode = signValues[1];
		String gstin = signValues[2];
		String taxperiod = signValues[3];
		String fileRedisKey= groupCode+"_"+signValues[0]+"_"+gstin+"_"+taxperiod;
			try {
				Map<String,String> appkeySignedDetails = new HashMap<String, String>();
				String userName = signValues[0];
				String signData = signedData;
				
				appkeySignedDetails.put("userName", userName);
				appkeySignedDetails.put("signData", signData);
				
				redisTemplate.opsForHash().put(Constant.REDIS_CACHE,fileRedisKey+"_"+"SIGNED_APPKEY_DATA"+"_"+appkey,appkeySignedDetails);
				isSuccess="true";
			} catch (Exception e) {
				isSuccess = "false";
				message = "{\"status\":\"failed\",\"msg\":\"Something went wrong in validation Signature, Please try again after sometime\"}";
			}

		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(Constant.LOGGER_EXITING + CLASS_NAME + Constant.LOGGER_METHOD + "submitDigitalSign");
		}
		redisTemplate.opsForHash().put(Constant.REDIS_CACHE,fileRedisKey+"_"+"FILE_MSG",message);
		return isSuccess;
	}
}*/