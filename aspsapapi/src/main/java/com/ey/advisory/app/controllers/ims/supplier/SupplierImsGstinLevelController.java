package com.ey.advisory.app.controllers.ims.supplier;

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

import com.ey.advisory.app.service.ims.supplier.SupplierImsEntityReqDto;
import com.ey.advisory.app.service.ims.supplier.SupplierImsGstinLevelService;
import com.ey.advisory.app.service.ims.supplier.SupplierImsGstinSummaryResponseDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author ashutosh.kar
 *
 */

@RestController
public class SupplierImsGstinLevelController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierImsGstinLevelController.class);

	@Autowired
	@Qualifier("SupplierImsGstinLevelServiceImpl")
	private SupplierImsGstinLevelService supplierImsGstinLevelService;

	@RequestMapping(value = "ui/getSupplierImsSummaryGstinLvl", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getImsProcessedRecords(@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getSupplierImsSummaryGstinLvl Request received from UI as {}", jsonString);
			}
			SupplierImsEntityReqDto criteria = gson.fromJson(reqJson, SupplierImsEntityReqDto.class);
			// call service get data
			SupplierImsGstinSummaryResponseDto response = supplierImsGstinLevelService
					.getSupplierImsSummaryGstinLvlData(criteria);

			JsonObject resp = new JsonObject();
			JsonObject hdr = gson.toJsonTree(APIRespDto.createSuccessResp()).getAsJsonObject();
			hdr.addProperty("status", "S");
			resp.add("hdr", hdr);

			JsonObject dataObject = new JsonObject();
			dataObject.add("supplierImsData", gson.toJsonTree(response.getImsData()));
			dataObject.add("gstr1And1AData", gson.toJsonTree(response.getGstr1And1AData()));

			resp.add("resp", dataObject);

			LOGGER.info("Successfully processed Supplier IMS Gstin Level Summary request for GSTINs: {}",
					criteria.getGstin());

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the Supplier IMS Gstin Level Summary", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
