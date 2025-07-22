package com.ey.advisory.app.controllers.ims.supplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.Gstr1A.Gstr1AGetCallHandler;
import com.ey.advisory.app.service.ims.supplier.SupplierImsGetCallHandler;
import com.ey.advisory.app.service.ims.supplier.SupplierImsGetCallRestrictionService;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.GetCallDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.InitiateGetCallDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Vishal.verma
 *
 */
@Slf4j
@RestController
public class SupplierImsGetCallController {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	AsyncJobsService asyncJobsService;

	/*@Autowired
	Gstr1AGetCallHandler gstr1AGetCallHandler;
*/
	@Autowired
	SupplierImsGetCallHandler suppImsGetCallHandler;
	
	@Autowired
	@Qualifier("SupplierImsGetCallRestrictionServiceImpl")
	SupplierImsGetCallRestrictionService supplierImsRestrictionService;
	

	public static List<String> sectionList = new ArrayList<>(
			Arrays.asList("B2B", "B2BA", "CDNR", "CDNRA", "ECOM", "ECOMA"));

	public static List<String> returnTypeList = new ArrayList<>(
			Arrays.asList("GSTR1", "GSTR1A"));

	@PostMapping(value = "/ui/initiateSupplierImsGetCall", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCallApi(@RequestBody String jsonString) {

		JsonArray resp1 = new JsonArray();
		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EntityServiceImpl"
						+ ".getGSTINsForEntity ,Parsing Input request";
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

			GetCallDto dto = gson.fromJson(requestObject, GetCallDto.class);

			List<InitiateGetCallDto> gstinTaxPeiordList = dto
					.getGstinTaxPeiordList();

			List<InitiateGetCallDto> activeGstinTaxPeiordList = new ArrayList<>();
			List<String> inActiveGstinList = new ArrayList<>();

			Map<String, String> authTokenStatusForGroup = authTokenService
					.getAuthTokenStatusForGroup();

			JsonArray respBody = new JsonArray();
			gstinTaxPeiordList.forEach(o -> {
				JsonObject json = new JsonObject();
				json.addProperty("gstin", o.getGstin());
				String authStatus = authTokenStatusForGroup.get(o.getGstin());
				if ("A".equalsIgnoreCase(authStatus)) {

					activeGstinTaxPeiordList.add(o);

				} else {
					
					json.addProperty("gstin", o.getGstin());
					json.addProperty("msg",
							"Auth Token is Inactive, Please Activate");
					respBody.add(json);
					 
				}
			});
			
			if(activeGstinTaxPeiordList.isEmpty()) {
				APIRespDto apiResp = null;
				if (apiResp == null) {
					apiResp = APIRespDto.createSuccessResp();
				}
				resp.add("hdr", gson.toJsonTree(apiResp));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

			}

			List<List<Pair<String, String>>> gstinReturnPeriodList = activeGstinTaxPeiordList
					.stream().map(o -> createGstinPair(o, dto.getFy()))
					.collect(Collectors.toList());

			List<Pair<String, String>> list = gstinReturnPeriodList.stream()
					.collect(ArrayList::new, List::addAll, List::addAll);

			List<AsyncExecJob> jobList = new ArrayList<>();

			for (int i = 0; i < list.size(); i++) {

				String taxPeriod = list.get(i).getValue1();

				boolean isNoFutureTaxPeriod = false;

				isNoFutureTaxPeriod = GenUtil
						.isValidTaxPeriodForCurrentFy(taxPeriod);

				if (!isNoFutureTaxPeriod) {
					LOGGER.error(
							"Requested TaxPeriod {} is Future TaxPeriod, Hence skipping",
							taxPeriod);
					continue;
				}

				String gstin = list.get(i).getValue0();
				//List<Gstr1GetInvoicesReqDto> dtos = new ArrayList<>();
				Gstr1GetInvoicesReqDto reqDto = new Gstr1GetInvoicesReqDto();

				reqDto.setGstin(gstin);
				reqDto.setReturnPeriod(taxPeriod);

				// TODO - check for next eligible date basis onboarding,
				// if it not today then check for Failed case,
				// Get call has failed then, only for failed one will can
				// initiate

				reqDto.setIsFailed(false);
				reqDto.setGstr1Sections(sectionList);
				reqDto.setImsReturnTypeList(returnTypeList);
				
				
				List<Gstr1GetInvoicesReqDto> restrictGetcallsFilteredDtos =
						supplierImsRestrictionService.processGetCall(reqDto);
				
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.SUPPLIER_IMS);

				 resp1 = suppImsGetCallHandler
						.createImsJobs(restrictGetcallsFilteredDtos, jobList, respBody);
				
			}
			
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		//resp.add("hdr", gson.toJsonTree(APIRespDto.SUCCESS_STATUS));
		resp.add("resp", gson.toJsonTree(resp1));
		return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

			
		} catch (Exception ex) {
			String msg = "Unexpected error while making Supplier IMS Get call ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	private List<Pair<String, String>> createGstinPair(InitiateGetCallDto o,
			String fy) {

		String[] year = fy.split("-");
		String stYear = year[0];
		String endYear = "20" + year[1];

		return o.getTaxPeriodList().stream().map(x -> {
			String yearSuffix = (Integer.parseInt(x) >= 4
					&& Integer.parseInt(x) <= 12) ? stYear : endYear;
			return new Pair<>(o.getGstin(), x + yearSuffix);
		}).collect(Collectors.toList());
	}

}
