package com.ey.advisory.controller.gstr2b;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.asprecon.gstr2.ap.recon.additional.reports.Gstr2APAndNonAPLockedCFSNAmendedRecords;
import com.ey.advisory.asprecon.gstr2.ap.recon.additional.reports.GstrAPAndNonAP2ReconITCTrackingReport;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Test2BPRReports {
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	
	/*@Autowired
	@Qualifier("ImpgNonAPReconReport")
	ImpgNonAPReconReport impgReport;*/
	
	@Autowired
	@Qualifier("Gstr2APAndNonAPLockedCFSNAmendedRecords")
	Gstr2APAndNonAPLockedCFSNAmendedRecords lockedCFSNFlag;
	
	@Autowired
	@Qualifier("GstrAPAndNonAP2ReconITCTrackingReport")
	GstrAPAndNonAP2ReconITCTrackingReport itcTrackingReport;

	@PostMapping(value = "/ui/testReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr2BDetailsResponse(
			@RequestBody String jsonString) {

		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE Gstr2BDetailsController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			Long configId = requestObject.has("configId")
					? requestObject.get("configId").getAsLong() : null;
					
					

					
			/*fmRecords.getForceMatchRecords(configId);

			cdrnA2Records.getCdnrInvRefRegGstin2ARecords(configId);

			cdrnPRRecords.getCdnrInvRefRegGstinPRRecords(configId);

			recpGstinPeriod.getRecipientGSTINPeriodWiseRecords(configId);

			recpGstinWiseRecords.getRecipientGSTINWiseRecords(configId);

			summCalPeriod.getSummaryCalendarPeriodRecords(configId);

			summTaxPeriod.getSummaryTaxPeriodRecords(configId);

			suppGstinSummry.getSupplierGSTINSummaryRecords(configId);

			suppPanSummry.getSupplierPANSummaryRecords(configId);

			vendorGstinTaxperiod.getVendorGSTINPeriodWiseRecords(configId);

			vendorGstinWise.getVendorGSTINWiseRecords(configId);

			vendorPanPeriod.getVendorPANPeriodWiseRecords(configId);

			vendorPanWise.getVendorPANWiseRecords(configId);

			panMaster.getMasterVendorPANWiseRecords(configId);

			gstinMaster.getMasterVendorGSTINWiseRecords(configId);

			reverseChargeRegister.getGsr2ReverseChargeRegisterReports(configId);*/
					
			List<String> findPan = findPan(configId);
			
			LOGGER.error("pan {}", findPan);
					
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree("Success");
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	private List<String> findPan(Long configId) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_AUTO_2APR_GET_VENDOR_PAN");

		storedProc.registerStoredProcedureParameter("P_RECON_REPORT_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executing  proc to get List of PAN"
							+ " USP_AUTO_2APR_GET_VENDOR_PAN: '%s'",
					configId.toString());
			LOGGER.debug(msg);
		}

		@SuppressWarnings("unchecked")	
		List<String> list = storedProc.getResultList();
		
		/*List<String> list = records.stream().map(o -> convertToString(o))
				.collect(Collectors.toList());
*/
		return list;
	}

	/*private String convertToString(Object[] o) {
		
		
		return (String)o[0];
	}
*/
}