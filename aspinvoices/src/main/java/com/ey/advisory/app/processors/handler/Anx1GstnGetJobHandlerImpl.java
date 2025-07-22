/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.anx1.Anx1InvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1GstnGetJobHandlerImpl")
@Slf4j
public class Anx1GstnGetJobHandlerImpl implements Anx1GstnGetJobHandler {

	@Autowired
	@Qualifier("anx1B2bInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getB2bService;

	@Autowired
	@Qualifier("anx1B2cInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getB2cService;

	@Autowired
	@Qualifier("anx1RevInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getRevService;

	@Autowired
	@Qualifier("anx1EcomInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getEcomservice;

	@Autowired
	@Qualifier("anx1ExpwopInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getExpwopService;

	@Autowired
	@Qualifier("Anx1ExpwpInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getExpwpService;
	
	@Autowired
	@Qualifier("Anx1MisInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getMisService;
	
	@Autowired
	@Qualifier("Anx1ImpgSezInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getImpgSezService;
	
	@Autowired
	@Qualifier("Anx1ImpgInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getImpgService;
	
	@Autowired
	@Qualifier("Anx1ImpsInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getImpsService;
	
	@Autowired
	@Qualifier("Anx1SezwopInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getSezwopService;
	
	@Autowired
	@Qualifier("Anx1SezwpInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getSezwpService;
	
	@Autowired
	@Qualifier("Anx1DeInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getDeService;
	
	
	@Override
	public void anx1GstnGetCall(String jsonReq, String groupCode) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
		Type listType = new TypeToken<List<Anx1GetInvoicesReqDto>>() {
		}.getType();
		List<Anx1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray, listType);

		dtos.forEach(dto -> {
			// Need to call return filling status
			// api(GstnReturnFilingStatus.java)
			// If it is submitted/filed then after submit/file, the very first
			// time
			// we need to call the GET API's and next time not required to call.

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Started.", APIConstants.B2C);
			}
			getB2cService.findInvFromGstn(dto, groupCode, APIConstants.B2C);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.B2C);
				LOGGER.debug("Anx1 {} Get Call is Started.", APIConstants.B2B);
			}
			getB2bService.findInvFromGstn(dto, groupCode, APIConstants.B2B);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.B2B);
				LOGGER.debug("Anx1 {} Get Call is Started.",
						APIConstants.EXPWP);
			}
			getExpwpService.findInvFromGstn(dto, groupCode, APIConstants.EXPWP);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.EXPWP);
				LOGGER.debug("Anx1 {} Get Call is Started.",
						APIConstants.EXPWOP);
			}
			getExpwopService.findInvFromGstn(dto, groupCode,
					APIConstants.EXPWOP);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.EXPWOP);
				LOGGER.debug("Anx1 {} Get Call is Started.",
						APIConstants.SEZWP);
			}
			getSezwpService.findInvFromGstn(dto, groupCode, APIConstants.SEZWP);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.SEZWP);
				LOGGER.debug("Anx1 {} Get Call is Started.",
						APIConstants.SEZWOP);
			}
			getSezwopService.findInvFromGstn(dto, groupCode,
					APIConstants.SEZWOP);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.SEZWOP);
				LOGGER.debug("Anx1 {} Get Call is Started.", APIConstants.DE);
			}
			getDeService.findInvFromGstn(dto, groupCode, APIConstants.DE);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.DE);
				LOGGER.debug("Anx1 {} Get Call is Started.", APIConstants.REV);
			}
			getRevService.findInvFromGstn(dto, groupCode, APIConstants.REV);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.REV);
				LOGGER.debug("Anx1 {} Get Call is Started.", APIConstants.IMPS);
			}
			getImpsService.findInvFromGstn(dto, groupCode, APIConstants.IMPS);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.IMPS);
				LOGGER.debug("Anx1 {} Get Call is Started.", APIConstants.IMPG);
			}
			getImpgService.findInvFromGstn(dto, groupCode, APIConstants.IMPG);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.IMPG);
				LOGGER.debug("Anx1 {} Get Call is Started.",
						APIConstants.IMPGSEZ);
			}
			getImpgSezService.findInvFromGstn(dto, groupCode,
					APIConstants.IMPGSEZ);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.",
						APIConstants.IMPGSEZ);
				LOGGER.debug("Anx1 {} Get Call is Started.", APIConstants.ECOM);
			}
			getEcomservice.findInvFromGstn(dto, groupCode, APIConstants.ECOM);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.ECOM);
				LOGGER.debug("Anx1 {} Get Call is Started.", APIConstants.MIS);
			}
			getMisService.findInvFromGstn(dto, groupCode, APIConstants.MIS);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1 {} Get Call is Ended.", APIConstants.MIS);
			}
		});
	}

}
