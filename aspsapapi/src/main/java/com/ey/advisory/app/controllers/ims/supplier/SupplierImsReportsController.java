package com.ey.advisory.app.controllers.ims.supplier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.dashboard.apiCall.ApiCallDashboardReqDto;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.ZipGenStatusRepository;
import com.ey.advisory.app.service.ims.supplier.SupplierImsApiCallService;
import com.ey.advisory.app.service.ims.supplier.SupplierImsGstinDetailsDto;
import com.ey.advisory.app.service.ims.supplier.SupplierImsTaxPeriodDetailsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.InitiateGetCallDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SupplierImsReportsController {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("SupplierImsApiCallServiceImpl")
	private SupplierImsApiCallService supplierImsApiCallService;

	@Autowired
	@Qualifier("ZipGenStatusRepository")
	ZipGenStatusRepository zipGenStatusRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
 
	
	
	@PostMapping(value = "/ui/getSupplierImsStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGSTINsandStatus(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Entered API Call Dashboard with input %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();
			ApiCallDashboardReqDto reqDashBoardDto = gson.fromJson(reqJson,
					ApiCallDashboardReqDto.class);

			List<String> gstnsList = new ArrayList<>();
			List<String> regTypeList = new ArrayList<>();
			Map<String, String> stateNames = new HashMap<>();
			Map<String, String> gstnRegMap = new HashMap<>();

			if ((reqDashBoardDto.getEntityId() == null
					|| reqDashBoardDto.getGstins()==null) && reqDashBoardDto.getFy() == null) {

				String msg = "Entity Id, Financial year,"
						+ " should be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			

			Long entityId = reqDashBoardDto.getEntityId();

			String financialPeriod = reqDashBoardDto.getFy();

			if (reqDashBoardDto.getGstins() != null
					&& !reqDashBoardDto.getGstins().isEmpty()) {

				gstnsList = reqDashBoardDto.getGstins();
				List<GSTNDetailEntity> entity = gSTNDetailRepository
						.findRegTypeByGstinList(gstnsList);

				regTypeList = entity.stream()
						.map(GSTNDetailEntity::getRegistrationType)
						.collect(Collectors.toList());

				for (int i = 0; i < gstnsList.size(); i++) {
					gstnRegMap.put(gstnsList.get(i), regTypeList.get(i));
				}

				stateNames = entityService.getStateNames(gstnsList);

				Collections.sort(gstnsList);

			} else {

				List<Object[]> gstnObject = gSTNDetailRepository
						.getGstinBasedOnRegTypeforACD(entityId,
								GenUtil.getRegTypesBasedOnTypeForACDIms());

				if (gstnObject == null || gstnObject.isEmpty()) {
					String msg = String.format(
							"No Active Gstn's For Selected Entity Id %s ",
							entityId);
					LOGGER.error(msg);
					throw new AppException(msg);
				}

				gstnRegMap = gstnObject.stream().collect(Collectors
						.toMap(obj -> (String) obj[0], obj -> (String) obj[1]));


				gstnsList = gstnRegMap.keySet().stream()
						.collect(Collectors.toList());

				stateNames = entityService.getStateNames(gstnsList);

				Collections.sort(gstnsList);
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = "EntityServiceImpl"
						+ ".getGSTINsForEntity Preparing Response Object";
				LOGGER.debug(msg);
			}
			//gstin, returnperiod,Api_section=supplier_ims,created_on=max lena hai,
					//GET_TYPE=B2B,B2BA,IS_DELETE=FALSE,IMS_RETURN_TYPE=GSTR1,GSTR1A

			String startMonth = "04";
			String endMonth = "03";
			String appendMonthYear = null;
			String appendMonthYear1 = null;
			if (financialPeriod != null && !financialPeriod.isEmpty()) {
				String[] arrOfStr = financialPeriod.split("-", 2);
				appendMonthYear = arrOfStr[0] + startMonth;
				appendMonthYear1 = "20" + arrOfStr[1] + endMonth;
			}
			int derivedStartPeriod = Integer.parseInt(appendMonthYear);

			int derivedEndPeriod = Integer.parseInt(appendMonthYear1);
							
			//GstnGetStatusEntity

			List<GetAnx1BatchEntity> getBatchEntityDetails = batchRepo
					.findBySgstinAndDerTaxPeriodAndApiSection(gstnsList, derivedStartPeriod,
							derivedEndPeriod,"SUPPLIER_IMS");

			List<String> taxPeriods;

			taxPeriods = GenUtil.extractTaxPeriodsFromFY(financialPeriod, "");

			List<SupplierImsGstinDetailsDto> sGstinTaxperiodDetails = null;

			if (!getBatchEntityDetails.isEmpty()) {
				sGstinTaxperiodDetails = supplierImsApiCallService
						.getTaxPeriodDetailsIms(getBatchEntityDetails);

				sGstinTaxperiodDetails.stream().forEach(
						x -> populateDefaultStatus(x.getTaxPeriodDetails(),
								taxPeriods));

				List<String> gstinsWithGetCall = new ArrayList<>();

				gstinsWithGetCall = sGstinTaxperiodDetails.stream()
						.map(o -> o.getGstin()).collect(Collectors.toList());

				gstnsList.removeAll(gstinsWithGetCall);

				List<SupplierImsGstinDetailsDto> notInitDto = populateDefaultValues(
						gstnsList, taxPeriods);

				sGstinTaxperiodDetails.addAll(notInitDto);

			} else {
				sGstinTaxperiodDetails = populateDefaultValues(gstnsList,
						taxPeriods);
			}
			for (SupplierImsGstinDetailsDto o : sGstinTaxperiodDetails) {
				// sGstinTaxperiodDetails.forEach(o -> {
				o.setAuthStatus(authTokenService
						.getAuthTokenStatusForGstin(o.getGstin()));
				o.setRegistrationType(gstnRegMap.get(o.getGstin()));
				o.setStateName(stateNames.get(o.getGstin()));
				// });
			}

			Collections.sort(sGstinTaxperiodDetails,
					Comparator.comparing(SupplierImsGstinDetailsDto::getGstin));

			SupplierImsGstinDetailsDto apiDtls = new SupplierImsGstinDetailsDto();
			apiDtls.setApiGstinDetails(sGstinTaxperiodDetails);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(apiDtls);
			gstinResp.add("gstins", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
	@RequestMapping(value = "/ui/getSupplierImsConsolidatedReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadSummaryReport(
			@RequestBody String jsonString, HttpServletResponse response) {

		/*JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();*/
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
    

		Gstr1GetInvoicesReqDto dto = gson.fromJson(requestObject.toString(),
				Gstr1GetInvoicesReqDto.class);
		List<InitiateGetCallDto> gstinTaxPeiordList = dto
				.getGstinTaxPeiordList();

       if(dto.getFinYear()!=null){
    	   
   		String fy = dto.getFinYear();

		String[] year = fy.split("-");
		String stYear = year[0];
		String endYear = "20" + year[1];

		gstinTaxPeiordList.forEach(o -> {
			List<String> taxPeriods = new ArrayList<>();
			o.getTaxPeriodList().forEach(tp -> {
				String taxPeriod = "";
				
					taxPeriod = (Integer.valueOf(tp) >= 4
							&& Integer.valueOf(tp) <= 12) ? tp + stYear
									: tp + endYear;
				
				taxPeriods.add(taxPeriod);
			});

			o.setTaxPeriodList(taxPeriods);

		});
       }

		String obj = gson.toJson(dto);

		requestObject = JsonParser.parseString(obj)
				.getAsJsonObject();
		
			
			Gstr1GetInvoicesReqDto criteria = gson.fromJson(requestObject.toString(),
					Gstr1GetInvoicesReqDto.class);
			
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			
			entity.setReqPayload(requestObject.toString());
			entity.setCreatedBy(userName);
			entity.setGstins(GenUtil.convertStringToClob(
					String.join(",", criteria.getGstins())));
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setDataType("Supplier IMS");
			entity.setType(criteria.getType());
			entity.setReportCateg("Consolidated Report – Action Wise");
			
            if("imsGetCall".equalsIgnoreCase(criteria.getType())){
            	
    			entity.setFyYear(criteria.getFinYear());

            }else if("summaryEntityLevel".equalsIgnoreCase(criteria.getType())){
            	
    			entity.setTaxPeriod(criteria.getReturnPeriod());
            	entity.setTableType(String.join(",", criteria.getTableType()));
    			entity.setGstReturn(String.join(",", criteria.getReturnTypes()));
            	
            }else if("summaryGstinLevel".equalsIgnoreCase(criteria.getType())){
            	
    			entity.setTaxPeriod(criteria.getReturnPeriod());

            	
            }else if ("imsRecords".equalsIgnoreCase(criteria.getType())) {

				entity.setUsrAcs1(String.join(",", criteria.getActionType()));
				entity.setTableType(String.join(",", criteria.getTableType()));
    			entity.setGstReturn(String.join(",", criteria.getReturnTypes()));
            	
				
				if (((criteria.getFromPeriod() != null)
						&& (!criteria.getFromPeriod().isEmpty()))
						|| (criteria.getToPeriod() != null)
								&& (!criteria.getToPeriod().isEmpty())) {

					entity.setTaxPeriodFrom(criteria.getFromPeriod().substring(2, 6)
							+ criteria.getFromPeriod().substring(0, 2));
					entity.setTaxPeriodTo(criteria.getToPeriod().substring(2, 6)
							+ criteria.getToPeriod().substring(0, 2));
				}

			}
            List<String> reportTypes = criteria.getReportTypes();

			if (reportTypes != null && !reportTypes.isEmpty()) {
				
				 if (reportTypes.containsAll(Arrays.asList("A", "R", "P", "N"))) {
				        entity.setReportType("A,R,P,N");
				    }else if(reportTypes.containsAll(Arrays.asList("A"))){
				        entity.setReportType("A");
				    	
				    }else if (reportTypes.containsAll(Arrays.asList("R"))) {
						entity.setReportType("R");
					} else if (reportTypes.containsAll(Arrays.asList("P"))) {
						entity.setReportType("P");
					} else if (reportTypes.containsAll(Arrays.asList("N"))) {
						entity.setReportType("N");
					} /*else if (reportTypes.contains(Arrays.asList("Not Eligible Records"))) {
						entity.setReportType("Not Eligible Records");
					}else if (reportTypes.contains(Arrays.asList("Rejected - GSTR-3B Liability"))) {
						entity.setReportType("Rejected - GSTR-3B Liability");
					}*/
			}

			entity = fileStatusDownloadReportRepo.save(entity);
			Long id = entity.getId();

			jobParams.addProperty("id", id);
			if("A,R,P,N".equalsIgnoreCase(entity.getReportType())){
				jobParams.addProperty("reportType", "All Records");
				
			}else if("A".equalsIgnoreCase(entity.getReportType())){
				jobParams.addProperty("reportType", "Accepted Records");
				
			}else if("R".equalsIgnoreCase(entity.getReportType())){
				jobParams.addProperty("reportType", "Rejected Records");

			}else if("P".equalsIgnoreCase(entity.getReportType())){
				jobParams.addProperty("reportType", "Pending Records");

			}else if("N".equalsIgnoreCase(entity.getReportType())){
				jobParams.addProperty("reportType", "No Action Records");
	
			}
			
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.CONSOLIDATED_SUPPLIER_IMS_REPORT, jobParams.toString(),
					userName, 1L, null, null);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = "Unexpected error occured in Consolidated Supplier"
					+ " Ims Download Async Report";
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", e.getMessage());
			LOGGER.error(errMsg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}
	
	@RequestMapping(value = "/ui/getSupplierImsDetailedSummaryReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadImsSummaryReport(
			@RequestBody String jsonString, HttpServletResponse response) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			Gstr1GetInvoicesReqDto criteria = gson.fromJson(json,
					Gstr1GetInvoicesReqDto.class);
			
			
			LOGGER.debug("SupplierImsReportsController Report Type : "+criteria.getReportType());

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			entity.setCreatedBy(userName);
			entity.setGstins(GenUtil.convertStringToClob(
					String.join(",", criteria.getGstins())));
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setDataType("Supplier IMS");
			entity.setReportCateg("Detailed Summary Report");
			entity.setTableType(String.join(",", criteria.getTableType()));
			entity.setGstReturn(String.join(",", criteria.getReturnTypes()));
			entity.setTaxPeriod(criteria.getReturnPeriod());
			entity.setType(criteria.getType());


			if ("Detailed Summary Report".equalsIgnoreCase(criteria.getReportType())) {
				entity.setReportType("Detailed Summary Report");
			}else {
				LOGGER.error("Invalid report type");
				throw new Exception("invalid request");
			}

			entity = fileStatusDownloadReportRepo.save(entity);
			Long id = entity.getId();

			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", entity.getReportType());
			
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.DETAILED_SUPPLIER_IMS_REPORT, jobParams.toString(),
					userName, 1L, null, null);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = "Unexpected error occured in Consolidated Supplier"
					+ " Ims Download Async Report";
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", e.getMessage());
			LOGGER.error(errMsg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}


	private List<SupplierImsGstinDetailsDto> populateDefaultValues(
			List<String> gstnsList, List<String> taxPeriods) {

		List<SupplierImsGstinDetailsDto> list = new ArrayList<>();
		gstnsList.stream().forEach(o -> {
			SupplierImsGstinDetailsDto dto = new SupplierImsGstinDetailsDto();
			dto.setGstin(o);
			List<SupplierImsTaxPeriodDetailsDto> defaultStatus = new ArrayList<>();
			taxPeriods.stream().forEach(x -> defaultStatus
					.add(new SupplierImsTaxPeriodDetailsDto(x, "NOT_INITIATED")));
			dto.setTaxPeriodDetails(defaultStatus);

			list.add(dto);
		});

		return list;
	}

	private void populateDefaultStatus(
			List<SupplierImsTaxPeriodDetailsDto> taxPeriodDetails,
			List<String> taxPeriods) {

		List<String> totalTaxPeriods = new ArrayList<>(taxPeriods);

		List<String> availableTaxPeriods = taxPeriodDetails.stream()
				.map(o -> o.getTaxPeriod()).collect(Collectors.toList());

		totalTaxPeriods.removeAll(availableTaxPeriods);

		totalTaxPeriods.stream().forEach(o -> taxPeriodDetails
				.add(new SupplierImsTaxPeriodDetailsDto(o, "NOT_INITIATED")));

	}
	/*public static void main(String[] args) {
		
        List<String> reportTypes = Arrays.asList("N");

		
		if (reportTypes != null && !reportTypes.isEmpty()) {
			
			 if (reportTypes.containsAll(Arrays.asList("A", "R", "P", "N"))) {
		        System.out.println("A,R,P,N");
		    }
			
			 else if(reportTypes.containsAll(Arrays.asList("A"))){
			        System.out.println("A");

			    	
			    }else if (reportTypes.containsAll(Arrays.asList("R"))) {
			        System.out.println("R");

				} else if (reportTypes.containsAll(Arrays.asList("P"))) {
			        System.out.println("P");

				} else if (reportTypes.containsAll(Arrays.asList("N"))) {
			        System.out.println("N");

				}else if (reportTypes.containsAll(Arrays.asList("A", "R", "P", "N"))) {
			        System.out.println("A,R,P,N");
			    } else if (reportTypes.contains(Arrays.asList("Not Eligible Records"))) {
					entity.setReportType("Not Eligible Records");
				}else if (reportTypes.contains(Arrays.asList("Rejected - GSTR-3B Liability"))) {
					entity.setReportType("Rejected - GSTR-3B Liability");
				}
		}
	}*/
}
