package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.sql.Clob;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository;
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
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BGetLedgerDetailImpl")
public class Gstr3BGetLedgerDetailImpl implements Gstr3BGetLedgerDetails {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService gstnAuthService;

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository")
	private Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository ledgerItcRepo;

	@Override
	public LedgerRespDto getLedgerdetails(String gstin, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Before Invoking Gstr3BGetLedgerDetailImpl"
							+ ".getLedgerdetails() :: %s, %s ",
					gstin, taxPeriod);
			LOGGER.debug(msg);
		}

		LedgerRespDto ledgerBalanceEntity = new LedgerRespDto();

		try {

			String authStatus = gstnAuthService
					.getAuthTokenStatusForGstin(gstin);
			if (authStatus.equals("I")) {
				String msg = String.format("Gstin Auth Token is InActive : %s",
						gstin);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.LEDGER_GET_BAL, param1,
					param2);
			APIResponse cashCreditResp = apiExecutor.execute(params, null);
			if (!cashCreditResp.isSuccess()) {
				String msg = String.format(
						"Failed to get Ledger Cash/Credit Balance"
								+ " for Gstn '%s' and Taxperiod '%s'. "
								+ "The Response" + " for Cash/Credit is '%s'",
						gstin, taxPeriod, cashCreditResp.getError());
				LOGGER.error(msg);

			} else {
				String apiRespCash = cashCreditResp.getResponse();
				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Invoking Gstr3BGetLedgerDetailImpl"
									+ "LEDGER_GET_BAL :: %s ", apiRespCash);
					LOGGER.debug(msg);
				}

				saveOrUpdateGstnUserRequest(gstin, taxPeriod, apiRespCash);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Invoked Gstr3BGetLedgerDetailImpl"
									+ ".saveOrUpdateGstnUserRequest :: %s, %s, %s ",
							gstin, taxPeriod, apiRespCash);
					LOGGER.debug(msg);
				}

				ledgerBalanceEntity = parseLedgerBalanceData(gstin, taxPeriod,
						apiRespCash, ledgerBalanceEntity);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Invoked Gstr3BGetLedgerDetailImpl"
									+ ".parseLedgerBalanceData :: %s, %s, %s ",
							gstin, taxPeriod, ledgerBalanceEntity);
					LOGGER.debug(msg);
				}

				if (ledgerBalanceEntity != null) {

					ledgerItcRepo.updateIsActive(taxPeriod, gstin);

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Invoked Gstr3BGetLedgerDetailImpl"
										+ ".updateIsActive :: %s, %s ",
								gstin, taxPeriod);
						LOGGER.debug(msg);
					}

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Before Gstr3BGetLedgerDetailImpl"
										+ ".persistLedgerData Invoke :: %s, %s ",
								gstin, taxPeriod);
						LOGGER.debug(msg);
					}
					persistLedgerData(ledgerBalanceEntity, gstin, taxPeriod);

				}

			}
		} catch (Exception ex) {

			String msg = String
					.format(" Error while executing GSTN API: " + gstin);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

		}
		return ledgerBalanceEntity;
	}

	public LedgerRespDto parseLedgerBalanceData(String gstin, String taxPeriod,
			String cashcrResp, LedgerRespDto entity) {

		BigDecimal zero = BigDecimal.ZERO;
		JsonObject responseObject = null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parsing Data For LedgerBalanceSummary Api Response ");
		}

		try {
			if (cashcrResp != null) {
				responseObject = (new JsonParser().parse(cashcrResp))
						.getAsJsonObject();
				JsonObject cashBal = responseObject.has("cash_bal")
						? responseObject.get("cash_bal").getAsJsonObject()
						: null;
				if (cashBal != null) {

					JsonObject cgst = cashBal.has("cgst")
							? cashBal.get("cgst").getAsJsonObject()
							: null;

					if (cgst != null) {
						entity.setCgstIntr(cgst.has("intr")
								? cgst.get("intr").getAsBigDecimal()
								: zero);
						entity.setCgstLateFee(cgst.has("fee")
								? cgst.get("fee").getAsBigDecimal()
								: zero);
						entity.setCgstTx(cgst.has("tx")
								? cgst.get("tx").getAsBigDecimal()
								: zero);
					}

					JsonObject igst = cashBal.has("igst")
							? cashBal.get("igst").getAsJsonObject()
							: null;
					if (igst != null) {
						entity.setIgstIntr(igst.has("intr")
								? igst.get("intr").getAsBigDecimal()
								: zero);
						entity.setIgstLateFee(igst.has("fee")
								? igst.get("fee").getAsBigDecimal()
								: zero);
						entity.setIgstTx(igst.has("tx")
								? igst.get("tx").getAsBigDecimal()
								: zero);
					}

					JsonObject sgst = cashBal.has("sgst")
							? cashBal.get("sgst").getAsJsonObject()
							: null;
					if (sgst != null) {
						entity.setSgstIntr(sgst.has("intr")
								? sgst.get("intr").getAsBigDecimal()
								: zero);
						entity.setSgstLateFee(sgst.has("fee")
								? sgst.get("fee").getAsBigDecimal()
								: zero);
						entity.setSgstTx(sgst.has("tx")
								? sgst.get("tx").getAsBigDecimal()
								: zero);
					}

					JsonObject cess = cashBal.has("cess")
							? cashBal.get("cess").getAsJsonObject()
							: null;
					if (cess != null) {
						entity.setCsgstIntr(cess.has("intr")
								? cess.get("intr").getAsBigDecimal()
								: zero);
						entity.setCsgstLateFee(cess.has("fee")
								? cess.get("fee").getAsBigDecimal()
								: zero);
						entity.setCsgstTx(cess.has("tx")
								? cess.get("tx").getAsBigDecimal()
								: zero);
					}

					JsonObject itcBal = responseObject.has("itc_bal")
							? responseObject.get("itc_bal").getAsJsonObject()
							: null;
					if (itcBal != null) {
						entity.setCrIgst(itcBal.has("igst_bal")
								? itcBal.get("igst_bal").getAsBigDecimal()
								: zero);
						entity.setCrCgst(itcBal.has("cgst_bal")
								? itcBal.get("cgst_bal").getAsBigDecimal()
								: zero);
						entity.setCrSgst(itcBal.has("sgst_bal")
								? itcBal.get("sgst_bal").getAsBigDecimal()
								: zero);
						entity.setCrCess(itcBal.has("cess_bal")
								? itcBal.get("cess_bal").getAsBigDecimal()
								: zero);
					}

				}
			}

		} catch (Exception ex) {
			String msg = String
					.format("Exception while populating the GSTN reponse to "
							+ " LedgerRespDto Dto", ex);
			LOGGER.error(msg, ex);
			throw new AppException(msg);

		}
		return entity;
	}

	private void saveOrUpdateGstnUserRequest(String gstin, String taxPeriod,
			String getGstnData) {

		GstnUserRequestEntity gstnUserRequestEntity = gstnUserRequestRepo
				.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
						"LEDGER");

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
				gstnUserRequestEntity.setReturnType("LEDGER");
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
						taxPeriod, "LEDGER", LocalDateTime.now());

			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting "
					+ "gstnUserRequest ledger data";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}
	}

	public void persistLedgerData(LedgerRespDto dto, String gstin,
			String taxPeriod) {
		LocalDateTime now = LocalDateTime.now();
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity entity = new Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside Gstr3BGetLedgerDetailImpl"
							+ ".persistLedgerData :: %s, %s ",
					gstin, taxPeriod);
			LOGGER.debug(msg);
		}
		entity.setCreatedBy(userName);
		entity.setFeeCess(dto.getCsgstLateFee());
		entity.setFeeCgst(dto.getCgstLateFee());
		entity.setFeeIgst(dto.getIgstLateFee());
		entity.setFeeSgst(dto.getSgstLateFee());
		entity.setGstin(gstin);
		entity.setInterestCess(dto.getCsgstIntr());
		entity.setInterestCgst(dto.getCgstIntr());
		entity.setInterestIgst(dto.getIgstIntr());
		entity.setInterestSgst(dto.getSgstIntr());
		entity.setIsActive(true);
		entity.setItcBalanceCess(dto.getCrCess());
		entity.setItcBalanceCgst(dto.getCrCgst());
		entity.setItcBalanceSgst(dto.getCrSgst());
		entity.setItcBalanceIgst(dto.getCrIgst());
		/*
		 * entity.setItcBlckCess(itcBlckCess);
		 * entity.setItcBlckCgst(itcBlckCgst);
		 * entity.setItcBlckIgst(itcBlckIgst);
		 * entity.setItcBlckSgst(itcBlckSgst);
		 */
		entity.setTaxCess(dto.getCsgstTx());
		entity.setTaxCgst(dto.getCgstTx());
		entity.setTaxIgst(dto.getIgstTx());
		entity.setTaxPeriod(taxPeriod);
		entity.setTaxSgst(dto.getSgstTx());
		entity.setUpdatedBy(userName);
		entity.setUpdatedOn(now);
		entity.setCreatedOn(now);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Before Saving entity"
							+ ".persistLedgerData :: %s, %s, %s ",
					gstin, taxPeriod, entity);
			LOGGER.debug(msg);
		}

		ledgerItcRepo.save(entity);

	}
}
