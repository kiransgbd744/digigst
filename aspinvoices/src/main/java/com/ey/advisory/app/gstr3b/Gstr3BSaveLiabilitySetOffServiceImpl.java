package com.ey.advisory.app.gstr3b;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialException;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BSaveChangesLiabilitySetOffRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveLiabilitySetOffStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityComputeDetailsRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityComputeDetailsEntity;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr3BSaveLiabilitySetOffServiceImpl")
public class Gstr3BSaveLiabilitySetOffServiceImpl
		implements Gstr3BSaveLiabilitySetOffService {

	/*public static void main(String[] args) {

		String gstnResponse = "{\"gstin\":\"23AAACE6641E1Z3\",\"ret_period\":\"092024\",\"sup_details\":{\"osup_det\":{\"txval\":444206781.67,\"iamt\":15611386,\"camt\":52771330.43,\"samt\":52771330.43,\"csamt\":0.0},\"osup_zero\":{\"txval\":0.0,\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},\"osup_nil_exmp\":{\"txval\":0.0,\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},\"isup_rev\":{\"txval\":3978629.44,\"iamt\":0.0,\"camt\":99465.98,\"samt\":99465.98,\"csamt\":0.0},\"osup_nongst\":{\"txval\":0.0,\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0}},\"inter_sup\":{\"unreg_details\":[{\"pos\":\"08\",\"txval\":0.0,\"iamt\":0.0}]},\"itc_elg\":{\"itc_avl\":[{\"ty\":\"IMPG\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"IMPS\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"ISRC\",\"iamt\":0.0,\"camt\":99465.98,\"samt\":99465.98,\"csamt\":0.0},{\"ty\":\"ISD\",\"iamt\":253721.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"OTH\",\"iamt\":115238941.07,\"camt\":1497729.14,\"samt\":1497729.14,\"csamt\":0.0}],\"itc_rev\":[{\"ty\":\"RUL\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"OTH\",\"iamt\":22509646.36,\"camt\":139137.75,\"samt\":139137.75,\"csamt\":0.0}],\"itc_net\":{\"iamt\":92983015.71,\"camt\":1458057.37,\"samt\":1458057.37,\"csamt\":0.0},\"itc_inelg\":[{\"ty\":\"RUL\",\"iamt\":14273487.84,\"camt\":217831.06,\"samt\":217831.06,\"csamt\":0.0},{\"ty\":\"OTH\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0}]},\"inward_sup\":{\"isup_details\":[{\"ty\":\"GST\",\"inter\":0.0,\"intra\":0.0},{\"ty\":\"NONGST\",\"inter\":0.0,\"intra\":0.0}]},\"intr_ltfee\":{\"intr_details\":{\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},\"ltfee_details\":{\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0}},\"tx_pmt\":{\"tx_py\":[{\"tran_desc\":\"Other than Reverse Charge\",\"trans_typ\":30002,\"igst\":{\"tx\":15611386,\"intr\":0.0,\"fee\":0.0},\"cgst\":{\"tx\":5.277133E+7,\"intr\":0.0,\"fee\":0.0},\"sgst\":{\"tx\":5.277133E+7,\"intr\":0.0,\"fee\":0.0},\"cess\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0}},{\"tran_desc\":\"Reverse charge and supplies made u/s 9(5)\",\"trans_typ\":30003,\"igst\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0},\"cgst\":{\"tx\":99466.0,\"intr\":0.0,\"fee\":0.0},\"sgst\":{\"tx\":99466.0,\"intr\":0.0,\"fee\":0.0},\"cess\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0}}],\"adjnegliab\":[{\"tran_desc\":\"Reverse charge and supplies made u/s 9(5)\",\"trans_typ\":30003,\"igst\":{\"tx\":100.0,\"intr\":0.0,\"fee\":0.0},\"cgst\":{\"tx\":200.0,\"intr\":0.0,\"fee\":0.0},\"sgst\":{\"tx\":300.0,\"intr\":0.0,\"fee\":0.0},\"cess\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0}},{\"tran_desc\":\"Other than Reverse Charge\",\"trans_typ\":30002,\"igst\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0},\"cgst\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0},\"sgst\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0},\"cess\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0}}],\"net_tax_pay\":[{\"tran_desc\":\"Reverse charge and supplies made u/s 9(5)\",\"trans_typ\":30003,\"igst\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0},\"cgst\":{\"tx\":99466.0,\"intr\":0.0,\"fee\":0.0},\"sgst\":{\"tx\":99466.0,\"intr\":0.0,\"fee\":0.0},\"cess\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0}},{\"tran_desc\":\"Other than Reverse Charge\",\"trans_typ\":30002,\"igst\":{\"tx\":15611386,\"intr\":0.0,\"fee\":0.0},\"cgst\":{\"tx\":5.277133E+7,\"intr\":0.0,\"fee\":0.0},\"sgst\":{\"tx\":5.277133E+7,\"intr\":0.0,\"fee\":0.0},\"cess\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0}}]},\"eco_dtls\":{\"eco_sup\":{\"txval\":0.0,\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},\"eco_reg_sup\":{\"txval\":0.0,\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0}}}";
		
	
		JsonObject suppDetails = JsonParser.parseString(gstnResponse)
				.getAsJsonObject();

		System.out.println(suppDetails);
		
		List<Gstr3BSetOffSavePdNlsDto> pdnlsList = new ArrayList<>();

		System.out.println(suppDetails);
		List<Gstr3BPaidNetTaxPayDto> netTaxPayList = new ArrayList<>();

		JsonObject txPmt = suppDetails.getAsJsonObject("tx_pmt");
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gstr3bLiabilitySetOffSaveToGstinDto jsonDto = new Gstr3bLiabilitySetOffSaveToGstinDto();

		if (txPmt != null) {
			JsonArray netTaxPayArray = txPmt.getAsJsonArray("net_tax_pay");
			if (netTaxPayArray != null) {
				Type netTaxPayListType = new TypeToken<List<Gstr3BPaidNetTaxPayDto>>() {
				}.getType();
				netTaxPayList = gson.fromJson(netTaxPayArray,
						netTaxPayListType);
				for (Gstr3BPaidNetTaxPayDto gstr3bPaidNetTaxPayDto : netTaxPayList) {
					if (gstr3bPaidNetTaxPayDto.getTransType() == 30003) {
						gstr3bPaidNetTaxPayDto.setTransDesc("Reverse charge");
					}
					if (gstr3bPaidNetTaxPayDto.getTransType() == 30002) {
						gstr3bPaidNetTaxPayDto
								.setTransDesc("Other than reverse charge");
					}
					gstr3bPaidNetTaxPayDto.setLiabLdgId(0);
				}
			}
			if (txPmt.has("adjnegliab")
					&& txPmt.getAsJsonArray("adjnegliab").size() > 0) {
				JsonArray adjnegliabArray = txPmt
						.getAsJsonArray("adjnegliab");
				LOGGER.debug("Found {} items in adjnegliab array",
						adjnegliabArray.size());

				Type adjnegliabTypeList = new TypeToken<List<Gstr3BPaidNetTaxPayDto>>() {
				}.getType();
				List<Gstr3BPaidNetTaxPayDto> adjnegliabList = gson
						.fromJson(adjnegliabArray, adjnegliabTypeList);

				adjnegliabList.forEach(adjnegliabDto -> {
					Gstr3BSetOffSavePdNlsDto pdnlsDto = new Gstr3BSetOffSavePdNlsDto();
					pdnlsDto.setTransType(
							Long.valueOf(adjnegliabDto.getTransType()));
					pdnlsDto.setLedgId(
							Long.valueOf(adjnegliabDto.getLiabLdgId()));

					pdnlsDto.setPdIgst(adjnegliabDto.getIgst() != null
							? adjnegliabDto.getIgst().getTx() : null);
					pdnlsDto.setPdCgst(adjnegliabDto.getCgst() != null
							? adjnegliabDto.getCgst().getTx() : null);
					pdnlsDto.setPdSgst(adjnegliabDto.getSgst() != null
							? adjnegliabDto.getSgst().getTx() : null);
					pdnlsDto.setPdCess(adjnegliabDto.getCess() != null
							? adjnegliabDto.getCess().getTx() : null);

					if ((pdnlsDto.getPdIgst() != null
							&& pdnlsDto.getPdIgst()
									.compareTo(BigDecimal.ZERO) != 0)
							|| (pdnlsDto.getPdCgst() != null
									&& pdnlsDto.getPdCgst().compareTo(
											BigDecimal.ZERO) != 0)
							|| (pdnlsDto.getPdSgst() != null
									&& pdnlsDto.getPdSgst().compareTo(
											BigDecimal.ZERO) != 0)
							|| (pdnlsDto.getPdCess() != null
									&& pdnlsDto.getPdCess().compareTo(
											BigDecimal.ZERO) != 0)) {

						pdnlsList.add(pdnlsDto);
					}
				});
			}
		}

		if (!netTaxPayList.isEmpty()) {
			jsonDto.setNetTaxPayItems(netTaxPayList);
		}
		if (!pdnlsList.isEmpty()) {
			jsonDto.setPdnls(pdnlsList);
		}

		System.out.println(jsonDto);
		String reqJson = gson.toJson(jsonDto);

		System.out.println("ReqJson : " + reqJson);
	}*/

	@Autowired
	@Qualifier("Gstr3BSaveChangesLiabilitySetOffRepository")
	private Gstr3BSaveChangesLiabilitySetOffRepository saveChangesRepo;

	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffStatusRepository")
	private Gstr3BSaveLiabilitySetOffStatusRepository saveStatusRepo;

	@Autowired
	@Qualifier("DefaultGSTNAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityComputeDetailsRepository")
	private Gstr3BSetOffEntityComputeDetailsRepository computeDetailsRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private final Long LEDGER_ID = 0L;
	private final Long ITC_TRANS_TYPE = 30002L;
	private final Long CASH_TRANS_TYPE = 30003L;
	private final BigDecimal zero = BigDecimal.ZERO;

	private static final List<String> sectionList = ImmutableList
			.of("ADDNL_CASH_REQ", "INTEREST_LATE_FEE", "NET_GST_LIABILITY");

	@Override
	public String saveLiablityToGstn(String gstin, String taxPeriod,
			String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("INSIDE Gstr3BSaveLiabilitySetOffServiceImpl"
					+ ".saveLiablityToGstn() method %s gstin, %s taxPeriod,"
					+ " %s groupCode {}", gstin, taxPeriod, groupCode);
		}

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		List<Gstr3BPaidNetTaxPayDto> netTaxPayList = new ArrayList<>();
		List<Gstr3BSetOffSavePdNlsDto> pdnlsList = new ArrayList<>();
		try {

			Gstr3bLiabilitySetOffSaveToGstinDto jsonDto = new Gstr3bLiabilitySetOffSaveToGstinDto();

			// making dynamic for pdcash by checking in config table
			Map<String, Config> configMap = configManager.getConfigs("GSTR3B",
					"gstr3b.offset.pdcashreq");

			boolean pdcashreq = configMap.get("gstr3b.offset.pdcashreq") == null
					? Boolean.TRUE
					: Boolean.valueOf(configMap.get("gstr3b.offset.pdcashreq")
							.getValue());

			if (pdcashreq) {
				List<Gstr3BPaidCashDto> pdCashList = getPdCashData(gstin,
						taxPeriod);

				if (pdCashList != null) {
					pdCashList.sort(Comparator
							.comparing(Gstr3BPaidCashDto::getTransType));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("PDCash getPdCashData Response {} : ",
								pdCashList);
					}

					jsonDto.setPdCash(pdCashList);
				}
			}
			Gstr3BSaveChangesLiabilitySetOffEntity dbResp = saveChangesRepo
					.findByGstinAndTaxPeriodAndIsActive(taxPeriod, gstin);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PDITC Response {} : ", dbResp);
			}

			Gstr3BPaidITCDto paidItcDto = getPDItcData(dbResp);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PDITC getPDItcData Response {} : ", paidItcDto);
			}

			// checkin for PDITC if its sum is zero need not to add pditc in
			// json

			BigDecimal pdItcSum = paidItcDto.getCPDCgst().add(paidItcDto
					.getCPDIgst()
					.add(paidItcDto.getCsPdCess().add(paidItcDto.getIPDCgst()
							.add(paidItcDto.getIPDIgst().add(paidItcDto
									.getIPDSgst().add(paidItcDto.getSPDIgst()
											.add(paidItcDto.getSPDSgst())))))));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PDITC pdItcSum {} : ", pdItcSum);
			}

			Boolean isPdItcEligible = pdItcSum.compareTo(BigDecimal.ZERO) == 1;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PDITC isPdItcEligible {} : ", isPdItcEligible);
			}

			if (isPdItcEligible) {
				jsonDto.setPdItcDto(paidItcDto);
			}

			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);

			APIResponse resp = getGSTR3BSummary(gstin, taxPeriod);
			if (resp != null && resp.isSuccess()) {
				String gstnResponse = resp.getResponse();

				JsonObject suppDetails = JsonParser.parseString(gstnResponse)
						.getAsJsonObject();

				JsonObject txPmt = suppDetails.getAsJsonObject("tx_pmt");

				if (txPmt != null) {
					JsonArray netTaxPayArray = txPmt
							.getAsJsonArray("net_tax_pay");
					if (netTaxPayArray != null) {
						Type netTaxPayListType = new TypeToken<List<Gstr3BPaidNetTaxPayDto>>() {
						}.getType();
						netTaxPayList = gson.fromJson(netTaxPayArray,
								netTaxPayListType);
						for (Gstr3BPaidNetTaxPayDto gstr3bPaidNetTaxPayDto : netTaxPayList) {
							if (gstr3bPaidNetTaxPayDto
									.getTransType() == 30003) {
								gstr3bPaidNetTaxPayDto
										.setTransDesc("Reverse charge");
							}
							if (gstr3bPaidNetTaxPayDto
									.getTransType() == 30002) {
								gstr3bPaidNetTaxPayDto.setTransDesc(
										"Other than reverse charge");
							}
							gstr3bPaidNetTaxPayDto.setLiabLdgId(0);
						}
					}
					if (txPmt.has("adjnegliab")
							&& txPmt.getAsJsonArray("adjnegliab").size() > 0) {
						JsonArray adjnegliabArray = txPmt
								.getAsJsonArray("adjnegliab");
						LOGGER.debug("Found {} items in adjnegliab array",
								adjnegliabArray.size());

						Type adjnegliabTypeList = new TypeToken<List<Gstr3BPaidNetTaxPayDto>>() {
						}.getType();
						List<Gstr3BPaidNetTaxPayDto> adjnegliabList = gson
								.fromJson(adjnegliabArray, adjnegliabTypeList);

						adjnegliabList.forEach(adjnegliabDto -> {
							Gstr3BSetOffSavePdNlsDto pdnlsDto = new Gstr3BSetOffSavePdNlsDto();
							pdnlsDto.setTransType(
									Long.valueOf(adjnegliabDto.getTransType()));
							pdnlsDto.setLedgId(
									Long.valueOf(adjnegliabDto.getLiabLdgId()));

							pdnlsDto.setPdIgst(adjnegliabDto.getIgst() != null
									? adjnegliabDto.getIgst().getTx() : null);
							pdnlsDto.setPdCgst(adjnegliabDto.getCgst() != null
									? adjnegliabDto.getCgst().getTx() : null);
							pdnlsDto.setPdSgst(adjnegliabDto.getSgst() != null
									? adjnegliabDto.getSgst().getTx() : null);
							pdnlsDto.setPdCess(adjnegliabDto.getCess() != null
									? adjnegliabDto.getCess().getTx() : null);

							if ((pdnlsDto.getPdIgst() != null
									&& pdnlsDto.getPdIgst()
											.compareTo(BigDecimal.ZERO) != 0)
									|| (pdnlsDto.getPdCgst() != null
											&& pdnlsDto.getPdCgst().compareTo(
													BigDecimal.ZERO) != 0)
									|| (pdnlsDto.getPdSgst() != null
											&& pdnlsDto.getPdSgst().compareTo(
													BigDecimal.ZERO) != 0)
									|| (pdnlsDto.getPdCess() != null
											&& pdnlsDto.getPdCess().compareTo(
													BigDecimal.ZERO) != 0)) {

								pdnlsList.add(pdnlsDto);
							}
						});
					}
				}

			}

			if (!netTaxPayList.isEmpty()) {
				jsonDto.setNetTaxPayItems(netTaxPayList);
			}

			if (!pdnlsList.isEmpty()) {
				jsonDto.setPdnls(pdnlsList);
			}

			String reqJson = gson.toJson(jsonDto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"GSTR3B Save Request Payload for GSTIN '%s', "
								+ "TaxPeriod '%s' is '%s'",
						gstin, taxPeriod, reqJson);
				LOGGER.debug(msg);
			}

			if (reqJson == null || reqJson.isEmpty()) {

				String msg = String.format(
						"No Data to Save " + "for GSTIN '%s', TaxPeriod '%s' ",
						gstin, taxPeriod);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR3B_SAVE_OFFSET, param1, param2);
			APIResponse apiResp = apiExecutor.execute(params, reqJson);

			Long id = createLiabilityOffSetSaveStatusEntry(taxPeriod, gstin,
					null, APIConstants.SAVE_INITIATED, null, groupCode, reqJson,
					apiResp.getResponse());

			if (apiResp.isSuccess()) {

				String status = apiResp.getResponse();
				LOGGER.debug("save status {} ", status);

				saveStatusRepo.updateStatus(APIConstants.SAVED, id);

				// 3B RevInt
				try {
					// postReverseIntegrationjob(groupCode, gstin, taxPeriod);
				} catch (Exception ex) {
					LOGGER.error("Exception while RevereseIntigrating 3B data",
							ex);
					throw new AppException(ex,
							"{} Exception while RevereseIntigrating 3B data");
				}

				return "Save Offset Liability Is Successful";
			} else {

				saveStatusRepo.updateStatus(APIConstants.SAVE_INITIATION_FAILED,
						id);

				return "Save Offset Liability Is Failed with :: "
						+ apiResp.getErrors().get(0).getErrorDesc();
			}

		} catch (Exception ex) {
			String msg = "Error while saving to gstn";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private Gstr3BPaidITCDto getPDItcData(
			Gstr3BSaveChangesLiabilitySetOffEntity dbResp) {

		Gstr3BPaidITCDto paidItcDto = new Gstr3BPaidITCDto();

		paidItcDto.setLedgId(LEDGER_ID);
		paidItcDto.setTransType(ITC_TRANS_TYPE);

		paidItcDto.setCPDCgst(
				dbResp.getCPDCgst() != null ? dbResp.getCPDCgst() : zero);
		paidItcDto.setCPDIgst(
				dbResp.getCPDIgst() != null ? dbResp.getCPDIgst() : zero);
		paidItcDto.setCsPdCess(
				dbResp.getCsPdCess() != null ? dbResp.getCsPdCess() : zero);
		paidItcDto.setIPDCgst(
				dbResp.getIPDCgst() != null ? dbResp.getIPDCgst() : zero);
		paidItcDto.setIPDIgst(
				dbResp.getIPDIgst() != null ? dbResp.getIPDIgst() : zero);
		paidItcDto.setIPDSgst(
				dbResp.getIPDSgst() != null ? dbResp.getIPDSgst() : zero);
		paidItcDto.setSPDIgst(
				dbResp.getSPDIgst() != null ? dbResp.getSPDIgst() : zero);
		paidItcDto.setSPDSgst(
				dbResp.getSPDSgst() != null ? dbResp.getSPDSgst() : zero);

		return paidItcDto;

	}

	private Long createLiabilityOffSetSaveStatusEntry(String taxPeriod,
			String gstin, String refId, String status, String filePath,
			String groupCode, String request, String response) {

		Gstr3BSaveLiabilitySetOffStatusEntity entity = new Gstr3BSaveLiabilitySetOffStatusEntity();
		TenantContext.setTenantId(groupCode);

		Clob reqClob;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(
					request.toCharArray());
		} catch (SerialException e) {
			String msg = "Error occured while serilizing the 3B request payload";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		} catch (SQLException e) {
			String msg = "SQL exception occured while converting 3B request "
					+ "payload to Clob.";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

		entity.setTaxPeriod(taxPeriod);
		entity.setGstin(gstin);
		entity.setStatus(status);
		entity.setRefId(refId);
		entity.setErrorCount(0);
		entity.setFilePath(filePath);
		entity.setCreatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setUpdatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setSaveRequestPayload(reqClob);
		entity.setSaveResponsePayload(response);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Before saveStatusRepo entity {} :", entity);
		}

		entity = saveStatusRepo.save(entity);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Success saveStatusRepo entity {} :", entity);
		}
		return entity.getId();
	}

	private List<Gstr3BPaidCashDto> getPdCashData(String gstin,
			String taxPeriod) {

		List<Gstr3BPaidCashDto> pdCshList = new ArrayList<>();

		try {
			List<Gstr3BSetOffEntityComputeDetailsEntity> dtoList = computeDetailsRepo
					.findByGstinAndTaxPeriodAndSectionNotInAndIsDeleteFalse(
							gstin, taxPeriod, sectionList);

			if (dtoList.isEmpty()) {
				String msg = String.format(
						"No Record found for PdCash :: gstin %s, TaxPerod %s ",
						gstin, taxPeriod);

				LOGGER.error(msg);
				throw new AppException(msg);
			}

			Pair<Boolean, Boolean> checkForCashEligibility = checkForCashEligibility(
					dtoList);

			if (!checkForCashEligibility.getValue0()
					&& !checkForCashEligibility.getValue1()) {

				String msg = String.format(
						"zero Amount found for PdCash :: gstin %s, TaxPerod %s ",
						gstin, taxPeriod);
				LOGGER.error(msg);
				return null;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PDCash DB Response {} : ", dtoList);
			}

			Gstr3BPaidCashDto obj1 = new Gstr3BPaidCashDto();
			obj1.setLedgId(LEDGER_ID);
			obj1.setTransType(ITC_TRANS_TYPE);

			for (Gstr3BSetOffEntityComputeDetailsEntity dto : dtoList) {

				if (checkForCashEligibility.getValue0()) {
					// col 7
					if (dto.getSection().equalsIgnoreCase("NET_LIABILITY")) {

						obj1.setPdCess(
								dto.getCess() != null ? dto.getCess() : zero);
						obj1.setPdCgst(
								dto.getCgst() != null ? dto.getCgst() : zero);
						obj1.setPdIgst(
								dto.getIgst() != null ? dto.getIgst() : zero);
						obj1.setPdSgst(
								dto.getSgst() != null ? dto.getSgst() : zero);
					}
					// col- 11
					if (dto.getSection().equalsIgnoreCase("INTEREST")) {

						obj1.setIntrCess(
								dto.getCess() != null ? dto.getCess() : zero);
						obj1.setIntrCgst(
								dto.getCgst() != null ? dto.getCgst() : zero);
						obj1.setIntrIgst(
								dto.getIgst() != null ? dto.getIgst() : zero);
						obj1.setIntrSgst(
								dto.getSgst() != null ? dto.getSgst() : zero);
					}
					// col-13
					if (dto.getSection().equalsIgnoreCase("LATE_FEE")) {

						obj1.setLateFeeCgst(
								dto.getCgst() != null ? dto.getCgst() : zero);
						obj1.setLateFeeSgst(
								dto.getSgst() != null ? dto.getSgst() : zero);
					}
				}
				// col- 9
				if (dto.getSection().equalsIgnoreCase("LIABILITY")) {
					if (checkForCashEligibility.getValue1()) {

						Gstr3BPaidCashDto obj2 = new Gstr3BPaidCashDto();

						obj2.setLedgId(LEDGER_ID);
						obj2.setTransType(CASH_TRANS_TYPE);
						obj2.setPdCess(
								dto.getCess() != null ? dto.getCess() : zero);
						obj2.setPdCgst(
								dto.getCgst() != null ? dto.getCgst() : zero);
						obj2.setPdIgst(
								dto.getIgst() != null ? dto.getIgst() : zero);
						obj2.setPdSgst(
								dto.getSgst() != null ? dto.getSgst() : zero);

						obj2.setIntrCess(zero);
						obj2.setIntrCgst(zero);
						obj2.setIntrIgst(zero);
						obj2.setIntrSgst(zero);

						obj2.setLateFeeCgst(zero);
						obj2.setLateFeeSgst(zero);

						pdCshList.add(obj2);

					}
				}
			}
			if (checkForCashEligibility.getValue0()) {
				pdCshList.add(obj1);

			}
		} catch (Exception e) {
			String msg = "Exception occured while Creating PdCash payload";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
		return pdCshList;
	}

	private Pair<Boolean, Boolean> checkForCashEligibility(
			List<Gstr3BSetOffEntityComputeDetailsEntity> dtoList) {

		BigDecimal obj1Sum = dtoList.stream()
				.filter(o -> o.getSection().equalsIgnoreCase("NET_LIABILITY")
						|| o.getSection().equalsIgnoreCase("INTEREST")
						|| o.getSection().equalsIgnoreCase("LATE_FEE"))
				.map(obj -> obj.getCess().add(obj.getCgst()).add(obj.getIgst())
						.add(obj.getSgst()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal obj2Sum = dtoList.stream()
				.filter(o -> o.getSection().equalsIgnoreCase("LIABILITY"))
				.map(obj -> obj.getCess().add(obj.getCgst()).add(obj.getIgst())
						.add(obj.getSgst()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Boolean isObj1Eligible = obj1Sum.compareTo(BigDecimal.ZERO) == 1;
		Boolean isObj2Eligible = obj2Sum.compareTo(BigDecimal.ZERO) == 1;

		return new Pair<>(isObj1Eligible, isObj2Eligible);

	}

	private APIResponse getGSTR3BSummary(String gstin, String taxPeriod) {
		APIResponse resp = null;
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			String groupCode = TenantContext.getTenantId();
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GET_GSTR3B_SUMMARY, param1, param2);
			resp = apiExecutor.execute(params, null);
			return resp;
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking '%s',"
							+ " GSTIN - '%s' and Taxperiod - '%s'",
					APIIdentifiers.GET_GSTR3B_SUMMARY, gstin, taxPeriod);
			LOGGER.error(msg, ex);
			return resp;
		}
	}
}
