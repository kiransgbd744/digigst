package com.ey.advisory.app.gstr3b;

import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BGetNegativeLedgerDetailService")
public class Gstr3BGetNegativeLedgerDetailService {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService gstnAuthService;

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository gstnUserRequestRepo;

	public String getNegativeLedgerdetails(String gstin, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Before Invoking Gstr3BGetNegativeLedgerDetailService"
							+ ".getNegativeLedgerdetails() :: %s, %s ",
					gstin, taxPeriod);
			LOGGER.debug(msg);
		}

		try {

			String authStatus = gstnAuthService
					.getAuthTokenStatusForGstin(gstin);
			if (authStatus.equals("I")) {
				String msg = String.format("Gstin Auth Token is InActive : %s",
						gstin);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			// Invoke the GSTN API - Get Return Status API and get the JSON.
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("from_date", getDate());
			APIParam param3 = new APIParam("to_date", getDate());
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR3B_NEGATIVE_LIABILITY,
					param1, param2, param3);

			APIResponse resp = apiExecutor.execute(params, null);
			if (!resp.isSuccess()) {
				String msg = String.format(
						"Failed to do Get  Gstr3BGetNegativeLedger Details from Gstn,"
								+ " Error Reponse is '%s'",
						resp.getError());
				LOGGER.error(msg);
				throw new AppException(resp.getError().toString());
			}
			// saving json to table
			saveOrUpdateGstnUserRequest(gstin, taxPeriod, resp.getResponse());
			
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while invoking Gstr3BGetNegativeLedgerDetailService",
					ex);
			//throw new AppException(ex);
		}
		return "Gstr3BGetNegativeLedgerDetailService Success";
		
	}

	private void saveOrUpdateGstnUserRequest(String gstin, String taxPeriod,
			String getGstnData) {

		GstnUserRequestEntity gstnUserRequestEntity = gstnUserRequestRepo
				.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
						"NegativeLedger");

		Clob reqClob = null;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(
					getGstnData.toCharArray());

			if (gstnUserRequestEntity == null) {

				gstnUserRequestEntity = new GstnUserRequestEntity();

				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser()
								.getUserPrincipalName() != null)
										? SecurityContext.getUser()
												.getUserPrincipalName()
										: "SYSTEM";

				if (userName == null || userName.isEmpty()) {
					userName = "SYSTEM";
				}

				gstnUserRequestEntity.setGstin(gstin);
				gstnUserRequestEntity.setTaxPeriod(taxPeriod);
				gstnUserRequestEntity.setReturnType("NegativeLedger");
				gstnUserRequestEntity.setRequestType("GET");
				gstnUserRequestEntity.setRequestStatus(1);
				gstnUserRequestEntity.setCreatedBy(userName);
				gstnUserRequestEntity.setCreatedOn(LocalDateTime.now());
				gstnUserRequestEntity.setDelete(false);
				gstnUserRequestEntity.setGetResponsePayload(reqClob);
				gstnUserRequestEntity.setDerivedRetPeriod(
						Integer.valueOf(taxPeriod.substring(2)
								.concat(taxPeriod.substring(0, 2))));
				gstnUserRequestRepo.save(gstnUserRequestEntity);

			} else {
				gstnUserRequestRepo.updateGstnResponse(reqClob, 1, gstin,
						taxPeriod, "NegativeLedger", LocalDateTime.now());

			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting "
					+ "gstnUserRequest NegativeLedger data";
			LOGGER.error(msg, e);

			//throw new AppException(msg, e);
		}
	}
	
	private String getDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = today.format(formatter);
        return formattedDate;
    }

}
